package com.xcompwiz.mystcraft.error;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

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

	private HashSet<ErrorChecker>	checks		= new HashSet<ErrorChecker>();
	HashSet<ErrorChecker>			completed	= new HashSet<ErrorChecker>();

	private GuiNotification			updateNotification;

	public MystcraftStartupChecker() {
		checks.add(new CheckSymbolLoadError());
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
