package com.xcompwiz.mystcraft.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;

public class CommandTPX extends CommandMyst {

	@Override
	public String getCommandName() {
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
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.tpx.usage";
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args) {
		if (args.length == 1) return getListOfStringsMatchingLastWord(args, this.getPlayers());
		if (args.length == 2) return getListOfStringsMatchingLastWord(args, this.getPlayers());
		return null;
	}

	protected String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	@Override
	public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
		return par2 == 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Entity subject = null;
		ILinkInfo link = null;
		try {
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
				subject = getTargetPlayer(sender, sSubject);
			}
			if (subject == null) { throw new WrongUsageException("commands.myst.tpx.fail.nosubject"); }

			link = getLinkInfoForTarget(sender, subject, sTarget, sX, sY, sZ);

			link.setFlag(ILinkPropertyAPI.FLAG_INTRA_LINKING, true);
			makeOpTP(link);
			LinkController.travelEntity(subject.worldObj, subject, link);

		} catch (CommandException e) {
			sender.addChatMessage(new ChatComponentText(e.getMessage()));
			sender.addChatMessage(new ChatComponentTranslation(getCommandUsage(sender)));
		}
	}

	public static ILinkInfo getLinkInfoForTarget(ICommandSender sender, Entity subject, String sTarget, String sX, String sY, String sZ) {
		ILinkInfo link = null;
		try {
			Entity target = getTargetPlayer(sender, sTarget);
			link = InternalAPI.linking.createLinkInfoFromPosition(target.worldObj, target);
		} catch (PlayerNotFoundException e) {
		}
		if (link == null) {
			link = new LinkOptions(null);
			int dim = (int) (handleRelativeNumber(sender, subject.dimension, sTarget, 0, 0) - 0.5D);
			if (MinecraftServer.getServer().worldServerForDimension(dim) == null) { throw new CommandException("commands.myst.tpx.fail.noworld", new Object[] { dim }); }
			link.setDimensionUID(dim);
			if (sX != null && sY != null && sZ != null) {
				int x = (int) handleRelativeNumber(sender, subject.posX, sX);
				int y = (int) handleRelativeNumber(sender, subject.posY, sY, 0, 0);
				int z = (int) handleRelativeNumber(sender, subject.posZ, sZ);
				link.setSpawn(new ChunkCoordinates(x, y, z));
			}
		}
		return link;
	}

	public static boolean isOpTP(ILinkInfo link) {
		return link.getFlag("Op-TP");
	}

	private void makeOpTP(ILinkInfo link) {
		link.setFlag(ILinkPropertyAPI.FLAG_TPCOMMAND, true);
	}
}
