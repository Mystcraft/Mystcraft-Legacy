package com.xcompwiz.mystcraft.instability;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class InstabilityBlockManager {

	private static final Collection<String>			watchedblocks	= new HashSet<String>();
	private static final Map<String, Float>			factor1s		= new HashMap<String, Float>();
	private static final Map<String, Float>			factor2s		= new HashMap<String, Float>();
	private static Map<String, ? extends Number>	freevals;

	public static final Map<String, Float>			ro_factor1s		= Collections.unmodifiableMap(factor1s);
	public static final Map<String, Float>			ro_factor2s		= Collections.unmodifiableMap(factor2s);

	public static void setInstabilityFactors(Block block, float factor1, float factor2) {
		setInstabilityFactors(block.getDefaultState(), factor1, factor2);
	}

	public static void setInstabilityFactors(IBlockState blockstate, float factor1, float factor2) {
		setInstabilityFactors(getOrCreateUnlocalizedKey(blockstate), factor1, factor2);
	}

	public static void setInstabilityFactors(String unlocalizedkey, float factor1, float factor2) {
		watchedblocks.add(unlocalizedkey);
		factor1s.put(unlocalizedkey, factor1);
		factor2s.put(unlocalizedkey, factor2);
	}

	public static <T extends Number> void setBaselineStability(Map<String, T> newfreevals) {
		freevals = newfreevals;
	}

	private static final HashMap<IBlockState, String>	keys	= new HashMap<IBlockState, String>();

	private static String getOrCreateUnlocalizedKey(IBlockState blockstate) {
		String key = keys.get(blockstate);
		if (key == null) {
			ItemStack localizationitemstack = new ItemStack(blockstate);
			key = localizationitemstack.getUnlocalizedName();
			keys.put(blockstate, key);
		}
		return key;
	}

	public static String getUnlocalizedKey(IBlockState block) {
		return keys.get(block);
	}

	public static Collection<String> getWatchedBlocks() {
		return Collections.unmodifiableCollection(watchedblocks);
	}

	public static int getBaseline(String key) {
		if (freevals == null) return 0;
		Number val = freevals.get(key);
		if (val == null) return 0;
		return val.intValue();
	}

	public static void clearBaselineStability() {
		freevals = null;
	}

	public static boolean isBaselineConstructed() {
		return freevals != null;
	}
}
