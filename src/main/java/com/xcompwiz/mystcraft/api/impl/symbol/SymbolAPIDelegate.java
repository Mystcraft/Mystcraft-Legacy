package com.xcompwiz.mystcraft.api.impl.symbol;

import java.util.List;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SymbolAPIDelegate {
	public void setSymbolCardRank(IAgeSymbol symbol, int rank) {
		SymbolManager.setSymbolItemCardRank(symbol.getRegistryName(), rank);
	}

	public void setSymbolIsPurchasable(IAgeSymbol symbol, boolean flag) {
		SymbolManager.setSymbolIsTradable(symbol.getRegistryName(), flag);
	}

	public void setSymbolTradeItem(IAgeSymbol symbol, ItemStack itemstack) {
		this.setSymbolTradeItems(symbol, itemstack, null);
	}

	public void setSymbolTradeItems(IAgeSymbol symbol, ItemStack primary, ItemStack secondary) {
		SymbolManager.setSymbolTradeItems(symbol.getRegistryName(), primary, secondary);
	}

	public float getSymbolItemWeight(ResourceLocation identifier) {
		return SymbolManager.getSymbolItemWeight(identifier);
	}

	public boolean getSymbolIsTradable(ResourceLocation identifier) {
		return SymbolManager.isSymbolTradable(identifier);
	}

	public List<ItemStack> getSymbolTradeItems(ResourceLocation identifier) {
		return SymbolManager.getSymbolTradeItems(identifier);
	}

	public void blacklistIdentifier(ResourceLocation identifier) {
		SymbolManager.blackListSymbol(identifier);
	}

	public void registerWord(String name, DrawableWord word) {
		DrawableWordManager.registerWord(name, word);
	}

	public void registerWord(String name, Integer[] components) {
		DrawableWordManager.registerWord(name, components);
	}

	public List<IAgeSymbol> getAllRegisteredSymbols() {
		return SymbolManager.getAgeSymbols();
	}

	public IAgeSymbol getSymbol(ResourceLocation identifier) {
		return SymbolManager.getAgeSymbol(identifier);
	}

	public String getSymbolOwner(ResourceLocation identifier) {
		return SymbolManager.getSymbolOwner(identifier);
	}

}
