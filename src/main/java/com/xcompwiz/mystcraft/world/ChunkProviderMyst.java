package com.xcompwiz.mystcraft.world;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
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
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.gen.structure.MapGenScatteredFeatureMyst;

public class ChunkProviderMyst implements IChunkProvider {
	private AgeController				controller;
	private Random						rand;
	private NoiseGeneratorOctaves		noiseGen4;
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
		noiseGen4 = new NoiseGeneratorOctaves(rand, 4);
	}

	private void replaceBlocksForBiome(int chunkX, int chunkZ, Block blocks[], byte[] metadata, BiomeGenBase abiomegenbase[]) {
		int layers = blocks.length / 256;
		int sealevel = controller.getSeaLevel();
		double noisefactor = 0.03125D;
		stoneNoise = noiseGen4.generateNoiseOctaves(stoneNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, noisefactor * 2D, noisefactor * 2D, noisefactor * 2D);

		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				BiomeGenBase biomegenbase = abiomegenbase[x + z * 16];

				int stone_noise_val = (int) (stoneNoise[z + x * 16] / 3D + 3D + rand.nextDouble() * 0.25D);
				int counter = -1;
				Block surface = biomegenbase.topBlock;
				Block filler = biomegenbase.fillerBlock;
				for (int y = layers - 1; y >= 0; --y) {
					float f = biomegenbase.getFloatTemperature(x, y, z);
					int coords = y << 8 | z << 4 | x;
					Block block = blocks[coords];
					if (block == null || block == Blocks.air || block == Blocks.bedrock) {
						counter = -1;
						continue;
					}
					if (block != Blocks.stone) {
						continue;
					}
					if (counter == -1) {
						if (stone_noise_val <= 0) {
							surface = Blocks.air;
							filler = Blocks.stone;
						} else {
							if (y >= sealevel - 4 && y <= sealevel + 1) {
								surface = biomegenbase.topBlock;
								filler = biomegenbase.fillerBlock;
							}
						}
						if (y < sealevel && surface == null) {
							if (f < 0.15F) {
								surface = Blocks.ice;
							} else {
								surface = Blocks.water;
							}
						}
						counter = stone_noise_val;
						if (y >= sealevel - 1) {
							blocks[coords] = surface;
						} else {
							blocks[coords] = filler;
						}
						continue;
					}
					if (counter <= 0) {
						continue;
					}
					--counter;
					blocks[coords] = filler;
					if (counter == 0 && filler == Blocks.sand) {
						counter = rand.nextInt(4);
						filler = Blocks.sandstone;
					}
				}

			}

		}

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
		controller.getChunkProfiler().baseChunk(chunk, chunkX, chunkZ);
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
		return true;
	}

	@Override
	public void populate(IChunkProvider ichunkprovider, int chunkX, int chunkZ) {
		BlockFalling.fallInstantly = true;
		int x = chunkX * 16;
		int z = chunkZ * 16;
		ChunkCoordinates spawn = worldObj.getSpawnPoint();
		if (spawn != null && spawn.posX >> 4 == chunkX && spawn.posZ >> 4 == chunkZ) generatePlatform(spawn.posX, spawn.posY - 1, spawn.posZ, Blocks.stone);
		BiomeGenBase biomegenbase = worldObj.getWorldChunkManager().getBiomeGenAt(x + 16, z + 16); //TODO: (BiomeDecoration) Wrap these biomes?
		rand.setSeed(agedata.getSeed());
		long l1 = (rand.nextLong() / 2L) * 2L + 1L;
		long l2 = (rand.nextLong() / 2L) * 2L + 1L;
		rand.setSeed(chunkX * l1 + chunkZ * l2 ^ agedata.getSeed());

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(ichunkprovider, worldObj, rand, chunkX, chunkZ, false));

		biomegenbase.decorate(worldObj, rand, x, z);
		this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.rand, chunkX, chunkZ);
		SpawnerAnimals.performWorldGenSpawning(worldObj, biomegenbase, x + 8, z + 8, 16, 16, rand); // TODO: (Spawning) Rewrite to use getPossibleCreatures
		controller.populate(worldObj, rand, x, z);

		for (int k1 = 0; k1 < 16; ++k1) {
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
					worldObj.setBlock(i2 + x, j4, j3 + z, Blocks.snow, 0, 2);
				}
			}

		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(ichunkprovider, worldObj, rand, chunkX, chunkZ, false));

		BlockFalling.fallInstantly = false;
		if (spawn != null && spawn.posX >> 4 == chunkX && spawn.posZ >> 4 == chunkZ) {
			generatePlatform(spawn.posX, spawn.posY - 1, spawn.posZ, Blocks.stone);
		}
		ChunkProfiler profiler = controller.getChunkProfiler();
		profiler.profile(ichunkprovider.provideChunk(chunkX, chunkZ), chunkX, chunkZ);
		int count = profiler.getCount();
		if (count > 400 && count % 100 == 0) {
			controller.updateProfiledInstability();
		}
	}

	private void generatePlatform(int i, int j, int k, Block block) {
		int size = 2;
		for (int x = -size; x <= size; ++x) {
			for (int z = -size; z <= size; ++z) {
				worldObj.setBlock(i + x, j, k + z, block, 0, 2);
				for (int y = j + 1; y < j + 5; ++y)
					worldObj.setBlock(i + x, y, k + z, Blocks.air, 0, 2);
			}
		}
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
