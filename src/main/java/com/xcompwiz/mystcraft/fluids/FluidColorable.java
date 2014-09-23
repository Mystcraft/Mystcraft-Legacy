package com.xcompwiz.mystcraft.fluids;

import net.minecraftforge.fluids.Fluid;

public class FluidColorable extends Fluid {

	private int	color;

	public FluidColorable(String fluidName, int color) {
		super(fluidName);
		this.color = color;
	}

	@Override
	public int getColor() {
		return color;
	}
}
