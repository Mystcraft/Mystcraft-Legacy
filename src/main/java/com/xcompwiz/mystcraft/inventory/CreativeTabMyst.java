package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
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
			creative_collections.add(ModPageCollections.buildPageCollection("Biome Distributions", IGrammarAPI.BIOMECONTROLLER));
			creative_collections.add(ModPageCollections.buildPageCollection("Celestials", IGrammarAPI.SUN, IGrammarAPI.MOON, IGrammarAPI.STARFIELD, IGrammarAPI.DOODAD));
			creative_collections.add(ModPageCollections.buildPageCollection("Effects", IGrammarAPI.EFFECT));
			creative_collections.add(ModPageCollections.buildPageCollection("Lighting", IGrammarAPI.LIGHTING));
			creative_collections.add(ModPageCollections.buildPageCollection("Modifiers, Basic", IGrammarAPI.ANGLE_BASIC, IGrammarAPI.PERIOD_BASIC, IGrammarAPI.PHASE_BASIC));
			creative_collections.add(ModPageCollections.buildPageCollection("Modifiers, Biomes", IGrammarAPI.BIOME));
			creative_collections.add(ModPageCollections.buildPageCollection("Modifiers, Block", IGrammarAPI.BLOCK_ANY, IGrammarAPI.BLOCK_TERRAIN, IGrammarAPI.BLOCK_SOLID, IGrammarAPI.BLOCK_STRUCTURE, IGrammarAPI.BLOCK_ORGANIC, IGrammarAPI.BLOCK_CRYSTAL, IGrammarAPI.BLOCK_SEA, IGrammarAPI.BLOCK_FLUID, IGrammarAPI.BLOCK_GAS));
			creative_collections.add(ModPageCollections.buildPageCollection("Modifiers, Colors", IGrammarAPI.COLOR_BASIC, IGrammarAPI.COLOR_SEQ, IGrammarAPI.GRADIENT_BASIC, IGrammarAPI.GRADIENT_SEQ, IGrammarAPI.SUNSET));
			creative_collections.add(ModPageCollections.buildPageCollection("Populators", IGrammarAPI.POPULATOR));
			creative_collections.add(ModPageCollections.buildPageCollection("Terrain Alterations", IGrammarAPI.TERRAINALT));
			creative_collections.add(ModPageCollections.buildPageCollection("World Landscapes", IGrammarAPI.TERRAIN));
			creative_collections.add(ModPageCollections.buildPageCollection("Visuals", IGrammarAPI.VISUAL_EFFECT));
			creative_collections.add(ModPageCollections.buildPageCollection("Weather", IGrammarAPI.WEATHER));
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
