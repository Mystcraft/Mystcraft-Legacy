package com.xcompwiz.mystcraft.world.profiling;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBiome;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBlock;
import com.xcompwiz.mystcraft.world.AgeController;
import com.xcompwiz.mystcraft.world.MystEmptyChunk;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;

public class WorldProviderMystDummy extends WorldProviderMyst {

	private static class AnvilChunkLoaderDummy extends AnvilChunkLoader {

		public AnvilChunkLoaderDummy(File p_i2003_1_) {
			super(p_i2003_1_, null);
		}

		@Override
		public boolean chunkExists(World world, int i, int j) {
			return false;
		}

		@Override
		public Chunk loadChunk(World p_75815_1_, int p_75815_2_, int p_75815_3_) throws java.io.IOException {
			return null;
		}

	}

	private static class ChunkProviderServerDummy extends ChunkProviderServer {

		private Chunk defaultEmptyChunk;

		private final Set<Long> droppedChunksSet = Sets.<Long> newHashSet();

		public ChunkProviderServerDummy(WorldServer worldServer, IChunkLoader loader, IChunkGenerator provider) {
			super(worldServer, loader, provider);
			this.defaultEmptyChunk = new MystEmptyChunk(worldServer, 0, 0);
		}

		@Override
		public boolean canSave() {
			return false;
		}

		@Override
		public void unloadAllChunks() {
			for (Chunk chunk : this.id2ChunkMap.values()) {
				this.unload(chunk);
			}
		}

		@Override
		public void unload(Chunk chunkIn) {
			if (this.world.provider.canDropChunk(chunkIn.xPosition, chunkIn.zPosition)) {
				this.droppedChunksSet.add(ChunkPos.asLong(chunkIn.xPosition, chunkIn.zPosition));
				chunkIn.unloaded = true;
			}
		}

		@Nullable
		@Override
		public Chunk getLoadedChunk(int x, int z) {
			long i = ChunkPos.asLong(x, z);
			Chunk chunk = this.id2ChunkMap.get(i);
			if (chunk != null) {
				chunk.unloaded = false;
			}
			return chunk;
		}

		@Override
		public Chunk loadChunk(int chunkX, int chunkZ) {
			if (outOfBounds(chunkX, chunkZ)) {
				return defaultEmptyChunk;
			}
			return super.loadChunk(chunkX, chunkZ);
		}

		@Override
		public boolean tick() {
			//> 80 otherwise we don't have anything to work with in terms of population
			if (!this.droppedChunksSet.isEmpty() && this.droppedChunksSet.size() > 80) {
				Iterator<Long> iterator = this.droppedChunksSet.iterator();
				for (int i = 0; i < 100 && iterator.hasNext(); iterator.remove()) {
					Long olong = iterator.next();
					Chunk chunk = this.id2ChunkMap.get(olong);
					if (chunk != null && chunk.unloadQueued) {
						chunk.onUnload();
						this.id2ChunkMap.remove(olong);
						++i;
					}
				}
				this.chunkLoader.chunkTick();
			}
			return false;
		}

	}

	private class AgeControllerDummy extends AgeController {

		public AgeControllerDummy(World worldObj, AgeData age) {
			super(worldObj, age);
		}

		@Override
		public ChunkProfiler getChunkProfiler() {
			return chunkprofiler;
		}

		@Override
		public void registerDebugInfo(DebugNode node) {}
	}

	public static void setChunkProfiler(ChunkProfiler profiler) {
		chunkprofiler = profiler;
	}

	public static void setBounds(int chunkX_min, int chunkX_max, int chunkZ_min, int chunkZ_max) {
		WorldProviderMystDummy.chunkX_min = chunkX_min;
		WorldProviderMystDummy.chunkZ_min = chunkZ_min;
		WorldProviderMystDummy.chunkZ_max = chunkZ_max;
	}

	private static boolean outOfBounds(int chunkX, int chunkZ) {
		return chunkZ < chunkZ_min || chunkZ > chunkZ_max || chunkX < chunkX_min;
	}

	private AgeController controller;
	private static ChunkProfiler chunkprofiler;
	private static int chunkX_min;
	private static int chunkZ_min;
	private static int chunkZ_max;
	private int chunkX, chunkZ;
	private boolean chunkproviderreplaced;

	private static List<Biome> biomeList;

	//We build a fake dimension setup using our own controller and a predefined agedata setup
	// NOTE: The reason why we do this and not just profile the Overworld is mostly biome distribution:
	//     If we profiled the Overworld we'd be biased towards whatever regions are generated in that dimension. ex. Emeralds could be super valuable.
	@Override protected void init() {
		chunkX = chunkX_min;
		chunkZ = chunkZ_min;
		agedata = new AgeData("CONTROL");
		agedata.setAgeName("CONTROL");
		agedata.setSpawn(null);
		agedata.setInstabilityEnabled(true);

		addBiomeSymbols(agedata);

		agedata.addSymbol("BioConGrid", 0);
		agedata.addSymbol(SymbolBlock.getSymbolIdentifier(Blocks.STONE.getDefaultState()), 0);
		agedata.addSymbol(SymbolBlock.getSymbolIdentifier(Blocks.FLOWING_WATER.getDefaultState()), 0);
		agedata.addSymbol("TerrainNormal", 0);

		//Lakes
		agedata.addSymbol(SymbolBlock.getSymbolIdentifier(Blocks.FLOWING_WATER.getDefaultState()), 0);
		agedata.addSymbol("LakesSurface", 0);

		agedata.addSymbol(SymbolBlock.getSymbolIdentifier(Blocks.FLOWING_LAVA.getDefaultState()), 0);
		agedata.addSymbol("LakesDeep", 0);

		//Caves
		agedata.addSymbol("Caves", 0);
		//Ravines
		agedata.addSymbol("Ravines", 0);
		//Villages
		agedata.addSymbol("Villages", 0);
		//Mineshafts
		agedata.addSymbol("Mineshafts", 0);
		//NormalWeather
		agedata.addSymbol("WeatherNorm", 0);
		//NormalLighting
		agedata.addSymbol("LightingNormal", 0);
		agedata.markVisited();

		controller = new AgeControllerDummy(world, agedata);
		biomeProvider = controller.getBiomeProvider();
		setWorldInfo();
	}

	private void addBiomeSymbols(AgeData agedata) {
		// Use only Overworld biomes. See {GenLayerBiome}.
		if (biomeList == null)
			getAndPrepareBiomeList();
		for (Biome biome : biomeList) {
			agedata.addSymbol(SymbolBiome.getBiomeSymbolId(biome), 0);
		}
	}

	public static List<Biome> getAndPrepareBiomeList() {
		if (biomeList != null)
			return biomeList;
		
		List<Biome> biomeList = new ArrayList<>();

		// Use only Overworld biomes. See {GenLayerBiome}.
		for (net.minecraftforge.common.BiomeManager.BiomeType type : net.minecraftforge.common.BiomeManager.BiomeType.values()) {
			com.google.common.collect.ImmutableList<net.minecraftforge.common.BiomeManager.BiomeEntry> biomesToAdd = net.minecraftforge.common.BiomeManager.getBiomes(type);

			for (net.minecraftforge.common.BiomeManager.BiomeEntry biomeentry : biomesToAdd) {
				biomeList.add(biomeentry.biome);
			}
		}

		biomeList.add(Biomes.DESERT);
		biomeList.add(Biomes.SAVANNA);
		biomeList.add(Biomes.PLAINS);
		WorldProviderMystDummy.biomeList = biomeList;
		return biomeList;
	}

	@Override
	public AgeController getAgeController() {
		return this.controller;
	}

	@Override
	public String getDimensionName() {
		return "CONTROL";
	}

	//We implement this to avoid the weather object updates (since we didn't instantiate them)
	@Override
	public void updateWeather() {
		getAgeController().tick();
		getAgeController().getWeatherController().updateRaining();
	}

	//We implement this to bypass the special biome wrappers (since we didn't instantiate them)
	@Override
	public Biome getBiomeForCoords(BlockPos pos) {
		return this.biomeProvider.getBiome(pos);
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		return true;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public void calculateInitialWeather() {
		super.calculateInitialWeather();
		replaceChunkProvider();
	}

	public void replaceChunkProvider() {
		if (chunkproviderreplaced) return;
		chunkproviderreplaced = true;
		WorldServer world = (WorldServer) this.world;
		ChunkProviderServerDummy theChunkProviderServer = new ChunkProviderServerDummy(world, new AnvilChunkLoaderDummy(((AnvilChunkLoader) world.getChunkProvider().chunkLoader).chunkSaveLocation), this.createChunkGenerator());
		ObfuscationReflectionHelper.setPrivateValue(World.class, world, theChunkProviderServer, "chunkProvider", "field" + "_73020_y");
	}

	public void generateNextChunk() {
		//At every call of this function, we want to fully generate a single chunk.
		IChunkProvider chunkgen = ((WorldServer) this.world).getChunkProvider();
		IChunkLoader chunkloader = ((WorldServer) this.world).getChunkProvider().chunkLoader;

		if (safeLoadChunk(chunkloader, this.world, chunkX, chunkZ) == null) {
			chunkgen.provideChunk(chunkX, chunkZ);
		}
		++chunkZ;
		if (chunkZ > chunkZ_max) {
			chunkZ = chunkZ_min;
			++chunkX;
		}
	}

	//XXX: Duplicated from AgeController
	private Chunk safeLoadChunk(IChunkLoader chunkloader, World worldObj, int par1, int par2) {
		if (chunkloader == null) {
			return null;
		}
		try {
			return chunkloader.loadChunk(worldObj, par1, par2);
		} catch (Exception exception) {
			return null;
		}
	}
}
