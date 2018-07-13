package com.xcompwiz.mystcraft.world;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.chunk.ChunkPrimerMyst;
import com.xcompwiz.mystcraft.world.gen.structure.MapGenScatteredFeatureMyst;

import net.minecraft.block.BlockFalling;
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
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChunkProviderMyst implements IChunkGenerator {

	private AgeController controller;
	private Random rand;
	private NoiseGeneratorPerlin stoneNoiseGen;
	private World worldObj;
	private AgeData agedata;
	private double stoneNoise[];
	private Biome biomesForGeneration[];

	private MapGenScatteredFeatureMyst scatteredFeatureGenerator = new MapGenScatteredFeatureMyst();
	private WorldGenMinable worldgenminablequartz = new WorldGenMinable(Blocks.QUARTZ_ORE.getDefaultState(), 13, BlockMatcher.forBlock(Blocks.NETHERRACK));

	public ChunkProviderMyst(AgeController ageController, World world, AgeData age) {
		controller = ageController;
		stoneNoise = new double[256];
		worldObj = world;
		agedata = age;
		rand = new Random(agedata.getSeed());
		stoneNoiseGen = new NoiseGeneratorPerlin(this.rand, 4);
	}

	private void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] aBiome) {
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
	}

	@Override
	@Nonnull
	public Chunk generateChunk(int chunkX, int chunkZ) {
		rand.setSeed(chunkX * 0x4f9939f508L + chunkZ * 0x1ef1565bd5L);
		ChunkPrimerMyst primer = new ChunkPrimerMyst();

		// Base terrain generation
		controller.generateTerrain(chunkX, chunkZ, primer);

		// Get list of biomes in chunk
		biomesForGeneration = worldObj.getBiomeProvider().getBiomesForGeneration(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);

		// perform biome decoration pass
		primer.inBiomeDecoration = true;
		replaceBlocksForBiome(chunkX, chunkZ, primer, biomesForGeneration);
		primer.inBiomeDecoration = false;

		// Perform terrain modification pass (ex. caves, skylands)
		controller.modifyTerrain(chunkX, chunkZ, primer);

		// generate features like witch huts, etc //TODO: more such features; world specific control; etc 
		this.scatteredFeatureGenerator.generate(worldObj, chunkX, chunkZ, primer);

		// make chunk
		Chunk chunk = new Chunk(worldObj, primer, chunkX, chunkZ);

		// lighting calcs
		chunk.generateSkylightMap();
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < 128; ++y) {
					chunk.setLightFor(EnumSkyBlock.BLOCK, new BlockPos(x, y, z), 0);
				}
			}
		}

		// Final logic pass through chunk (ex. Floating Island biome replacement) 
		controller.finalizeChunk(chunk, chunkX, chunkZ);
		return chunk;
	}

	@Override
	public void populate(int chunkX, int chunkZ) {
		Chunk chunk = worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
		chunk.setTerrainPopulated(false);
		BlockFalling.fallInstantly = true;
		int x = chunkX * 16;
		int z = chunkZ * 16;
		BlockPos blockpos = new BlockPos(x, 0, z);
		Biome biome = worldObj.getBiome(blockpos.add(16, 0, 16));
		this.rand.setSeed(worldObj.getSeed());
		long k = this.rand.nextLong() / 2L * 2L + 1L;
		long l = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long) chunkX * k + (long) chunkZ * l ^ worldObj.getSeed());
		boolean flag = false;
		ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
		ForgeEventFactory.onChunkPopulate(true, this, worldObj, this.rand, chunkX, chunkZ, flag);

		this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkpos);

		try {
			biome.decorate(this.worldObj, this.rand, new BlockPos(x, 0, z));
		} catch (Exception e) {
			throw new RuntimeException(String.format("Biome [%s] threw an error while populating chunk.", biome.getRegistryName()), e);
		}
		if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, chunkX, chunkZ, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS)) {
			WorldEntitySpawner.performWorldGenSpawning(this.worldObj, biome, x + 8, z + 8, 16, 16, this.rand);
		}
		controller.populate(worldObj, rand, x, z);
		blockpos = blockpos.add(8, 0, 8);

		boolean doGen = TerrainGen.generateOre(worldObj, this.rand, worldgenminablequartz, new BlockPos(x, 0, z), OreGenEvent.GenerateMinable.EventType.QUARTZ);
		for (int k1 = 0; doGen && k1 < 16; ++k1) {
			int gx = x + this.rand.nextInt(16);
			int gy = this.rand.nextInt(108) + 10;
			int gz = z + this.rand.nextInt(16);
			worldgenminablequartz.generate(this.worldObj, this.rand, new BlockPos(gx, gy, gz));
		}

		if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, chunkX, chunkZ, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE)) {
			for (int k2 = 0; k2 < 16; ++k2) {
				for (int j3 = 0; j3 < 16; ++j3) {
					BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k2, 0, j3));
					BlockPos blockpos2 = blockpos1.down();

					if (this.worldObj.canBlockFreezeWater(blockpos2)) {
						this.worldObj.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
					}

					if (this.worldObj.canSnowAt(blockpos1, true)) {
						this.worldObj.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
					}
				}
			}
		}

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.worldObj, this.rand, chunkX, chunkZ, flag);
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
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
		return controller.locateTerrainFeature(worldIn, structureName, position, findUnexplored);
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		return controller.isInsideFeature(worldIn, structureName, pos);
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
