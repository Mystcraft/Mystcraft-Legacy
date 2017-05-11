package com.xcompwiz.mystcraft.world;

import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.gen.structure.MapGenScatteredFeatureMyst;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
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
	private WorldGenMinable				worldgenminablequartz		= new WorldGenMinable(Blocks.QUARTZ_ORE.getDefaultState(), 13, BlockMatcher.forBlock(Blocks.NETHERRACK));

	public ChunkProviderMyst(AgeController ageController, World world, AgeData age) {
		controller = ageController;
		stoneNoise = new double[256];
		worldObj = world;
		agedata = age;
		rand = new Random(agedata.getSeed());
		stoneNoiseGen = new NoiseGeneratorPerlin(this.rand, 4);
	}

	private IBlockState[] vblocks;

	private void replaceBlocksForBiome(int chunkX, int chunkZ, IBlockState[] blocks, Biome[] aBiome) {
		if (vblocks == null || vblocks.length != blocks.length) {
		    vblocks = new IBlockState[blocks.length];
        }
		ArrayMappingUtils.mapLocalToVanilla(blocks, vblocks);
		StateBasedChunkPrimer primer = StateBasedChunkPrimer.intoData(blocks); //Primer that'll feed/mirror this state-array

        ArrayMappingUtils.fillPrimerVanillaIndexing(primer, blocks);
		ChunkGeneratorEvent.ReplaceBiomeBlocks event = new ChunkGeneratorEvent.ReplaceBiomeBlocks(this, chunkX, chunkZ, primer, this.worldObj);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() != Event.Result.DENY) {
			//TODO: Vanilla is now using a different noise generation system for stone noise
			double noisefactor = 0.03125D;
			this.stoneNoise = stoneNoiseGen.getRegion(this.stoneNoise, chunkX * 16, chunkZ * 16, 16, 16, noisefactor * 2.0D, noisefactor * 2.0D, 1.0D);
	
			for (int xoff = 0; xoff < 16; ++xoff) {
				for (int zoff = 0; zoff < 16; ++zoff) {
					Biome b = aBiome[zoff + xoff * 16];
					b.genTerrainBlocks(this.worldObj, this.rand, primer, chunkX * 16 + xoff, chunkZ * 16 + zoff, this.stoneNoise[zoff + xoff * 16]);
				}
			}
		}
		ArrayMappingUtils.mapVanillaToLocal(vblocks, blocks);
	}

	@Override
    @Nonnull
	public Chunk provideChunk(int chunkX, int chunkZ) {
		rand.setSeed(chunkX * 0x4f9939f508L + chunkZ * 0x1ef1565bd5L);
		IBlockState[] blocks = new IBlockState[256 * 256];
		controller.generateTerrain(chunkX, chunkZ, blocks);
		biomesForGeneration = worldObj.getBiomeProvider().getBiomesForGeneration(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		replaceBlocksForBiome(chunkX, chunkZ, blocks, biomesForGeneration);
		controller.modifyTerrain(chunkX, chunkZ, blocks);
		this.scatteredFeatureGenerator.generate(worldObj, chunkX, chunkZ, null);
		Chunk chunk = new Chunk(worldObj, chunkX, chunkZ);
		mapBlocksToChunk(chunk, blocks);
		chunk.generateSkylightMap();
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < 128; ++y) {
				    chunk.setLightFor(EnumSkyBlock.BLOCK, new BlockPos(x, y, z), 0);
				}
			}
		}
		controller.finalizeChunk(chunk, chunkX, chunkZ);
		return chunk;
	}

	private void mapBlocksToChunk(Chunk chunk, IBlockState[] blocks) {
		ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
		int layers = blocks.length / 256;
		boolean flag = !worldObj.provider.hasNoSky();
		for (int y = 0; y < layers; ++y) {
			int storagei = y >> 4;
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					int coords = y << 8 | z << 4 | x;
					IBlockState block = blocks[coords];

					if (block != null && !block.getBlock().equals(Blocks.AIR)) {
						if (storageArrays[storagei] == null) {
							storageArrays[storagei] = new ExtendedBlockStorage(storagei << 4, flag);
						}

						storageArrays[storagei].set(x, y & 15, z, block);
					}
				}
			}
		}
		chunk.setStorageArrays(storageArrays);
	}

	@Override
	public void populate(int chunkX, int chunkZ) {
		Chunk chunk = worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
		chunk.setTerrainPopulated(false);
		BlockFalling.fallInstantly = true;
		int x = chunkX * 16;
		int z = chunkZ * 16;
		Biome Biome = worldObj.getBiomeProvider().getBiome(new BlockPos(x + 16, 0, z + 16)); //TODO: (BiomeDecoration) Wrap these biomes?
		rand.setSeed(agedata.getSeed());
		long l1 = (rand.nextLong() / 2L) * 2L + 1L;
		long l2 = (rand.nextLong() / 2L) * 2L + 1L;
		rand.setSeed(chunkX * l1 + chunkZ * l2 ^ agedata.getSeed());

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(this, worldObj, rand, chunkX, chunkZ, false));

		try {
			Biome.decorate(worldObj, rand, new BlockPos(x, 0, z)); //Column gen?..
		} catch (Exception e) {
			throw new RuntimeException(String.format("Biome [%s] threw an error while populating chunk.", Biome.getBiomeName()), e);
		}
		this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, new ChunkPos(chunkX, chunkZ));
        WorldEntitySpawner.performWorldGenSpawning(worldObj, Biome, x + 8, z + 8, 16, 16, rand); // TODO: (Spawning) Rewrite to use getPossibleCreatures
		controller.populate(worldObj, rand, x, z);

		boolean doGen = TerrainGen.generateOre(worldObj, this.rand, worldgenminablequartz, new BlockPos(x, 0, z), OreGenEvent.GenerateMinable.EventType.QUARTZ);
		for (int k1 = 0; doGen && k1 < 16; ++k1) {
			int gx = x + this.rand.nextInt(16);
			int gy = this.rand.nextInt(108) + 10;
			int gz = z + this.rand.nextInt(16);
			worldgenminablequartz.generate(this.worldObj, this.rand, new BlockPos(gx, gy, gz));
		}
		for (int i2 = 0; i2 < 16; i2++) {
			for (int j3 = 0; j3 < 16; j3++) {
				int j4 = worldObj.getPrecipitationHeight(new BlockPos(x + i2, 0, z + j3)).getY();
				BlockPos at = new BlockPos(i2 + x, j4, j3 + z);
				if (worldObj.canBlockFreezeWater(at.down())) {
					worldObj.setBlockState(at.down(), Blocks.ICE.getDefaultState(), 2);
				}
				if (worldObj.canSnowAt(at, false)) {
					worldObj.setBlockState(at, Blocks.SNOW_LAYER.getDefaultState(), 2);
				}
			}

		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(this, worldObj, rand, chunkX, chunkZ, false));

		BlockFalling.fallInstantly = false;
		chunk.setTerrainPopulated(true);
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
