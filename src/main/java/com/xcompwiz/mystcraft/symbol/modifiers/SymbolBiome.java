package com.xcompwiz.mystcraft.symbol.modifiers;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolBiome extends SymbolBase {
	public static ArrayList<BiomeGenBase>	selectables	= new ArrayList<BiomeGenBase>();

	private BiomeGenBase					biome;
	private String							displayName;

	public SymbolBiome(BiomeGenBase biome) {
		super("Biome" + biome.biomeID);
		this.biome = biome;
		this.displayName = formatted(biome);
		this.setWords(new String[] { WordData.Nature, WordData.Nurture, WordData.Encourage, biome.biomeName + biome.biomeID });
	}

	//TODO: Make into a helper somewhere
	private static String formatted(BiomeGenBase biome) {
		String regex = "([A-Z][a-z]+)";
		String replacement = "$1 ";
		String name = biome.biomeName.replaceAll(regex, replacement).replaceAll("([A-Z][a-z]+)  ", replacement).trim();
		if (name.endsWith("Biome")) name = name.substring(0, name.length() - "Biome".length()).trim();
		name = StatCollector.translateToLocalFormatted("myst.symbol.biome.wrapper", name);
		return name;
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.setAverageGroundLevel((int) ((biome.rootHeight) * 64 + 64));
		ModifierUtils.pushBiome(controller, biome);
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
