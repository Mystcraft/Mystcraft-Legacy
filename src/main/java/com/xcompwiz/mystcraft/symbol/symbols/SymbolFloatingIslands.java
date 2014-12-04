package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.IChunkProviderFinalization;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBiome;
import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;
import com.xcompwiz.mystcraft.world.gen.MapGenFloatingIslands;
import com.xcompwiz.mystcraft.world.gen.MapGenFloatingIslands.IModifiedHandler;

public class SymbolFloatingIslands extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		BiomeGenBase biome = ModifierUtils.popBiome(controller);
		BlockDescriptor blockdesc = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);

		Random rand = new Random(seed);
		if (biome == null) {
			biome = ModifierBiome.getRandomBiome(rand);
		}
		BiomeReplacer replacer = new BiomeReplacer(biome);
		controller.registerInterface(new TerrainAlteration(seed, blockdesc, biome, replacer));
		controller.registerInterface(replacer);
	}

	@Override
	public String identifier() {
		return "FloatIslands";
	}

	private class TerrainAlteration implements ITerrainAlteration {
		private MapGenAdvanced	generator;

		public TerrainAlteration(long seed, BlockDescriptor blockdesc, BiomeGenBase biome, IModifiedHandler callback) {
			Block block = Blocks.stone;
			byte meta = 0;
			if (blockdesc != null) {
				block = blockdesc.block;
				meta = blockdesc.metadata;
			}
			generator = new MapGenFloatingIslands(seed, biome, callback, block, meta);
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
			generator.generate(worldObj.getChunkProvider(), worldObj, chunkX, chunkZ, blocks, metadata);
		}

	}

	public class BiomeReplacer implements IModifiedHandler, IChunkProviderFinalization {

		private BiomeGenBase						biome;
		private HashMap<List<Integer>, boolean[]>	chunks	= new HashMap<List<Integer>, boolean[]>();

		public BiomeReplacer(BiomeGenBase biome) {
			this.biome = biome;
		}

		@Override
		public void passModified(int chunkX, int chunkZ, boolean[] modified, BiomeGenBase biome) {
			List<Integer> key = Arrays.asList(chunkX, chunkZ);
			boolean[] prev = chunks.get(key);
			if (prev != null) {
				for (int coords = 0; coords < modified.length; ++coords) {
					modified[coords] |= prev[coords];
				}
			}
			this.chunks.put(key, modified);
		}

		@Override
		public void finalizeChunk(Chunk chunk, int chunkX, int chunkZ) {
			boolean[] modified = chunks.remove(Arrays.asList(chunkX, chunkZ));
			if (modified == null) { return; }
			byte[] biomes = chunk.getBiomeArray();
			for (int coords = 0; coords < modified.length; ++coords) {
				if (modified[coords]) {
					biomes[coords] = (byte) (biome.biomeID & 255);
				}
			}
		}
	}
}
