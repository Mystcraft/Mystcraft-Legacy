package com.xcompwiz.mystcraft.world.storage;

import java.util.Map;

import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.WorldInfo;

public class WorldInfoMyst extends DerivedWorldInfo {

	private final WorldProviderMyst	provider;

	private long					tickcounter;

	public WorldInfoMyst(WorldProviderMyst provider, WorldInfo worldInfo) {
		super(worldInfo);
		this.provider = provider;
		this.tickcounter = 0;
		if (worldInfo instanceof WorldInfoMyst) {
			throw new RuntimeException("Attempting to create a WorldInfoMyst instance pointed at a WorldInfoMyst instance");
		}
	}

	/**
	 * This would be better named _SET_TotalWorldTime. Actually not meant to increment.
	 */
	@Override
	public void incrementTotalWorldTime(long par1) {
		if (provider.worldObj.isRemote) tickcounter = par1;
	}

	@Override
	public long getWorldTotalTime() {
		if (provider.worldObj.isRemote) return tickcounter;
		return super.getWorldTotalTime();
	}

	/**
	 * Get current world time
	 */
	@Override
	public long getWorldTime() {
		return this.provider.getWorldTime();
	}

	/**
	 * Returns vanilla MC dimension (-1,0,1). For custom dimension compatibility, always prefer WorldProvider.dimensionID accessed from
	 * World.provider.dimensionID
	 */
	@Override
	public int getVanillaDimension() {
		return this.provider.dimensionId;
	}

	/**
	 * Sets the spawn zone position. Args: x, y, z
	 */
	@Override
	public void setSpawnPosition(int x, int y, int z) {
		provider.agedata.setSpawn(new ChunkPos(x, y, z));
	}

	/**
	 * Returns the x spawn position
	 */
	@Override
	public int getSpawnX() {
		ChunkPos spawn = this.provider.agedata.getSpawn();
		if (spawn == null) return 0;
		return spawn.posX;
	}

	/**
	 * Return the Y axis spawning point of the player.
	 */
	@Override
	public int getSpawnY() {
		ChunkPos spawn = this.provider.agedata.getSpawn();
		if (spawn == null) return 64;
		return spawn.posY;
	}

	/**
	 * Returns the z spawn position
	 */
	@Override
	public int getSpawnZ() {
		ChunkPos spawn = this.provider.agedata.getSpawn();
		if (spawn == null) return 0;
		return spawn.posZ;
	}

	/**
	 * Returns the seed of current world.
	 */
	@Override
	public long getSeed() {
		if (this.provider.agedata == null) {
			LoggerUtils.warn("Attempting to get world seed before world completely initialized in dimension %d", this.provider.dimensionId);
			return super.getSeed();
		}
		return this.provider.agedata.getSeed();
	}

	/**
	 * Set current world time
	 */
	@Override
	public void setWorldTime(long par1) {
		//LoggerHelper.warn("Something is attempting to set the time in an age via WorldInfo.  Use the provider!");
		//provider.setWorldTime(par1);
	}

	/**
	 * Returns true if it is thundering, false otherwise.
	 */
	@Override
	public boolean isThundering() {
		return this.provider.worldObj.isThundering();
	}

	/**
	 * Sets whether it is thundering or not.
	 */
	@Override
	public void setThundering(boolean par1) {
		//NOTE: We don't process thundering like this
	}

	/**
	 * Returns the number of ticks until next thunderbolt.
	 */
	@Override
	public int getThunderTime() {
		return 0;
	}

	/**
	 * Defines the number of ticks until next thunderbolt.
	 */
	@Override
	public void setThunderTime(int par1) {}

	/**
	 * Returns true if it is raining, false otherwise.
	 */
	@Override
	public boolean isRaining() {
		return this.provider.worldObj.isRaining();
	}

	/**
	 * Sets whether it is raining or not.
	 */
	@Override
	public void setRaining(boolean par1) {
		if (isRaining() != par1) {
			provider.togglePrecipitation();
		}
	}

	/**
	 * Return the number of ticks until rain.
	 */
	@Override
	public int getRainTime() {
		return 0;
	}

	/**
	 * Sets the number of ticks until rain.
	 */
	@Override
	public void setRainTime(int par1) {}

	/**
	 * Gets the GameType.
	 */
	@Override
	public WorldSettings.GameType getGameType() {
		return super.getGameType(); // Check this out for possibilities?
	}

	/**
	 * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
	 */
	@Override
	public boolean isMapFeaturesEnabled() {
		return true;
	}

	@Override
	public WorldType getTerrainType() {
		return super.getTerrainType(); // Return the "save's" terrain type here. The dim's terrain type is in the provider.
	}

	@Override
	public void setTerrainType(WorldType par1WorldType) {
		super.setTerrainType(par1WorldType);
	}

	/**
	 * Gets the GameRules class Instance.
	 */
	@Override
	public GameRules getGameRulesInstance() {
		return super.getGameRulesInstance(); //XXX: (WorldInfo) Are there possibilities here?
	}

	/**
	 * Allow access to additional mod specific world based properties Used by FML to store mod list associated with a world, and maybe an id map Used by Forge
	 * to store the dimensions available to a world
	 * @param additionalProperties
	 */
	@Override
	public void setAdditionalProperties(Map<String, NBTBase> additionalProperties) {
		super.setAdditionalProperties(additionalProperties);
	}

	@Override
	public NBTBase getAdditionalProperty(String additionalProperty) {
		return super.getAdditionalProperty(additionalProperty);
	}

}
