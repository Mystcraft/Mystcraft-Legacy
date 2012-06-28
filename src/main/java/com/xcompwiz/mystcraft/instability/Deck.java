package com.xcompwiz.mystcraft.instability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Deck {

	private final String		name;
	private LinkedList<String>	cards;
	//XXX: Move this to Instability Controller
	private final int			activationCost;

	public Deck(String name, Collection<String> cards) {
		this(name, cards, 0);
	}

	public Deck(String name, Collection<String> cards, int activationcost) {
		this.name = name;
		this.cards = new LinkedList<String>(cards);
		this.activationCost = activationcost;
	}

	public String getName() {
		return this.name;
	}

	public Collection<String> getCards() {
		return Collections.unmodifiableCollection(cards);
	}

	public void removeAll() {
		cards.clear();
	}

	public void shuffle(Random random) {
		Collections.shuffle(cards, random);
	}

	public void putOnBottom(String card) {
		cards.add(card);
	}

	public void putOnBottom(Deck newdeck) {
		cards.addAll(newdeck.cards);
		newdeck.cards.clear();
	}

	//XXX: Move this to Instability Controller
	public Collection<String> getProviders(int instabilityScore) {
		instabilityScore -= activationCost;
		if (instabilityScore <= 0) return null;
		Collection<String> providers = new ArrayList<String>();
		for (String card : cards) {
			int cost = InstabilityManager.getCardCost(card);
			instabilityScore -= cost;
			if (instabilityScore <= 0) break;
			providers.add(card);
		}
		return providers;
	}

}
