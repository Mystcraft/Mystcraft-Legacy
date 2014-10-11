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

	public static class FluidData {
		public boolean	seabanned	= false;
		public float	grammar		= 0.1F;
		public float	rarity		= 0.1F;
		public float	factor1		= 0.25F;
		public float	factor2		= 0F;

		public FluidData setBannedSea(boolean b) {
			this.seabanned = b;
			return this;
		}

		public FluidData setGrammarW(float v) {
			this.grammar = v;
			return this;
		}

		public FluidData setRarity(float v) {
			this.rarity = v;
			return this;
		}

		public FluidData setFactor1(float v) {
			this.factor1 = v;
			return this;
		}

		public FluidData setFactor2(float v) {
			this.factor2 = v;
			return this;
		}
	}

	private static Map<String, FluidData>	defaults			= new HashMap<String, SymbolDataFluids.FluidData>();
	private static FluidData				defaultfluidvals	= new FluidData();

	private static Map<Integer, Boolean>	allowedsea			= new HashMap<Integer, Boolean>();
	private static Map<Integer, Float>		rarities			= new HashMap<Integer, Float>();
	private static Map<Integer, Float>		grammarWeights		= new HashMap<Integer, Float>();
	private static Map<Integer, Float>		factor1s			= new HashMap<Integer, Float>();
	private static Map<Integer, Float>		factor2s			= new HashMap<Integer, Float>();
	private static MystConfig				config;

	public static void setConfig(MystConfig mystconfig) {
		config = mystconfig;
	}

	public static FluidData getDefault(String fluidname) {
		FluidData data = defaults.get(fluidname);
		if (data == null) {
			data = new FluidData();
			defaults.put(fluidname, data);
		}
		return data;
	}

	public static void init() {
		getDefault("fluid.mobessence").setBannedSea(false).setFactor1(3.5F).setFactor2(1.75F);
		getDefault("fluid.ender").setBannedSea(true).setFactor1(3F).setFactor2(1.5F);
		getDefault("fluid.manyullyn.molten").setBannedSea(true).setFactor1(3F).setFactor2(1.5F);
		getDefault("fluid.redstone").setBannedSea(true).setFactor1(3F).setFactor2(1.5F);
		getDefault("fluid.fluiduumatter").setBannedSea(true).setFactor1(2F).setFactor2(1F);
		getDefault("fluid.life_essence").setBannedSea(false).setFactor1(2F).setFactor2(1F);
		getDefault("fluid.alumite.molten").setBannedSea(true).setFactor1(1.5F).setFactor2(0.75F);
		getDefault("fluid.ardite.molten").setBannedSea(true).setFactor1(1.5F).setFactor2(0.75F);
		getDefault("fluid.cobalt.molten").setBannedSea(true).setFactor1(1.5F).setFactor2(0.75F);
		getDefault("fluid.fluid.purity").setBannedSea(false).setFactor1(1.5F).setFactor2(0.75F);
		getDefault("fluid.aluminum.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.biofuel").setBannedSea(false).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.bronze.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.chocolatemilk").setBannedSea(false).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.copper.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.creosote").setBannedSea(false).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.electrum.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.emerald.liquid").setBannedSea(false).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.fluidbiogas").setBannedSea(false).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.fluidbiomass").setBannedSea(false).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.fuel").setBannedSea(false).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.gold.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.invar.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.iron.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.lead.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.nickel.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.obsidian.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.platinum.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.silver.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.steel.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.tin.molten").setBannedSea(true).setFactor1(1F).setFactor2(0.5F);
		getDefault("fluid.aluminumbrass.molten").setBannedSea(true).setFactor1(0.5F).setFactor2(0.25F);
		getDefault("fluid.coal").setBannedSea(false).setFactor1(0.5F).setFactor2(0F);
		getDefault("fluid.glass.molten").setBannedSea(false).setFactor1(0.5F).setFactor2(0F);
		getDefault("fluid.glowstone").setBannedSea(false).setFactor1(0.5F).setFactor2(0F);
		getDefault("fluid.honey").setBannedSea(false).setFactor1(0.5F).setFactor2(0.25F);
		getDefault("fluid.oil").setBannedSea(false).setFactor1(0.5F).setFactor2(0F);
		getDefault("fluid.spring_water").setBannedSea(false).setFactor1(0.5F).setFactor2(0.25F);
		getDefault("fluid.witchery:fluidspirit").setBannedSea(false).setFactor1(0.5F).setFactor2(0.25F);
		getDefault("fluid.meat").setBannedSea(false).setFactor1(0.25F).setFactor2(0F);
		getDefault("fluid.myst.ink.black").setBannedSea(false).setFactor1(0.25F).setFactor2(0F);
		getDefault("fluid.blood").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.fluidconstructionfoam").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.fluidcoolant").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.fluiddistilledwater").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.fluidpahoehoelava").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.fluidsteam").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.fluidsuperheatedsteam").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.glue").setBannedSea(true).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.milk").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.mushroomsoup").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.steam").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.stone.seared").setBannedSea(false).setFactor1(0F).setFactor2(0F);
		getDefault("fluid.slime.blue").setBannedSea(false).setFactor1(-0.25F).setFactor2(0F);
		getDefault("fluid.cryotheum").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.25F);
		getDefault("fluid.etchacid").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.25F);
		getDefault("fluid.fluid.death").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.25F);
		getDefault("fluid.fluidhotcoolant").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.25F);
		getDefault("fluid.poison").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.25F);
		getDefault("fluid.sewage").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.25F);
		getDefault("fluid.sludge").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.25F);
		getDefault("fluid.witchery:hollowtears").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.25F);
		getDefault("fluid.mana").setBannedSea(false).setFactor1(-1F).setFactor2(-0.5F);
		getDefault("fluid.pyrotheum").setBannedSea(false).setFactor1(-1F).setFactor2(-0.5F);
		getDefault("fluid.fluxgoo").setBannedSea(false).setFactor1(-1.5F).setFactor2(-1F);
	}

	public static void modsLoaded() {
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
				if (!isBannedSea(fluid)) container.add(BlockCategory.SEA, grammarWeight(fluid));
				container.add(BlockCategory.FLUID, grammarWeight(fluid));
			}
		}
		if (config != null && config.hasChanged()) config.save();
	}

	private static FluidData getDefault(Fluid fluid) {
		FluidData data = defaults.get(fluid.getUnlocalizedName().toLowerCase().replace(' ', '_'));
		if (data != null) { return data; }
		return defaultfluidvals;
	}

	private static boolean isBannedSea(Fluid fluid) {
		Boolean value = allowedsea.get(fluid.getID());
		if (value != null) return value;
		boolean val = getDefault(fluid).seabanned;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_BALANCE, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".seabanned", val);
		return val;
	}

	private static float symbolRarity(Fluid fluid) {
		Float value = rarities.get(fluid.getID());
		if (value != null) return value;
		float val = getDefault(fluid).rarity;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_BALANCE, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".rarity", val);
		return val;
	}

	private static float grammarWeight(Fluid fluid) {
		Float value = grammarWeights.get(fluid.getID());
		if (value != null) return value;
		float val = getDefault(fluid).grammar;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_BALANCE, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".grammar", val);
		return val;
	}

	private static float factor1(Fluid fluid) {
		Float value = factor1s.get(fluid.getID());
		if (value != null) return value;
		float val = getDefault(fluid).factor1;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_BALANCE, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".instability.factor_accessibility", val);
		return val;
	}

	private static float factor2(Fluid fluid) {
		Float value = factor2s.get(fluid.getID());
		if (value != null) return value;
		float val = getDefault(fluid).factor2;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_BALANCE, fluid.getUnlocalizedName().toLowerCase().replace(' ', '_') + ".instability.factor_flat", val);
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
