package com.xcompwiz.mystcraft.world.profiling;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBiome;
import com.xcompwiz.mystcraft.world.AgeController;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class WorldProviderMystDummy extends WorldProviderMyst {

	private static class AnvilChunkLoaderDummy extends AnvilChunkLoader {
		public AnvilChunkLoaderDummy(File p_i2003_1_) {
			super(p_i2003_1_);
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
		private Chunk		defaultEmptyChunk;

		private List<Long>	chunkqueue	= new LinkedList<Long>();

		public ChunkProviderServerDummy(WorldServer worldServer, IChunkLoader loader, IChunkProvider provider) {
			super(worldServer, loader, provider);
			this.defaultEmptyChunk = new EmptyChunk(worldServer, 0, 0);
		}

		@Override
		public boolean canSave() {
			return false;
		}

		@Override
		public Chunk originalLoadChunk(int chunkX, int chunkZ) {
			if (outOfBounds(chunkX, chunkZ)) { return defaultEmptyChunk; }
			Chunk chunk = super.originalLoadChunk(chunkX, chunkZ);
			this.chunkqueue.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition)));
			return chunk;
		}

		@Override
		public boolean unloadQueuedChunks() {
			for (int i = 0; i < 100 && this.chunkqueue.size() > 64; ++i) {
				Long olong = this.chunkqueue.get(0);
				Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(olong.longValue());
				if (chunk != null) {
                    chunk.onChunkUnload();
					this.loadedChunks.remove(chunk);
				}
				chunkqueue.remove(0);
				this.loadedChunkHashMap.remove(olong.longValue());
			}
			return this.currentChunkProvider.unloadQueuedChunks();
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

	private AgeController			controller;
	private static ChunkProfiler	chunkprofiler;
	private static int				chunkX_min;
	private static int				chunkZ_min;
	private static int				chunkZ_max;
	private int						chunkX, chunkZ;
	private boolean					chunkproviderreplaced;

	//We build a fake dimension setup using our own controller and a predefined agedata setup
	@Override
	protected void registerWorldChunkManager() {
		chunkX = chunkX_min;
		chunkZ = chunkZ_min;
		agedata = new AgeData("CONTROL");
		agedata.setAgeName("CONTROL");
		agedata.setSpawn(null);
		agedata.setInstabilityEnabled(true);

		for (BiomeGenBase biome : SymbolBiome.selectables) {
			agedata.addSymbol("Biome" + biome.biomeID, 0);
		}
		agedata.addSymbol("BioConGrid", 0);
		agedata.addSymbol("ModMat_tile.stone", 0);
		agedata.addSymbol("ModMat_tile.water", 0);
		agedata.addSymbol("TerrainNormal", 0);

		//Lakes
		agedata.addSymbol("ModMat_tile.water", 0);
		agedata.addSymbol("LakesSurface", 0);

		agedata.addSymbol("ModMat_tile.lava", 0);
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

		controller = new AgeControllerDummy(worldObj, agedata);
		worldChunkMgr = controller.getWorldChunkManager();
		setWorldInfo();
	}

	@Override
	public AgeController getAgeController() {
		return this.controller;
	}

	@Override
	public String getDimensionName() {
		return "CONTROL";
	}

	//We implement this to bypass the special biome wrappers
	@Override
	public void updateWeather() {
		getAgeController().tick();
		getAgeController().getWeatherController().updateRaining();
	}

	//We implement this to avoid the weather object updates (since we didn't instantiate them)
	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return controller.getBiomeController().getBiomeAtCoords(x, z);
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
		WorldServer world = (WorldServer) worldObj;
		world.theChunkProviderServer = new ChunkProviderServerDummy(world, new AnvilChunkLoaderDummy(((AnvilChunkLoader) world.theChunkProviderServer.currentChunkLoader).chunkSaveLocation), this.createChunkGenerator());
		ObfuscationReflectionHelper.setPrivateValue(World.class, worldObj, world.theChunkProviderServer, "chunkProvider", "field" + "_73020_y");
	}

	public void generateNextChunk() {
		//At every call of this function, we want to fully generate a single chunk.
		IChunkProvider chunkgen = ((WorldServer) this.worldObj).theChunkProviderServer;
		IChunkLoader chunkloader = ((WorldServer) this.worldObj).theChunkProviderServer.currentChunkLoader;

		if (safeLoadChunk(chunkloader, this.worldObj, chunkX, chunkZ) == null) {
			chunkgen.loadChunk(chunkX, chunkZ);
		}
		++chunkZ;
		if (chunkZ > chunkZ_max) {
			chunkZ = chunkZ_min;
			++chunkX;
		}
	}

	//XXX: Duplicated from AgeController
	private Chunk safeLoadChunk(IChunkLoader chunkloader, World worldObj, int par1, int par2) {
		if (chunkloader == null) { return null; }
		try {
			return chunkloader.loadChunk(worldObj, par1, par2);
		} catch (Exception exception) {
			return null;
		}
	}
}
