package com.xcompwiz.mystcraft.imc;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCBlacklistFluid implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		ItemStack itemstack = null;
		if (message.isStringMessage()) {
			LoggerUtils.info(String.format("Receiving fluid symbol blacklist request from [%s] for fluid %s", message.getSender(), message.getStringValue()));
			String fluidname = message.getStringValue();
			Fluid fluid = null;
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
		String identifier = "ModMat_" + itemstack.getUnlocalizedName();
		SymbolManager.blackListSymbol(identifier);
		LoggerUtils.info("Fluid blacklist request successful.");
	}

}
