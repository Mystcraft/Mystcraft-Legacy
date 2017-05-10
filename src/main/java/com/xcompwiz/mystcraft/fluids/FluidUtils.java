package com.xcompwiz.mystcraft.fluids;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nonnull;

public final class FluidUtils {

	@Nonnull
	public static ItemStack emptyContainer(@Nonnull ItemStack container) {
		if (container.isEmpty()) return ItemStack.EMPTY;
		if (container.getCount() > 1) {
			container.splitStack(1);
		}
		if (container.getItem().hasContainerItem(container)) {
			return container.getItem().getContainerItem(container);
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	public static ItemStack fillTankWithContainer(IFluidTank tank, ItemStack containerStack) {
		ItemStack container = containerStack.copy();
		container.setCount(1);
		FluidStack fluid = FluidUtil.getFluidContained(container);
		if (fluid != null) {
			int used = tank.fill(fluid, false);
			if (used == fluid.amount) {
				containerStack.shrink(1);
				tank.fill(fluid, true);
				container = emptyContainer(container);
				return container;
			}
		}
		return null;
	}

}
