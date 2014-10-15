package com.xcompwiz.mystcraft.core;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.ImmutableList;
import com.xcompwiz.mystcraft.api.MystAPI;
import com.xcompwiz.mystcraft.data.SymbolDataFluids;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCHandler {
	public static void process(ImmutableList<IMCMessage> messages) {
		for (IMCMessage message : messages) {
			//String key = message.key.toLowerCase();
			//IMCProcess process = processes.get(key);
			//try{process.message(message);}catch
			try {
				//FIXME: There's getting to be a few of these.  Registration system needed.
				if (message.key.equalsIgnoreCase("blacklistfluid")) {
					ItemStack itemstack = null;
					if (message.isStringMessage()) {
						LoggerUtils.info(String.format("Receiving fluid symbol blacklist request from [%s] for fluid %s", message.getSender(), message.getStringValue()));
						String fluidname = message.getStringValue();
						Fluid fluid = null;
						if (fluidname != null) {
							fluid = FluidRegistry.getFluid(fluidname);
						}
						if (fluid == null) continue;
						if (fluid.getBlock() == null) continue;
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
					if (itemstack == null) continue;
					String identifier = "ModMat_" + itemstack.getUnlocalizedName();
					SymbolManager.blackListSymbol(identifier);
					LoggerUtils.info("Fluid blacklist request successful.");
				}
				if (message.key.equalsIgnoreCase("blacklist")) {
					if (!message.isStringMessage()) continue;
					String identifier = message.getStringValue();
					SymbolManager.blackListSymbol(identifier);
					LoggerUtils.info(String.format("Symbol blacklist request from [%s] successful on identifier %s", message.getSender(), identifier));
				}
				if (message.key.equalsIgnoreCase("fluidsymboldata")) {
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
					if (data.hasKey("seabanned")) SymbolDataFluids.setSeaBanned(fluid, data.getBoolean("seabanned"));
					if (data.hasKey("rarity")) SymbolDataFluids.setRarity(fluid, data.getFloat("rarity"));
					if (data.hasKey("grammar")) SymbolDataFluids.setGrammarWeight(fluid, data.getFloat("grammar"));
					if (data.hasKey("factor1")) SymbolDataFluids.setFactor1(fluid, data.getFloat("factor1"));
					if (data.hasKey("factor2")) SymbolDataFluids.setFactor2(fluid, data.getFloat("factor2"));
				}
				if (message.key.equalsIgnoreCase("register")) {
					if (!message.isStringMessage()) continue;
					LoggerUtils.info(String.format("Receiving registration request from [%s] for method %s", message.getSender(), message.getStringValue()));
					callbackRegistration(message.getStringValue(), message.getSender());
				}
			} catch (Exception e) {
				LoggerUtils.error(e.toString());
			}
		}
	}

	/**
	 * This is a cool bit of code lifted from WAILA
	 * @author: ProfMobius, Edited by XCompWiz
	 * @param method The method (prefixed by classname) to call
	 * @param modname The name of the mod which made the request
	 */
	public static void callbackRegistration(String method, String modname) {
		String[] splitName = method.split("\\.");
		String methodName = splitName[splitName.length - 1];
		String className = method.substring(0, method.length() - methodName.length() - 1);

		LoggerUtils.info(String.format("Trying to call (reflection) %s %s", className, methodName));

		try {
			Class reflectClass = Class.forName(className);
			Method reflectMethod = reflectClass.getDeclaredMethod(methodName, MystAPI.class);
			reflectMethod.invoke(null, InternalAPI.getAPIInstance(modname));

			LoggerUtils.info(String.format("Success in registering %s", modname));

		} catch (ClassNotFoundException e) {
			LoggerUtils.warn(String.format("Could not find class %s", className));
		} catch (NoSuchMethodException e) {
			LoggerUtils.warn(String.format("Could not find method %s", methodName));
		} catch (Exception e) {
			LoggerUtils.warn(String.format("Exception while trying to access the method : %s", e.toString()));
		}
	}
}
