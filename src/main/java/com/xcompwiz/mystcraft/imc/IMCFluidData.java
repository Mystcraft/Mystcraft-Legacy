package com.xcompwiz.mystcraft.imc;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.xcompwiz.mystcraft.data.SymbolDataFluids;
import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCFluidData implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		if (!message.isNBTMessage()) return;
		NBTTagCompound data = message.getNBTValue();
		Fluid fluid = null;
		if (data.hasKey("fluidname")) {
			fluid = FluidRegistry.getFluid(data.getString("fluidname"));
		}
		if (data.hasKey("fluidid")) {
			fluid = FluidRegistry.getFluid(data.getInteger("fluidid"));
		}
		if (fluid == null) return;
		if (data.hasKey("seabanned")) SymbolDataFluids.setSeaBanned(fluid, data.getBoolean("seabanned"));
		if (data.hasKey("rarity")) SymbolDataFluids.setRarity(fluid, data.getFloat("rarity"));
		if (data.hasKey("grammar")) SymbolDataFluids.setGrammarWeight(fluid, data.getFloat("grammar"));
		if (data.hasKey("factor1")) SymbolDataFluids.setFactor1(fluid, data.getFloat("factor1"));
		if (data.hasKey("factor2")) SymbolDataFluids.setFactor2(fluid, data.getFloat("factor2"));
	}

}
