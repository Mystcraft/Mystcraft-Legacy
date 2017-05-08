package com.xcompwiz.mystcraft.world.profiling;

import java.util.Random;

import com.xcompwiz.mystcraft.world.AgeController;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

public class MystWorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World worldObj, IChunkProvider chunkGenerator, IChunkProvider ichunkprovider) {
		if (!(worldObj.provider instanceof WorldProviderMyst)) return;
		AgeController controller = ((WorldProviderMyst) worldObj.provider).getAgeController();

		ChunkPos spawn = worldObj.getSpawnPoint();
		if (spawn != null && spawn.posX >> 4 == chunkX && spawn.posZ >> 4 == chunkZ) generatePlatform(worldObj, spawn.posX, spawn.posY - 1, spawn.posZ, Blocks.COBBLESTONE);

		ChunkProfiler profiler = controller.getChunkProfiler();
		profileCompletedChunks(profiler, chunkX, chunkZ, ichunkprovider);
	}

	private void profileCompletedChunks(ChunkProfiler profiler, int chunkX, int chunkZ, IChunkProvider ichunkprovider) {
		//TODO: Multiple checkoff for profiling chunks (greenlight flag system)
		//Check nearby chunks if we've completed them without generating any other chunks. 
		for (int i = chunkX - 1; i <= chunkX + 1; ++i) {
			for (int k = chunkZ - 1; k <= chunkZ + 1; ++k) {
				if (!ichunkprovider.chunkExists(i, k)) continue;
				Chunk chunk = ichunkprovider.loadChunk(i, k);
				if (checkForCompletion(ichunkprovider, chunk, i, k)) {
					ChunkProfilerManager.addChunk(profiler, chunk);
				}
			}
		}
	}

	private boolean checkForCompletion(IChunkProvider ichunkprovider, Chunk chunk, int chunkX, int chunkZ) {
		if (!chunk.isTerrainPopulated) return false;
		if (!ichunkprovider.chunkExists(chunkX - 1, chunkZ - 1)) return false;
		if (!ichunkprovider.chunkExists(chunkX, chunkZ - 1)) return false;
		if (!ichunkprovider.chunkExists(chunkX + 1, chunkZ - 1)) return false;
		if (!ichunkprovider.chunkExists(chunkX - 1, chunkZ)) return false;
		if (!ichunkprovider.chunkExists(chunkX + 1, chunkZ)) return false;
		if (!ichunkprovider.chunkExists(chunkX - 1, chunkZ + 1)) return false;
		if (!ichunkprovider.chunkExists(chunkX, chunkZ + 1)) return false;
		if (!ichunkprovider.chunkExists(chunkX + 1, chunkZ + 1)) return false;

		if (!ichunkprovider.provideChunk(chunkX - 1, chunkZ - 1).isTerrainPopulated) return false;
		if (!ichunkprovider.provideChunk(chunkX, chunkZ - 1).isTerrainPopulated) return false;
		if (!ichunkprovider.provideChunk(chunkX + 1, chunkZ - 1).isTerrainPopulated) return false;
		if (!ichunkprovider.provideChunk(chunkX - 1, chunkZ).isTerrainPopulated) return false;
		if (!ichunkprovider.provideChunk(chunkX + 1, chunkZ).isTerrainPopulated) return false;
		if (!ichunkprovider.provideChunk(chunkX - 1, chunkZ + 1).isTerrainPopulated) return false;
		if (!ichunkprovider.provideChunk(chunkX, chunkZ + 1).isTerrainPopulated) return false;
		if (!ichunkprovider.provideChunk(chunkX + 1, chunkZ + 1).isTerrainPopulated) return false;
		return true;
	}

	private static void generatePlatform(World worldObj, int i, int j, int k, Block block) {
		int size = 2;
		for (int x = -size; x <= size; ++x) {
			for (int z = -size; z <= size; ++z) {
				worldObj.setBlock(i + x, j, k + z, block, 0, 2);
				for (int y = j + 1; y < j + 5; ++y)
					worldObj.setBlock(i + x, y, k + z, Blocks.AIR, 0, 2);
			}
		}
	}

}
