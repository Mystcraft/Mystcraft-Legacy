package com.xcompwiz.mystcraft.command;

import com.xcompwiz.mystcraft.entity.EntityMeteor;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class CommandSpawnMeteor extends CommandBaseAdv {

	@Override
	public String getCommandName() {
		return "myst-spawnmeteor";
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
		return "commands.myst.meteor.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		World worldObj = sender.getEntityWorld();
		if (worldObj == null) throw new CommandException("This command does not function from the commandline");
		Integer dimId = worldObj.provider.dimensionId;
		ChunkCoordinates coords = sender.getPlayerCoordinates();
		Double originx = 0.5D + coords.posX;
		Double originy = 500D;
		Double originz = 0.5D + coords.posZ;
		Double xd = 0D;
		Double zd = 0D;

		float scale = 1.0F;
		if (args.length > 0) {
			scale = parseFloat(sender, args[0]);
		}
		int penetration = 0;
		if (args.length > 1) {
			penetration = parseInt(sender, args[1]);
		}
		if (args.length > 3) {
			originx = 0.5D + handleRelativeNumber(sender, coords.posX, args[2]);
			originz = 0.5D + handleRelativeNumber(sender, coords.posZ, args[3]);
		}
		Entity entity = new EntityMeteor(worldObj, scale, penetration, originx, originy, originz, xd, -3, zd);
		worldObj.spawnEntityInWorld(entity);
		sendToAdmins(sender, sender.getCommandSenderName() + String.format(" spawned a meteor at (%.3f, %.3f, %.3f, %d)", originx, originy, originz, dimId), new Object[0]);
	}
}
