package com.xcompwiz.mystcraft.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

public class CommandCreateAgebook extends CommandBaseAdv {

	@Override
	public String getCommandName() {
		return "myst-agebook";
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.agebook.usage";
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {
		EntityPlayer player = null;
		try {
			player = getCommandSenderAsPlayer(agent);
		} catch (Exception e) {
			throw new PlayerNotFoundException("Only players may perform this action.", new Object[0]);
		}

		Integer dimId = null;
		if (args.length != 0) {
			dimId = parseInt(agent, args[0]);
		} else {
			dimId = player.dimension;
		}

		if (!Mystcraft.registeredDims.contains(dimId) || AgeData.getAge(dimId, false) == null) { throw new CommandException("Cannot create Descriptive Books for non-Mystcraft Dimensions", new Object[0]); }
		ItemStack itemstack = new ItemStack(ModItems.agebook);
		ItemAgebook.initializeCompound(itemstack, dimId, AgeData.getAge(dimId, false));
		if (player.inventory.addItemStackToInventory(itemstack)) {
			player.inventory.markDirty();
			sendToAdmins(agent, agent.getCommandSenderName() + " created Descriptive Book for Dimension " + dimId, new Object[0]);
		}
	}
}
