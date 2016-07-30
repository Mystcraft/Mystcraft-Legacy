package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.util.CollectionUtils;

public class GrammarRules {
	public static final String	ROOT				= "Age";

	private static final String	BIOME_GEN			= "BiomesAdv";
	private static final String	BIOME_EXT			= "BiomesExt";

	private static final String	VISUAL_EFFECT_GEN	= "VisualsAdv";
	private static final String	VISUAL_EFFECT_EXT	= "VisualsExt";

	private static final String	FEATURE_LARGE_GEN	= "FeatureLargeAdv";
	public  static final String	FEATURE_LARGE_EXT	= "FeatureLargeExt";

	private static final String	FEATURE_MEDIUM_GEN	= "FeatureMediumAdv";
	public  static final String	FEATURE_MEDIUM_EXT	= "FeatureMediumExt";

	private static final String	FEATURE_SMALL_GEN	= "FeatureSmallAdv";
	public  static final String	FEATURE_SMALL_EXT	= "FeatureSmallExt";

	private static final String	EFFECT_GEN			= "EffectsAdv";
	private static final String	EFFECT_EXT			= "EffectsExt";

	private static final String	SUN_GEN				= "SunsAdv";
	private static final String	SUN_EXT				= "SunsExt";

	private static final String	MOON_GEN			= "MoonsAdv";
	private static final String	MOON_EXT			= "MoonsExt";

	private static final String	STARFIELD_GEN		= "StarfieldsAdv";
	private static final String	STARFIELD_EXT		= "StarfieldsExt";

	private static final String	DOODAD_GEN			= "DoodadsAdv";
	private static final String	DOODAD_EXT			= "DoodadsExt";

	private static final String	ANGLE_GEN			= "AngleAdv";
	private static final String	PERIOD_GEN			= "PeriodAdv";
	private static final String	PHASE_GEN			= "PhaseAdv";
	private static final String	COLOR_GEN			= "ColorAdv";
	private static final String	GRADIENT_GEN		= "GradientAdv";

	public static final String	ANGLE_EXT			= "Angle_Ext";
	public static final String	PERIOD_EXT			= "Period_Ext";
	public static final String	PHASE_EXT			= "Phase_Ext";
	public static final String	COLOR_EXT			= "Color_Ext";
	public static final String	GRADIENT_EXT		= "Gradient_Ext";
	public static final String	SUNSET_EXT			= "Sunset_Ext";

	public static final String	BLOCK_NONSOLID		= "BLOCK_NONSOLID";

	public static void initialize() {
		registerRule(buildRule(0, ROOT, GrammarData.TERRAIN, GrammarData.BIOMECONTROLLER, GrammarData.WEATHER, GrammarData.LIGHTING, "Spawning0", "Suns0", "Moons0", "Starfields0", "Doodads0", "Visuals0", "FeatureSmalls0", "FeatureMediums0", "FeatureLarges0", "Effects0"));

		registerRule(buildRule(10, "Spawning0"));

		registerRule(buildRule(1, GrammarData.BIOME_LIST, BIOME_GEN));
		registerRule(buildRule(2, BIOME_GEN, BIOME_GEN, GrammarData.BIOME));
		registerRule(buildRule(3, BIOME_GEN, GrammarData.BIOME));
		registerRule(buildRule(null, GrammarData.BIOME_LIST, BIOME_EXT, GrammarData.BIOME));
		registerRule(buildRule(null, BIOME_EXT, BIOME_EXT, GrammarData.BIOME_LIST));
		registerRule(buildRule(1, BIOME_EXT));

		registerRule(buildRule(1, "Suns0", SUN_GEN));
		registerRule(buildRule(4, SUN_GEN, SUN_GEN, GrammarData.SUN));
		registerRule(buildRule(2, SUN_GEN, GrammarData.SUN));
		registerRule(buildRule(null, "Suns0", SUN_EXT, GrammarData.SUN));
		registerRule(buildRule(null, SUN_EXT, SUN_EXT, GrammarData.SUN));
		registerRule(buildRule(1, SUN_EXT));

		registerRule(buildRule(1, "Moons0", MOON_GEN));
		registerRule(buildRule(2, MOON_GEN, MOON_GEN, GrammarData.MOON));
		registerRule(buildRule(2, MOON_GEN, GrammarData.MOON));
		registerRule(buildRule(null, "Moons0", MOON_EXT, GrammarData.MOON));
		registerRule(buildRule(null, MOON_EXT, MOON_EXT, GrammarData.MOON));
		registerRule(buildRule(1, MOON_EXT));

		registerRule(buildRule(1, "Starfields0", STARFIELD_GEN));
		registerRule(buildRule(3, STARFIELD_GEN, STARFIELD_GEN, GrammarData.STARFIELD));
		registerRule(buildRule(2, STARFIELD_GEN, GrammarData.STARFIELD));
		registerRule(buildRule(null, "Starfields0", STARFIELD_EXT, GrammarData.STARFIELD));
		registerRule(buildRule(null, STARFIELD_EXT, STARFIELD_EXT, GrammarData.STARFIELD));
		registerRule(buildRule(1, STARFIELD_EXT));
		registerRule(buildRule(1, GrammarData.STARFIELD));

		registerRule(buildRule(1, "Doodads0", DOODAD_GEN));
		registerRule(buildRule(5, DOODAD_GEN, DOODAD_GEN, GrammarData.DOODAD));
		registerRule(buildRule(2, DOODAD_GEN, GrammarData.DOODAD));
		registerRule(buildRule(null, "Doodads0", DOODAD_EXT, GrammarData.DOODAD));
		registerRule(buildRule(null, DOODAD_EXT, DOODAD_EXT, GrammarData.DOODAD));
		registerRule(buildRule(1, DOODAD_EXT));
		registerRule(buildRule(0, GrammarData.DOODAD));

		registerRule(buildRule(1, "Visuals0", VISUAL_EFFECT_GEN));
		registerRule(buildRule(3, VISUAL_EFFECT_GEN, VISUAL_EFFECT_GEN, GrammarData.VISUAL_EFFECT));
		registerRule(buildRule(2, VISUAL_EFFECT_GEN, GrammarData.VISUAL_EFFECT));
		registerRule(buildRule(null, "Visuals0", VISUAL_EFFECT_EXT, GrammarData.VISUAL_EFFECT));
		registerRule(buildRule(null, VISUAL_EFFECT_EXT, VISUAL_EFFECT_EXT, GrammarData.VISUAL_EFFECT));
		registerRule(buildRule(1, VISUAL_EFFECT_EXT));
		registerRule(buildRule(1, GrammarData.VISUAL_EFFECT));

		registerRule(buildRule(1, "FeatureLarges0", FEATURE_LARGE_GEN));
		registerRule(buildRule(2, FEATURE_LARGE_GEN, FEATURE_LARGE_GEN, GrammarData.FEATURE_LARGE));
		registerRule(buildRule(2, FEATURE_LARGE_GEN, GrammarData.FEATURE_LARGE));
		registerRule(buildRule(null, "FeatureLarges0", FEATURE_LARGE_EXT, GrammarData.FEATURE_LARGE));
		registerRule(buildRule(null, FEATURE_LARGE_EXT, FEATURE_LARGE_EXT, GrammarData.FEATURE_LARGE));
		registerRule(buildRule(1, FEATURE_LARGE_EXT));
		registerRule(buildRule(4, GrammarData.FEATURE_LARGE));

		registerRule(buildRule(1, "FeatureMediums0", FEATURE_MEDIUM_GEN));
		registerRule(buildRule(2, FEATURE_MEDIUM_GEN, FEATURE_MEDIUM_GEN, GrammarData.FEATURE_MEDIUM));
		registerRule(buildRule(3, FEATURE_MEDIUM_GEN, GrammarData.FEATURE_MEDIUM));
		registerRule(buildRule(null, "FeatureMediums0", FEATURE_MEDIUM_EXT, GrammarData.FEATURE_MEDIUM));
		registerRule(buildRule(null, FEATURE_MEDIUM_EXT, FEATURE_MEDIUM_EXT, GrammarData.FEATURE_MEDIUM));
		registerRule(buildRule(1, FEATURE_MEDIUM_EXT));
		registerRule(buildRule(4, GrammarData.FEATURE_MEDIUM));

		registerRule(buildRule(1, "FeatureSmalls0", FEATURE_SMALL_GEN));
		registerRule(buildRule(2, FEATURE_SMALL_GEN, FEATURE_SMALL_GEN, GrammarData.FEATURE_SMALL));
		registerRule(buildRule(4, FEATURE_SMALL_GEN, GrammarData.FEATURE_SMALL));
		registerRule(buildRule(null, "FeatureSmalls0", FEATURE_SMALL_EXT, GrammarData.FEATURE_SMALL));
		registerRule(buildRule(null, FEATURE_SMALL_EXT, FEATURE_SMALL_EXT, GrammarData.FEATURE_SMALL));
		registerRule(buildRule(1, FEATURE_SMALL_EXT));
		registerRule(buildRule(4, GrammarData.FEATURE_SMALL));

		registerRule(buildRule(1, "Effects0", EFFECT_GEN));
		registerRule(buildRule(3, EFFECT_GEN, EFFECT_GEN, GrammarData.EFFECT));
		registerRule(buildRule(2, EFFECT_GEN, GrammarData.EFFECT));
		registerRule(buildRule(null, "Effects0", EFFECT_EXT, GrammarData.EFFECT));
		registerRule(buildRule(null, EFFECT_EXT, EFFECT_EXT, GrammarData.EFFECT));
		registerRule(buildRule(1, EFFECT_EXT));
		registerRule(buildRule(1, GrammarData.EFFECT));

		registerRule(buildRule(2, GrammarData.SUNSET_UNCOMMON));
		registerRule(buildRule(3, GrammarData.SUNSET_UNCOMMON, GrammarData.SUNSET));
		registerRule(buildRule(1, GrammarData.SUNSET));
		registerRule(buildRule(null, SUNSET_EXT, GrammarData.SUNSET));
		registerRule(buildRule(1, SUNSET_EXT));

		registerRule(buildRule(1, GrammarData.ANGLE_SEQ, ANGLE_GEN));
		registerRule(buildRule(2, ANGLE_GEN, ANGLE_GEN, GrammarData.ANGLE_BASIC));
		registerRule(buildRule(3, ANGLE_GEN, GrammarData.ANGLE_BASIC));
		registerRule(buildRule(null, GrammarData.ANGLE_SEQ, ANGLE_EXT, GrammarData.ANGLE_BASIC));
		registerRule(buildRule(null, ANGLE_EXT, GrammarData.ANGLE_SEQ));
		registerRule(buildRule(1, ANGLE_EXT));

		registerRule(buildRule(1, GrammarData.PERIOD_SEQ, PERIOD_GEN));
		registerRule(buildRule(2, PERIOD_GEN, PERIOD_GEN, GrammarData.PERIOD_BASIC));
		registerRule(buildRule(3, PERIOD_GEN, GrammarData.PERIOD_BASIC));
		registerRule(buildRule(null, GrammarData.PERIOD_SEQ, PERIOD_EXT, GrammarData.PERIOD_BASIC));
		registerRule(buildRule(null, PERIOD_EXT, GrammarData.PERIOD_SEQ));
		registerRule(buildRule(1, PERIOD_EXT));

		registerRule(buildRule(1, GrammarData.PHASE_SEQ, PHASE_GEN));
		registerRule(buildRule(2, PHASE_GEN, PHASE_GEN, GrammarData.PHASE_BASIC));
		registerRule(buildRule(3, PHASE_GEN, GrammarData.PHASE_BASIC));
		registerRule(buildRule(null, GrammarData.PHASE_SEQ, PHASE_EXT, GrammarData.PHASE_BASIC));
		registerRule(buildRule(null, PHASE_EXT, GrammarData.PHASE_SEQ));
		registerRule(buildRule(1, PHASE_EXT));

		registerRule(buildRule(1, GrammarData.COLOR_SEQ, COLOR_GEN));
		registerRule(buildRule(2, COLOR_GEN, COLOR_GEN, GrammarData.COLOR_BASIC));
		registerRule(buildRule(3, COLOR_GEN, GrammarData.COLOR_BASIC));
		registerRule(buildRule(null, GrammarData.COLOR_SEQ, COLOR_EXT, GrammarData.COLOR_BASIC));
		registerRule(buildRule(null, COLOR_EXT, GrammarData.COLOR_SEQ));
		registerRule(buildRule(1, COLOR_EXT));

		registerRule(buildRule(1, GrammarData.GRADIENT_SEQ, GRADIENT_GEN));
		registerRule(buildRule(2, GRADIENT_GEN, GRADIENT_GEN, GrammarData.GRADIENT_BASIC));
		registerRule(buildRule(2, GRADIENT_GEN, GrammarData.GRADIENT_BASIC));
		registerRule(buildRule(null, GrammarData.GRADIENT_SEQ, GRADIENT_EXT, GrammarData.GRADIENT_BASIC));
		registerRule(buildRule(null, GRADIENT_EXT, GrammarData.GRADIENT_SEQ));
		registerRule(buildRule(1, GRADIENT_EXT));

		registerRule(buildRule(0, GrammarData.BLOCK_TERRAIN));
		registerRule(buildRule(0, GrammarData.BLOCK_SOLID));
		registerRule(buildRule(0, GrammarData.BLOCK_STRUCTURE));
		registerRule(buildRule(0, GrammarData.BLOCK_ORGANIC));
		registerRule(buildRule(0, GrammarData.BLOCK_CRYSTAL));
		registerRule(buildRule(0, GrammarData.BLOCK_SEA));
		registerRule(buildRule(0, GrammarData.BLOCK_FLUID));
		registerRule(buildRule(0, GrammarData.BLOCK_GAS));
		registerRule(buildRule(0, GrammarData.BLOCK_ANY));

		registerRule(buildRule(1, BLOCK_NONSOLID, GrammarData.BLOCK_FLUID));
		registerRule(buildRule(2, BLOCK_NONSOLID, GrammarData.BLOCK_GAS));
	}

	private static void registerRule(Rule rule) {
		GrammarGenerator.registerRule(rule);
	}

	//XXX: (Helper) Absorb this helper into something else (API?)
	private static Rule buildRule(Integer rank, String parent, String... args) {
		ArrayList<String> list = CollectionUtils.buildList(args);
		return new Rule(parent, list, rank);
	}
}
