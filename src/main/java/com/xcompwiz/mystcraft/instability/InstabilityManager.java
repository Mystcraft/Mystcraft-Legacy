package com.xcompwiz.mystcraft.instability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;

import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class InstabilityManager {
	private static HashMap<String, IInstabilityProvider>	providers		= new HashMap<String, IInstabilityProvider>();
	private static HashMap<String, Integer>					cardcosts		= new HashMap<String, Integer>();
	private static HashMap<String, Integer>					cardcounts		= new HashMap<String, Integer>();
	private static Configuration							config;

	private static InstabilityProfiler						profiler		= new InstabilityProfiler();
	private static HashSet<String>							errored			= new HashSet<String>();
	private static HashSet<String>							warned			= new HashSet<String>();

	private static HashMap<String, List<String>>			deckcards		= new HashMap<String, List<String>>();
	private static HashMap<String, Integer>					deckcosts		= new HashMap<String, Integer>();
	private static int										smallestcost	= 500;

	public static void setConfig(MystConfig mystconfig) {
		config = mystconfig;
	}

	public static boolean registerProvider(String identifier, IInstabilityProvider provider, int activationcost) {
		if (identifier == null || identifier.length() == 0) {
			LoggerUtils.error(String.format("Attempting to bind instability provider with null or zero length identifier."));
			return false;
		}
		if (providers.get(identifier) != null) {
			LoggerUtils.warn(String.format("Instability with Identifier %s already bound", identifier));
		}
		if (config != null && !config.get(MystConfig.CATEGORY_INSTABILITY, identifier.toLowerCase().replace(' ', '_') + ".enabled", true).getBoolean(true)) { return false; }

		providers.put(identifier, provider);
		cardcosts.put(identifier, activationcost);
		if (activationcost > 0 && activationcost < smallestcost) smallestcost = activationcost;
		if (config != null && config.hasChanged()) config.save();
		return true;
	}

	private static void profile(String identifier) {
		IInstabilityProvider provider = providers.get(identifier);
		if (provider == null) return;
		profiler.startProfiling(provider);
		try {
			provider.addEffects(profiler, cardcounts.get(identifier));
		} catch (Exception e) {
			LoggerUtils.error(String.format("Exception encountered when profiling instability provider with identifier %s.", identifier));
			LoggerUtils.error(e.toString());
			e.printStackTrace();
			errored.add(identifier);
			unregisterProvider(identifier, provider);
		} finally {
			profiler.endProfiling(provider);
		}
	}

	private static void unregisterProvider(String identifier, IInstabilityProvider provider) {
		if (providers.get(identifier) == provider) {
			providers.remove(identifier);
		}
	}

	public static int getSmallestCost() {
		return smallestcost;
	}

	public static IInstabilityProvider getProvider(String identifier) {
		IInstabilityProvider provider = providers.get(identifier);
		if (provider == null && warned.add(identifier)) {
			LoggerUtils.error(String.format("No Instability match for identifier %s.  Are all of the Instability Providers loaded?", identifier));
		}
		return provider;
	}

	public static Collection<String> getAllProviders() {
		return Collections.unmodifiableCollection(providers.keySet());
	}

	public static void addCards(String deckname, List<String> cards) {
		if (!deckcosts.containsKey(deckname)) { throw new RuntimeException("Attempting to register card to unregistered deck"); }
		List<String> deck = deckcards.get(deckname);
		if (deck == null) {
			deck = new ArrayList<String>();
			deckcards.put(deckname, deck);
		}
		Set<String> newcards = new HashSet<String>();
		for (String card : cards) {
			if (getProvider(card) == null) {
				LoggerUtils.error(String.format("Attempting to register card for unmatched identifier %s to deck %s.", card, deckname));
				continue;
			}
			Integer v = cardcounts.get(card);
			if (v == null) v = 0;
			cardcounts.put(card, v + 1);
			deck.add(card);
			newcards.add(card);
		}
		for (String provider : newcards) {
			profile(provider);
		}
	}

	public static void addCards(String deckname, String... cards) {
		addCards(deckname, Arrays.asList(cards));
	}

	public static void addCards(String deckname, String card, int count) {
		addCards(deckname, Collections.nCopies(count, card));
	}

	public static Collection<Deck> createDecks() {
		Collection<Deck> decks = new ArrayList<Deck>();
		for (Entry<String, List<String>> entry : deckcards.entrySet()) {
			decks.add(new Deck(entry.getKey(), entry.getValue()));
		}
		return decks;
	}

	public static int getCardCost(String card) {
		Integer val = cardcosts.get(card);
		if (val == null) return 0;
		return val;
	}

	public static void setDeckCost(String deckname, int cost) {
		deckcosts.put(deckname, cost);
	}

	public static int getDeckCost(String deckname) {
		Integer val = deckcosts.get(deckname);
		if (val == null) return 0;
		return val;
	}

	public static Collection<String> getDecks() {
		return deckcosts.keySet();
	}
}
