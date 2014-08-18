package com.xcompwiz.mystcraft.instability;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Deck {

	private final String		name;
	private LinkedList<String>	cards;

	public Deck(String name, Collection<String> cards) {
		this.name = name;
		this.cards = new LinkedList<String>(cards);
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
}
