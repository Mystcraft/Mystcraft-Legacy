package com.xcompwiz.mystcraft.api.impl.symbol;

import java.util.List;

import com.xcompwiz.mystcraft.api.hook.SymbolValuesAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

import net.minecraft.item.ItemStack;

public class SymbolValuesAPIWrapper extends APIWrapper implements SymbolValuesAPI {

	public SymbolValuesAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public void setSymbolCardRank(IAgeSymbol symbol, int rank) {
		InternalAPI.symbolValues.setSymbolCardRank(symbol, rank);
	}

	@Override
	public void setSymbolIsPurchasable(IAgeSymbol symbol, boolean flag) {
		InternalAPI.symbolValues.setSymbolIsPurchasable(symbol, flag);
	}

	@Override
	public float getSymbolItemWeight(String identifier) {
		return InternalAPI.symbolValues.getSymbolItemWeight(identifier);
	}

	@Override
	public boolean getSymbolIsTradable(String identifier) {
		return InternalAPI.symbolValues.getSymbolIsTradable(identifier);
	}

	@Override
	public void setSymbolTradeItem(IAgeSymbol symbol, ItemStack itemstack) {
		InternalAPI.symbolValues.setSymbolTradeItem(symbol, itemstack);
	}

	@Override
	public void setSymbolTradeItems(IAgeSymbol symbol, ItemStack primary, ItemStack secondary) {
		InternalAPI.symbolValues.setSymbolTradeItems(symbol, primary, secondary);
	}

	@Override
	public List<ItemStack> getSymbolTradeItems(String identifier) {
		return InternalAPI.symbolValues.getSymbolTradeItems(identifier);
	}

}
