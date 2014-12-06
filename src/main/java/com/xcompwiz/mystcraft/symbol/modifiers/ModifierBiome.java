package com.xcompwiz.mystcraft.symbol.modifiers;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class ModifierBiome extends SymbolBase {
	public static ArrayList<BiomeGenBase>	selectables	= new ArrayList<BiomeGenBase>();

	private BiomeGenBase					biome;
	private String							displayName;

	public ModifierBiome(BiomeGenBase biome) {
		this.biome = biome;
		this.displayName = formatted();
		if (!this.displayName.endsWith("Biome")) this.displayName += " Biome";
		this.setWords(new String[] { WordData.Nature, WordData.Nurture, WordData.Encourage, biome.biomeName + biome.biomeID });
	}

	private String formatted() {
		String regex = "([A-Z][a-z]+)";
		String replacement = "$1 ";

		return biome.biomeName.replaceAll(regex, replacement).replaceAll("([A-Z][a-z]+)  ", replacement).trim();
	}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.setAverageGroundLevel((int) ((biome.rootHeight) * 64 + 64));
		ModifierUtils.pushBiome(controller, biome);
	}

	@Override
	public String identifier() {
		return "Biome" + biome.biomeID;
	}

	@Override
	public String displayName() {
		return displayName;
	}

	public static BiomeGenBase getRandomBiome(Random rand) {
		BiomeGenBase biome = null;
		while (biome == null) {
			if (selectables.size() > 0) {
				biome = selectables.get(rand.nextInt(selectables.size()));
			} else {
				biome = BiomeGenBase.getBiome(rand.nextInt(BiomeGenBase.getBiomeGenArray().length));
			}
		}
		return biome;
	}
}
