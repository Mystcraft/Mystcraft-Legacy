package com.xcompwiz.mystcraft.command;

import java.util.List;

import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkOptions;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandTPX extends CommandBaseAdv {

	@Override
	public String getName() {
		return "tpx";
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
		return "commands.myst.tpx.usage";
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender par1ICommandSender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) return getListOfStringsMatchingLastWord(args, this.getPlayers(server));
		if (args.length == 2) return getListOfStringsMatchingLastWord(args, this.getPlayers(server));
		return null;
	}

	protected String[] getPlayers(MinecraftServer server) {
		return server.getOnlinePlayerNames();
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	@Override
	public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
		return par2 == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Entity subject = null;
		ILinkInfo link = null;
		String sSubject = null;
		String sTarget = null;
		String sX = null, sY = null, sZ = null;

		if (args.length > 3) {
			if (args.length > 4) sSubject = args[args.length - 5];
			sTarget = args[args.length - 4];
			sX = args[args.length - 3];
			sY = args[args.length - 2];
			sZ = args[args.length - 1];
		} else if (args.length == 2) {
			sSubject = args[args.length - 2];
			sTarget = args[args.length - 1];
		} else if (args.length == 1) {
			sTarget = args[args.length - 1];
		} else {
			throw new WrongUsageException("commands.myst.tpx.usage");
		}

		if (sSubject == null) {
			subject = getCommandSenderAsPlayer(sender);
		} else {
			subject = getTargetPlayer(server, sender, sSubject);
		}
		if (subject == null) { throw new WrongUsageException("commands.myst.tpx.fail.nosubject"); }

		link = getLinkInfoForTarget(server, sender, subject, sTarget, sX, sY, sZ);

		link.setFlag(LinkPropertyAPI.FLAG_INTRA_LINKING, true);
		makeOpTP(link);
		LinkController.travelEntity(subject.world, subject, link);
	}

	public static ILinkInfo getLinkInfoForTarget(MinecraftServer server, ICommandSender sender, Entity subject, String sTarget, String sX, String sY, String sZ) throws CommandException {
		ILinkInfo link = null;
		try {
			Entity target = getTargetPlayer(server, sender, sTarget);
			link = InternalAPI.linking.createLinkInfoFromPosition(target.world, target);
		} catch (PlayerNotFoundException e) {
		}
		if (link == null) {
			link = new LinkOptions(null);
			int dim = (int) (handleRelativeNumber(sender, subject.dimension, sTarget, 0, 0) - 0.5D);
			if (server.worldServerForDimension(dim) == null) { throw new CommandException("commands.myst.tpx.fail.noworld", new Object[] { dim }); }
			link.setDimensionUID(dim);
			if (sX != null && sY != null && sZ != null) {
				int x = (int) handleRelativeNumber(sender, subject.posX, sX);
				int y = (int) handleRelativeNumber(sender, subject.posY, sY, 0, 0);
				int z = (int) handleRelativeNumber(sender, subject.posZ, sZ);
				link.setSpawn(new BlockPos(x, y, z));
			}
		}
		return link;
	}

	public static boolean isOpTP(ILinkInfo link) {
		return link.getFlag(LinkPropertyAPI.FLAG_TPCOMMAND);
	}

	private void makeOpTP(ILinkInfo link) {
		link.setFlag(LinkPropertyAPI.FLAG_TPCOMMAND, true);
	}
}
