package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IChunkProviderFinalization;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBiome;
import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;
import com.xcompwiz.mystcraft.world.gen.MapGenFloatingIslands;
import com.xcompwiz.mystcraft.world.gen.MapGenFloatingIslands.IModifiedHandler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

public class SymbolFloatingIslands extends SymbolBase {

	public SymbolFloatingIslands(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Biome biome = ModifierUtils.popBiome(controller);
		BlockDescriptor blockdesc = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);

		Random rand = new Random(seed);
		if (biome == null) {
			biome = SymbolBiome.getRandomBiome(rand);
		}
		BiomeReplacer replacer = new BiomeReplacer(biome);
		controller.registerInterface(new TerrainAlteration(seed, blockdesc, biome, replacer));
		controller.registerInterface(replacer);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class TerrainAlteration implements ITerrainAlteration {

		private MapGenAdvanced generator;

		public TerrainAlteration(long seed, BlockDescriptor blockdesc, Biome biome, IModifiedHandler callback) {
			IBlockState state = Blocks.STONE.getDefaultState();
			if (blockdesc != null) {
				state = blockdesc.blockstate;
			}
			generator = new MapGenFloatingIslands(seed, biome, callback, state);
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
			generator.generate(worldObj.getChunkProvider(), worldObj, chunkX, chunkZ, primer);
		}

	}

	public class BiomeReplacer implements IModifiedHandler, IChunkProviderFinalization {

		private Biome biome;
		private HashMap<List<Integer>, boolean[]> chunks = new HashMap<>();

		public BiomeReplacer(Biome biome) {
			this.biome = biome;
		}

		@Override
		public void passModified(int chunkX, int chunkZ, boolean[] modified, Biome biome) {
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
			if (modified == null) {
				return;
			}
			byte[] biomes = chunk.getBiomeArray();
			for (int coords = 0; coords < modified.length; ++coords) {
				if (modified[coords]) {
					biomes[coords] = (byte) (Biome.getIdForBiome(biome) & 255);
				}
			}
		}
	}
}
