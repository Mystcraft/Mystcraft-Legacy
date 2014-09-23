package com.xcompwiz.mystcraft.data;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemInkVial;
import com.xcompwiz.mystcraft.item.ItemLinkbook;
import com.xcompwiz.mystcraft.item.ItemLinkbookUnlinked;
import com.xcompwiz.mystcraft.item.ItemMyGlasses;
import com.xcompwiz.mystcraft.item.ItemNotebook;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.item.ItemWritingDesk;

import cpw.mods.fml.common.registry.GameRegistry;

public class LoaderItems {
	public static Item glasses = new ItemMyGlasses();

	public static void loadConfigs(MystConfig config) {}

	public static void init() {
		ItemPage.instance = (new ItemPage()).setUnlocalizedName("myst.page").setCreativeTab(null);
		ItemAgebook.instance = (ItemAgebook) (new ItemAgebook()).setUnlocalizedName("myst.agebook").setCreativeTab(CreativeTabs.tabTransport);
		ItemLinkbook.instance = (new ItemLinkbook()).setUnlocalizedName("myst.linkbook").setCreativeTab(CreativeTabs.tabTransport);
		ItemLinkbookUnlinked.instance = (new ItemLinkbookUnlinked()).setUnlocalizedName("myst.unlinkedbook").setCreativeTab(CreativeTabs.tabTransport);
		ItemNotebook.instance = (new ItemNotebook()).setUnlocalizedName("myst.notebook").setCreativeTab(CreativeTabs.tabMisc);
		ItemWritingDesk.instance = (new ItemWritingDesk()).setUnlocalizedName("myst.writingdesk").setCreativeTab(CreativeTabs.tabDecorations);
		ItemInkVial.instance = (ItemInkVial) (new ItemInkVial()).setUnlocalizedName("myst.vial").setCreativeTab(CreativeTabs.tabMaterials).setContainerItem(Items.glass_bottle);

		GameRegistry.registerItem(ItemPage.instance, MystObjects.item_page);
		GameRegistry.registerItem(ItemAgebook.instance, MystObjects.item_descriptive_book);
		GameRegistry.registerItem(ItemLinkbook.instance, MystObjects.item_linkbook);
		GameRegistry.registerItem(ItemLinkbookUnlinked.instance, MystObjects.item_linkbook_unlinked);
		GameRegistry.registerItem(ItemNotebook.instance, MystObjects.item_notebook);
		GameRegistry.registerItem(ItemWritingDesk.instance, MystObjects.item_writing_desk);
		GameRegistry.registerItem(ItemInkVial.instance, MystObjects.item_inkvial);
		GameRegistry.registerItem(glasses, "glasses");
	}
}
