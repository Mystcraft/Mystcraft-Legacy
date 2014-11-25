package com.xcompwiz.mystcraft.world.agedata;

import java.util.HashSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderManager.AgeDataLoader;

public class AgeDataLegacy extends AgeDataLoader {
	public static class AgeDataData extends com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderV4.AgeDataData {
	}

	@Override
	public AgeDataData load(NBTTagCompound nbttagcompound) {
		AgeDataData data = new AgeDataData();
		data.version = "legacy";
		data.agename = nbttagcompound.getString("AgeName");
		data.seed = nbttagcompound.getLong("Seed");
		data.visited = nbttagcompound.getBoolean("Visited");
		data.worldtime = nbttagcompound.getLong("WorldTime");

		if (nbttagcompound.hasKey("BaseIns")) {
			data.instability = nbttagcompound.getShort("BaseIns");
		} else if (nbttagcompound.hasKey("Instability")) {
			data.instability = nbttagcompound.getShort("Instability");
		} else {
			data.instability = nbttagcompound.getShort("Decay"); // Old save handling: Name was remapped to Instability
		}

		if (nbttagcompound.hasKey("InstabilityEnabled")) {
			data.instabilityEnabled = nbttagcompound.getBoolean("InstabilityEnabled");
		} else {
			data.instabilityEnabled = true; // Old Save handling: Variable added
		}

		if (nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
			data.spawn = new ChunkCoordinates(nbttagcompound.getInteger("SpawnX"), nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ"));
		} else {
			data.spawn = null;
		}

		if (nbttagcompound.hasKey("Pages")) {
			NBTTagList list = nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound pagedata = list.getCompoundTagAt(i);
				data.pages.add(Page.createPage(pagedata));
			}
		} else if (nbttagcompound.hasKey("SymbolCount")) { // Old Save handling: Raw symbols
			int symbolcount = nbttagcompound.getInteger("SymbolCount");
			for (int i = 0; i < symbolcount; ++i) {
				if (nbttagcompound.hasKey("Symbol" + i)) {
					data.pages.add(Page.createSymbolPage(nbttagcompound.getString("Symbol" + i)));
				}
			}
		}
		if (nbttagcompound.hasKey("Symbols")) {
			NBTUtils.readStringCollection(nbttagcompound.getTagList("Symbols", Constants.NBT.TAG_STRING), data.symbols);
		} else if (data.visited) { // Old Save handling: new symbol list structure
			for (ItemStack page : data.pages) {
				String symbol = Page.getSymbol(page);
				if (symbol != null) {
					data.symbols.add(symbol);
				}
			}
		}
		if (nbttagcompound.hasKey("Effects")) {
			NBTUtils.readStringCollection(nbttagcompound.getTagList("Effects", Constants.NBT.TAG_STRING), data.effects);
		} else if (nbttagcompound.hasKey("EffectsCount")) { // Old save handling: effects save structure
			int effectscount = nbttagcompound.getInteger("EffectsCount");
			for (int i = 0; i < effectscount; ++i) {
				if (nbttagcompound.hasKey("Effect" + i)) {
					data.effects.add(nbttagcompound.getString("Effect" + i));
				}
			}
		} else { // Old save handling: pre-instability mechanics
			data.effects.add("decayblack"); // Should be auto-removed by instability system if not necessary
		}

		if (nbttagcompound.hasKey("DataCompound")) {
			data.datacompound = nbttagcompound.getCompoundTag("DataCompound");
		} else {
			data.datacompound = new NBTTagCompound(); // Old save handling: Pre-weather
			data.pages.add(Page.createSymbolPage("WeatherNorm"));
		}

		if (data.visited && (!nbttagcompound.hasKey("Version") || nbttagcompound.getString("Version").equals("3"))) { // Old save handling: pre-sky rewrite
			data.pages.add(Page.createSymbolPage("NormalStars"));
		}

		// Legacy save model. Old save handling: Legacy
		if (nbttagcompound.hasKey("BiomeCount")) {
			int biomecount = nbttagcompound.getInteger("BiomeCount");
			for (int i = 0; i < biomecount; ++i) {
				if (nbttagcompound.hasKey("Biome" + i)) {
					data.pages.add(Page.createSymbolPage(BiomeGenBase.getBiome(nbttagcompound.getInteger("Biome" + i)).biomeName));
				}
			}
		}
		if (nbttagcompound.hasKey("AgeType")) {
			data.pages.add(Page.createSymbolPage("Single Biome"));
			data.pages.add(Page.createSymbolPage("LightingNormal"));
			data.pages.add(Page.createSymbolPage("TerrainNormal"));
			data.pages.add(Page.createSymbolPage("Villages"));
			data.pages.add(Page.createSymbolPage("Caves"));
			data.pages.add(Page.createSymbolPage("Ravines"));
			data.pages.add(Page.createSymbolPage("Lakes"));
			data.pages.add(Page.createSymbolPage("LavaLakes"));
			data.pages.add(Page.createSymbolPage("ModFull"));
			data.pages.add(Page.createSymbolPage("SunNormal"));
			data.pages.add(Page.createSymbolPage("ModFull"));
			data.pages.add(Page.createSymbolPage("MoonNormal"));
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
		for (int i = 0; i < data.pages.size(); ++i) {
			String symbolid = Page.getSymbol(data.pages.get(i));
			if (biomenames.contains(symbolid)) {
				data.pages.add(0, data.pages.remove(i));
				++cpos;
			} else if (controllernames.contains(symbolid)) {
				data.pages.add(cpos, data.pages.remove(i));
				++cpos;
			}
		}
		cpos = 0;
		for (int i = 0; i < data.symbols.size(); ++i) {
			String symbolid = data.symbols.get(i);
			if (biomenames.contains(symbolid)) {
				data.symbols.add(0, data.symbols.remove(i));
				++cpos;
			} else if (controllernames.contains(symbolid)) {
				data.symbols.add(cpos, data.symbols.remove(i));
				++cpos;
			}
		}
		return data;
	}
}
