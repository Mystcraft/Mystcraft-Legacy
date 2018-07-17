package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.util.CollectionUtils;

import net.minecraft.util.ResourceLocation;

public class SymbolRules {

	public static void initialize() {
		addRuleInternal("ModNorth", buildRule(1, GrammarData.ANGLE_BASIC, asMyst("ModNorth")));
		addRuleInternal("ModSouth", buildRule(1, GrammarData.ANGLE_BASIC, asMyst("ModSouth")));
		addRuleInternal("ModEast", buildRule(1, GrammarData.ANGLE_BASIC, asMyst("ModEast")));
		addRuleInternal("ModWest", buildRule(1, GrammarData.ANGLE_BASIC, asMyst("ModWest")));

		addRuleInternal("ModRising", buildRule(1, GrammarData.PHASE_BASIC, asMyst("ModRising")));
		addRuleInternal("ModNoon", buildRule(1, GrammarData.PHASE_BASIC, asMyst("ModNoon")));
		addRuleInternal("ModSetting", buildRule(1, GrammarData.PHASE_BASIC, asMyst("ModSetting")));
		addRuleInternal("ModEnd", buildRule(1, GrammarData.PHASE_BASIC, asMyst("ModEnd")));

		addRuleInternal("ModZero", buildRule(2, GrammarData.PERIOD_BASIC, asMyst("ModZero")));
		addRuleInternal("ModHalf", buildRule(1, GrammarData.PERIOD_BASIC, asMyst("ModHalf")));
		addRuleInternal("ModFull", buildRule(1, GrammarData.PERIOD_BASIC, asMyst("ModFull")));
		addRuleInternal("ModDouble", buildRule(1, GrammarData.PERIOD_BASIC, asMyst("ModDouble")));

		addRuleInternal("ModGradient", buildRule(1, GrammarData.GRADIENT_BASIC, GrammarData.COLOR_SEQ, GrammarData.PERIOD_SEQ, asMyst("ModGradient")));

		addRuleInternal("ColorHorizon", buildRule(2, GrammarData.SUNSET, GrammarRules.SUNSET_EXT, GrammarData.GRADIENT_SEQ, asMyst("ColorHorizon")));

		addRuleInternal("NoSea", buildRule(2, GrammarData.BLOCK_SEA, asMyst("NoSea")));

		addRuleInternal("WeatherOff", buildRule(2, GrammarData.WEATHER, asMyst("WeatherOff")));
		addRuleInternal("WeatherStorm", buildRule(2, GrammarData.WEATHER, asMyst("WeatherStorm")));
		addRuleInternal("WeatherSnow", buildRule(2, GrammarData.WEATHER, asMyst("WeatherSnow")));
		addRuleInternal("WeatherRain", buildRule(2, GrammarData.WEATHER, asMyst("WeatherRain")));
		addRuleInternal("WeatherCloudy", buildRule(2, GrammarData.WEATHER, asMyst("WeatherCloudy")));
		addRuleInternal("WeatherOn", buildRule(2, GrammarData.WEATHER, asMyst("WeatherOn")));
		addRuleInternal("WeatherSlow", buildRule(2, GrammarData.WEATHER, asMyst("WeatherSlow")));
		addRuleInternal("WeatherNorm", buildRule(1, GrammarData.WEATHER, asMyst("WeatherNorm")));
		addRuleInternal("WeatherFast", buildRule(2, GrammarData.WEATHER, asMyst("WeatherFast")));

		addRuleInternal("TerrainVoid", buildRule(3, GrammarData.TERRAIN, asMyst("TerrainVoid")));
		addRuleInternal("TerrainNormal", buildRule(1, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, asMyst("TerrainNormal")));
		addRuleInternal("TerrainAmplified", buildRule(3, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, asMyst("TerrainAmplified")));
		addRuleInternal("TerrainNether", buildRule(3, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, asMyst("TerrainNether")));
		addRuleInternal("TerrainFlat", buildRule(2, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, asMyst("TerrainFlat")));
		addRuleInternal("TerrainEnd", buildRule(3, GrammarData.TERRAIN, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SEA, asMyst("TerrainEnd")));

		addRuleInternal("LightingNormal", buildRule(1, GrammarData.LIGHTING, asMyst("LightingNormal")));
		addRuleInternal("LightingDark", buildRule(2, GrammarData.LIGHTING, asMyst("LightingDark")));
		addRuleInternal("LightingBright", buildRule(2, GrammarData.LIGHTING, asMyst("LightingBright")));

		addRuleInternal("ColorFog", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.GRADIENT_SEQ, asMyst("ColorFog")));
		addRuleInternal("ColorSkyNight", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.GRADIENT_SEQ, asMyst("ColorSkyNight")));
		addRuleInternal("ColorSky", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.GRADIENT_SEQ, asMyst("ColorSky")));
		addRuleInternal("ColorCloud", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.GRADIENT_SEQ, asMyst("ColorCloud")));
		addRuleInternal("ColorGrass", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.COLOR_SEQ, asMyst("ColorGrass")));
		addRuleInternal("ColorFoliage", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.COLOR_SEQ, asMyst("ColorFoliage")));
		addRuleInternal("ColorWater", buildRule(3, GrammarData.VISUAL_EFFECT, GrammarData.COLOR_SEQ, asMyst("ColorWater")));
		addRuleInternal("ColorFogNat", buildRule(2, GrammarData.VISUAL_EFFECT, asMyst("ColorFogNat")));
		addRuleInternal("ColorSkyNat", buildRule(2, GrammarData.VISUAL_EFFECT, asMyst("ColorSkyNat")));
		addRuleInternal("ColorCloudNat", buildRule(2, GrammarData.VISUAL_EFFECT, asMyst("ColorCloudNat")));
		addRuleInternal("ColorGrassNat", buildRule(2, GrammarData.VISUAL_EFFECT, asMyst("ColorGrassNat")));
		addRuleInternal("ColorFoliageNat", buildRule(2, GrammarData.VISUAL_EFFECT, asMyst("ColorFoliageNat")));
		addRuleInternal("ColorWaterNat", buildRule(2, GrammarData.VISUAL_EFFECT, asMyst("ColorWaterNat")));
		addRuleInternal("NoHorizon", buildRule(2, GrammarData.VISUAL_EFFECT, asMyst("NoHorizon")));
		addRuleInternal("Rainbow", buildRule(4, GrammarData.VISUAL_EFFECT, GrammarData.ANGLE_SEQ, asMyst("Rainbow")));

		addRuleInternal("EnvScorch", buildRule(null, GrammarData.EFFECT, asMyst("EnvScorch")));
		addRuleInternal("EnvMeteor", buildRule(null, GrammarData.EFFECT, asMyst("EnvMeteor")));
		addRuleInternal("EnvLightning", buildRule(null, GrammarData.EFFECT, GrammarData.GRADIENT_SEQ, asMyst("EnvLightning")));
		addRuleInternal("EnvExplosions", buildRule(null, GrammarData.EFFECT, asMyst("EnvExplosions")));
		addRuleInternal("EnvAccel", buildRule(null, GrammarData.EFFECT, asMyst("EnvAccel")));

		addRuleInternal("StarsEndSky", buildRule(4, GrammarData.STARFIELD, GrammarData.GRADIENT_SEQ, asMyst("StarsEndSky")));
		addRuleInternal("StarsNormal", buildRule(1, GrammarData.STARFIELD, GrammarData.GRADIENT_SEQ, GrammarData.PERIOD_SEQ, GrammarData.ANGLE_SEQ, asMyst("StarsNormal")));
		addRuleInternal("StarsTwinkle", buildRule(2, GrammarData.STARFIELD, GrammarData.GRADIENT_SEQ, GrammarData.PERIOD_SEQ, GrammarData.ANGLE_SEQ, asMyst("StarsTwinkle")));
		addRuleInternal("StarsDark", buildRule(3, GrammarData.STARFIELD, asMyst("StarsDark")));
		addRuleInternal("SunNormal", buildRule(1, GrammarData.SUN, GrammarData.SUNSET, GrammarData.PERIOD_SEQ, GrammarData.ANGLE_SEQ, GrammarData.PHASE_SEQ, asMyst("SunNormal")));
		addRuleInternal("SunDark", buildRule(3, GrammarData.SUN, asMyst("SunDark")));
		addRuleInternal("MoonNormal", buildRule(1, GrammarData.MOON, GrammarData.SUNSET_UNCOMMON, GrammarData.PERIOD_SEQ, GrammarData.ANGLE_SEQ, GrammarData.PHASE_SEQ, asMyst("MoonNormal")));
		addRuleInternal("MoonDark", buildRule(3, GrammarData.MOON, asMyst("MoonDark")));

		addRuleInternal("BioConNative", buildRule(1, GrammarData.BIOMECONTROLLER, asMyst("BioConNative")));
		addRuleInternal("BioConSingle", buildRule(1, GrammarData.BIOMECONTROLLER, GrammarData.BIOME, asMyst("BioConSingle")));
		addRuleInternal("BioConTiled", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, asMyst("BioConTiled")));
		addRuleInternal("BioConGrid", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, asMyst("BioConGrid")));
		addRuleInternal("BioConTiny", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, asMyst("BioConTiny")));
		addRuleInternal("BioConSmall", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, asMyst("BioConSmall")));
		addRuleInternal("BioConMedium", buildRule(1, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, asMyst("BioConMedium")));
		addRuleInternal("BioConLarge", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, asMyst("BioConLarge")));
		addRuleInternal("BioConHuge", buildRule(2, GrammarData.BIOMECONTROLLER, GrammarData.BIOME_LIST, GrammarData.BIOME, GrammarData.BIOME, asMyst("BioConHuge")));

		addRuleInternal("FloatIslands", buildRule(4, GrammarData.FEATURE_LARGE, GrammarData.BIOME, GrammarData.BLOCK_STRUCTURE, asMyst("FloatIslands")));
		addRuleInternal("Tendrils", buildRule(4, GrammarData.FEATURE_LARGE, GrammarData.BLOCK_STRUCTURE, asMyst("Tendrils")));
		addRuleInternal("Skylands", buildRule(5, GrammarData.FEATURE_LARGE, asMyst("Skylands")));
		addRuleInternal("Caves", buildRule(1, GrammarData.FEATURE_LARGE, asMyst("Caves")));
		addRuleInternal("DenseOres", buildRule(null, GrammarData.FEATURE_LARGE, asMyst("DenseOres")));
		addRuleInternal("HugeTrees", buildRule(2, GrammarData.FEATURE_LARGE, asMyst("HugeTrees")));

		addRuleInternal("NetherFort", buildRule(2, GrammarData.FEATURE_MEDIUM, asMyst("NetherFort")));
		addRuleInternal("Villages", buildRule(1, GrammarData.FEATURE_MEDIUM, asMyst("Villages")));
		addRuleInternal("Strongholds", buildRule(1, GrammarData.FEATURE_MEDIUM, asMyst("Strongholds")));
		addRuleInternal("Mineshafts", buildRule(1, GrammarData.FEATURE_MEDIUM, asMyst("Mineshafts")));

		addRuleInternal("Ravines", buildRule(1, GrammarData.FEATURE_MEDIUM, asMyst("Ravines")));
		addRuleInternal("TerModSpheres", buildRule(3, GrammarData.FEATURE_MEDIUM, GrammarData.BLOCK_STRUCTURE, asMyst("TerModSpheres")));
		addRuleInternal("Dungeons", buildRule(2, GrammarData.FEATURE_MEDIUM, asMyst("Dungeons")));
		addRuleInternal("GenSpikes", buildRule(3, GrammarData.FEATURE_MEDIUM, GrammarData.BLOCK_STRUCTURE, asMyst("GenSpikes")));

		addRuleInternal("StarFissure", buildRule(3, GrammarData.FEATURE_SMALL, asMyst("StarFissure")));
		addRuleInternal("Obelisks", buildRule(3, GrammarData.FEATURE_SMALL, GrammarData.BLOCK_STRUCTURE, asMyst("Obelisks")));
		addRuleInternal("LakesSurface", buildRule(1, GrammarData.FEATURE_SMALL, GrammarData.BLOCK_FLUID, asMyst("LakesSurface")));
		addRuleInternal("LakesDeep", buildRule(1, GrammarData.FEATURE_SMALL, GrammarRules.BLOCK_NONSOLID, asMyst("LakesDeep")));
		addRuleInternal("CryForm", buildRule(3, GrammarData.FEATURE_SMALL, GrammarData.BLOCK_CRYSTAL, asMyst("CryForm")));

		//XXX: Direct string references to primary grammar rules
		// The dummy features should take up the entire rule chain when generating
		// However, if the player wrote a dummy and some features (or multiple of the same dummy), everything should be able to connect to the grammar 
		addRuleInternal("FeatureLargeDummy", buildRule(5, asMyst("FeatureLarges0"), GrammarRules.FEATURE_LARGE_EXT, asMyst("FeatureLargeDummy")));
		addRuleInternal("FeatureMediumDummy", buildRule(5, asMyst("FeatureMediums0"), GrammarRules.FEATURE_MEDIUM_EXT, asMyst("FeatureMediumDummy")));
		addRuleInternal("FeatureSmallDummy", buildRule(null, asMyst("FeatureSmalls0"), GrammarRules.FEATURE_SMALL_EXT, asMyst("FeatureSmallDummy")));
		addRuleInternal("FeatureLargeDummy", buildRule(null, GrammarRules.FEATURE_LARGE_EXT, asMyst("FeatureLargeDummy")));
		addRuleInternal("FeatureMediumDummy", buildRule(null, GrammarRules.FEATURE_MEDIUM_EXT, asMyst("FeatureMediumDummy")));
		addRuleInternal("FeatureSmallDummy", buildRule(null, GrammarRules.FEATURE_SMALL_EXT, asMyst("FeatureSmallDummy")));
	}

	private static void addRuleInternal(String key, Rule rule) {
		addRule(asMyst(key), rule);
	}

	private static ResourceLocation asMyst(String path) {
		return new ResourceLocation(MystObjects.MystcraftModId, path);
	}

	private static void addRule(ResourceLocation string, Rule rule) {
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
	private static Rule buildRule(Integer rank, ResourceLocation parent, ResourceLocation... args) {
		ArrayList<ResourceLocation> list = CollectionUtils.buildList(args);
		return new Rule(parent, list, rank);
	}
}
