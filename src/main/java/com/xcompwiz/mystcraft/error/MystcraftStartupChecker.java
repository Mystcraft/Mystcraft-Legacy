package com.xcompwiz.mystcraft.error;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.xcompwiz.mystcraft.MystcraftFirstRun;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.api.world.logic.ILightingController;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;
import com.xcompwiz.mystcraft.api.world.logic.IWeatherController;
import com.xcompwiz.mystcraft.client.gui.error.GuiNonCriticalError;
import com.xcompwiz.mystcraft.client.gui.overlay.GuiNotification;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.world.profiling.GuiMystcraftProfiling;
import com.xcompwiz.mystcraft.world.profiling.InstabilityDataCalculator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class MystcraftStartupChecker {

	public static abstract class ErrorChecker {

		private boolean	hasRun	= false;

		public final boolean hasRun() {
			return hasRun;
		}

		public final boolean run() {
			hasRun = true;
			GuiScreen gui = getErrorGui();
			if (gui != null) {
				Minecraft.getMinecraft().displayGuiScreen(gui);
				return true;
			}
			return false;
		}

		protected abstract GuiScreen getErrorGui();

	}

	public static class CheckSymbolLoadError extends ErrorChecker {
		@Override
		protected GuiScreen getErrorGui() {
			HashSet<String> errored = SymbolManager.getErroredSymbols();
			if (!errored.isEmpty()) {
				ArrayList<String> messages = new ArrayList<String>();
				messages.add("WARNING: Mystcraft detected errors in the following symbols on loadup.");
				messages.add("These symbols are not loaded and will not occur in the game.");
				messages.add("Please check your setup and attempt to get a log of this session to the symbol developer.");
				messages.add("To prevent this message from coming up in the future, disable or remove these symbols.");
				messages.add("");
				messages.addAll(errored);
				return new GuiNonCriticalError(messages);
			}
			return null;
		}
	}

	public static class CheckSymbolLogicError extends ErrorChecker {

		private Class	clazz;
		private String	type;

		public CheckSymbolLogicError(Class clazz, String type) {
			this.clazz = clazz;
			this.type = type;
		}

		@Override
		protected GuiScreen getErrorGui() {
			IAgeSymbol symbol = SymbolManager.findAgeSymbolImplementing(new Random(0), clazz);
			if (symbol == null) {
				ArrayList<String> messages = new ArrayList<String>();
				messages.add("ERROR: Mystcraft detected errors on loadup.");
				messages.add("There are no symbols which provide logic for " + type + ".");
				messages.add("Mystcraft requires at least one symbol providing logic of this type.");
				messages.add("Be aware the Mystcraft WILL crash should you try to enter an age.");
				return new GuiNonCriticalError(messages);
			}
			return null;
		}
	}

	private HashSet<ErrorChecker>	checks		= new HashSet<ErrorChecker>();
	HashSet<ErrorChecker>			completed	= new HashSet<ErrorChecker>();

	private GuiNotification			updateNotification;

	public MystcraftStartupChecker() {
		checks.add(new CheckSymbolLoadError());
		checks.add(new CheckSymbolLogicError(IBiomeController.class, "Biome Distribution"));
		checks.add(new CheckSymbolLogicError(ITerrainGenerator.class, "World Terrain Type"));
		checks.add(new CheckSymbolLogicError(ILightingController.class, "Lighting"));
		checks.add(new CheckSymbolLogicError(IWeatherController.class, "Weather"));
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event) {
		if (MystcraftFirstRun.isReady()) return;
		MystcraftFirstRun.onSaveEvent(event);
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (MystcraftFirstRun.isStopped() && Minecraft.getMinecraft().func_147104_D() == null) MystcraftFirstRun.start();
	}

	@SubscribeEvent
	public void onClientInitGUI(InitGuiEvent.Post event) {
		if (event.gui instanceof GuiMainMenu) {
			if (checkForErrors() && !InstabilityDataCalculator.isPerSave() && !InstabilityDataCalculator.isDisabled()) MystcraftFirstRun.enable();
		}
	}

	@SubscribeEvent
	public void onClientOpenGUI(GuiOpenEvent event) {
		if (InstabilityDataCalculator.isPerSave()) return;
		if (InstabilityDataCalculator.isDisabled()) return;
		if (MystcraftFirstRun.isReady()) return;
		if (!MystcraftFirstRun.isEnabled()) return;
		if (event.gui instanceof GuiMystcraftProfiling) return;
		if (MystcraftFirstRun.isStopped() && Minecraft.getMinecraft().func_147104_D() == null) MystcraftFirstRun.start();
		if (event.gui instanceof GuiSelectWorld) {
			if (MystcraftFirstRun.isStopped()) {
				ArrayList<String> messages = new ArrayList<String>();
				messages.add("Mystcraft hasn't finished it's profiling yet.");
				messages.add("Unfortunately, this does not seem to be running. Try restarting your client.");
				messages.add("");
				messages.add("Mystcraft requires some information about the generation provided by the mods in your game.");
				messages.add("In order to do this, it does a first-time profiling. This must finish before you start a Single Player game.");
				event.gui = new GuiNonCriticalError(messages);
			} else {
				MystcraftFirstRun.showProfilingGui();
				event.setCanceled(true);
			}
		}
		if (MystcraftFirstRun.isStopped()) return;
		if (event.gui == null) {
			MystcraftFirstRun.showProfilingGui();
			event.setCanceled(true);
		}
		if (event.gui instanceof GuiDownloadTerrain) {
			MystcraftFirstRun.showProfilingGui();
			event.setCanceled(true);
		}
		if (event.gui instanceof GuiIngameMenu) {
			MystcraftFirstRun.showProfilingGui();
			event.setCanceled(true);
		}
		if (event.gui instanceof GuiConnecting) {
			MystcraftFirstRun.stop();
		}
	}

	@SubscribeEvent
	public void onClientRenderTick(RenderTickEvent event) {
		if (event.phase == Phase.END) {
			if (updateNotification != null) {
				updateNotification.render();
			}
		}
	}

	private synchronized boolean checkForErrors() {
		for (ErrorChecker check : checks) {
			completed.add(check);
			if (!check.hasRun()) {
				if (check.run()) {
					LoggerUtils.info("Mystcraft Start-Up Error Checking Reported an Error to the user.");
					break;
				}
			}
		}
		for (ErrorChecker check : completed) {
			checks.remove(check);
		}
		boolean ret = false;
		if (checks.size() == 0 && completed.size() > 0) {
			LoggerUtils.info("Mystcraft Start-Up Error Checking Completed");
			ret = true;
		}
		completed.clear();
		return ret;
	}

	public GuiNotification getNotificationGui() {
		if (updateNotification == null) updateNotification = new GuiNotification(Minecraft.getMinecraft());
		return updateNotification;
	}
}
