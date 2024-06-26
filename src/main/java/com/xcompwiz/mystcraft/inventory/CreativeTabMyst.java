package com.xcompwiz.mystcraft.inventory;

import javax.annotation.Nonnull;

import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.data.ModPageCollections;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabMyst extends CreativeTabs {

	private boolean hasSearchBar = false;
	private boolean collections = false;

	public CreativeTabMyst(String label) {
		this(label, false);
	}

	public CreativeTabMyst(String label, boolean folders) {
		super(label);
		this.collections = folders;
	}

	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		return new ItemStack(ModItems.agebook);
	}

	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> list) {
		for (Item item : Item.REGISTRY) {
			if (item == null || item == Items.AIR) {
				continue;
			}
			item.getSubItems(this, list);
		}

		if (collections) {
			//TODO: Use folders builder system
			NonNullList<ItemStack> creative_collections = NonNullList.create();
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
			list.addAll(creative_collections);
		}
	}

	private String getCollectionName(String name) {
		return I18n.format("myst.creative.notebook.wrapper", I18n.format(name));
	}

	public void setHasSearchBar(boolean flag) {
		hasSearchBar = flag;
	}

	@Override
	public boolean hasSearchBar() {
		return hasSearchBar;
	}
}
