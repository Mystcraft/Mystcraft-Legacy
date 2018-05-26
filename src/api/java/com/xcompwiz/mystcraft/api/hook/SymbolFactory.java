package com.xcompwiz.mystcraft.api.hook;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

/**
 * Provides methods for generating boilerplate {@link IAgeSymbol}s. These methods do not register the symbol directly. Use the
 * {@link RegistryEvent.Register<IAgeSymbol>} for that. The implementation of this is provided by {@link APIInstanceProvider} as "symbolfact-1". Do NOT
 * implement this yourself!
 * @author xcompwiz
 */
public interface SymbolFactory {

	/**
	 * Deprecated. The ownerModid isn't used any longer, preferably use the method that doesn't require it as parameter.
	 *
	 * Creates a block modifier symbol from a specified block. Remember to register the symbol via {@link RegistryEvent.Register<IAgeSymbol>}.
	 * @param ownerModid the modid of the mod owning/registering this symbol
	 * @param blockState The block to use
	 * @param thirdword The third word in the Narayan poem (symbol rendering)
	 * @param rank The rank category for the symbol (0 super common, 1 common, 2 uncommon, 3 rare, 4+ increasing levels of rarity)
	 * @param categories Category-Integer pairs in object arrays used to specify which categories the block can be used in and what rank the grammar treats the
	 *            rule as. (ex: {BlockCategory.TERRAIN, 4} or {"BlockTerrain", 2})
	 * @return
	 */
	@Deprecated
	public IAgeSymbol createSymbol(String ownerModid, IBlockState blockState, String thirdword, int rank, CategoryPair... categories);

	/**
	 * Creates a block modifier symbol from a specified block. Remember to register the symbol via {@link RegistryEvent.Register<IAgeSymbol>}.
	 * @param blockState The block to use
	 * @param thirdword The third word in the Narayan poem (symbol rendering)
	 * @param rank The rank category for the symbol (0 super common, 1 common, 2 uncommon, 3 rare, 4+ increasing levels of rarity)
	 * @param categories Category-Integer pairs in object arrays used to specify which categories the block can be used in and what rank the grammar treats the
	 *            rule as. (ex: {BlockCategory.TERRAIN, 4} or {"BlockTerrain", 2})
	 * @return
	 */
	public IAgeSymbol createSymbol(IBlockState blockState, String thirdword, int rank, CategoryPair... categories);

	public static class CategoryPair {
		public final BlockCategory category;
		public final int rank;

		public CategoryPair(ResourceLocation category, int rank) {
			this.category = BlockCategory.getBlockCategory(category);
			this.rank = rank;
		}

		public CategoryPair(BlockCategory category, int rank) {
			this.category = category;
			this.rank = rank;
		}
	}
}
