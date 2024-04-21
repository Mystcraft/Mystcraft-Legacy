package com.xcompwiz.mystcraft.api;

import net.minecraftforge.common.AchievementPage;

/**
 * This class provides a bunch of constant values which are the identifiers for various things. For things like Blocks and Items you'll use GameRegistry to get
 * an instance. For things like the Achievements you'll need to look them up in their respective locations.
 * @author XCompWiz
 */
public final class MystObjects {

	/** You'll need this to look up the blocks and items */
	public static final String	MystcraftModId		= "Mystcraft";

	/** The Creative Tab for items. This is set during Post-Init. */
	//public CreativeTabs			creativeTab;
	/** The Creative Tab for page items (incl. linking pages) This is set during Post-Init. */
	//public CreativeTabs			pageTab;

	/** For use with ChestGenHooks. Treasure is not built until post-init. */
	public static final String	MYST_TREASURE		= "mystcraftTreasure";

	/** For registering Mystcraft related achievements. See {@link AchievementPage}. */
	public static final String	ACHIEVEMENT_PAGE	= "Mystcraft";

	public static final class Blocks {
		public static final String	portal				= "LinkPortal";
		public static final String	crystal				= "BlockCrystal";
		public static final String	crystal_receptacle	= "BlockBookReceptacle";
		public static final String	decay				= "BlockDecay";
		public static final String	bookstand			= "BlockBookstand";
		public static final String	book_lectern		= "BlockLectern";
		public static final String	writing_desk_block	= "WritingDesk";
		public static final String	bookbinder			= "BlockBookBinder";
		public static final String	inkmixer			= "BlockInkMixer";
		public static final String	star_fissure		= "BlockStarFissure";
		/** Remember that this is a debug block! */
		public static final String	link_modifer		= "BlockLinkModifier";

	}

	public static final class Items {
		public static final String	writing_desk		= "writingdesk";
		public static final String	page				= "page";
		public static final String	descriptive_book	= "agebook";
		public static final String	linkbook_unlinked	= "unlinkedbook";
		public static final String	linkbook			= "linkbook";
		public static final String	folder				= "folder";
		public static final String	booster				= "booster";
		public static final String	inkvial				= "vial";
		public static final String	portfolio			= "portfolio";
	}

	public static final class Fluids {
		public static final String	black_ink	= "myst.ink.black";
	}
}
