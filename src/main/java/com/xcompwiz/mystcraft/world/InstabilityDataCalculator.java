package com.xcompwiz.mystcraft.world;

import java.io.File;
import java.util.HashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;

import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBiome;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class InstabilityDataCalculator {

	private static final String		StorageID	= "myst_baseline";

	private MinecraftServer			mcserver;
	private MapStorage				storage;
	private int						minimumchunks;

	private HashMap<String, Float>	constants;

	private float					tolerance	= 1.01F;

	public InstabilityDataCalculator(MinecraftServer mcserver) {
		this.mcserver = mcserver;
		this.storage = mcserver.worldServerForDimension(0).mapStorage;
		this.minimumchunks = ModifierBiome.selectables.size() * 20;
		ChunkProfiler profiler = getChunkProfiler();
		this.chunkX = profiler.getCount();
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (mcserver == null) return;
		ChunkProfiler profiler = getChunkProfiler();
		if (profiler.getCount() <= minimumchunks) {
			stepChunkGeneration(profiler);
		} else {
			cleanup();
			constants = getChunkProfiler().calculateSplitInstability();
			for (String key : constants.keySet()) {
				float val = constants.get(key);
				val = (val * tolerance);
				val = (float) (Math.floor(val / 100) * 100);
				ChunkProfiler.setBaselineStability(key, val);
			}
			mcserver = null;
		}
	}

	public void shutdown() {
		this.cleanup();
		mcserver = null;
	}

	private ChunkProfiler getChunkProfiler() {
		ChunkProfiler chunkprofiler = (ChunkProfiler) storage.loadData(ChunkProfiler.class, StorageID);
		if (chunkprofiler == null) {
			chunkprofiler = new ChunkProfiler(StorageID);
			storage.setData(StorageID, chunkprofiler);
		}
		chunkprofiler.setDebugName(StorageID);
		return chunkprofiler;
	}

	int				chunkX		= 0;
	int				chunkZ		= 0;

	private Integer	providerId	= null;
	private Integer	dimId		= null;
	private World	world;

	private void cleanup() {
		if (world != null) {
			DimensionManager.unloadWorld(world.provider.dimensionId);
			world = null;
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (dimId != null && event.world.provider.dimensionId == dimId) {
			((WorldProviderMystDummy) event.world.provider).replaceChunkProvider();
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if (dimId != null && event.world.provider.dimensionId == dimId) {
			if (dimId != null) DimensionManager.unregisterDimension(dimId);
			dimId = null;
			if (providerId != null) DimensionManager.unregisterProviderType(providerId);
			providerId = null;
			File dir = event.world.getSaveHandler().getWorldDirectory();
			dir = new File(dir, event.world.provider.getSaveFolder());
			dir.deleteOnExit();
			//TODO: Request for the profiled data to save NOW
		}
	}

	private void stepChunkGeneration(ChunkProfiler profiler) {
		//If the world has yet to be established, we create a fake world to play with.
		if (world == null) {
			//First we need to create a provider specifically for this purpose.  We don't care at all for the id we get, so try something silly and then go from there.
			providerId = Integer.MIN_VALUE;
			while (true) {
				try {
					DimensionManager.registerProviderType(providerId, WorldProviderMystDummy.class, false);
					break;
				} catch (Exception e) {
					++providerId;
				}
			}
			//Next we get a dim id the same way.
			dimId = Integer.MIN_VALUE;
			while (true) {
				try {
					DimensionManager.registerDimension(dimId, providerId);
					break;
				} catch (Exception e) {
					++dimId;
				}
			}
			//Finally we obtain a world instance and set the chunk profiler for it.
			world = mcserver.worldServerForDimension(dimId);
			if (world == null) throw new RuntimeException("Could not create Instability Comparison Dimension");
			((WorldProviderMystDummy) world.provider).setChunkProfiler(profiler);
		}

		//At every call of this function, we want to fully generate a single chunk.
		IChunkProvider chunkgen = ((WorldServer) world).theChunkProviderServer;
		IChunkLoader chunkloader = ((WorldServer) world).theChunkProviderServer.currentChunkLoader;

		if (safeLoadChunk(chunkloader, world, chunkX, chunkZ) == null) {
			chunkgen.loadChunk(chunkX, chunkZ + 2);
			chunkgen.loadChunk(chunkX, chunkZ + 1);
			chunkgen.loadChunk(chunkX, chunkZ);
			chunkgen.loadChunk(chunkX, chunkZ - 1);
		}
		++chunkX;
	}

	//XXX: Duplicated from AgeController
	private Chunk safeLoadChunk(IChunkLoader chunkloader, World worldObj, int par1, int par2) {
		if (chunkloader == null) { return null; }
		try {
			return chunkloader.loadChunk(worldObj, par1, par2);
		} catch (Exception exception) {
			return null;
		}
	}
}
