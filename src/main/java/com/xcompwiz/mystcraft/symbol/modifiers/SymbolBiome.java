package com.xcompwiz.mystcraft.symbol.modifiers;

import java.util.ArrayList;
import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.client.resources.I18n;
import net.minecraft.world.biome.Biome;

public class SymbolBiome extends SymbolBase {
	public static ArrayList<Biome>	selectables	= new ArrayList<Biome>();

	private Biome					biome;
	private String					displayName;

	public static String getBiomeSymbolId(Biome biome) {
		return "Biome" + Biome.getIdForBiome(biome);
	}

	public SymbolBiome(Biome biome) {
		super(getBiomeSymbolId(biome));
		this.biome = biome;
		this.displayName = formatted(biome);
		this.setWords(new String[] { WordData.Nature, WordData.Nurture, WordData.Encourage, biome.getBiomeName() + Biome.getIdForBiome(biome) });
	}

	//TODO: Make into a helper somewhere
	private static String formatted(Biome biome) {
		String regex = "([A-Z][a-z]+)";
		String replacement = "$1 ";
		String name = biome.getBiomeName().replaceAll(regex, replacement).replaceAll("([A-Z][a-z]+)  ", replacement).trim();
		if (name.endsWith("Biome")) name = name.substring(0, name.length() - "Biome".length()).trim();
		name = I18n.format("myst.symbol.biome.wrapper", name);
		return name;
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.setAverageGroundLevel((int) ((biome.getBaseHeight()) * 64 + 64));
		ModifierUtils.pushBiome(controller, biome);
	}

	@Override
	public String displayName() {
		return displayName;
	}

	public static Biome getRandomBiome(Random rand) {
		Biome biome = null;
		while (biome == null) {
			if (selectables.size() > 0) {
				biome = selectables.get(rand.nextInt(selectables.size()));
			} else {
				biome = Biome.getBiome(rand.nextInt(Biome.REGISTRY.getKeys().size()));
			}
		}
		return biome;
	}
}
