package com.xcompwiz.mystcraft.symbol;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.internal.IWordAPI;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.oldapi.internal.ISymbolAPI;
import com.xcompwiz.mystcraft.oldapi.internal.ISymbolValuesAPI;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

public class SymbolAPIDelegate implements ISymbolAPI, ISymbolValuesAPI, IWordAPI {

	@Override
	public void setSymbolCardRank(IAgeSymbol symbol, int weight) {
		SymbolManager.setSymbolItemCardRank(symbol.identifier(), weight);
	}

	@Override
	public void setSymbolIsPurchasable(IAgeSymbol symbol, boolean flag) {
		SymbolManager.setSymbolIsTradable(symbol.identifier(), flag);
	}

	@Override
	public void setSymbolTradeItem(IAgeSymbol symbol, ItemStack itemstack) {
		this.setSymbolTradeItems(symbol, itemstack, null);
	}

	@Override
	public void setSymbolTradeItems(IAgeSymbol symbol, ItemStack primary, ItemStack secondary) {
		SymbolManager.setSymbolTradeItems(symbol.identifier(), primary, secondary);
	}

	@Override
	public float getSymbolItemWeight(String identifier) {
		return SymbolManager.getSymbolItemWeight(identifier);
	}

	@Override
	public boolean getSymbolIsTradable(String identifier) {
		return SymbolManager.isSymbolTradable(identifier);
	}

	@Override
	public List<ItemStack> getSymbolTradeItems(String identifier) {
		return SymbolManager.getSymbolTradeItems(identifier);
	}

	@Override
	public void blacklistIdentifier(String identifier) {
		SymbolManager.blackListSymbol(identifier);
	}

	@Override
	public boolean registerSymbol(IAgeSymbol symbol) {
		return SymbolManager.registerSymbol(symbol, true);
	}

	@Override
	public boolean registerSymbol(IAgeSymbol symbol, boolean generateConfigOption) {
		return SymbolManager.registerSymbol(symbol, generateConfigOption);
	}

	@Override
	public void registerWord(String name, DrawableWord word) {
		DrawableWordManager.registerWord(name, word);
	}

	@Override
	public void registerWord(String name, Integer[] components) {
		DrawableWordManager.registerWord(name, components);
	}

	@Override
	public List<IAgeSymbol> getAllRegisteredSymbols() {
		return SymbolManager.getAgeSymbols();
	}

	@Override
	public IAgeSymbol getSymbolForIdentifier(String identifier) {
		return SymbolManager.getAgeSymbol(identifier);
	}

	@Override
	public String getSymbolOwner(String identifier) {
		return SymbolManager.getSymbolOwner(identifier);
	}

}
