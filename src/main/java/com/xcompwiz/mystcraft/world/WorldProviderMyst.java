package com.xcompwiz.mystcraft.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.client.render.CloudRendererMyst;
import com.xcompwiz.mystcraft.client.render.WeatherRendererMyst;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.storage.WorldInfoMyst;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		dimensionId = dim;
		super.setDimension(dim);
	}

	@Override
	protected void registerWorldChunkManager() {
		agedata = AgeData.getAge(ageUID, worldObj.isRemote);
		biomeManager = new BiomeWrapperManager(this);
		controller = new AgeController(worldObj, agedata);
		skyrenderer = controller.getSkyRenderer();
		weatherrenderer = controller.getWeatherRenderer();
		cloudrenderer = controller.getCloudRenderer();
		worldChunkMgr = controller.getWorldChunkManager();
		setWorldInfo();
	}

	public AgeController getAgeController() {
		return this.controller;
	}

	public void setWorldInfo() {
		ObfuscationReflectionHelper.setPrivateValue(World.class, worldObj, new WorldInfoMyst(this, worldObj.getWorldInfo()), "worldInfo", "field" + "_72986_A");
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
	public IChunkProvider createChunkGenerator() {
		return new ChunkProviderMyst(getAgeController(), worldObj, agedata);
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

	@Override
	public double getVoidFogYFactor() {
		return 1.0;
	}

	@Override
	public Vec3 getFogColor(float celestial_angle, float time) {
		Vec3 fog = getAgeController().getFogColor(celestial_angle, time);
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
			return Vec3.createVectorHelper(f3, f4, f5);
		}
		return fog;
	}

	@Override
	public Vec3 drawClouds(float partialtick) {
		float celestial_angle = worldObj.getCelestialAngle(partialtick);
		float var3 = MathHelper.cos(celestial_angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

		if (var3 < 0.0F) {
			var3 = 0.0F;
		}

		if (var3 > 1.0F) {
			var3 = 1.0F;
		}

		Vec3 temp = getAgeController().getCloudColor(worldObj.getWorldTime(), celestial_angle);
		float var4;
		float var5;
		float var6;
		if (temp != null) {
			var4 = (float) temp.xCoord;
			var5 = (float) temp.yCoord;
			var6 = (float) temp.zCoord;
		} else {
			var4 = 1.0F;
			var5 = 1.0F;
			var6 = 1.0F;
		}
		float var7 = worldObj.getRainStrength(partialtick);
		float var8;
		float var9;

		if (var7 > 0.0F) {
			var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
			var9 = 1.0F - var7 * 0.95F;
			var4 = var4 * var9 + var8 * (1.0F - var9);
			var5 = var5 * var9 + var8 * (1.0F - var9);
			var6 = var6 * var9 + var8 * (1.0F - var9);
		}

		var4 *= var3 * 0.9F + 0.1F;
		var5 *= var3 * 0.9F + 0.1F;
		var6 *= var3 * 0.85F + 0.15F;
		var8 = worldObj.getWeightedThunderStrength(partialtick);

		if (var8 > 0.0F) {
			var9 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
			float var10 = 1.0F - var8 * 0.95F;
			var4 = var4 * var10 + var9 * (1.0F - var10);
			var5 = var5 * var10 + var9 * (1.0F - var10);
			var6 = var6 * var10 + var9 * (1.0F - var10);
		}

		return Vec3.createVectorHelper(var4, var5, var6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * returns true if this dimension is supposed to display void particles and pull in the far plane based on the
	 * user's Y offset.
	 */
	public boolean getWorldHasVoidParticles() {
		return false;
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
	public boolean canBlockFreeze(int x, int y, int z, boolean reqLand) {
		BiomeGenBase biome = this.getBiomeGenForCoords(x, z);
		float temp = biome.getFloatTemperature(x, y, z);
		temp = this.getAgeController().getTemperatureAtHeight(temp, y);

		if (temp > 0.15F) { return false; }
		if (y >= 0 && y < 256 && worldObj.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10) {
			Block block = worldObj.getBlock(x, y, z);

			if ((block == Blocks.water || block == Blocks.flowing_water) && worldObj.getBlockMetadata(x, y, z) == 0) {
				if (!reqLand) { return true; }

				boolean var8 = true;

				if (var8 && worldObj.getBlock(x - 1, y, z).getMaterial() != Material.water) {
					var8 = false;
				}

				if (var8 && worldObj.getBlock(x + 1, y, z).getMaterial() != Material.water) {
					var8 = false;
				}

				if (var8 && worldObj.getBlock(x, y, z - 1).getMaterial() != Material.water) {
					var8 = false;
				}

				if (var8 && worldObj.getBlock(x, y, z + 1).getMaterial() != Material.water) {
					var8 = false;
				}

				if (!var8) { return true; }
			}
		}

		return false;
	}

	@Override
	public boolean canSnowAt(int x, int y, int z, boolean checkLight) {
		BiomeGenBase biome = this.getBiomeGenForCoords(x, z);
		float temp = biome.getFloatTemperature(x, y, z);
		temp = this.getAgeController().getTemperatureAtHeight(temp, y);

		if (temp > 0.15F) { return false; }

		if (y >= 0 && y < 256 && worldObj.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10) {
			Block var7 = worldObj.getBlock(x, y, z);

			if ((var7 == null || var7.isAir(worldObj, x, y, z)) && Blocks.snow_layer.canPlaceBlockAt(worldObj, x, y, z)) { return true; }
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

		this.worldObj.prevRainingStrength = this.worldObj.rainingStrength;
		this.worldObj.rainingStrength = this.getAgeController().getWeatherController().getRainingStrength();
		this.worldObj.prevThunderingStrength = this.worldObj.thunderingStrength;
		this.worldObj.thunderingStrength = this.getAgeController().getWeatherController().getStormStrength();

		if (worldObj.isRemote || !(worldObj instanceof WorldServer)) return;
		WorldServer world = (WorldServer) this.worldObj;
		if (this.worldObj.playerEntities.isEmpty()) {
			if (emptyTicks == 10) world.theChunkProviderServer.unloadAllChunks();
			++emptyTicks;
		} else {
			emptyTicks = 0;
		}
		if (world.areAllPlayersAsleep()) {
			this.setWorldTime(this.getWorldTime() + timeToSunrise());
		}
		this.setWorldTime(this.getWorldTime() + 1L);
		if (this.agedata.needsResend() == true && world.getTotalWorldTime() % 20 == 0) {
			this.agedata.resent();
			for (Object player : this.worldObj.playerEntities) {
				NetworkUtils.sendAgeData(worldObj, (EntityPlayer) player, this.dimensionId);
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
	public ChunkCoordinates getSpawnPoint() {
		verifySpawn();
		return new ChunkCoordinates(agedata.getSpawn());
	}

	@Override
	public ChunkCoordinates getRandomizedSpawnPoint() {
		return getSpawnPoint();
	}

	@Override
	public ChunkCoordinates getEntrancePortalLocation() {
		verifySpawn();
		return new ChunkCoordinates(agedata.getSpawn());
	}

	private void verifySpawn() {
		if (agedata.getSpawn() != null) return;
		if (worldObj.isRemote) {
			agedata.setSpawn(new ChunkCoordinates(0, 0, 0));
			return;
		}
		worldObj.findingSpawnPoint = true;
		Random random = new Random(agedata.getSeed());
		ChunkPosition chunkposition = worldChunkMgr.findBiomePosition(0, 0, 256, worldChunkMgr.getBiomesToSpawnIn(), random);
		int x = 0;
		int y = getAgeController().getSeaLevel();
		int z = 0;
		if (chunkposition != null) {
			x = chunkposition.chunkPosX;
			z = chunkposition.chunkPosZ;
		} else {
			System.out.println("Still searching for a spawn point.");
		}
		for (int l = 0; l < 1000; ++l) {
			if (canCoordinateBeSpawn(x, z) || agedata.getSpawn() != null) {
				break;
			}
			x = random.nextInt(64) - random.nextInt(64);
			z = random.nextInt(64) - random.nextInt(64);
		}
		if (agedata.getSpawn() == null) {
			worldObj.getChunkFromBlockCoords(x, z);
			while (!worldObj.isAirBlock(x, y, z)) {
				++y;
			}
			agedata.setSpawn(new ChunkCoordinates(x, y, z));
		}
		worldObj.findingSpawnPoint = false;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		int y = worldObj.getTopSolidOrLiquidBlock(x, z);
		Block block = worldObj.getBlock(x, y, z);
		if (block == Blocks.bedrock) { return false; }
		if (worldObj.isAirBlock(x, y, z)) { return false; }
		return block.getMaterial().blocksMovement();
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return biomeManager.getWrapper(x, z);
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

	public Color getStaticColor(String string, BiomeGenBase biome, int x, int y, int z) {
		return getAgeController().getStaticColor(string, biome, x, y, z);
	}

	/**
	 * Calculates the color for the skybox
	 */
	@Override
	public Vec3 getSkyColor(Entity entity, float partialtick) {
		float celestial_angle = this.worldObj.getCelestialAngle(partialtick);
		float red = 0;
		float green = 0;
		float blue = 0;
		BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posZ));
		Vec3 out = getAgeController().getSkyColor(entity, biome, worldObj.getWorldTime(), celestial_angle);
		if (out == null) {
			float var4 = MathHelper.cos(celestial_angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

			if (var4 < 0.0F) {
				var4 = 0.0F;
			}

			if (var4 > 1.0F) {
				var4 = 1.0F;
			}

			float var8 = biome.getFloatTemperature(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ));
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
		float rainstrength = this.worldObj.getRainStrength(partialtick);
		float var14;
		float var15;

		if (rainstrength > 0.0F) {
			var14 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.6F;
			var15 = 1.0F - rainstrength * 0.75F;
			red = red * var15 + var14 * (1.0F - var15);
			green = green * var15 + var14 * (1.0F - var15);
			blue = blue * var15 + var14 * (1.0F - var15);
		}

		var14 = this.worldObj.getWeightedThunderStrength(partialtick);

		if (var14 > 0.0F) {
			var15 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.2F;
			float var16 = 1.0F - var14 * 0.75F;
			red = red * var16 + var15 * (1.0F - var16);
			green = green * var16 + var15 * (1.0F - var16);
			blue = blue * var16 + var15 * (1.0F - var16);
		}

		if (this.worldObj.lastLightningBolt > 0) {
			var15 = this.worldObj.lastLightningBolt - partialtick;

			if (var15 > 1.0F) {
				var15 = 1.0F;
			}

			var15 *= 0.45F;
			red = red * (1.0F - var15) + 0.8F * var15;
			green = green * (1.0F - var15) + 0.8F * var15;
			blue = blue * (1.0F - var15) + 1.0F * var15;
		}

		return Vec3.createVectorHelper(red, green, blue);
	}

	@Override
	public String getSaveFolder() {
		return "DIM_MYST" + ageUID;
	}

	@Override
	public String getWelcomeMessage() {
		return "Traveling to " + getDimensionName();
	}

	@Override
	public String getDepartMessage() {
		return "Traveling from " + getDimensionName();
	}

	@Override
	public String getDimensionName() {
		if (agedata == null) {
			if (worldObj != null) {
				agedata = AgeData.getAge(ageUID, worldObj.isRemote);
			} else {
				LoggerUtils.warn("Someone is trying to get the age name from an improperly constructed world provider with dim id " + ageUID);
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
}
