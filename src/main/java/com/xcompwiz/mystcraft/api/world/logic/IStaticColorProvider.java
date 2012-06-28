package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.symbol.Color;

/**
 * Used to produce a color once. This color cannot change over time.
 * 
 * @author xcompwiz
 */
public interface IStaticColorProvider {
	public final static String	WATER	= "Water";
	public final static String	GRASS	= "Grass";
	public final static String	FOLIAGE	= "Foliage";

	/**
	 * Returns a color based on the provided information Note that the produced color is fixed, so this type of element
	 * should not use gradients Can return null if the logic element does not provide a color for this case
	 * 
	 * @param type String identifier of the type of color
	 * @param worldObj The world object
	 * @param biome The current biome. Can be null
	 * @param x The x coordinate of the block we are coloring
	 * @param y The y coordinate of the block we are coloring
	 * @param z The z coordinate of the block we are coloring
	 * @return A Mystcraft Color object or null
	 */
	public abstract Color getStaticColor(String type, World worldObj, BiomeGenBase biome, int x, int y, int z);
}
