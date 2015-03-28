package com.xcompwiz.mystcraft.world;

import java.io.File;
import java.util.HashMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;

import com.mojang.authlib.GameProfile;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAllow;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.debug.DebugHierarchy;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DefaultValueCallback;
import com.xcompwiz.mystcraft.instability.InstabilityBlockManager;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.network.packet.MPacketProfilingState;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBiome;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public class InstabilityDataCalculator {

	private static final String		StorageID			= "myst_baseline";

	private static boolean			disconnectclients	= false;

	private MinecraftServer			mcserver;
	private MapStorage				storage;

	private int						minimumchunks;							//TODO: Make this configurable
	private float					tolerance			= 1.05F;			//TODO: Make this configurable

	private HashMap<String, Number>	freevals;

	private Integer					providerId			= null;
	private Integer					dimId				= null;
	private World					world;

	public static void loadConfigs(Configuration config) {
		disconnectclients = config.get(MystConfig.CATEGORY_GENERAL, "options.profiling.baseline.disconnectclients", disconnectclients, "If set to true this will prevent clients from connecting while baseline profiling is ongoing (Only works on dedicated servers)").getBoolean(disconnectclients);
	}

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
		if (event.phase == Phase.START) return;
		if (mcserver == null) return;
		ChunkProfiler profiler = getChunkProfiler();
		if (profiler.getCount() <= minimumchunks) {
			stepChunkGeneration(profiler);
		} else {
			cleanup();
			HashMap<String, Float> constants = profiler.calculateSplitInstability();
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

	private void cleanup() {
		if (world != null) {
			DimensionManager.unloadWorld(world.provider.dimensionId);
			world = null;
			mcserver.getConfigurationManager().sendPacketToAllPlayers(MPacketProfilingState.createPacket(false));
			LoggerUtils.info("Baseline Profiling for Instability completed.");
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
		if (dimId != null && event.info.getDimensionUID() == dimId) event.setCanceled(true);
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
	public void connectionOpened(ServerConnectionFromClientEvent event) {
		if (mcserver != null) {
			if (disconnectclients && mcserver.isDedicatedServer()) {
				String denymessage = "Mystcraft still needs to finish profiling. Please try again later.";
				try {
					((NetHandlerPlayServer) event.handler).kickPlayerFromServer(denymessage);
					GameProfile gameprofile = ((NetHandlerPlayServer) event.handler).playerEntity.getGameProfile();
					LoggerUtils.info("Disconnecting " + getPlayernameFromProfile(event.manager, gameprofile) + ": " + denymessage);
				} catch (Exception exception) {
					LoggerUtils.error("Error whilst disconnecting player", exception);
				}
			} else {
				event.manager.scheduleOutboundPacket(MPacketProfilingState.createPacket(true));
			}
		}
	}

	//XXX: Move to some Utils
	public String getPlayernameFromProfile(NetworkManager networkmanager, GameProfile gameprofile) {
		return gameprofile != null ? gameprofile.toString() + " (" + networkmanager.getSocketAddress().toString() + ")" : String.valueOf(networkmanager.getSocketAddress());
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		//TODO: We should probably try and prohibit teleportation to this dimension...
		//Alternatively, detect player changed dimension and queue them to teleport again next tick
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
			LoggerUtils.info("Baseline Profiling for Instability started. Expect some lag.");
			WorldProviderMystDummy.setChunkProfiler(profiler);
			WorldProviderMystDummy.setBounds(profiler.getCount() - 1, //TODO: Tie this to the needed chunk radius
					minimumchunks + 2, -1, 2);
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
		}

		((WorldProviderMystDummy) world.provider).generateNextChunk();
	}
}
