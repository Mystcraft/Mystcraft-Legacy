package com.xcompwiz.mystcraft.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.page.Page;

public class SymbolRemappings {

	private static HashMap<String, List<String>>	mappings	= new HashMap<String, List<String>>();

	public static void initialize() {
		fogColorRemappings();
		cloudColorRemappings();
		skyColorRemappings();
		horizonColorRemappings();

		addSymbolRemapping("ModMaroon", "ModColorMaroon");
		addSymbolRemapping("ModRed", "ModColorRed");
		addSymbolRemapping("ModOlive", "ModColorOlive");
		addSymbolRemapping("ModYellow", "ModColorYellow");
		addSymbolRemapping("ModDark Green", "ModColorDarkGreen");
		addSymbolRemapping("ModGreen", "ModColorGreen");
		addSymbolRemapping("ModTeal", "ModColorTeal");
		addSymbolRemapping("ModCyan", "ModColorCyan");
		addSymbolRemapping("ModNavy", "ModColorNavy");
		addSymbolRemapping("ModBlue", "ModColorBlue");
		addSymbolRemapping("ModPurple", "ModColorPurple");
		addSymbolRemapping("ModMagenta", "ModColorMagenta");
		addSymbolRemapping("ModBlack", "ModColorBlack");
		addSymbolRemapping("ModGrey", "ModColorGrey");
		addSymbolRemapping("ModSilver", "ModColorSilver");
		addSymbolRemapping("ModWhite", "ModColorWhite");

		addSymbolRemapping("LavaLakes", "ModMat_tile.lava", "LakesDeep");
		addSymbolRemapping("Lakes", "ModMat_tile.water", "LakesSurface");

		addSymbolRemapping("CryFormCry", "ModMat_tile.myst.crystal", "CryForm");
		addSymbolRemapping("CryFormGlow", "ModMat_tile.myst.lightgem", "CryForm");
		addSymbolRemapping("CryFormQuartz", "ModMat_tile.myst.netherquartz", "CryForm");

		addSymbolRemapping("Standard Terrain", "TerrainNormal");
		addSymbolRemapping("Star Fissure", "StarFissure");

		addSymbolRemapping("Rain", "WeatherRain");
		addSymbolRemapping("Snow", "WeatherSnow");

		addSymbolRemapping("Huge Trees", "HugeTrees");

		addSymbolRemapping("NormalStars", "StarsNormal");

		addSymbolRemapping("Single Biome", "BioConSingle");

		addSymbolRemapping("Checkerboard Biomes", "BioConTiled");

		addSymbolRemapping("BiomeControllerNative", "BioConNative");

		addSymbolRemapping("Lava Lakes", "LavaLakes");

		addSymbolRemapping("WeatherSun", "WeatherOff");

		addSymbolRemapping("Standard Lighting", "LightingNormal");

		addSymbolRemapping("Storm", "WeatherStorm");

		addSymbolRemapping("Fog", "ColorFog");

		addSymbolRemapping("ModFluid_tile.lava", "ModMat_tile.lava");
		addSymbolRemapping("ModFluid_tile.water", "ModMat_tile.water");
		addSymbolRemapping("ModFluidtile.water", "ModMat_tile.water");
		addSymbolRemapping("ModFluidtile.lava", "ModMat_tile.lava");

		addSymbolRemapping("ModLavaSea", "ModMat_tile.lava");
		addSymbolRemapping("ModNetherTerrain", "ModMat_tile.hellrock");

		addSymbolRemapping("ModMattile.hellrock", "ModMat_tile.hellrock");
		addSymbolRemapping("ModMattile.whiteStone", "ModMat_tile.whiteStone");
		addSymbolRemapping("ModMattile.oreDiamond", "ModMat_tile.oreDiamond");

		addSymbolRemapping("TendrilsIce", "ModMat_tile.ice", "Tendrils");
		addSymbolRemapping("WoodCaves", "Tendrils");

		addSymbolRemapping("SkyDropDark", "StarsDark");

		// Fast Time
		addSymbolRemapping("FTime", "ModHalf", "SunNormal", "ModHalf", "MoonNormal");

		// Slow Time
		addSymbolRemapping("STime", "ModDouble", "SunNormal", "ModDouble", "MoonNormal");

		// Normal Time
		addSymbolRemapping("NTime", "ModFull", "SunNormal", "ModFull", "MoonNormal");

		// Eternal Dusk
		addSymbolRemapping("Dusk", "ModZero", "ModSetting", "SunNormal", "ModZero", "MoonNormal");

		// Eternal Night
		addSymbolRemapping("Night", "SunDark", "ModZero", "MoonNormal");

		// Eternal Day
		addSymbolRemapping("Day", "MoonDark", "ModZero", "ModNoon", "SunNormal");

		addSymbolRemapping("Heavy Resources", "DenseOres");

		addSymbolRemapping("SunsetNormal", "SunsetRed");

		// "CloudNormal" -> "CloudWhite"
		addSymbolRemapping("CloudNormal", "CloudWhite");

		addSymbolRemapping("Normal Sunset Colors", "SunsetRed");

		addSymbolRemapping("NativeBiomeController", "BioConLarge");

		addSymbolRemapping("Flat Sea", "TerrainFlat");

		addSymbolRemapping("Sky Islands", "Skylands");

		addSymbolRemapping("Tree Age", "Huge Trees", "TerrainFlat", "Swampland", "BioConSingle");

		addSymbolRemapping("DefaultBiome", "BioConSingle");

		addSymbolRemapping("DefaultLighting", "Standard Lighting");

		addSymbolRemapping("DefaultSunrise", "Normal Sunset Colors");

		addSymbolRemapping("DefaultTerrain", "Standard Terrain");

		addSymbolRemapping("Flat", "TerrainFlat");
		addSymbolRemapping("Void", "TerrainVoid");
	}

	private static void addChromaticGradients(List<String> mapping) {
		mapping.add("ModBlack");
		mapping.add("ModRed");
		mapping.add("ModRed");
		mapping.add("ModGradient");
		mapping.add("ModBlack");
		mapping.add("ModGreen");
		mapping.add("ModGreen");
		mapping.add("ModGradient");
		mapping.add("ModBlack");
		mapping.add("ModBlue");
		mapping.add("ModBlue");
		mapping.add("ModGradient");
	}

	private static void fogColorRemappings() {
		List<String> mapping = null;

		// FogChromatic
		mapping = new ArrayList<String>();
		addChromaticGradients(mapping);
		mapping.add("ColorFog");
		addSymbolRemapping("FogChromatic", mapping);

		// FogRed
		mapping = new ArrayList<String>();
		mapping.add("ModRed");
		mapping.add("ColorFog");
		addSymbolRemapping("FogRed", mapping);

		// FogGreen
		mapping = new ArrayList<String>();
		mapping.add("ModGreen");
		mapping.add("ColorFog");
		addSymbolRemapping("FogGreen", mapping);

		// FogBlue
		mapping = new ArrayList<String>();
		mapping.add("ModBlue");
		mapping.add("ColorFog");
		addSymbolRemapping("FogBlue", mapping);

		// FogBlack
		mapping = new ArrayList<String>();
		mapping.add("ModBlack");
		mapping.add("ColorFog");
		addSymbolRemapping("FogBlack", mapping);

		// FogWhite
		mapping = new ArrayList<String>();
		mapping.add("ModWhite");
		mapping.add("ColorFog");
		addSymbolRemapping("FogWhite", mapping);

		// FogNormal
		mapping = new ArrayList<String>();
		mapping.add("ModWhite");
		mapping.add("ColorFog");
		addSymbolRemapping("FogNormal", mapping);
	}

	private static void cloudColorRemappings() {
		List<String> mapping = null;

		// CloudChromatic
		mapping = new ArrayList<String>();
		addChromaticGradients(mapping);
		mapping.add("ColorCloud");
		addSymbolRemapping("CloudChromatic", mapping);

		// CloudRed
		mapping = new ArrayList<String>();
		mapping.add("ModRed");
		mapping.add("ColorCloud");
		addSymbolRemapping("CloudRed", mapping);

		// CloudGreen
		mapping = new ArrayList<String>();
		mapping.add("ModGreen");
		mapping.add("ColorCloud");
		addSymbolRemapping("CloudGreen", mapping);

		// CloudBlue
		mapping = new ArrayList<String>();
		mapping.add("ModBlue");
		mapping.add("ColorCloud");
		addSymbolRemapping("CloudBlue", mapping);

		// CloudBlack
		mapping = new ArrayList<String>();
		mapping.add("ModBlack");
		mapping.add("ColorCloud");
		addSymbolRemapping("CloudBlack", mapping);

		// CloudWhite
		mapping = new ArrayList<String>();
		mapping.add("ModWhite");
		mapping.add("ColorCloud");
		addSymbolRemapping("CloudWhite", "ModWhite", "ColorCloud");
	}

	private static void skyColorRemappings() {
		List<String> mapping = null;

		mapping = new ArrayList<String>();
		mapping.add("ModGradient");
		mapping.add("ColorSky");
		addSymbolRemapping("ModGradient_HERE", mapping);

		// SkyChromatic
		mapping = new ArrayList<String>();
		addChromaticGradients(mapping);
		mapping.add("ColorSky");
		addSymbolRemapping("SkyChromatic", mapping);

		// SkyRed
		mapping = new ArrayList<String>();
		mapping.add("ModRed");
		mapping.add("ColorSky");
		addSymbolRemapping("SkyRed", mapping);

		// SkyGreen
		mapping = new ArrayList<String>();
		mapping.add("ModGreen");
		mapping.add("ColorSky");
		addSymbolRemapping("SkyGreen", mapping);

		// SkyBlue
		mapping = new ArrayList<String>();
		mapping.add("ModBlue");
		mapping.add("ColorSky");
		addSymbolRemapping("SkyBlue", mapping);

		// SkyBlack
		mapping = new ArrayList<String>();
		mapping.add("ModBlack");
		mapping.add("ColorSky");
		addSymbolRemapping("SkyBlack", mapping);

		// SkyWhite
		mapping = new ArrayList<String>();
		mapping.add("ModWhite");
		mapping.add("ColorSky");
		addSymbolRemapping("SkyWhite", mapping);

		// SkyNormal
		mapping = new ArrayList<String>();
		mapping.add("ModBlue");
		mapping.add("ColorSky");
		addSymbolRemapping("SkyNormal", mapping);
	}

	private static void horizonColorRemappings() {
		List<String> mapping = null;

		// SunsetChromatic
		mapping = new ArrayList<String>();
		addChromaticGradients(mapping);
		mapping.add("ColorHorizon");
		addSymbolRemapping("SunsetChromatic", mapping);

		// SunsetRed
		mapping = new ArrayList<String>();
		mapping.add("ModRed");
		mapping.add("ColorHorizon");
		addSymbolRemapping("SunsetRed", mapping);

		// SunsetGreen
		mapping = new ArrayList<String>();
		mapping.add("ModGreen");
		mapping.add("ColorHorizon");
		addSymbolRemapping("SunsetGreen", mapping);

		// SunsetBlue
		mapping = new ArrayList<String>();
		mapping.add("ModBlue");
		mapping.add("ColorHorizon");
		addSymbolRemapping("SunsetBlue", mapping);

		// SunsetBlack
		mapping = new ArrayList<String>();
		mapping.add("ModBlack");
		mapping.add("ColorHorizon");
		addSymbolRemapping("SunsetBlack", mapping);

		// SunsetWhite
		mapping = new ArrayList<String>();
		mapping.add("ModWhite");
		mapping.add("ColorHorizon");
		addSymbolRemapping("SunsetWhite", mapping);
	}

	public static boolean hasRemapping(String symbol) {
		return mappings.get(symbol) != null;
	}

	public static List<String> remap(String symbol) {
		List<String> symbols = mappings.get(symbol);
		if (symbols == null) {
			symbols = new ArrayList<String>();
			symbols.add(symbol);
		}
		return symbols;
	}

	public static List<ItemStack> remap(ItemStack page) {
		List<ItemStack> result = new ArrayList<ItemStack>();
		if (page == null) return result;
		if (!(page.getItem() instanceof ItemPage)) return result;
		String symbol = Page.getSymbol(page);
		List<String> symbols = mappings.get(symbol);
		if (symbols != null) {
			for (String mapping : symbols) {
				result.add(Page.createSymbolPage(mapping));
			}
			if (symbols.size() > 0) {
				Page.setSymbol(page, Page.getSymbol(result.get(0)));
				result.set(0, page.copy());
			}
		} else {
			result.add(page);
		}
		return result;
	}

	public static <T> List<T> remap(List<T> collection) {
		for (int i = 0; i < collection.size();) {
			T element = collection.remove(i);
			List<T> mapping = null;
			if (element instanceof String) mapping = (List<T>) SymbolRemappings.remap((String)element);
			if (element instanceof ItemStack) mapping = (List<T>) SymbolRemappings.remap((ItemStack)element);
			if (mapping == null) continue;
			collection.addAll(i, mapping);
			if (mapping.size() > 0 && element.equals(mapping.get(0))) {
				++i;
			}
		}
		return collection;
	}

	private static void addSymbolRemapping(String id, List<String> mapping) {
		mappings.put(id, mapping);
	}

	public static void addSymbolRemapping(String id, String... newids) {
		List<String> mapping = new ArrayList<String>();
		for (String newid : newids) {
			mapping.add(newid);
		}
		mappings.put(id, mapping);
	}
}
