package com.xcompwiz.mystcraft.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.debug.DebugHierarchy;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugTaskCallback;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugValueCallback;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.IDebugElement;
import com.xcompwiz.mystcraft.debug.DebugUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandDebug extends CommandBaseAdv {

	@Override
	public String getName() {
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
	public String getUsage(ICommandSender par1ICommandSender) {
		return "/" + this.getName() + " <'read'> <param> OR <'set'> <flag>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if (sender.getName().equals("XCompWiz"))
			return true;
		return super.checkPermission(server, sender);
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender par1ICommandSender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length < 2)
			return getListOfStringsMatchingLastWord(args, new String[] { "read", "set", "run" });
		if (args.length >= 2 && (args[0].equals("read") || args[0].equals("set") || args[0].equals("run")))
			return getListOfStringsMatchingLastWord(args, this.getKeys(args));
		return null;
	}

	protected String[] getKeys(String[] args) {
		String address = getAddress(Arrays.copyOf(args, args.length - 1));
		IDebugElement elem = DebugUtils.getElement(address);
		if (elem == null)
			elem = DebugHierarchy.root;
		Collection<String> allflags = Collections.emptyList();
		if (elem instanceof DebugNode) {
			allflags = ((DebugNode) elem).getChildren();
		}
		String[] params = allflags.toArray(new String[allflags.size()]);
		return params;
	}

	//Helper
	private String getAddress(String[] args) {
		if (args.length == 0)
			return null;
		StringBuilder str = new StringBuilder();
		for (int i = 1; i < args.length; ++i) {
			if (str.length() > 0)
				str.append(".");
			str.append(args[i]);
		}
		return str.toString();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender agent, String[] args) throws CommandException {
		String command = null;
		String address = null;
		String setval = null;

		if (args.length > 1) {
			command = args[0];
			if (command.equals("set")) {
				setval = args[args.length - 1];
				args = Arrays.copyOf(args, args.length - 1);
			}
			address = getAddress(args);
		} else {
			throw new WrongUsageException("Could not parse command.");
		}
		IDebugElement elem = DebugUtils.getElement(address);
		if (command.equals("read")) {
			if (elem instanceof DebugValueCallback) {
				String value = ((DebugValueCallback) elem).get(agent);
				agent.sendMessage(new TextComponentString(value));
			} else if (elem instanceof DebugNode) {
				Collection<String> children = ((DebugNode) elem).getChildren();
				agent.sendMessage(new TextComponentTranslation("%s", children.toString()));
			} else {
				throw new CommandException("myst.debug.address.invalid");
			}
		} else if (command.equals("run")) {
			if (elem instanceof DebugTaskCallback) {
				((DebugTaskCallback) elem).run(agent, setval);
			}
		} else if (command.equals("set")) {
			if (elem instanceof DebugValueCallback) {
				if (args.length <= 2)
					throw new WrongUsageException("Could not parse command.");
				((DebugValueCallback) elem).set(agent, setval);
			} else {
				throw new CommandException("myst.debug.address.invalid");
			}
		}
	}
}
