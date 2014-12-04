package com.xcompwiz.mystcraft.fluids;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public final class FluidUtils {

	public static ItemStack emptyContainer(ItemStack container) {
		if (container == null) return null;
		if (container.stackSize > 1) {
			container.splitStack(1);
		}
		if (container.getItem().hasContainerItem(container)) { return container.getItem().getContainerItem(container); }
		return null;
	}

	public static ItemStack drainTankIntoContainer(IFluidTank tank, ItemStack containerStack) {
		FluidStack tankFluid = tank.getFluid();
		if (tankFluid != null) {
			ItemStack container = containerStack.copy();
			container.stackSize = 1;
			container = FluidContainerRegistry.fillFluidContainer(tankFluid, container);
			if (container != null) {
				--containerStack.stackSize;
				tank.drain(FluidContainerRegistry.getFluidForFilledItem(container).amount, true);
				return container;
			}
		}
		return null;
	}

	public static ItemStack fillTankWithContainer(IFluidTank tank, ItemStack containerStack) {
		ItemStack container = containerStack.copy();
		container.stackSize = 1;
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(container);
		if (fluid != null) {
			int used = tank.fill(fluid, false);
			if (used == fluid.amount) {
				--containerStack.stackSize;
				tank.fill(fluid, true);
				container = emptyContainer(container);
				return container;
			}
		}
		return null;
	}

}
