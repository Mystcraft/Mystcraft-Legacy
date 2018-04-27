package com.xcompwiz.mystcraft.imc;

import com.xcompwiz.mystcraft.data.ModSymbolsFluids;
import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBlock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCBlacklistFluid implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		Fluid fluid = null;
		IBlockState blockstate = null;
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
			if (block instanceof BlockFluidBase) {
				meta = (byte) ((BlockFluidBase) block).getMaxRenderHeightMeta();
			}
			blockstate = block.getStateFromMeta(meta);
		}
//		if (message.isItemStackMessage()) {
//			ItemStack itemstack = message.getItemStackValue();
//			LoggerUtils.info(String.format("Receiving fluid symbol blacklist request from [%s] for fluid %s", message.getSender(), message.getItemStackValue().getUnlocalizedName()));
//			blockstate = ; //TODO:
//		}
		if (blockstate == null) {
			return;
		}
		ResourceLocation identifier = SymbolBlock.getSymbolIdentifier(blockstate);
		SymbolManager.blackListSymbol(identifier);
		ModSymbolsFluids.blacklist(fluid);
		LoggerUtils.info("Fluid blacklist request successful.");
	}

}
