package com.xcompwiz.mystcraft.world;

import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.QUARTZ;

import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.gen.structure.MapGenScatteredFeatureMyst;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import javax.annotation.Nullable;

public class ChunkProviderMyst implements IChunkGenerator {

	private AgeController				controller;
	private Random						rand;
	private NoiseGeneratorPerlin		stoneNoiseGen;
	private World						worldObj;
	private AgeData						agedata;
	private double						stoneNoise[];
	private Biome				biomesForGeneration[];

	private MapGenScatteredFeatureMyst	scatteredFeatureGenerator	= new MapGenScatteredFeatureMyst();
	private WorldGenMinable				worldgenminablequartz		= new WorldGenMinable(Blocks.QUARTZ_ORE, 13, Blocks.NETHERRACK);

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

	private void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] blocks, byte[] metadata, Biome[] aBiome) {
		if (vblocks == null || vblocks.length != blocks.length) vblocks = new Block[blocks.length];
		if (vmetadata == null || vmetadata.length != metadata.length) vmetadata = new byte[metadata.length];
		ArrayMappingUtils.mapLocalToVanilla(blocks, vblocks);
		ArrayMappingUtils.mapLocalToVanilla(metadata, vmetadata);
		ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, chunkX, chunkZ, vblocks, vmetadata, aBiome, this.worldObj);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() != Result.DENY) {
			//TODO: Vanilla is now using a different noise generation system for stone noise
			double noisefactor = 0.03125D;
			this.stoneNoise = stoneNoiseGen.func_151599_a(this.stoneNoise, chunkX * 16, chunkZ * 16, 16, 16, noisefactor * 2.0D, noisefactor * 2.0D, 1.0D);
	
			for (int xoff = 0; xoff < 16; ++xoff) {
				for (int zoff = 0; zoff < 16; ++zoff) {
					Biome Biome = aBiome[zoff + xoff * 16];
					Biome.genTerrainBlocks(this.worldObj, this.rand, vblocks, vmetadata, chunkX * 16 + xoff, chunkZ * 16 + zoff, this.stoneNoise[zoff + xoff * 16]);
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

					if (block != null && block != Blocks.AIR) {
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
	public void populate(int x, int z) {

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
		Biome Biome = worldObj.getWorldChunkManager().getBiomeGenAt(x + 16, z + 16); //TODO: (BiomeDecoration) Wrap these biomes?
		rand.setSeed(agedata.getSeed());
		long l1 = (rand.nextLong() / 2L) * 2L + 1L;
		long l2 = (rand.nextLong() / 2L) * 2L + 1L;
		rand.setSeed(chunkX * l1 + chunkZ * l2 ^ agedata.getSeed());

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(ichunkprovider, worldObj, rand, chunkX, chunkZ, false));

		try {
			Biome.decorate(worldObj, rand, x, z);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Biome [%s] threw an error while populating chunk.", Biome.biomeName), e);
		}
		this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.rand, chunkX, chunkZ);
		SpawnerAnimals.performWorldGenSpawning(worldObj, Biome, x + 8, z + 8, 16, 16, rand); // TODO: (Spawning) Rewrite to use getPossibleCreatures
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
					worldObj.setBlock(i2 + x, j4 - 1, j3 + z, Blocks.ICE, 0, 2);
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
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		List<SpawnListEntry> list = null;
		Biome b = worldObj.getBiome(pos);
		list = b.getSpawnableList(creatureType);
		return controller.affectCreatureList(creatureType, list, pos);
	}

	@Nullable
	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_) {
		return controller.locateTerrainFeature(worldIn, structureName, position, p_180513_4_);
	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		this.scatteredFeatureGenerator.generate(worldObj, x, z, null);
	}

}
