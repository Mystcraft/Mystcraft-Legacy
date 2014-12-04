package com.xcompwiz.mystcraft.treasure;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector.IWeightProvider;

public class WeightProviderSymbolItem implements IWeightProvider {

	public static final IWeightProvider	instance	= new WeightProviderSymbolItem();

	private WeightProviderSymbolItem() {}

	@Override
	public float getItemWeight(Object item) {
		if (item instanceof IAgeSymbol) {
			IAgeSymbol symbol = ((IAgeSymbol) item);
			String id = symbol.identifier();
			return SymbolManager.getSymbolItemWeight(id);
		}
		return 1;
	};

}
