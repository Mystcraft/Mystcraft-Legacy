package com.xcompwiz.mystcraft.world.agedata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.instability.InstabilityManager;
import com.xcompwiz.mystcraft.page.Page;

public class AgeDataLoaderV4_2 extends AgeDataLoader {
	private String								agename;
	private Set<String>							authors	= new HashSet<String>();
	private long								seed;
	private short								instability;
	private boolean								instabilityEnabled;
	private ChunkCoordinates					spawn;
	private List<ItemStack>						pages	= new ArrayList<ItemStack>();
	private List<String>						symbols	= new ArrayList<String>();
	private boolean								visited;
	private NBTTagCompound						datacompound;
	private long								worldtime;

	public AgeDataLoaderV4_2(NBTTagCompound nbttagcompound) {
		String version = nbttagcompound.getString("Version");
		if (version.equals("4.2")) {
			agename = nbttagcompound.getString("AgeName");
			seed = nbttagcompound.getLong("Seed");
			visited = nbttagcompound.getBoolean("Visited");
			worldtime = nbttagcompound.getLong("WorldTime");

			instability = nbttagcompound.getShort("BaseIns");
			instabilityEnabled = nbttagcompound.getBoolean("InstabilityEnabled");

			datacompound = nbttagcompound.getCompoundTag("DataCompound");

			if (nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
				spawn = new ChunkCoordinates(nbttagcompound.getInteger("SpawnX"), nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ"));
			}

			NBTTagList list = null;
			list = nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); ++i) {
				pages.add(Page.createPage(list.getCompoundTagAt(i)));
			}

			list = nbttagcompound.getTagList("Symbols", Constants.NBT.TAG_STRING);
			for (int i = 0; i < list.tagCount(); ++i) {
				symbols.add(list.getStringTagAt(i));
			}

			list = nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING);
			for (int i = 0; i < list.tagCount(); ++i) {
				authors.add(list.getStringTagAt(i));
			}
		} else {
			AgeDataLoader loader = new AgeDataLoaderV4_1(nbttagcompound);

			agename = loader.getAgeName();
			authors = loader.getAuthors();
			seed = loader.getSeed();
			visited = loader.getVisited();
			worldtime = loader.getWorldTime();

			instability = loader.getBaseInstability();
			instabilityEnabled = loader.isInstabilityEnabled();

			spawn = loader.getSpawn();

			pages = loader.getPages();
			symbols = loader.getSymbols();
			datacompound = loader.getDataCompound();

			//Update
			List<String> effects = loader.getEffects();
			for (String deckname : InstabilityManager.getDecks()) {
				//FIXME: !Prioritize old effects?
			}
		}
		if (agename.isEmpty()) {
			agename = "Unnamed Age";
		}
	}

	@Override
	public String getAgeName() {
		return agename;
	}

	@Override
	public Set<String> getAuthors() {
		return authors;
	}

	@Override
	public long getSeed() {
		return seed;
	}

	@Override
	public boolean getVisited() {
		return visited;
	}

	@Override
	public long getWorldTime() {
		return worldtime;
	}

	@Override
	public short getBaseInstability() {
		return instability;
	}

	@Override
	public boolean isInstabilityEnabled() {
		return instabilityEnabled;
	}

	@Override
	public List<ItemStack> getPages() {
		return pages;
	}

	@Override
	public List<String> getSymbols() {
		return symbols;
	}

	@Override
	public List<String> getEffects() {
		return null;
	}

	@Override
	public NBTTagCompound getDataCompound() {
		return datacompound;
	}

	@Override
	public ChunkCoordinates getSpawn() {
		return spawn;
	}
}
