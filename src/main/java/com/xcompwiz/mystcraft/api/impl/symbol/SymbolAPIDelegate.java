package com.xcompwiz.mystcraft.api.impl.symbol;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

public class SymbolAPIDelegate {
	public void setSymbolCardRank(IAgeSymbol symbol, int weight) {
		SymbolManager.setSymbolItemCardRank(symbol.identifier(), weight);
	}

	public void setSymbolIsPurchasable(IAgeSymbol symbol, boolean flag) {
		SymbolManager.setSymbolIsTradable(symbol.identifier(), flag);
	}

	public void setSymbolTradeItem(IAgeSymbol symbol, ItemStack itemstack) {
		this.setSymbolTradeItems(symbol, itemstack, null);
	}

	public void setSymbolTradeItems(IAgeSymbol symbol, ItemStack primary, ItemStack secondary) {
		SymbolManager.setSymbolTradeItems(symbol.identifier(), primary, secondary);
	}

	public float getSymbolItemWeight(String identifier) {
		return SymbolManager.getSymbolItemWeight(identifier);
	}

	public boolean getSymbolIsTradable(String identifier) {
		return SymbolManager.isSymbolTradable(identifier);
	}

	public List<ItemStack> getSymbolTradeItems(String identifier) {
		return SymbolManager.getSymbolTradeItems(identifier);
	}

	public void blacklistIdentifier(String identifier) {
		SymbolManager.blackListSymbol(identifier);
	}

	public boolean registerSymbol(IAgeSymbol symbol, String modid) {
		return SymbolManager.registerSymbol(symbol, true, modid);
	}

	public boolean registerSymbol(IAgeSymbol symbol, boolean generateConfigOption, String modid) {
		return SymbolManager.registerSymbol(symbol, generateConfigOption, modid);
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

	public IAgeSymbol getSymbol(String identifier) {
		return SymbolManager.getAgeSymbol(identifier);
	}

	public String getSymbolOwner(String identifier) {
		return SymbolManager.getSymbolOwner(identifier);
	}

}
