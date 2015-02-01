package com.xcompwiz.mystcraft.world;

import java.io.File;
import java.util.HashMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;

import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAllow;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.debug.DebugHierarchy;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DefaultValueCallback;
import com.xcompwiz.mystcraft.instability.InstabilityBlockManager;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBiome;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class InstabilityDataCalculator {

	private static final String		StorageID	= "myst_baseline";

	private MinecraftServer			mcserver;
	private MapStorage				storage;

	private int						minimumchunks; //TODO: Make this configurable
	private float					tolerance	= 1.05F; //TODO: Make this configurable

	private HashMap<String, Number>	freevals;

	public static DebugNode getDebugNode() {
		DebugNode current = DebugHierarchy.root;
		current = current.getOrCreateNode("data");
		current = current.getOrCreateNode("instability_calc");
		return current;
	}

	public InstabilityDataCalculator(MinecraftServer mcserver) {
		this.mcserver = mcserver;
		this.storage = mcserver.worldServerForDimension(0).mapStorage;
		this.minimumchunks = ModifierBiome.selectables.size() * 20;
		final ChunkProfiler profiler = getChunkProfiler();
		this.chunkX = profiler.getCount() - 1;

		DebugNode node = getDebugNode();
		//@formatter:off
		node.addChild("profiled_chunks", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return "" + profiler.getCount(); }});
		this.registerDebugInfo(node.getOrCreateNode("freevals"));
		profiler.registerDebugInfo(node.getOrCreateNode("profiled"));
		//@formatter:on
	}

	private void registerDebugInfo(DebugNode node) {
		for (final String blockkey : InstabilityBlockManager.getWatchedBlocks()) {
			node.addChild(blockkey.replaceAll("\\.", "_"), new DefaultValueCallback() {
				private InstabilityDataCalculator	calculator;
				private String						blockkey;

				@Override
				public String get(ICommandSender agent) {
					HashMap<String, Number> split = calculator.freevals;
					if (split == null) return "N/A";
					Number val = split.get(blockkey);
					if (val == null) return "None";
					return "" + val;
				}

				private DefaultValueCallback init(InstabilityDataCalculator calculator, String blockkey) {
					this.calculator = calculator;
					this.blockkey = blockkey;
					return this;
				}
			}.init(this, blockkey));
		}
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (mcserver == null) return;
		ChunkProfiler profiler = getChunkProfiler();
		if (profiler.getCount() <= minimumchunks) {
			stepChunkGeneration(profiler);
		} else {
			cleanup();
			HashMap<String, Float> constants = getChunkProfiler().calculateSplitInstability();
			freevals = new HashMap<String, Number>();
			for (String key : constants.keySet()) {
				float val = constants.get(key);
				val = (val * tolerance);
				val = (float) (Math.ceil(val / 100) * 100);
				freevals.put(key, val);
			}
			InstabilityBlockManager.setBaselineStability(freevals);
			mcserver = null;
		}
	}

	public void shutdown() {
		this.cleanup();
		InstabilityBlockManager.clearBaselineStability();
		mcserver = null;

		DebugNode node = getDebugNode();
		node.parent.removeChild(node);
	}

	private ChunkProfiler getChunkProfiler() {
		ChunkProfiler chunkprofiler = (ChunkProfiler) storage.loadData(ChunkProfiler.class, StorageID);
		if (chunkprofiler == null) {
			chunkprofiler = new ChunkProfiler(StorageID);
			storage.setData(StorageID, chunkprofiler);
		}
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
			//TODO: Request for the profile data to save NOW
		}
	}

	@SubscribeEvent
	public void isLinkPermitted(LinkEventAllow event) {
		if (dimId != null && event.destination.provider.dimensionId == dimId) event.setCanceled(true);
	}

		@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (dimId != null && event.player.dimension == dimId) {
			ILinkInfo link = new LinkOptions(null);
			link.setDimensionUID(0);
			link.setFlag(ILinkPropertyAPI.FLAG_TPCOMMAND, true);
			LinkController.travelEntity(event.player.worldObj, event.player, link);
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		if (dimId != null && event.toDim == dimId) {
			ILinkInfo link = new LinkOptions(null);
			link.setDimensionUID(0);
			link.setFlag(ILinkPropertyAPI.FLAG_TPCOMMAND, true);
			LinkController.travelEntity(event.player.worldObj, event.player, link);
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
			//TODO: We should probably try and prohibit teleportation to this dimension...
			//Alternatively, detect player changed dimension and queue them to teleport again next tick
		}

		//At every call of this function, we want to fully generate a single chunk.
		IChunkProvider chunkgen = ((WorldServer) world).theChunkProviderServer;
		IChunkLoader chunkloader = ((WorldServer) world).theChunkProviderServer.currentChunkLoader;

		if (safeLoadChunk(chunkloader, world, chunkX, chunkZ) == null) {
			//We generate a band in order to get the center chunk populated.
			//Technically we don't profile it until the next set is populated, due to needing all the surrounding chunks to be complete
			//The first set actually only provides the end cap, and is never profiled at all
			//XXX: (Optimization) One chunk per tick rather than 4
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
