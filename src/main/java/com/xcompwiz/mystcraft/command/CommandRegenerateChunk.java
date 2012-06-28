package com.xcompwiz.mystcraft.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

public class CommandRegenerateChunk extends CommandMyst {

	@Override
	public String getCommandName() {
		return "myst-regenchunk";
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.chunkregen.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		int range = 3;
		Integer dimension = null;
		Integer chunkX = null;
		Integer chunkZ = null;
		if (args.length > 0) {
			range = parseInt(sender, args[0]);
		}
		if (args.length > 1) {
			dimension = parseInt(sender, args[1]);
		}
		if (args.length > 2) {
			chunkX = parseInt(sender, args[2]);
		}
		if (args.length > 3) {
			chunkZ = parseInt(sender, args[3]);
		}

		EntityPlayer caller = null;
		try {
			caller = getCommandSenderAsPlayer(sender);
		} catch (Exception e) {
		}

		if (dimension == null) {
			if (caller == null) { throw new PlayerNotFoundException("To use this from the commandline you must provide a dimension!", new Object[0]); }
			dimension = caller.dimension;
		}
		if (chunkX == null || chunkZ == null) {
			if (caller == null) { throw new PlayerNotFoundException("To use this from the commandline you must provide x and z coordinates!", new Object[0]); }
			chunkX = (int) caller.posX >> 4;
			chunkZ = (int) caller.posZ >> 4;
		}

		WorldServer worldObj = DimensionManager.getWorld(dimension);
		if (worldObj == null) { throw new CommandException("The target world is not loaded"); }

		ChunkProviderServer chunkprovider = (ChunkProviderServer) worldObj.getChunkProvider();
		List<EntityPlayerMP> players = new ArrayList<EntityPlayerMP>();
		players.addAll(worldObj.playerEntities);

		for (int x = chunkX - range; x <= chunkX + range; ++x) {
			for (int z = chunkZ - range; z <= chunkZ + range; ++z) {
				for (EntityPlayerMP player : players) {
					if (worldObj.getPlayerManager().isPlayerWatchingChunk(player, x, z)) {
						player.setLocationAndAngles((chunkX - range - 2) << 4, player.posY, (chunkZ - range - 2) << 4, 0, 0);
						worldObj.updateEntityWithOptionalForce(player, false);
					}
				}
				chunkprovider.unloadChunksIfNotNearSpawn(x, z);
			}
		}
		int lastloaded = 0;
		while (chunkprovider.getLoadedChunkCount() != lastloaded) {
			lastloaded = chunkprovider.getLoadedChunkCount();
			chunkprovider.unloadQueuedChunks();
		}
		System.out.println(chunkprovider.makeString());

		IChunkLoader chunkloader = chunkprovider.currentChunkLoader;
		chunkprovider.currentChunkLoader = null;

		for (int x = chunkX - range; x <= chunkX + range; ++x) {
			for (int z = chunkZ - range; z <= chunkZ + range; ++z) {
				chunkprovider.loadChunk(x, z);
			}
		}
		chunkprovider.currentChunkLoader = chunkloader;

		notifyAdmins(sender, String.format("%s regenerated chunks (%d, %d)+-%d in Dimension %d", sender.getCommandSenderName(), chunkX, chunkZ, range, caller.dimension), new Object[0]);

		for (int x = chunkX - range; x <= chunkX + range; ++x) {
			for (int z = chunkZ - range; z <= chunkZ + range; ++z) {
				Chunk chunk = worldObj.getChunkFromChunkCoords(x, z);
				Packet pkt = new S21PacketChunkData(chunk, true, 0xFFFFFFFF);
				sendToAllPlayersWatchingChunk(worldObj, chunk.getChunkCoordIntPair(), pkt);
			}
		}
	}

	public void sendToAllPlayersWatchingChunk(WorldServer worldObj, ChunkCoordIntPair chunkLocation, Packet pkt) {
		Collection<EntityPlayer> players = worldObj.playerEntities;
		for (EntityPlayer entityplayer : players) {
			if (!(entityplayer instanceof EntityPlayerMP)) continue;
			EntityPlayerMP entityplayermp = (EntityPlayerMP) entityplayer;
			if (!entityplayermp.loadedChunks.contains(chunkLocation)) {
				entityplayermp.playerNetServerHandler.sendPacket(pkt);
			}
		}
	}
}
