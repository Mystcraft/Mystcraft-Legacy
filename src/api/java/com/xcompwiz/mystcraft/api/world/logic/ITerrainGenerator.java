package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.block.state.IBlockState;

/**
 * Used to produce the base terrain for an age.
 * @author xcompwiz
 */
public interface ITerrainGenerator {

	/**
	 * Generates the base terrain for the age.
	 * @param chunkX The chunk x coordinate in chunk space
	 * @param chunkZ The chunk z coordinate in chunk space
	 * @param blocks The block array being manipulated (y << 8 | z << 4 | x)
	 */
	public abstract void generateTerrain(int chunkX, int chunkZ, IBlockState[] blocks);
}
