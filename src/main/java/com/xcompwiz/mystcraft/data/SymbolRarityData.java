package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class SymbolRarityData {

	public static void initialize() {
		setSymbolRarity("ModNorth", 0.98F);
		setSymbolRarity("ModSouth", 0.98F);
		setSymbolRarity("ModEast", 0.98F);
		setSymbolRarity("ModWest", 0.98F);

		setSymbolRarity("ModRising", 1F);
		setSymbolRarity("ModNoon", 0.96F);
		setSymbolRarity("ModSetting", 0.98F);
		setSymbolRarity("ModEnd", 0.95F);

		setSymbolRarity("ModZero", 0.87F);
		setSymbolRarity("ModHalf", 0.95F);
		setSymbolRarity("ModFull", 0.96F);
		setSymbolRarity("ModDouble", 0.95F);

		setSymbolRarity("ModGradient", 0.93F);

		setSymbolRarity("ColorHorizon", 0.93F);

		setSymbolRarity("NoSea", 0.96F);

		setSymbolRarity("ModClear", 1F);

		setSymbolRarity("PvPOff", 0);

		setSymbolRarity("WeatherOff", 0.76F);
		setSymbolRarity("WeatherStorm", 0.82F);
		setSymbolRarity("WeatherSnow", 0.82F);
		setSymbolRarity("WeatherRain", 0.82F);
		setSymbolRarity("WeatherCloudy", 0.82F);
		setSymbolRarity("WeatherOn", 0.82F);
		setSymbolRarity("WeatherSlow", 0.89F);
		setSymbolRarity("WeatherNorm", 0.93F);
		setSymbolRarity("WeatherFast", 0.89F);

		setSymbolRarity("Void", 0.73F);
		setSymbolRarity("TerrainNormal", 0.93F);
		setSymbolRarity("TerrainNether", 0.56F);
		setSymbolRarity("Flat", 0.64F);
		setSymbolRarity("TerrainEnd", 0.73F);
		setSymbolRarity("TerrainAmplified", 0.56F);

		setSymbolRarity("LightingNormal", 0.93F);
		setSymbolRarity("LightingDark", 0.81F);
		setSymbolRarity("LightingBright", 0.75F);

		setSymbolRarity("ColorFog", 0.98F);
		setSymbolRarity("ColorSkyNight", 0.92F);
		setSymbolRarity("ColorSky", 0.96F);
		setSymbolRarity("ColorCloud", 1F);
		setSymbolRarity("ColorGrass", 0.95F);
		setSymbolRarity("ColorFoliage", 0.96F);
		setSymbolRarity("ColorWater", 0.93F);

		setSymbolRarity("EnvScorch", 0.4F);
		setSymbolRarity("EnvMeteor", 0.07F);
		setSymbolRarity("EnvLightning", 0.42F);
		setSymbolRarity("EnvExplosions", 0.46F);
		setSymbolRarity("EnvAccel", 0.35F);
		setSymbolRarity("DenseOres", 0.31F);

		setSymbolRarity("NoHorizon", 0.84F);
		setSymbolRarity("StarsEndSky", 0.26F);
		setSymbolRarity("StarsNormal", 0.95F);
		setSymbolRarity("StarsTwinkle", 0.95F);
		setSymbolRarity("StarsDark", 0.64F);
		setSymbolRarity("SunNormal", 0.92F);
		setSymbolRarity("SunDark", 0.73F);
		setSymbolRarity("MoonNormal", 0.93F);
		setSymbolRarity("MoonDark", 0.78F);
		setSymbolRarity("Rainbow", 0.73F);

		setSymbolRarity("BioConNative", 0.7F);
		setSymbolRarity("BioConSingle", 0.93F);
		setSymbolRarity("BioConTiled", 0.78F);
		setSymbolRarity("BioConTiny", 0.71F);
		setSymbolRarity("BioConSmall", 0.79F);
		setSymbolRarity("BioConMedium", 0.93F);
		setSymbolRarity("BioConLarge", 0.85F);
		setSymbolRarity("BioConHuge", 0.9F);

		setSymbolRarity("Tendrils", 0.6F);
		setSymbolRarity("Skylands", 0.7F);
		setSymbolRarity("Ravines", 1F);
		setSymbolRarity("Caves", 1F);
		setSymbolRarity("TerModSpheres", 0.6F);
		setSymbolRarity("FloatIslands", 0.6F);

		setSymbolRarity("NetherFort", 0.7F);
		setSymbolRarity("Villages", 0.8F);
		setSymbolRarity("Strongholds", 0.8F);
		setSymbolRarity("Mineshafts", 0.8F);
		setSymbolRarity("Dungeons", 0.8F);
		setSymbolRarity("Obelisks", 0.96F);

		setSymbolRarity("StarFissure", 0.7F);
		setSymbolRarity("LakesSurface", 0.96F);
		setSymbolRarity("LakesDeep", 0.96F);
		setSymbolRarity("HugeTrees", 0.8F);
		setSymbolRarity("CryForm", 0.6F);
		setSymbolRarity("GenSpikes", 0.6F);
	}

	private static boolean setSymbolRarity(String symbolId, float rarity) {
		IAgeSymbol isymbol = SymbolManager.getAgeSymbol(symbolId);
		if (isymbol == null) return false;
		if (isymbol instanceof SymbolBase) {
			SymbolBase symbol = (SymbolBase) isymbol;
			symbol.setRarity(rarity);
			return true;
		}
		return false;
	}

}
