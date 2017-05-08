package com.xcompwiz.mystcraft.world.biome;

import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeWrapperMyst extends Biome {

	private final WorldProviderMyst	provider;
	private final Biome		baseBiome;

	public BiomeWrapperMyst(WorldProviderMyst provider, Biome baseBiome) {
		super(baseBiome.biomeID, false);

		this.provider = provider;
		this.baseBiome = baseBiome;

		this.biomeName = baseBiome.biomeName;
		this.color = baseBiome.color;
		field_150609_ah = baseBiome.field_150609_ah;

		this.topBlock = baseBiome.topBlock;
		field_150604_aj = baseBiome.field_150604_aj;
		this.fillerBlock = baseBiome.fillerBlock;
		this.field_76754_C = baseBiome.field_76754_C;
		this.rootHeight = baseBiome.rootHeight;
		this.heightVariation = baseBiome.heightVariation;
		this.temperature = baseBiome.temperature;
		this.rainfall = baseBiome.rainfall;
		if (provider != null) {
			this.temperature = provider.getTemperature(temperature, this.biomeID);
			this.rainfall = provider.getRainfall(rainfall, this.biomeID);
		}
		this.theBiomeDecorator = baseBiome.theBiomeDecorator;
		this.waterColorMultiplier = baseBiome.waterColorMultiplier;
		this.spawnableMonsterList = null;
		this.spawnableCreatureList = null;
		this.spawnableWaterCreatureList = null;
		this.spawnableCaveCreatureList = null;
		this.worldGeneratorTrees = null;
		this.worldGeneratorBigTree = null;
		this.worldGeneratorSwamp = null;
	}

	@Override
	public BiomeDecorator createBiomeDecorator() {
		return getBiome(this.biomeID).theBiomeDecorator;
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random rand) {
		return baseBiome.func_150567_a(rand);
	}

	/**
	 * Gets a WorldGen appropriate for this biome.
	 */
	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random par1Random) {
		return baseBiome.getRandomWorldGenForGrass(par1Random);
	}

	@Override
	public String func_150572_a(Random rand, int p_150572_2_, int p_150572_3_, int p_150572_4_) {
		return baseBiome.func_150572_a(rand, p_150572_2_, p_150572_3_, p_150572_4_);
	}

	@Override
	public void decorate(World par1World, Random par2Random, int par3, int par4) {
		baseBiome.decorate(par1World, par2Random, par3, par4);
	}

	@Override
	public void genTerrainBlocks(World p_150573_1_, Random p_150573_2_, Block[] p_150573_3_, byte[] p_150573_4_, int p_150573_5_, int p_150573_6_, double p_150573_7_) {
		baseBiome.genTerrainBlocks(p_150573_1_, p_150573_2_, p_150573_3_, p_150573_4_, p_150573_5_, p_150573_6_, p_150573_7_);
	}

	@Override
	public void plantFlower(World world, Random rand, int x, int y, int z) {
		baseBiome.plantFlower(world, rand, x, y, z);
	}

	/**
	 * returns the chance a creature has to spawn.
	 */
	@Override
	public float getSpawningChance() {
		return baseBiome.getSpawningChance();
	}

	/**
	 * Returns the correspondent list of the EnumCreatureType informed.
	 */
	@Override
	public List getSpawnableList(EnumCreatureType par1EnumCreatureType) {
		return baseBiome.getSpawnableList(par1EnumCreatureType);
	}

	/**
	 * Returns true if the biome have snowfall instead a normal rain.
	 */
	@Override
	public boolean getEnableSnow() {
		if (this.provider == null) { return this.baseBiome.getEnableSnow(); }
		return provider.getEnableSnow(baseBiome.getEnableSnow(), this.biomeID);
	}

	@Override
	public boolean func_150559_j() {
		return baseBiome.func_150559_j();
	}

	/**
	 * Return true if the biome supports lightning bolt spawn, either by have the bolts enabled and have rain enabled.
	 */
	@Override
	public boolean canSpawnLightningBolt() {
		if (this.provider == null) { return this.baseBiome.canSpawnLightningBolt(); }
		return this.getEnableSnow() ? false : provider.getEnableRain(baseBiome.canSpawnLightningBolt(), this.biomeID);
	}

	/**
	 * Checks to see if the rainfall level of the biome is extremely high
	 */
	@Override
	public boolean isHighHumidity() {
		return super.isHighHumidity();
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * takes temperature, returns color
	 */
	public int getSkyColorByTemp(float par1) {
		return baseBiome.getSkyColorByTemp(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Provides the basic grass color based on the biome temperature and rainfall
	 */
	public int getBiomeGrassColor(int x, int y, int z) {
		if (this.provider == null) { return this.baseBiome.getBiomeGrassColor(x, y, z); }
		Color color = provider.getStaticColor(IStaticColorProvider.GRASS, this.baseBiome, x, y, z);
		if (color != null) return getModdedBiomeGrassColor(color.asInt());
		return this.baseBiome.getBiomeGrassColor(x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Provides the basic foliage color based on the biome temperature and rainfall
	 */
	public int getBiomeFoliageColor(int x, int y, int z) {
		if (this.provider == null) { return this.baseBiome.getBiomeFoliageColor(x, y, z); }
		Color color = provider.getStaticColor(IStaticColorProvider.FOLIAGE, this.baseBiome, x, y, z);
		if (color != null) return getModdedBiomeFoliageColor(color.asInt());
		return this.baseBiome.getBiomeFoliageColor(x, y, z);
	}

	@Override
	public int getWaterColorMultiplier() {
		return this.getWaterColorMultiplier(0, 0, 0); // FORGE: Actual coords?
	}

	public int getWaterColorMultiplier(int x, int y, int z) {
		if (this.provider == null) { return this.baseBiome.getWaterColorMultiplier(); }
		Color color = provider.getStaticColor(IStaticColorProvider.WATER, this.baseBiome, x, y, z);
		if (color != null) return getModdedBiomeWaterColor(color.asInt());
		return this.baseBiome.getWaterColorMultiplier();
	}

	public int getModdedBiomeWaterColor(int original) {
		BiomeEvent.GetWaterColor event = new BiomeEvent.GetWaterColor(this, original);
		MinecraftForge.EVENT_BUS.post(event);
		return event.newColor;
	}

	@Override
	public boolean isEqualTo(Biome p_150569_1_) {
		return baseBiome.isEqualTo(p_150569_1_);
	}

	@Override
	public Biome.TempCategory getTempCategory() {
		return baseBiome.getTempCategory();
	}
}
