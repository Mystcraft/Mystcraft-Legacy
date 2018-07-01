package com.xcompwiz.mystcraft.inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;

public class FluidTankProvider implements IFluidTankProvider {

	private FluidTank provided;

	public void setTank(FluidTank tank) {
		this.provided = tank;
	}

	@Override
	@Nullable
	public FluidStack getFluid() {
		return provided.getFluid();
	}

	@Override
	public int getMax() {
		return this.provided.getCapacity();
	}

}
