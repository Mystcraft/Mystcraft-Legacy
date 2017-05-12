package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Used to produce the base terrain for an age.
 * @author xcompwiz
 */
public interface ITerrainGenerator {

	/**
	 * Generates the base terrain for the age.
	 * @param chunkX The chunk x coordinate in chunk space
	 * @param chunkZ The chunk z coordinate in chunk space
	 * @param primer The block array being manipulated
	 */
	public abstract void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer);
}
