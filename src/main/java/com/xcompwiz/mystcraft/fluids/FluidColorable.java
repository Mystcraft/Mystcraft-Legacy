package com.xcompwiz.mystcraft.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidColorable extends Fluid {

	private int	color;

	public FluidColorable(String fluidName, ResourceLocation resFluidStill, ResourceLocation resFluidFlow, int color) {
		super(fluidName, resFluidStill, resFluidFlow);
		this.color = color;
	}

	@Override
	public int getColor() {
		return color;
	}
}
