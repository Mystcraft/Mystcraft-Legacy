package com.xcompwiz.mystcraft.command;

import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.util.CollectionUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandCreateDim extends CommandBaseAdv {

	@Override
	public String getName() {
		return "myst-create";
	}

	@Override
	public String getUsage(ICommandSender par1ICommandSender) {
		return "/" + this.getName() + " dimId";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender agent, String[] args) throws CommandException {
		String sTarget = null;

		if (args.length > 0) {
			sTarget = args[0];
		} else {
			throw new WrongUsageException("Could not parse command.");
		}

		int dimId = parseInt(sTarget);
		try {
			AgeData data = DimensionUtils.createAge(dimId);
			data.setPages(CollectionUtils.buildList(Page.createLinkPage())); //TODO: (Command) Pages could be added at this point, allowing the command to create an age with specific symbols 
		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
		sendToAdmins(agent, "Dimension " + dimId + " created as Mystcraft Age", new Object[0]);
	}
}
