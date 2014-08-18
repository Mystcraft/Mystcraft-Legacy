package com.xcompwiz.mystcraft.instability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.google.common.collect.HashMultiset;
import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.core.DebugDataTracker;
import com.xcompwiz.mystcraft.world.IAgeController;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

public class InstabilityController implements IInstabilityController {
	private IAgeController							controller;
	private AgeData									agedata;
	private boolean									enabled;
	private int										lastScore;

	private Collection<Deck>						decks;
	private HashMap<String, Integer>	providerlevels	= new HashMap<String, Integer>();
	private Collection<IEnvironmentalEffect>		effects			= new ArrayList<IEnvironmentalEffect>();

	public InstabilityController(IAgeController controller, AgeData agedata) {
		this.controller = controller;
		this.enabled = (agedata.isInstabilityEnabled() && Mystcraft.instabilityEnabled);
		this.agedata = agedata;
		buildDecks();
		reconstruct();
	}

	private void buildDecks() {
		decks = InstabilityManager.createDecks();
		Random rand = new Random(agedata.getSeed());
		for (Deck deck : decks) {
			String deckname = deck.getName();
			Collection<String> cards = HashMultiset.create(deck.getCards());
			deck.removeAll();
			Collection<String> order = agedata.getDeck(deckname);
			boolean dirty = false;
			for (String card : order) {
				if (cards.remove(card)) {
					deck.putOnBottom(card);
				} else {
					dirty = true;
				}
			}
			if (cards.size() > 0) {
				Deck newdeck = new Deck("temp", cards);
				newdeck.shuffle(rand);
				deck.putOnBottom(newdeck);
				dirty = true;
			}
			if (dirty) {
				agedata.saveDeck(deck);
			}
		}
	}

	private void validate() {
		if (enabled != (agedata.isInstabilityEnabled() && Mystcraft.instabilityEnabled)) {
			enabled = (agedata.isInstabilityEnabled() && Mystcraft.instabilityEnabled);
			reconstruct();
			return;
		}
		if (controller.getInstabilityScore() != lastScore) {
			lastScore = controller.getInstabilityScore();
			reconstruct();
		}
	}

	private void reconstruct() {
		providerlevels.clear();
		effects.clear();
		if (!enabled) return;
		for (Deck deck : decks) {
			Collection<String> providernames = getProviders(deck);
			if (providernames == null) continue;
			for (String name : providernames) {
				addProviderLevel(name);
			}
		}
		rebuildEffects();
	}

	private Collection<String> getProviders(Deck deck) {
		int instabilityScore = getInstabilityScore();
		instabilityScore -= InstabilityManager.getDeckCost(deck.getName());
		if (instabilityScore <= 0) return null;
		Collection<String> providers = new ArrayList<String>();
		for (String card : deck.getCards()) {
			int cost = InstabilityManager.getCardCost(card);
			instabilityScore -= cost;
			if (instabilityScore <= 0) break;
			providers.add(card);
		}
		return providers;
	}

	private void addProviderLevel(String provider) {
		Integer level = providerlevels.get(provider);
		if (level == null) level = 0;
		this.providerlevels.put(provider, level + 1);
	}

	private void rebuildEffects() {
		effects.clear();
		for (String name : providerlevels.keySet()) {
			IInstabilityProvider provider = InstabilityManager.getProvider(name);
			Integer level = providerlevels.get(name);
			if (provider != null && level != null) {
				provider.addEffects(this, level );
			}
		}
		DebugDataTracker.set(agedata.getAgeName()+".effects", ""+providerlevels);
	}

	public void tick(World world, Chunk chunk) {
		validate();
		if (!enabled) return;
		if (effects != null && effects.size() > 0) {
			for (IEnvironmentalEffect effect : effects) {
				effect.tick(world, chunk);
			}
		}
	}

	@Override
	public int getInstabilityScore() {
		return lastScore;
	}

	@Override
	public void registerEffect(IEnvironmentalEffect effect) {
		effects.add(effect);
	}
}
