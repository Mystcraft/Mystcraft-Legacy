package com.xcompwiz.mystcraft.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.data.ModSymbolsModifiers.BlockModifierContainerObject;
import com.xcompwiz.mystcraft.instability.InstabilityBlockManager;

public class ModSymbolsFluids {
	private static MystConfig	config;

	public static void setConfig(MystConfig mystconfig) {
		config = mystconfig;
	}

	public static void init() {
		loadDefaults();
	}

	public static void modsLoaded() {
		Map<String, Fluid> map = FluidRegistry.getRegisteredFluids();
		for (Entry<String, Fluid> entry : map.entrySet()) {
			Fluid fluid = entry.getValue();
			if (blacklist.contains(fluid)) continue;
			Block block = fluid.getBlock();
			if (block == Blocks.water) continue;
			if (block == Blocks.lava) continue;
			if (block == null) continue;
			if (Item.getItemFromBlock(block) == null) continue;

			byte meta = 0;
			if (block instanceof BlockFluidBase) meta = (byte) ((BlockFluidBase) block).getMaxRenderHeightMeta();
			String fluidkey = getFluidKey(fluid);
			BlockModifierContainerObject container = BlockModifierContainerObject.create(WordData.Sea, symbolCardRank(fluidkey), block, meta);
			InstabilityBlockManager.setInstabilityFactors(block, factor1(fluidkey), factor2(fluidkey));
			if (fluid.isGaseous()) {
				container.add(BlockCategory.GAS, grammarRank(fluidkey));
			} else {
				if (!isBannedSea(fluidkey)) container.add(BlockCategory.SEA, grammarRank(fluidkey));
				container.add(BlockCategory.FLUID, grammarRank(fluidkey));
			}
			if (container.getSymbol() != null) InternalAPI.symbol.registerSymbol(container.getSymbol(), MystObjects.MystcraftModId);
		}
		if (config != null && config.hasChanged()) config.save();
	}

	public static class FluidData {
		public boolean	seabanned	= false;
		public int		grammar		= 4;
		public int		cardrank	= 4;
		public float	factor1		= 1.00F;
		public float	factor2		= 0.25F;

		public FluidData setBannedSea(boolean b) {
			this.seabanned = b;
			return this;
		}

		public FluidData setGrammarRank(int v) {
			this.grammar = v;
			return this;
		}

		public FluidData setCardRank(int v) {
			this.cardrank = v;
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

	private static Collection<Fluid>		blacklist			= new HashSet<Fluid>();
	private static Map<String, FluidData>	defaults			= new HashMap<String, ModSymbolsFluids.FluidData>();
	private static FluidData				defaultfluidvals	= new FluidData();

	private static Map<String, Boolean>		bannedsea			= new HashMap<String, Boolean>();
	private static Map<String, Integer>		cardranks			= new HashMap<String, Integer>();
	private static Map<String, Integer>		grammarranks		= new HashMap<String, Integer>();
	private static Map<String, Float>		factor1s			= new HashMap<String, Float>();
	private static Map<String, Float>		factor2s			= new HashMap<String, Float>();

	public static void blacklist(Fluid fluid) {
		blacklist.add(fluid);
	}

	public static FluidData getDefault(String fluidname) {
		FluidData data = defaults.get(fluidname);
		if (data == null) {
			data = new FluidData();
			defaults.put(fluidname, data);
		}
		return data;
	}

	private static FluidData getDefaultValue(String fluidkey) {
		FluidData data = defaults.get(fluidkey);
		if (data != null) { return data; }
		return defaultfluidvals;
	}

	private static boolean isBannedSea(String fluidkey) {
		Boolean value = bannedsea.get(fluidkey);
		if (value != null) return value;
		boolean val = getDefaultValue(fluidkey).seabanned;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_FLUIDS, fluidkey + ".seabanned", val);
		return val;
	}

	private static Integer symbolCardRank(String fluidkey) {
		Integer value = cardranks.get(fluidkey);
		if (value != null) return value;
		int val = getDefaultValue(fluidkey).cardrank;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_FLUIDS, fluidkey + ".cardrank", val);
		return val;
	}

	private static Integer grammarRank(String fluidkey) {
		Integer value = grammarranks.get(fluidkey);
		if (value != null) return value;
		int val = getDefaultValue(fluidkey).grammar;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_FLUIDS, fluidkey + ".grammar", val);
		return val;
	}

	private static float factor1(String fluidkey) {
		Float value = factor1s.get(fluidkey);
		if (value != null) return value;
		float val = getDefaultValue(fluidkey).factor1;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_FLUIDS, fluidkey + ".instability.factor_accessibility", val);
		return val;
	}

	private static float factor2(String fluidkey) {
		Float value = factor2s.get(fluidkey);
		if (value != null) return value;
		float val = getDefaultValue(fluidkey).factor2;
		if (config != null) return config.getOptional(MystConfig.CATEGORY_FLUIDS, fluidkey + ".instability.factor_flat", val);
		return val;
	}

	private static String getFluidKey(Fluid fluid) {
		return fluid.getUnlocalizedName().toLowerCase().replace(' ', '_');
	}

	public static void setSeaBanned(Fluid fluid, boolean value) {
		bannedsea.put(getFluidKey(fluid), value);
	}

	public static void setCardRank(Fluid fluid, Integer value) {
		cardranks.put(getFluidKey(fluid), value);
	}

	public static void setGrammarRank(Fluid fluid, Integer value) {
		grammarranks.put(getFluidKey(fluid), value);
	}

	public static void setFactor1(Fluid fluid, float value) {
		factor1s.put(getFluidKey(fluid), value);
	}

	public static void setFactor2(Fluid fluid, float value) {
		factor2s.put(getFluidKey(fluid), value);
	}

	private static void loadDefaults() {
		getDefault("fluid.mobessence").setBannedSea(false).setFactor1(98F).setFactor2(7F);
		getDefault("fluid.ender").setBannedSea(true).setFactor1(72F).setFactor2(6F);
		getDefault("fluid.manyullyn.molten").setBannedSea(true).setFactor1(72F).setFactor2(6F);
		getDefault("fluid.redstone").setBannedSea(true).setFactor1(72F).setFactor2(6F);
		getDefault("fluid.fluiduumatter").setBannedSea(true).setFactor1(32F).setFactor2(4F);
		getDefault("fluid.life_essence").setBannedSea(false).setFactor1(32F).setFactor2(4F);
		getDefault("fluid.alumite.molten").setBannedSea(true).setFactor1(18F).setFactor2(3F);
		getDefault("fluid.ardite.molten").setBannedSea(true).setFactor1(18F).setFactor2(3F);
		getDefault("fluid.cobalt.molten").setBannedSea(true).setFactor1(18F).setFactor2(3F);
		getDefault("fluid.fluid.purity").setBannedSea(false).setFactor1(18F).setFactor2(3F);
		getDefault("fluid.aluminum.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.biofuel").setBannedSea(false).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.bronze.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.chocolatemilk").setBannedSea(false).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.copper.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.creosote").setBannedSea(false).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.electrum.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.emerald.liquid").setBannedSea(false).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.fluidbiogas").setBannedSea(false).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.fluidbiomass").setBannedSea(false).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.fuel").setBannedSea(false).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.gold.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.invar.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.iron.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.lead.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.nickel.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.obsidian.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.platinum.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.silver.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.steel.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.tin.molten").setBannedSea(true).setFactor1(8F).setFactor2(2F);
		getDefault("fluid.aluminumbrass.molten").setBannedSea(true).setFactor1(2F).setFactor2(1F);
		getDefault("fluid.coal").setBannedSea(false).setFactor1(2F).setFactor2(1F);
		getDefault("fluid.glass.molten").setBannedSea(false).setFactor1(2F).setFactor2(1F);
		getDefault("fluid.glowstone").setBannedSea(false).setFactor1(2F).setFactor2(1F);
		getDefault("fluid.honey").setBannedSea(false).setFactor1(1F).setFactor2(0F);
		getDefault("fluid.oil").setBannedSea(false).setFactor1(1F).setFactor2(0F);
		getDefault("fluid.spring_water").setBannedSea(false).setFactor1(2F).setFactor2(1F);
		getDefault("fluid.witchery:fluidspirit").setBannedSea(false).setFactor1(2F).setFactor2(1F);
		getDefault("fluid.meat").setBannedSea(false).setFactor1(1F).setFactor2(0F);
		getDefault("fluid.myst.ink.black").setBannedSea(false).setFactor1(1F).setFactor2(0F);
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
		getDefault("fluid.slime.blue").setBannedSea(false).setFactor1(-0.5F).setFactor2(-0.5F);
		getDefault("fluid.cryotheum").setBannedSea(false).setFactor1(-2F).setFactor2(-1F);
		getDefault("fluid.etchacid").setBannedSea(false).setFactor1(-2F).setFactor2(-1F);
		getDefault("fluid.fluid.death").setBannedSea(false).setFactor1(-2F).setFactor2(-1F);
		getDefault("fluid.fluidhotcoolant").setBannedSea(false).setFactor1(-2F).setFactor2(-1F);
		getDefault("fluid.poison").setBannedSea(false).setFactor1(-2F).setFactor2(-1F);
		getDefault("fluid.sewage").setBannedSea(false).setFactor1(-2F).setFactor2(-1F);
		getDefault("fluid.sludge").setBannedSea(false).setFactor1(-2F).setFactor2(-1F);
		getDefault("fluid.witchery:hollowtears").setBannedSea(false).setFactor1(-2F).setFactor2(-1F);
		getDefault("fluid.mana").setBannedSea(false).setFactor1(-6F).setFactor2(-2F);
		getDefault("fluid.pyrotheum").setBannedSea(false).setFactor1(-6F).setFactor2(-2F);
		getDefault("fluid.fluxgoo").setBannedSea(true).setFactor1(-12F).setFactor2(-3F);
	}
}
