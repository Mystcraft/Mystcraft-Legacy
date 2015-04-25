package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Implement this to provide an effect through either an AgeSymbol or an InstabilityProvider
 * @author xcompwiz
 */
public interface IEnvironmentalEffect {

	/**
	 * This is called every tick on every loaded chunk. This means the execution time of the function must be very, very fast.
	 * @param worldObj The current world
	 * @param chunk A loaded chunk on which this is being applied
	 */
	public void tick(World worldObj, Chunk chunk);
}
