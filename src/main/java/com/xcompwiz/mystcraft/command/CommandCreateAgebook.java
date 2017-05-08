package com.xcompwiz.mystcraft.command;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandCreateAgebook extends CommandBaseAdv {

	@Override
	public String getName() {
		return "myst-agebook";
	}

	@Override
	public String getUsage(ICommandSender par1ICommandSender) {
		return "commands.myst.agebook.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender agent, String[] args) throws CommandException {
		EntityPlayer player = null;
		try {
			player = getCommandSenderAsPlayer(agent);
		} catch (Exception e) {
			throw new PlayerNotFoundException("Only players may perform this action.", new Object[0]);
		}

		Integer dimId = null;
		if (args.length != 0) {
			dimId = parseInt(args[0]);
		} else {
			dimId = player.dimension;
		}

		if (!Mystcraft.registeredDims.contains(dimId) || AgeData.getAge(dimId, false) == null) { throw new CommandException("Cannot create Descriptive Books for non-Mystcraft Dimensions", new Object[0]); }
		ItemStack itemstack = new ItemStack(ModItems.agebook);
		ItemAgebook.initializeCompound(itemstack, dimId, AgeData.getAge(dimId, false));
		if (player.inventory.addItemStackToInventory(itemstack)) {
			player.inventory.markDirty();
			sendToAdmins(agent, agent.getName() + " created Descriptive Book for Dimension " + dimId, new Object[0]);
		}
	}
}
