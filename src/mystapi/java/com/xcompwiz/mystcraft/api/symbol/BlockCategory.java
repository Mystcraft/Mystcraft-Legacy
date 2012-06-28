package com.xcompwiz.mystcraft.api.symbol;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Defines the categories for the BlockDescriptors. These are used to define what cases a block is usable in when
 * generating. For example, only BlockDescriptors marked with TERRAIN will be used when a TERRAIN block is requested.
 * 
 * @author xcompwiz
 */
public final class BlockCategory {

	private static HashMap<String, BlockCategory>	categories	= new HashMap<String, BlockCategory>();

	/**
	 * Gets all of the generation categories
	 * 
	 * @return The set of all generation categories
	 */
	public static Collection<BlockCategory> getCategories() {
		return Collections.unmodifiableCollection(categories.values());
	}

	/** This is a special-case category.  All block modifiers satisfy this. */
	public static final BlockCategory	ANY			= new BlockCategory("BlockAny");
	/** Valid Solid blocks include sand (everything but fluids and air) */
	public static final BlockCategory	SOLID		= new BlockCategory("BlockSolid");
	/** Valid Fluid blocks are fluids (not solid or air) */
	public static final BlockCategory	FLUID		= new BlockCategory("BlockFluid");
	/** Valid Fluid blocks are fluids (not solid or air) */
	public static final BlockCategory	GAS			= new BlockCategory("BlockGas");
	/** Valid Terrain blocks may be used in terrain gen */
	public static final BlockCategory	TERRAIN		= new BlockCategory("BlockTerrain");
	/** Valid Structure blocks are solid and do not fall */
	public static final BlockCategory	STRUCTURE	= new BlockCategory("BlockStructure");
	/** Valid Organic blocks, for things like plant matter or flesh */
	public static final BlockCategory	ORGANIC		= new BlockCategory("BlockOrganic");
	/** Valid Crystal blocks may be used in crystalline formations */
	public static final BlockCategory	CRYSTAL		= new BlockCategory("BlockCrystal");
	/** Valid Sea blocks may be used in terrain gen as the ocean blocks */
	public static final BlockCategory	SEA			= new BlockCategory("BlockSea");

	private final String				name;

	/**
	 * Creates a new BlockCategory. Generally you shouldn't make more than one instance with a name, but it is supported
	 * for the sake of multiple add-ons wanting identical new block types.
	 * 
	 * @param name The name of the category. Used for comparisons and as the category's grammar token
	 */
	private BlockCategory(String name) {
		this.name = name;
		categories.put(name, this);
	}

	public static BlockCategory registerBlockCategory(String name) {
		if (categories.containsKey(name)) {
			return categories.get(name);
		}
		return new BlockCategory(name);
	}

	/**
	 * Returns the type name of the BlockCategory
	 * 
	 * @return the name provided to the BlockCategory
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the grammar token bound to a generation category
	 * 
	 * @return The grammar token for the category
	 */
	public String getGrammarBinding() {
		return this.name;
	}
}
