package com.xcompwiz.mystcraft.inventory;

import net.minecraftforge.fluids.FluidStack;

public class FluidTankProvider implements IFluidTankProvider {

	private int			maxamount;
	private FluidStack	fluidstack;

	public void setFluid(FluidStack fluidstack) {
		this.fluidstack = fluidstack;
	}

	public void setMax(int max) {
		this.maxamount = max;
	}

	@Override
	public FluidStack getFluid() {
		return fluidstack;
	}

	@Override
	public int getMax() {
		return this.maxamount;
	}

}
