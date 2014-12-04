package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
			creative_collections.add(ModPageCollections.createCreativeCollection());
			creative_collections.add(ModPageCollections.buildPageCollection("Biome Distributions", GrammarData.BIOMECONTROLLER));
			creative_collections.add(ModPageCollections.buildPageCollection("Celestials", GrammarData.SUN, GrammarData.MOON, GrammarData.STARFIELD, GrammarData.DOODAD));
			creative_collections.add(ModPageCollections.buildPageCollection("Effects", GrammarData.EFFECT));
			creative_collections.add(ModPageCollections.buildPageCollection("Lighting", GrammarData.LIGHTING));
			creative_collections.add(ModPageCollections.buildPageCollection("Modifiers, Basic", GrammarData.ANGLE_BASIC, GrammarData.PERIOD_BASIC, GrammarData.PHASE_BASIC));
			creative_collections.add(ModPageCollections.buildPageCollection("Modifiers, Biomes", GrammarData.BIOME));
			creative_collections.add(ModPageCollections.buildPageCollection("Modifiers, Block", GrammarData.BLOCK_ANY, GrammarData.BLOCK_TERRAIN, GrammarData.BLOCK_SOLID, GrammarData.BLOCK_STRUCTURE, GrammarData.BLOCK_ORGANIC, GrammarData.BLOCK_CRYSTAL, GrammarData.BLOCK_SEA, GrammarData.BLOCK_FLUID, GrammarData.BLOCK_GAS));
			creative_collections.add(ModPageCollections.buildPageCollection("Modifiers, Colors", GrammarData.COLOR_BASIC, GrammarData.COLOR_SEQ, GrammarData.GRADIENT_BASIC, GrammarData.GRADIENT_SEQ, GrammarData.SUNSET));
			creative_collections.add(ModPageCollections.buildPageCollection("Populators", GrammarData.POPULATOR));
			creative_collections.add(ModPageCollections.buildPageCollection("Terrain Alterations", GrammarData.TERRAINALT));
			creative_collections.add(ModPageCollections.buildPageCollection("World Landscapes", GrammarData.TERRAIN));
			creative_collections.add(ModPageCollections.buildPageCollection("Visuals", GrammarData.VISUAL_EFFECT));
			creative_collections.add(ModPageCollections.buildPageCollection("Weather", GrammarData.WEATHER));
			for (ItemStack collection : creative_collections) {
				list.add(collection);
			}
		}
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
