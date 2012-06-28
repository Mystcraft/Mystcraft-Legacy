package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.util.CollectionUtils;

public class GrammarRules {
	public static final String	ROOT				= "Age";

	private static final String	BIOME_GEN			= "BiomesAdv";
	private static final String	BIOME_EXT			= "BiomesExt";

	private static final String	VISUAL_EFFECT_GEN	= "VisualsAdv";
	private static final String	VISUAL_EFFECT_EXT	= "VisualsExt";

	private static final String	TERRAINALT_GEN		= "TerrainAltAdv";
	private static final String	TERRAINALT_EXT		= "TerrainAltExt";

	private static final String	POPULATOR_GEN		= "PopulatorAdv";
	private static final String	POPULATOR_EXT		= "PopulatorExt";

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
		registerRule(buildRule(ROOT, IGrammarAPI.TERRAIN, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.WEATHER, IGrammarAPI.LIGHTING, "Spawning0", "Suns0", "Moons0", "Starfields0", "Doodads0", "Visuals0", "TerrainAlts0", "Populators0", "Effects0"));

		registerRule(buildRule("Spawning0"));

		registerRule(buildRule(1.0F, IGrammarAPI.BIOME_LIST, BIOME_GEN));
		registerRule(buildRule(0.6F, BIOME_GEN, BIOME_GEN, IGrammarAPI.BIOME));
		registerRule(buildRule(0.4F, BIOME_GEN, IGrammarAPI.BIOME));
		registerRule(buildRule(0.0F, IGrammarAPI.BIOME_LIST, BIOME_EXT, IGrammarAPI.BIOME));
		registerRule(buildRule(0.0F, BIOME_EXT, BIOME_EXT, IGrammarAPI.BIOME_LIST));
		registerRule(buildRule(1.0F, BIOME_EXT));

		registerRule(buildRule(1.0F, "Suns0", SUN_GEN));
		registerRule(buildRule(0.2F, SUN_GEN, SUN_GEN, IGrammarAPI.SUN));
		registerRule(buildRule(0.8F, SUN_GEN, IGrammarAPI.SUN));
		registerRule(buildRule(0.0F, "Suns0", SUN_EXT, IGrammarAPI.SUN));
		registerRule(buildRule(0.0F, SUN_EXT, SUN_EXT, IGrammarAPI.SUN));
		registerRule(buildRule(1.0F, SUN_EXT));

		registerRule(buildRule(1.0F, "Moons0", MOON_GEN));
		registerRule(buildRule(0.5F, MOON_GEN, MOON_GEN, IGrammarAPI.MOON));
		registerRule(buildRule(0.5F, MOON_GEN, IGrammarAPI.MOON));
		registerRule(buildRule(0.0F, "Moons0", MOON_EXT, IGrammarAPI.MOON));
		registerRule(buildRule(0.0F, MOON_EXT, MOON_EXT, IGrammarAPI.MOON));
		registerRule(buildRule(1.0F, MOON_EXT));

		registerRule(buildRule(1.0F, "Starfields0", STARFIELD_GEN));
		registerRule(buildRule(0.3F, STARFIELD_GEN, STARFIELD_GEN, IGrammarAPI.STARFIELD));
		registerRule(buildRule(0.7F, STARFIELD_GEN, IGrammarAPI.STARFIELD));
		registerRule(buildRule(0.0F, "Starfields0", STARFIELD_EXT, IGrammarAPI.STARFIELD));
		registerRule(buildRule(0.0F, STARFIELD_EXT, STARFIELD_EXT, IGrammarAPI.STARFIELD));
		registerRule(buildRule(1.0F, STARFIELD_EXT));
		registerRule(buildRule(IGrammarAPI.STARFIELD));

		registerRule(buildRule(1.0F, "Doodads0", DOODAD_GEN));
		registerRule(buildRule(0.1F, DOODAD_GEN, DOODAD_GEN, IGrammarAPI.DOODAD));
		registerRule(buildRule(0.9F, DOODAD_GEN, IGrammarAPI.DOODAD));
		registerRule(buildRule(0.0F, "Doodads0", DOODAD_EXT, IGrammarAPI.DOODAD));
		registerRule(buildRule(0.0F, DOODAD_EXT, DOODAD_EXT, IGrammarAPI.DOODAD));
		registerRule(buildRule(1.0F, DOODAD_EXT));
		registerRule(buildRule(5.0F, IGrammarAPI.DOODAD));

		registerRule(buildRule(1.0F, "Visuals0", VISUAL_EFFECT_GEN));
		registerRule(buildRule(0.7F, VISUAL_EFFECT_GEN, VISUAL_EFFECT_GEN, IGrammarAPI.VISUAL_EFFECT));
		registerRule(buildRule(0.3F, VISUAL_EFFECT_GEN, IGrammarAPI.VISUAL_EFFECT));
		registerRule(buildRule(0.0F, "Visuals0", VISUAL_EFFECT_EXT, IGrammarAPI.VISUAL_EFFECT));
		registerRule(buildRule(0.0F, VISUAL_EFFECT_EXT, VISUAL_EFFECT_EXT, IGrammarAPI.VISUAL_EFFECT));
		registerRule(buildRule(1.0F, VISUAL_EFFECT_EXT));
		registerRule(buildRule(IGrammarAPI.VISUAL_EFFECT));

		registerRule(buildRule(1.0F, "TerrainAlts0", TERRAINALT_GEN));
		registerRule(buildRule(0.5F, TERRAINALT_GEN, TERRAINALT_GEN, IGrammarAPI.TERRAINALT));
		registerRule(buildRule(0.5F, TERRAINALT_GEN, IGrammarAPI.TERRAINALT));
		registerRule(buildRule(0.0F, "TerrainAlts0", TERRAINALT_EXT, IGrammarAPI.TERRAINALT));
		registerRule(buildRule(0.0F, TERRAINALT_EXT, TERRAINALT_EXT, IGrammarAPI.TERRAINALT));
		registerRule(buildRule(1.0F, TERRAINALT_EXT));
		registerRule(buildRule(0.1F, IGrammarAPI.TERRAINALT));

		registerRule(buildRule(1.0F, "Populators0", POPULATOR_GEN));
		registerRule(buildRule(0.8F, POPULATOR_GEN, POPULATOR_GEN, IGrammarAPI.POPULATOR));
		registerRule(buildRule(0.2F, POPULATOR_GEN, IGrammarAPI.POPULATOR, IGrammarAPI.POPULATOR));
		registerRule(buildRule(0.0F, "Populators0", POPULATOR_EXT, IGrammarAPI.POPULATOR));
		registerRule(buildRule(0.0F, POPULATOR_EXT, POPULATOR_EXT, IGrammarAPI.POPULATOR));
		registerRule(buildRule(1.0F, POPULATOR_EXT));
		registerRule(buildRule(0.1F, IGrammarAPI.POPULATOR));

		registerRule(buildRule(1.0F, "Effects0", EFFECT_GEN));
		registerRule(buildRule(0.2F, EFFECT_GEN, EFFECT_GEN, IGrammarAPI.EFFECT));
		registerRule(buildRule(0.8F, EFFECT_GEN, IGrammarAPI.EFFECT));
		registerRule(buildRule(0.0F, "Effects0", EFFECT_EXT, IGrammarAPI.EFFECT));
		registerRule(buildRule(0.0F, EFFECT_EXT, EFFECT_EXT, IGrammarAPI.EFFECT));
		registerRule(buildRule(1.0F, EFFECT_EXT));
		registerRule(buildRule(IGrammarAPI.EFFECT));

		registerRule(buildRule(0.8F, IGrammarAPI.SUNSET_UNCOMMON));
		registerRule(buildRule(0.2F, IGrammarAPI.SUNSET_UNCOMMON, IGrammarAPI.SUNSET));
		registerRule(buildRule(IGrammarAPI.SUNSET));
		registerRule(buildRule(0.0F, SUNSET_EXT, IGrammarAPI.SUNSET));
		registerRule(buildRule(1.0F, SUNSET_EXT));

		registerRule(buildRule(1.0F, IGrammarAPI.ANGLE_SEQ, ANGLE_GEN));
		registerRule(buildRule(0.8F, ANGLE_GEN, ANGLE_GEN, IGrammarAPI.ANGLE_BASIC));
		registerRule(buildRule(0.2F, ANGLE_GEN, IGrammarAPI.ANGLE_BASIC));
		registerRule(buildRule(0.0F, IGrammarAPI.ANGLE_SEQ, ANGLE_EXT, IGrammarAPI.ANGLE_BASIC));
		registerRule(buildRule(0.0F, ANGLE_EXT, IGrammarAPI.ANGLE_SEQ));
		registerRule(buildRule(1.0F, ANGLE_EXT));

		registerRule(buildRule(1.0F, IGrammarAPI.PERIOD_SEQ, PERIOD_GEN));
		registerRule(buildRule(0.8F, PERIOD_GEN, PERIOD_GEN, IGrammarAPI.PERIOD_BASIC));
		registerRule(buildRule(0.2F, PERIOD_GEN, IGrammarAPI.PERIOD_BASIC));
		registerRule(buildRule(0.0F, IGrammarAPI.PERIOD_SEQ, PERIOD_EXT, IGrammarAPI.PERIOD_BASIC));
		registerRule(buildRule(0.0F, PERIOD_EXT, IGrammarAPI.PERIOD_SEQ));
		registerRule(buildRule(1.0F, PERIOD_EXT));

		registerRule(buildRule(1.0F, IGrammarAPI.PHASE_SEQ, PHASE_GEN));
		registerRule(buildRule(0.8F, PHASE_GEN, PHASE_GEN, IGrammarAPI.PHASE_BASIC));
		registerRule(buildRule(0.2F, PHASE_GEN, IGrammarAPI.PHASE_BASIC));
		registerRule(buildRule(0.0F, IGrammarAPI.PHASE_SEQ, PHASE_EXT, IGrammarAPI.PHASE_BASIC));
		registerRule(buildRule(0.0F, PHASE_EXT, IGrammarAPI.PHASE_SEQ));
		registerRule(buildRule(1.0F, PHASE_EXT));

		registerRule(buildRule(1.0F, IGrammarAPI.COLOR_SEQ, COLOR_GEN));
		registerRule(buildRule(0.8F, COLOR_GEN, COLOR_GEN, IGrammarAPI.COLOR_BASIC));
		registerRule(buildRule(0.2F, COLOR_GEN, IGrammarAPI.COLOR_BASIC));
		registerRule(buildRule(0.0F, IGrammarAPI.COLOR_SEQ, COLOR_EXT, IGrammarAPI.COLOR_BASIC));
		registerRule(buildRule(0.0F, COLOR_EXT, IGrammarAPI.COLOR_SEQ));
		registerRule(buildRule(1.0F, COLOR_EXT));

		registerRule(buildRule(1.0F, IGrammarAPI.GRADIENT_SEQ, GRADIENT_GEN));
		registerRule(buildRule(0.5F, GRADIENT_GEN, GRADIENT_GEN, IGrammarAPI.GRADIENT_BASIC));
		registerRule(buildRule(0.5F, GRADIENT_GEN, IGrammarAPI.GRADIENT_BASIC));
		registerRule(buildRule(0.0F, IGrammarAPI.GRADIENT_SEQ, GRADIENT_EXT, IGrammarAPI.GRADIENT_BASIC));
		registerRule(buildRule(0.0F, GRADIENT_EXT, IGrammarAPI.GRADIENT_SEQ));
		registerRule(buildRule(1.0F, GRADIENT_EXT));

		registerRule(buildRule(5.0F, IGrammarAPI.BLOCK_TERRAIN));
		registerRule(buildRule(5.0F, IGrammarAPI.BLOCK_SOLID));
		registerRule(buildRule(5.0F, IGrammarAPI.BLOCK_STRUCTURE));
		registerRule(buildRule(5.0F, IGrammarAPI.BLOCK_ORGANIC));
		registerRule(buildRule(5.0F, IGrammarAPI.BLOCK_CRYSTAL));
		registerRule(buildRule(5.0F, IGrammarAPI.BLOCK_SEA));
		registerRule(buildRule(5.0F, IGrammarAPI.BLOCK_FLUID));
		registerRule(buildRule(5.0F, IGrammarAPI.BLOCK_ANY));

		registerRule(buildRule(1.0F, BLOCK_NONSOLID, IGrammarAPI.BLOCK_FLUID));
		registerRule(buildRule(0.5F, BLOCK_NONSOLID, IGrammarAPI.BLOCK_GAS));
	}

	private static void registerRule(Rule rule) {
		GrammarGenerator.registerRule(rule);
	}

	//XXX: (Helper) Absorb this helper into something else (API?)
	public static Rule buildRule(String parent, String... args) {
		return buildRule(null, parent, args);
	}

	//XXX: (Helper) Absorb this helper into something else (API?)
	private static Rule buildRule(Float rarity, String parent, String... args) {
		ArrayList<String> list = CollectionUtils.buildList(args);
		if (rarity == null) rarity = 1.0F;
		return new Rule(parent, list, rarity);
	}
}
