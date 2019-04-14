package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.annotation.Nullable;

/**
 * Applied to the chunk data after initial generation
 * @author xcompwiz
 */
public interface ITerrainAlteration {

	/**
	 * Called after the initial terrain generation in order to alter the terrain. This is called after the biome block replacement
	 *
	 * @param worldObj The world object of the chunk being modified. Do NOT access blocks directly through this.
	 * @param chunkX The chunk x coordinate in chunk space
	 * @param chunkZ The chunk z coordinate in chunk space
	 * @param primer The block array being manipulated
	 */
	public abstract void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer);

	/**
	 * Called before any terrain generation is done on the chunk.
	 * May be used to filter some blockstates on the ChunkPrimer to be placed into the world.
	 *
	 * @param world The world object of the chunk generated into. DO NOT ACCESS BLOCKS THROUGH THIS.
	 * @param primer The chunk primer to be buffered into
	 * @param chunkX The chunk x coordinate in chunk space
	 * @param chunkZ The chunk z coordinate in chunk space
	 *
	 * @return a ChunkPrimer Filter or null for no filtering
	 */
	@Nullable
	default public IPrimerFilter getGenerationFilter(World world, ChunkPrimer primer, int chunkX, int chunkZ) {
		return null;
	}

}
