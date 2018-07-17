package com.xcompwiz.mystcraft.inventory;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

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
	@Nullable
	public void setFluid(FluidStack fluid) {
		provided.setFluid(fluid);
	}

	@Override
	public int getMax() {
		return this.provided.getCapacity();
	}

}
