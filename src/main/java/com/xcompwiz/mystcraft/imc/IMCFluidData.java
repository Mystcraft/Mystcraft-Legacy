package com.xcompwiz.mystcraft.imc;

import com.xcompwiz.mystcraft.data.ModSymbolsFluids;
import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCFluidData implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		if (!message.isNBTMessage())
			return;
		NBTTagCompound data = message.getNBTValue();
		Fluid fluid = null;
		if (data.hasKey("fluidname")) {
			fluid = FluidRegistry.getFluid(data.getString("fluidname"));
		}
		if (fluid == null)
			return;
		if (data.hasKey("seabanned"))
			ModSymbolsFluids.setSeaBanned(fluid, data.getBoolean("seabanned"));
		if (data.hasKey("cardrank"))
			ModSymbolsFluids.setCardRank(fluid, data.getInteger("cardrank"));
		if (data.hasKey("grammarrank"))
			ModSymbolsFluids.setGrammarRank(fluid, data.getInteger("grammarrank"));
		if (data.hasKey("factor1"))
			ModSymbolsFluids.setFactor1(fluid, data.getFloat("factor1"));
		if (data.hasKey("factor2"))
			ModSymbolsFluids.setFactor2(fluid, data.getFloat("factor2"));
	}

}
