package com.xcompwiz.mystcraft.symbol;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SymbolBase implements IAgeSymbol {

	protected final ResourceLocation registryName;
	private ArrayList<Rule> rules;
	private String[] words;

	@SideOnly(Side.CLIENT)
	private String localizedName;

	public SymbolBase(ResourceLocation registryName) {
		this.registryName = registryName;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

	public IAgeSymbol setCardRank(Integer cardrank) {
		if (cardrank == null)
			return this;
		InternalAPI.symbolValues.setSymbolCardRank(this, cardrank);
		return this;
	}

	public final ArrayList<Rule> getRules() {
		ArrayList<Rule> out = createRules();
		if (rules != null) {
			if (out == null)
				out = new ArrayList<Rule>();
			out.addAll(rules);
		}
		return out;
	}

	protected ArrayList<Rule> createRules() {
		return null;
	}

	public void addRule(Rule rule) {
		if (rules == null)
			rules = new ArrayList<Rule>();
		rules.add(rule);
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public IAgeSymbol setRegistryName(ResourceLocation registryName) {
		return this;
	}

	/**
	 * Returns the unlocalized name
	 * @return The lookup key used to map the symbol to a user readable text string
	 */
	protected String getUnlocalizedName() {
		return "myst.symbol." + this.getRegistryName().getResourcePath();
	}

	@SideOnly(Side.CLIENT)
	protected String generateLocalizedName() {
		return I18n.format(this.getUnlocalizedName() + ".name");
	}

	/**
	 * Returns the user localized name
	 * @return Name the user sees for the symbol
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public String getLocalizedName() {
		if (localizedName == null)
			localizedName = generateLocalizedName();
		return localizedName;
	}

	@Override
	public abstract void registerLogic(AgeDirector controller, long seed);

	@Override
	public int instabilityModifier(int count) {
		return 0;
	}

	@Override
	public final String[] getPoem() {
		return words;
	}
}
