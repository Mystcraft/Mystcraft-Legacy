package com.xcompwiz.mystcraft.api.symbol;

import net.minecraft.block.Block;

/**
 * Provides methods for generating boilerplate {@link IAgeSymbol}s. These methods do not register the symbol directly. Use the {@link ISymbolAPI} for that. The
 * implementation of this is provided by MystAPI Do NOT implement this yourself!
 * @author xcompwiz
 */
public interface ISymbolFactory {
	/**
	 * Creates a block modifier symbol from a specified block. Remember to register the symbol via {@link ISymbolAPI}.
	 * @param block The block to use
	 * @param metadata The metadata value to use
	 * @param thirdword The third word in the Narayan poem (symbol rendering)
	 * @param rank The rank category for the symbol (0 super common, 1 common, 2 uncommon, 3 rare, 4+ increasing levels of rarity)
	 * @param categories Category-Integer pairs in object arrays used to specify which categories the block can be used in and what rank the grammar treats the
	 *            rule as. (ex: {BlockCategory.TERRAIN, 4} or {"BlockTerrain", 2})
	 * @return
	 */
	public IAgeSymbol createSymbol(Block block, int metadata, String thirdword, int rank, Object[]... categories);
}
