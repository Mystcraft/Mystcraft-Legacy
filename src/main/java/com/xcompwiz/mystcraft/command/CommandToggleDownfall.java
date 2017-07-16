package com.xcompwiz.mystcraft.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class CommandToggleDownfall extends CommandBaseAdv {
	@Override
	public String getName() {
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
	public String getUsage(ICommandSender icommandsender) {
		return "commands.myst.toggledownfall.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Integer dimension = null;
		if (args.length > 0) {
			dimension = parseInt(args[0]);
		}
		if (dimension == null) {
			dimension = getSenderDimension(sender);
		}
		if (dimension != null) {
			this.toggleDownfall(server, dimension);
			sendToAdmins(sender, "commands.myst.downfall.success", new Object[] { dimension });
		} else {
			throw new CommandException("commands.myst.downfall.fail.nodim");
		}
	}

	/**
	 * Toggle rain and enable thundering.
	 */
	protected void toggleDownfall(MinecraftServer server, int dimension) {
		World world = server.getWorld(dimension);
		world.getWorldInfo().setRaining(!world.isRaining()); // Forge: !!!Welp, they broke weather more! Override for getWorldInfo would fix.
		world.getWorldInfo().setThundering(true);
	}
}
