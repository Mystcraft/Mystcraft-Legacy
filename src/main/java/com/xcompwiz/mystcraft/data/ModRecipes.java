package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.config.MystConfig;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModRecipes {

	private static boolean	Linkbook;
	private static boolean	Folder;
	private static boolean	Portfolio;
	private static boolean	BookReceptacle;
	private static boolean	Lectern;
	private static boolean	Bookstand;
	private static boolean	WritingDesk;
	private static boolean	WritingDeskBack;
	private static boolean	BookBinder;
	private static boolean	InkMixer;

	public static void loadConfigs(MystConfig config) {
		Linkbook = config.get(MystConfig.CATEGORY_GENERAL, "crafting.linkbook.enabled", true).getBoolean(true);
		Folder = config.get(MystConfig.CATEGORY_GENERAL, "crafting.folder.enabled", true).getBoolean(true);
		Portfolio = config.get(MystConfig.CATEGORY_GENERAL, "crafting.portfolio.enabled", true).getBoolean(true);
		BookReceptacle = config.get(MystConfig.CATEGORY_GENERAL, "crafting.receptacle.enabled", true).getBoolean(true);
		Lectern = config.get(MystConfig.CATEGORY_GENERAL, "crafting.lectern.enabled", true).getBoolean(true);
		Bookstand = config.get(MystConfig.CATEGORY_GENERAL, "crafting.bookstand.enabled", true).getBoolean(true);
		WritingDesk = config.get(MystConfig.CATEGORY_GENERAL, "crafting.desk.enabled", true).getBoolean(true);
		WritingDeskBack = config.get(MystConfig.CATEGORY_GENERAL, "crafting.deskback.enabled", true).getBoolean(true);
		BookBinder = config.get(MystConfig.CATEGORY_GENERAL, "crafting.binder.enabled", true).getBoolean(true);
		InkMixer = config.get(MystConfig.CATEGORY_GENERAL, "crafting.inkmixer.enabled", true).getBoolean(true);
	}

	public static void addRecipes(CraftingManager craftingmanager) {
		// Ink
		IRecipe recipe = new ShapelessOreRecipe(new ItemStack(ModItems.inkvial), "dyeBlack", "dyeBlack", Items.POTIONITEM);
		craftingmanager.getRecipeList().add(recipe);
		recipe = new ShapelessOreRecipe(new ItemStack(ModItems.inkvial), "dyeBlack", "dyeBlack", Items.GLASS_BOTTLE, Items.WATER_BUCKET);
		craftingmanager.getRecipeList().add(recipe);
		// Linking Book
		if (Linkbook) {
			craftingmanager.getRecipeList().add(new RecipeLinkingbook());
			RecipeSorter.register("myst.linkbook", RecipeLinkingbook.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		}
		// Folder
		if (Folder) {
			recipe = new ShapedOreRecipe(new ItemStack(ModItems.folder), " # ", "S  ", " # ", '#', Items.LEATHER, 'S', Items.STRING);
			craftingmanager.getRecipeList().add(recipe);
		}
		// Portfolio
		if (Portfolio) {
			recipe = new ShapedOreRecipe(new ItemStack(ModItems.portfolio), "###", "S  ", "###", '#', Items.LEATHER, 'S', Items.STRING);
			craftingmanager.getRecipeList().add(recipe);
		}
		// Crystal Receptacle
		if (BookReceptacle) {
			craftingmanager.addRecipe(new ItemStack(ModBlocks.receptacle), "###", "# #", "###", '#', new ItemStack(ModBlocks.crystal));
		}
		// Lectern
		if (Lectern) {
			recipe = new ShapedOreRecipe(new ItemStack(ModBlocks.lectern, 2), "#  ", "##S", "###", 'S', "stickWood", '#', "plankWood");
			craftingmanager.getRecipeList().add(recipe);
		}
		// Bookstand
		if (Bookstand) {
			recipe = new ShapedOreRecipe(new ItemStack(ModBlocks.bookstand), "S S", " # ", 'S', "stickWood", '#', "plankWood");
			craftingmanager.getRecipeList().add(recipe);
		}
		// Ink Mixer
		if (InkMixer) {
			recipe = new ShapedOreRecipe(new ItemStack(ModBlocks.inkmixer), "S S", "SVS", "WSW", 'S', Blocks.STONE, 'V', Items.GLASS_BOTTLE, 'W', "plankWood");
			craftingmanager.getRecipeList().add(recipe);
		}
		// Writing Desk
		if (WritingDesk) {
			recipe = new ShapedOreRecipe(new ItemStack(ModItems.desk), "I F", "###", "# #", 'I', Items.GLASS_BOTTLE, 'F', Items.FEATHER, '#', "plankWood");
			craftingmanager.getRecipeList().add(recipe);
		}
		// Writing Desk Backboard
		if (WritingDeskBack) {
			recipe = new ShapedOreRecipe(new ItemStack(ModItems.desk, 1, 1), "###", "#I#", 'I', Items.ITEM_FRAME, '#', "plankWood");
			craftingmanager.getRecipeList().add(recipe);
		}
		// Book Binder
		if (BookBinder) {
			recipe = new ShapedOreRecipe(new ItemStack(ModBlocks.bookbinder), "III", "###", "# #", 'I', Items.IRON_INGOT, '#', "plankWood");
			craftingmanager.getRecipeList().add(recipe);
		}
	}
}
