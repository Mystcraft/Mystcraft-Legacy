package com.xcompwiz.mystcraft.data;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBiome;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierHorizonColor;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolAntiPvP;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerGrid;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerHuge;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerLarge;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerMedium;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerNative;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerSingle;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerSmall;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerTiled;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolBiomeControllerTiny;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolCaves;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorCloud;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorCloudNatural;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorFog;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorFogNatural;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorFoliage;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorFoliageNatural;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorGrass;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorGrassNatural;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorSky;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorSkyNatural;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorSkyNight;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorWater;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolColorWaterNatural;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolCrystalFormation;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolDenseOres;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolDoodadRainbow;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolDungeons;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolEnvAccelerated;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolEnvExplosions;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolEnvLightning;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolEnvMeteor;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolEnvScorched;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolFloatingIslands;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolHideHorizon;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolHugeTrees;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolLakesDeep;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolLakesSurface;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolLightingBright;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolLightingDark;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolLightingNormal;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolMineshafts;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolMoonDark;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolMoonNormal;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolNetherFort;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolObelisks;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolRavines;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolSkylands;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolSpheres;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolSpikes;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarFissure;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarsDark;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarsEndSky;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarsNormal;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarsTwinkle;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStrongholds;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolSunDark;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolSunNormal;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolTendrils;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolTerrainGenAmplified;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolTerrainGenEnd;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolTerrainGenFlat;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolTerrainGenNether;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolTerrainGenNormal;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolTerrainGenVoid;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolVillages;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherAlways;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherCloudy;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherFast;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherNormal;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherOff;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherRain;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherSlow;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherSnow;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolWeatherStorm;
import com.xcompwiz.util.CollectionUtils;

public class SymbolData {

	public static void initialize() {
		SymbolDataModifiers.initialize();

		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerSingle()));
		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerTiled()));
		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerGrid()));
		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerNative()));
		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerTiny()));
		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerSmall()));
		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerMedium()));
		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerLarge()));
		InternalAPI.symbol.registerSymbol((new SymbolBiomeControllerHuge()));

		InternalAPI.symbol.registerSymbol((new SymbolTerrainGenNormal()));
		InternalAPI.symbol.registerSymbol((new SymbolTerrainGenFlat()));
		InternalAPI.symbol.registerSymbol((new SymbolTerrainGenVoid()));
		InternalAPI.symbol.registerSymbol((new SymbolTerrainGenNether()));
		InternalAPI.symbol.registerSymbol((new SymbolTerrainGenEnd()));
		InternalAPI.symbol.registerSymbol((new SymbolTerrainGenAmplified()));

		InternalAPI.symbol.registerSymbol((new SymbolLightingNormal()));
		InternalAPI.symbol.registerSymbol((new SymbolLightingDark()));
		InternalAPI.symbol.registerSymbol((new SymbolLightingBright()));

		InternalAPI.symbol.registerSymbol((new SymbolStarsDark()));
		InternalAPI.symbol.registerSymbol((new SymbolStarsNormal()));
		InternalAPI.symbol.registerSymbol((new SymbolStarsTwinkle()));
		InternalAPI.symbol.registerSymbol((new SymbolStarsEndSky()));

		InternalAPI.symbol.registerSymbol((new SymbolDoodadRainbow()));
		InternalAPI.symbol.registerSymbol((new SymbolSunDark()));
		InternalAPI.symbol.registerSymbol((new SymbolSunNormal()));
		InternalAPI.symbol.registerSymbol((new SymbolMoonDark()));
		InternalAPI.symbol.registerSymbol((new SymbolMoonNormal()));

		InternalAPI.symbol.registerSymbol((new SymbolWeatherSlow()));
		InternalAPI.symbol.registerSymbol((new SymbolWeatherNormal()));
		InternalAPI.symbol.registerSymbol((new SymbolWeatherFast()));
		InternalAPI.symbol.registerSymbol((new SymbolWeatherOff()));
		InternalAPI.symbol.registerSymbol((new SymbolWeatherStorm()));
		InternalAPI.symbol.registerSymbol((new SymbolWeatherRain()));
		InternalAPI.symbol.registerSymbol((new SymbolWeatherSnow()));
		InternalAPI.symbol.registerSymbol((new SymbolWeatherAlways()));
		InternalAPI.symbol.registerSymbol((new SymbolWeatherCloudy()));

		InternalAPI.symbol.registerSymbol((new ModifierHorizonColor()));
		InternalAPI.symbol.registerSymbol((new SymbolColorSky()));
		InternalAPI.symbol.registerSymbol((new SymbolColorSkyNight()));
		InternalAPI.symbol.registerSymbol((new SymbolColorCloud()));
		InternalAPI.symbol.registerSymbol((new SymbolColorFog()));
		InternalAPI.symbol.registerSymbol((new SymbolColorGrass()));
		InternalAPI.symbol.registerSymbol((new SymbolColorWater()));
		InternalAPI.symbol.registerSymbol((new SymbolColorFoliage()));

		InternalAPI.symbol.registerSymbol((new SymbolColorSkyNatural()));
		InternalAPI.symbol.registerSymbol((new SymbolColorCloudNatural()));
		InternalAPI.symbol.registerSymbol((new SymbolColorFogNatural()));
		InternalAPI.symbol.registerSymbol((new SymbolColorGrassNatural()));
		InternalAPI.symbol.registerSymbol((new SymbolColorWaterNatural()));
		InternalAPI.symbol.registerSymbol((new SymbolColorFoliageNatural()));

		InternalAPI.symbol.registerSymbol((new SymbolSpikes()));
		InternalAPI.symbol.registerSymbol((new SymbolLakesSurface()));
		InternalAPI.symbol.registerSymbol((new SymbolLakesDeep()));
		InternalAPI.symbol.registerSymbol((new SymbolHugeTrees()));
		InternalAPI.symbol.registerSymbol((new SymbolCrystalFormation()));
		InternalAPI.symbol.registerSymbol((new SymbolStarFissure()));
		InternalAPI.symbol.registerSymbol((new SymbolFloatingIslands()));

		InternalAPI.symbol.registerSymbol((new SymbolObelisks()));
		InternalAPI.symbol.registerSymbol((new SymbolDungeons()));
		InternalAPI.symbol.registerSymbol((new SymbolMineshafts()));
		InternalAPI.symbol.registerSymbol((new SymbolVillages()));
		InternalAPI.symbol.registerSymbol((new SymbolStrongholds()));
		InternalAPI.symbol.registerSymbol((new SymbolNetherFort()));

		InternalAPI.symbol.registerSymbol((new SymbolSkylands()));
		InternalAPI.symbol.registerSymbol((new SymbolCaves()));
		InternalAPI.symbol.registerSymbol((new SymbolRavines()));
		InternalAPI.symbol.registerSymbol((new SymbolTendrils()));
		InternalAPI.symbol.registerSymbol((new SymbolSpheres()));

		InternalAPI.symbol.registerSymbol((new SymbolHideHorizon()));
		InternalAPI.symbol.registerSymbol((new SymbolAntiPvP()));

		InternalAPI.symbol.registerSymbol((new SymbolEnvAccelerated()));
		InternalAPI.symbol.registerSymbol((new SymbolEnvExplosions()));
		InternalAPI.symbol.registerSymbol((new SymbolEnvLightning()));
		InternalAPI.symbol.registerSymbol((new SymbolEnvMeteor()));
		InternalAPI.symbol.registerSymbol((new SymbolEnvScorched()));
		InternalAPI.symbol.registerSymbol((new SymbolDenseOres()));
	}

	public static void generateBiomeSymbols() {
		// Generates symbols for registered biomes
		for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; ++i) {
			BiomeGenBase biome = BiomeGenBase.getBiome(i);
			if (biome == null) continue;
			if (biome.biomeName == null) {
				LoggerUtils.warn("Biome (id " + i + ") has null name, could not build symbol");
				continue;
			}
			if (biome.biomeID != i) {
				LoggerUtils.warn("Biome (id " + biome.biomeID + ") is in list as element " + i + ".");
				continue;
			}

			SymbolBase symbol = (new ModifierBiome(biome));
			if (InternalAPI.symbol.registerSymbol(symbol)) {
				float weight = 1.0F;
				if (biome == BiomeGenBase.sky) {
					weight = 0;
				}
				GrammarGenerator.registerRule(new Rule(IGrammarAPI.BIOME, CollectionUtils.buildList(symbol.identifier()), weight));
				symbol.setRarity(0.9F);
				InternalAPI.symbolValues.setSymbolTradeItem(symbol, new ItemStack(Items.emerald, 1));
				ModifierBiome.selectables.add(biome);
			}

			// Handle remappings
			if (!SymbolRemappings.hasRemapping(biome.biomeName)) {
				SymbolRemappings.addSymbolRemapping(biome.biomeName, symbol.identifier());
			}
		}
	}
}
