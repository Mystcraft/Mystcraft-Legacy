package com.xcompwiz.mystcraft.api.symbol;

import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Implement and register this through the ISymbolAPI to add your own symbols to Mystcraft
 */
public interface IAgeSymbol extends IForgeRegistryEntry<IAgeSymbol> {

	/**
	 * Called when it is time for the Symbol to register its logic elements to the controller
	 * @param controller The controller to register the logic elements to
	 * @param seed A unique seed generated from the world seed and the symbol position
	 * @param A unique seed for the symbol call. The seed is based on the age seed and the order of the symbols, providing a deterministic way of making the
	 *            same symbol produce different results within the same age
	 */
	public abstract void registerLogic(AgeDirector controller, long seed);

	/**
	 * How much instability should be added to the world. This is called every time the symbol is added to the world (if it is stacked). It is not necessary to
	 * use this function to add instability for duplicated critical logic. Used to limit stacking (ex. After three mineshaft symbols, add 100 instability every
	 * time mineshafts is added).
	 * @param count How many times the symbol has been added thus far (first time = 1)
	 * @return the amount of instability to add
	 */
	public abstract int instabilityModifier(int count);

	/**
	 * Defines if the IAgeSymbol should generate a config entry in the Mystcraft config once the symbol is registered The config entry generated defines if the
	 * symbol is enabled or not.
	 * @return true to generate the config entry and prevent registration
	 */
	public abstract boolean generatesConfigOption();

	/**
	 * Returns the user localized name
	 * @return Name the user sees for the symbol
	 */
	@SideOnly(Side.CLIENT)
	public abstract String getLocalizedName();

	/**
	 * Returns a list of words that are used to render the symbol. Should return 4 words to build a Narayan "poem". See {@link WordData}.
	 * @return 4 element array of words to be mapped to drawn symbol parts
	 */
	public abstract String[] getPoem();

	@Override
	default public Class<IAgeSymbol> getRegistryType() {
		return IAgeSymbol.class;
	}

}
