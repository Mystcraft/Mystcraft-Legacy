package com.xcompwiz.mystcraft.inventory;

import net.minecraftforge.fluids.FluidStack;

public interface IFluidTankProvider {

	public FluidStack getFluid();

	public int getMax();
}
