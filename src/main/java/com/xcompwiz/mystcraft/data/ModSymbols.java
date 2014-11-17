package com.xcompwiz.mystcraft.data;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.core.InternalAPI;
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

	public static void registerSymbol(String identifier, SymbolBase symbol, String[] poem, Integer cardrank) {
		if (!symbol.identifier().equals(identifier)) LoggerUtils.error("XComp done screwed up. Give him this message. (%s != %s)", identifier, symbol.identifier());
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
				float weight = 1.0F;
				if (biome == BiomeGenBase.sky) {
					weight = 0;
				} else {
					symbol.setCardRank(2);
				}
				GrammarGenerator.registerRule(new Rule(IGrammarAPI.BIOME, CollectionUtils.buildList(symbol.identifier()), weight));
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
		SymbolDataModifiers.initialize();

		registerSymbol("ColorCloud", new SymbolColorCloud(), new String[] {WordData.Image, WordData.Entropy, WordData.Believe, WordData.Weave}, 1);
		registerSymbol("ColorCloudNat", new SymbolColorCloudNatural(), new String[] {WordData.Image, WordData.Entropy, WordData.Believe, WordData.Nature}, 1);
		registerSymbol("ColorFog", new SymbolColorFog(), new String[] {WordData.Image, WordData.Entropy, WordData.Explore, WordData.Weave}, 1);
		registerSymbol("ColorFogNat", new SymbolColorFogNatural(), new String[] {WordData.Image, WordData.Entropy, WordData.Explore, WordData.Nature}, 1);
		registerSymbol("ColorFoliage", new SymbolColorFoliage(), new String[] {WordData.Image, WordData.Growth, WordData.Elevate, WordData.Weave}, 1);
		registerSymbol("ColorFoliageNat", new SymbolColorFoliageNatural(), new String[] {WordData.Image, WordData.Growth, WordData.Elevate, WordData.Nature}, 1);
		registerSymbol("ColorGrass", new SymbolColorGrass(), new String[] {WordData.Image, WordData.Growth, WordData.Resilience, WordData.Weave}, 1);
		registerSymbol("ColorGrassNat", new SymbolColorGrassNatural(), new String[] {WordData.Image, WordData.Growth, WordData.Resilience, WordData.Nature}, 1);
		registerSymbol("ColorSky", new SymbolColorSky(), new String[] {WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Weave}, 1);
		registerSymbol("ColorSkyNat", new SymbolColorSkyNatural(), new String[] {WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Nature}, 1);
		registerSymbol("ColorSkyNight", new SymbolColorSkyNight(), new String[] {WordData.Image, WordData.Celestial, WordData.Contradict, WordData.Weave}, 1);
		registerSymbol("ColorWater", new SymbolColorWater(), new String[] {WordData.Image, WordData.Flow, WordData.Constraint, WordData.Weave}, 1);
		registerSymbol("ColorWaterNat", new SymbolColorWaterNatural(), new String[] {WordData.Image, WordData.Flow, WordData.Constraint, WordData.Nature}, 1);
		registerSymbol("Rainbow", new SymbolDoodadRainbow(), new String[] {WordData.Celestial, WordData.Image, WordData.Harmony, WordData.Balance}, 1);
		registerSymbol("NoHorizon", new SymbolHideHorizon(), new String[] {WordData.Celestial, WordData.Inhibit, WordData.Image, WordData.Void}, 1);
		registerSymbol("MoonDark", new SymbolMoonDark(), new String[] {WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Wisdom}, 1);
		registerSymbol("MoonNormal", new SymbolMoonNormal(), new String[] {WordData.Celestial, WordData.Image, WordData.Cycle, WordData.Wisdom}, 1);
		registerSymbol("StarsDark", new SymbolStarsDark(), new String[] {WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Order}, 1);
		registerSymbol("StarsEndSky", new SymbolStarsEndSky(), new String[] {WordData.Celestial, WordData.Image, WordData.Chaos, WordData.Weave}, 1);
		registerSymbol("StarsNormal", new SymbolStarsNormal(), new String[] {WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Order}, 1);
		registerSymbol("StarsTwinkle", new SymbolStarsTwinkle(), new String[] {WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Entropy}, 1);
		registerSymbol("SunDark", new SymbolSunDark(), new String[] {WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Energy}, 1);
		registerSymbol("BioConGrid", new SymbolBiomeControllerGrid(), new String[] {WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Mutual}, 3);
		registerSymbol("BioConNative", new SymbolBiomeControllerNative(), new String[] {WordData.Constraint, WordData.Nature, WordData.Tradition, WordData.Sustain}, 3);
		registerSymbol("BioConSingle", new SymbolBiomeControllerSingle(), new String[] {WordData.Constraint, WordData.Nature, WordData.Infinite, WordData.Static}, 3);
		registerSymbol("BioConTiled", new SymbolBiomeControllerTiled(), new String[] {WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Contradict}, 3);
		registerSymbol("BioConHuge", new SymbolBiomeControllerHuge(), new String[] {WordData.Constraint, WordData.Nature, WordData.Weave, "Huge"}, 3);
		registerSymbol("BioConLarge", new SymbolBiomeControllerLarge(), new String[] {WordData.Constraint, WordData.Nature, WordData.Weave, "Large"}, 3);
		registerSymbol("BioConMedium", new SymbolBiomeControllerMedium(), new String[] {WordData.Constraint, WordData.Nature, WordData.Weave, "Medium"}, 3);
		registerSymbol("BioConSmall", new SymbolBiomeControllerSmall(), new String[] {WordData.Constraint, WordData.Nature, WordData.Weave, "Small"}, 3);
		registerSymbol("BioConTiny", new SymbolBiomeControllerTiny(), new String[] {WordData.Constraint, WordData.Nature, WordData.Weave, "Tiny"}, 3);
		registerSymbol("NoSea", new ModifierNoSea(), new String[] {WordData.Modifier, WordData.Constraint, WordData.Flow, WordData.Inhibit}, 2);
		registerSymbol("PvPOff", new SymbolAntiPvP(), new String[] {WordData.Chain, WordData.Chaos, WordData.Encourage, WordData.Harmony}, null);
		registerSymbol("EnvAccel", new SymbolEnvAccelerated(), new String[] {WordData.Environment, WordData.Dynamic, WordData.Change, WordData.Spur}, 3);
		registerSymbol("EnvExplosions", new SymbolEnvExplosions(), new String[] {WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Force}, 3);
		registerSymbol("EnvLightning", new SymbolEnvLightning(), new String[] {WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Energy}, 3);
		registerSymbol("EnvMeteor", new SymbolEnvMeteor(), new String[] {WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Momentum}, 3);
		registerSymbol("EnvScorch", new SymbolEnvScorched(), new String[] {WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Chaos}, 3);
		registerSymbol("SunNormal", new SymbolSunNormal(), new String[] {WordData.Celestial, WordData.Image, WordData.Stimulate, WordData.Energy}, 2);
		registerSymbol("LightingBright", new SymbolLightingBright(), new String[] {WordData.Ethereal, WordData.Power, WordData.Infinite, WordData.Spur}, 3);
		registerSymbol("LightingDark", new SymbolLightingDark(), new String[] {WordData.Ethereal, WordData.Void, WordData.Constraint, WordData.Inhibit}, 3);
		registerSymbol("LightingNormal", new SymbolLightingNormal(), new String[] {WordData.Ethereal, WordData.Dynamic, WordData.Cycle, WordData.Balance}, 2);
		registerSymbol("ModNorth", new ModifierAngle(000.0F, "ModNorth", "North"), new String[] {WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Control}, 0);
		registerSymbol("ModEast", new ModifierAngle(090.0F, "ModEast", "East"), new String[] {WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Tradition}, 0);
		registerSymbol("ModSouth", new ModifierAngle(180.0F, "ModSouth", "South"), new String[] {WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Chaos}, 0);
		registerSymbol("ModWest", new ModifierAngle(270.0F, "ModWest", "West"), new String[] {WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Change}, 0);
		registerSymbol("ModClear", new ModifierClear(), new String[] {WordData.Contradict, WordData.Transform, WordData.Change, WordData.Void}, 0);
		registerSymbol("ModGradient", new ModifierGradient(), new String[] {WordData.Modifier, WordData.Image, WordData.Merge, WordData.Weave}, 0);
		registerSymbol("ColorHorizon", new ModifierHorizonColor(), new String[] {WordData.Modifier, WordData.Image, WordData.Celestial, WordData.Change}, 0);
		registerSymbol("ModZero", new ModifierLength(0.0F, "ModZero", "Zero Length"), new String[] {WordData.Modifier, WordData.Time, WordData.System, WordData.Inhibit}, 0);
		registerSymbol("ModHalf", new ModifierLength(0.5F, "ModHalf", "Half Length"), new String[] {WordData.Modifier, WordData.Time, WordData.System, WordData.Stimulate}, 0);
		registerSymbol("ModFull", new ModifierLength(1.0F, "ModFull", "Full Length"), new String[] {WordData.Modifier, WordData.Time, WordData.System, WordData.Balance}, 0);
		registerSymbol("ModDouble", new ModifierLength(2.0F, "ModDouble", "Double Length"), new String[] {WordData.Modifier, WordData.Time, WordData.System, WordData.Sacrifice}, 0);
		registerSymbol("ModEnd", new ModifierPhase(000F, "ModEnd", "Nadir"), new String[] {WordData.Modifier, WordData.Cycle, WordData.System, WordData.Rebirth}, 0);
		registerSymbol("ModRising", new ModifierPhase(090F, "ModRising", "Rising"), new String[] {WordData.Modifier, WordData.Cycle, WordData.System, WordData.Growth}, 0);
		registerSymbol("ModNoon", new ModifierPhase(180F, "ModNoon", "Zenith"), new String[] {WordData.Modifier, WordData.Cycle, WordData.System, WordData.Harmony}, 0);
		registerSymbol("ModSetting", new ModifierPhase(270F, "ModSetting", "Setting"), new String[] {WordData.Modifier, WordData.Cycle, WordData.System, WordData.Future}, 0);
		registerSymbol("Caves", new SymbolCaves(), new String[] {WordData.Terrain, WordData.Transform, WordData.Void, WordData.Flow}, 2);
		registerSymbol("Dungeons", new SymbolDungeons(), new String[] {WordData.Civilization, WordData.Constraint, WordData.Chain, WordData.Resurrect}, 2);
		registerSymbol("FloatIslands", new SymbolFloatingIslands(), new String[] {WordData.Terrain, WordData.Transform, WordData.Form, WordData.Celestial}, 3);
		registerSymbol("HugeTrees", new SymbolHugeTrees(), new String[] {WordData.Nature, WordData.Stimulate, WordData.Spur, WordData.Elevate}, 2);
		registerSymbol("LakesDeep", new SymbolLakesDeep(), new String[] {WordData.Nature, WordData.Flow, WordData.Static, WordData.Explore}, 3);
		registerSymbol("LakesSurface", new SymbolLakesSurface(), new String[] {WordData.Nature, WordData.Flow, WordData.Static, WordData.Elevate}, 3);
		registerSymbol("Mineshafts", new SymbolMineshafts(), new String[] {WordData.Civilization, WordData.Machine, WordData.Motion, WordData.Tradition}, 3);
		registerSymbol("NetherFort", new SymbolNetherFort(), new String[] {WordData.Civilization, WordData.Machine, WordData.Power, WordData.Entropy}, 3);
		registerSymbol("Obelisks", new SymbolObelisks(), new String[] {WordData.Civilization, WordData.Resilience, WordData.Static, WordData.Form}, 3);
		registerSymbol("Ravines", new SymbolRavines(), new String[] {WordData.Terrain, WordData.Transform, WordData.Void, WordData.Weave}, 2);
		registerSymbol("TerModSpheres", new SymbolSpheres(), new String[] {WordData.Terrain, WordData.Transform, WordData.Form, WordData.Cycle}, 2);
		registerSymbol("GenSpikes", new SymbolSpikes(), new String[] {WordData.Nature, WordData.Encourage, WordData.Entropy, WordData.Structure}, 3);
		registerSymbol("Strongholds", new SymbolStrongholds(), new String[] {WordData.Civilization, WordData.Wisdom, WordData.Future, WordData.Honor}, 3);
		registerSymbol("Tendrils", new SymbolTendrils(), new String[] {WordData.Terrain, WordData.Transform, WordData.Growth, WordData.Flow}, 3);
		registerSymbol("Villages", new SymbolVillages(), new String[] {WordData.Civilization, WordData.Society, WordData.Harmony, WordData.Nurture}, 3);
		registerSymbol("CryForm", new SymbolCrystalFormation(), new String[] {WordData.Nature, WordData.Encourage, WordData.Growth, WordData.Structure}, 3);
		registerSymbol("Skylands", new SymbolSkylands(), new String[] {WordData.Terrain, WordData.Transform, WordData.Void, WordData.Elevate}, 3);
		registerSymbol("StarFissure", new SymbolStarFissure(), new String[] {WordData.Nature, WordData.Harmony, WordData.Mutual, WordData.Void}, 3);
		registerSymbol("DenseOres", new SymbolDenseOres(), new String[] {WordData.Environment, WordData.Stimulate, WordData.Machine, WordData.Chaos}, 5);
		registerSymbol("WeatherOn", new SymbolWeatherAlways(), new String[] {WordData.Sustain, WordData.Static, WordData.Tradition, WordData.Stimulate}, 3);
		registerSymbol("WeatherCloudy", new SymbolWeatherCloudy(), new String[] {WordData.Sustain, WordData.Static, WordData.Believe, WordData.Motion}, 3);
		registerSymbol("WeatherFast", new SymbolWeatherFast(), new String[] {WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Spur}, 3);
		registerSymbol("WeatherNorm", new SymbolWeatherNormal(), new String[] {WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Balance}, 2);
		registerSymbol("WeatherOff", new SymbolWeatherOff(), new String[] {WordData.Sustain, WordData.Static, WordData.Stimulate, WordData.Energy}, 3);
		registerSymbol("WeatherRain", new SymbolWeatherRain(), new String[] {WordData.Sustain, WordData.Static, WordData.Rebirth, WordData.Growth}, 3);
		registerSymbol("WeatherSlow", new SymbolWeatherSlow(), new String[] {WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Inhibit}, 3);
		registerSymbol("WeatherSnow", new SymbolWeatherSnow(), new String[] {WordData.Sustain, WordData.Static, WordData.Inhibit, WordData.Energy}, 3);
		registerSymbol("WeatherStorm", new SymbolWeatherStorm(), new String[] {WordData.Sustain, WordData.Static, WordData.Nature, WordData.Power}, 3);
		registerSymbol("TerrainAmplified", new SymbolTerrainGenAmplified(), new String[] {WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Spur}, 3);
		registerSymbol("TerrainEnd", new SymbolTerrainGenEnd(), new String[] {WordData.Terrain, WordData.Form, WordData.Ethereal, WordData.Flow}, 4);
		registerSymbol("Flat", new SymbolTerrainGenFlat(), new String[] {WordData.Terrain, WordData.Form, WordData.Inhibit, WordData.Motion}, 3);
		registerSymbol("TerrainNether", new SymbolTerrainGenNether(), new String[] {WordData.Terrain, WordData.Form, WordData.Constraint, WordData.Entropy}, 4);
		registerSymbol("TerrainNormal", new SymbolTerrainGenNormal(), new String[] {WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Flow}, 2);
		registerSymbol("Void", new SymbolTerrainGenVoid(), new String[] {WordData.Terrain, WordData.Form, WordData.Infinite, WordData.Void}, 4);
	}
}
