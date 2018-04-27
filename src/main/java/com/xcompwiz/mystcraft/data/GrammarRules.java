package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.util.CollectionUtils;
import net.minecraft.util.ResourceLocation;

public class GrammarRules {
	public static final ResourceLocation	ROOT				= asMyst("Age");

	private static final ResourceLocation	BIOME_GEN			= asMyst("BiomesAdv");
	private static final ResourceLocation	BIOME_EXT			= asMyst("BiomesExt");

	private static final ResourceLocation	VISUAL_EFFECT_GEN	= asMyst("VisualsAdv");
	private static final ResourceLocation	VISUAL_EFFECT_EXT	= asMyst("VisualsExt");

	private static final ResourceLocation	FEATURE_LARGE_GEN	= asMyst("FeatureLargeAdv");
	public  static final ResourceLocation	FEATURE_LARGE_EXT	= asMyst("FeatureLargeExt");

	private static final ResourceLocation	FEATURE_MEDIUM_GEN	= asMyst("FeatureMediumAdv");
	public  static final ResourceLocation	FEATURE_MEDIUM_EXT	= asMyst("FeatureMediumExt");

	private static final ResourceLocation	FEATURE_SMALL_GEN	= asMyst("FeatureSmallAdv");
	public  static final ResourceLocation	FEATURE_SMALL_EXT	= asMyst("FeatureSmallExt");

	private static final ResourceLocation	EFFECT_GEN			= asMyst("EffectsAdv");
	private static final ResourceLocation	EFFECT_EXT			= asMyst("EffectsExt");

	private static final ResourceLocation	SUN_GEN				= asMyst("SunsAdv");
	private static final ResourceLocation	SUN_EXT				= asMyst("SunsExt");

	private static final ResourceLocation	MOON_GEN			= asMyst("MoonsAdv");
	private static final ResourceLocation	MOON_EXT			= asMyst("MoonsExt");

	private static final ResourceLocation	STARFIELD_GEN		= asMyst("StarfieldsAdv");
	private static final ResourceLocation	STARFIELD_EXT		= asMyst("StarfieldsExt");

	private static final ResourceLocation	DOODAD_GEN			= asMyst("DoodadsAdv");
	private static final ResourceLocation	DOODAD_EXT			= asMyst("DoodadsExt");

	private static final ResourceLocation	ANGLE_GEN			= asMyst("AngleAdv");
	private static final ResourceLocation	PERIOD_GEN			= asMyst("PeriodAdv");
	private static final ResourceLocation	PHASE_GEN			= asMyst("PhaseAdv");
	private static final ResourceLocation	COLOR_GEN			= asMyst("ColorAdv");
	private static final ResourceLocation	GRADIENT_GEN		= asMyst("GradientAdv");

	public static final ResourceLocation	ANGLE_EXT			= asMyst("Angle_Ext");
	public static final ResourceLocation	PERIOD_EXT			= asMyst("Period_Ext");
	public static final ResourceLocation	PHASE_EXT			= asMyst("Phase_Ext");
	public static final ResourceLocation	COLOR_EXT			= asMyst("Color_Ext");
	public static final ResourceLocation	GRADIENT_EXT		= asMyst("Gradient_Ext");
	public static final ResourceLocation	SUNSET_EXT			= asMyst("Sunset_Ext");

	public static final ResourceLocation	BLOCK_NONSOLID		= asMyst("BLOCK_NONSOLID");

	public static void initialize() {
		registerRule(buildRule(0, ROOT, GrammarData.TERRAIN, GrammarData.BIOMECONTROLLER, GrammarData.WEATHER, GrammarData.LIGHTING, asMyst("Spawning0"), asMyst("Suns0"), asMyst("Moons0"), asMyst("Starfields0"), asMyst("Doodads0"), asMyst("Visuals0"), asMyst("FeatureSmalls0"), asMyst("FeatureMediums0"), asMyst("FeatureLarges0"), asMyst("Effects0")));

		registerRule(buildRule(10, asMyst("Spawning0")));

		registerRule(buildRule(1, GrammarData.BIOME_LIST, BIOME_GEN));
		registerRule(buildRule(2, BIOME_GEN, BIOME_GEN, GrammarData.BIOME));
		registerRule(buildRule(3, BIOME_GEN, GrammarData.BIOME));
		registerRule(buildRule(null, GrammarData.BIOME_LIST, BIOME_EXT, GrammarData.BIOME));
		registerRule(buildRule(null, BIOME_EXT, BIOME_EXT, GrammarData.BIOME_LIST));
		registerRule(buildRule(1, BIOME_EXT));

		registerRule(buildRule(1, asMyst("Suns0"), SUN_GEN));
		registerRule(buildRule(4, SUN_GEN, SUN_GEN, GrammarData.SUN));
		registerRule(buildRule(2, SUN_GEN, GrammarData.SUN));
		registerRule(buildRule(null, asMyst("Suns0"), SUN_EXT, GrammarData.SUN));
		registerRule(buildRule(null, SUN_EXT, SUN_EXT, GrammarData.SUN));
		registerRule(buildRule(1, SUN_EXT));

		registerRule(buildRule(1, asMyst("Moons0"), MOON_GEN));
		registerRule(buildRule(2, MOON_GEN, MOON_GEN, GrammarData.MOON));
		registerRule(buildRule(2, MOON_GEN, GrammarData.MOON));
		registerRule(buildRule(null, asMyst("Moons0"), MOON_EXT, GrammarData.MOON));
		registerRule(buildRule(null, MOON_EXT, MOON_EXT, GrammarData.MOON));
		registerRule(buildRule(1, MOON_EXT));

		registerRule(buildRule(1, asMyst("Starfields0"), STARFIELD_GEN));
		registerRule(buildRule(3, STARFIELD_GEN, STARFIELD_GEN, GrammarData.STARFIELD));
		registerRule(buildRule(2, STARFIELD_GEN, GrammarData.STARFIELD));
		registerRule(buildRule(null, asMyst("Starfields0"), STARFIELD_EXT, GrammarData.STARFIELD));
		registerRule(buildRule(null, STARFIELD_EXT, STARFIELD_EXT, GrammarData.STARFIELD));
		registerRule(buildRule(1, STARFIELD_EXT));
		registerRule(buildRule(1, GrammarData.STARFIELD));

		registerRule(buildRule(1, asMyst("Doodads0"), DOODAD_GEN));
		registerRule(buildRule(5, DOODAD_GEN, DOODAD_GEN, GrammarData.DOODAD));
		registerRule(buildRule(2, DOODAD_GEN, GrammarData.DOODAD));
		registerRule(buildRule(null, asMyst("Doodads0"), DOODAD_EXT, GrammarData.DOODAD));
		registerRule(buildRule(null, DOODAD_EXT, DOODAD_EXT, GrammarData.DOODAD));
		registerRule(buildRule(1, DOODAD_EXT));
		registerRule(buildRule(0, GrammarData.DOODAD));

		registerRule(buildRule(1, asMyst("Visuals0"), VISUAL_EFFECT_GEN));
		registerRule(buildRule(3, VISUAL_EFFECT_GEN, VISUAL_EFFECT_GEN, GrammarData.VISUAL_EFFECT));
		registerRule(buildRule(2, VISUAL_EFFECT_GEN, GrammarData.VISUAL_EFFECT));
		registerRule(buildRule(null, asMyst("Visuals0"), VISUAL_EFFECT_EXT, GrammarData.VISUAL_EFFECT));
		registerRule(buildRule(null, VISUAL_EFFECT_EXT, VISUAL_EFFECT_EXT, GrammarData.VISUAL_EFFECT));
		registerRule(buildRule(1, VISUAL_EFFECT_EXT));
		registerRule(buildRule(1, GrammarData.VISUAL_EFFECT));

		registerRule(buildRule(1, asMyst("FeatureLarges0"), FEATURE_LARGE_GEN));
		registerRule(buildRule(2, FEATURE_LARGE_GEN, FEATURE_LARGE_GEN, GrammarData.FEATURE_LARGE));
		registerRule(buildRule(2, FEATURE_LARGE_GEN, GrammarData.FEATURE_LARGE));
		registerRule(buildRule(null, asMyst("FeatureLarges0"), FEATURE_LARGE_EXT, GrammarData.FEATURE_LARGE));
		registerRule(buildRule(null, FEATURE_LARGE_EXT, FEATURE_LARGE_EXT, GrammarData.FEATURE_LARGE));
		registerRule(buildRule(1, FEATURE_LARGE_EXT));
		registerRule(buildRule(4, GrammarData.FEATURE_LARGE));

		registerRule(buildRule(1, asMyst("FeatureMediums0"), FEATURE_MEDIUM_GEN));
		registerRule(buildRule(2, FEATURE_MEDIUM_GEN, FEATURE_MEDIUM_GEN, GrammarData.FEATURE_MEDIUM));
		registerRule(buildRule(3, FEATURE_MEDIUM_GEN, GrammarData.FEATURE_MEDIUM));
		registerRule(buildRule(null, asMyst("FeatureMediums0"), FEATURE_MEDIUM_EXT, GrammarData.FEATURE_MEDIUM));
		registerRule(buildRule(null, FEATURE_MEDIUM_EXT, FEATURE_MEDIUM_EXT, GrammarData.FEATURE_MEDIUM));
		registerRule(buildRule(1, FEATURE_MEDIUM_EXT));
		registerRule(buildRule(4, GrammarData.FEATURE_MEDIUM));

		registerRule(buildRule(1, asMyst("FeatureSmalls0"), FEATURE_SMALL_GEN));
		registerRule(buildRule(2, FEATURE_SMALL_GEN, FEATURE_SMALL_GEN, GrammarData.FEATURE_SMALL));
		registerRule(buildRule(4, FEATURE_SMALL_GEN, GrammarData.FEATURE_SMALL));
		registerRule(buildRule(null, asMyst("FeatureSmalls0"), FEATURE_SMALL_EXT, GrammarData.FEATURE_SMALL));
		registerRule(buildRule(null, FEATURE_SMALL_EXT, FEATURE_SMALL_EXT, GrammarData.FEATURE_SMALL));
		registerRule(buildRule(1, FEATURE_SMALL_EXT));
		registerRule(buildRule(4, GrammarData.FEATURE_SMALL));

		registerRule(buildRule(1, asMyst("Effects0"), EFFECT_GEN));
		registerRule(buildRule(3, EFFECT_GEN, EFFECT_GEN, GrammarData.EFFECT));
		registerRule(buildRule(2, EFFECT_GEN, GrammarData.EFFECT));
		registerRule(buildRule(null, asMyst("Effects0"), EFFECT_EXT, GrammarData.EFFECT));
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

	private static ResourceLocation asMyst(String path) {
		return new ResourceLocation(MystObjects.MystcraftModId, path);
	}

	//XXX: (Helper) Absorb this helper into something else (API?)
	private static Rule buildRule(Integer rank, ResourceLocation parent, ResourceLocation... args) {
		ArrayList<ResourceLocation> list = CollectionUtils.buildList(args);
		return new Rule(parent, list, rank);
	}
}
