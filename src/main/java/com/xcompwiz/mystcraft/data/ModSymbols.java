package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

public class ModSymbols {

	public static void registerSymbol(SymbolBase symbol, Integer cardrank, String... poem) {
		if (poem.length != 4) LoggerUtils.warn("Weird poem length (%d) when registering %s", poem.length, symbol.identifier());
		symbol.setWords(poem);
		symbol.setCardRank(cardrank);
		InternalAPI.symbol.registerSymbol(symbol, MystObjects.MystcraftModId);
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

			SymbolBase symbol = (new SymbolBiome(biome));
			if (InternalAPI.symbol.registerSymbol(symbol, MystObjects.MystcraftModId)) {
				Integer rank = 1;
				if (biome == BiomeGenBase.sky) {
					rank = null;
				} else {
					symbol.setCardRank(2);
				}
				GrammarGenerator.registerRule(new Rule(GrammarData.BIOME, CollectionUtils.buildList(symbol.identifier()), rank));
				InternalAPI.symbolValues.setSymbolTradeItem(symbol, new ItemStack(Items.emerald, 1));
				SymbolBiome.selectables.add(biome);
			}
		}
	}

	public static void initialize() {
		ModSymbolsModifiers.initialize();

		registerSymbol(new SymbolColorCloud("ColorCloud"), 1, WordData.Image, WordData.Entropy, WordData.Believe, WordData.Weave);
		registerSymbol(new SymbolColorCloudNatural("ColorCloudNat"), 1, WordData.Image, WordData.Entropy, WordData.Believe, WordData.Nature);
		registerSymbol(new SymbolColorFog("ColorFog"), 1, WordData.Image, WordData.Entropy, WordData.Explore, WordData.Weave);
		registerSymbol(new SymbolColorFogNatural("ColorFogNat"), 1, WordData.Image, WordData.Entropy, WordData.Explore, WordData.Nature);
		registerSymbol(new SymbolColorFoliage("ColorFoliage"), 1, WordData.Image, WordData.Growth, WordData.Elevate, WordData.Weave);
		registerSymbol(new SymbolColorFoliageNatural("ColorFoliageNat"), 1, WordData.Image, WordData.Growth, WordData.Elevate, WordData.Nature);
		registerSymbol(new SymbolColorGrass("ColorGrass"), 1, WordData.Image, WordData.Growth, WordData.Resilience, WordData.Weave);
		registerSymbol(new SymbolColorGrassNatural("ColorGrassNat"), 1, WordData.Image, WordData.Growth, WordData.Resilience, WordData.Nature);
		registerSymbol(new SymbolColorSky("ColorSky"), 1, WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Weave);
		registerSymbol(new SymbolColorSkyNatural("ColorSkyNat"), 1, WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Nature);
		registerSymbol(new SymbolColorSkyNight("ColorSkyNight"), 1, WordData.Image, WordData.Celestial, WordData.Contradict, WordData.Weave);
		registerSymbol(new SymbolColorWater("ColorWater"), 1, WordData.Image, WordData.Flow, WordData.Constraint, WordData.Weave);
		registerSymbol(new SymbolColorWaterNatural("ColorWaterNat"), 1, WordData.Image, WordData.Flow, WordData.Constraint, WordData.Nature);
		registerSymbol(new SymbolDoodadRainbow("Rainbow"), 1, WordData.Celestial, WordData.Image, WordData.Harmony, WordData.Balance);
		registerSymbol(new SymbolHideHorizon("NoHorizon"), 1, WordData.Celestial, WordData.Inhibit, WordData.Image, WordData.Void);
		registerSymbol(new SymbolDummy("MoonDark"), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Wisdom);
		registerSymbol(new SymbolMoonNormal("MoonNormal"), 1, WordData.Celestial, WordData.Image, WordData.Cycle, WordData.Wisdom);
		registerSymbol(new SymbolDummy("StarsDark"), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Order);
		registerSymbol(new SymbolStarsEndSky("StarsEndSky"), 1, WordData.Celestial, WordData.Image, WordData.Chaos, WordData.Weave);
		registerSymbol(new SymbolStarsNormal("StarsNormal"), 1, WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Order);
		registerSymbol(new SymbolStarsTwinkle("StarsTwinkle"), 1, WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Entropy);
		registerSymbol(new SymbolDummy("SunDark"), 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Energy);
		registerSymbol(new SymbolSunNormal("SunNormal"), 2, WordData.Celestial, WordData.Image, WordData.Stimulate, WordData.Energy);
		registerSymbol(new SymbolBiomeControllerGrid("BioConGrid"), 3, WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Mutual);
		registerSymbol(new SymbolBiomeControllerNative("BioConNative"), 3, WordData.Constraint, WordData.Nature, WordData.Tradition, WordData.Sustain);
		registerSymbol(new SymbolBiomeControllerSingle("BioConSingle"), 3, WordData.Constraint, WordData.Nature, WordData.Infinite, WordData.Static);
		registerSymbol(new SymbolBiomeControllerTiled("BioConTiled"), 3, WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Contradict);
		registerSymbol(new SymbolBiomeControllerHuge("BioConHuge"), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Huge");
		registerSymbol(new SymbolBiomeControllerLarge("BioConLarge"), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Large");
		registerSymbol(new SymbolBiomeControllerMedium("BioConMedium"), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Medium");
		registerSymbol(new SymbolBiomeControllerSmall("BioConSmall"), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Small");
		registerSymbol(new SymbolBiomeControllerTiny("BioConTiny"), 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Tiny");
		registerSymbol(new SymbolNoSea("NoSea"), 2, WordData.Modifier, WordData.Constraint, WordData.Flow, WordData.Inhibit);
		registerSymbol(new SymbolAntiPvP("PvPOff"), null, WordData.Chain, WordData.Chaos, WordData.Encourage, WordData.Harmony);
		registerSymbol(new SymbolEnvAccelerated("EnvAccel"), 3, WordData.Environment, WordData.Dynamic, WordData.Change, WordData.Spur);
		registerSymbol(new SymbolEnvExplosions("EnvExplosions"), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Force);
		registerSymbol(new SymbolEnvLightning("EnvLightning"), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Energy);
		registerSymbol(new SymbolEnvMeteor("EnvMeteor"), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Momentum);
		registerSymbol(new SymbolEnvScorched("EnvScorch"), 3, WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Chaos);
		registerSymbol(new SymbolLightingBright("LightingBright"), 3, WordData.Ethereal, WordData.Power, WordData.Infinite, WordData.Spur);
		registerSymbol(new SymbolLightingDark("LightingDark"), 3, WordData.Ethereal, WordData.Void, WordData.Constraint, WordData.Inhibit);
		registerSymbol(new SymbolLightingNormal("LightingNormal"), 2, WordData.Ethereal, WordData.Dynamic, WordData.Cycle, WordData.Balance);
		registerSymbol(new SymbolAngle("ModNorth", 000.0F, "North"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Control);
		registerSymbol(new SymbolAngle("ModEast", 090.0F, "East"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Tradition);
		registerSymbol(new SymbolAngle("ModSouth", 180.0F, "South"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Chaos);
		registerSymbol(new SymbolAngle("ModWest", 270.0F, "West"), 0, WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Change);
		registerSymbol(new SymbolClear("ModClear"), 0, WordData.Contradict, WordData.Transform, WordData.Change, WordData.Void);
		registerSymbol(new SymbolGradient("ModGradient"), 0, WordData.Modifier, WordData.Image, WordData.Merge, WordData.Weave);
		registerSymbol(new SymbolHorizonColor("ColorHorizon"), 0, WordData.Modifier, WordData.Image, WordData.Celestial, WordData.Change);
		registerSymbol(new SymbolLength("ModZero", 0.0F, "Zero Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Inhibit);
		registerSymbol(new SymbolLength("ModHalf", 0.5F, "Half Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Stimulate);
		registerSymbol(new SymbolLength("ModFull", 1.0F, "Full Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Balance);
		registerSymbol(new SymbolLength("ModDouble", 2.0F, "Double Length"), 0, WordData.Modifier, WordData.Time, WordData.System, WordData.Sacrifice);
		registerSymbol(new SymbolPhase("ModEnd", 000F, "Nadir"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Rebirth);
		registerSymbol(new SymbolPhase("ModRising", 090F, "Rising"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Growth);
		registerSymbol(new SymbolPhase("ModNoon", 180F, "Zenith"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Harmony);
		registerSymbol(new SymbolPhase("ModSetting", 270F, "Setting"), 0, WordData.Modifier, WordData.Cycle, WordData.System, WordData.Future);
		registerSymbol(new SymbolCaves("Caves"), 2, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Flow);
		registerSymbol(new SymbolDungeons("Dungeons"), 2, WordData.Civilization, WordData.Constraint, WordData.Chain, WordData.Resurrect);
		registerSymbol(new SymbolFloatingIslands("FloatIslands"), 3, WordData.Terrain, WordData.Transform, WordData.Form, WordData.Celestial);
		registerSymbol(new SymbolDummy("FeatureLargeDummy", InstabilityData.symbol.dummyFeatureLarge), 4, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Terrain);
		registerSymbol(new SymbolDummy("FeatureMediumDummy", InstabilityData.symbol.dummyFeatureMedium), 4, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Balance);
		registerSymbol(new SymbolDummy("FeatureSmallDummy", InstabilityData.symbol.dummyFeatureSmall), 5, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Form);
		registerSymbol(new SymbolHugeTrees("HugeTrees"), 2, WordData.Nature, WordData.Stimulate, WordData.Spur, WordData.Elevate);
		registerSymbol(new SymbolLakesDeep("LakesDeep"), 3, WordData.Nature, WordData.Flow, WordData.Static, WordData.Explore);
		registerSymbol(new SymbolLakesSurface("LakesSurface"), 3, WordData.Nature, WordData.Flow, WordData.Static, WordData.Elevate);
		registerSymbol(new SymbolMineshafts("Mineshafts"), 3, WordData.Civilization, WordData.Machine, WordData.Motion, WordData.Tradition);
		registerSymbol(new SymbolNetherFort("NetherFort"), 3, WordData.Civilization, WordData.Machine, WordData.Power, WordData.Entropy);
		registerSymbol(new SymbolObelisks("Obelisks"), 3, WordData.Civilization, WordData.Resilience, WordData.Static, WordData.Form);
		registerSymbol(new SymbolRavines("Ravines"), 2, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Weave);
		registerSymbol(new SymbolSpheres("TerModSpheres"), 2, WordData.Terrain, WordData.Transform, WordData.Form, WordData.Cycle);
		registerSymbol(new SymbolSpikes("GenSpikes"), 3, WordData.Nature, WordData.Encourage, WordData.Entropy, WordData.Structure);
		registerSymbol(new SymbolStrongholds("Strongholds"), 3, WordData.Civilization, WordData.Wisdom, WordData.Future, WordData.Honor);
		registerSymbol(new SymbolTendrils("Tendrils"), 3, WordData.Terrain, WordData.Transform, WordData.Growth, WordData.Flow);
		registerSymbol(new SymbolVillages("Villages"), 3, WordData.Civilization, WordData.Society, WordData.Harmony, WordData.Nurture);
		registerSymbol(new SymbolCrystalFormation("CryForm"), 3, WordData.Nature, WordData.Encourage, WordData.Growth, WordData.Structure);
		registerSymbol(new SymbolSkylands("Skylands"), 3, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Elevate);
		registerSymbol(new SymbolStarFissure("StarFissure"), 3, WordData.Nature, WordData.Harmony, WordData.Mutual, WordData.Void);
		registerSymbol(new SymbolDenseOres("DenseOres"), 5, WordData.Environment, WordData.Stimulate, WordData.Machine, WordData.Chaos);
		registerSymbol(new SymbolWeatherAlways("WeatherOn"), 3, WordData.Sustain, WordData.Static, WordData.Tradition, WordData.Stimulate);
		registerSymbol(new SymbolWeatherCloudy("WeatherCloudy"), 3, WordData.Sustain, WordData.Static, WordData.Believe, WordData.Motion);
		registerSymbol(new SymbolWeatherFast("WeatherFast"), 3, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Spur);
		registerSymbol(new SymbolWeatherNormal("WeatherNorm"), 2, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Balance);
		registerSymbol(new SymbolWeatherOff("WeatherOff"), 3, WordData.Sustain, WordData.Static, WordData.Stimulate, WordData.Energy);
		registerSymbol(new SymbolWeatherRain("WeatherRain"), 3, WordData.Sustain, WordData.Static, WordData.Rebirth, WordData.Growth);
		registerSymbol(new SymbolWeatherSlow("WeatherSlow"), 3, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Inhibit);
		registerSymbol(new SymbolWeatherSnow("WeatherSnow"), 3, WordData.Sustain, WordData.Static, WordData.Inhibit, WordData.Energy);
		registerSymbol(new SymbolWeatherStorm("WeatherStorm"), 3, WordData.Sustain, WordData.Static, WordData.Nature, WordData.Power);
		registerSymbol(new SymbolTerrainGenAmplified("TerrainAmplified"), 3, WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Spur);
		registerSymbol(new SymbolTerrainGenEnd("TerrainEnd"), 4, WordData.Terrain, WordData.Form, WordData.Ethereal, WordData.Flow);
		registerSymbol(new SymbolTerrainGenFlat("TerrainFlat"), 3, WordData.Terrain, WordData.Form, WordData.Inhibit, WordData.Motion);
		registerSymbol(new SymbolTerrainGenNether("TerrainNether"), 4, WordData.Terrain, WordData.Form, WordData.Constraint, WordData.Entropy);
		registerSymbol(new SymbolTerrainGenNormal("TerrainNormal"), 2, WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Flow);
		registerSymbol(new SymbolTerrainGenVoid("TerrainVoid"), 4, WordData.Terrain, WordData.Form, WordData.Infinite, WordData.Void);
	}
}
