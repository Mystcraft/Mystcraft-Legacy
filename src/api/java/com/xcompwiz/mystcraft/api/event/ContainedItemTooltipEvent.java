package com.xcompwiz.mystcraft.api.event;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ContainedItemTooltipEvent extends ItemTooltipEvent {

	private final ItemStack container;

	public ContainedItemTooltipEvent(ItemStack itemStack, ItemStack container, EntityPlayer entityPlayer, List<String> toolTip, ITooltipFlag flags) {
		super(itemStack, entityPlayer, toolTip, flags);
		this.container = container;
	}

	/**
	 * The {@link ItemStack} that contains the itemStack.
	 */
	@Nonnull
	public ItemStack getContainer() {
		return container;
	}
}
