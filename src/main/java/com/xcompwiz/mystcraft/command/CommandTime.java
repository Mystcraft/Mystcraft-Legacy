package com.xcompwiz.mystcraft.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.xcompwiz.mystcraft.world.WorldInfoHelper;

public class CommandTime extends CommandBaseAdv {

	@Override
	public String getCommandName() {
		return "time";
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
		return "commands.myst.time.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length > 1) {
			long value;
			long daytime = 0;
			long nighttime = 12500;

			Integer dimension = null;
			if (args.length > 2) {
				if (!args[2].equals("all")) {
					dimension = parseInt(sender, args[2]);
				}
			} else {
				dimension = getSenderDimension(sender);
			}
			if (dimension != null) {
				WorldServer world = DimensionManager.getWorld(dimension);
				if (world == null) { throw new CommandException("commands.myst.time.fail.noworld"); }
				daytime = WorldInfoHelper.getWorldNextDawnTime(world);
				nighttime = WorldInfoHelper.getWorldNextDuskTime(world);
			}

			if (args[0].equals("set")) {
				if (args[1].equals("day")) {
					value = daytime;
				} else if (args[1].equals("night")) {
					value = nighttime;
				} else {
					value = parseIntWithMin(sender, args[1], 0);
				}

				this.setTime(sender, value, dimension);
				return;
			}

			if (args[0].equals("add")) {
				value = parseIntWithMin(sender, args[1], 0);
				this.addTime(sender, value, dimension);
				return;
			}
		}

		throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		if (par2ArrayOfStr.length == 1) {
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] { "set", "add" });
		} else if (par2ArrayOfStr.length == 2 && par2ArrayOfStr[0].equals("set")) {
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] { "day", "night" });
		} else if (par2ArrayOfStr.length == 3) { return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] { "all" }); }
		return null;
	}

	/**
	 * Set the time in the server object.
	 */
	protected void setTime(ICommandSender sender, long value, Integer dim) {
		if (dim == null) {
			for (int var3 = 0; var3 < MinecraftServer.getServer().worldServers.length; ++var3) {
				MinecraftServer.getServer().worldServers[var3].setWorldTime(value);
				sendToAdmins(sender, "commands.myst.time.set.all", new Object[] { Long.valueOf(value) });
			}
		} else {
			DimensionManager.getWorld(dim).setWorldTime(value);
			sendToAdmins(sender, "commands.myst.time.set", new Object[] { Long.valueOf(value), dim });
		}
	}

	/**
	 * Adds (or removes) time in the server object.
	 */
	protected void addTime(ICommandSender sender, long value, Integer dim) {
		if (dim == null) {
			for (int var3 = 0; var3 < MinecraftServer.getServer().worldServers.length; ++var3) {
				WorldServer var4 = MinecraftServer.getServer().worldServers[var3];
				var4.setWorldTime(var4.getWorldTime() + value);
				sendToAdmins(sender, "commands.myst.time.added.all", new Object[] { Long.valueOf(value) });
			}
		} else {
			WorldServer var4 = DimensionManager.getWorld(dim);
			var4.setWorldTime(var4.getWorldTime() + value);
			sendToAdmins(sender, "commands.myst.time.added", new Object[] { Long.valueOf(value), dim });
		}
	}
}
