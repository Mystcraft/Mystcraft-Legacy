package com.xcompwiz.mystcraft.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.world.ChunkProfiler;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

public class CommandReprofile extends CommandBaseAdv {

	@Override
	public String getCommandName() {
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
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.reprofile.usage";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (sender.getCommandSenderName().equals("XCompWiz")) return true;
		return super.canCommandSenderUseCommand(sender);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Integer dimId = null;
		try {
			dimId = parseInt(sender, args[0]);
		} catch (Exception e) {
			dimId = getSenderDimension(sender);
		}

		if (!Mystcraft.registeredDims.contains(dimId) || AgeData.getAge(dimId, false) == null) { throw new CommandException("Cannot (re)profile block instability for non-Mystcraft Dimensions", new Object[0]); }

		MinecraftServer server = MinecraftServer.getServer();
		World worldObj = server .worldServerForDimension(dimId);
		ChunkProfiler chunkprofiler = (ChunkProfiler) worldObj.perWorldStorage.loadData(ChunkProfiler.class, ChunkProfiler.ID);
		chunkprofiler.clear();

		sendToAdmins(sender, "Cleared Profile Data for Dimension " + dimId, new Object[0]);
	}
}
