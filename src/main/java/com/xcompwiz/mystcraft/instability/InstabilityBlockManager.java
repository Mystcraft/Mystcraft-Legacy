package com.xcompwiz.mystcraft.instability;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class InstabilityBlockManager {

	private static final Collection<String>			watchedblocks	= new HashSet<String>();
	private static final Map<String, Float>			factor1s		= new HashMap<String, Float>();
	private static final Map<String, Float>			factor2s		= new HashMap<String, Float>();
	private static Map<String, ? extends Number>	freevals;

	public static final Map<String, Float>			ro_factor1s		= Collections.unmodifiableMap(factor1s);
	public static final Map<String, Float>			ro_factor2s		= Collections.unmodifiableMap(factor2s);

	public static void setInstabilityFactors(Block block, float factor1, float factor2) {
		setInstabilityFactors(block, 0, factor1, factor2);
	}

	public static void setInstabilityFactors(Block block, int metadata, float factor1, float factor2) {
		setInstabilityFactors(getOrCreateUnlocalizedKey(block, metadata), factor1, factor2);
	}

	public static void setInstabilityFactors(String unlocalizedkey, float factor1, float factor2) {
		watchedblocks.add(unlocalizedkey);
		factor1s.put(unlocalizedkey, factor1);
		factor2s.put(unlocalizedkey, factor2);
	}

	public static <T extends Number> void setBaselineStability(Map<String, T> newfreevals) {
		freevals = newfreevals;
	}

	private static final Map<Block, HashMap<Integer, String>>	keys	= new HashMap<Block, HashMap<Integer, String>>();

	private static String getOrCreateUnlocalizedKey(Block block, int metadata) {
		HashMap<Integer, String> metakeys = keys.get(block);
		if (metakeys == null) {
			metakeys = new HashMap<Integer, String>();
			keys.put(block, metakeys);
		}
		String key = metakeys.get(metadata);
		if (key == null) {
			ItemStack localizationitemstack = new ItemStack(block, 1, metadata);
			key = localizationitemstack.getUnlocalizedName();
			metakeys.put(metadata, key);
		}
		return key;
	}

	public static String getUnlocalizedKey(Block block, int metadata) {
		HashMap<Integer, String> metakeys = keys.get(block);
		if (metakeys == null) return null;
		return metakeys.get(metadata);
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
