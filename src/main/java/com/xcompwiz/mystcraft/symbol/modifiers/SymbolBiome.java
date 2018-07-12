package com.xcompwiz.mystcraft.symbol.modifiers;

import java.util.ArrayList;
import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import com.xcompwiz.mystcraft.utility.ReflectionUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class SymbolBiome extends SymbolBase {

	public static ArrayList<Biome> selectables = new ArrayList<Biome>();

	private Biome biome;
	private String unlocalizedBiomeName;

	public static ResourceLocation getBiomeSymbolId(String ownerModid, Biome biome) {
		return new ResourceLocation(ownerModid, "Biome" + Biome.getIdForBiome(biome));
	}

	public SymbolBiome(String ownerModid, Biome biome) {
		super(getBiomeSymbolId(ownerModid, biome));
		this.biome = biome;
		this.unlocalizedBiomeName = formatted(biome);
		this.setWords(new String[] { WordData.Nature, WordData.Nurture, WordData.Encourage, ReflectionUtil.getBiomeName(biome) + Biome.getIdForBiome(biome) });
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	//TODO: Make into a helper somewhere
	private static String formatted(Biome biome) {
		String regex = "([A-Z][a-z]+_*)";
		String replacement = "$1 ";
		String name = ReflectionUtil.getBiomeName(biome).replaceAll(regex, replacement).replaceAll("([A-Z][a-z]+) +", replacement).trim();
		if (name.endsWith("Biome"))
			name = name.substring(0, name.length() - "Biome".length()).trim();
		return name;
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.setAverageGroundLevel((int) ((biome.getBaseHeight()) * 64 + 64));
		ModifierUtils.pushBiome(controller, biome);
	}

	@Override
	public String generateLocalizedName() {
		return I18n.format("myst.symbol.biome.wrapper", unlocalizedBiomeName);
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
