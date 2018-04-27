package com.xcompwiz.mystcraft.api.impl.item;

import com.xcompwiz.mystcraft.api.hook.ItemFactory;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemFactAPIWrapper extends APIWrapper implements ItemFactory {

	public ItemFactAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public ItemStack buildPage() {
		return InternalAPI.itemFact.buildPage();
	}

	@Override
	public ItemStack buildSymbolPage(ResourceLocation identifier) {
		return InternalAPI.itemFact.buildSymbolPage(identifier);
	}

	@Override
	public ItemStack buildLinkPage(String... properties) {
		return InternalAPI.itemFact.buildLinkPage(properties);
	}

	@Override
	public ItemStack buildCollectionItem(String name, ResourceLocation... tokens) {
		return InternalAPI.itemFact.buildCollectionItem(name, tokens);
	}

	@Override
	public ItemStack buildCollectionItem(String name, ItemStack... pages) {
		return InternalAPI.itemFact.buildCollectionItem(name, pages);
	}

}
