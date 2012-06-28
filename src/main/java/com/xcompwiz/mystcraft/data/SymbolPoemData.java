package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class SymbolPoemData {

	public static void initialize() {
		WordData.init();
		for (int i = 0; i < 26; ++i) {
			InternalAPI.symbol.registerWord("" + i, constructNumber(i));
		}

		setSymbolWords("WeatherCloudy", new String[] { WordData.Sustain, WordData.Static, WordData.Believe, WordData.Motion });
		setSymbolWords("WeatherFast", new String[] { WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Spur });
		setSymbolWords("WeatherNorm", new String[] { WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Balance });
		setSymbolWords("WeatherOn", new String[] { WordData.Sustain, WordData.Static, WordData.Tradition, WordData.Stimulate });
		setSymbolWords("WeatherRain", new String[] { WordData.Sustain, WordData.Static, WordData.Rebirth, WordData.Growth });
		setSymbolWords("WeatherSlow", new String[] { WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Inhibit });
		setSymbolWords("WeatherSnow", new String[] { WordData.Sustain, WordData.Static, WordData.Inhibit, WordData.Energy });
		setSymbolWords("WeatherStorm", new String[] { WordData.Sustain, WordData.Static, WordData.Nature, WordData.Power });
		setSymbolWords("WeatherOff", new String[] { WordData.Sustain, WordData.Static, WordData.Stimulate, WordData.Energy });

		setSymbolWords("TerrainNormal", new String[] { WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Flow });
		setSymbolWords("TerrainAmplified", new String[] { WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Spur });
		setSymbolWords("TerrainEnd", new String[] { WordData.Terrain, WordData.Form, WordData.Ethereal, WordData.Flow });
		setSymbolWords("TerrainNether", new String[] { WordData.Terrain, WordData.Form, WordData.Constraint, WordData.Entropy });
		setSymbolWords("Flat", new String[] { WordData.Terrain, WordData.Form, WordData.Inhibit, WordData.Motion });
		setSymbolWords("Void", new String[] { WordData.Terrain, WordData.Form, WordData.Infinite, WordData.Void });

		setSymbolWords("BioConHuge", new String[] { WordData.Constraint, WordData.Nature, WordData.Weave, "Huge" });
		setSymbolWords("BioConLarge", new String[] { WordData.Constraint, WordData.Nature, WordData.Weave, "Large" });
		setSymbolWords("BioConMedium", new String[] { WordData.Constraint, WordData.Nature, WordData.Weave, "Medium" });
		setSymbolWords("BioConSmall", new String[] { WordData.Constraint, WordData.Nature, WordData.Weave, "Small" });
		setSymbolWords("BioConTiny", new String[] { WordData.Constraint, WordData.Nature, WordData.Weave, "Tiny" });
		setSymbolWords("BioConSingle", new String[] { WordData.Constraint, WordData.Nature, WordData.Infinite, WordData.Static });
		setSymbolWords("BioConNative", new String[] { WordData.Constraint, WordData.Nature, WordData.Tradition, WordData.Sustain });
		setSymbolWords("BioConTiled", new String[] { WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Contradict });
		setSymbolWords("BioConGrid", new String[] { WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Mutual });

		setSymbolWords("ColorCloud", new String[] { WordData.Image, WordData.Entropy, WordData.Believe, WordData.Weave });
		setSymbolWords("ColorFog", new String[] { WordData.Image, WordData.Entropy, WordData.Explore, WordData.Weave });
		setSymbolWords("ColorSky", new String[] { WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Weave });
		setSymbolWords("ColorSkyNight", new String[] { WordData.Image, WordData.Celestial, WordData.Contradict, WordData.Weave });
		setSymbolWords("ColorFoliage", new String[] { WordData.Image, WordData.Growth, WordData.Elevate, WordData.Weave });
		setSymbolWords("ColorGrass", new String[] { WordData.Image, WordData.Growth, WordData.Resilience, WordData.Weave });
		setSymbolWords("ColorWater", new String[] { WordData.Image, WordData.Flow, WordData.Constraint, WordData.Weave });
		setSymbolWords("ColorCloudNat", new String[] { WordData.Image, WordData.Entropy, WordData.Believe, WordData.Nature });
		setSymbolWords("ColorFogNat", new String[] { WordData.Image, WordData.Entropy, WordData.Explore, WordData.Nature });
		setSymbolWords("ColorSkyNat", new String[] { WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Nature });
		setSymbolWords("ColorFoliageNat", new String[] { WordData.Image, WordData.Growth, WordData.Elevate, WordData.Nature });
		setSymbolWords("ColorGrassNat", new String[] { WordData.Image, WordData.Growth, WordData.Resilience, WordData.Nature });
		setSymbolWords("ColorWaterNat", new String[] { WordData.Image, WordData.Flow, WordData.Constraint, WordData.Nature });

		setSymbolWords("EnvAccel", new String[] { WordData.Environment, WordData.Dynamic, WordData.Change, WordData.Spur });
		setSymbolWords("EnvExplosions", new String[] { WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Force });
		setSymbolWords("EnvLightning", new String[] { WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Energy });
		setSymbolWords("EnvMeteor", new String[] { WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Momentum });
		setSymbolWords("EnvScorch", new String[] { WordData.Environment, WordData.Sacrifice, WordData.Power, WordData.Chaos });

		setSymbolWords("ModClear", new String[] { WordData.Contradict, WordData.Transform, WordData.Change, WordData.Void });
		setSymbolWords("PvPOff", new String[] { WordData.Chain, WordData.Chaos, WordData.Encourage, WordData.Harmony });

		setSymbolWords("ModZero", new String[] { WordData.Modifier, WordData.Time, WordData.System, WordData.Inhibit });
		setSymbolWords("ModHalf", new String[] { WordData.Modifier, WordData.Time, WordData.System, WordData.Stimulate });
		setSymbolWords("ModDouble", new String[] { WordData.Modifier, WordData.Time, WordData.System, WordData.Sacrifice });
		setSymbolWords("ModFull", new String[] { WordData.Modifier, WordData.Time, WordData.System, WordData.Balance });

		setSymbolWords("ModNorth", new String[] { WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Control });
		setSymbolWords("ModSouth", new String[] { WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Chaos });
		setSymbolWords("ModEast", new String[] { WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Tradition });
		setSymbolWords("ModWest", new String[] { WordData.Modifier, WordData.Flow, WordData.Motion, WordData.Change });

		setSymbolWords("ModEnd", new String[] { WordData.Modifier, WordData.Cycle, WordData.System, WordData.Rebirth });
		setSymbolWords("ModRising", new String[] { WordData.Modifier, WordData.Cycle, WordData.System, WordData.Growth });
		setSymbolWords("ModNoon", new String[] { WordData.Modifier, WordData.Cycle, WordData.System, WordData.Harmony });
		setSymbolWords("ModSetting", new String[] { WordData.Modifier, WordData.Cycle, WordData.System, WordData.Future });

		setSymbolWords("ModGradient", new String[] { WordData.Modifier, WordData.Image, WordData.Merge, WordData.Weave });
		setSymbolWords("ColorHorizon", new String[] { WordData.Modifier, WordData.Image, WordData.Celestial, WordData.Change });
		setSymbolWords("NoSea", new String[] { WordData.Modifier, WordData.Constraint, WordData.Flow, WordData.Inhibit });

		setSymbolWords("LightingNormal", new String[] { WordData.Ethereal, WordData.Dynamic, WordData.Cycle, WordData.Balance });
		setSymbolWords("LightingBright", new String[] { WordData.Ethereal, WordData.Power, WordData.Infinite, WordData.Spur });
		setSymbolWords("LightingDark", new String[] { WordData.Ethereal, WordData.Void, WordData.Constraint, WordData.Inhibit });

		setSymbolWords("GenSpikes", new String[] { WordData.Nature, WordData.Encourage, WordData.Entropy, WordData.Structure });
		setSymbolWords("CryForm", new String[] { WordData.Nature, WordData.Encourage, WordData.Growth, WordData.Structure });
		setSymbolWords("HugeTrees", new String[] { WordData.Nature, WordData.Stimulate, WordData.Spur, WordData.Elevate });
		setSymbolWords("LakesSurface", new String[] { WordData.Nature, WordData.Flow, WordData.Static, WordData.Elevate });
		setSymbolWords("LakesDeep", new String[] { WordData.Nature, WordData.Flow, WordData.Static, WordData.Explore });
		setSymbolWords("StarFissure", new String[] { WordData.Nature, WordData.Harmony, WordData.Mutual, WordData.Void });
		setSymbolWords("DenseOres", new String[] { WordData.Environment, WordData.Stimulate, WordData.Machine, WordData.Chaos });

		setSymbolWords("SunNormal", new String[] { WordData.Celestial, WordData.Image, WordData.Stimulate, WordData.Energy });
		setSymbolWords("SunDark", new String[] { WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Energy });
		setSymbolWords("MoonNormal", new String[] { WordData.Celestial, WordData.Image, WordData.Cycle, WordData.Wisdom });
		setSymbolWords("MoonDark", new String[] { WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Wisdom });
		setSymbolWords("StarsNormal", new String[] { WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Order });
		setSymbolWords("StarsTwinkle", new String[] { WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Entropy });
		setSymbolWords("StarsDark", new String[] { WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Order });
		setSymbolWords("StarsEndSky", new String[] { WordData.Celestial, WordData.Image, WordData.Chaos, WordData.Weave });
		setSymbolWords("Rainbow", new String[] { WordData.Celestial, WordData.Image, WordData.Harmony, WordData.Balance });
		setSymbolWords("NoHorizon", new String[] { WordData.Celestial, WordData.Inhibit, WordData.Image, WordData.Void });

		setSymbolWords("Obelisks", new String[] { WordData.Civilization, WordData.Resilience, WordData.Static, WordData.Form });
		setSymbolWords("Dungeons", new String[] { WordData.Civilization, WordData.Constraint, WordData.Chain, WordData.Resurrect });
		setSymbolWords("Strongholds", new String[] { WordData.Civilization, WordData.Wisdom, WordData.Future, WordData.Honor });
		setSymbolWords("Villages", new String[] { WordData.Civilization, WordData.Society, WordData.Harmony, WordData.Nurture });
		setSymbolWords("NetherFort", new String[] { WordData.Civilization, WordData.Machine, WordData.Power, WordData.Entropy });
		setSymbolWords("Mineshafts", new String[] { WordData.Civilization, WordData.Machine, WordData.Motion, WordData.Tradition });

		setSymbolWords("Tendrils", new String[] { WordData.Terrain, WordData.Transform, WordData.Growth, WordData.Flow });
		setSymbolWords("TerModSpheres", new String[] { WordData.Terrain, WordData.Transform, WordData.Form, WordData.Cycle });
		setSymbolWords("Ravines", new String[] { WordData.Terrain, WordData.Transform, WordData.Void, WordData.Weave });
		setSymbolWords("Skylands", new String[] { WordData.Terrain, WordData.Transform, WordData.Void, WordData.Elevate });
		setSymbolWords("Caves", new String[] { WordData.Terrain, WordData.Transform, WordData.Void, WordData.Flow });
		setSymbolWords("FloatIslands", new String[] { WordData.Terrain, WordData.Transform, WordData.Form, WordData.Celestial });
	}

	private static boolean setSymbolWords(String symbolId, String[] words) {
		IAgeSymbol isymbol = SymbolManager.getAgeSymbol(symbolId);
		if (isymbol == null) {
			LoggerUtils.info("Failed to set poem for symbol " + symbolId);
			return false;
		}
		if (isymbol instanceof SymbolBase) {
			SymbolBase symbol = (SymbolBase) isymbol;
			symbol.setWords(words);
			return true;
		}
		return false;
	}

	private static Integer[] constructNumber(int num) {
		int first = 0;
		if (num == 0) return new Integer[] { 1 };
		else if (num >= 25) return new Integer[] { 2 };
		else if (num >= 20) first = 63;
		else if (num >= 15) first = 62;
		else if (num >= 10) first = 61;
		else if (num >= 5) first = 60;
		int second = 0;
		if (num % 5 > 0) {
			second = num % 5 + 55;
		}
		if (first > 0) {
			if (second > 0) return new Integer[] { first, second };
			return new Integer[] { first };
		}
		return new Integer[] { second };
	}

}
