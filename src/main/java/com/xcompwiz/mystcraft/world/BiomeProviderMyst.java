package com.xcompwiz.mystcraft.world;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.IntCache;

public class BiomeProviderMyst extends BiomeProvider {

	private AgeController controller;

	public BiomeProviderMyst(AgeController c) {
		controller = c;
	}

	@Override
	public List<Biome> getBiomesToSpawnIn() {
		return controller.getBiomeController().getValidSpawnBiomes();
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return getBiome(pos, Biomes.PLAINS);
	}

	public Biome getBiome(BlockPos pos, Biome defaultBiome) {
		Biome biome = controller.getBiomeController().getBiomeAtCoords(pos.getX(), pos.getZ());
		controller.modifyBiomeAt(biome, pos.getX(), pos.getZ());
		return biome;
	}

	@Override
	public Biome[] getBiomesForGeneration(Biome aBiome[], int x, int z, int xSize, int zSize) {
		IntCache.resetIntCache();
		if (aBiome == null || aBiome.length < xSize * zSize) {
			aBiome = new Biome[xSize * zSize];
		}
		aBiome = controller.getBiomeController().getBiomesForGeneration(aBiome, x, z, xSize, zSize);
		controller.modifyGenerationBiomesAt(aBiome, x, z, xSize, zSize);
		return aBiome;
	}

	/**
	 * Gets biomes to use for the blocks and loads the other data like
	 * temperature and humidity onto the WorldChunkManager.
	 */
	public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth) {
		return this.getBiomes(oldBiomeList, x, z, width, depth, true);
	}

	/**
	 * Gets a list of biomes for the specified blocks.
	 */
	public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
		IntCache.resetIntCache();
		if (listToReuse == null || listToReuse.length < width * length) {
			listToReuse = new Biome[width * length];
		}
		listToReuse = controller.getBiomeController().getBiomesAtCoords(listToReuse, x, z, width, length, cacheFlag);
		controller.modifyBiomesAt(listToReuse, x, z, width, length, cacheFlag);
		return listToReuse;
	}

	@Override
	public float getTemperatureAtHeight(float temp, int y) {
		return controller.getTemperatureAtHeight(temp, y);
	}

	@Override
	public boolean areBiomesViable(int i, int j, int k, List<Biome> list) {
		int l = i - k >> 2;
		int i1 = j - k >> 2;
		int j1 = i + k >> 2;
		int k1 = j + k >> 2;
		int l1 = (j1 - l) + 1;
		int i2 = (k1 - i1) + 1;
		Biome biomes[] = getBiomesForGeneration(null, l, i1, l1, i2);
		for (int j2 = 0; j2 < l1 * i2; j2++) {
			if (!list.contains(biomes[j2])) {
				return false;
			}
		}

		return true;
	}

	@Override
	public BlockPos findBiomePosition(int i, int j, int k, List<Biome> list, Random random) {
		int l = i - k >> 2;
		int i1 = j - k >> 2;
		int j1 = i + k >> 2;
		int k1 = j + k >> 2;
		int l1 = (j1 - l) + 1;
		int i2 = (k1 - i1) + 1;
		Biome biomes[] = getBiomesForGeneration(null, l, i1, l1, i2);
		BlockPos blockpos = null;
		int j2 = 0;
		for (int k2 = 0; k2 < 16 * 16; k2++) {
			int l2 = l + k2 % l1 << 2;
			int i3 = i1 + k2 / l1 << 2;
			if (list.contains(biomes[k2]) && (blockpos == null || random.nextInt(j2 + 1) == 0)) {
				blockpos = new BlockPos(l2, 0, i3);
				j2++;
			}
		}

		return blockpos;
	}

	@Override
	public void cleanupCache() {
		controller.getBiomeController().cleanupCache();
	}
}
