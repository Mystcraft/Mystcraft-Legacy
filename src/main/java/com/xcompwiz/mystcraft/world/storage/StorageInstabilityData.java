package com.xcompwiz.mystcraft.world.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.xcompwiz.mystcraft.instability.Deck;
import com.xcompwiz.mystcraft.instability.InstabilityManager;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class StorageInstabilityData extends WorldSavedData {

	public static final String					ID		= "MystInstabilityData";
	private HashMap<String, Collection<String>>	decks	= new HashMap<String, Collection<String>>();

	public StorageInstabilityData(String id) {
		super(id);
	}

	public void setAgeData(AgeData data) {
		NBTBase cruft = data.popCruft("instabilityeffects");
		if (cruft == null) return;
		Collection<String> cards = NBTUtils.readStringCollection((NBTTagList) cruft, new ArrayList<String>());
		for (String deck : InstabilityManager.getDecks()) {
			decks.put(deck, cards);
		}
	}

	public Collection<String> getDeck(String deckname) {
		Collection<String> cards = decks.get(deckname);
		if (cards == null) return new ArrayList<String>();
		return Collections.unmodifiableCollection(cards);
	}

	public void updateDeck(Deck deck) {
		Collection<String> cards = new ArrayList<String>();
		cards.addAll(deck.getCards());
		decks.put(deck.getName(), cards);
		this.markDirty();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for (Entry<String, Collection<String>> entry : decks.entrySet()) {
			NBTTagCompound decknbt = new NBTTagCompound();
			decknbt.setString("Name", entry.getKey());
			decknbt.setTag("Cards", NBTUtils.writeStringCollection(new NBTTagList(), entry.getValue()));
			list.appendTag(decknbt);
		}
		nbt.setTag("Decks", list);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("Decks", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound decknbt = list.getCompoundTagAt(i);
			String deckname = decknbt.getString("Name");
			Collection<String> cards = NBTUtils.readStringCollection(decknbt.getTagList("Cards", Constants.NBT.TAG_STRING), new ArrayList<String>());
			decks.put(deckname, cards);
		}
	}
}
