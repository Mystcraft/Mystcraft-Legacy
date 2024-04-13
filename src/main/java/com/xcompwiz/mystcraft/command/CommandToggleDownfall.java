package com.xcompwiz.mystcraft.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class CommandToggleDownfall extends CommandBaseAdv {
	@Override
	public String getCommandName() {
		return "toggledownfall";
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "commands.myst.toggledownfall.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Integer dimension = null;
		if (args.length > 0) {
			dimension = parseInt(sender, args[0]);
		}
		if (dimension == null) {
			dimension = getSenderDimension(sender);
		}
		if (dimension != null) {
			this.toggleDownfall(dimension);
			sendToAdmins(sender, "commands.myst.downfall.success", new Object[] { dimension });
		} else {
			throw new CommandException("commands.myst.downfall.fail.nodim");
		}
	}

	/**
	 * Toggle rain and enable thundering.
	 */
	protected void toggleDownfall(int dimension) {
		World world = MinecraftServer.getServer().worldServerForDimension(dimension);
		world.getWorldInfo().setRaining(!world.isRaining()); // Forge: !!!Welp, they broke weather more! Override for getWorldInfo would fix.
		world.getWorldInfo().setThundering(true);
	}
}
