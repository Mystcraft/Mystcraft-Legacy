package com.xcompwiz.mystcraft.world.agedata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class AgeDataLegacy extends AgeDataLoader {

	private String				agename;
	private long				seed;
	private short				instability;
	private boolean				instabilityEnabled;
	private ChunkCoordinates	spawn;
	private List<ItemStack>		pages	= new ArrayList<ItemStack>();
	private List<String>		symbols	= new ArrayList<String>();
	private List<String>		effects	= new ArrayList<String>();
	private boolean				visited;
	private NBTTagCompound		datacompound;
	private long				worldtime;

	public AgeDataLegacy(NBTTagCompound nbttagcompound) {
		agename = nbttagcompound.getString("AgeName");
		seed = nbttagcompound.getLong("Seed");
		visited = nbttagcompound.getBoolean("Visited");
		worldtime = nbttagcompound.getLong("WorldTime");

		if (nbttagcompound.hasKey("BaseIns")) {
			instability = nbttagcompound.getShort("BaseIns");
		} else if (nbttagcompound.hasKey("Instability")) {
			instability = nbttagcompound.getShort("Instability");
		} else {
			instability = nbttagcompound.getShort("Decay"); // Old save handling: Name was remapped to Instability
		}

		if (nbttagcompound.hasKey("InstabilityEnabled")) {
			instabilityEnabled = nbttagcompound.getBoolean("InstabilityEnabled");
		} else {
			instabilityEnabled = true; // Old Save handling: Variable added
		}

		if (nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
			spawn = new ChunkCoordinates(nbttagcompound.getInteger("SpawnX"), nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ"));
		} else {
			spawn = null;
		}

		pages.clear();
		symbols.clear();
		effects.clear();
		if (nbttagcompound.hasKey("Pages")) {
			NBTTagList list = nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound pagedata = list.getCompoundTagAt(i);
				pages.add(Page.createPage(pagedata));
			}
		} else if (nbttagcompound.hasKey("SymbolCount")) { // Old Save handling: Raw symbols
			int symbolcount = nbttagcompound.getInteger("SymbolCount");
			for (int i = 0; i < symbolcount; ++i) {
				if (nbttagcompound.hasKey("Symbol" + i)) {
					pages.add(Page.createSymbolPage(nbttagcompound.getString("Symbol" + i)));
				}
			}
		}
		if (nbttagcompound.hasKey("Symbols")) {
			NBTTagList list = nbttagcompound.getTagList("Symbols", Constants.NBT.TAG_STRING);
			for (int i = 0; i < list.tagCount(); i++) {
				symbols.add(list.getStringTagAt(i));
			}
		} else if (visited) { // Old Save handling: new symbol list structure
			for (ItemStack page : pages) {
				String symbol = Page.getSymbol(page);
				if (symbol != null) {
					symbols.add(symbol);
				}
			}
		}
		if (nbttagcompound.hasKey("Effects")) {
			NBTTagList list = nbttagcompound.getTagList("Effects", Constants.NBT.TAG_STRING);
			for (int i = 0; i < list.tagCount(); i++) {
				effects.add(list.getStringTagAt(i));
			}
		} else if (nbttagcompound.hasKey("EffectsCount")) { // Old save handling: effects save structure
			int effectscount = nbttagcompound.getInteger("EffectsCount");
			for (int i = 0; i < effectscount; ++i) {
				if (nbttagcompound.hasKey("Effect" + i)) {
					effects.add(nbttagcompound.getString("Effect" + i));
				}
			}
		} else { // Old save handling: pre-instability mechanics
			effects.add("decayblack"); // Should be auto-removed by instability system if not necessary
		}

		if (nbttagcompound.hasKey("DataCompound")) {
			datacompound = nbttagcompound.getCompoundTag("DataCompound");
		} else {
			datacompound = new NBTTagCompound(); // Old save handling: Pre-weather
			pages.add(Page.createSymbolPage("WeatherNorm"));
		}

		if (this.visited && (!nbttagcompound.hasKey("Version") || nbttagcompound.getString("Version").equals("3"))) { // Old save handling: pre-sky rewrite
			pages.add(Page.createSymbolPage("NormalStars"));
		}

		// Legacy save model. Old save handling: Legacy
		if (nbttagcompound.hasKey("BiomeCount")) {
			int biomecount = nbttagcompound.getInteger("BiomeCount");
			for (int i = 0; i < biomecount; ++i) {
				if (nbttagcompound.hasKey("Biome" + i)) {
					pages.add(Page.createSymbolPage(BiomeGenBase.getBiome(nbttagcompound.getInteger("Biome" + i)).biomeName));
				}
			}
		}
		if (nbttagcompound.hasKey("AgeType")) {
			pages.add(Page.createSymbolPage("Single Biome"));
			pages.add(Page.createSymbolPage("LightingNormal"));
			pages.add(Page.createSymbolPage("TerrainNormal"));
			pages.add(Page.createSymbolPage("Villages"));
			pages.add(Page.createSymbolPage("Caves"));
			pages.add(Page.createSymbolPage("Ravines"));
			pages.add(Page.createSymbolPage("Lakes"));
			pages.add(Page.createSymbolPage("LavaLakes"));
			pages.add(Page.createSymbolPage("ModFull"));
			pages.add(Page.createSymbolPage("SunNormal"));
			pages.add(Page.createSymbolPage("ModFull"));
			pages.add(Page.createSymbolPage("MoonNormal"));
		}

		// Moves all biome symbols to front in reverse order
		HashSet<String> biomenames = new HashSet<String>();
		for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; ++i) {
			if (BiomeGenBase.getBiome(i) != null) biomenames.add(BiomeGenBase.getBiome(i).biomeName);
		}
		HashSet<IAgeSymbol> controllers = SymbolManager.findAgeSymbolsImplementing(IBiomeController.class);
		HashSet<String> controllernames = new HashSet<String>();
		for (IAgeSymbol symbol : controllers) {
			controllernames.add(symbol.identifier());
		}
		int cpos = 0;
		for (int i = 0; i < pages.size(); ++i) {
			String symbolid = Page.getSymbol(pages.get(i));
			if (biomenames.contains(symbolid)) {
				pages.add(0, pages.remove(i));
				++cpos;
			} else if (controllernames.contains(symbolid)) {
				pages.add(cpos, pages.remove(i));
				++cpos;
			}
		}
		cpos = 0;
		for (int i = 0; i < symbols.size(); ++i) {
			String symbolid = symbols.get(i);
			if (biomenames.contains(symbolid)) {
				symbols.add(0, symbols.remove(i));
				++cpos;
			} else if (controllernames.contains(symbolid)) {
				symbols.add(cpos, symbols.remove(i));
				++cpos;
			}
		}
	}

	@Override
	public String getAgeName() {
		return agename;
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
		return effects;
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
