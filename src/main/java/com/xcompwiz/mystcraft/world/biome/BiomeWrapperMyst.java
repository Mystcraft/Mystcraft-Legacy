package com.xcompwiz.mystcraft.world.biome;

import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.utility.ReflectionUtil;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.block.BlockFlower;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeWrapperMyst extends Biome {

	private final WorldProviderMyst provider;
	private final Biome baseBiome;

	private static BiomeProperties generateBiomeProperties(WorldProviderMyst provider, Biome biome) {
		BiomeProperties properties = new BiomeProperties(ReflectionUtil.getBiomeName(biome));
		properties.setBaseHeight(biome.getBaseHeight());
		properties.setHeightVariation(biome.getHeightVariation());
		if (provider != null) {
			ResourceLocation biomeIdentifier = biome.getRegistryName();
			properties.setTemperature(provider.getTemperature(biome.getDefaultTemperature(), biomeIdentifier));
			properties.setRainfall(provider.getRainfall(biome.getRainfall(), biomeIdentifier));
		} else {
			properties.setTemperature(biome.getDefaultTemperature());
			properties.setRainfall(biome.getRainfall());
		}
		properties.setWaterColor(biome.getWaterColorMultiplier());
		if (!biome.canRain())
			properties.setRainDisabled();
		if (biome.getEnableSnow())
			properties.setSnowEnabled();
		if (biome.isMutation())
			properties.setBaseBiome("mutant"); // XXX: This one is a bit weird, but should be (mostly) functionally equivalent

		return properties;
	}

	public BiomeWrapperMyst(WorldProviderMyst provider, Biome baseBiome) {
		super(generateBiomeProperties(provider, baseBiome));

		//HellFire> This way we always will mirror down towards the actual biome in registry questions.
		//This might be unsafe?
		ModContainer active = Loader.instance().activeModContainer();
		String makeActiveId = baseBiome.getRegistryName().getResourceDomain();
		ModContainer tryActive = "minecraft".equals(makeActiveId) ? Loader.instance().getMinecraftModContainer() : Loader.instance().getIndexedModList().get(makeActiveId);
		Loader.instance().setActiveModContainer(tryActive);
		setRegistryName(baseBiome.getRegistryName());
		Loader.instance().setActiveModContainer(active);

		this.provider = provider;
		this.baseBiome = baseBiome;
		this.decorator = baseBiome.decorator;

		this.topBlock = baseBiome.topBlock;
		this.fillerBlock = baseBiome.fillerBlock;

		this.spawnableMonsterList = null;
		this.spawnableCreatureList = null;
		this.spawnableWaterCreatureList = null;
		this.spawnableCaveCreatureList = null;
	}

	@Override
	public BiomeDecorator createBiomeDecorator() {
	    if(baseBiome == null) {
	        return null;
        }
		return baseBiome.decorator;
	}

	@Override
	public boolean isMutation() {
		return baseBiome.isMutation();
	}

	@Override
	public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
		return baseBiome.getRandomTreeFeature(rand);
	}

	/**
	 * Gets a WorldGen appropriate for this biome.
	 */
	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random par1Random) {
		return baseBiome.getRandomWorldGenForGrass(par1Random);
	}

	@Override
	public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
		return baseBiome.pickRandomFlower(rand, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * takes temperature, returns color
	 */
	public int getSkyColorByTemp(float temp) {
		return baseBiome.getSkyColorByTemp(temp);
	}

	/**
	 * Returns the correspondent list of the EnumCreatureType informed.
	 */
	@Override
	public List<Biome.SpawnListEntry> getSpawnableList(EnumCreatureType par1EnumCreatureType) {
		return baseBiome.getSpawnableList(par1EnumCreatureType);
	}

	/**
	 * Returns true if the biome have snowfall instead a normal rain.
	 */
	@Override
	public boolean getEnableSnow() {
		if (this.provider == null) {
			return this.baseBiome.getEnableSnow();
		}
		return provider.getEnableSnow(baseBiome.getEnableSnow(), baseBiome.getRegistryName());
	}

	/**
	 * Return true if the biome supports lightning bolt spawn, either by have the bolts enabled and have rain enabled.
	 */
	@Override
	public boolean canRain() {
		if (this.provider == null) {
			return this.baseBiome.canRain();
		}
		return this.getEnableSnow() ? false : provider.getEnableRain(baseBiome.canRain(), baseBiome.getRegistryName());
	}

	/**
	 * Checks to see if the rainfall level of the biome is extremely high
	 */
	@Override
	public boolean isHighHumidity() {
		return baseBiome.isHighHumidity();
	}

	/**
	 * returns the chance a creature has to spawn.
	 */
	@Override
	public float getSpawningChance() {
		return baseBiome.getSpawningChance();
	}

	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos) {
		baseBiome.decorate(worldIn, rand, pos);
	}

	@Override
	public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
		baseBiome.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Provides the basic grass color based on the biome temperature and rainfall
	 */
	public int getGrassColorAtPos(BlockPos pos) {
		if (this.provider == null) {
			return this.baseBiome.getGrassColorAtPos(pos);
		}
		Color color = provider.getStaticColor(IStaticColorProvider.GRASS, this.baseBiome, pos);
		if (color != null)
			return getModdedBiomeGrassColor(color.asInt());
		return this.baseBiome.getGrassColorAtPos(pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Provides the basic foliage color based on the biome temperature and rainfall
	 */
	public int getFoliageColorAtPos(BlockPos pos) {
		if (this.provider == null) {
			return this.baseBiome.getFoliageColorAtPos(pos);
		}
		Color color = provider.getStaticColor(IStaticColorProvider.FOLIAGE, this.baseBiome, pos);
		if (color != null)
			return getModdedBiomeFoliageColor(color.asInt());
		return this.baseBiome.getFoliageColorAtPos(pos);
	}

	@Override
	public Class<? extends Biome> getBiomeClass() {
		return baseBiome.getBiomeClass();
	}

	@Override
	public Biome.TempCategory getTempCategory() {
		return baseBiome.getTempCategory();
	}

	@Override
	public int getWaterColorMultiplier() {
		return this.getWaterColorMultiplier(BlockPos.ORIGIN); // FORGE: Actual coords?
	}

	public int getWaterColorMultiplier(BlockPos pos) {
		if (this.provider == null) {
			return this.baseBiome.getWaterColorMultiplier();
		}
		Color color = provider.getStaticColor(IStaticColorProvider.WATER, baseBiome, pos);
		if (color != null)
			return getModdedBiomeWaterColor(color.asInt());
		return this.baseBiome.getWaterColorMultiplier();
	}

	public int getModdedBiomeWaterColor(int original) {
		BiomeEvent.GetWaterColor event = new BiomeEvent.GetWaterColor(baseBiome, original);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getNewColor();
	}
}
