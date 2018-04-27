package com.xcompwiz.mystcraft.api.grammar;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import net.minecraft.util.ResourceLocation;

public class GrammarData {

	/** Generates a single Biome */
	public static final ResourceLocation 	BIOME			= asMyst("Biome");
	/** Generates a number of Biomes (minimum 1) */
	public static final ResourceLocation	BIOME_LIST		= asMyst("Biomes");
	/** Generates a Biome Controller */
	public static final ResourceLocation	BIOMECONTROLLER	= asMyst("BiomeController");
	/** Generates a Lighting Controller */
	public static final ResourceLocation	LIGHTING		= asMyst("Lighting");
	/** Generates a Weather Controller */
	public static final ResourceLocation	WEATHER			= asMyst("Weather");
	/** Generates a Base Terrain Generator */
	public static final ResourceLocation	TERRAIN			= asMyst("TerrainGen");
	/** Generates a Visual Effect like Sky or Fog Color */
	public static final ResourceLocation	VISUAL_EFFECT	= asMyst("Visual");
	/** Generates a small world feature */
	public static final ResourceLocation	FEATURE_SMALL	= asMyst("FeatureSmall");
	/** Generates a small world feature */
	public static final ResourceLocation	FEATURE_MEDIUM	= asMyst("FeatureMedium");
	/** Generates a small world feature */
	public static final ResourceLocation	FEATURE_LARGE	= asMyst("FeatureLarge");
	/** @deprecated Use one of the FEATURE tokens. This now maps to FEATURE_LARGE. */
	@Deprecated
	public static final ResourceLocation	TERRAINALT		= FEATURE_LARGE;
	/** @deprecated Use one of the FEATURE tokens. This now maps to FEATURE_MEDIUM. */
	@Deprecated
	public static final ResourceLocation	POPULATOR		= FEATURE_MEDIUM;
	/** Generates a world Effect, like Accelerated */
	public static final ResourceLocation	EFFECT			= asMyst("Effect");
	/** Generates a Sun */
	public static final ResourceLocation	SUN				= asMyst("Sun");
	/** Generates a Moon */
	public static final ResourceLocation	MOON			= asMyst("Moon");
	/** Generates a Starfield */
	public static final ResourceLocation	STARFIELD		= asMyst("Starfield");
	/** Generates a DOODAD */
	public static final ResourceLocation	DOODAD			= asMyst("Doodad");

	/** Generates a Block Modifier which is a valid Terrain Block */
	public static final ResourceLocation	BLOCK_TERRAIN	= BlockCategory.TERRAIN.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Solid Block */
	public static final ResourceLocation	BLOCK_SOLID		= BlockCategory.SOLID.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Structure Block */
	public static final ResourceLocation	BLOCK_STRUCTURE	= BlockCategory.STRUCTURE.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Organic Block */
	public static final ResourceLocation	BLOCK_ORGANIC	= BlockCategory.ORGANIC.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Crystal Block */
	public static final ResourceLocation	BLOCK_CRYSTAL	= BlockCategory.CRYSTAL.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Sea Block */
	public static final ResourceLocation	BLOCK_SEA		= BlockCategory.SEA.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Fluid Block */
	public static final ResourceLocation	BLOCK_FLUID		= BlockCategory.FLUID.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Fluid Block */
	public static final ResourceLocation	BLOCK_GAS		= BlockCategory.GAS.getGrammarBinding();
	/** Generates a Block Modifier */
	public static final ResourceLocation	BLOCK_ANY		= BlockCategory.ANY.getGrammarBinding();

	/** Generates a Sunset modifier about 20% of the time */
	public static final ResourceLocation	SUNSET_UNCOMMON	= asMyst("SunsetUncommon");
	/** Generates a Sunset modifier */
	public static final ResourceLocation	SUNSET			= asMyst("Sunset");

	/** Generates an Angle sequence */
	public static final ResourceLocation	ANGLE_SEQ		= asMyst("Angle");
	/** Generates a Period sequence */
	public static final ResourceLocation	PERIOD_SEQ		= asMyst("Period");
	/** Generates a Phase sequence */
	public static final ResourceLocation	PHASE_SEQ		= asMyst("Phase");
	/** Generates a Color sequence */
	public static final ResourceLocation	COLOR_SEQ		= asMyst("Color");
	/** Generates a Gradient sequence */
	public static final ResourceLocation	GRADIENT_SEQ	= asMyst("Gradient");
	/** Generates a singular Angle value (any symbol expanding this must accept and produce an angle modifier) */
	public static final ResourceLocation	ANGLE_BASIC		= asMyst("AngleBasic");
	/** Generates a singular Period value (any symbol expanding this must accept and produce a period modifier) */
	public static final ResourceLocation	PERIOD_BASIC	= asMyst("PeriodBasic");
	/** Generates a singular Phase value (any symbol expanding this must accept and produce a phase modifier) */
	public static final ResourceLocation	PHASE_BASIC		= asMyst("PhaseBasic");
	/** Generates a singular Color (any symbol expanding this must accept and produce a color modifier) */
	public static final ResourceLocation	COLOR_BASIC		= asMyst("ColorBasic");
	/** Generates a singular Gradient (any symbol expanding this must accept and produce a gradient modifier) */
	public static final ResourceLocation	GRADIENT_BASIC	= asMyst("GradientBasic");

	private static ResourceLocation asMyst(String path) {
		return new ResourceLocation(MystObjects.MystcraftModId, path);
	}

}
