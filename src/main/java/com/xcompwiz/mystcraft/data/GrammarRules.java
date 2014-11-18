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
		registerRule(buildRule(0, ROOT, IGrammarAPI.TERRAIN, IGrammarAPI.BIOMECONTROLLER, IGrammarAPI.WEATHER, IGrammarAPI.LIGHTING, "Spawning0", "Suns0", "Moons0", "Starfields0", "Doodads0", "Visuals0", "TerrainAlts0", "Populators0", "Effects0"));

		registerRule(buildRule(10, "Spawning0"));

		registerRule(buildRule(1, IGrammarAPI.BIOME_LIST, BIOME_GEN));
		registerRule(buildRule(2, BIOME_GEN, BIOME_GEN, IGrammarAPI.BIOME));
		registerRule(buildRule(3, BIOME_GEN, IGrammarAPI.BIOME));
		registerRule(buildRule(null, IGrammarAPI.BIOME_LIST, BIOME_EXT, IGrammarAPI.BIOME));
		registerRule(buildRule(null, BIOME_EXT, BIOME_EXT, IGrammarAPI.BIOME_LIST));
		registerRule(buildRule(1, BIOME_EXT));

		registerRule(buildRule(1, "Suns0", SUN_GEN));
		registerRule(buildRule(4, SUN_GEN, SUN_GEN, IGrammarAPI.SUN));
		registerRule(buildRule(2, SUN_GEN, IGrammarAPI.SUN));
		registerRule(buildRule(null, "Suns0", SUN_EXT, IGrammarAPI.SUN));
		registerRule(buildRule(null, SUN_EXT, SUN_EXT, IGrammarAPI.SUN));
		registerRule(buildRule(1, SUN_EXT));

		registerRule(buildRule(1, "Moons0", MOON_GEN));
		registerRule(buildRule(2, MOON_GEN, MOON_GEN, IGrammarAPI.MOON));
		registerRule(buildRule(2, MOON_GEN, IGrammarAPI.MOON));
		registerRule(buildRule(null, "Moons0", MOON_EXT, IGrammarAPI.MOON));
		registerRule(buildRule(null, MOON_EXT, MOON_EXT, IGrammarAPI.MOON));
		registerRule(buildRule(1, MOON_EXT));

		registerRule(buildRule(1, "Starfields0", STARFIELD_GEN));
		registerRule(buildRule(3, STARFIELD_GEN, STARFIELD_GEN, IGrammarAPI.STARFIELD));
		registerRule(buildRule(2, STARFIELD_GEN, IGrammarAPI.STARFIELD));
		registerRule(buildRule(null, "Starfields0", STARFIELD_EXT, IGrammarAPI.STARFIELD));
		registerRule(buildRule(null, STARFIELD_EXT, STARFIELD_EXT, IGrammarAPI.STARFIELD));
		registerRule(buildRule(1, STARFIELD_EXT));
		registerRule(buildRule(1, IGrammarAPI.STARFIELD));

		registerRule(buildRule(1, "Doodads0", DOODAD_GEN));
		registerRule(buildRule(5, DOODAD_GEN, DOODAD_GEN, IGrammarAPI.DOODAD));
		registerRule(buildRule(2, DOODAD_GEN, IGrammarAPI.DOODAD));
		registerRule(buildRule(null, "Doodads0", DOODAD_EXT, IGrammarAPI.DOODAD));
		registerRule(buildRule(null, DOODAD_EXT, DOODAD_EXT, IGrammarAPI.DOODAD));
		registerRule(buildRule(1, DOODAD_EXT));
		registerRule(buildRule(0, IGrammarAPI.DOODAD));

		registerRule(buildRule(1, "Visuals0", VISUAL_EFFECT_GEN));
		registerRule(buildRule(3, VISUAL_EFFECT_GEN, VISUAL_EFFECT_GEN, IGrammarAPI.VISUAL_EFFECT));
		registerRule(buildRule(2, VISUAL_EFFECT_GEN, IGrammarAPI.VISUAL_EFFECT));
		registerRule(buildRule(null, "Visuals0", VISUAL_EFFECT_EXT, IGrammarAPI.VISUAL_EFFECT));
		registerRule(buildRule(null, VISUAL_EFFECT_EXT, VISUAL_EFFECT_EXT, IGrammarAPI.VISUAL_EFFECT));
		registerRule(buildRule(1, VISUAL_EFFECT_EXT));
		registerRule(buildRule(1, IGrammarAPI.VISUAL_EFFECT));

		registerRule(buildRule(1, "TerrainAlts0", TERRAINALT_GEN));
		registerRule(buildRule(2, TERRAINALT_GEN, TERRAINALT_GEN, IGrammarAPI.TERRAINALT));
		registerRule(buildRule(2, TERRAINALT_GEN, IGrammarAPI.TERRAINALT));
		registerRule(buildRule(null, "TerrainAlts0", TERRAINALT_EXT, IGrammarAPI.TERRAINALT));
		registerRule(buildRule(null, TERRAINALT_EXT, TERRAINALT_EXT, IGrammarAPI.TERRAINALT));
		registerRule(buildRule(1, TERRAINALT_EXT));
		registerRule(buildRule(4, IGrammarAPI.TERRAINALT));

		registerRule(buildRule(1, "Populators0", POPULATOR_GEN));
		registerRule(buildRule(2, POPULATOR_GEN, POPULATOR_GEN, IGrammarAPI.POPULATOR));
		registerRule(buildRule(3, POPULATOR_GEN, IGrammarAPI.POPULATOR, IGrammarAPI.POPULATOR));
		registerRule(buildRule(null, "Populators0", POPULATOR_EXT, IGrammarAPI.POPULATOR));
		registerRule(buildRule(null, POPULATOR_EXT, POPULATOR_EXT, IGrammarAPI.POPULATOR));
		registerRule(buildRule(1, POPULATOR_EXT));
		registerRule(buildRule(4, IGrammarAPI.POPULATOR));

		registerRule(buildRule(1, "Effects0", EFFECT_GEN));
		registerRule(buildRule(3, EFFECT_GEN, EFFECT_GEN, IGrammarAPI.EFFECT));
		registerRule(buildRule(2, EFFECT_GEN, IGrammarAPI.EFFECT));
		registerRule(buildRule(null, "Effects0", EFFECT_EXT, IGrammarAPI.EFFECT));
		registerRule(buildRule(null, EFFECT_EXT, EFFECT_EXT, IGrammarAPI.EFFECT));
		registerRule(buildRule(1, EFFECT_EXT));
		registerRule(buildRule(1, IGrammarAPI.EFFECT));

		registerRule(buildRule(2, IGrammarAPI.SUNSET_UNCOMMON));
		registerRule(buildRule(3, IGrammarAPI.SUNSET_UNCOMMON, IGrammarAPI.SUNSET));
		registerRule(buildRule(1, IGrammarAPI.SUNSET));
		registerRule(buildRule(null, SUNSET_EXT, IGrammarAPI.SUNSET));
		registerRule(buildRule(1, SUNSET_EXT));

		registerRule(buildRule(1, IGrammarAPI.ANGLE_SEQ, ANGLE_GEN));
		registerRule(buildRule(2, ANGLE_GEN, ANGLE_GEN, IGrammarAPI.ANGLE_BASIC));
		registerRule(buildRule(3, ANGLE_GEN, IGrammarAPI.ANGLE_BASIC));
		registerRule(buildRule(null, IGrammarAPI.ANGLE_SEQ, ANGLE_EXT, IGrammarAPI.ANGLE_BASIC));
		registerRule(buildRule(null, ANGLE_EXT, IGrammarAPI.ANGLE_SEQ));
		registerRule(buildRule(1, ANGLE_EXT));

		registerRule(buildRule(1, IGrammarAPI.PERIOD_SEQ, PERIOD_GEN));
		registerRule(buildRule(2, PERIOD_GEN, PERIOD_GEN, IGrammarAPI.PERIOD_BASIC));
		registerRule(buildRule(3, PERIOD_GEN, IGrammarAPI.PERIOD_BASIC));
		registerRule(buildRule(null, IGrammarAPI.PERIOD_SEQ, PERIOD_EXT, IGrammarAPI.PERIOD_BASIC));
		registerRule(buildRule(null, PERIOD_EXT, IGrammarAPI.PERIOD_SEQ));
		registerRule(buildRule(1, PERIOD_EXT));

		registerRule(buildRule(1, IGrammarAPI.PHASE_SEQ, PHASE_GEN));
		registerRule(buildRule(2, PHASE_GEN, PHASE_GEN, IGrammarAPI.PHASE_BASIC));
		registerRule(buildRule(3, PHASE_GEN, IGrammarAPI.PHASE_BASIC));
		registerRule(buildRule(null, IGrammarAPI.PHASE_SEQ, PHASE_EXT, IGrammarAPI.PHASE_BASIC));
		registerRule(buildRule(null, PHASE_EXT, IGrammarAPI.PHASE_SEQ));
		registerRule(buildRule(1, PHASE_EXT));

		registerRule(buildRule(1, IGrammarAPI.COLOR_SEQ, COLOR_GEN));
		registerRule(buildRule(2, COLOR_GEN, COLOR_GEN, IGrammarAPI.COLOR_BASIC));
		registerRule(buildRule(3, COLOR_GEN, IGrammarAPI.COLOR_BASIC));
		registerRule(buildRule(null, IGrammarAPI.COLOR_SEQ, COLOR_EXT, IGrammarAPI.COLOR_BASIC));
		registerRule(buildRule(null, COLOR_EXT, IGrammarAPI.COLOR_SEQ));
		registerRule(buildRule(1, COLOR_EXT));

		registerRule(buildRule(1, IGrammarAPI.GRADIENT_SEQ, GRADIENT_GEN));
		registerRule(buildRule(2, GRADIENT_GEN, GRADIENT_GEN, IGrammarAPI.GRADIENT_BASIC));
		registerRule(buildRule(2, GRADIENT_GEN, IGrammarAPI.GRADIENT_BASIC));
		registerRule(buildRule(null, IGrammarAPI.GRADIENT_SEQ, GRADIENT_EXT, IGrammarAPI.GRADIENT_BASIC));
		registerRule(buildRule(null, GRADIENT_EXT, IGrammarAPI.GRADIENT_SEQ));
		registerRule(buildRule(1, GRADIENT_EXT));

		registerRule(buildRule(0, IGrammarAPI.BLOCK_TERRAIN));
		registerRule(buildRule(0, IGrammarAPI.BLOCK_SOLID));
		registerRule(buildRule(0, IGrammarAPI.BLOCK_STRUCTURE));
		registerRule(buildRule(0, IGrammarAPI.BLOCK_ORGANIC));
		registerRule(buildRule(0, IGrammarAPI.BLOCK_CRYSTAL));
		registerRule(buildRule(0, IGrammarAPI.BLOCK_SEA));
		registerRule(buildRule(0, IGrammarAPI.BLOCK_FLUID));
		registerRule(buildRule(0, IGrammarAPI.BLOCK_ANY));

		registerRule(buildRule(1, BLOCK_NONSOLID, IGrammarAPI.BLOCK_FLUID));
		registerRule(buildRule(2, BLOCK_NONSOLID, IGrammarAPI.BLOCK_GAS));
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
