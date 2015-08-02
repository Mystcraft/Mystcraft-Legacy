package com.xcompwiz.mystcraft.world;

import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.QUARTZ;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.gen.structure.MapGenScatteredFeatureMyst;

import cpw.mods.fml.common.eventhandler.Event.Result;

public class ChunkProviderMyst implements IChunkProvider {
	private AgeController				controller;
	private Random						rand;
	private NoiseGeneratorPerlin		stoneNoiseGen;
	private World						worldObj;
	private AgeData						agedata;
	private double						stoneNoise[];
	private BiomeGenBase				biomesForGeneration[];

	private MapGenScatteredFeatureMyst	scatteredFeatureGenerator	= new MapGenScatteredFeatureMyst();
	private WorldGenMinable				worldgenminablequartz		= new WorldGenMinable(Blocks.quartz_ore, 13, Blocks.netherrack);

	public ChunkProviderMyst(AgeController ageController, World world, AgeData age) {
		controller = ageController;
		stoneNoise = new double[256];
		worldObj = world;
		agedata = age;
		rand = new Random(agedata.getSeed());
		stoneNoiseGen = new NoiseGeneratorPerlin(this.rand, 4);
	}

	private Block[]	vblocks;
	private byte[]	vmetadata;

	private void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] blocks, byte[] metadata, BiomeGenBase[] abiomegenbase) {
		if (vblocks == null || vblocks.length != blocks.length) vblocks = new Block[blocks.length];
		if (vmetadata == null || vmetadata.length != metadata.length) vmetadata = new byte[metadata.length];
		ArrayMappingUtils.mapLocalToVanilla(blocks, vblocks);
		ArrayMappingUtils.mapLocalToVanilla(metadata, vmetadata);
		ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, chunkX, chunkZ, vblocks, vmetadata, abiomegenbase, this.worldObj);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() != Result.DENY) {
			//TODO: Vanilla is now using a different noise generation system for stone noise
			double noisefactor = 0.03125D;
			this.stoneNoise = stoneNoiseGen.func_151599_a(this.stoneNoise, chunkX * 16, chunkZ * 16, 16, 16, noisefactor * 2.0D, noisefactor * 2.0D, 1.0D);
	
			for (int xoff = 0; xoff < 16; ++xoff) {
				for (int zoff = 0; zoff < 16; ++zoff) {
					BiomeGenBase biomegenbase = abiomegenbase[zoff + xoff * 16];
					biomegenbase.genTerrainBlocks(this.worldObj, this.rand, vblocks, vmetadata, chunkX * 16 + xoff, chunkZ * 16 + zoff, this.stoneNoise[zoff + xoff * 16]);
				}
			}
		}
		ArrayMappingUtils.mapVanillaToLocal(vblocks, blocks);
		ArrayMappingUtils.mapVanillaToLocal(vmetadata, metadata);
	}

	@Override
	public Chunk loadChunk(int i, int j) {
		return provideChunk(i, j);
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ) {
		rand.setSeed(chunkX * 0x4f9939f508L + chunkZ * 0x1ef1565bd5L);
		Block blocks[] = new Block[256 * 256];
		byte metadata[] = new byte[blocks.length];
		controller.generateTerrain(chunkX, chunkZ, blocks, metadata);
		biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		replaceBlocksForBiome(chunkX, chunkZ, blocks, metadata, biomesForGeneration);
		controller.modifyTerrain(chunkX, chunkZ, blocks, metadata);
		this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, null);
		Chunk chunk = new Chunk(worldObj, chunkX, chunkZ);
		mapBlocksToChunk(chunk, blocks, metadata);
		chunk.generateSkylightMap();
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < 128; ++y) {
					chunk.setLightValue(EnumSkyBlock.Block, x, y, z, 0);
				}
			}
		}
		controller.finalizeChunk(chunk, chunkX, chunkZ);
		return chunk;
	}

	private void mapBlocksToChunk(Chunk chunk, Block[] blocks, byte[] metadata) {
		ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
		int layers = blocks.length / 256;
		boolean flag = !worldObj.provider.hasNoSky;
		for (int y = 0; y < layers; ++y) {
			int storagei = y >> 4;
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					int coords = y << 8 | z << 4 | x;
					Block block = blocks[coords];

					if (block != null && block != Blocks.air) {
						if (storageArrays[storagei] == null) {
							storageArrays[storagei] = new ExtendedBlockStorage(storagei << 4, flag);
						}

						storageArrays[storagei].func_150818_a(x, y & 15, z, block);
						storageArrays[storagei].setExtBlockMetadata(x, y & 15, z, metadata[coords]);
					}
				}
			}
		}
		chunk.setStorageArrays(storageArrays);
	}

	@Override
	public boolean chunkExists(int i, int j) {
		try {
			throw new RuntimeException("Something has called chunkExists on the lower-level generation logic. This is a logic error.  Please report this if you see it.");
		} catch (Exception e) {
			LoggerUtils.warn(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void populate(IChunkProvider ichunkprovider, int chunkX, int chunkZ) {
		Chunk chunk = worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
		chunk.isTerrainPopulated = false;
		BlockFalling.fallInstantly = true;
		int x = chunkX * 16;
		int z = chunkZ * 16;
		BiomeGenBase biomegenbase = worldObj.getWorldChunkManager().getBiomeGenAt(x + 16, z + 16); //TODO: (BiomeDecoration) Wrap these biomes?
		rand.setSeed(agedata.getSeed());
		long l1 = (rand.nextLong() / 2L) * 2L + 1L;
		long l2 = (rand.nextLong() / 2L) * 2L + 1L;
		rand.setSeed(chunkX * l1 + chunkZ * l2 ^ agedata.getSeed());

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(ichunkprovider, worldObj, rand, chunkX, chunkZ, false));

		try {
			biomegenbase.decorate(worldObj, rand, x, z);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Biome [%s] threw an error while populating chunk.", biomegenbase.biomeName), e);
		}
		this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.rand, chunkX, chunkZ);
		SpawnerAnimals.performWorldGenSpawning(worldObj, biomegenbase, x + 8, z + 8, 16, 16, rand); // TODO: (Spawning) Rewrite to use getPossibleCreatures
		controller.populate(worldObj, rand, x, z);

		boolean doGen = TerrainGen.generateOre(worldObj, this.rand, worldgenminablequartz, x, z, QUARTZ);
		for (int k1 = 0; doGen && k1 < 16; ++k1) {
			int gx = x + this.rand.nextInt(16);
			int gy = this.rand.nextInt(108) + 10;
			int gz = z + this.rand.nextInt(16);
			worldgenminablequartz.generate(this.worldObj, this.rand, gx, gy, gz);
		}
		for (int i2 = 0; i2 < 16; i2++) {
			for (int j3 = 0; j3 < 16; j3++) {
				int j4 = worldObj.getPrecipitationHeight(x + i2, z + j3);
				if (worldObj.isBlockFreezable(i2 + x, j4 - 1, j3 + z)) {
					worldObj.setBlock(i2 + x, j4 - 1, j3 + z, Blocks.ice, 0, 2);
				}
				if (worldObj.func_147478_e(i2 + x, j4, j3 + z, false)) {
					worldObj.setBlock(i2 + x, j4, j3 + z, Blocks.snow_layer, 0, 2);
				}
			}

		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(ichunkprovider, worldObj, rand, chunkX, chunkZ, false));

		BlockFalling.fallInstantly = false;
		chunk.isTerrainPopulated = true;
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
		return true;
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
	 */
	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public String makeString() {
		return "RandomLevelSource";
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType enumcreaturetype, int i, int j, int k) {
		List<SpawnListEntry> list = null;
		BiomeGenBase biomegenbase = worldObj.getBiomeGenForCoords(i, k);
		if (biomegenbase != null) {
			list = biomegenbase.getSpawnableList(enumcreaturetype);
		}
		return controller.affectCreatureList(enumcreaturetype, list, i, j, k);
	}

	@Override
	public ChunkPosition func_147416_a(World world, String s, int i, int j, int k) {
		return controller.locateTerrainFeature(world, s, i, j, k);
	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public void recreateStructures(int var1, int var2) {
		this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, var1, var2, (Block[]) null);
	}

	// Something for the chunk manager system to handle saving loaded chunk data with current chunk loader
	// Unused for generation chunk providers
	@Override
	public void saveExtraData() {}
}
