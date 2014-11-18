package com.xcompwiz.mystcraft.command;

import java.util.Collection;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import com.xcompwiz.mystcraft.core.DebugDataTracker;

public class CommandDebug extends CommandBaseAdv {

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
		return "/" + this.getCommandName() + " <'read'> <param> OR <'set'> <flag>";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (sender.getCommandSenderName().equals("XCompWiz")) return true;
		return super.canCommandSenderUseCommand(sender);
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args) {
		if (args.length < 2) return getListOfStringsMatchingLastWord(args, new String[] { "read", "set" });
		if (args[0].equals("read") && args.length == 2) return getListOfStringsMatchingLastWord(args, this.getParams());
		if (args[0].equals("set") && args.length == 2) return getListOfStringsMatchingLastWord(args, this.getFlags());
		return null;
	}

	protected String[] getParams() {
		Collection<String> allparams = DebugDataTracker.getParams();
		String[] params = allparams.toArray(new String[allparams.size()]);
		return params;
	}

	protected String[] getFlags() {
		Collection<String> allflags = DebugDataTracker.getFlags();
		String[] params = allflags.toArray(new String[allflags.size()]);
		return params;
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {
		String command = null;
		String flag = null;

		if (args.length > 1) {
			command = args[0];
			flag = args[1];
		} else {
			throw new WrongUsageException("Could not parse command.");
		}
		if (command.equals("read")) {
			String value = DebugDataTracker.get(flag);
			agent.addChatMessage(new ChatComponentText(value));
		} else if (command.equals("set")) {
			boolean b = true;
			if (args.length > 2) b = (Boolean.parseBoolean(args[2]) || args[2].equals("1"));
			DebugDataTracker.setFlag(agent, flag, b);
		}
	}
}
