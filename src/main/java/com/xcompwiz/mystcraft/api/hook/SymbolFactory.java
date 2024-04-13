package com.xcompwiz.mystcraft.api.hook;

import net.minecraft.block.Block;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

/**
 * Provides methods for generating boilerplate {@link IAgeSymbol}s. These methods do not register the symbol directly. Use the {@link SymbolAPI} for that. The
 * implementation of this is provided by {@link APIInstanceProvider}.
 * @author xcompwiz
 */
public interface SymbolFactory {

	/**
	 * Creates a block modifier symbol from a specified block. Remember to register the symbol via {@link SymbolAPI}.
	 * @param block The block to use
	 * @param metadata The metadata value to use
	 * @param thirdword The third word in the Narayan poem (symbol rendering)
	 * @param rank The rank category for the symbol (0 super common, 1 common, 2 uncommon, 3 rare, 4+ increasing levels of rarity)
	 * @param categories Category-Integer pairs in object arrays used to specify which categories the block can be used in and what rank the grammar treats the
	 *            rule as. (ex: {BlockCategory.TERRAIN, 4} or {"BlockTerrain", 2})
	 * @return
	 */
	public IAgeSymbol createSymbol(Block block, int metadata, String thirdword, int rank, CategoryPair... categories);

	public static class CategoryPair {
		public final BlockCategory category;
		public final int rank;

		public CategoryPair(String category, int rank) {
			this.category = BlockCategory.getBlockCategory(category);
			this.rank = rank;
		}

		public CategoryPair(BlockCategory category, int rank) {
			this.category = category;
			this.rank = rank;
		}
	}
}