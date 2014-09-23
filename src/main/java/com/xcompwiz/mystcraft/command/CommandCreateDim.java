package com.xcompwiz.mystcraft.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.util.CollectionUtils;

public class CommandCreateDim extends CommandBaseAdv {

	@Override
	public String getCommandName() {
		return "myst-create";
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "/" + this.getCommandName() + " dimId";
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {
		String sTarget = null;

		if (args.length > 0) {
			sTarget = args[0];
		} else {
			throw new WrongUsageException("Could not parse command.");
		}

		int dimId = parseInt(agent, sTarget);
		try {
			AgeData data = DimensionUtils.createAge(dimId);
			data.setPages(CollectionUtils.buildList(Page.createLinkPage())); //TODO: (Command) Pages could be added at this point, allowing the command to create an age with specific symbols 
		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
		sendToAdmins(agent, "Dimension " + dimId + " created as Mystcraft Age", new Object[0]);
	}
}
