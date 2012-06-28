package com.xcompwiz.mystcraft.fluids;

import net.minecraftforge.fluids.Fluid;

public class FluidMyst extends Fluid {

	private int	color;

	public FluidMyst(String fluidName, int color) {
		super(fluidName);
		this.color = color;
	}

	@Override
	public int getColor() {

		return color;
	}
}
