package com.xcompwiz.mystcraft.data;

import java.util.Iterator;
import java.util.stream.Collectors;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolAngle;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBiome;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolClear;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolGradient;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolHorizonColor;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolLength;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolNoSea;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolPhase;
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
import com.xcompwiz.mystcraft.symbol.symbols.SymbolDummy;
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
import com.xcompwiz.mystcraft.symbol.symbols.SymbolMoonNormal;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolNetherFort;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolObelisks;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolRavines;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolSkylands;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolSpheres;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolSpikes;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarFissure;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarsEndSky;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarsNormal;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStarsTwinkle;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolStrongholds;
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

import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class ModSymbols {

	public static void registerSymbol(SymbolBase symbol, Integer cardrank, String... poem) {
		if (poem.length != 4)
			LoggerUtils.warn("Weird poem length (%d) when registering %s", poem.length, symbol.getRegistryName().toString());
		symbol.setWords(poem);
		symbol.setCardRank(cardrank);
		SymbolManager.tryAddSymbol(symbol);
	}

	public static void generateBiomeSymbols() {
		// Generates symbols for registered biomes
		Iterator<Biome> iterator = Biome.REGISTRY.iterator();
		while (iterator.hasNext()) {
			Biome biome = iterator.next();
			if (biome == null)
				continue;
			ResourceLocation biomeID = biome.getRegistryName();
			//if (biome.getBiomeName() == null) { //If it is null, we have bigger problems..
			//	LoggerUtils.warn("Biome (id " + biomeID.toString() + ") has null name, could not build symbol");
			//	continue;
			//}

			SymbolBase symbol = new SymbolBiome(MystObjects.MystcraftModId, biome);
			if (SymbolManager.tryAddSymbol(symbol)) {
				Integer rank = 1;
				if (biome == Biomes.SKY) {
					rank = null;
				} else {
					symbol.setCardRank(2);
				}
				GrammarGenerator.registerRule(new Rule(GrammarData.BIOME, CollectionUtils.buildList(symbol.getRegistryName()), rank));
				InternalAPI.symbolValues.setSymbolTradeItem(symbol, new ItemStack(Items.EMERALD, 1));
				SymbolBiome.selectables.add(biome);
			}
		}
	}

	public static void initialize() {
		ModSymbolsModifiers.initialize();

		registerSymbol(new SymbolColorCloud(forMyst("ColorCloud")), 1, WordData.Image, WordData.Entropy, WordData.Believe, WordData.Weave);
		registerSymbol(new SymbolColorCloudNatural(forMyst("ColorCloudNat")), 1, WordData.Image, WordData.Entropy, WordData.Believe, WordData.Nature);
		registerSymbol(new SymbolColorFog(forMyst("ColorFog")), 1, WordData.Image, WordData.Entropy, WordData.Explore, WordData.Weave);
		registerSymbol(new SymbolColorFogNatural(forMyst("ColorFogNat")), 1, WordData.Image, WordData.Entropy, WordData.Explore, WordData.Nature);
		registerSymbol(new SymbolColorFoliage(forMyst("ColorFoliage")), 1, WordData.Image, WordData.Growth, WordData.Elevate, WordData.Weave);
		registerSymbol(new SymbolColorFoliageNatural(forMyst("ColorFoliageNat")), 1, WordData.Image, WordData.Growth, WordData.Elevate, WordData.Nature);
		registerSymbol(new SymbolColorGrass(forMyst("ColorGrass")), 1, WordData.Image, WordData.Growth, WordData.Resilience, WordData.Weave);
		registerSymbol(new SymbolColorGrassNatural(forMyst("ColorGrassNat")), 1, WordData.Image, WordData.Growth, WordData.Resilience, WordData.Nature);
		registerSymbol(new SymbolColorSky(forMyst("ColorSky")), 1, WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Weave);
		registerSymbol(new SymbolColorSkyNatural(forMyst("ColorSkyNat")), 1, WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Nature);
		registerSymbol(new SymbolColorSkyNight(forMyst("ColorSkyNight")), 1, WordData.Image, WordData.Celestial, WordData.Contradict, WordData.Weave);
		registerSymbol(new SymbolColorWater(forMyst("ColorWater")), 1, WordData.Image, WordData.Flow, WordData.Constraint, WordData.Weave);
		registerSymbol(new SymbolColorWaterNatural(forMyst("ColorWaterNat")), 1, WordData.Image, WordData.Flow, WordData.Constraint, WordData.Nature);
		registerSymbol(new SymbolDoodadRainbow(forMyst("Rainbow")), 1, WordData.Celestial, WordData.Image, WordData.Harmony, WordData.Balance);
		registerSymbol(new SymbolHideHorizon(forMyst("NoHorizon")), 1, WordData.Celestial, WordData.Inhibit, WordData.Image, WordData.Void);
		registerSymbol(new SymbolDummy(forMyst("MoonDark")), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Wisdom);
		registerSymbol(new SymbolMoonNormal(forMyst("MoonNormal")), 1, WordData.Celestial, WordData.Image, WordData.Cycle, WordData.Wisdom);
		registerSymbol(new SymbolDummy(forMyst("StarsDark")), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Order);
		registerSymbol(new SymbolStarsEndSky(forMyst("StarsEndSky")), 1, WordData.Celestial, WordData.Image, WordData.Chaos, WordData.Weave);
		registerSymbol(new SymbolStarsNormal(forMyst("StarsNormal")), 1, WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Order);
		registerSymbol(new SymbolStarsTwinkle(forMyst("StarsTwinkle")), 1, WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Entropy);
		registerSymbol(new SymbolDummy(forMyst("SunDark")), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Energy);
		registerSymbol(new SymbolSunNormal(forMyst("SunNormal")), 2, WordData.Celestial, WordData.Image, WordData.Stimulate, WordData.Energy);
		registerSymbol(new SymbolBiomeControllerGrid(forMyst("BioConGrid")), 3, WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Mutual);
		registerSymbol(new SymbolBiomeControllerNative(forMyst("BioConNative")), 3, WordData.Constraint, WordData.Nature, WordData.Tradition, WordData.Sustain);
		registerSymbol(new SymbolBiomeControllerSingle(forMyst("BioConSingle")), 3, WordData.Constraint, WordData.Nature, WordData.Infinite, WordData.Static);
		registerSymbol(new SymbolBiomeControllerTiled(forMyst("BioConTiled")), 3, WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Contradict);
		registerSymbol(new SymbolBiomeControllerHuge(forMyst("BioConHuge")), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Huge");
		registerSymbol(new SymbolBiomeControllerLarge(forMyst("BioConLarge")), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Large");
		registerSymbol(new SymbolBiomeControllerMedium(forMyst("BioConMedium")), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Medium");
		registerSymbol(new SymbolBiomeControllerSmall(forMyst("BioConSmall")), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Small");
		registerSymbol(new SymbolBiomeControllerTiny(forMyst("BioConTiny")), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Tiny");
		registerSymbol(new SymbolNoSea(forMyst("NoSea")), 2, WordData.Modifier, WordData.Constraint, WordData.Flow, WordData.Inhibit);
		registerSymbol(new SymbolAntiPvP(forMyst("PvPOff")), null, WordData.Chain, WordData.Chaos, WordData.Encourage, WordData.Harmony);
		registerSymbol(new SymbolEnvAccelerated(forMyst("EnvAccel")), 3, WordData.Environment, WordData.Dynamic, WordData.Change, WordData.Spur);
		registerSymbol(new SymbolEnvExplosions(forMyst("EnvExplosions")), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Force);
		registerSymbol(new SymbolEnvLightning(forMyst("EnvLightning")), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Energy);
		registerSymbol(new SymbolEnvMeteor(forMyst("EnvMeteor")), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Momentum);
		registerSymbol(new SymbolEnvScorched(forMyst("EnvScorch")), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Chaos);
		registerSymbol(new SymbolLightingBright(forMyst("LightingBright")), 3, WordData.Ethereal, WordData.Power, WordData.Infinite, WordData.Spur);
		registerSymbol(new SymbolLightingDark(forMyst("LightingDark")), 3, WordData.Ethereal, WordData.Void, WordData.Constraint, WordData.Inhibit);
		registerSymbol(new SymbolLightingNormal(forMyst("LightingNormal")), 2, WordData.Ethereal, WordData.Dynamic, WordData.Cycle, WordData.Balance);
		registerSymbol(new SymbolAngle(forMyst("ModNorth"), 000.0F, "North"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Control);
		registerSymbol(new SymbolAngle(forMyst("ModEast"), 090.0F, "East"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Tradition);
		registerSymbol(new SymbolAngle(forMyst("ModSouth"), 180.0F, "South"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Chaos);
		registerSymbol(new SymbolAngle(forMyst("ModWest"), 270.0F, "West"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Change);
		registerSymbol(new SymbolClear(forMyst("ModClear")), 0, WordData.Contradict, WordData.Transform, WordData.Change, WordData.Void);
		registerSymbol(new SymbolGradient(forMyst("ModGradient")), 1, WordData.Modifier, WordData.Image, WordData.Merge, WordData.Weave);
		registerSymbol(new SymbolHorizonColor(forMyst("ColorHorizon")), 0, WordData.Modifier, WordData.Image, WordData.Celestial, WordData.Change);
		registerSymbol(new SymbolLength(forMyst("ModZero"), 0.0F, "Zero Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Inhibit);
		registerSymbol(new SymbolLength(forMyst("ModHalf"), 0.5F, "Half Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Stimulate);
		registerSymbol(new SymbolLength(forMyst("ModFull"), 1.0F, "Full Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Balance);
		registerSymbol(new SymbolLength(forMyst("ModDouble"), 2.0F, "Double Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Sacrifice);
		registerSymbol(new SymbolPhase(forMyst("ModEnd"), 000F, "Nadir"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Rebirth);
		registerSymbol(new SymbolPhase(forMyst("ModRising"), 090F, "Rising"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Growth);
		registerSymbol(new SymbolPhase(forMyst("ModNoon"), 180F, "Zenith"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Harmony);
		registerSymbol(new SymbolPhase(forMyst("ModSetting"), 270F, "Setting"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Future);
		registerSymbol(new SymbolCaves(forMyst("Caves")), 2, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Flow);
		registerSymbol(new SymbolDungeons(forMyst("Dungeons")), 2, WordData.Civilization, WordData.Constraint, WordData.Chain, WordData.Resurrect);
		registerSymbol(new SymbolFloatingIslands(forMyst("FloatIslands")), 3, WordData.Terrain, WordData.Transform, WordData.Form, WordData.Celestial);
		registerSymbol(new SymbolDummy(forMyst("FeatureLargeDummy"), InstabilityData.symbol.dummyFeatureLarge), 4, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Terrain);
		registerSymbol(new SymbolDummy(forMyst("FeatureMediumDummy"), InstabilityData.symbol.dummyFeatureMedium), 4, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Balance);
		registerSymbol(new SymbolDummy(forMyst("FeatureSmallDummy"), InstabilityData.symbol.dummyFeatureSmall), 5, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Form);
		registerSymbol(new SymbolHugeTrees(forMyst("HugeTrees")), 2, WordData.Nature, WordData.Stimulate, WordData.Spur, WordData.Elevate);
		registerSymbol(new SymbolLakesDeep(forMyst("LakesDeep")), 3, WordData.Nature, WordData.Flow, WordData.Static, WordData.Explore);
		registerSymbol(new SymbolLakesSurface(forMyst("LakesSurface")), 3, WordData.Nature, WordData.Flow, WordData.Static, WordData.Elevate);
		registerSymbol(new SymbolMineshafts(forMyst("Mineshafts")), 3, WordData.Civilization, WordData.Machine, WordData.Motion, WordData.Tradition);
		registerSymbol(new SymbolNetherFort(forMyst("NetherFort")), 3, WordData.Civilization, WordData.Machine, WordData.Power, WordData.Entropy);
		registerSymbol(new SymbolObelisks(forMyst("Obelisks")), 3, WordData.Civilization, WordData.Resilience, WordData.Static, WordData.Form);
		registerSymbol(new SymbolRavines(forMyst("Ravines")), 2, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Weave);
		registerSymbol(new SymbolSpheres(forMyst("TerModSpheres")), 2, WordData.Terrain, WordData.Transform, WordData.Form, WordData.Cycle);
		registerSymbol(new SymbolSpikes(forMyst("GenSpikes")), 3, WordData.Nature, WordData.Encourage, WordData.Entropy, WordData.Structure);
		registerSymbol(new SymbolStrongholds(forMyst("Strongholds")), 3, WordData.Civilization, WordData.Wisdom, WordData.Future, WordData.Honor);
		registerSymbol(new SymbolTendrils(forMyst("Tendrils")), 3, WordData.Terrain, WordData.Transform, WordData.Growth, WordData.Flow);
		registerSymbol(new SymbolVillages(forMyst("Villages")), 3, WordData.Civilization, WordData.Society, WordData.Harmony, WordData.Nurture);
		registerSymbol(new SymbolCrystalFormation(forMyst("CryForm")), 3, WordData.Nature, WordData.Encourage, WordData.Growth, WordData.Structure);
		registerSymbol(new SymbolSkylands(forMyst("Skylands")), 3, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Elevate);
		registerSymbol(new SymbolStarFissure(forMyst("StarFissure")), 3, WordData.Nature, WordData.Harmony, WordData.Mutual, WordData.Void);
		registerSymbol(new SymbolDenseOres(forMyst("DenseOres")), 5, WordData.Environment, WordData.Stimulate, WordData.Machine, WordData.Chaos);
		registerSymbol(new SymbolWeatherAlways(forMyst("WeatherOn")), 3, WordData.Sustain, WordData.Static, WordData.Tradition, WordData.Stimulate);
		registerSymbol(new SymbolWeatherCloudy(forMyst("WeatherCloudy")), 3, WordData.Sustain, WordData.Static, WordData.Believe, WordData.Motion);
		registerSymbol(new SymbolWeatherFast(forMyst("WeatherFast")), 3, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Spur);
		registerSymbol(new SymbolWeatherNormal(forMyst("WeatherNorm")), 2, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Balance);
		registerSymbol(new SymbolWeatherOff(forMyst("WeatherOff")), 3, WordData.Sustain, WordData.Static, WordData.Stimulate, WordData.Energy);
		registerSymbol(new SymbolWeatherRain(forMyst("WeatherRain")), 3, WordData.Sustain, WordData.Static, WordData.Rebirth, WordData.Growth);
		registerSymbol(new SymbolWeatherSlow(forMyst("WeatherSlow")), 3, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Inhibit);
		registerSymbol(new SymbolWeatherSnow(forMyst("WeatherSnow")), 3, WordData.Sustain, WordData.Static, WordData.Inhibit, WordData.Energy);
		registerSymbol(new SymbolWeatherStorm(forMyst("WeatherStorm")), 3, WordData.Sustain, WordData.Static, WordData.Nature, WordData.Power);
		registerSymbol(new SymbolTerrainGenAmplified(forMyst("TerrainAmplified")), 3, WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Spur);
		registerSymbol(new SymbolTerrainGenEnd(forMyst("TerrainEnd")), 4, WordData.Terrain, WordData.Form, WordData.Ethereal, WordData.Flow);
		registerSymbol(new SymbolTerrainGenFlat(forMyst("TerrainFlat")), 3, WordData.Terrain, WordData.Form, WordData.Inhibit, WordData.Motion);
		registerSymbol(new SymbolTerrainGenNether(forMyst("TerrainNether")), 4, WordData.Terrain, WordData.Form, WordData.Constraint, WordData.Entropy);
		registerSymbol(new SymbolTerrainGenNormal(forMyst("TerrainNormal")), 2, WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Flow);
		registerSymbol(new SymbolTerrainGenVoid(forMyst("TerrainVoid")), 4, WordData.Terrain, WordData.Form, WordData.Infinite, WordData.Void);
	}

	private static ResourceLocation forMyst(String name) {
		return new ResourceLocation(MystObjects.MystcraftModId, name);
	}

}
