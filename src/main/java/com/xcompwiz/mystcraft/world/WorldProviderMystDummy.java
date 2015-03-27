package com.xcompwiz.mystcraft.world;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBiome;
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
		private List<Long>	chunkqueue	= new LinkedList<Long>();

		public ChunkProviderServerDummy(WorldServer p_i1520_1_, IChunkLoader p_i1520_2_, IChunkProvider p_i1520_3_) {
			super(p_i1520_1_, p_i1520_2_, p_i1520_3_);
		}

		@Override
		public boolean canSave() {
			return false;
		}

		@Override
		public Chunk originalLoadChunk(int p_73158_1_, int p_73158_2_) {
			Chunk chunk = super.originalLoadChunk(p_73158_1_, p_73158_2_);
			this.chunkqueue.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition)));
			return chunk;
		}

		@Override
		public boolean unloadQueuedChunks() {
			for (int i = 0; i < 100 && this.chunkqueue.size() > 32; ++i) {
				Long olong = this.chunkqueue.get(0);
				Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(olong.longValue());
				if (chunk != null) {
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

	private AgeController	controller;
	public ChunkProfiler	chunkprofiler;

	//We build a fake dimension setup using our own controller and a predefined agedata setup
	@Override
	protected void registerWorldChunkManager() {
		agedata = new AgeData("CONTROL");
		agedata.setAgeName("CONTROL");
		agedata.setSpawn(null);
		agedata.setInstabilityEnabled(true);

		for (BiomeGenBase biome : ModifierBiome.selectables) {
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

	public void setChunkProfiler(ChunkProfiler chunkprofiler) {
		this.chunkprofiler = chunkprofiler;
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

	public void replaceChunkProvider() {
		WorldServer world = (WorldServer) worldObj;
		world.theChunkProviderServer = new ChunkProviderServerDummy(world, new AnvilChunkLoaderDummy(((AnvilChunkLoader) world.theChunkProviderServer.currentChunkLoader).chunkSaveLocation), this.createChunkGenerator());
		ObfuscationReflectionHelper.setPrivateValue(World.class, worldObj, world.theChunkProviderServer, "chunkProvider", "field" + "_73020_y");
	}
}
