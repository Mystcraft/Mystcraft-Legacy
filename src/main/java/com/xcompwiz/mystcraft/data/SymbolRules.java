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
		addRule("ModNorth", buildRule(1, IGrammarAPI.ANGLE_BASIC, "ModNorth"));
		addRule("ModSouth", buildRule(1, IGrammarAPI.ANGLE_BASIC, "ModSouth"));
		addRule("ModEast", buildRule(1, IGrammarAPI.ANGLE_BASIC, "ModEast"));
		addRule("ModWest", buildRule(1, IGrammarAPI.ANGLE_BASIC, "ModWest"));

		addRule("ModRising", buildRule(1, IGrammarAPI.PHASE_BASIC, "ModRising"));
		addRule("ModNoon", buildRule(1, IGrammarAPI.PHASE_BASIC, "ModNoon"));
		addRule("ModSetting", buildRule(1, IGrammarAPI.PHASE_BASIC, "ModSetting"));
		addRule("ModEnd", buildRule(1, IGrammarAPI.PHASE_BASIC, "ModEnd"));

		addRule("ModZero", buildRule(2, IGrammarAPI.PERIOD_BASIC, "ModZero"));
		addRule("ModHalf", buildRule(1, IGrammarAPI.PERIOD_BASIC, "ModHalf"));
		addRule("ModFull", buildRule(1, IGrammarAPI.PERIOD_BASIC, "ModFull"));
		addRule("ModDouble", buildRule(1, IGrammarAPI.PERIOD_BASIC, "ModDouble"));

		addRule("ModGradient", buildRule(1, IGrammarAPI.GRADIENT_BASIC, IGrammarAPI.COLOR_SEQ, IGrammarAPI.PERIOD_SEQ, "ModGradient"));

		addRule("ColorHorizon", buildRule(2, IGrammarAPI.SUNSET, GrammarRules.SUNSET_EXT, IGrammarAPI.GRADIENT_SEQ, "ColorHorizon"));

		addRule("NoSea", buildRule(2, IGrammarAPI.BLOCK_SEA, "NoSea"));

		addRule("WeatherOff", buildRule(2, IGrammarAPI.WEATHER, "WeatherOff"));
		addRule("WeatherStorm", buildRule(2, IGrammarAPI.WEATHER, "WeatherStorm"));
		addRule("WeatherSnow", buildRule(2, IGrammarAPI.WEATHER, "WeatherSnow"));
		addRule("WeatherRain", buildRule(2, IGrammarAPI.WEATHER, "WeatherRain"));
		addRule("WeatherCloudy", buildRule(2, IGrammarAPI.WEATHER, "WeatherCloudy"));
		addRule("WeatherOn", buildRule(2, IGrammarAPI.WEATHER, "WeatherOn"));
		addRule("WeatherSlow", buildRule(2, IGrammarAPI.WEATHER, "WeatherSlow"));
		addRule("WeatherNorm", buildRule(1, IGrammarAPI.WEATHER, "WeatherNorm"));
		addRule("WeatherFast", buildRule(2, IGrammarAPI.WEATHER, "WeatherFast"));

		addRule("Void", buildRule(3, IGrammarAPI.TERRAIN, "Void"));
		addRule("TerrainNormal", buildRule(1, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "TerrainNormal"));
		addRule("TerrainAmplified", buildRule(3, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "TerrainAmplified"));
		addRule("TerrainNether", buildRule(3, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "TerrainNether"));
		addRule("Flat", buildRule(2, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "Flat"));
		addRule("TerrainEnd", buildRule(3, IGrammarAPI.TERRAIN, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SEA, "TerrainEnd"));

		addRule("LightingNormal", buildRule(1, IGrammarAPI.LIGHTING, "LightingNormal"));
		addRule("LightingDark", buildRule(2, IGrammarAPI.LIGHTING, "LightingDark"));
		addRule("LightingBright", buildRule(2, IGrammarAPI.LIGHTING, "LightingBright"));

		addRule("ColorFog", buildRule(3, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.GRADIENT_SEQ, "ColorFog"));
		addRule("ColorSkyNight", buildRule(3, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.GRADIENT_SEQ, "ColorSkyNight"));
		addRule("ColorSky", buildRule(3, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.GRADIENT_SEQ, "ColorSky"));
		addRule("ColorCloud", buildRule(3, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.GRADIENT_SEQ, "ColorCloud"));
		addRule("ColorGrass", buildRule(3, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.COLOR_SEQ, "ColorGrass"));
		addRule("ColorFoliage", buildRule(3, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.COLOR_SEQ, "ColorFoliage"));
		addRule("ColorWater", buildRule(3, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.COLOR_SEQ, "ColorWater"));
		addRule("ColorFogNat", buildRule(2, IGrammarAPI.VISUAL_EFFECT, "ColorFogNat"));
		addRule("ColorSkyNat", buildRule(2, IGrammarAPI.VISUAL_EFFECT, "ColorSkyNat"));
		addRule("ColorCloudNat", buildRule(2, IGrammarAPI.VISUAL_EFFECT, "ColorCloudNat"));
		addRule("ColorGrassNat", buildRule(2, IGrammarAPI.VISUAL_EFFECT, "ColorGrassNat"));
		addRule("ColorFoliageNat", buildRule(2, IGrammarAPI.VISUAL_EFFECT, "ColorFoliageNat"));
		addRule("ColorWaterNat", buildRule(2, IGrammarAPI.VISUAL_EFFECT, "ColorWaterNat"));
		addRule("NoHorizon", buildRule(2, IGrammarAPI.VISUAL_EFFECT, "NoHorizon"));
		addRule("Rainbow", buildRule(4, IGrammarAPI.VISUAL_EFFECT, IGrammarAPI.ANGLE_SEQ, "Rainbow"));

		addRule("EnvScorch", buildRule(null, IGrammarAPI.EFFECT, "EnvScorch"));
		addRule("EnvMeteor", buildRule(null, IGrammarAPI.EFFECT, "EnvMeteor"));
		addRule("EnvLightning", buildRule(null, IGrammarAPI.EFFECT, IGrammarAPI.GRADIENT_SEQ, "EnvLightning"));
		addRule("EnvExplosions", buildRule(null, IGrammarAPI.EFFECT, "EnvExplosions"));
		addRule("EnvAccel", buildRule(null, IGrammarAPI.EFFECT, "EnvAccel"));

		addRule("StarsEndSky", buildRule(4, IGrammarAPI.STARFIELD, IGrammarAPI.GRADIENT_SEQ, "StarsEndSky"));
		addRule("StarsNormal", buildRule(1, IGrammarAPI.STARFIELD, IGrammarAPI.GRADIENT_SEQ, IGrammarAPI.PERIOD_SEQ, IGrammarAPI.ANGLE_SEQ, "StarsNormal"));
		addRule("StarsTwinkle", buildRule(2, IGrammarAPI.STARFIELD, IGrammarAPI.GRADIENT_SEQ, IGrammarAPI.PERIOD_SEQ, IGrammarAPI.ANGLE_SEQ, "StarsTwinkle"));
		addRule("StarsDark", buildRule(3, IGrammarAPI.STARFIELD, "StarsDark"));
		addRule("SunNormal", buildRule(1, IGrammarAPI.SUN, IGrammarAPI.SUNSET, IGrammarAPI.PERIOD_SEQ, IGrammarAPI.ANGLE_SEQ, IGrammarAPI.PHASE_SEQ, "SunNormal"));
		addRule("SunDark", buildRule(3, IGrammarAPI.SUN, "SunDark"));
		addRule("MoonNormal", buildRule(1, IGrammarAPI.MOON, IGrammarAPI.SUNSET_UNCOMMON, IGrammarAPI.PERIOD_SEQ, IGrammarAPI.ANGLE_SEQ, IGrammarAPI.PHASE_SEQ, "MoonNormal"));
		addRule("MoonDark", buildRule(3, IGrammarAPI.MOON, "MoonDark"));

		addRule("BioConNative", buildRule(1, IGrammarAPI.BIOMECONTROLLER, "BioConNative"));
		addRule("BioConSingle", buildRule(1, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME, "BioConSingle"));
		addRule("BioConTiled", buildRule(2, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, "BioConTiled"));
		addRule("BioConGrid", buildRule(2, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, "BioConGrid"));
		addRule("BioConTiny", buildRule(2, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConTiny"));
		addRule("BioConSmall", buildRule(2, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConSmall"));
		addRule("BioConMedium", buildRule(1, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConMedium"));
		addRule("BioConLarge", buildRule(2, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConLarge"));
		addRule("BioConHuge", buildRule(2, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.BIOME_LIST, IGrammarAPI.BIOME, IGrammarAPI.BIOME, "BioConHuge"));

		addRule("FloatIslands", buildRule(4, IGrammarAPI.TERRAINALT, IGrammarAPI.BIOME, IGrammarAPI.BLOCK_STRUCTURE, "FloatIslands"));
		addRule("Tendrils", buildRule(4, IGrammarAPI.TERRAINALT, IGrammarAPI.BLOCK_STRUCTURE, "Tendrils"));
		addRule("Skylands", buildRule(5, IGrammarAPI.TERRAINALT, "Skylands"));
		addRule("Ravines", buildRule(1, IGrammarAPI.TERRAINALT, "Ravines"));
		addRule("Caves", buildRule(1, IGrammarAPI.TERRAINALT, "Caves"));
		addRule("TerModSpheres", buildRule(3, IGrammarAPI.TERRAINALT, IGrammarAPI.BLOCK_STRUCTURE, "TerModSpheres"));

		addRule("NetherFort", buildRule(2, IGrammarAPI.POPULATOR, "NetherFort"));
		addRule("Villages", buildRule(1, IGrammarAPI.POPULATOR, "Villages"));
		addRule("Strongholds", buildRule(1, IGrammarAPI.POPULATOR, "Strongholds"));
		addRule("Mineshafts", buildRule(1, IGrammarAPI.POPULATOR, "Mineshafts"));
		addRule("Dungeons", buildRule(2, IGrammarAPI.POPULATOR, "Dungeons"));
		addRule("Obelisks", buildRule(3, IGrammarAPI.POPULATOR, IGrammarAPI.BLOCK_STRUCTURE, "Obelisks"));
		addRule("DenseOres", buildRule(null, IGrammarAPI.POPULATOR, "DenseOres"));

		addRule("StarFissure", buildRule(3, IGrammarAPI.POPULATOR, "StarFissure"));
		addRule("LakesSurface", buildRule(1, IGrammarAPI.POPULATOR, IGrammarAPI.BLOCK_FLUID, "LakesSurface"));
		addRule("LakesDeep", buildRule(1, IGrammarAPI.POPULATOR, GrammarRules.BLOCK_NONSOLID, "LakesDeep"));
		addRule("HugeTrees", buildRule(2, IGrammarAPI.POPULATOR, "HugeTrees"));

		addRule("GenSpikes", buildRule(3, IGrammarAPI.POPULATOR, IGrammarAPI.BLOCK_STRUCTURE, "GenSpikes"));
		addRule("CryForm", buildRule(3, IGrammarAPI.POPULATOR, IGrammarAPI.BLOCK_CRYSTAL, "CryForm"));
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
	private static Rule buildRule(Integer rank, String parent, String... args) {
		ArrayList<String> list = CollectionUtils.buildList(args);
		return new Rule(parent, list, rank);
	}
}
