package com.xcompwiz.mystcraft.api;

import net.minecraftforge.common.AchievementPage;

/**
 * This class provides a bunch of constant values which are the identifiers for various things. For things like Blocks
 * and Items you'll use GameRegistry to get an instance. For things like the Achievements you'll need to look them up in
 * their respective locations.
 * 
 * @author XCompWiz
 * 
 */
public final class MystObjects {

	/** You'll need this to look up the blocks and items */
	public static final String	MystcraftModId				= "Mystcraft";

	/** The Creative Tab for items. This is set during Post-Init. */
	//public CreativeTabs			creativeTab;
	/** The Creative Tab for page items (incl. linking pages) This is set during Post-Init. */
	//public CreativeTabs			pageTab;

	/** For use with ChestGenHooks. Treasure is not built until post-init. */
	public static final String	MYST_TREASURE				= "mystcraftTreasure";

	/** For registering Mystcraft related achievements. See {@link AchievementPage}. */
	public static final String	ACHIEVEMENT_PAGE			= "Mystcraft";

	public static final String	block_portal				= "LinkPortal";
	public static final String	block_crystal				= "BlockCrystal";
	public static final String	block_crystal_receptacle	= "BlockBookReceptacle";
	public static final String	block_decay					= "BlockDecay";
	public static final String	block_bookstand				= "BlockBookstand";
	public static final String	block_book_lectern			= "BlockLectern";
	public static final String	block_writing_desk_block	= "WritingDesk";
	public static final String	block_bookbinder			= "BlockBookBinder";
	public static final String	block_inkmixer				= "BlockInkMixer";
	public static final String	block_star_fissure			= "BlockStarFissure";

	/** Remember that this is a debug block! */
	public static final String	block_link_modifer			= "BlockLinkModifier";

	public static final String	item_writing_desk			= "writingdesk";
	public static final String	item_page					= "page";
	public static final String	item_descriptive_book		= "agebook";
	public static final String	item_linkbook_unlinked		= "unlinkedbook";
	public static final String	item_linkbook				= "linkbook";
	public static final String	item_notebook				= "notebook";
	public static final String	item_inkvial				= "vial";

	public static final String	fluid_black_ink				= "myst.ink.black";
}
