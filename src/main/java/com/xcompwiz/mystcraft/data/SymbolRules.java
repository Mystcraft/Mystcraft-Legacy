package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.util.CollectionUtils;

public class SymbolRules {

	public static void initialize() {
		addRule("ModNorth", buildRule(1, GrammarData.ANGLE_BASIC, "ModNorth"));
		addRule("ModSouth", buildRule(1, GrammarData.ANGLE_BASIC, "ModSouth"));
		addRule("ModEast", buildRule(1, GrammarData.ANGLE_BASIC, "ModEast"));
		addRule("ModWest", buildRule(1, GrammarData.ANGLE_BASIC, "ModWest"));

		addRule("ModRising", buildRule(1, GrammarData.PHASE_BASIC, "ModRising"));
		addRule("ModNoon", buildRule(1, GrammarData.PHASE_BASIC, "ModNoon"));
		addRule("ModSetting", buildRule(1, GrammarData.PHASE_BASIC, "ModSetting"));
		addRule("ModEnd", buildRule(1, GrammarData.PHASE_BASIC, "ModEnd"));

		addRule("ModZero", buildRule(2, GrammarData.PERIOD_BASIC, "ModZero"));
		addRule("ModHalf", buildRule(1, GrammarData.PERIOD_BASIC, "ModHalf"));
		addRule("ModFull", buildRule(1, GrammarData.PERIOD_BASIC, "ModFull"));
		addRule("ModDouble", buildRule(1, GrammarData.PERIOD_BASIC, "ModDouble"));

		addRule("ModGradient", buildRule(1, GrammarData.GRADIENT_BASIC, GrammarData.COLOR_SEQ, GrammarData.PERIOD_SEQ, "ModGradient"));

		addRule("ColorHorizon", buildRule(2, GrammarData.SUNSET, GrammarRules.SUNSET_EXT, GrammarData.GRADIENT_SEQ, "ColorHorizon"));

		addRule("NoSea", buildRule(2, GrammarData.BLOCK_SEA, "NoSea"));

		addRule("WeatherOff", buildRule(2, GrammarData.WEATHER, "WeatherOff"));
		addRule("WeatherStorm", buildRule(2, GrammarData.WEATHER, "WeatherStorm"));
		addRule("WeatherSnow", buildRule(2, GrammarData.WEATHER, "WeatherSnow"));
		addRule("WeatherRain", buildRule(2, GrammarData.WEATHER, "WeatherRain"));
		addRule("WeatherCloudy", buildRule(2, GrammarData.WEATHER, "WeatherCloudy"));
		addRule("WeatherOn", buildRule(2, GrammarData.WEATHER, "WeatherOn"));
		addRule("WeatherSlow", buildRule(2, GrammarData.WEATHER, "WeatherSlow"));
		addRule("WeatherNorm", buildRule(1, GrammarData.WEATHER, "WeatherNorm"));
		addRule("WeatherFast", buildRule(2, GrammarData.WEATHER, "WeatherFast"));

		addRule("TerrainVoid", buildRule(3, GrammarData.TERRAIN, "TerrainVoid"));
		addRule("TerrainNormal", buildRule(1, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, "TerrainNormal"));
		addRule("TerrainAmplified", buildRule(3, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, "TerrainAmplified"));
		addRule("TerrainNether", buildRule(3, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, "TerrainNether"));
		addRule("TerrainFlat", buildRule(2, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, "TerrainFlat"));
		addRule("TerrainEnd", buildRule(3, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, "TerrainEnd"));

		addRule("LightingNormal", buildRule(1, GrammarData.LIGHTING, "LightingNormal"));
		addRule("LightingDark", buildRule(2, GrammarData.LIGHTING, "LightingDark"));
		addRule("LightingBright", buildRule(2, GrammarData.LIGHTING, "LightingBright"));

		addRule("ColorFog", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.GRADIENT_SEQ, "ColorFog"));
		addRule("ColorSkyNight", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.GRADIENT_SEQ, "ColorSkyNight"));
		addRule("ColorSky", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.GRADIENT_SEQ, "ColorSky"));
		addRule("ColorCloud", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.GRADIENT_SEQ, "ColorCloud"));
		addRule("ColorGrass", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.COLOR_SEQ, "ColorGrass"));
		addRule("ColorFoliage", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.COLOR_SEQ, "ColorFoliage"));
		addRule("ColorWater", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.COLOR_SEQ, "ColorWater"));
		addRule("ColorFogNat", buildRule(2, GrammarData.VISUAL_EFFECT, "ColorFogNat"));
		addRule("ColorSkyNat", buildRule(2, GrammarData.VISUAL_EFFECT, "ColorSkyNat"));
		addRule("ColorCloudNat", buildRule(2, GrammarData.VISUAL_EFFECT, "ColorCloudNat"));
		addRule("ColorGrassNat", buildRule(2, GrammarData.VISUAL_EFFECT, "ColorGrassNat"));
		addRule("ColorFoliageNat", buildRule(2, GrammarData.VISUAL_EFFECT, "ColorFoliageNat"));
		addRule("ColorWaterNat", buildRule(2, GrammarData.VISUAL_EFFECT, "ColorWaterNat"));
		addRule("NoHorizon", buildRule(2, GrammarData.VISUAL_EFFECT, "NoHorizon"));
		addRule("Rainbow", buildRule(4, GrammarData.VISUAL_EFFECT, GrammarData.ANGLE_SEQ, "Rainbow"));

		addRule("EnvScorch", buildRule(null, GrammarData.EFFECT, "EnvScorch"));
		addRule("EnvMeteor", buildRule(null, GrammarData.EFFECT, "EnvMeteor"));
		addRule("EnvLightning", buildRule(null, GrammarData.EFFECT, GrammarData.GRADIENT_SEQ, "EnvLightning"));
		addRule("EnvExplosions", buildRule(null, GrammarData.EFFECT, "EnvExplosions"));
		addRule("EnvAccel", buildRule(null, GrammarData.EFFECT, "EnvAccel"));

		addRule("StarsEndSky", buildRule(4, GrammarData.STARFIELD, GrammarData.GRADIENT_SEQ, "StarsEndSky"));
		addRule("StarsNormal", buildRule(1, GrammarData.STARFIELD, GrammarData.GRADIENT_SEQ, GrammarData.PERIOD_SEQ, GrammarData.ANGLE_SEQ, "StarsNormal"));
		addRule("StarsTwinkle", buildRule(2, GrammarData.STARFIELD, GrammarData.GRADIENT_SEQ, GrammarData.PERIOD_SEQ, GrammarData.ANGLE_SEQ, "StarsTwinkle"));
		addRule("StarsDark", buildRule(3, GrammarData.STARFIELD, "StarsDark"));
		addRule("SunNormal", buildRule(1, GrammarData.SUN, GrammarData.SUNSET, GrammarData.PERIOD_SEQ, GrammarData.ANGLE_SEQ, GrammarData.PHASE_SEQ, "SunNormal"));
		addRule("SunDark", buildRule(3, GrammarData.SUN, "SunDark"));
		addRule("MoonNormal", buildRule(1, GrammarData.MOON, GrammarData.SUNSET_UNCOMMON, GrammarData.PERIOD_SEQ, GrammarData.ANGLE_SEQ, GrammarData.PHASE_SEQ, "MoonNormal"));
		addRule("MoonDark", buildRule(3, GrammarData.MOON, "MoonDark"));

		addRule("BioConNative", buildRule(1, GrammarData.BIOMECONTROLLER, "BioConNative"));
		addRule("BioConSingle", buildRule(1, GrammarData.BIOMECONTROLLER, GrammarData.BIOME, "BioConSingle"));
		addRule("BioConTiled", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, "BioConTiled"));
		addRule("BioConGrid", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, "BioConGrid"));
		addRule("BioConTiny", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, "BioConTiny"));
		addRule("BioConSmall", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, "BioConSmall"));
		addRule("BioConMedium", buildRule(1, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, "BioConMedium"));
		addRule("BioConLarge", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, "BioConLarge"));
		addRule("BioConHuge", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, "BioConHuge"));

		addRule("FloatIslands", buildRule(4, GrammarData.FEATURE_LARGE, GrammarData.BIOME, GrammarData.BLOCK_STRUCTURE, "FloatIslands"));
		addRule("Tendrils", buildRule(4, GrammarData.FEATURE_LARGE, GrammarData.BLOCK_STRUCTURE, "Tendrils"));
		addRule("Skylands", buildRule(5, GrammarData.FEATURE_LARGE, "Skylands"));
		addRule("Caves", buildRule(1, GrammarData.FEATURE_LARGE, "Caves"));
		addRule("DenseOres", buildRule(null, GrammarData.FEATURE_LARGE, "DenseOres"));
		addRule("HugeTrees", buildRule(2, GrammarData.FEATURE_LARGE, "HugeTrees"));

		addRule("NetherFort", buildRule(2, GrammarData.FEATURE_MEDIUM, "NetherFort"));
		addRule("Villages", buildRule(1, GrammarData.FEATURE_MEDIUM, "Villages"));
		addRule("Strongholds", buildRule(1, GrammarData.FEATURE_MEDIUM, "Strongholds"));
		addRule("Mineshafts", buildRule(1, GrammarData.FEATURE_MEDIUM, "Mineshafts"));

		addRule("Ravines", buildRule(1, GrammarData.FEATURE_MEDIUM, "Ravines"));
		addRule("TerModSpheres", buildRule(3, GrammarData.FEATURE_MEDIUM, GrammarData.BLOCK_STRUCTURE, "TerModSpheres"));
		addRule("Dungeons", buildRule(2, GrammarData.FEATURE_MEDIUM, "Dungeons"));
		addRule("GenSpikes", buildRule(3, GrammarData.FEATURE_MEDIUM, GrammarData.BLOCK_STRUCTURE, "GenSpikes"));

		addRule("StarFissure", buildRule(3, GrammarData.FEATURE_SMALL, "StarFissure"));
		addRule("Obelisks", buildRule(3, GrammarData.FEATURE_SMALL, GrammarData.BLOCK_STRUCTURE, "Obelisks"));
		addRule("LakesSurface", buildRule(1, GrammarData.FEATURE_SMALL, GrammarData.BLOCK_FLUID, "LakesSurface"));
		addRule("LakesDeep", buildRule(1, GrammarData.FEATURE_SMALL, GrammarRules.BLOCK_NONSOLID, "LakesDeep"));
		addRule("CryForm", buildRule(3, GrammarData.FEATURE_SMALL, GrammarData.BLOCK_CRYSTAL, "CryForm"));

		//XXX: Direct string references to primary grammar rules
		// The dummy features should take up the entire rule chain when generating
		// However, if the player wrote a dummy and some features (or multiple of the same dummy), everything should be able to connect to the grammar 
		addRule("FeatureLargeDummy", buildRule(5, "FeatureLarges0", GrammarRules.FEATURE_LARGE_EXT, "FeatureLargeDummy"));
		addRule("FeatureMediumDummy", buildRule(5, "FeatureMediums0", GrammarRules.FEATURE_MEDIUM_EXT, "FeatureMediumDummy"));
		addRule("FeatureSmallDummy", buildRule(null, "FeatureSmalls0", GrammarRules.FEATURE_SMALL_EXT, "FeatureSmallDummy"));
		addRule("FeatureLargeDummy", buildRule(null, GrammarRules.FEATURE_LARGE_EXT, "FeatureLargeDummy"));
		addRule("FeatureMediumDummy", buildRule(null, GrammarRules.FEATURE_MEDIUM_EXT, "FeatureMediumDummy"));
		addRule("FeatureSmallDummy", buildRule(null, GrammarRules.FEATURE_SMALL_EXT, "FeatureSmallDummy"));
	}

	private static void addRule(String string, Rule rule) {
		IAgeSymbol symbol = SymbolManager.getAgeSymbol(string);
		if (string == null) {
			LoggerUtils.info("Failed to add rule to symbol " + string);
			return;
		}
		if (symbol instanceof SymbolBase) {
			((SymbolBase) symbol).addRule(rule);
		}
	}

	//XXX: (Helper) Essentially the same function again
	private static Rule buildRule(Integer rank, String parent, String... args) {
		ArrayList<String> list = CollectionUtils.buildList(args);
		return new Rule(parent, list, rank);
	}
}
