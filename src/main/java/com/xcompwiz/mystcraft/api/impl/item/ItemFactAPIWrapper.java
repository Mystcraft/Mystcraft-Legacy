package com.xcompwiz.mystcraft.api.impl.item;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.hook.ItemFactory;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

public class ItemFactAPIWrapper extends APIWrapper implements ItemFactory {

	public ItemFactAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public ItemStack buildPage() {
		return InternalAPI.itemFact.buildPage();
	}

	@Override
	public ItemStack buildSymbolPage(String identifier) {
		return InternalAPI.itemFact.buildSymbolPage(identifier);
	}

	@Override
	public ItemStack buildLinkPage(String... properties) {
		return InternalAPI.itemFact.buildLinkPage(properties);
	}

	@Override
	public ItemStack buildCollectionItem(String name, String... tokens) {
		return InternalAPI.itemFact.buildCollectionItem(name, tokens);
	}

	@Override
	public ItemStack buildCollectionItem(String name, ItemStack... pages) {
		return InternalAPI.itemFact.buildCollectionItem(name, pages);
	}

}
