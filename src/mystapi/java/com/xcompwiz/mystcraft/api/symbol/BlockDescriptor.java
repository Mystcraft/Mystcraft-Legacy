package com.xcompwiz.mystcraft.api.symbol;

import java.util.HashMap;

import net.minecraft.block.Block;


/**
 * For defining information for block modifiers This handles the block modifier system. Use this to get block modifiers for usage as well as to push block
 * modifiers to the age. See {@link BlockCategory}
 * @author xcompwiz
 */
public final class BlockDescriptor {
	public final Block						block;
	public final byte						metadata;
	private final HashMap<String, Boolean>	useable	= new HashMap<String, Boolean>();

	/**
	 * Describes a block
	 * @param blockId ID of the block to use in generation
	 * @param metadata Metadata value of the block to use in generation
	 */
	public BlockDescriptor(Block block, byte metadata) {
		this.block = block;
		this.metadata = metadata;
	}

	/**
	 * Describes a block
	 * @param blockId ID of the block to use in generation
	 */
	public BlockDescriptor(Block block) {
		this(block, (byte) 0);
	}

	/**
	 * Mark the block descriptor as valid for certain categories of generation example: Stone is valid for Solid, Structure, and Terrain
	 * @param key The category of terrain which is valid. See the definitions in the {@link BlockDescriptor} class.
	 * @param flag Whether it is valid or not
	 */
	public void setUsable(BlockCategory key, boolean flag) {
		if (key == null) return;
		if (key == BlockCategory.ANY) return;
		this.useable.put(key.getName(), flag);
	}

	/**
	 * Returns if the block descriptor is flagged for satisfying a category of generation Generally, you won't need this function. It is mostly for internal
	 * use.
	 * @param key The category to check, null defaults to ANY
	 * @return True is valid, false otherwise
	 */
	public boolean isUsable(BlockCategory key) {
		if (key == null) return true;
		if (key == BlockCategory.ANY) return true;
		if (!this.useable.containsKey(key.getName())) return false;
		return this.useable.get(key.getName());
	}
}
