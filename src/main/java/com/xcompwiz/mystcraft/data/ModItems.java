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

public class ModItems {
	public static Item			page;
	public static ItemAgebook	agebook;
	public static Item			linkbook;
	public static Item			unlinked;
	public static Item			notebook;
	public static Item			desk;
	public static ItemInkVial	inkvial;
	public static Item			glasses	= new ItemMyGlasses();

	public static void loadConfigs(MystConfig config) {}

	public static void init() {
		page = (new ItemPage()).setUnlocalizedName("myst.page").setCreativeTab(null);
		agebook = (ItemAgebook) (new ItemAgebook()).setUnlocalizedName("myst.agebook").setCreativeTab(CreativeTabs.tabTransport);
		linkbook = (new ItemLinkbook()).setUnlocalizedName("myst.linkbook").setCreativeTab(CreativeTabs.tabTransport);
		unlinked = (new ItemLinkbookUnlinked()).setUnlocalizedName("myst.unlinkedbook").setCreativeTab(CreativeTabs.tabTransport);
		notebook = (new ItemNotebook()).setUnlocalizedName("myst.notebook").setCreativeTab(CreativeTabs.tabMisc);
		desk = (new ItemWritingDesk()).setUnlocalizedName("myst.writingdesk").setCreativeTab(CreativeTabs.tabDecorations);
		inkvial = (ItemInkVial) (new ItemInkVial()).setUnlocalizedName("myst.vial").setCreativeTab(CreativeTabs.tabMaterials).setContainerItem(Items.glass_bottle);

		GameRegistry.registerItem(page, MystObjects.item_page);
		GameRegistry.registerItem(agebook, MystObjects.item_descriptive_book);
		GameRegistry.registerItem(linkbook, MystObjects.item_linkbook);
		GameRegistry.registerItem(unlinked, MystObjects.item_linkbook_unlinked);
		GameRegistry.registerItem(notebook, MystObjects.item_notebook);
		GameRegistry.registerItem(desk, MystObjects.item_writing_desk);
		GameRegistry.registerItem(inkvial, MystObjects.item_inkvial);
		GameRegistry.registerItem(glasses, "glasses");
	}
}
