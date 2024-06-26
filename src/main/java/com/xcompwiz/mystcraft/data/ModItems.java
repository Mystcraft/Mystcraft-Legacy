package com.xcompwiz.mystcraft.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemBoosterPack;
import com.xcompwiz.mystcraft.item.ItemFolder;
import com.xcompwiz.mystcraft.item.ItemInkVial;
import com.xcompwiz.mystcraft.item.ItemLinkbook;
import com.xcompwiz.mystcraft.item.ItemLinkbookUnlinked;
import com.xcompwiz.mystcraft.item.ItemMyGlasses;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.item.ItemPortfolio;
import com.xcompwiz.mystcraft.item.ItemWritingDesk;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

	public static Item page;
	public static ItemAgebook agebook;
	public static Item linkbook;
	public static Item unlinked;
	public static Item booster;
	public static Item folder;
	public static Item portfolio;
	public static Item desk;
	public static ItemInkVial inkvial;
	public static Item glasses = new ItemMyGlasses();

	public static void loadConfigs(MystConfig config) {}

	public static void init() {
		page = new ItemPage();
		agebook = new ItemAgebook();
		linkbook = new ItemLinkbook();
		unlinked = new ItemLinkbookUnlinked();
		booster = new ItemBoosterPack();
		folder = new ItemFolder();
		portfolio = new ItemPortfolio();
		desk = new ItemWritingDesk();
		inkvial = new ItemInkVial();

		page.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.page));
		agebook.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.descriptive_book));
		linkbook.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.linkbook));
		unlinked.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.linkbook_unlinked));
		booster.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.booster));
		folder.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.folder));
		portfolio.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.portfolio));
		desk.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.writing_desk));
		inkvial.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, MystObjects.Items.inkvial));
		glasses.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, "glasses"));

		ModRegistryPrimer.queueForRegistration(page);
		ModRegistryPrimer.queueForRegistration(agebook);
		ModRegistryPrimer.queueForRegistration(linkbook);
		ModRegistryPrimer.queueForRegistration(unlinked);
		ModRegistryPrimer.queueForRegistration(booster);
		ModRegistryPrimer.queueForRegistration(folder);
		ModRegistryPrimer.queueForRegistration(portfolio);
		ModRegistryPrimer.queueForRegistration(desk);
		ModRegistryPrimer.queueForRegistration(inkvial);
		ModRegistryPrimer.queueForRegistration(glasses);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModelColors() {
		ItemColors ic = Minecraft.getMinecraft().getItemColors();
		ic.registerItemColorHandler(new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				return 0x3333FF;
			}
		}, ModBlocks.portal);
		ic.registerItemColorHandler(new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				return ModFluids.black_ink.getColor();
			}
		}, ModBlocks.black_ink);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		ModelLoader.setCustomMeshDefinition(page, PageMeshDefinition.instance);
		ModelLoader.setCustomMeshDefinition(agebook, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "agebook"), "inventory"));
		ModelLoader.setCustomMeshDefinition(linkbook, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "linkbook"), "inventory"));
		ModelLoader.setCustomMeshDefinition(unlinked, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "unlinkedbook"), "inventory"));
		ModelLoader.setCustomMeshDefinition(booster, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "booster"), "inventory"));
		ModelLoader.setCustomMeshDefinition(folder, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "folder"), "inventory"));
		ModelLoader.setCustomMeshDefinition(portfolio, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "portfolio"), "inventory"));

		ModelBakery.registerItemVariants(desk, new ResourceLocation(MystObjects.MystcraftModId, "desk_bottom"), new ResourceLocation(MystObjects.MystcraftModId, "desk_top"));

		ModelLoader.setCustomMeshDefinition(desk, (stack) -> {
			if (stack.getItemDamage() == 0) {
				return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "desk_bottom"), "inventory");
			} else {
				return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "desk_top"), "inventory");
			}
		});

		ModelLoader.setCustomMeshDefinition(inkvial, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "vial"), "inventory"));
		ModelLoader.setCustomMeshDefinition(glasses, (stack -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "glasses"), "inventory")));

		ModelLoader.setCustomMeshDefinition(page, new PageMeshDefinition());
	}

	public static class PageMeshDefinition implements ItemMeshDefinition {

		public static PageMeshDefinition instance = new PageMeshDefinition();

		@SideOnly(Side.CLIENT)
		public String pathForSymbol(@Nullable IAgeSymbol symbol) {
			if (symbol == null)
				return "page_err_symbol";

			return "page_" + symbol.getRegistryName().getResourcePath();
		}

		@SideOnly(Side.CLIENT)
		public String getSubPath(@Nonnull ItemStack stack) {
			if (Page.isBlank(stack))
				return "page_blank";
			
			if (Page.isLinkPanel(stack))
				return "page_linkpanel";
			
			ResourceLocation symbolUniqueId = Page.getSymbol(stack);
			if (symbolUniqueId == null)
				return "page_err_symbol";
			
			return pathForSymbol(SymbolManager.getAgeSymbol(symbolUniqueId));
		}

		@Nonnull
		public ModelResourceLocation getModelLocationForSymbol(@Nullable IAgeSymbol symbol) {
			return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, pathForSymbol(symbol)), "inventory");
		}

		@Nonnull
		public ModelResourceLocation getModelLocationForLinkPanel() {
			return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "page_linkpanel"), "inventory");
		}

		@Nonnull
		public ModelResourceLocation getModelLocationForBlankPage() {
			return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "page_blank"), "inventory");
		}

		@Override
		@Nonnull
		public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
			return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, getSubPath(stack)), "inventory");
		}
	}

}
