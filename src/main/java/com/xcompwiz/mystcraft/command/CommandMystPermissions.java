package com.xcompwiz.mystcraft.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import com.xcompwiz.mystcraft.linking.LinkListenerPermissions;

public class CommandMystPermissions extends CommandBaseAdv {
	@Override
	public String getCommandName() {
		return "myst-permissions";
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.permissions.usage";
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args) {
		if (args.length == 1) return getListOfStringsMatchingLastWord(args, this.getPlayers());
		if (args.length == 2) return getListOfStringsMatchingLastWord(args, "restrict", "permit");
		if (args.length == 3) return getListOfStringsMatchingLastWord(args, "entry", "depart");
		if (args.length == 4) return getListOfStringsMatchingLastWord(args, "all");
		return null;
	}

	protected String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Integer dim = null;
		if (args.length < 4) { throw new WrongUsageException("commands.myst.permissions.usage"); }
		String playername = args[0];
		String command = args[1];
		String direction = args[2];
		String dim_str = args[3];
		if (!dim_str.equals("all")) {
			dim = parseInt(sender, dim_str);
		}
		if (command.equals("permit") && direction.equals("depart")) {
			LinkListenerPermissions.permitPlayerDepart(playername, dim);
		} else if (command.equals("restrict") && direction.equals("depart")) {
			LinkListenerPermissions.restrictPlayerDepart(playername, dim);
		} else if (command.equals("permit") && direction.equals("entry")) {
			LinkListenerPermissions.permitPlayerEntry(playername, dim);
		} else if (command.equals("restrict") && direction.equals("entry")) {
			LinkListenerPermissions.restrictPlayerEntry(playername, dim);
		} else {
			throw new WrongUsageException("commands.myst.permissions.usage");
		}
		sendToAdmins(sender, "Set permissions for player: " + playername + " " + command + " " + direction + " " + dim_str, new Object[0]);
	}
}
