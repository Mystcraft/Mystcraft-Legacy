package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.config.MystConfig;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModRecipes {

	private static boolean	InkVial;
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
		InkVial = config.get(MystConfig.CATEGORY_GENERAL, "crafting.inkvial.enabled", true).getBoolean(true);
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

	public static void addRecipes() {
		// Ink
		IRecipe recipe;
		if (InkVial) {
			recipe = new ShapelessOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shapeless/inkvial/bottle"), new ItemStack(ModItems.inkvial), "dyeBlack", "dyeBlack", Items.POTIONITEM);
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shapeless/inkvial/bottle"));
			ModRegistryPrimer.queueForRegistration(recipe);
			recipe = new ShapelessOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shapeless/inkvial/bucket"), new ItemStack(ModItems.inkvial), "dyeBlack", "dyeBlack", Items.GLASS_BOTTLE, Items.WATER_BUCKET);
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shapeless/inkvial/bucket"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Linking Book
		if (Linkbook) {
			ModRegistryPrimer.queueForRegistration(new RecipeLinkingbook());
		}
		// Folder
		if (Folder) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/folder"), new ItemStack(ModItems.folder), " # ", "S  ", " # ", '#', Items.LEATHER, 'S', Items.STRING);
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/folder"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Portfolio
		if (Portfolio) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/portfolio"), new ItemStack(ModItems.portfolio), "###", "S  ", "###", '#', Items.LEATHER, 'S', Items.STRING);
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/portfolio"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Crystal Receptacle
		if (BookReceptacle) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/receptacle"), new ItemStack(ModBlocks.getItemBlock(ModBlocks.receptacle)), "###", "# #", "###", '#', new ItemStack(ModBlocks.getItemBlock(ModBlocks.crystal)));
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/receptacle"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Lectern
		if (Lectern) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/lectern"), new ItemStack(ModBlocks.getItemBlock(ModBlocks.lectern), 2), "#  ", "##S", "###", 'S', "stickWood", '#', "plankWood");
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/lectern"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Bookstand
		if (Bookstand) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/bookstand"), new ItemStack(ModBlocks.getItemBlock(ModBlocks.bookstand)), "S S", " # ", 'S', "stickWood", '#', "plankWood");
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/bookstand"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Ink Mixer
		if (InkMixer) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/inkmixer"), new ItemStack(ModBlocks.getItemBlock(ModBlocks.inkmixer)), "S S", "SVS", "WSW", 'S', Blocks.STONE, 'V', Items.GLASS_BOTTLE, 'W', "plankWood");
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/inkmixer"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Writing Desk
		if (WritingDesk) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/writingdesk/base"), new ItemStack(ModItems.desk), "I F", "###", "# #", 'I', Items.GLASS_BOTTLE, 'F', Items.FEATHER, '#', "plankWood");
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/writingdesk/base"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Writing Desk Backboard
		if (WritingDeskBack) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/writingdesk/back"), new ItemStack(ModItems.desk, 1, 1), "###", "#I#", 'I', Items.ITEM_FRAME, '#', "plankWood");
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/writingdesk/back"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
		// Book Binder
		if (BookBinder) {
			recipe = new ShapedOreRecipe(new ResourceLocation(MystObjects.MystcraftModId, "shaped/bookbinder"), new ItemStack(ModBlocks.getItemBlock(ModBlocks.bookbinder)), "III", "###", "# #", 'I', Items.IRON_INGOT, '#', "plankWood");
			recipe.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "shaped/bookbinder"));
			ModRegistryPrimer.queueForRegistration(recipe);
		}
	}
}
