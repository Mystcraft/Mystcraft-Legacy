package com.xcompwiz.mystcraft.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;

public class SymbolManager {

	private static HashMap<String, IAgeSymbol>		ageSymbols					= new HashMap<String, IAgeSymbol>();
	private static HashMap<String, String>			owners						= new HashMap<String, String>();
	private static SymbolProfiler					symbolProfiler				= new SymbolProfiler();
	private static HashSet<String>					errored						= new HashSet<String>();
	private static HashSet<String>					warned						= new HashSet<String>();
	private static HashSet<String>					blacklist					= new HashSet<String>();
	private static Configuration					config;

	private static HashMap<String, Integer>			cardranks					= new HashMap<String, Integer>();
	private static ArrayList<Integer>				cardranksizes				= new ArrayList<Integer>();
	private static HashMap<Integer, Integer>		cardrankweights				= null;
	private static HashMap<String, Integer>			maxTreasureStackOverrides	= new HashMap<String, Integer>();
	private static HashMap<String, Boolean>			tradeableOverrides			= new HashMap<String, Boolean>();
	private static HashMap<String, List<ItemStack>>	tradeItemOverrides			= new HashMap<String, List<ItemStack>>();

	private static HashMap<Integer, Integer>			defaultMaxStacks			= new HashMap<Integer, Integer>();
	static {
		defaultMaxStacks.put(null, 0);
		defaultMaxStacks.put(0, 16);
		defaultMaxStacks.put(1, 8);
		defaultMaxStacks.put(2, 4);
		defaultMaxStacks.put(3, 2);
	}

	public static void blackListSymbol(String identifier) {
		blacklist.add(identifier);
		IAgeSymbol symbol = ageSymbols.get(identifier);
		if (symbol != null) unregisterSymbol(symbol);
	}

	public static void setConfig(MystConfig mystconfig) {
		config = mystconfig;
	}

	public static boolean registerSymbol(IAgeSymbol symbol, boolean generateConfigOption, String modid) {
		boolean enabled = true;
		if (symbol.identifier() == null || symbol.identifier().length() == 0) {
			LoggerUtils.error(String.format("Attempting to bind symbol with null or zero length identifier."));
			return false;
		}
		if (config != null && generateConfigOption && !config.get(MystConfig.CATEGORY_SYMBOLS, symbol.identifier().toLowerCase().replace(' ', '_') + ".enabled", enabled).getBoolean(enabled)) { return false; }
		if (SymbolRemappings.hasRemapping(symbol.identifier())) {
			LoggerUtils.error(String.format("%s has a remapping binding.  No symbol can be bound to this name.", symbol.identifier()));
			return false;
		}
		if (blacklist.contains(symbol.identifier())) {
			LoggerUtils.info(String.format("Symbol %s is turned off by blacklist.", symbol.identifier()));
			return false;
		}
		if (ageSymbols.get(symbol.identifier()) != null) {
			String str = String.format("%s cannot register symbol with identifier %s. Already bound by %s.", modid, symbol.identifier(), owners.get(symbol.identifier()));
			LoggerUtils.error(str);
			throw new RuntimeException(str);
		}
		symbolProfiler.startProfiling(symbol);
		try {
			symbol.registerLogic(symbolProfiler, 0);
		} catch (Exception e) {
			LoggerUtils.error(String.format("Exception encountered when profiling symbol with identifier %s.", symbol.identifier()));
			LoggerUtils.error(e.toString());
			e.printStackTrace();
			blackListSymbol(symbol.identifier());
			errored.add(symbol.identifier());
		} finally {
			symbolProfiler.endProfiling(symbol);
			if (config != null && config.hasChanged()) config.save();
		}
		ageSymbols.put(symbol.identifier(), symbol);
		owners.put(symbol.identifier(), modid);
		return true;
	}

	public static HashSet<String> getErroredSymbols() {
		return errored;
	}

	private static void unregisterSymbol(IAgeSymbol symbol) {
		if (ageSymbols.get(symbol.identifier()) == symbol) {
			ageSymbols.remove(symbol.identifier());
			symbolProfiler.remove(symbol);
		}
	}

	// Get Age Symbol by identifier
	public static IAgeSymbol getAgeSymbol(String id) {
		IAgeSymbol symbol;
		symbol = ageSymbols.get(id);
		if (symbol == null && warned.add(id)) {
			LoggerUtils.error(String.format("No Symbol match for identifier %s.  Are all of the Age Symbols loaded?", id));
		}
		return symbol;
	}

	public static boolean hasBinding(String id) {
		return ageSymbols.containsKey(id);
	}

	public static String getSymbolOwner(String identifier) {
		return owners.get(identifier);
	}

	// Get all registered AgeSymbols
	public static ArrayList<IAgeSymbol> getAgeSymbols() {
		ArrayList<IAgeSymbol> symbols = new ArrayList<IAgeSymbol>();
		for (IAgeSymbol s : ageSymbols.values()) {
			symbols.add(s);
		}
		return symbols;
	}

	// Gets a list of symbols which implement a specific interface
	public static HashSet<IAgeSymbol> findAgeSymbolsImplementing(Class<?> Interface) {
		return symbolProfiler.getSymbolsProviding(Interface);
	}

	public static IAgeSymbol findAgeSymbolImplementing(Random rand, Class<?> instance) {
		return WeightedItemSelector.getRandomItem(rand, findAgeSymbolsImplementing(instance));
	}

	public static Collection<IAgeSymbol> getSymbolsByRank(Integer exact) {
		return getSymbolsByRank(exact, exact);
	}

	/**
	 * Retrieves all symbols which have an item weight in the provide range. The range is (min, max]. If null is passed for part of the range, it counts as
	 * infinity in that direction.
	 * @param min The minimum weight, non-inclusive or null
	 * @param max The maximum weight, inclusive or null
	 * @return The collection of symbols with an item weight in the range
	 */
	public static Collection<IAgeSymbol> getSymbolsByRank(Integer min, Integer max) {
		Collection<IAgeSymbol> set = new ArrayList<IAgeSymbol>();
		Collection<String> symbolIds = ageSymbols.keySet();
		for (String symbolId : symbolIds) {
			Integer rank = getSymbolItemCardRank(symbolId);
			if (rank == null) continue;
			if (min != null && min > rank) continue;
			if (max != null && max < rank) continue;
			set.add(ageSymbols.get(symbolId));
		}
		return set;
	}

	// ----------------------------- Rarities and Settings ----------------------------- //
	public static int getSymbolItemWeight(String identifier) {
		if (!cardranks.containsKey(identifier)) return 0;
		Integer rank = cardranks.get(identifier);
		if (rank == null) return 0;
		if (cardrankweights == null) { throw new RuntimeException("Cannot obtain symbol treasure weight: Card ranking system not built"); }
		return cardrankweights.get(rank);
	}

	public static void setSymbolItemCardRank(String identifier, int cardrank) {
		if (cardrankweights != null) { throw new RuntimeException("Cannot set symbol rarity ranking: rank weights finalized"); }
		cardranks.put(identifier, cardrank);
		while (cardranksizes.size() <= cardrank) {
			cardranksizes.add(0);
		}
		cardranksizes.set(cardrank, cardranksizes.get(cardrank) + 1);
	}

	public static Integer getSymbolItemCardRank(String identifier) {
		return cardranks.get(identifier);
	}

	public static int getSymbolTreasureMaxStack(IAgeSymbol symbol) {
		Integer rank = getSymbolItemCardRank(symbol.identifier());
		Integer dfault = defaultMaxStacks.get(rank);
		if (dfault == null) dfault = 1;
		if (!maxTreasureStackOverrides.containsKey(symbol.identifier())) return dfault;
		Integer override = maxTreasureStackOverrides.get(symbol.identifier());
		if (override == null) return dfault;
		return override;
	}

	public static void setSymbolTreasureMaxStack(String identifier, int override) {
		maxTreasureStackOverrides.put(identifier, override);
	}

	public static boolean isSymbolTradable(String identifier) {
		boolean dfault = getSymbolItemWeight(identifier) > 0;
		if (!tradeableOverrides.containsKey(identifier)) return dfault;
		Boolean override = tradeableOverrides.get(identifier);
		if (override == null) return dfault;
		return override;
	}

	public static void setSymbolIsTradable(String identifier, boolean override) {
		tradeableOverrides.put(identifier, override);
	}

	public static List<ItemStack> getSymbolTradeItems(String identifier) {
		ItemStack dfault = new ItemStack(Items.emerald, Math.max(1, (int) (65F - 64F * getSymbolItemWeight(identifier))));
		if (!tradeItemOverrides.containsKey(identifier)) return Arrays.asList(dfault);
		List<ItemStack> override = tradeItemOverrides.get(identifier);
		if (override == null) return Arrays.asList(dfault);
		return override;
	}

	public static void setSymbolTradeItems(String identifier, ItemStack primary, ItemStack secondary) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (primary != null) {
			list.add(primary.copy());
		}
		if (secondary != null) {
			list.add(secondary.copy());
		}
		if (list.size() > 0) {
			tradeItemOverrides.put(identifier, list);
		} else {
			setSymbolIsTradable(identifier, false);
		}
	}

	public static void buildCardRanks() {
		final int step = 1; //Increment between ranks
		cardrankweights = new HashMap<Integer, Integer>();
		int weight = 1;
		int lasttotal = 0;
		for (int i = cardranksizes.size() - 1; i >= 0; --i) {
			int count = cardranksizes.get(i);
			if (weight != 1 && count > 0) {
				weight = Math.max(weight, lasttotal / count + step);
			}
			cardrankweights.put(i, weight);
			lasttotal = count * weight;
			weight += step;
		}
	}

	public static void registerRules() {
		ArrayList<IAgeSymbol> symbols = getAgeSymbols();
		for (IAgeSymbol symbol : symbols) {
			if (symbol instanceof SymbolBase) {
				ArrayList<Rule> rules = ((SymbolBase) symbol).getRules();
				if (rules == null) continue;
				for (Rule rule : rules) {
					GrammarGenerator.registerRule(rule);
				}
			}
		}
	}
}
