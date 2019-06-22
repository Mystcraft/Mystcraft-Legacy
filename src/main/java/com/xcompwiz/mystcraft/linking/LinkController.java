package com.xcompwiz.mystcraft.linking;

import java.util.ArrayList;
import java.util.List;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAlter;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;

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
		
		entity = entity.getLowestRidingEntity();

		MinecraftServer mcServer = Mystcraft.sidedProxy.getMCServer();
		if (mcServer == null || (dimension != 0 && !mcServer.getAllowNether()))
			return false;

		WorldServer newworld = mcServer.getWorld(dimension);
		if (newworld == null) {
			System.err.println("Cannot Link Entity to Dimension: Could not get World for Dimension " + dimension);
			return false;
		}

		// Get destination coords if not set
		if (spawn == null) {
			spawn = newworld.getSpawnPoint();
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

	private static class LinkTeleporter implements ITeleporter {
		private final BlockPos targetPos;
		private final float yaw;

		private LinkTeleporter(BlockPos spawn, float yaw, ILinkInfo info) {
			this.targetPos = spawn;
			this.yaw = yaw;
		}

		@Override
		public void placeEntity(World world, Entity entity, float yaw) {
			entity.moveToBlockPosAndAngles(this.targetPos, this.yaw, entity.rotationPitch);
		}
		
		@Override
		public boolean isVanilla() {
			return false;
		}
	}

	private static Entity teleportEntity(World newworld, Entity entity, int dimension, BlockPos spawn, float yaw, ILinkInfo info) {
		List<Entity> passengers = entity.getPassengers();
		List<Entity> successfulPassengers = new ArrayList<Entity>();

		for (Entity mounted : passengers) {
			mounted.dismountRidingEntity();
			Entity result = teleportEntity(newworld, mounted, dimension, spawn, yaw, info);
			if (result != null)
				successfulPassengers.add(result);
		}

		World origin = entity.getEntityWorld();
		if (!LinkListenerManager.isLinkPermitted(origin, entity, info)) {
			return null;
		}

		LinkListenerManager.onLinkStart(origin, entity, info);

		// Fix broken world teleportation causing desync
		if (entity.isSneaking()) {
			entity.setSneaking(false);
		}
		
		if (origin != newworld) {
			Entity result = entity.changeDimension(dimension, new LinkTeleporter(spawn, yaw, info));
			if (result == null)
			{
				LinkListenerManager.onLinkFailed(origin, entity, info);
				return null;
			}
			entity = result;
		} else {
			// Move entity
			entity.setLocationAndAngles(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, yaw, entity.rotationPitch);
		}
		((WorldServer) newworld).getChunkProvider().loadChunk(spawn.getX() >> 4, spawn.getZ() >> 4);

		while (LinkController.getCollidingWorldGeometry(newworld, entity.getEntityBoundingBox(), entity).size() != 0) {
			spawn = spawn.up();
			entity.setPosition(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5);
		}

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.connection.setPlayerLocation(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, player.rotationYaw, player.rotationPitch);
			newworld.updateEntityWithOptionalForce(entity, false);
		}

		LinkListenerManager.onLinkEnd(origin, newworld, entity, info);

		for (Entity passenger : successfulPassengers) {
			if (passenger instanceof EntityPlayerMP) {
				newworld.updateEntityWithOptionalForce(passenger, true);
			}
			passenger.startRiding(entity);
		}

		return entity;
	}

	private static List<AxisAlignedBB> getCollidingWorldGeometry(World world, AxisAlignedBB axisalignedbb,
			Entity entity) {
		ArrayList<AxisAlignedBB> collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
		int i = MathHelper.floor(axisalignedbb.minX);
		int j = MathHelper.floor(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor(axisalignedbb.minY);
		int l = MathHelper.floor(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor(axisalignedbb.minZ);
		int j1 = MathHelper.floor(axisalignedbb.maxZ + 1.0D);
		for (int k1 = i; k1 < j; k1++) {
			for (int l1 = i1; l1 < j1; l1++) {
				if (!world.isBlockLoaded(new BlockPos(k1, 128 / 2, l1))) {
					continue;
				}
				for (int i2 = k - 1; i2 < l; i2++) {
					BlockPos pos = new BlockPos(l1, i2, l1);
					IBlockState state = world.getBlockState(pos);
					state.addCollisionBoxToList(world, pos, axisalignedbb, collidingBoundingBoxes, entity, true);
				}
			}
		}
		return collidingBoundingBoxes;
	}

}
