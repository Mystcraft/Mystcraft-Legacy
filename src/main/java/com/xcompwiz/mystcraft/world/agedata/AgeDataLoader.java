package com.xcompwiz.mystcraft.world.agedata;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

public abstract class AgeDataLoader {

	public abstract String getAgeName();

	public Set<String> getAuthors() {
		return null;
	}

	public abstract long getSeed();

	public abstract boolean getVisited();

	public abstract long getWorldTime();

	public abstract short getBaseInstability();

	public abstract boolean isInstabilityEnabled();

	public abstract ChunkCoordinates getSpawn();

	public abstract List<ItemStack> getPages();

	public abstract List<String> getSymbols();

	public HashMap<String, Collection<String>> getDecks() {
		return null;
	}

	public abstract List<String> getEffects();

	public abstract NBTTagCompound getDataCompound();
}
