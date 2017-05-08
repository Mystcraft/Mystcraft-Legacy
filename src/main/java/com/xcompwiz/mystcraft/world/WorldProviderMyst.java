package com.xcompwiz.mystcraft.world;

import java.util.Random;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.client.render.CloudRendererMyst;
import com.xcompwiz.mystcraft.client.render.WeatherRendererMyst;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.storage.WorldInfoMyst;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderMyst extends WorldProvider {

	public int					ageUID;
	public AgeData				agedata;
	private AgeController		controller;
	private BiomeWrapperManager	biomeManager;
	private SkyRendererMyst		skyrenderer;
	private CloudRendererMyst	cloudrenderer;
	private WeatherRendererMyst	weatherrenderer;

	private int					emptyTicks;

	public WorldProviderMyst() {
		super();
	}

	/**
	 * Sets the providers current dimension ID, used in default getSaveFolder() Added to allow default providers to be registered for multiple dimensions.
	 * @param dim Dimension ID
	 */
	@Override
	public void setDimension(int dim) {
		ageUID = dim;
		super.setDimension(dim);
	}

	@Override
	protected void init() {
		//XXX: terrainType = WorldType.DEFAULT;
		agedata = AgeData.getAge(ageUID, world.isRemote);
		biomeManager = new BiomeWrapperManager(this);
		controller = new AgeController(world, agedata);
		skyrenderer = controller.getSkyRenderer();
		weatherrenderer = controller.getWeatherRenderer();
		cloudrenderer = controller.getCloudRenderer();
		biomeProvider = controller.getWorldChunkManager();
		setWorldInfo();
	}

	public AgeController getAgeController() {
		return this.controller;
	}

	public void setWorldInfo() {
		if (world.getWorldInfo() instanceof WorldInfoMyst) return;
		ObfuscationReflectionHelper.setPrivateValue(World.class, world, new WorldInfoMyst(this, world.getWorldInfo()), "worldInfo", "field" + "_72986_A");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
		return skyrenderer;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getCloudRenderer() {
		return cloudrenderer;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getWeatherRenderer() {
		return weatherrenderer;
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderMyst(getAgeController(), world, agedata);
	}

	@Override
	protected void generateLightBrightnessTable() {
		getAgeController().generateLightBrightnessTable(lightBrightnessTable);
	}

	@Override
	public float[] calcSunriseSunsetColors(float f, float f1) {
		return null; // getAgeController().getHorizonColor(f, f1);
	}

	/**
	 * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime) par1 = worldTime par3 = partialtick
	 */
	@Override
	public float calculateCelestialAngle(long time, float partialtick) {
		return getAgeController().calculateCelestialAngle(time, partialtick);
	}

    @SideOnly(Side.CLIENT)
	@Override
	public Vec3d getFogColor(float celestial_angle, float partialtick) {
		//XXX: Is this safe enough?  Should I do something more robust?
		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		Biome biome = this.world.getBiome(new BlockPos(entity));
		Vec3d fog = getAgeController().getFogColor(entity, biome, world.getWorldTime(), celestial_angle, partialtick);
		if (fog == null) {
			float f2 = MathHelper.cos(celestial_angle * 3.141593F * 2.0F) * 2.0F + 0.5F;
			if (f2 < 0.0F) {
				f2 = 0.0F;
			}
			if (f2 > 1.0F) {
				f2 = 1.0F;
			}
			float f3 = 0.7529412F;
			float f4 = 0.8470588F;
			float f5 = 1.0F;
			f3 *= f2 * 0.94F + 0.06F;
			f4 *= f2 * 0.94F + 0.06F;
			f5 *= f2 * 0.91F + 0.09F;
			return new Vec3d(f3, f4, f5);
		}
		return fog;
	}

    @SideOnly(Side.CLIENT)
	@Override
	public Vec3d getCloudColor(float partialtick) {
		float celestial_angle = world.getCelestialAngle(partialtick);
		float var3 = MathHelper.cos(celestial_angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

		if (var3 < 0.0F) {
			var3 = 0.0F;
		}

		if (var3 > 1.0F) {
			var3 = 1.0F;
		}

		//XXX: Is this safe enough?  Should I do something more robust?
		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		Biome biome = this.world.getBiome(new BlockPos(entity));
		Vec3d temp = getAgeController().getCloudColor(entity, biome, world.getWorldTime(), celestial_angle, partialtick);
		float red;
		float grn;
		float blu;
		if (temp != null) {
			red = (float) temp.xCoord;
			grn = (float) temp.yCoord;
			blu = (float) temp.zCoord;
		} else {
			red = 1.0F;
			grn = 1.0F;
			blu = 1.0F;
		}
		float var7 = world.getRainStrength(partialtick);
		float var8;
		float var9;

		if (var7 > 0.0F) {
			var8 = (red * 0.3F + grn * 0.59F + blu * 0.11F) * 0.6F;
			var9 = 1.0F - var7 * 0.95F;
			red = red * var9 + var8 * (1.0F - var9);
			grn = grn * var9 + var8 * (1.0F - var9);
			blu = blu * var9 + var8 * (1.0F - var9);
		}

		red *= var3 * 0.9F + 0.1F;
		grn *= var3 * 0.9F + 0.1F;
		blu *= var3 * 0.85F + 0.15F;
		var8 = world.getThunderStrength(partialtick);

		if (var8 > 0.0F) {
			var9 = (red * 0.3F + grn * 0.59F + blu * 0.11F) * 0.2F;
			float var10 = 1.0F - var8 * 0.95F;
			red = red * var10 + var9 * (1.0F - var10);
			grn = grn * var10 + var9 * (1.0F - var10);
			blu = blu * var10 + var9 * (1.0F - var10);
		}

		return new Vec3d(red, grn, blu);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getVoidFogYFactor() {
		return 0;
	}

	@Override
	public boolean isSkyColored() {
		return false;
	}

	@Override
	public float getCloudHeight() {
		return getAgeController().getCloudHeight();
	}

	@Override
	public double getHorizon() {
		return getAgeController().getHorizon();
	}

	@Override
	public int getAverageGroundLevel() {
		return 64;
	}

	@Override
	public boolean canBlockFreeze(BlockPos pos, boolean reqLand) {
		Biome biome = this.getBiomeForCoords(pos);
		float temp = biome.getFloatTemperature(pos);
		int y = pos.getY();
		temp = this.getAgeController().getTemperatureAtHeight(temp, y);

		if (temp > 0.15F) { return false; }
		if (y >= 0 && y < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
			IBlockState blockstate = world.getBlockState(pos);

			if ((blockstate.getBlock() == Blocks.WATER || blockstate.getBlock() == Blocks.FLOWING_WATER) && ((Integer)blockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0) {
				if (!reqLand) { return true; }

				boolean canFreeze = true;

				if (canFreeze && world.getBlockState(pos.west()).getMaterial() != Material.WATER) {
					canFreeze = false;
				}

				if (canFreeze && world.getBlockState(pos.east()).getMaterial() != Material.WATER) {
					canFreeze = false;
				}

				if (canFreeze && world.getBlockState(pos.south()).getMaterial() != Material.WATER) {
					canFreeze = false;
				}

				if (canFreeze && world.getBlockState(pos.north()).getMaterial() != Material.WATER) {
					canFreeze = false;
				}

				return canFreeze;
			}
		}

		return false;
	}

	@Override
	public boolean canSnowAt(BlockPos pos, boolean checkLight) {
		Biome biome = this.getBiomeForCoords(pos);
		float temp = biome.getFloatTemperature(pos);
		int y = pos.getY();
		temp = this.getAgeController().getTemperatureAtHeight(temp, y);

		if (temp > 0.15F) { return false; }

		if (y >= 0 && y < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
			IBlockState blockstate = world.getBlockState(pos);

			if ((blockstate == null || blockstate.getBlock().isAir(blockstate, world, pos)) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos)) { return true; }
		}
		return false;
	}

	/**
	 * Called from World constructor to set rainingStrength and thunderingStrength
	 */
	@Override
	public void calculateInitialWeather() {
		updateWeather();
	}

	@Override
	public boolean canDoLightning(Chunk chunk) {
		getAgeController().tickBlocksAndAmbiance(chunk);
		return false;
	}

	@Override
	/**
	 * Called at the beginning of the World update tick
	 */
	public void updateWeather() {
		getAgeController().tick();
		getAgeController().getWeatherController().updateRaining();
		weatherrenderer.updateClouds();
		cloudrenderer.updateClouds();

		this.world.prevRainingStrength = this.world.rainingStrength;
		this.world.rainingStrength = this.getAgeController().getWeatherController().getRainingStrength();
		this.world.prevThunderingStrength = this.world.thunderingStrength;
		this.world.thunderingStrength = this.getAgeController().getWeatherController().getStormStrength();

		if (world.isRemote || !(world instanceof WorldServer)) return;
		WorldServer world = (WorldServer) this.world;
		if (this.world.playerEntities.isEmpty()) {
			if (emptyTicks == 10) world.getChunkProvider().unloadAllChunks();
			++emptyTicks;
		} else {
			emptyTicks = 0;
		}
		if (world.areAllPlayersAsleep()) {
			this.setWorldTime(this.getWorldTime() + timeToSunrise());
		}
		this.setWorldTime(this.getWorldTime() + 1L);
		if (this.agedata.needsResend() == true && world.getTotalWorldTime() % 200 == 0) {
			this.agedata.resent();
			for (Object player : this.world.playerEntities) {
				NetworkUtils.sendAgeData((EntityPlayer) player, this.getDimension());
			}
		}
	}

	private long timeToSunrise() {
		return getAgeController().getTimeToSunrise(this.getWorldTime());
	}

	@Override
	public void resetRainAndThunder() {
		this.agedata.markNeedsResend();
		this.getAgeController().getWeatherController().reset();
	}

	public void togglePrecipitation() {
		this.agedata.markNeedsResend();
		this.getAgeController().getWeatherController().togglePrecipitation();
	}

	@Override
	public BlockPos getSpawnPoint() {
		verifySpawn();
		return agedata.getSpawn();
	}

	@Override
	public BlockPos getRandomizedSpawnPoint() {
		return getSpawnPoint();
	}

	private void verifySpawn() {
		if (agedata.getSpawn() != null) return;
		if (world.isRemote) {
			agedata.setSpawn(new BlockPos(0, 0, 0));
			return;
		}
		//XXX: world.findingSpawnPoint = true;
		Random random = new Random(agedata.getSeed());
		BiomeProvider biomeprovider = this.getBiomeProvider();
		BlockPos spawnpos = biomeprovider.findBiomePosition(0, 0, 256, biomeprovider.getBiomesToSpawnIn(), random);
		if (spawnpos == null) {
			System.out.println("Searching for viable spawn point.");
		}
		int x = 0;
		int y = getAgeController().getSeaLevel();
		int z = 0;
		for (int l = 0; l < 1000; ++l) {
			if (canCoordinateBeSpawn(x, z) || agedata.getSpawn() != null) {
				break;
			}
			x = random.nextInt(64) - random.nextInt(64);
			z = random.nextInt(64) - random.nextInt(64);
		}
		if (agedata.getSpawn() == null) {
			spawnpos = new BlockPos(x, y, z);
			world.getChunkFromBlockCoords(spawnpos);
			while (!world.isAirBlock(spawnpos)) {
				spawnpos = spawnpos.up();
			}
			agedata.setSpawn(spawnpos);
		}
		//XXX: world.findingSpawnPoint = false;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		BlockPos blockpos = new BlockPos(x, 0, z);
		blockpos = world.getTopSolidOrLiquidBlock(blockpos);
		IBlockState block = world.getBlockState(blockpos);
		if (block.getBlock() == Blocks.BEDROCK) { return false; }
		if (world.isAirBlock(blockpos)) { return false; }
		return block.getMaterial().blocksMovement();
	}

	@Override
	public Biome getBiomeForCoords(BlockPos pos) {
		return biomeManager.getWrapper(pos.getX(), pos.getZ());
	}

	private int getSkyColorByTemp(float par1) {
		par1 /= 3.0F;

		if (par1 < -1.0F) {
			par1 = -1.0F;
		}

		if (par1 > 1.0F) {
			par1 = 1.0F;
		}

		return java.awt.Color.getHSBColor(0.62222224F - par1 * 0.05F, 0.5F + par1 * 0.1F, 1.0F).getRGB();
	}

	public Color getStaticColor(String string, Biome biome, int x, int y, int z) {
		return getAgeController().getStaticColor(string, biome, x, y, z);
	}

	/**
	 * Calculates the color for the skybox
	 */
	@Override
	public Vec3d getSkyColor(Entity entity, float partialtick) {
		float celestial_angle = this.world.getCelestialAngle(partialtick);
		float red = 0;
		float green = 0;
		float blue = 0;
		BlockPos blockPos = new BlockPos(entity);
		Biome biome = this.world.getBiome(blockPos);
		Vec3d out = getAgeController().getSkyColor(entity, biome, world.getWorldTime(), celestial_angle, partialtick);
		if (out == null) {
			float var4 = MathHelper.cos(celestial_angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

			if (var4 < 0.0F) {
				var4 = 0.0F;
			}

			if (var4 > 1.0F) {
				var4 = 1.0F;
			}

			float var8 = biome.getFloatTemperature(blockPos);
			int var9 = getSkyColorByTemp(var8);
			red = (var9 >> 16 & 255) / 255.0F;
			green = (var9 >> 8 & 255) / 255.0F;
			blue = (var9 & 255) / 255.0F;
			red *= var4;
			green *= var4;
			blue *= var4;
		} else {
			red = (float) out.xCoord;
			green = (float) out.yCoord;
			blue = (float) out.zCoord;
		}
		float rainstrength = this.world.getRainStrength(partialtick);
		float var14;
		float var15;

		if (rainstrength > 0.0F) {
			var14 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.6F;
			var15 = 1.0F - rainstrength * 0.75F;
			red = red * var15 + var14 * (1.0F - var15);
			green = green * var15 + var14 * (1.0F - var15);
			blue = blue * var15 + var14 * (1.0F - var15);
		}

		var14 = this.world.getThunderStrength(partialtick);

		if (var14 > 0.0F) {
			var15 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.2F;
			float var16 = 1.0F - var14 * 0.75F;
			red = red * var16 + var15 * (1.0F - var16);
			green = green * var16 + var15 * (1.0F - var16);
			blue = blue * var16 + var15 * (1.0F - var16);
		}

		if (this.world.getLastLightningBolt() > 0) {
			var15 = this.world.getLastLightningBolt() - partialtick;

			if (var15 > 1.0F) {
				var15 = 1.0F;
			}

			var15 *= 0.45F;
			red = red * (1.0F - var15) + 0.8F * var15;
			green = green * (1.0F - var15) + 0.8F * var15;
			blue = blue * (1.0F - var15) + 1.0F * var15;
		}

		return new Vec3d(red, green, blue);
	}

	@Override
	public String getSaveFolder() {
		return getSaveFolderName(ageUID);
	}

	public static String getSaveFolderName(int dimid) {
		return "DIM_MYST" + dimid;
	}

	@Override
	public String getWelcomeMessage() {
		return "Traveling to " + getDimensionName();
	}

	@Override
	public String getDepartMessage() {
		return "Traveling from " + getDimensionName();
	}

	public String getDimensionName() {
		if (agedata == null) {
			if (world != null) {
				agedata = AgeData.getAge(ageUID, world.isRemote);
			} else {
				LoggerUtils.warn("Someone is trying to get the age name from an improperly constructed world provider with dim id " + getDimension());
				agedata = AgeData.getAge(ageUID, false);
			}
		}
		if (agedata == null) return "Age " + ageUID;
		return agedata.getAgeName();
	}

	@Override
	public long getWorldTime() {
		return agedata.getWorldTime();
	}

	@Override
	public void setWorldTime(long time) {
		agedata.setWorldTime(time);
	}

	@Override
	public boolean canRespawnHere() {
		return Mystcraft.respawnInAges;
	}

	public boolean isPvPEnabled() {
		return getAgeController().isPvPEnabled();
	}

	public float getTemperature(float temperature, int biomeId) {
		return getAgeController().getWeatherController().getTemperature(temperature, biomeId);
	}

	public float getRainfall(float rainfall, int biomeId) {
		return getAgeController().getWeatherController().getRainfall(rainfall, biomeId);
	}

	public boolean getEnableSnow(boolean enableSnow, int biomeId) {
		return getAgeController().getWeatherController().getEnableSnow(enableSnow, biomeId);
	}

	public boolean getEnableRain(boolean canSpawnLightningBolt, int biomeId) {
		return getAgeController().getWeatherController().getEnableRain(canSpawnLightningBolt, biomeId);
	}

	@Override
	public DimensionType getDimensionType() {
		return Mystcraft.dimensionType;
	}
}
