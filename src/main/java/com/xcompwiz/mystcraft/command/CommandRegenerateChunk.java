package com.xcompwiz.mystcraft.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xcompwiz.mystcraft.utility.ReflectionUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CommandRegenerateChunk extends CommandBaseAdv {

	@Override
	public String getName() {
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
	public String getUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.chunkregen.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		int range = 3;
		Integer dimension = null;
		Integer chunkX = null;
		Integer chunkZ = null;
		if (args.length > 0) {
			range = parseInt(args[0]);
		}
		if (args.length > 1) {
			dimension = parseInt(args[1]);
		}
		if (args.length > 2) {
			chunkX = parseInt(args[2]);
		}
		if (args.length > 3) {
			chunkZ = parseInt(args[3]);
		}

		EntityPlayer caller = getCommandSenderAsPlayer(sender);

		if (dimension == null) {
			dimension = caller.dimension;
		}
		if (chunkX == null || chunkZ == null) {
			chunkX = (int) caller.posX >> 4;
			chunkZ = (int) caller.posZ >> 4;
		}

		WorldServer worldObj = DimensionManager.getWorld(dimension);
		if (worldObj == null) { throw new CommandException("The target world is not loaded"); }

		ChunkProviderServer chunkprovider = worldObj.getChunkProvider();
		List<EntityPlayer> players = new ArrayList<>();
		players.addAll(worldObj.playerEntities);

		for (int x = chunkX - range; x <= chunkX + range; ++x) {
			for (int z = chunkZ - range; z <= chunkZ + range; ++z) {
				for (EntityPlayer player : players) {
					if(worldObj.getPlayerChunkMap().isPlayerWatchingChunk((EntityPlayerMP) player, x, z)) {
						player.setLocationAndAngles((chunkX - range - 2) << 4, player.posY, (chunkZ - range - 2) << 4, 0, 0);
						worldObj.updateEntityWithOptionalForce(player, false);
					}
				}
				Chunk c = chunkprovider.getLoadedChunk(x, z);
				if(c != null) {
					chunkprovider.unload(c);
				}
			}
		}
		int lastloaded = 0;
		while (chunkprovider.getLoadedChunkCount() != lastloaded) {
			lastloaded = chunkprovider.getLoadedChunkCount();
			chunkprovider.tick();
		}
		System.out.println(chunkprovider.makeString());

		for (int x = chunkX - range; x <= chunkX + range; ++x) {
			for (int z = chunkZ - range; z <= chunkZ + range; ++z) {
                long i = ChunkPos.asLong(x, z);
                Chunk chunk;
                try {
                    chunk = chunkprovider.chunkGenerator.provideChunk(x, z);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                    crashreportcategory.addCrashSection("Location", String.format("%d,%d", x, z));
                    crashreportcategory.addCrashSection("Position hash", i);
                    crashreportcategory.addCrashSection("Generator", chunkprovider.chunkGenerator);
                    throw new ReportedException(crashreport);
                }

                chunkprovider.id2ChunkMap.put(i, chunk);
                chunk.onChunkLoad();
                chunk.populateChunk(chunkprovider, chunkprovider.chunkGenerator);
				chunkprovider.loadChunk(x, z);
			}
		}

		sendToAdmins(sender, String.format("%s regenerated chunks (%d, %d)+-%d in Dimension %d", sender.getName(), chunkX, chunkZ, range, caller.dimension), new Object[0]);

		for (int x = chunkX - range; x <= chunkX + range; ++x) {
			for (int z = chunkZ - range; z <= chunkZ + range; ++z) {
				Chunk chunk = worldObj.getChunkFromChunkCoords(x, z);
				Packet pkt = new SPacketChunkData(chunk, 0xFFFFFFFF);
				sendToAllPlayersWatchingChunk(worldObj, chunk.getPos(), pkt);
			}
		}
	}

	private void sendToAllPlayersWatchingChunk(WorldServer worldObj, ChunkPos chunkLocation, Packet pkt) {
		Collection<EntityPlayer> players = worldObj.playerEntities;
		PlayerChunkMapEntry entry = worldObj.getPlayerChunkMap().getEntry(chunkLocation.chunkXPos, chunkLocation.chunkZPos);
		if(entry != null) {
            entry.sendPacket(pkt);
        }
	}
}
