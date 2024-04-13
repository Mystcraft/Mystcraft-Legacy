package com.xcompwiz.mystcraft.data;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import com.xcompwiz.mystcraft.api.MystObjects;
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

import cpw.mods.fml.common.registry.GameRegistry;

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
		page = (new ItemPage()).setUnlocalizedName("myst.page").setCreativeTab(null);
		agebook = (ItemAgebook) (new ItemAgebook()).setUnlocalizedName("myst.agebook").setCreativeTab(CreativeTabs.tabTransport);
		linkbook = (new ItemLinkbook()).setUnlocalizedName("myst.linkbook").setCreativeTab(CreativeTabs.tabTransport);
		unlinked = (new ItemLinkbookUnlinked()).setUnlocalizedName("myst.unlinkedbook").setCreativeTab(CreativeTabs.tabTransport);
		booster = (new ItemBoosterPack()).setUnlocalizedName("myst.booster").setCreativeTab(CreativeTabs.tabMisc);
		folder = (new ItemFolder()).setUnlocalizedName("myst.folder").setCreativeTab(CreativeTabs.tabMisc);
		portfolio = (new ItemPortfolio()).setUnlocalizedName("myst.portfolio").setCreativeTab(CreativeTabs.tabMisc);
		desk = (new ItemWritingDesk()).setUnlocalizedName("myst.writingdesk").setCreativeTab(CreativeTabs.tabDecorations);
		inkvial = (ItemInkVial) (new ItemInkVial()).setUnlocalizedName("myst.vial").setCreativeTab(CreativeTabs.tabMaterials).setContainerItem(Items.glass_bottle);

		GameRegistry.registerItem(page, MystObjects.Items.page);
		GameRegistry.registerItem(agebook, MystObjects.Items.descriptive_book);
		GameRegistry.registerItem(linkbook, MystObjects.Items.linkbook);
		GameRegistry.registerItem(unlinked, MystObjects.Items.linkbook_unlinked);
		GameRegistry.registerItem(booster, MystObjects.Items.booster);
		GameRegistry.registerItem(folder, MystObjects.Items.folder);
		GameRegistry.registerItem(portfolio, MystObjects.Items.portfolio);
		GameRegistry.registerItem(desk, MystObjects.Items.writing_desk);
		GameRegistry.registerItem(inkvial, MystObjects.Items.inkvial);
		GameRegistry.registerItem(glasses, "glasses");
	}
}
