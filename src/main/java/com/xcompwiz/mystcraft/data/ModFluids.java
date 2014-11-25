package com.xcompwiz.mystcraft.data;

import java.util.HashSet;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.MystObjects.Fluids;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.fluids.FluidColorable;

public class ModFluids {

	public static Fluid	black_ink;

	public static void loadConfigs(MystConfig config) {}

	public static void init() {
		Mystcraft.validInks = new HashSet<String>();

		black_ink = new FluidColorable(Fluids.black_ink, 0x191919);
		FluidRegistry.registerFluid(black_ink);
		Mystcraft.validInks.add(black_ink.getName());
	}

	public static void initIcons(IIconRegister register) {
		black_ink.setFlowingIcon(register.registerIcon("mystcraft:fluid_flow"));
		black_ink.setStillIcon(register.registerIcon("mystcraft:fluid"));
	}
}
