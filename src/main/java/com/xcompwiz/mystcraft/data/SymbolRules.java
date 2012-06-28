package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.util.CollectionUtils;

public class SymbolRules {

	public static void initialize() {
		addRule("ModNorth", buildRule(1.0F, IGrammarAPI.ANGLE_BASIC, "ModNorth"));
		addRule("ModSouth", buildRule(1.0F, IGrammarAPI.ANGLE_BASIC, "ModSouth"));
		addRule("ModEast", buildRule(1.0F, IGrammarAPI.ANGLE_BASIC, "ModEast"));
		addRule("ModWest", buildRule(1.0F, IGrammarAPI.ANGLE_BASIC, "ModWest"));

		addRule("ModRising", buildRule(1.0F, IGrammarAPI.PHASE_BASIC, "ModRising"));
		addRule("ModNoon", buildRule(1.0F, IGrammarAPI.PHASE_BASIC, "ModNoon"));
		addRule("ModSetting", buildRule(1.0F, IGrammarAPI.PHASE_BASIC, "ModSetting"));
		addRule("ModEnd", buildRule(1.0F, IGrammarAPI.PHASE_BASIC, "ModEnd"));

		addRule("ModZero", buildRule(0.5F, IGrammarAPI.PERIOD_BASIC, "ModZero"));
		addRule("ModHalf", buildRule(1.0F, IGrammarAPI.PERIOD_BASIC, "ModHalf"));
		addRule("ModFull", buildRule(1.0F, IGrammarAPI.PERIOD_BASIC, "ModFull"));
		addRule("ModDouble", buildRule(1.0F, IGrammarAPI.PERIOD_BASIC, "ModDouble"));

		addRule("ModGradient", buildRule(1.0F, IGrammarAPI.GRADIENT_BASIC, IGrammarAPI.COLOR_SEQ, IGrammarAPI.PERIOD_SEQ, "ModGradient"));

		addRule("ColorHorizon", buildRule(0.5F, IGrammarAPI.SUNSET, GrammarRules.SUNSET_EXT, IGrammarAPI.GRADIENT_SEQ, "ColorHorizon"));

		addRule("NoSea", buildRule(0.4F, IGrammarAPI.BLOCK_SEA, "NoSea"));

		addRule("WeatherOff", buildRule(0.7F, IGrammarAPI.WEATHER, "WeatherOff"));
		addRule("WeatherStorm", buildRule(0.7F, IGrammarAPI.WEATHER, "WeatherStorm"));
		addRule("WeatherSnow", buildRule(0.7F, IGrammarAPI.WEATHER, "WeatherSnow"));
		addRule("WeatherRain", buildRule(0.7F, IGrammarAPI.WEATHER, "WeatherRain"));
		addRule("WeatherCloudy", buildRule(0.7F, IGrammarAPI.WEATHER, "WeatherCloudy"));
		addRule("WeatherOn", buildRule(0.7F, IGrammarAPI.WEATHER, "WeatherOn"));
		addRule("WeatherSlow", buildRule(0.8F, IGrammarAPI.WEATHER, "WeatherSlow"));
		addRule("WeatherNorm", buildRule(1.0F, IGrammarAPI.WEATHER, "WeatherNorm"));
		addRule("WeatherFast", buildRule(0.8F, IGrammarAPI.WEATHER, "WeatherFast"));

		addRule("Void", buildRule(0.0F, IGrammarAPI.TERRAIN, "Void"));
		addRule("TerrainNormal", buildRule(1.0F, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "TerrainNormal"));
		addRule("TerrainAmplified", buildRule(1.0F, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "TerrainAmplified"));
		addRule("TerrainNether", buildRule(0.3F, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "TerrainNether"));
		addRule("Flat", buildRule(0.5F, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "Flat"));
		addRule("TerrainEnd", buildRule(0.3F, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "TerrainEnd"));

		addRule("LightingNormal", buildRule(1.0F, IGrammarAPI.LIGHTING, "LightingNormal"));
		addRule("LightingDark", buildRule(0.5F, IGrammarAPI.LIGHTING, "LightingDark"));
		addRule("LightingBright", buildRule(0.5F, IGrammarAPI.LIGHTING, "LightingBright"));

		addRule("ColorFog", buildRule(1.5F, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.GRADIENT_SEQ, "ColorFog"));
		addRule("ColorSkyNight", buildRule(1.0F, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.GRADIENT_SEQ, "ColorSkyNight"));
		addRule("ColorSky", buildRule(2.0F, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.GRADIENT_SEQ, "ColorSky"));
		addRule("ColorCloud", buildRule(1.5F, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.GRADIENT_SEQ, "ColorCloud"));
		addRule("ColorGrass", buildRule(0.2F, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.COLOR_SEQ, "ColorGrass"));
		addRule("ColorFoliage", buildRule(0.2F, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.COLOR_SEQ, "ColorFoliage"));
		addRule("ColorWater", buildRule(0.2F, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.COLOR_SEQ, "ColorWater"));
		addRule("ColorFogNat", buildRule(1.5F, IGrammarAPI.VISUAL_EFFECT, "ColorFogNat"));
		addRule("ColorSkyNat", buildRule(2.0F, IGrammarAPI.VISUAL_EFFECT, "ColorSkyNat"));
		addRule("ColorCloudNat", buildRule(1.5F, IGrammarAPI.VISUAL_EFFECT, "ColorCloudNat"));
		addRule("ColorGrassNat", buildRule(0.2F, IGrammarAPI.VISUAL_EFFECT, "ColorGrassNat"));
		addRule("ColorFoliageNat", buildRule(0.2F, IGrammarAPI.VISUAL_EFFECT, "ColorFoliageNat"));
		addRule("ColorWaterNat", buildRule(0.2F, IGrammarAPI.VISUAL_EFFECT, "ColorWaterNat"));
		addRule("NoHorizon", buildRule(0.2F, IGrammarAPI.VISUAL_EFFECT, "NoHorizon"));
		addRule("Rainbow", buildRule(0.1F, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.ANGLE_SEQ, "Rainbow"));

		addRule("EnvScorch", buildRule(0.0F, IGrammarAPI.EFFECT, "EnvScorch"));
		addRule("EnvMeteor", buildRule(0.0F, IGrammarAPI.EFFECT, "EnvMeteor"));
		addRule("EnvLightning", buildRule(0.1F, IGrammarAPI.EFFECT, IGrammarAPI.GRADIENT_SEQ, "EnvLightning"));
		addRule("EnvExplosions", buildRule(0.0F, IGrammarAPI.EFFECT, "EnvExplosions"));
		addRule("EnvAccel", buildRule(0.0F, IGrammarAPI.EFFECT, "EnvAccel"));

		addRule("StarsEndSky", buildRule(0.3F, IGrammarAPI.STARFIELD, IGrammarAPI.GRADIENT_SEQ, "StarsEndSky"));
		addRule("StarsNormal", buildRule(2.0F, IGrammarAPI.STARFIELD, IGrammarAPI.GRADIENT_SEQ, IGrammarAPI.PERIOD_SEQ, IGrammarAPI.ANGLE_SEQ, "StarsNormal"));
		addRule("StarsTwinkle", buildRule(1.0F, IGrammarAPI.STARFIELD, IGrammarAPI.GRADIENT_SEQ, IGrammarAPI.PERIOD_SEQ, IGrammarAPI.ANGLE_SEQ, "StarsTwinkle"));
		addRule("StarsDark", buildRule(0.2F, IGrammarAPI.STARFIELD, "StarsDark"));
		addRule("SunNormal", buildRule(2.0F, IGrammarAPI.SUN, IGrammarAPI.SUNSET, IGrammarAPI.PERIOD_SEQ, IGrammarAPI.ANGLE_SEQ, IGrammarAPI.PHASE_SEQ, "SunNormal"));
		addRule("SunDark", buildRule(0.2F, IGrammarAPI.SUN, "SunDark"));
		addRule("MoonNormal", buildRule(2.0F, IGrammarAPI.MOON, IGrammarAPI.SUNSET_UNCOMMON, IGrammarAPI.PERIOD_SEQ, IGrammarAPI.ANGLE_SEQ, IGrammarAPI.PHASE_SEQ, "MoonNormal"));
		addRule("MoonDark", buildRule(0.2F, IGrammarAPI.MOON, "MoonDark"));

		addRule("BioConNative", buildRule(0.0F, IGrammarAPI.BIOMECONTROLLER, "BioConNative"));
		addRule("BioConSingle", buildRule(0.7F, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME, "BioConSingle"));
		addRule("BioConTiled", buildRule(0.8F, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, "BioConTiled"));
		addRule("BioConGrid", buildRule(0.8F, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, "BioConGrid"));
		addRule("BioConTiny", buildRule(0.8F, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConTiny"));
		addRule("BioConSmall", buildRule(0.9F, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConSmall"));
		addRule("BioConMedium", buildRule(1.0F, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConMedium"));
		addRule("BioConLarge", buildRule(0.8F, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConLarge"));
		addRule("BioConHuge", buildRule(0.8F, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConHuge"));

		addRule("FloatIslands", buildRule(0.4F, IGrammarAPI.TERRAINALT, IGrammarAPI.BIOME, IGrammarAPI.BLOCK_STRUCTURE, "FloatIslands"));
		addRule("Tendrils", buildRule(0.3F, IGrammarAPI.TERRAINALT, IGrammarAPI.BLOCK_STRUCTURE, "Tendrils"));
		addRule("Skylands", buildRule(0.1F, IGrammarAPI.TERRAINALT, "Skylands"));
		addRule("Ravines", buildRule(2.0F, IGrammarAPI.TERRAINALT, "Ravines"));
		addRule("Caves", buildRule(2.0F, IGrammarAPI.TERRAINALT, "Caves"));
		addRule("TerModSpheres", buildRule(0.5F, IGrammarAPI.TERRAINALT, IGrammarAPI.BLOCK_STRUCTURE, "TerModSpheres"));

		addRule("NetherFort", buildRule(0.5F, IGrammarAPI.POPULATOR, "NetherFort"));
		addRule("Villages", buildRule(1.0F, IGrammarAPI.POPULATOR, "Villages"));
		addRule("Strongholds", buildRule(1.0F, IGrammarAPI.POPULATOR, "Strongholds"));
		addRule("Mineshafts", buildRule(1.0F, IGrammarAPI.POPULATOR, "Mineshafts"));
		addRule("Dungeons", buildRule(1.0F, IGrammarAPI.POPULATOR, "Dungeons"));
		addRule("Obelisks", buildRule(0.6F, IGrammarAPI.POPULATOR, IGrammarAPI.BLOCK_STRUCTURE, "Obelisks"));
		addRule("DenseOres", buildRule(0.0F, IGrammarAPI.POPULATOR, "DenseOres"));

		addRule("StarFissure", buildRule(0.3F, IGrammarAPI.POPULATOR, "StarFissure"));
		addRule("LakesSurface", buildRule(1.0F, IGrammarAPI.POPULATOR, IGrammarAPI.BLOCK_FLUID, "LakesSurface"));
		addRule("LakesDeep", buildRule(1.0F, IGrammarAPI.POPULATOR, GrammarRules.BLOCK_NONSOLID, "LakesDeep"));
		addRule("HugeTrees", buildRule(0.1F, IGrammarAPI.POPULATOR, "HugeTrees"));

		addRule("GenSpikes", buildRule(0.6F, IGrammarAPI.POPULATOR, IGrammarAPI.BLOCK_STRUCTURE, "GenSpikes"));
		addRule("CryForm", buildRule(0.6F, IGrammarAPI.POPULATOR, IGrammarAPI.BLOCK_CRYSTAL, "CryForm"));
	}

	public static void register() {
		ArrayList<IAgeSymbol> symbols = SymbolManager.getAgeSymbols();
		for (IAgeSymbol symbol : symbols) {
			if (symbol instanceof SymbolBase) {
				ArrayList<Rule> rules = ((SymbolBase) symbol).getRules();
				if (rules == null) continue;
				for (Rule rule : rules) {
					GrammarGenerator.registerRule(rule);
				}
			}
		}
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
	private static Rule buildRule(float rarity, String parent, String... args) {
		ArrayList<String> list = CollectionUtils.buildList(args);
		return new Rule(parent, list, rarity);
	}
}
