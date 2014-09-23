package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.data.ModNotebooks;
import com.xcompwiz.mystcraft.item.ItemAgebook;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabMyst extends CreativeTabs {

	private List<ItemStack>	forcelist		= new ArrayList<ItemStack>();
	private boolean			hasSearchBar	= false;
	private boolean			notebooks		= false;

	public CreativeTabMyst(String label) {
		this(label, false);
	}

	public CreativeTabMyst(String label, boolean notebooks) {
		super(label);
		this.notebooks = notebooks;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ItemAgebook.instance;
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
		if (notebooks) {
			//TODO: Use notebook builder system
			ArrayList<ItemStack> creative_notebooks = new ArrayList<ItemStack>();
			creative_notebooks.add(ModNotebooks.createCreativeNotebook());
			creative_notebooks.add(ModNotebooks.buildNotebook("Biome Controllers", IGrammarAPI.BIOMECONTROLLER));
			creative_notebooks.add(ModNotebooks.buildNotebook("Celestials", IGrammarAPI.SUN, IGrammarAPI.MOON, IGrammarAPI.STARFIELD, IGrammarAPI.DOODAD));
			creative_notebooks.add(ModNotebooks.buildNotebook("Effects", IGrammarAPI.EFFECT));
			creative_notebooks.add(ModNotebooks.buildNotebook("Lighting", IGrammarAPI.LIGHTING));
			creative_notebooks.add(ModNotebooks.buildNotebook("Modifiers, Basic", IGrammarAPI.ANGLE_BASIC, IGrammarAPI.PERIOD_BASIC, IGrammarAPI.PHASE_BASIC));
			creative_notebooks.add(ModNotebooks.buildNotebook("Modifiers, Biomes", IGrammarAPI.BIOME));
			creative_notebooks.add(ModNotebooks.buildNotebook("Modifiers, Block", IGrammarAPI.BLOCK_ANY, IGrammarAPI.BLOCK_CRYSTAL, IGrammarAPI.BLOCK_FLUID, IGrammarAPI.BLOCK_GAS, IGrammarAPI.BLOCK_SEA, IGrammarAPI.BLOCK_SOLID, IGrammarAPI.BLOCK_STRUCTURE, IGrammarAPI.BLOCK_TERRAIN));
			creative_notebooks.add(ModNotebooks.buildNotebook("Modifiers, Colors", IGrammarAPI.COLOR_BASIC, IGrammarAPI.COLOR_SEQ, IGrammarAPI.GRADIENT_BASIC, IGrammarAPI.GRADIENT_SEQ, IGrammarAPI.SUNSET));
			creative_notebooks.add(ModNotebooks.buildNotebook("Populators", IGrammarAPI.POPULATOR));
			creative_notebooks.add(ModNotebooks.buildNotebook("Terrain Alterations", IGrammarAPI.TERRAINALT));
			creative_notebooks.add(ModNotebooks.buildNotebook("Terrains", IGrammarAPI.TERRAIN));
			creative_notebooks.add(ModNotebooks.buildNotebook("Visuals", IGrammarAPI.VISUAL_EFFECT));
			creative_notebooks.add(ModNotebooks.buildNotebook("Weather", IGrammarAPI.WEATHER));
			for (ItemStack notebook : creative_notebooks) {
				list.add(notebook);
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
