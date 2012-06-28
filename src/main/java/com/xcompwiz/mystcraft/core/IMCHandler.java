package com.xcompwiz.mystcraft.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.ImmutableList;
import com.xcompwiz.mystcraft.data.SymbolDataFluids;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCHandler {
	/*
	 * fluidsymbol - NBT Message Must specify the fluid either by name or id; required String fluidname Integer fluidid
	 * Float rarity - optional; sets the item treasure rarity (1 is common, 0 is impossible); default 0.1F Float
	 * grammarweight - optional; sets the weight the grammar will use when selecting tokens (1 is common, 0 is
	 * impossible); default 0.1F Instability Function - optional; default linear at 1 to 10 Currently, only a linear
	 * formula is supported. To make a more complex instability formula, use the actual Mystcraft API Float
	 * instabilityPerBlock - multiplied by the number of blocks added to each chunk on average. The result is added to
	 * the world once.
	 */

	public static void process(ImmutableList<IMCMessage> messages) {
		for (IMCMessage message : messages) {
			if (message.key.equals("fluidsymbol")) {
				if (!message.isNBTMessage()) continue;
				NBTTagCompound data = message.getNBTValue();
				Fluid fluid = null;
				if (data.hasKey("fluidname")) {
					fluid = FluidRegistry.getFluid(data.getString("fluidname"));
				}
				if (data.hasKey("fluidid")) {
					fluid = FluidRegistry.getFluid(data.getInteger("fluidid"));
				}
				if (fluid == null) continue;
				if (data.hasKey("rarity")) SymbolDataFluids.setRarity(fluid, data.getFloat("rarity"));
				if (data.hasKey("grammarweight")) SymbolDataFluids.setGrammarWeight(fluid, data.getFloat("grammarweight"));
				//if (data.hasKey("instabilityPerBlock")) factor2 = data.getFloat("instabilityPerBlock");
				//FIXME: Instability factors for fluid blocks!
			}
		}
	}

}
