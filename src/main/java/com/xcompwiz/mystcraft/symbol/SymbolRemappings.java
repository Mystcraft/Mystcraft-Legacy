package com.xcompwiz.mystcraft.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SymbolRemappings {

	private static HashMap<ResourceLocation, List<ResourceLocation>> mappings = new HashMap<>();

	public static void initialize() {
		fogColorRemappings();
		cloudColorRemappings();
		skyColorRemappings();
		horizonColorRemappings();

		addSymbolRemappingInternal("ModMat_tile.stone", "ModMat_minecraft:stone_0");
		addSymbolRemappingInternal("ModMat_tile.lava", "ModMat_minecraft:flowing_lava_0");
		addSymbolRemappingInternal("ModMat_tile.water", "ModMat_minecraft:flowing_water_0");
		addSymbolRemappingInternal("modmat_flowing_water_0", "modmat_water_0");

		addSymbolRemappingInternal("ModMaroon", "ModColorMaroon");
		addSymbolRemappingInternal("ModRed", "ModColorRed");
		addSymbolRemappingInternal("ModOlive", "ModColorOlive");
		addSymbolRemappingInternal("ModYellow", "ModColorYellow");
		addSymbolRemappingInternal("ModDark Green", "ModColorDarkGreen");
		addSymbolRemappingInternal("ModGreen", "ModColorGreen");
		addSymbolRemappingInternal("ModTeal", "ModColorTeal");
		addSymbolRemappingInternal("ModCyan", "ModColorCyan");
		addSymbolRemappingInternal("ModNavy", "ModColorNavy");
		addSymbolRemappingInternal("ModBlue", "ModColorBlue");
		addSymbolRemappingInternal("ModPurple", "ModColorPurple");
		addSymbolRemappingInternal("ModMagenta", "ModColorMagenta");
		addSymbolRemappingInternal("ModBlack", "ModColorBlack");
		addSymbolRemappingInternal("ModGrey", "ModColorGrey");
		addSymbolRemappingInternal("ModSilver", "ModColorSilver");
		addSymbolRemappingInternal("ModWhite", "ModColorWhite");

		addSymbolRemappingInternal("LavaLakes", "ModMat_tile.lava", "LakesDeep");
		addSymbolRemappingInternal("Lakes", "ModMat_tile.water", "LakesSurface");

		addSymbolRemappingInternal("CryFormCry", "ModMat_tile.myst.crystal", "CryForm");
		addSymbolRemappingInternal("CryFormGlow", "ModMat_tile.myst.lightgem", "CryForm");
		addSymbolRemappingInternal("CryFormQuartz", "ModMat_tile.myst.netherquartz", "CryForm");

		addSymbolRemappingInternal("Standard Terrain", "TerrainNormal");
		addSymbolRemappingInternal("Star Fissure", "StarFissure");

		addSymbolRemappingInternal("Rain", "WeatherRain");
		addSymbolRemappingInternal("Snow", "WeatherSnow");

		addSymbolRemappingInternal("Huge Trees", "HugeTrees");

		addSymbolRemappingInternal("NormalStars", "StarsNormal");

		addSymbolRemappingInternal("Single Biome", "BioConSingle");

		addSymbolRemappingInternal("Checkerboard Biomes", "BioConTiled");

		addSymbolRemappingInternal("BiomeControllerNative", "BioConNative");

		addSymbolRemappingInternal("Lava Lakes", "LavaLakes");

		addSymbolRemappingInternal("WeatherSun", "WeatherOff");

		addSymbolRemappingInternal("Standard Lighting", "LightingNormal");

		addSymbolRemappingInternal("Storm", "WeatherStorm");

		addSymbolRemappingInternal("Fog", "ColorFog");

		addSymbolRemappingInternal("ModFluid_tile.lava", "ModMat_tile.lava");
		addSymbolRemappingInternal("ModFluid_tile.water", "ModMat_tile.water");
		addSymbolRemappingInternal("ModFluidtile.water", "ModMat_tile.water");
		addSymbolRemappingInternal("ModFluidtile.lava", "ModMat_tile.lava");

		addSymbolRemappingInternal("ModLavaSea", "ModMat_tile.lava");
		addSymbolRemappingInternal("ModNetherTerrain", "ModMat_tile.hellrock");

		addSymbolRemappingInternal("ModMattile.hellrock", "ModMat_tile.hellrock");
		addSymbolRemappingInternal("ModMattile.whiteStone", "ModMat_tile.whiteStone");
		addSymbolRemappingInternal("ModMattile.oreDiamond", "ModMat_tile.oreDiamond");

		addSymbolRemappingInternal("TendrilsIce", "ModMat_tile.ice", "Tendrils");
		addSymbolRemappingInternal("WoodCaves", "Tendrils");

		addSymbolRemappingInternal("SkyDropDark", "StarsDark");

		// Fast Time
		addSymbolRemappingInternal("FTime", "ModHalf", "SunNormal", "ModHalf", "MoonNormal");

		// Slow Time
		addSymbolRemappingInternal("STime", "ModDouble", "SunNormal", "ModDouble", "MoonNormal");

		// Normal Time
		addSymbolRemappingInternal("NTime", "ModFull", "SunNormal", "ModFull", "MoonNormal");

		// Eternal Dusk
		addSymbolRemappingInternal("Dusk", "ModZero", "ModSetting", "SunNormal", "ModZero", "MoonNormal");

		// Eternal Night
		addSymbolRemappingInternal("Night", "SunDark", "ModZero", "MoonNormal");

		// Eternal Day
		addSymbolRemappingInternal("Day", "MoonDark", "ModZero", "ModNoon", "SunNormal");

		addSymbolRemappingInternal("Heavy Resources", "DenseOres");

		addSymbolRemappingInternal("SunsetNormal", "SunsetRed");

		// "CloudNormal" -> "CloudWhite"
		addSymbolRemappingInternal("CloudNormal", "CloudWhite");

		addSymbolRemappingInternal("Normal Sunset Colors", "SunsetRed");

		addSymbolRemappingInternal("NativeBiomeController", "BioConLarge");

		addSymbolRemappingInternal("Flat Sea", "TerrainFlat");

		addSymbolRemappingInternal("Sky Islands", "Skylands");

		addSymbolRemappingInternal("Tree Age", "Huge Trees", "TerrainFlat", "Swampland", "BioConSingle");

		addSymbolRemappingInternal("DefaultBiome", "BioConSingle");

		addSymbolRemappingInternal("DefaultLighting", "Standard Lighting");

		addSymbolRemappingInternal("DefaultSunrise", "Normal Sunset Colors");

		addSymbolRemappingInternal("DefaultTerrain", "Standard Terrain");

		addSymbolRemappingInternal("Flat", "TerrainFlat");
		addSymbolRemappingInternal("Void", "TerrainVoid");
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
		List<String> mapping;

		// FogChromatic
		mapping = new ArrayList<>();
		addChromaticGradients(mapping);
		mapping.add("ColorFog");
		addSymbolRemappingInternal("FogChromatic", mapping);

		// FogRed
		mapping = new ArrayList<>();
		mapping.add("ModRed");
		mapping.add("ColorFog");
		addSymbolRemappingInternal("FogRed", mapping);

		// FogGreen
		mapping = new ArrayList<>();
		mapping.add("ModGreen");
		mapping.add("ColorFog");
		addSymbolRemappingInternal("FogGreen", mapping);

		// FogBlue
		mapping = new ArrayList<>();
		mapping.add("ModBlue");
		mapping.add("ColorFog");
		addSymbolRemappingInternal("FogBlue", mapping);

		// FogBlack
		mapping = new ArrayList<>();
		mapping.add("ModBlack");
		mapping.add("ColorFog");
		addSymbolRemappingInternal("FogBlack", mapping);

		// FogWhite
		mapping = new ArrayList<>();
		mapping.add("ModWhite");
		mapping.add("ColorFog");
		addSymbolRemappingInternal("FogWhite", mapping);

		// FogNormal
		mapping = new ArrayList<>();
		mapping.add("ModWhite");
		mapping.add("ColorFog");
		addSymbolRemappingInternal("FogNormal", mapping);
	}

	private static void cloudColorRemappings() {
		List<String> mapping;

		// CloudChromatic
		mapping = new ArrayList<>();
		addChromaticGradients(mapping);
		mapping.add("ColorCloud");
		addSymbolRemappingInternal("CloudChromatic", mapping);

		// CloudRed
		mapping = new ArrayList<>();
		mapping.add("ModRed");
		mapping.add("ColorCloud");
		addSymbolRemappingInternal("CloudRed", mapping);

		// CloudGreen
		mapping = new ArrayList<>();
		mapping.add("ModGreen");
		mapping.add("ColorCloud");
		addSymbolRemappingInternal("CloudGreen", mapping);

		// CloudBlue
		mapping = new ArrayList<>();
		mapping.add("ModBlue");
		mapping.add("ColorCloud");
		addSymbolRemappingInternal("CloudBlue", mapping);

		// CloudBlack
		mapping = new ArrayList<>();
		mapping.add("ModBlack");
		mapping.add("ColorCloud");
		addSymbolRemappingInternal("CloudBlack", mapping);

		// CloudWhite
		mapping = new ArrayList<>();
		mapping.add("ModWhite");
		mapping.add("ColorCloud");
		addSymbolRemappingInternal("CloudWhite", "ModWhite", "ColorCloud");
	}

	private static void skyColorRemappings() {
		List<String> mapping;

		mapping = new ArrayList<>();
		mapping.add("ModGradient");
		mapping.add("ColorSky");
		addSymbolRemappingInternal("ModGradient_HERE", mapping);

		// SkyChromatic
		mapping = new ArrayList<>();
		addChromaticGradients(mapping);
		mapping.add("ColorSky");
		addSymbolRemappingInternal("SkyChromatic", mapping);

		// SkyRed
		mapping = new ArrayList<>();
		mapping.add("ModRed");
		mapping.add("ColorSky");
		addSymbolRemappingInternal("SkyRed", mapping);

		// SkyGreen
		mapping = new ArrayList<>();
		mapping.add("ModGreen");
		mapping.add("ColorSky");
		addSymbolRemappingInternal("SkyGreen", mapping);

		// SkyBlue
		mapping = new ArrayList<>();
		mapping.add("ModBlue");
		mapping.add("ColorSky");
		addSymbolRemappingInternal("SkyBlue", mapping);

		// SkyBlack
		mapping = new ArrayList<>();
		mapping.add("ModBlack");
		mapping.add("ColorSky");
		addSymbolRemappingInternal("SkyBlack", mapping);

		// SkyWhite
		mapping = new ArrayList<>();
		mapping.add("ModWhite");
		mapping.add("ColorSky");
		addSymbolRemappingInternal("SkyWhite", mapping);

		// SkyNormal
		mapping = new ArrayList<>();
		mapping.add("ModBlue");
		mapping.add("ColorSky");
		addSymbolRemappingInternal("SkyNormal", mapping);
	}

	private static void horizonColorRemappings() {
		List<String> mapping;

		// SunsetChromatic
		mapping = new ArrayList<>();
		addChromaticGradients(mapping);
		mapping.add("ColorHorizon");
		addSymbolRemappingInternal("SunsetChromatic", mapping);

		// SunsetRed
		mapping = new ArrayList<>();
		mapping.add("ModRed");
		mapping.add("ColorHorizon");
		addSymbolRemappingInternal("SunsetRed", mapping);

		// SunsetGreen
		mapping = new ArrayList<>();
		mapping.add("ModGreen");
		mapping.add("ColorHorizon");
		addSymbolRemappingInternal("SunsetGreen", mapping);

		// SunsetBlue
		mapping = new ArrayList<>();
		mapping.add("ModBlue");
		mapping.add("ColorHorizon");
		addSymbolRemappingInternal("SunsetBlue", mapping);

		// SunsetBlack
		mapping = new ArrayList<>();
		mapping.add("ModBlack");
		mapping.add("ColorHorizon");
		addSymbolRemappingInternal("SunsetBlack", mapping);

		// SunsetWhite
		mapping = new ArrayList<>();
		mapping.add("ModWhite");
		mapping.add("ColorHorizon");
		addSymbolRemappingInternal("SunsetWhite", mapping);
	}

	public static boolean hasRemapping(ResourceLocation symbol) {
		if (symbol.getResourceDomain().startsWith("modmat_"))
			return true;
		if (symbol.getResourceDomain().equals("minecraft"))
			return true;
		return mappings.get(symbol) != null;
	}

	public static List<ResourceLocation> remap(ResourceLocation symbol) {
		if (symbol == null)
			return null;

		if (symbol.getResourceDomain().startsWith("modmat_"))
			symbol = new ResourceLocation(symbol.getResourceDomain().replace("modmat_", ""), "modmat_" + symbol.getResourcePath());

		if (symbol.getResourceDomain().equals("minecraft"))
			symbol = new ResourceLocation(MystObjects.MystcraftModId, symbol.getResourcePath());

		List<ResourceLocation> symbols = mappings.get(symbol);
		if (symbols == null) {
			symbols = new ArrayList<>();
			symbols.add(symbol);
		}
		return symbols;
	}

	public static List<ItemStack> remap(ItemStack page) {
		List<ItemStack> result = new ArrayList<>();
		if (page == null)
			return result;
		if (!(page.getItem() instanceof ItemPage))
			return result;
		ResourceLocation symbol = Page.getSymbol(page);
		List<ResourceLocation> symbols = remap(symbol);
		if (symbols != null) {
			for (ResourceLocation mapping : symbols) {
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

	@SuppressWarnings("unchecked")
	public static <T> List<T> remap(List<T> collection) {
		for (int i = 0; i < collection.size();) {
			T element = collection.remove(i);
			List<T> mapping = null;
			if (element instanceof ResourceLocation)
				mapping = (List<T>) SymbolRemappings.remap((ResourceLocation) element);
			if (element instanceof ItemStack)
				mapping = (List<T>) SymbolRemappings.remap((ItemStack) element);
			if (mapping == null)
				continue;
			collection.addAll(i, mapping);
			++i;
		}
		return collection;
	}

	//Also automatically lowercases things
	private static void addSymbolRemappingInternal(String mystId, List<String> mapping) {
		List<ResourceLocation> mappingList = new LinkedList<>();
		for (String str : mapping) {
			mappingList.add(new ResourceLocation(MystObjects.MystcraftModId, str));
		}
		addSymbolRemapping(new ResourceLocation(MystObjects.MystcraftModId, mystId), mappingList);
	}

	private static void addSymbolRemappingInternal(String mystId, String... newMappings) {
		ResourceLocation[] mappingArr = new ResourceLocation[newMappings.length];
		for (int i = 0; i < newMappings.length; i++) {
			mappingArr[i] = new ResourceLocation(MystObjects.MystcraftModId, newMappings[i]);
		}
		addSymbolRemapping(new ResourceLocation(MystObjects.MystcraftModId, mystId), mappingArr);
	}

	private static void addSymbolRemapping(ResourceLocation id, List<ResourceLocation> mapping) {
		mappings.put(id, mapping);
	}

	public static void addSymbolRemapping(ResourceLocation id, ResourceLocation... newids) {
		List<ResourceLocation> mapping = new ArrayList<>();
		for (ResourceLocation newid : newids) {
			mapping.add(newid);
		}
		mappings.put(id, mapping);
	}
}
