package com.xcompwiz.mystcraft.tileentity;

import java.util.Set;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidTankFiltered extends FluidTank {

	private Set<String>	allowed;

	public FluidTankFiltered(int capacity) {
		super(capacity);
	}

	public FluidTankFiltered(FluidStack fluid, int capacity) {
		super(fluid, capacity);
	}

	public FluidTankFiltered(Fluid fluid, int amount, int capacity) {
		super(fluid, amount, capacity);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (resource == null) return 0;
		if (isFluidPermitted(resource.getFluid())) { return super.fill(resource, doFill); }
		return 0;
	}

	public void setPermittedFluids(Set<String> allowed) {
		this.allowed = allowed;
	}

	public boolean isFluidPermitted(Fluid fluid) {
		if (allowed == null) return true;
		if (allowed.contains(fluid.getName())) return true;
		return false;
	}

}
