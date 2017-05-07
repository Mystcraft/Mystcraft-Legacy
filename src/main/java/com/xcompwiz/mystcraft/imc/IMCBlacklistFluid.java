package com.xcompwiz.mystcraft.imc;

import com.xcompwiz.mystcraft.data.ModSymbolsFluids;
import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCBlacklistFluid implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		Fluid fluid = null;
		ItemStack itemstack = null;
		if (message.isStringMessage()) {
			LoggerUtils.info(String.format("Receiving fluid symbol blacklist request from [%s] for fluid %s", message.getSender(), message.getStringValue()));
			String fluidname = message.getStringValue();
			if (fluidname != null) {
				fluid = FluidRegistry.getFluid(fluidname);
			}
			if (fluid == null) return;
			if (fluid.getBlock() == null) return;
			Block block = fluid.getBlock();
			byte meta = 0;
			if (block instanceof BlockFluidBase) meta = (byte) ((BlockFluidBase) block).getMaxRenderHeightMeta();
			//TODO: Share code with ModifierBlock
			itemstack = new ItemStack(block, 1, meta);
			if (itemstack.getItem() == null) { throw new RuntimeException("Invalid item form for block " + block.getUnlocalizedName() + "with metadata " + meta); }
		}
		if (message.isItemStackMessage()) {
			itemstack = message.getItemStackValue();
			LoggerUtils.info(String.format("Receiving fluid symbol blacklist request from [%s] for fluid %s", message.getSender(), message.getItemStackValue().getUnlocalizedName()));
		}
		if (itemstack == null) return;
		String identifier = "ModMat_" + itemstack.getUnlocalizedName(); //XXX: Building ModMat identifier externally...
		SymbolManager.blackListSymbol(identifier);
		ModSymbolsFluids.blacklist(fluid);
		LoggerUtils.info("Fluid blacklist request successful.");
	}

}
