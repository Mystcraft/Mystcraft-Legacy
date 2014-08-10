package com.xcompwiz.mystcraft.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

public class CommandToggleWorldInstability extends CommandMyst {

	@Override
	public String getCommandName() {
		return "myst-toggleworldinstability";
	}

	@Override
	public List<String> getCommandAliases() {
		List<String> list = new ArrayList<String>();
		list.add("myst-twi");
		return list;
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.twi.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Integer dimId = null;
		try {
			dimId = parseInt(sender, args[0]);
		} catch (Exception e) {
			dimId = getSenderDimension(sender);
		}

		if (!Mystcraft.registeredDims.contains(dimId) || AgeData.getAge(dimId, false) == null) { throw new CommandException("Cannot toggle instability for non-Mystcraft Dimensions", new Object[0]); }

		AgeData data = AgeData.getAge(dimId, false);
		boolean setting = !data.isInstabilityEnabled();
		if (args.length > 0) {
			if (args[args.length - 1].equals("true")) setting = true;
			if (args[args.length - 1].equals("false")) setting = false;
		}
		data.setInstabilityEnabled(setting);

		sendToAdmins(sender, sender.getCommandSenderName() + " Toggled Instability for Dimension " + dimId + "(" + data.isInstabilityEnabled() + ")", new Object[0]);
	}
}
