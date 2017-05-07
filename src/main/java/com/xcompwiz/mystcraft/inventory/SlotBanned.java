package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotBanned extends Slot {

	public SlotBanned(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
	}

	@Override
	public boolean canTakeStack(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_111238_b() {
		return false;
	}
}
