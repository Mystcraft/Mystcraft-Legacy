package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Applied to the chunk data after initial generation
 * @author xcompwiz
 */
public interface ITerrainAlteration {

	/**
	 * Called after the initial terrain generation in order to alter the terrain. This is called after the biome block replacement
	 * @param worldObj The world object of the chunk being modified. Do NOT access blocks directly through this.
	 * @param chunkX The chunk x coordinate in chunk space
	 * @param chunkZ The chunk z coordinate in chunk space
	 * @param primer The block array being manipulated
	 */
	public abstract void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer);
}
