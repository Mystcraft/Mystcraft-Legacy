package com.xcompwiz.mystcraft.inventory;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public interface IFluidTankProvider {

	@Nullable
	public FluidStack getFluid();

	public int getMax();
}
