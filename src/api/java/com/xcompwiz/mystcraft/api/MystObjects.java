package com.xcompwiz.mystcraft.api;

/**
 * This class provides a bunch of constant values which are the identifiers for various things. For things like Blocks and Items you'll use GameRegistry to get
 * an instance. For things like the Achievements you'll need to look them up in their respective locations.
 * @author XCompWiz
 */
public final class MystObjects {

	/** This is set during Mystcraft's Pre-Init. Use it to get an API provider instance. */
	public static APIInstanceProvider.EntryPoint entryPoint;

	/** You'll need this to look up the blocks and items */
	public static final String MystcraftModId = "mystcraft";

	/** The Creative Tab for items. This is set during Post-Init. */
	//public CreativeTabs creativeTab; //TODO: missing API object
	/** The Creative Tab for page items (incl. linking pages) This is set during Post-Init. */
	//public CreativeTabs pageTab; //TODO: missing API object 

	/** For use with ChestGenHooks. Treasure is not built until post-init. */
	public static final String MYST_TREASURE = "mystcraftTreasure"; //FIXME Hellfire> might be most likely subject for removal...

	public static final class Blocks {
		public static final String portal = "linkportal";
		public static final String crystal = "blockcrystal";
		public static final String crystal_receptacle = "blockbookreceptacle";
		public static final String decay = "blockdecay";
		public static final String bookstand = "blockbookstand";
		public static final String book_lectern = "blocklectern";
		public static final String writing_desk_block = "writingdesk";
		public static final String bookbinder = "blockbookbinder";
		public static final String inkmixer = "blockinkmixer";
		public static final String star_fissure = "blockstarfissure";
		/** Remember that this is a debug block! */
		public static final String link_modifer = "blocklinkmodifier";

		public static final String fluidblock_black_ink = "fluidblockblackink";

	}

	public static final class Items {
		public static final String writing_desk = "writingdesk";
		public static final String page = "page";
		public static final String descriptive_book = "agebook";
		public static final String linkbook_unlinked = "unlinkedbook";
		public static final String linkbook = "linkbook";
		public static final String folder = "folder";
		public static final String booster = "booster";
		public static final String inkvial = "vial";
		public static final String portfolio = "portfolio";
	}

	public static final class Fluids {
		public static final String black_ink = "myst.ink.black";
	}
}
