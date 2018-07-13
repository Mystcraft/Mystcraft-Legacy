package com.xcompwiz.mystcraft.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

public class SymbolManager {

	private static ResourceLocation SYMBOL_REGISTRY_NAME = new ResourceLocation(MystObjects.MystcraftModId, "symbol_registry");
	private static IForgeRegistryModifiable<IAgeSymbol> SYMBOL_REGISTRY = null;

	private static SymbolProfiler symbolProfiler = new SymbolProfiler();
	private static Set<ResourceLocation> errored = new HashSet<>();
	private static Set<ResourceLocation> warned = new HashSet<>();
	private static Set<ResourceLocation> blacklist = new HashSet<>();
	private static Configuration config;

	private static HashMap<ResourceLocation, Integer> cardranks = new HashMap<>();
	private static ArrayList<Integer> cardranksizes = new ArrayList<>();
	private static HashMap<Integer, Integer> cardrankweights = null;
	private static HashMap<ResourceLocation, Integer> maxTreasureStackOverrides = new HashMap<>();
	private static HashMap<ResourceLocation, Boolean> tradeableOverrides = new HashMap<>();
	private static HashMap<ResourceLocation, List<ItemStack>> tradeItemOverrides = new HashMap<>();

	private static HashMap<Integer, Integer> defaultMaxStacks = new HashMap<>();

	static {
		defaultMaxStacks.put(null, 0);
		defaultMaxStacks.put(0, 16);
		defaultMaxStacks.put(1, 8);
		defaultMaxStacks.put(2, 4);
		defaultMaxStacks.put(3, 2);
	}

	public static void buildRegistry() {
		if (SYMBOL_REGISTRY != null)
			return;

		RegistryBuilder<IAgeSymbol> builder = new RegistryBuilder<>();
		builder.setName(SYMBOL_REGISTRY_NAME).disableSaving();
		builder.setType(IAgeSymbol.class).allowModification();
		builder.add(new SymbolAddListener());

		IForgeRegistry<IAgeSymbol> registry = builder.create();
		if (!(registry instanceof IForgeRegistryModifiable)) {
			throw new IllegalArgumentException("Forge registry builder didn't build a modifiable registry! Did something in forge change internally?");
		}
		SYMBOL_REGISTRY = (IForgeRegistryModifiable<IAgeSymbol>) registry;
	}

	public static void blackListSymbol(ResourceLocation identifier) {
		blacklist.add(identifier);
		IAgeSymbol symbol = SYMBOL_REGISTRY.getValue(identifier);
		if (symbol != null) {
			symbolProfiler.remove(symbol);
		}
	}

	public static void setConfig(MystConfig mystconfig) {
		config = mystconfig;
	}

	public static boolean tryAddSymbol(IAgeSymbol symbol) {
		return tryAddSymbol(symbol, true);
	}

	private static boolean tryAddSymbol(IAgeSymbol symbol, boolean addForRegistration) {
		boolean enabled = true;
		//HellFire> RegistryName empty check happens forge internally.
		//if (symbol.identifier() == null || symbol.identifier().length() == 0) {
		//	LoggerUtils.error(String.format("Attempting to bind symbol with null or zero length identifier."));
		//	return false;
		//}
		if (config != null && symbol.generatesConfigOption() && !config.get(MystConfig.CATEGORY_SYMBOLS + "." + symbol.getRegistryName().getResourceDomain(), symbol.getRegistryName().getResourcePath().toLowerCase().replace(' ', '_') + ".enabled", enabled).getBoolean(enabled)) {
			return false;
		}
		if (SymbolRemappings.hasRemapping(symbol.getRegistryName())) {
			LoggerUtils.error(String.format("%s has a remapping binding.  No symbol can be bound to this name.", symbol.getRegistryName().toString()));
			return false;
		}
		if (blacklist.contains(symbol.getRegistryName())) {
			LoggerUtils.info(String.format("Symbol %s is turned off by blacklist.", symbol.getRegistryName().toString()));
			return false;
		}
		//HellFire> Registry prevents this kind of shenanigans
		//if (ageSymbols.get(symbol.identifier()) != null) {
		//	String str = String.format("%s cannot register symbol with identifier %s. Already bound by %s.", modid, symbol.identifier(), owners.get(symbol.identifier()));
		//	LoggerUtils.error(str);
		//	throw new RuntimeException(str);
		//}
		symbolProfiler.startProfiling(symbol);
		try {
			symbol.registerLogic(symbolProfiler, 0);
		} catch (Exception e) {
			LoggerUtils.error(String.format("Exception encountered when profiling symbol with identifier %s.", symbol.getRegistryName().toString()));
			LoggerUtils.error(e.toString());
			e.printStackTrace();
			blackListSymbol(symbol.getRegistryName());
			errored.add(symbol.getRegistryName());
		} finally {
			symbolProfiler.endProfiling(symbol);
			if (config != null && config.hasChanged()) {
				config.save();
			}
		}
		if (addForRegistration) {
			SYMBOL_REGISTRY.register(symbol);
		}
		return true;
	}

	public static Set<ResourceLocation> getErroredSymbols() {
		return errored;
	}

	// Get Age Symbol by identifier
	public static IAgeSymbol getAgeSymbol(ResourceLocation id) {
		if (id == null) {
			return null;
		}
		IAgeSymbol symbol = null;
		if (!blacklist.contains(id)) {
			symbol = SYMBOL_REGISTRY.getValue(id);
		}
		if (symbol == null && warned.add(id)) {
			LoggerUtils.error(String.format("No Symbol match for identifier %s.  Are all of the Age Symbols loaded?", id.toString()));
		}
		return symbol;
	}

	public static boolean hasBinding(ResourceLocation id) {
		return !blacklist.contains(id) && SYMBOL_REGISTRY.containsKey(id);
	}

	public static String getSymbolOwner(ResourceLocation identifier) {
		if (blacklist.contains(identifier)) {
			return null;
		}
		IAgeSymbol symbol = SYMBOL_REGISTRY.getValue(identifier);
		return symbol == null ? null : symbol.getRegistryName().getResourceDomain();
	}

	// Get all registered AgeSymbols
	public static ArrayList<IAgeSymbol> getAgeSymbols() {
		ArrayList<IAgeSymbol> symbols = new ArrayList<IAgeSymbol>();
		for (IAgeSymbol s : SYMBOL_REGISTRY.getValues()) {
			if (blacklist.contains(s.getRegistryName()))
				continue;
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
	 * Retrieves all symbols which have an item weight in the provide range. The range is [min, max]. If null is passed for part of the range, it counts as
	 * infinity in that direction.
	 * @param min The minimum weight, inclusive or null
	 * @param max The maximum weight, inclusive or null
	 * @return The collection of symbols with an item weight in the range
	 */
	public static Collection<IAgeSymbol> getSymbolsByRank(Integer min, Integer max) {
		Collection<IAgeSymbol> set = new ArrayList<IAgeSymbol>();
		Collection<ResourceLocation> symbolIds = SYMBOL_REGISTRY.getKeys();
		for (ResourceLocation symbolId : symbolIds) {
			if (blacklist.contains(symbolId))
				continue;

			Integer rank = getSymbolItemCardRank(symbolId);
			if (rank == null)
				continue;
			if (min != null && min > rank)
				continue;
			if (max != null && max < rank)
				continue;
			set.add(SYMBOL_REGISTRY.getValue(symbolId));
		}
		return set;
	}

	//SymbolRegistry add listener
	public static class SymbolAddListener implements IForgeRegistry.AddCallback<IAgeSymbol> {

		@Override
		public void onAdd(IForgeRegistryInternal<IAgeSymbol> owner, RegistryManager stage, int id, IAgeSymbol obj, @Nullable IAgeSymbol oldObj) {
			if (!SymbolManager.tryAddSymbol(obj, false)) {
				stage.getRegistry(SYMBOL_REGISTRY_NAME).remove(obj.getRegistryName());
			}
		}

	}

	// ----------------------------- Rarities and Settings ----------------------------- //
	public static int getSymbolItemWeight(ResourceLocation identifier) {
		if (!cardranks.containsKey(identifier))
			return 0;
		Integer rank = cardranks.get(identifier);
		if (rank == null)
			return 0;
		if (cardrankweights == null) {
			throw new RuntimeException("Cannot obtain symbol treasure weight: Card ranking system not built");
		}
		return cardrankweights.get(rank);
	}

	public static void setSymbolItemCardRank(ResourceLocation identifier, int cardrank) {
		if (cardrankweights != null) {
			throw new RuntimeException("Cannot set symbol rarity ranking: rank weights finalized");
		}
		cardranks.put(identifier, cardrank);
		while (cardranksizes.size() <= cardrank) {
			cardranksizes.add(0);
		}
		cardranksizes.set(cardrank, cardranksizes.get(cardrank) + 1);
	}

	public static Integer getSymbolItemCardRank(ResourceLocation identifier) {
		return cardranks.get(identifier);
	}

	public static int getSymbolTreasureMaxStack(IAgeSymbol symbol) {
		Integer rank = getSymbolItemCardRank(symbol.getRegistryName());
		Integer dfault = defaultMaxStacks.get(rank);
		if (dfault == null)
			dfault = 1;
		if (!maxTreasureStackOverrides.containsKey(symbol.getRegistryName()))
			return dfault;
		Integer override = maxTreasureStackOverrides.get(symbol.getRegistryName());
		if (override == null)
			return dfault;
		return override;
	}

	public static void setSymbolTreasureMaxStack(ResourceLocation identifier, int override) {
		maxTreasureStackOverrides.put(identifier, override);
	}

	public static boolean isSymbolTradable(ResourceLocation identifier) {
		boolean dfault = getSymbolItemWeight(identifier) > 0;
		if (!tradeableOverrides.containsKey(identifier))
			return dfault;
		Boolean override = tradeableOverrides.get(identifier);
		if (override == null)
			return dfault;
		return override;
	}

	public static void setSymbolIsTradable(ResourceLocation identifier, boolean override) {
		tradeableOverrides.put(identifier, override);
	}

	public static List<ItemStack> getSymbolTradeItems(ResourceLocation identifier) {
		ItemStack dfault = new ItemStack(Items.EMERALD, Math.max(1, (int) (12F * getSymbolItemCardRank(identifier))));
		if (!tradeItemOverrides.containsKey(identifier))
			return Arrays.asList(dfault);
		List<ItemStack> override = tradeItemOverrides.get(identifier);
		if (override == null)
			return Arrays.asList(dfault);
		return override;
	}

	public static void setSymbolTradeItems(ResourceLocation identifier, ItemStack primary, ItemStack secondary) {
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
		cardrankweights = new HashMap<>();
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
				if (rules == null)
					continue;
				for (Rule rule : rules) {
					GrammarGenerator.registerRule(rule);
				}
			}
		}
	}
}
