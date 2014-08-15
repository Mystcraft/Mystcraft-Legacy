package com.xcompwiz.mystcraft.command;

import java.util.Collection;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import com.xcompwiz.mystcraft.core.DebugDataTracker;

public class CommandDebug extends CommandMyst {

	@Override
	public String getCommandName() {
		return "myst-dbg";
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
		return "/" + this.getCommandName() + " param";
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args) {
		if (args.length == 1) return getListOfStringsMatchingLastWord(args, this.getParams());
		return null;
	}

	protected String[] getParams() {
		Collection<String> allparams = DebugDataTracker.getParams();
		String[] params = allparams.toArray(new String[allparams.size()]);
		return params;
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {
		String sTarget = null;

		if (args.length > 0) {
			sTarget = args[0];
		} else {
			throw new WrongUsageException("Could not parse command.");
		}
		String value = DebugDataTracker.get(sTarget);
		agent.addChatMessage(new ChatComponentText(value));
	}
}
