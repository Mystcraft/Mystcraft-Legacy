package com.xcompwiz.mystcraft.command;

import java.util.ArrayList;
import java.util.List;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandToggleWorldInstability extends CommandBaseAdv {

	@Override
	public String getName() {
		return "myst-toggleworldinstability";
	}

	@Override
	public List<String> getAliases() {
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
	public String getUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.twi.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Integer dimId = null;
		try {
			dimId = parseInt(args[0]);
		} catch (Exception e) {
			dimId = getSenderDimension(sender);
		}

		if (!Mystcraft.registeredDims.contains(dimId) || AgeData.getAge(dimId, false) == null) {
			throw new CommandException("Cannot toggle instability for non-Mystcraft Dimensions", new Object[0]);
		}

		AgeData data = AgeData.getAge(dimId, false);
		boolean setting = !data.isInstabilityEnabled();
		if (args.length > 0) {
			if (args[args.length - 1].equals("true"))
				setting = true;
			if (args[args.length - 1].equals("false"))
				setting = false;
		}
		data.setInstabilityEnabled(setting);

		sendToAdmins(sender, sender.getName() + " Toggled Instability for Dimension " + dimId + "(" + data.isInstabilityEnabled() + ")", new Object[0]);
	}
}
