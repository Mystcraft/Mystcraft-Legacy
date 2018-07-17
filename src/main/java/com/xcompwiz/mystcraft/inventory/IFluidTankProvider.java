package com.xcompwiz.mystcraft.inventory;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;

public interface IFluidTankProvider {

	@Nullable
	public FluidStack getFluid();

	public int getMax();

	void setFluid(FluidStack fluid);
}
