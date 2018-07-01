package com.xcompwiz.mystcraft.instability;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class InstabilityBlockManager {

	private static final Collection<String> watchedblocks = new HashSet<String>();
	private static final Map<String, Float> factor1s = new HashMap<String, Float>();
	private static final Map<String, Float> factor2s = new HashMap<String, Float>();
	private static Map<String, ? extends Number> freevals;

	public static final Map<String, Float> ro_factor1s = Collections.unmodifiableMap(factor1s);
	public static final Map<String, Float> ro_factor2s = Collections.unmodifiableMap(factor2s);

	public static void setInstabilityFactors(Block block, float factor1, float factor2) {
		setInstabilityFactors(block.getDefaultState(), factor1, factor2);
	}

	public static void setInstabilityFactors(IBlockState blockstate, float factor1, float factor2) {
		setInstabilityFactors(getOrCreateStateKey(blockstate), factor1, factor2);
	}

	public static void setInstabilityFactors(String statekey, float factor1, float factor2) {
		watchedblocks.add(statekey);
		factor1s.put(statekey, factor1);
		factor2s.put(statekey, factor2);
	}

	public static <T extends Number> void setBaselineStability(Map<String, T> newfreevals) {
		freevals = newfreevals;
	}

	private static final HashMap<IBlockState, String> keys = new HashMap<>();

	//This name is not used for display/translation. a registry name is the better choice here.
	private static String getOrCreateStateKey(IBlockState blockstate) {
		String key = keys.get(blockstate);
		if (key == null) {
			key = blockstate.getBlock().getRegistryName().toString() + "_" + blockstate.getBlock().getMetaFromState(blockstate);
			keys.put(blockstate, key);
		}
		return key;
	}

	public static String getStateKey(IBlockState block) {
		return keys.get(block);
	}

	public static Collection<String> getWatchedBlocks() {
		return Collections.unmodifiableCollection(watchedblocks);
	}

	public static int getBaseline(String key) {
		if (freevals == null)
			return 0;
		Number val = freevals.get(key);
		if (val == null)
			return 0;
		return val.intValue();
	}

	public static void clearBaselineStability() {
		freevals = null;
	}

	public static boolean isBaselineConstructed() {
		return freevals != null;
	}
}
