package com.xcompwiz.mystcraft.data;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierAngle;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBiome;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierClear;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierGradient;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierHorizonColor;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierLength;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierNoSea;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierPhase;
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

public class ModSymbols {

	public static void registerSymbol(String identifier, SymbolBase symbol, Integer cardrank, String... poem) {
		if (!symbol.identifier().equals(identifier)) LoggerUtils.error("XComp done screwed up. Give him this message. (%s != %s)", identifier, symbol.identifier());
		if (poem.length != 4) LoggerUtils.warn("Weird poem length (%d) when registering %s", poem.length, identifier);
		symbol.setWords(poem);
		symbol.setCardRank(cardrank);
		InternalAPI.symbol.registerSymbol(symbol);
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
				Integer rank = 1;
				if (biome == BiomeGenBase.sky) {
					rank = null;
				} else {
					symbol.setCardRank(2);
				}
				GrammarGenerator.registerRule(new Rule(GrammarData.BIOME, CollectionUtils.buildList(symbol.identifier()), rank));
				InternalAPI.symbolValues.setSymbolTradeItem(symbol, new ItemStack(Items.emerald, 1));
				ModifierBiome.selectables.add(biome);
			}

			// Handle remappings
			if (!SymbolRemappings.hasRemapping(biome.biomeName)) {
				SymbolRemappings.addSymbolRemapping(biome.biomeName, symbol.identifier());
			}
		}
	}

	public static void initialize() {
		ModSymbolsModifiers.initialize();

		registerSymbol("ColorCloud", new SymbolColorCloud(), 1, WordData.Image, WordData.Entropy, WordData.Believe, WordData.Weave);
		registerSymbol("ColorCloudNat", new SymbolColorCloudNatural(), 1, WordData.Image, WordData.Entropy, WordData.Believe, WordData.Nature);
		registerSymbol("ColorFog", new SymbolColorFog(), 1, WordData.Image, WordData.Entropy, WordData.Explore, WordData.Weave);
		registerSymbol("ColorFogNat", new SymbolColorFogNatural(), 1, WordData.Image, WordData.Entropy, WordData.Explore, WordData.Nature);
		registerSymbol("ColorFoliage", new SymbolColorFoliage(), 1, WordData.Image, WordData.Growth, WordData.Elevate, WordData.Weave);
		registerSymbol("ColorFoliageNat", new SymbolColorFoliageNatural(), 1, WordData.Image, WordData.Growth, WordData.Elevate, WordData.Nature);
		registerSymbol("ColorGrass", new SymbolColorGrass(), 1, WordData.Image, WordData.Growth, WordData.Resilience, WordData.Weave);
		registerSymbol("ColorGrassNat", new SymbolColorGrassNatural(), 1, WordData.Image, WordData.Growth, WordData.Resilience, WordData.Nature);
		registerSymbol("ColorSky", new SymbolColorSky(), 1, WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Weave);
		registerSymbol("ColorSkyNat", new SymbolColorSkyNatural(), 1, WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Nature);
		registerSymbol("ColorSkyNight", new SymbolColorSkyNight(), 1, WordData.Image, WordData.Celestial, WordData.Contradict, WordData.Weave);
		registerSymbol("ColorWater", new SymbolColorWater(), 1, WordData.Image, WordData.Flow, WordData.Constraint, WordData.Weave);
		registerSymbol("ColorWaterNat", new SymbolColorWaterNatural(), 1, WordData.Image, WordData.Flow, WordData.Constraint, WordData.Nature);
		registerSymbol("Rainbow", new SymbolDoodadRainbow(), 1, WordData.Celestial, WordData.Image, WordData.Harmony, WordData.Balance);
		registerSymbol("NoHorizon", new SymbolHideHorizon(), 1, WordData.Celestial, WordData.Inhibit, WordData.Image, WordData.Void);
		registerSymbol("MoonDark", new SymbolMoonDark(), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Wisdom);
		registerSymbol("MoonNormal", new SymbolMoonNormal(), 1, WordData.Celestial, WordData.Image, WordData.Cycle, WordData.Wisdom);
		registerSymbol("StarsDark", new SymbolStarsDark(), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Order);
		registerSymbol("StarsEndSky", new SymbolStarsEndSky(), 1, WordData.Celestial, WordData.Image, WordData.Chaos, WordData.Weave);
		registerSymbol("StarsNormal", new SymbolStarsNormal(), 1, WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Order);
		registerSymbol("StarsTwinkle", new SymbolStarsTwinkle(), 1, WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Entropy);
		registerSymbol("SunDark", new SymbolSunDark(), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Energy);
		registerSymbol("BioConGrid", new SymbolBiomeControllerGrid(), 3, WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Mutual);
		registerSymbol("BioConNative", new SymbolBiomeControllerNative(), 3, WordData.Constraint, WordData.Nature, WordData.Tradition, WordData.Sustain);
		registerSymbol("BioConSingle", new SymbolBiomeControllerSingle(), 3, WordData.Constraint, WordData.Nature, WordData.Infinite, WordData.Static);
		registerSymbol("BioConTiled", new SymbolBiomeControllerTiled(), 3, WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Contradict);
		registerSymbol("BioConHuge", new SymbolBiomeControllerHuge(), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Huge");
		registerSymbol("BioConLarge", new SymbolBiomeControllerLarge(), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Large");
		registerSymbol("BioConMedium", new SymbolBiomeControllerMedium(), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Medium");
		registerSymbol("BioConSmall", new SymbolBiomeControllerSmall(), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Small");
		registerSymbol("BioConTiny", new SymbolBiomeControllerTiny(), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Tiny");
		registerSymbol("NoSea", new ModifierNoSea(), 2, WordData.Modifier, WordData.Constraint, WordData.Flow, WordData.Inhibit);
		registerSymbol("PvPOff", new SymbolAntiPvP(), null, WordData.Chain, WordData.Chaos, WordData.Encourage, WordData.Harmony);
		registerSymbol("EnvAccel", new SymbolEnvAccelerated(), 3, WordData.Environment, WordData.Dynamic, WordData.Change, WordData.Spur);
		registerSymbol("EnvExplosions", new SymbolEnvExplosions(), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Force);
		registerSymbol("EnvLightning", new SymbolEnvLightning(), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Energy);
		registerSymbol("EnvMeteor", new SymbolEnvMeteor(), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Momentum);
		registerSymbol("EnvScorch", new SymbolEnvScorched(), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Chaos);
		registerSymbol("SunNormal", new SymbolSunNormal(), 2, WordData.Celestial, WordData.Image, WordData.Stimulate, WordData.Energy);
		registerSymbol("LightingBright", new SymbolLightingBright(), 3, WordData.Ethereal, WordData.Power, WordData.Infinite, WordData.Spur);
		registerSymbol("LightingDark", new SymbolLightingDark(), 3, WordData.Ethereal, WordData.Void, WordData.Constraint, WordData.Inhibit);
		registerSymbol("LightingNormal", new SymbolLightingNormal(), 2, WordData.Ethereal, WordData.Dynamic, WordData.Cycle, WordData.Balance);
		registerSymbol("ModNorth", new ModifierAngle(000.0F, "ModNorth", "North"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Control);
		registerSymbol("ModEast", new ModifierAngle(090.0F, "ModEast", "East"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Tradition);
		registerSymbol("ModSouth", new ModifierAngle(180.0F, "ModSouth", "South"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Chaos);
		registerSymbol("ModWest", new ModifierAngle(270.0F, "ModWest", "West"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Change);
		registerSymbol("ModClear", new ModifierClear(), 0, WordData.Contradict, WordData.Transform, WordData.Change, WordData.Void);
		registerSymbol("ModGradient", new ModifierGradient(), 0, WordData.Modifier, WordData.Image, WordData.Merge, WordData.Weave);
		registerSymbol("ColorHorizon", new ModifierHorizonColor(), 0, WordData.Modifier, WordData.Image, WordData.Celestial, WordData.Change);
		registerSymbol("ModZero", new ModifierLength(0.0F, "ModZero", "Zero Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Inhibit);
		registerSymbol("ModHalf", new ModifierLength(0.5F, "ModHalf", "Half Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Stimulate);
		registerSymbol("ModFull", new ModifierLength(1.0F, "ModFull", "Full Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Balance);
		registerSymbol("ModDouble", new ModifierLength(2.0F, "ModDouble", "Double Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Sacrifice);
		registerSymbol("ModEnd", new ModifierPhase(000F, "ModEnd", "Nadir"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Rebirth);
		registerSymbol("ModRising", new ModifierPhase(090F, "ModRising", "Rising"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Growth);
		registerSymbol("ModNoon", new ModifierPhase(180F, "ModNoon", "Zenith"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Harmony);
		registerSymbol("ModSetting", new ModifierPhase(270F, "ModSetting", "Setting"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Future);
		registerSymbol("Caves", new SymbolCaves(), 2, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Flow);
		registerSymbol("Dungeons", new SymbolDungeons(), 2, WordData.Civilization, WordData.Constraint, WordData.Chain, WordData.Resurrect);
		registerSymbol("FloatIslands", new SymbolFloatingIslands(), 3, WordData.Terrain, WordData.Transform, WordData.Form, WordData.Celestial);
		registerSymbol("HugeTrees", new SymbolHugeTrees(), 2, WordData.Nature, WordData.Stimulate, WordData.Spur, WordData.Elevate);
		registerSymbol("LakesDeep", new SymbolLakesDeep(), 3, WordData.Nature, WordData.Flow, WordData.Static, WordData.Explore);
		registerSymbol("LakesSurface", new SymbolLakesSurface(), 3, WordData.Nature, WordData.Flow, WordData.Static, WordData.Elevate);
		registerSymbol("Mineshafts", new SymbolMineshafts(), 3, WordData.Civilization, WordData.Machine, WordData.Motion, WordData.Tradition);
		registerSymbol("NetherFort", new SymbolNetherFort(), 3, WordData.Civilization, WordData.Machine, WordData.Power, WordData.Entropy);
		registerSymbol("Obelisks", new SymbolObelisks(), 3, WordData.Civilization, WordData.Resilience, WordData.Static, WordData.Form);
		registerSymbol("Ravines", new SymbolRavines(), 2, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Weave);
		registerSymbol("TerModSpheres", new SymbolSpheres(), 2, WordData.Terrain, WordData.Transform, WordData.Form, WordData.Cycle);
		registerSymbol("GenSpikes", new SymbolSpikes(), 3, WordData.Nature, WordData.Encourage, WordData.Entropy, WordData.Structure);
		registerSymbol("Strongholds", new SymbolStrongholds(), 3, WordData.Civilization, WordData.Wisdom, WordData.Future, WordData.Honor);
		registerSymbol("Tendrils", new SymbolTendrils(), 3, WordData.Terrain, WordData.Transform, WordData.Growth, WordData.Flow);
		registerSymbol("Villages", new SymbolVillages(), 3, WordData.Civilization, WordData.Society, WordData.Harmony, WordData.Nurture);
		registerSymbol("CryForm", new SymbolCrystalFormation(), 3, WordData.Nature, WordData.Encourage, WordData.Growth, WordData.Structure);
		registerSymbol("Skylands", new SymbolSkylands(), 3, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Elevate);
		registerSymbol("StarFissure", new SymbolStarFissure(), 3, WordData.Nature, WordData.Harmony, WordData.Mutual, WordData.Void);
		registerSymbol("DenseOres", new SymbolDenseOres(), 5, WordData.Environment, WordData.Stimulate, WordData.Machine, WordData.Chaos);
		registerSymbol("WeatherOn", new SymbolWeatherAlways(), 3, WordData.Sustain, WordData.Static, WordData.Tradition, WordData.Stimulate);
		registerSymbol("WeatherCloudy", new SymbolWeatherCloudy(), 3, WordData.Sustain, WordData.Static, WordData.Believe, WordData.Motion);
		registerSymbol("WeatherFast", new SymbolWeatherFast(), 3, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Spur);
		registerSymbol("WeatherNorm", new SymbolWeatherNormal(), 2, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Balance);
		registerSymbol("WeatherOff", new SymbolWeatherOff(), 3, WordData.Sustain, WordData.Static, WordData.Stimulate, WordData.Energy);
		registerSymbol("WeatherRain", new SymbolWeatherRain(), 3, WordData.Sustain, WordData.Static, WordData.Rebirth, WordData.Growth);
		registerSymbol("WeatherSlow", new SymbolWeatherSlow(), 3, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Inhibit);
		registerSymbol("WeatherSnow", new SymbolWeatherSnow(), 3, WordData.Sustain, WordData.Static, WordData.Inhibit, WordData.Energy);
		registerSymbol("WeatherStorm", new SymbolWeatherStorm(), 3, WordData.Sustain, WordData.Static, WordData.Nature, WordData.Power);
		registerSymbol("TerrainAmplified", new SymbolTerrainGenAmplified(), 3, WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Spur);
		registerSymbol("TerrainEnd", new SymbolTerrainGenEnd(), 4, WordData.Terrain, WordData.Form, WordData.Ethereal, WordData.Flow);
		registerSymbol("Flat", new SymbolTerrainGenFlat(), 3, WordData.Terrain, WordData.Form, WordData.Inhibit, WordData.Motion);
		registerSymbol("TerrainNether", new SymbolTerrainGenNether(), 4, WordData.Terrain, WordData.Form, WordData.Constraint, WordData.Entropy);
		registerSymbol("TerrainNormal", new SymbolTerrainGenNormal(), 2, WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Flow);
		registerSymbol("Void", new SymbolTerrainGenVoid(), 4, WordData.Terrain, WordData.Form, WordData.Infinite, WordData.Void);
	}
}
