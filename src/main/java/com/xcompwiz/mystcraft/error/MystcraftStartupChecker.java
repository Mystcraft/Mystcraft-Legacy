package com.xcompwiz.mystcraft.error;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.api.world.logic.ILightingController;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;
import com.xcompwiz.mystcraft.api.world.logic.IWeatherController;
import com.xcompwiz.mystcraft.client.gui.error.GuiNonCriticalError;
import com.xcompwiz.mystcraft.client.gui.overlay.GuiNotification;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

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
			if (symbol == null){
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
	public void onClientTick(ClientTickEvent event) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
			checkForErrors();
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

	private synchronized void checkForErrors() {
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
		if (checks.size() == 0 && completed.size() > 0) LoggerUtils.info("Mystcraft Start-Up Error Checking Completed");
		completed.clear();
	}

	public GuiNotification getNotificationGui() {
		if (updateNotification == null) updateNotification = new GuiNotification(Minecraft.getMinecraft());
		return updateNotification;
	}
}
