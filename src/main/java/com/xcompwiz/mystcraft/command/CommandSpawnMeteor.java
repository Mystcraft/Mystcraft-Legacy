package com.xcompwiz.mystcraft.command;

import com.xcompwiz.mystcraft.entity.EntityMeteor;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSpawnMeteor extends CommandBaseAdv {

	@Override
	public String getName() {
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
	public String getUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.meteor.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World worldObj = sender.getEntityWorld();
		if (worldObj == null)
			throw new CommandException("This command does not function from the commandline");
		Integer dimId = worldObj.provider.getDimension();
		BlockPos coords = sender.getPosition();
		Double originx = 0.5D + coords.getX();
		Double originy = 500D;
		Double originz = 0.5D + coords.getZ();
		Double xd = 0D;
		Double zd = 0D;

		float scale = 1.0F;
		if (args.length > 0) {
			scale = parseFloat(sender, args[0]);
		}
		int penetration = 0;
		if (args.length > 1) {
			penetration = parseInt(args[1]);
		}
		if (args.length > 3) {
			originx = 0.5D + handleRelativeNumber(sender, coords.getX(), args[2]);
			originz = 0.5D + handleRelativeNumber(sender, coords.getZ(), args[3]);
		}
		Entity entity = new EntityMeteor(worldObj, scale, penetration, originx, originy, originz, xd, -3, zd);
		worldObj.spawnEntity(entity);
		sendToAdmins(sender, sender.getName() + String.format(" spawned a meteor at (%.3f, %.3f, %.3f, %d)", originx, originy, originz, dimId), new Object[0]);
	}
}
