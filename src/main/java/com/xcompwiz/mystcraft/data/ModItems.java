package com.xcompwiz.mystcraft.data;

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
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModItems {

	public static Item			page;
	public static ItemAgebook	agebook;
	public static Item			linkbook;
	public static Item			unlinked;
	public static Item			booster;
	public static Item			folder;
	public static Item			portfolio;
	public static Item			desk;
	public static ItemInkVial	inkvial;
	public static Item			glasses	= new ItemMyGlasses();

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

		GameRegistry.register(page);
		GameRegistry.register(agebook);
		GameRegistry.register(linkbook);
		GameRegistry.register(unlinked);
		GameRegistry.register(booster);
		GameRegistry.register(folder);
		GameRegistry.register(portfolio);
		GameRegistry.register(desk);
		GameRegistry.register(inkvial);
		GameRegistry.register(glasses);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		imm.register(page, new PageMeshDefinition());

		imm.register(agebook, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "agebook"), "inventory"));
		imm.register(linkbook, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "linkbook"), "inventory"));
		imm.register(unlinked, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "unlinked"), "inventory"));
		imm.register(booster, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "booster"), "inventory"));
		imm.register(folder, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "folder"), "inventory"));
		imm.register(portfolio, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "portfolio"), "inventory"));

		imm.register(desk, 0, new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "desk_bottom"), "inventory"));
		imm.register(desk, 1, new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "desk_top"), "inventory"));

		imm.register(inkvial, (stack) -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "ink_vial"), "inventory"));
		imm.register(glasses, (stack -> new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "glasses"), "inventory")));
	}

	public static class PageMeshDefinition implements ItemMeshDefinition {

		public static PageMeshDefinition instance = new PageMeshDefinition();

		@SideOnly(Side.CLIENT)
		public String pathForSymbol(@Nullable IAgeSymbol symbol) {
		    if(symbol == null) {
		        return "page_no_symbol";
            }
			return "page_" + symbol.identifier();
		}

		@SideOnly(Side.CLIENT)
		public String pathForSymbol(@Nonnull ItemStack stack) {
			String symbolUniqueId = Page.getSymbol(stack);
			if(symbolUniqueId == null) {
				return "page_no_symbol";
			} else {
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(symbolUniqueId);
				if(symbol != null) {
					return "page_" + symbol.identifier();
				} else {
					return "page_no_symbol";
				}
			}
		}

		@Nonnull
		public ModelResourceLocation getModelLocationForSymbol(@Nullable IAgeSymbol symbol) {
			return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, pathForSymbol(symbol)), "inventory");
		}

		@Override
		@Nonnull
		public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
			return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, pathForSymbol(stack)), "inventory");
		}
	}

}
