package com.xcompwiz.mystcraft.linking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAlter;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

public class LinkController {

	public static boolean travelEntity(World world, Entity entity, ILinkInfo info) {
		if (world.isRemote)
			return false;
		if (info == null)
			return false;
		info = info.clone();
		Integer dimension = info.getDimensionUID();
		if (dimension == null)
			return false;
		BlockPos spawn = info.getSpawn();
		float yaw = info.getSpawnYaw();
		if (!LinkListenerManager.isLinkPermitted(world, entity, info))
			return false;
		MinecraftServer mcServer = Mystcraft.sidedProxy.getMCServer();
		if (mcServer == null || (dimension != 0 && !mcServer.getAllowNether()))
			return false;
		WorldServer newworld = mcServer.getWorld(dimension);
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
		if (event.spawn != null) {
			spawn = event.spawn;
			info.setSpawn(spawn);
		}
		if (event.rotationYaw != null) {
			yaw = event.rotationYaw;
			info.setSpawnYaw(yaw);
		}

		// Move entity
		teleportEntity(newworld, entity, dimension, spawn, yaw, info);
		return true;
	}

	private static Entity teleportEntity(World newworld, Entity entity, int dimension, BlockPos spawn, float yaw, ILinkInfo info) {
		World origin = entity.getEntityWorld();
		if (!LinkListenerManager.isLinkPermitted(origin, entity, info)) {
			return null;
		}
		if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, dimension)) {
			return null;
		}
		Entity mount = entity.getRidingEntity();
		if (mount != null) {
			entity.dismountRidingEntity();
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
				player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), newworld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
				((WorldServer) origin).getPlayerChunkMap().removePlayer(player);
			}
		}
		if (changingworlds) {
			LinkController.removeEntityFromWorld(origin, entity);
		}
		LinkListenerManager.onExitWorld(entity, info);
		// Move entity
		entity.setLocationAndAngles(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, yaw, entity.rotationPitch);
		((WorldServer) newworld).getChunkProvider().loadChunk(spawn.getX() >> 4, spawn.getZ() >> 4);

		while (LinkController.getCollidingWorldGeometry(newworld, entity.getEntityBoundingBox(), entity).size() != 0) {
			spawn = spawn.up();
			entity.setPosition(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5);
		}

		if (changingworlds) {
			if (!(entity instanceof EntityPlayer)) {
				origin.removeEntity(entity);
				entity.isDead = false;
				NBTTagCompound nbttagcompound = entity.writeToNBT(new NBTTagCompound());
				nbttagcompound.removeTag("Dimension");

				Entity newEntity = EntityList.createEntityFromNBT(nbttagcompound, newworld);
				if (newEntity == null) {
					LoggerUtils.warn("Failed to reconstruct entity when linking");
					return null;
				}
				newEntity.readFromNBT(nbttagcompound);
				newEntity.timeUntilPortal = entity.timeUntilPortal;
				entity.isDead = true;
				newEntity.dimension = newworld.provider.getDimension();

				boolean flag = newEntity.forceSpawn;
				newEntity.forceSpawn = true;
				newworld.spawnEntity(newEntity);
				newEntity.forceSpawn = flag;
				entity = newEntity;
			} else {
				entity.setPositionAndUpdate(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5);
				newworld.spawnEntity(entity);
			}
			entity.setWorld(newworld);
		}
		entity.setLocationAndAngles(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, yaw, entity.rotationPitch);
		LinkListenerManager.onEnterWorld(origin, newworld, entity, info);
		newworld.updateEntityWithOptionalForce(entity, false);
		entity.setLocationAndAngles(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, yaw, entity.rotationPitch);

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			if (changingworlds) {
				//Removing being done way above
				player.getServerWorld().getPlayerChunkMap().addPlayer(player);
				player.getServerWorld().getChunkProvider().provideChunk((int) player.posX >> 4, (int) player.posZ >> 4);
			}
			player.connection.setPlayerLocation(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, player.rotationYaw, player.rotationPitch);
		}

		newworld.updateEntityWithOptionalForce(entity, false);
		if (entity instanceof EntityPlayerMP && changingworlds) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.interactionManager.setWorld((WorldServer) newworld);
			player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, (WorldServer) newworld);
			player.mcServer.getPlayerList().syncPlayerInventory(player);
			Iterator<PotionEffect> iter = player.getActivePotionEffects().iterator();

			while (iter.hasNext()) {
				PotionEffect effect = iter.next();
				player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), effect));
			}
			player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
		}
		entity.setLocationAndAngles(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, yaw, entity.rotationPitch);
		LinkListenerManager.onLinkEnd(origin, newworld, entity, info);
		if (mount != null) {
			if (entity instanceof EntityPlayerMP) {
				newworld.updateEntityWithOptionalForce(entity, true);
			}
			entity.startRiding(mount);
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
			if (entity.addedToChunk && world.getChunkProvider().isChunkGeneratedAt(i, j)) {
				world.getChunkFromChunkCoords(i, j).removeEntity(entity);
				world.getChunkFromChunkCoords(i, j).setModified(true);
			}
			world.loadedEntityList.remove(entity);
			world.onEntityRemoved(entity);
		}
	}

	private static List<AxisAlignedBB> getCollidingWorldGeometry(World world, AxisAlignedBB axisalignedbb, Entity entity) {
		ArrayList<AxisAlignedBB> collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
		int i = MathHelper.floor(axisalignedbb.minX);
		int j = MathHelper.floor(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor(axisalignedbb.minY);
		int l = MathHelper.floor(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor(axisalignedbb.minZ);
		int j1 = MathHelper.floor(axisalignedbb.maxZ + 1.0D);
		for (int k1 = i; k1 < j; k1++) {
			for (int l1 = i1; l1 < j1; l1++) {
				if (!world.isBlockLoaded(new BlockPos(k1, 64, l1))) {
					continue;
				}
				for (int i2 = k - 1; i2 < l; i2++) {
					BlockPos pos = new BlockPos(k1, i2, l1);
					IBlockState state = world.getBlockState(pos);
					state.addCollisionBoxToList(world, pos, axisalignedbb, collidingBoundingBoxes, entity, true);
				}
			}
		}
		return collidingBoundingBoxes;
	}

}
