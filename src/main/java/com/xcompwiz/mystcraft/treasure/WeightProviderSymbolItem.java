package com.xcompwiz.mystcraft.treasure;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector.IWeightProvider;
import net.minecraft.util.ResourceLocation;

public class WeightProviderSymbolItem implements IWeightProvider {

	public static final IWeightProvider	instance	= new WeightProviderSymbolItem();

	private WeightProviderSymbolItem() {}

	@Override
	public float getItemWeight(Object item) {
		if (item instanceof IAgeSymbol) {
			IAgeSymbol symbol = ((IAgeSymbol) item);
			ResourceLocation id = symbol.getRegistryName();
			return SymbolManager.getSymbolItemWeight(id);
		}
		return 1;
	}

}
