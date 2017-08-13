package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This is used for locating things like Strongholds
 * @author xcompwiz
 */
public interface ITerrainFeatureLocator {

	/**
	 * Returns the location of the nearest instance of the named location. Can return null if there aren't any instances or if the named location is
	 * unrecognized You only need one of these if you are adding a structure you want to be able to locate using an item, akin to how Strongholds may be found
	 * @param world The world object of the dimension
	 * @param identifier The name of the location or element to locate
	 * @param pos Block coordinates
	 * @param genChunks should generate chunks in order to properly locate this feature
	 * @return
	 */
	public abstract BlockPos locate(World world, String identifier, BlockPos pos, boolean genChunks);

	/**
	 * Returns if the specified position is inside one of the features described by this feature locator
	 * @param world The world of the dimension
	 * @param identifier Name of the element to locate
	 * @param pos Block coordinates
	 * @return If the position is inside the specified feature - or false if the locator doesn't support locating a feature
	 */
	default public boolean isInsideFeature(World world, String identifier, BlockPos pos) {
		return false;
	}
}
