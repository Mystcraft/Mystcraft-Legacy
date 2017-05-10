package com.xcompwiz.mystcraft.api.impl.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.page.SortingUtils.ComparatorSymbolAlphabetical;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemFactoryImpl {

	public ItemStack buildPage() {
		return Page.createPage();
	}

	public ItemStack buildSymbolPage(String identifier) {
		return Page.createSymbolPage(identifier);
	}

	public ItemStack buildLinkPage(String... properties) {
		ItemStack page = Page.createPage();
		Page.makeLinkPanel(page);
		for (String property : properties) {
			Page.addLinkProperty(page, property);
		}
		return page;
	}

	@Nonnull
	public ItemStack buildCollectionItem(String name, String... tokens) {
		// First, grab all the rules
		HashSet<Rule> rules = new HashSet<Rule>();
		for (String token : tokens) {
			if (token == null) continue;
			List<Rule> tokenrules = GrammarGenerator.getAllRules(token);
			if (tokenrules != null) rules.addAll(tokenrules);
		}

		HashSet<IAgeSymbol> symbolsset = new HashSet<IAgeSymbol>();

		// Get symbols
		for (Rule rule : rules) {
			for (String token : rule.getValues()) {
				if (!SymbolManager.hasBinding(token)) continue;
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(token);
				if (symbol == null) continue;
				symbolsset.add(symbol);
			}
		}
		ArrayList<IAgeSymbol> symbols = new ArrayList<IAgeSymbol>();
		symbols.addAll(symbolsset);
		Collections.sort(symbols, ComparatorSymbolAlphabetical.instance);

		ItemStack itemstack = new ItemStack(ModItems.portfolio, 1, 0);
		IItemPageCollection item = (IItemPageCollection) itemstack.getItem();
		item.setDisplayName(null, itemstack, name);

		for (IAgeSymbol symbol : symbols) {
			item.addPage(null, itemstack, Page.createSymbolPage(symbol.identifier()));
		}
		return itemstack;
	}

	public ItemStack buildCollectionItem(String name, ItemStack... pages) {
		ItemStack itemstack = new ItemStack(ModItems.portfolio, 1, 0);
		IItemPageCollection item = (IItemPageCollection) itemstack.getItem();
		item.setDisplayName(null, itemstack, name);

		for (ItemStack page : pages) {
			if (page == null) continue;
			if (!(page.getItem() instanceof ItemPage)) continue;
			item.addPage(null, itemstack, page.copy());
		}
		return itemstack;
	}
}
