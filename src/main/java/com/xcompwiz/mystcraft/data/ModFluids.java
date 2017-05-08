package com.xcompwiz.mystcraft.data;

import java.util.HashSet;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.MystObjects.Fluids;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.fluids.FluidColorable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModFluids {

	public static Fluid	black_ink;

	public static void loadConfigs(MystConfig config) {}

	public static void init() {
		Mystcraft.validInks = new HashSet<String>();

		black_ink = new FluidColorable(Fluids.black_ink,
				new ResourceLocation("mystcraft:fluid_flow"),
				new ResourceLocation("mystcraft:fluid"), 0x191919);
		FluidRegistry.registerFluid(black_ink);
		black_ink.setBlock(ModBlocks.black_ink); //Hellfire> that's why blocks need to be initialized first.
		Mystcraft.validInks.add(black_ink.getName());

		FluidRegistry.addBucketForFluid(black_ink);
		//UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, black_ink);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {

	}

	//public static void initIcons(IIconRegister register) {
	//	black_ink.setFlowingIcon(register.registerIcon("mystcraft:fluid_flow"));
	//	black_ink.setStillIcon(register.registerIcon("mystcraft:fluid"));
	//}

}
