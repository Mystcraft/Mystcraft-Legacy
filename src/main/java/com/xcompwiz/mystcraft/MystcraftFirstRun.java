package com.xcompwiz.mystcraft;

import com.xcompwiz.mystcraft.core.TaskQueueManager;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.world.profiling.ChunkProfiler;
import com.xcompwiz.mystcraft.world.profiling.GuiMystcraftProfiling;
import com.xcompwiz.mystcraft.world.profiling.InstabilityDataCalculator;
import com.xcompwiz.mystcraft.world.storage.ExternalSaveHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MystcraftFirstRun {
	private static final String SAVE_NAME = "mystcraft_profiling";

	private static boolean readyToPlay = false;
	private static InstabilityDataCalculator instabilitycalculator;

	private static MapStorage storage;
	private static GuiMystcraftProfiling guiscreen;

	@SideOnly(Side.CLIENT)
	public static void enable() {
		Minecraft mc = Minecraft.getMinecraft();
		if (guiscreen == null)
			guiscreen = new GuiMystcraftProfiling(mc.currentScreen);
	}

	@SideOnly(Side.CLIENT)
	public synchronized static void start() {
		if (instabilitycalculator != null)
			return;
		Minecraft mc = Minecraft.getMinecraft();

		ExternalSaveHandler savehandler = new ExternalSaveHandler(mc.mcDataDir, "mystcraft");
		storage = new MapStorage(savehandler);

		ChunkProfiler profiler = InstabilityDataCalculator.getChunkProfiler(storage);
		int remaining = InstabilityDataCalculator.getChunksRemaining(profiler);
		if (remaining == 0) { // We're already ready to roll
			LoggerUtils.info("Client baseline profiling completed.");
			InstabilityDataCalculator.updateInstabilityData(profiler);
			readyToPlay = true;
			return;
		}
		// Display a gui to the user
		if (guiscreen == null)
			guiscreen = new GuiMystcraftProfiling(mc.currentScreen);
		mc.displayGuiScreen(guiscreen);

		// Spin up a server instance
		WorldSettings worldsettings = new WorldSettings(0, GameType.CREATIVE, true, false, WorldType.DEFAULT); // TODO: Configurable world seed?
		//XXX: worldsettings.func_82750_a(SAVE_NAME);
		worldsettings.enableCommands();
		mc.launchIntegratedServer(SAVE_NAME, SAVE_NAME, worldsettings);
		MinecraftServer mcserver = mc.getIntegratedServer();

		// Start the instability calculator
		instabilitycalculator = new InstabilityDataCalculator(mcserver, storage);
		instabilitycalculator.setCallback(guiscreen);
		MinecraftForge.EVENT_BUS.register(instabilitycalculator);
	}

	public synchronized static void stop() {
		if (storage == null || instabilitycalculator == null)
			return;
		storage.saveAllData();
		instabilitycalculator.shutdown();
		MinecraftForge.EVENT_BUS.unregister(instabilitycalculator);
		storage = null;
		instabilitycalculator = null;
		// Spin down the server instance
		// Minecraft.getMinecraft().getIntegratedServer().initiateShutdown();
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.world != null) {
			mc.world.sendQuittingDisconnectingPacket();
			mc.loadWorld(null);
		}
	}

	public static boolean isStopped() {
		return storage == null && guiscreen != null;
	}

	public static boolean shouldRun() {
		return instabilitycalculator == null && Minecraft.getMinecraft().world == null && !Minecraft.getMinecraft().isIntegratedServerRunning();
	}

	@SideOnly(Side.CLIENT)
	public synchronized static void end() {
		Runnable deleteworld = new Runnable() {
			@Override
			public void run() {
				// Delete the save data (not reuseable anymore)
				ISaveFormat isaveformat = Minecraft.getMinecraft().getSaveLoader();
				isaveformat.flushCache();
				isaveformat.deleteWorldDirectory(SAVE_NAME);
				readyToPlay = true;
			}
		};
		TaskQueueManager.runOnServerShutdown(deleteworld);
		stop();
	}

	@SideOnly(Side.CLIENT)
	public static boolean isReady() {
		return readyToPlay;
	}

	public static void showProfilingGui() {
		Minecraft.getMinecraft().displayGuiScreen(guiscreen);
	}

	public static void onSaveEvent(Save event) {
		if (storage != null)
			storage.saveAllData();
	}

	public static boolean isEnabled() {
		return guiscreen != null;
	}
}
