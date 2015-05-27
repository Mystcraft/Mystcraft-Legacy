package com.xcompwiz.mystcraft.linking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAlter;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class LinkController {

	public static boolean travelEntity(World world, Entity entity, ILinkInfo info) {
		if (world.isRemote) return false;
		if (info == null) return false;
		info = info.clone();
		Integer dimension = info.getDimensionUID();
		if (dimension == null) return false;
		ChunkCoordinates spawn = info.getSpawn();
		float yaw = info.getSpawnYaw();
		if (!LinkListenerManager.isLinkPermitted(world, entity, info)) return false;
		MinecraftServer mcServer = Mystcraft.sidedProxy.getMCServer();
		if (mcServer == null || (dimension != 0 && !mcServer.getAllowNether())) return false;
		WorldServer newworld = mcServer.worldServerForDimension(dimension);
		if (newworld == null) {
			System.err.println("Cannot Link Entity to Dimension: Could not get World for Dimension " + dimension);
			return false;
		}
		if (spawn == null) {
			spawn = newworld.getSpawnPoint(); // Get destination coords if not set
			info.setSpawn(spawn);
		}
		LinkEventAlter event = new LinkEventAlter(world, newworld, entity, info.clone());
		MinecraftForge.EVENT_BUS.post(event);
		if (event.spawn != null) spawn = event.spawn;
		if (event.rotationYaw != null) yaw = event.rotationYaw;

		// Move entity
		teleportEntity(newworld, entity, dimension, spawn, yaw, info);
		return true;
	}

	private static Entity teleportEntity(World newworld, Entity entity, int dimension, ChunkCoordinates spawn, float yaw, ILinkInfo info) {
		World origin = entity.worldObj;
		if (!LinkListenerManager.isLinkPermitted(origin, entity, info)) { return null; }
		Entity mount = entity.ridingEntity;
		if (entity.ridingEntity != null) {
			entity.mountEntity(null);
			mount = teleportEntity(newworld, mount, dimension, spawn, yaw, info);
		}
		boolean changingworlds = origin != newworld;
		LinkListenerManager.onLinkStart(origin, entity, info);
		origin.updateEntityWithOptionalForce(entity, false);
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.closeScreen();
			if (changingworlds) {
				player.dimension = dimension;
				player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, newworld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
				((WorldServer) origin).getPlayerManager().removePlayer(player);
			}
		}
		if (changingworlds) {
			LinkController.removeEntityFromWorld(origin, entity);
		}
		LinkListenerManager.onExitWorld(entity, info);
		// Move entity
		entity.setLocationAndAngles(spawn.posX + 0.5, spawn.posY, spawn.posZ + 0.5, yaw, entity.rotationPitch);
		((WorldServer) newworld).theChunkProviderServer.loadChunk(spawn.posX >> 4, spawn.posZ >> 4);
		while (LinkController.getCollidingWorldGeometry(newworld, entity.boundingBox, entity).size() != 0) {
			++spawn.posY;
			entity.setPosition(spawn.posX + 0.5, spawn.posY, spawn.posZ + 0.5);
		}
		if (changingworlds) {
			if (!(entity instanceof EntityPlayer)) {
				NBTTagCompound entityNBT = new NBTTagCompound();
				entity.isDead = false;
				entityNBT.setString("id", EntityList.getEntityString(entity));
				entity.writeToNBT(entityNBT);
				entity.isDead = true;
				entity = EntityList.createEntityFromNBT(entityNBT, newworld);
				if (entity == null) {
					LoggerUtils.warn("Failed to reconstruct entity when linking");
					return null;
				}
				entity.dimension = newworld.provider.dimensionId;
			}
			newworld.spawnEntityInWorld(entity);
			entity.setWorld(newworld);
		}
		entity.setLocationAndAngles(spawn.posX + 0.5, spawn.posY, spawn.posZ + 0.5, yaw, entity.rotationPitch);
		LinkListenerManager.onEnterWorld(origin, newworld, entity, info);
		newworld.updateEntityWithOptionalForce(entity, false);
		entity.setLocationAndAngles(spawn.posX + 0.5, spawn.posY, spawn.posZ + 0.5, yaw, entity.rotationPitch);
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			if (changingworlds) player.mcServer.getConfigurationManager().func_72375_a(player, (WorldServer) newworld);
			player.playerNetServerHandler.setPlayerLocation(spawn.posX + 0.5, spawn.posY, spawn.posZ + 0.5, player.rotationYaw, player.rotationPitch);
		}
		newworld.updateEntityWithOptionalForce(entity, false);
		if (entity instanceof EntityPlayerMP && changingworlds) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.theItemInWorldManager.setWorld((WorldServer) newworld);
			player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer) newworld);
			player.mcServer.getConfigurationManager().syncPlayerInventory(player);
			Iterator<PotionEffect> iter = player.getActivePotionEffects().iterator();

			while (iter.hasNext()) {
				PotionEffect effect = iter.next();
				player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), effect));
			}
			player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
		}
		entity.setLocationAndAngles(spawn.posX + 0.5, spawn.posY, spawn.posZ + 0.5, yaw, entity.rotationPitch);
		LinkListenerManager.onLinkEnd(origin, newworld, entity, info);
		if (mount != null) {
			if (entity instanceof EntityPlayerMP) {
				newworld.updateEntityWithOptionalForce(entity, true);
			}
			entity.mountEntity(mount);
		}
		return entity;
	}

	private static void removeEntityFromWorld(World world, Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			player.closeScreen();
			world.playerEntities.remove(player);
			world.updateAllPlayersSleepingFlag();
			int i = entity.chunkCoordX;
			int j = entity.chunkCoordZ;
			if (entity.addedToChunk && world.getChunkProvider().chunkExists(i, j)) {
				world.getChunkFromChunkCoords(i, j).removeEntity(entity);
				world.getChunkFromChunkCoords(i, j).isModified = true;
			}
			world.loadedEntityList.remove(entity);
			world.onEntityRemoved(entity);
		}
	}

	private static List<AxisAlignedBB> getCollidingWorldGeometry(World world, AxisAlignedBB axisalignedbb, Entity entity) {
		ArrayList<AxisAlignedBB> collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
		for (int k1 = i; k1 < j; k1++) {
			for (int l1 = i1; l1 < j1; l1++) {
				if (!world.blockExists(k1, 128 / 2, l1)) {
					continue;
				}
				for (int i2 = k - 1; i2 < l; i2++) {
					Block block = world.getBlock(k1, i2, l1);
					if (block != null) {
						block.addCollisionBoxesToList(world, k1, i2, l1, axisalignedbb, collidingBoundingBoxes, entity);
					}
				}
			}
		}
		return collidingBoundingBoxes;
	}

}
