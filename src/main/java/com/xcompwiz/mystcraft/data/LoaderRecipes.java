package com.xcompwiz.mystcraft.data;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.xcompwiz.mystcraft.block.BlockBookBinder;
import com.xcompwiz.mystcraft.block.BlockBookReceptacle;
import com.xcompwiz.mystcraft.block.BlockBookstand;
import com.xcompwiz.mystcraft.block.BlockCrystal;
import com.xcompwiz.mystcraft.block.BlockInkMixer;
import com.xcompwiz.mystcraft.block.BlockLectern;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.item.ItemInkVial;
import com.xcompwiz.mystcraft.item.ItemNotebook;
import com.xcompwiz.mystcraft.item.ItemWritingDesk;

public class LoaderRecipes {

	private static boolean	Linkbook;
	private static boolean	Notebook;
	private static boolean	BookReceptacle;
	private static boolean	Lectern;
	private static boolean	Bookstand;
	private static boolean	WritingDesk;
	private static boolean	WritingDeskBack;
	private static boolean	BookBinder;
	private static boolean	InkMixer;

	public static void loadConfigs(MystConfig config) {
		Linkbook = config.get(MystConfig.CATEGORY_GENERAL, "options.linkbook.enabled", true).getBoolean(true);
		Notebook = config.get(MystConfig.CATEGORY_GENERAL, "options.notebook.enabled", true).getBoolean(true);
		BookReceptacle = config.get(MystConfig.CATEGORY_GENERAL, "options.receptacle.enabled", true).getBoolean(true);
		Lectern = config.get(MystConfig.CATEGORY_GENERAL, "options.lectern.enabled", true).getBoolean(true);
		Bookstand = config.get(MystConfig.CATEGORY_GENERAL, "options.bookstand.enabled", true).getBoolean(true);
		WritingDesk = config.get(MystConfig.CATEGORY_GENERAL, "options.desk.enabled", true).getBoolean(true);
		WritingDeskBack = config.get(MystConfig.CATEGORY_GENERAL, "options.deskback.enabled", true).getBoolean(true);
		BookBinder = config.get(MystConfig.CATEGORY_GENERAL, "options.binder.enabled", true).getBoolean(true);
		InkMixer = config.get(MystConfig.CATEGORY_GENERAL, "options.inkmixer.enabled", true).getBoolean(true);
	}

	public static void addRecipes(CraftingManager craftingmanager) {
		// Ink
		{
			IRecipe recipe = new ShapelessOreRecipe(new ItemStack(ItemInkVial.instance, 1), new Object[] { "dyeBlack", "dyeBlack", Items.potionitem });
			craftingmanager.getRecipeList().add(recipe);
			recipe = new ShapelessOreRecipe(new ItemStack(ItemInkVial.instance, 1), new Object[] { "dyeBlack", "dyeBlack", Items.glass_bottle, Items.water_bucket });
			craftingmanager.getRecipeList().add(recipe);
		}
		// Linking Book
		if (Linkbook) {
			craftingmanager.getRecipeList().add(new RecipeLinkingbook());
			RecipeSorter.register("myst.linkbook", RecipeLinkingbook.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		}
		// Notebook
		if (Notebook) {
			IRecipe recipe = new ShapedOreRecipe(new ItemStack(ItemNotebook.instance, 1), new Object[] { "# #", " # ", '#', Items.leather });
			craftingmanager.getRecipeList().add(recipe);
		}
		// Crystal Receptacle
		if (BookReceptacle) {
			craftingmanager.addRecipe(new ItemStack(BlockBookReceptacle.instance, 1), new Object[] { "###", "# #", "###", '#', new ItemStack(BlockCrystal.instance, 1, 0) });
		}
		// Lectern
		if (Lectern) {
			IRecipe recipe = new ShapedOreRecipe(new ItemStack(BlockLectern.instance, 2), new Object[] { "#  ", "##S", "###", 'S', "stickWood", '#', "plankWood" });
			craftingmanager.getRecipeList().add(recipe);
		}
		// Bookstand
		if (Bookstand) {
			IRecipe recipe = new ShapedOreRecipe(new ItemStack(BlockBookstand.instance, 1), new Object[] { "S S", " # ", 'S', "stickWood", '#', "plankWood" });
			craftingmanager.getRecipeList().add(recipe);
		}
		// Ink Mixer
		if (InkMixer) {
			IRecipe recipe;
			recipe = new ShapedOreRecipe(new ItemStack(BlockInkMixer.instance, 1), new Object[] { "S S", "SVS", "WSW", 'S', Blocks.stone, 'V', Items.glass_bottle, 'W', "plankWood" });
			craftingmanager.getRecipeList().add(recipe);
		}
		// Writing Desk
		if (WritingDesk) {
			IRecipe recipe;
			recipe = new ShapedOreRecipe(new ItemStack(ItemWritingDesk.instance, 1, 0), new Object[] { "I F", "###", "# #", 'I', Items.glass_bottle, 'F', Items.feather, '#', "plankWood" });
			craftingmanager.getRecipeList().add(recipe);
		}
		// Writing Desk Backboard
		if (WritingDeskBack) {
			IRecipe recipe;
			recipe = new ShapedOreRecipe(new ItemStack(ItemWritingDesk.instance, 1, 1), new Object[] { "###", "#I#", 'I', Items.item_frame, '#', "plankWood" });
			craftingmanager.getRecipeList().add(recipe);
		}
		// Book Binder
		if (BookBinder) {
			IRecipe recipe;
			recipe = new ShapedOreRecipe(new ItemStack(BlockBookBinder.instance, 1), new Object[] { "III", "###", "# #", 'I', Items.iron_ingot, '#', "plankWood" });
			craftingmanager.getRecipeList().add(recipe);
		}
	}
}
