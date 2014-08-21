package com.xcompwiz.mystcraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotBanned extends Slot {

	public SlotBanned(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
	}

	@Override
	public boolean canTakeStack(EntityPlayer p_82869_1_) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_111238_b() {
		return false;
	}
}
