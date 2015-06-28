package com.xcompwiz.mystcraft.api.grammar;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;

public class GrammarData {

	/** Generates a single Biome */
	public static final String	BIOME			= "Biome";
	/** Generates a number of Biomes (minimum 1) */
	public static final String	BIOME_LIST		= "Biomes";
	/** Generates a Biome Controller */
	public static final String	BIOMECONTROLLER	= "BiomeController";
	/** Generates a Lighting Controller */
	public static final String	LIGHTING		= "Lighting";
	/** Generates a Weather Controller */
	public static final String	WEATHER			= "Weather";
	/** Generates a Base Terrain Generator */
	public static final String	TERRAIN			= "TerrainGen";
	/** Generates a Visual Effect like Sky or Fog Color */
	public static final String	VISUAL_EFFECT	= "Visual";
	/** Generates a small world feature */
	public static final String	FEATURE_SMALL	= "FeatureSmall";
	/** Generates a small world feature */
	public static final String	FEATURE_MEDIUM	= "FeatureMedium";
	/** Generates a small world feature */
	public static final String	FEATURE_LARGE	= "FeatureLarge";
	/** @deprecated Use one of the FEATURE tokens. This now maps to FEATURE_LARGE. */
	@Deprecated
	public static final String	TERRAINALT		= FEATURE_LARGE;
	/** @deprecated Use one of the FEATURE tokens. This now maps to FEATURE_MEDIUM. */
	@Deprecated
	public static final String	POPULATOR		= FEATURE_MEDIUM;
	/** Generates a world Effect, like Accelerated */
	public static final String	EFFECT			= "Effect";
	/** Generates a Sun */
	public static final String	SUN				= "Sun";
	/** Generates a Moon */
	public static final String	MOON			= "Moon";
	/** Generates a Starfield */
	public static final String	STARFIELD		= "Starfield";
	/** Generates a DOODAD */
	public static final String	DOODAD			= "Doodad";

	/** Generates a Block Modifier which is a valid Terrain Block */
	public static final String	BLOCK_TERRAIN	= BlockCategory.TERRAIN.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Solid Block */
	public static final String	BLOCK_SOLID		= BlockCategory.SOLID.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Structure Block */
	public static final String	BLOCK_STRUCTURE	= BlockCategory.STRUCTURE.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Organic Block */
	public static final String	BLOCK_ORGANIC	= BlockCategory.ORGANIC.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Crystal Block */
	public static final String	BLOCK_CRYSTAL	= BlockCategory.CRYSTAL.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Sea Block */
	public static final String	BLOCK_SEA		= BlockCategory.SEA.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Fluid Block */
	public static final String	BLOCK_FLUID		= BlockCategory.FLUID.getGrammarBinding();
	/** Generates a Block Modifier which is a valid Fluid Block */
	public static final String	BLOCK_GAS		= BlockCategory.GAS.getGrammarBinding();
	/** Generates a Block Modifier */
	public static final String	BLOCK_ANY		= BlockCategory.ANY.getGrammarBinding();

	/** Generates a Sunset modifier about 20% of the time */
	public static final String	SUNSET_UNCOMMON	= "SunsetUncommon";
	/** Generates a Sunset modifier */
	public static final String	SUNSET			= "Sunset";

	/** Generates an Angle sequence */
	public static final String	ANGLE_SEQ		= "Angle";
	/** Generates a Period sequence */
	public static final String	PERIOD_SEQ		= "Period";
	/** Generates a Phase sequence */
	public static final String	PHASE_SEQ		= "Phase";
	/** Generates a Color sequence */
	public static final String	COLOR_SEQ		= "Color";
	/** Generates a Gradient sequence */
	public static final String	GRADIENT_SEQ	= "Gradient";
	/** Generates a singular Angle value (any symbol expanding this must accept and produce an angle modifier) */
	public static final String	ANGLE_BASIC		= "AngleBasic";
	/** Generates a singular Period value (any symbol expanding this must accept and produce a period modifier) */
	public static final String	PERIOD_BASIC	= "PeriodBasic";
	/** Generates a singular Phase value (any symbol expanding this must accept and produce a phase modifier) */
	public static final String	PHASE_BASIC		= "PhaseBasic";
	/** Generates a singular Color (any symbol expanding this must accept and produce a color modifier) */
	public static final String	COLOR_BASIC		= "ColorBasic";
	/** Generates a singular Gradient (any symbol expanding this must accept and produce a gradient modifier) */
	public static final String	GRADIENT_BASIC	= "GradientBasic";
}
