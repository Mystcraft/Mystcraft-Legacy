package com.xcompwiz.mystcraft.api.world.logic;

import com.xcompwiz.mystcraft.api.util.Color;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Used to produce a color once. This color cannot change over time.
 * @author xcompwiz
 */
public interface IStaticColorProvider {

	public final static String FOLIAGE = "foliage";
	public final static String GRASS = "grass";
	public final static String WATER = "water";

	/**
	 * Returns a color based on the provided information. Note that the produced color is fixed, so this type of element should not use gradients. Returning
	 * null has no effect.
	 * @param worldObj The world object
	 * @param biome The current biome. Can be null.
	 * @param pos The position of the block we are coloring
	 * @return A Mystcraft Color object or null
	 */
	public abstract Color getStaticColor(World worldObj, Biome biome, BlockPos pos);
}
