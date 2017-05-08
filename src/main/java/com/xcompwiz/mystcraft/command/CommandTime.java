package com.xcompwiz.mystcraft.command;

import java.util.List;

import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.world.WorldInfoUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CommandTime extends CommandBaseAdv {

	@Override
	public String getName() {
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
	public String getUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.time.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 1) {
			long value;
			long daytime = 0;
			long nighttime = 12500;

			Integer dimension = null;
			if (args.length > 2) {
				if (!args[2].equals("all")) {
					dimension = parseInt(args[2]);
				}
			} else {
				dimension = getSenderDimension(sender);
			}
			if (dimension != null) {
				WorldServer world = DimensionManager.getWorld(dimension);
				if (world == null) { throw new CommandException("commands.myst.time.fail.noworld"); }
				daytime = WorldInfoUtils.getWorldNextDawnTime(world);
				nighttime = WorldInfoUtils.getWorldNextDuskTime(world);
			}

			if (args[0].equals("set")) {
				if (args[1].equals("day")) {
					value = daytime;
				} else if (args[1].equals("night")) {
					value = nighttime;
				} else {
					value = parseInt(args[1], 0);
				}

				this.setTime(server, sender, value, dimension);
				return;
			}

			if (args[0].equals("add")) {
				value = parseInt(args[1], 0);
				this.addTime(server, sender, value, dimension);
				return;
			}
		}

		throw new WrongUsageException(getUsage(sender), new Object[0]);
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender par1ICommandSender, String[] par2ArrayOfStr, @Nullable BlockPos targetPos) {
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
	protected void setTime(MinecraftServer server, ICommandSender sender, long value, Integer dim) {
		if (dim == null) {
			for (int var3 = 0; var3 < server.worlds.length; ++var3) {
				server.worlds[var3].setWorldTime(value);
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
	protected void addTime(MinecraftServer server, ICommandSender sender, long value, Integer dim) {
		if (dim == null) {
			for (int var3 = 0; var3 < server.worlds.length; ++var3) {
				WorldServer var4 = server.worlds[var3];
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
