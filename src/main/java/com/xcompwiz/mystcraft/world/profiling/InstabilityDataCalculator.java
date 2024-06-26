package com.xcompwiz.mystcraft.world.profiling;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAllow;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.debug.DebugHierarchy;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DefaultValueCallback;
import com.xcompwiz.mystcraft.instability.InstabilityBlockManager;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketProfilingState;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public class InstabilityDataCalculator {

	private static final String StorageID = "myst_baseline";

	private static MystConfig balanceconfig;

	private static boolean persave = true;
	private static boolean disconnectclients = false;
	private static boolean useconfigs = false;
	private static int tickrate = 5;

	private static int minimumchunks;							//TODO: Make this configurable
	private static float tolerance;								//TODO: Make this configurable

	private MinecraftServer mcserver;
	private MapStorage storage;

	private HashMap<String, Number> freevals;
	private static Map<String, Number> defaults;

	private Integer providerId = null;
	private DimensionType dimensionType = null;
	private Integer dimId = null;
	private World world = null;
	private boolean running = false;
	private int tickAccumulator;

	private IMystcraftProfilingCallback callback;

	public static void loadConfigs(Configuration config) {
		minimumchunks = 0;
		tolerance = 1.05F;
		persave = config.get(MystConfig.CATEGORY_BASELINING, "client.persave", persave, "If false, the profiling will run on game startup with the loading bar. If true, it will run in the background when playing. Setting this to false disables tickrate checking, even on the server.").getBoolean(persave);
		useconfigs = config.get(MystConfig.CATEGORY_BASELINING, "useconfigs", useconfigs, "If true, the baseline calculations won't run and instead a config file will be read.").getBoolean(useconfigs);
		disconnectclients = config.get(MystConfig.CATEGORY_BASELINING, "server.disconnectclients", disconnectclients, "If set to true this will prevent clients from connecting while baseline profiling is ongoing (Only works on dedicated servers)").getBoolean(disconnectclients);
		tickrate = config.get(MystConfig.CATEGORY_BASELINING, "tickrate.minimum", tickrate, "This controls the minimum number of ticks to wait before a new chunk will be generated when doing the baseline profiling in the background.").getInt(tickrate);
		if (!persave)
			tickrate = 1;
	}

	public static void loadBalanceData() {
		if (useconfigs) {
			setBaselineDefaults();
			loadBaselineFromConfig(balanceconfig);
		}
	}

	private static void setBaselineDefaults() {
		defaults = new HashMap<>();
		defaults.put("minecraft:coal_ore_0", 300);
		defaults.put("minecraft:diamond_ore_0", 1000);
		defaults.put("minecraft:emerald_ore_0", 100);
		defaults.put("minecraft:glowstone_0", 0);
		defaults.put("minecraft:gold_ore_0", 500);
		defaults.put("minecraft:iron_ore_0", 500);
		defaults.put("minecraft:lapis_ore_0", 100);
		defaults.put("minecraft:quartz_ore_0", 0);
		defaults.put("minecraft:redstone_ore_0", 600);
		defaults.put("mystcraft:blockcrystal_0", 0);
	}

	private static void loadBaselineFromConfig(Configuration config) {
		Collection<String> keyset = InstabilityBlockManager.getWatchedBlocks();
		HashMap<String, Number> freevals = new HashMap<String, Number>();
		for (String key : keyset) {
			int def = getBaselineVanillaDefault(key);
			float val = config.get(MystConfig.CATEGORY_BASELINING, key, def).getInt(def);
			freevals.put(key, val);
		}
		InstabilityBlockManager.setBaselineStability(freevals);
		if (config != null && config.hasChanged())
			config.save();
	}

	private static int getBaselineVanillaDefault(String key) {
		Number def = defaults.get(key);
		if (def == null)
			return 0;
		return def.intValue();
	}

	public static boolean isPerSave() {
		return persave;
	}

	public static boolean isDisabled() {
		return useconfigs;
	}

	public static DebugNode getDebugNode() {
		DebugNode current = DebugHierarchy.root;
		current = current.getOrCreateNode("data");
		current = current.getOrCreateNode("instability_calc");
		return current;
	}

	public InstabilityDataCalculator(MinecraftServer mcserver, MapStorage storage) {
		this.mcserver = mcserver;
		this.storage = storage;
		final ChunkProfiler profiler = getChunkProfiler(storage);

		DebugNode node = getDebugNode();
		//@formatter:off
		node.addChild("profiled_chunks", new DefaultValueCallback() {
			@Override public String get(ICommandSender agent) {
				return "" + profiler.getCount();
			}
		});
		this.registerDebugInfo(node.getOrCreateNode("freevals"));
		profiler.registerDebugInfo(node.getOrCreateNode("profiled"));
		//@formatter:on
	}

	private void registerDebugInfo(DebugNode node) {
		for (final String blockkey : InstabilityBlockManager.getWatchedBlocks()) {
			node.addChild(blockkey.replaceAll("\\.", "_"), new DefaultValueCallback() {
				private InstabilityDataCalculator calculator;
				private String blockkey;

				@Override
				public String get(ICommandSender agent) {
					HashMap<String, Number> split = calculator.freevals;
					if (split == null)
						return "N/A";
					Number val = split.get(blockkey);
					if (val == null)
						return "None";
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
		if (event.phase == Phase.START)
			return;
		if (mcserver == null)
			return;
		ChunkProfiler profiler = getChunkProfiler(storage);
		int chunksremaining = getChunksRemaining(profiler);
		if (callback != null)
			callback.setCompleted(profiler.getCount());
		if (callback != null)
			callback.setRemaining(chunksremaining);
		if (callback != null)
			callback.setQueued(ChunkProfilerManager.getSize());
		if (++tickAccumulator < tickrate)
			return;
		tickAccumulator = 0;
		if (chunksremaining > 0) {
			// We check to see if the profiling queue is backed up (might be enough chunks generated.
			if (ChunkProfilerManager.getSize() < chunksremaining)
				stepChunkGeneration(profiler);
		} else {
			if (world != null)
				LoggerUtils.info("Baseline Profiling for Instability completed.");
			cleanup();
			freevals = updateInstabilityData(profiler);
			mcserver = null;
			if (callback != null)
				callback.onFinished();
		}
	}

	public static HashMap<String, Number> updateInstabilityData(ChunkProfiler profiler) {
		HashMap<String, Float> generated = profiler.calculateSplitInstability();
		HashMap<String, Number> freevals = new HashMap<String, Number>();
		for (String key : generated.keySet()) {
			float val = generated.get(key);
			val = (val * tolerance);
			val = (float) (Math.ceil(val / 100) * 100);
			freevals.put(key, val);
		}
		InstabilityBlockManager.setBaselineStability(freevals);
		return freevals;
	}

	public static int getChunksRemaining(ChunkProfiler profiler) {
		if (minimumchunks == 0) {
			int factor = (int) Math.ceil(500D / WorldProviderMystDummy.getAndPrepareBiomeList().size());
			if (factor < 10)
				factor = 10;
			minimumchunks = WorldProviderMystDummy.getAndPrepareBiomeList().size() * factor;
		}
		return minimumchunks - profiler.getCount();
	}

	public void shutdown() {
		this.cleanup();
		mcserver = null;
		if (persave)
			InstabilityBlockManager.clearBaselineStability();

		DebugNode node = getDebugNode();
		node.parent.removeChild(node);
	}

	public static ChunkProfiler getChunkProfiler(MapStorage storage) {
		ChunkProfiler chunkprofiler = (ChunkProfiler) storage.getOrLoadData(ChunkProfiler.class, StorageID);
		if (chunkprofiler == null) {
			chunkprofiler = new ChunkProfiler(StorageID);
			storage.setData(StorageID, chunkprofiler);
		}
		return chunkprofiler;
	}

	private void cleanup() {
		if (world != null) {
			LoggerUtils.info("Baseline Profiling cleaning up.");
			running = false;
			DimensionManager.unloadWorld(dimId);
			world = null;
			MystcraftPacketHandler.CHANNEL.sendToAll(new MPacketProfilingState(false));
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if (dimId == null || event.getWorld().provider.getDimension() != dimId)
			return;
		if (event.getWorld() != world) {
			LoggerUtils.error("World with matching dim id to profiling dim unloaded!");
			return;
		}
		if (running) {
			LoggerUtils.info("Baseline Profiling world unloaded before time!");
			world = null;
			return;
		}
		LoggerUtils.info("Baseline Profiling world unloading.");
		if (dimId != null)
			DimensionManager.unregisterDimension(dimId);
		dimId = null;
		if (providerId != null) {
			DimensionManager.unregisterProviderType(providerId);
		}
		providerId = null;
		File dir = event.getWorld().getSaveHandler().getWorldDirectory();
		dir = new File(dir, event.getWorld().provider.getSaveFolder());
		dir.deleteOnExit();
		//TODO: Request for the profile data to save NOW
	}

	@SubscribeEvent
	public void isLinkPermitted(LinkEventAllow event) {
		if (dimId != null && dimId.equals(event.info.getDimensionUID()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (running) {
			MystcraftPacketHandler.CHANNEL.sendTo(new MPacketProfilingState(running), (EntityPlayerMP) event.player);
		}
		// detect player entered dimension and queue them to teleport again next tick
		if (dimId != null && event.player.dimension == dimId) {
			DimensionUtils.scheduleEjectPlayerFromDimension(event.player);
		}
	}

	@SubscribeEvent
	public void connectionOpened(ServerConnectionFromClientEvent event) {
		if (mcserver != null) {
			if (disconnectclients && mcserver.isDedicatedServer()) {
				String denymessage = "Mystcraft still needs to finish profiling. Please try again later.";
				try {
					((NetHandlerPlayServer) event.getHandler()).disconnect(new TextComponentString(denymessage));
					GameProfile gameprofile = ((NetHandlerPlayServer) event.getHandler()).player.getGameProfile();
					LoggerUtils.info("Disconnecting " + getIDString(event.getManager(), gameprofile) + ": " + denymessage);
				} catch (Exception exception) {
					LoggerUtils.error("Error whilst disconnecting player", exception);
				}
			}
		}
	}

	//XXX: Move to some Utils
	public String getIDString(NetworkManager networkmanager, GameProfile gameprofile) {
		return gameprofile != null ? gameprofile.toString() + " (" + networkmanager.getRemoteAddress().toString() + ")" : String.valueOf(networkmanager.getRemoteAddress());
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		//TODO: We should probably try and prohibit teleportation to this dimension...

		// detect player changed dimension and queue them to teleport again next tick
		if (dimId != null && event.toDim == dimId) {
			DimensionUtils.scheduleEjectPlayerFromDimension(event.player);
		}
	}

	private void stepChunkGeneration(ChunkProfiler profiler) {
		if (providerId == null) {
			//First we need to create a provider specifically for this purpose.  We don't care at all for the id we get, so try something silly and then go from there.
			providerId = Integer.MIN_VALUE;
			while (true) {
				try {
					dimensionType = DimensionType.register("Mystcraft_ProfilerDummy", "_mystprof", providerId, WorldProviderMystDummy.class, false);
					break;
				} catch (Exception e) {
					++providerId;
				}
			}
			LoggerUtils.info("Baseline Profiling provider registered at %d", providerId);
		}
		if (dimId == null) {
			//Next we get a dim id the same way.
			dimId = Integer.MIN_VALUE;
			while (true) {
				try {
					DimensionManager.registerDimension(dimId, dimensionType);
					break;
				} catch (Exception e) {
					++dimId;
				}
			}
			LoggerUtils.info("Baseline Profiling dimension registered at %d", dimId);
		}
		//If the world has yet to be established, we create a fake world to play with.
		if (world == null) {
			running = true;
			LoggerUtils.info("Baseline Profiling for Instability started. Expect some lag.");
			WorldProviderMystDummy.setChunkProfiler(profiler);
			WorldProviderMystDummy.setBounds(profiler.getCount() - 1, //TODO: Tie this to the needed chunk radius
					minimumchunks + 2, -1, 2);
			//Finally we obtain a world instance and set the chunk profiler for it.
			world = mcserver.getWorld(dimId);
			if (world == null)
				throw new RuntimeException("Could not create Instability Comparison Dimension");
		}

		((WorldProviderMystDummy) world.provider).generateNextChunk();
	}

	public void setCallback(IMystcraftProfilingCallback callback) {
		this.callback = callback;
	}

	public static void setBalanceConfig(MystConfig config) {
		balanceconfig = config;
	}
}
