package com.xcompwiz.mystcraft.api.symbol;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

/**
 * For defining information for block modifiers This handles the block modifier system. Use this to get block modifiers for usage as well as to push block
 * modifiers to the age. See {@link BlockCategory}
 * @author xcompwiz
 */
public final class BlockDescriptor {

	public final IBlockState blockstate;
	private final HashMap<ResourceLocation, Boolean> useable = new HashMap<>();

	/**
	 * Describes a block
	 * @param blockstate blockstate to use in generation
	 */
	public BlockDescriptor(IBlockState blockstate) {
		this.blockstate = blockstate;
	}

	/**
	 * Describes a block
	 * @param block ID of the block to use in generation
	 */
	public BlockDescriptor(Block block) {
		this(block.getDefaultState());
	}

	/**
	 * Mark the block descriptor as valid for certain categories of generation example: Stone is valid for Solid, Structure, and Terrain
	 * @param key The category of terrain which is valid. See the definitions in the {@link BlockDescriptor} class.
	 * @param flag Whether it is valid or not
	 */
	public void setUsable(BlockCategory key, boolean flag) {
		if (key == null)
			return;
		if (key == BlockCategory.ANY)
			return;
		this.useable.put(key.getName(), flag);
	}

	/**
	 * Returns if the block descriptor is flagged for satisfying a category of generation Generally, you won't need this function. It is mostly for internal
	 * use.
	 * @param key The category to check, null defaults to ANY
	 * @return True is valid, false otherwise
	 */
	public boolean isUsable(BlockCategory key) {
		if (key == null)
			return true;
		if (key == BlockCategory.ANY)
			return true;
		if (!this.useable.containsKey(key.getName()))
			return false;
		return this.useable.get(key.getName());
	}
}
