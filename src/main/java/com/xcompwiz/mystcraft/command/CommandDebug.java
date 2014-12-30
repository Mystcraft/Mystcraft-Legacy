package com.xcompwiz.mystcraft.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import com.xcompwiz.mystcraft.debug.DebugHierarchy;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugValueCallback;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.IDebugElement;
import com.xcompwiz.mystcraft.debug.DebugUtils;

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
		if (args.length >= 2 && (args[0].equals("read") || args[0].equals("set"))) return getListOfStringsMatchingLastWord(args, this.getKeys(args));
		return null;
	}

	protected String[] getKeys(String[] args) {
		String address = getAddress(Arrays.copyOf(args, args.length-1));
		IDebugElement elem = DebugUtils.getElement(address);
		if (elem == null) elem = DebugHierarchy.root;
		Collection<String> allflags = Collections.EMPTY_LIST;
		if (elem instanceof DebugNode) {
			allflags = ((DebugNode) elem).getChildren();
		}
		String[] params = allflags.toArray(new String[allflags.size()]);
		return params;
	}

	//Helper
	private String getAddress(String[] args) {
		if (args.length == 0) return null;
		StringBuilder str = new StringBuilder();
		String command = args[0];
		int max = args.length;
		if (command.equals("set")) {
			--max;
		}
		for (int i = 1; i < max; ++i) {
			if (str.length() > 0) str.append(".");
			str.append(args[i]);
		}
		return str.toString();
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {
		String command = null;
		String address = null;

		if (args.length > 1) {
			command = args[0];
			address = getAddress(args);
		} else {
			throw new WrongUsageException("Could not parse command.");
		}
		if (command.equals("read")) {
			IDebugElement elem = DebugUtils.getElement(address);
			if (elem instanceof DebugValueCallback) {
				String value = ((DebugValueCallback) elem).get(agent);
				agent.addChatMessage(new ChatComponentText(value));
			} else if (elem instanceof DebugNode) {
				Collection<String> children = ((DebugNode) elem).getChildren();
				agent.addChatMessage(new ChatComponentTranslation("%s", children.toString()));
			} else {
				throw new CommandException("myst.debug.address.invalid");
			}
		} else if (command.equals("set")) {
			IDebugElement elem = DebugUtils.getElement(address);
			if (elem instanceof DebugValueCallback) {
				boolean b = true;
				if (args.length > 2) b = (Boolean.parseBoolean(args[args.length - 1]) || args[args.length - 1].equals("1"));
				((DebugValueCallback) elem).set(agent, b);
			} else {
				throw new CommandException("myst.debug.address.invalid");
			}
		}
	}
}
