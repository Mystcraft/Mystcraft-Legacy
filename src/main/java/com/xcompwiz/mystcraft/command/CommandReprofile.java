package com.xcompwiz.mystcraft.command;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.profiling.ChunkProfiler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class CommandReprofile extends CommandBaseAdv {

	@Override
	public String getName() {
		return "myst-reprofile";
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.reprofile.usage";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if (sender.getName().equals("XCompWiz"))
			return true;
		return super.checkPermission(server, sender);
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
			throw new CommandException("Cannot (re)profile block instability for non-Mystcraft Dimensions", new Object[0]);
		}

		World worldObj = server.getWorld(dimId);
		ChunkProfiler chunkprofiler = (ChunkProfiler) worldObj.getPerWorldStorage().getOrLoadData(ChunkProfiler.class, ChunkProfiler.ID);
		chunkprofiler.clear();

		sendToAdmins(sender, "Cleared Profile Data for Dimension " + dimId, new Object[0]);
	}
}
