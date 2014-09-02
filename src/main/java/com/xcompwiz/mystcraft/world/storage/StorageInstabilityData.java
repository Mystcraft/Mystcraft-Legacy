package com.xcompwiz.mystcraft.world.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.instability.Deck;

public class StorageInstabilityData extends WorldSavedData {

	public static final String					ID		= "MystInstabilityData";
	private HashMap<String, Collection<String>>	decks	= new HashMap<String, Collection<String>>();

	public StorageInstabilityData(String id) {
		super(id);
	}

	public Collection<String> getDeck(String deckname) {
		Collection<String> cards = decks.get(deckname);
		if (cards == null) return new ArrayList<String>();
		return Collections.unmodifiableCollection(cards);
	}

	public void saveDeck(Deck deck) {
		Collection<String> cards = new ArrayList<String>();
		cards.addAll(deck.getCards());
		decks.put(deck.getName(), cards);
		this.markDirty();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for (Entry<String, Collection<String>> entry : decks.entrySet()) {
			NBTTagCompound decknbt = new NBTTagCompound();
			decknbt.setString("Name", entry.getKey());
			NBTTagList cardlist = new NBTTagList();
			for (String card : entry.getValue()) {
				cardlist.appendTag(new NBTTagString(card));
			}
			decknbt.setTag("Cards", cardlist);
			list.appendTag(decknbt);
		}
		nbt.setTag("Decks", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("Decks", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound decknbt = list.getCompoundTagAt(i);
			String deckname = decknbt.getString("Name");
			NBTTagList list2 = decknbt.getTagList("Cards", Constants.NBT.TAG_STRING);
			Collection<String> cards = new ArrayList<String>();
			for (int j = 0; j < list2.tagCount(); ++j) {
				cards.add(list.getStringTagAt(j));
			}
			decks.put(deckname, cards);
		}
	}
}
