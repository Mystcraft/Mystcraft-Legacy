package com.xcompwiz.mystcraft.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.data.SymbolDataModifiers.BlockModifierContainerObject;
import com.xcompwiz.mystcraft.world.ChunkProfiler;

public class SymbolDataFluids {

	private static Map<Integer, Float>	rarities		= new HashMap<Integer, Float>();
	private static Map<Integer, Float>	grammarWeights	= new HashMap<Integer, Float>();
	private static Map<Integer, Float>	factor1s		= new HashMap<Integer, Float>();
	private static Map<Integer, Float>	factor2s		= new HashMap<Integer, Float>();
	private static MystConfig			config;

	public static void setConfig(MystConfig mystconfig) {
		config = mystconfig;
	}

	public static void initialize() {
		Map<String, Fluid> map = FluidRegistry.getRegisteredFluids();
		for (Entry<String, Fluid> entry : map.entrySet()) {
			Fluid fluid = entry.getValue();
			Block block = fluid.getBlock();
			if (block == Blocks.water) continue;
			if (block == Blocks.lava) continue;
			if (block == null) continue;
			if (Item.getItemFromBlock(block) == null) continue;

			byte meta = 0;
			if (block instanceof BlockFluidBase) meta = (byte) ((BlockFluidBase) block).getMaxRenderHeightMeta();
			BlockModifierContainerObject container = BlockModifierContainerObject.create(WordData.Sea, symbolRarity(fluid), block, meta);
			ChunkProfiler.setInstabilityFactors(block, factor1(fluid), factor2(fluid), 0);
			if (fluid.isGaseous()) {
				container.add(BlockCategory.GAS, grammarWeight(fluid));
			} else {
				container.add(BlockCategory.SEA, grammarWeight(fluid));
				container.add(BlockCategory.FLUID, grammarWeight(fluid));
			}
		}
		if (config != null && config.hasChanged()) config.save();
	}

	private static float symbolRarity(Fluid fluid) {
		Float value = rarities.get(fluid.getID());
		if (value != null) return value;
		float val = 0.1F;
		if (config != null) return (float) config.get(MystConfig.CATEGORY_DEBUG, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".rarity", val).getDouble(val);
		return val;
	}

	private static float grammarWeight(Fluid fluid) {
		Float value = grammarWeights.get(fluid.getID());
		if (value != null) return value;
		float val = 0.1F;
		if (config != null) return (float) config.get(MystConfig.CATEGORY_DEBUG, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".grammar", val).getDouble(val);
		return val;
	}

	private static float factor1(Fluid fluid) {
		Float value = factor1s.get(fluid.getID());
		if (value != null) return value;
		float val = 0.0F;
		if (config != null) return (float) config.get(MystConfig.CATEGORY_DEBUG, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".instability.factor1", val).getDouble(val);
		return val;
	}

	private static float factor2(Fluid fluid) {
		Float value = factor2s.get(fluid.getID());
		if (value != null) return value;
		float val = 0.1F;
		if (config != null) return (float) config.get(MystConfig.CATEGORY_DEBUG, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".instability.factor2", val).getDouble(val);
		return val;
	}

	public static void setRarity(Fluid fluid, float value) {
		rarities.put(fluid.getID(), value);
	}

	public static void setGrammarWeight(Fluid fluid, float value) {
		grammarWeights.put(fluid.getID(), value);
	}

	public static void setFactor1(Fluid fluid, float value) {
		factor1s.put(fluid.getID(), value);
	}

	public static void setFactor2(Fluid fluid, float value) {
		factor2s.put(fluid.getID(), value);
	}
}
