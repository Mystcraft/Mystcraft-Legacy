package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.data.ModPageCollections;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabMyst extends CreativeTabs {

	private List<ItemStack>	forcelist		= new ArrayList<ItemStack>();
	private boolean			hasSearchBar	= false;
	private boolean			collections		= false;

	public CreativeTabMyst(String label) {
		this(label, false);
	}

	public CreativeTabMyst(String label, boolean folders) {
		super(label);
		this.collections = folders;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ModItems.agebook;
	}

	/**
	 * only shows items which have tabToDisplayOn == this
	 */
	@Override
	public void displayAllReleventItems(List list) {
		Iterator<Item> iterator = Item.itemRegistry.iterator();

		while (iterator.hasNext()) {
			Item item = iterator.next();
			if (item == null) continue;

			for (CreativeTabs tab : item.getCreativeTabs()) {
				if (tab == this) {
					item.getSubItems(item, this, list);
				}
			}
		}
		for (ItemStack itemstack : forcelist) {
			list.add(itemstack);
		}
		if (collections) {
			//TODO: Use folders builder system
			ArrayList<ItemStack> creative_collections = new ArrayList<ItemStack>();
			creative_collections.add(ModPageCollections.createCreativeCollection(getCollectionName("myst.creative.notebook.all")));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.biomedist"), GrammarData.BIOMECONTROLLER));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.celestials"), GrammarData.SUN, GrammarData.MOON, GrammarData.STARFIELD, GrammarData.DOODAD));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.effects"), GrammarData.EFFECT));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.lighting"), GrammarData.LIGHTING));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.modbasic"), GrammarData.ANGLE_BASIC, GrammarData.PERIOD_BASIC, GrammarData.PHASE_BASIC));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.modbiome"), GrammarData.BIOME));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.modblock"), GrammarData.BLOCK_ANY, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SOLID, GrammarData.BLOCK_STRUCTURE, GrammarData.BLOCK_ORGANIC, GrammarData.BLOCK_CRYSTAL, GrammarData.BLOCK_SEA, GrammarData.BLOCK_FLUID, GrammarData.BLOCK_GAS));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.modcolor"), GrammarData.COLOR_BASIC, GrammarData.COLOR_SEQ, GrammarData.GRADIENT_BASIC, GrammarData.GRADIENT_SEQ, GrammarData.SUNSET));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.features"), GrammarData.FEATURE_SMALL, GrammarData.FEATURE_MEDIUM, GrammarData.FEATURE_LARGE));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.terrain"), GrammarData.TERRAIN));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.visuals"), GrammarData.VISUAL_EFFECT));
			creative_collections.add(ModPageCollections.buildPageCollection(getCollectionName("myst.creative.notebook.weather"), GrammarData.WEATHER));
			for (ItemStack collection : creative_collections) {
				list.add(collection);
			}
		}
	}

	private String getCollectionName(String name) {
		return StatCollector.translateToLocalFormatted("myst.creative.notebook.wrapper", StatCollector.translateToLocal(name));
	}

	public void registerItemStack(ItemStack itemstack) {
		forcelist.add(itemstack);
	}

	public void setHasSearchBar(boolean flag) {
		hasSearchBar = flag;
	}

	@Override
	public boolean hasSearchBar() {
		return hasSearchBar;
	}
}
