package com.xcompwiz.mystcraft.api.hook;

import java.util.List;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Allows the setting of various treasure rarity and weighting values for symbols. See {@link GrammarAPI} for setting the grammar rule(s) for a symbol. These
 * should be set before post-init. The implementation of this is provided by {@link APIInstanceProvider} as "symbolvals-1". Do NOT implement this yourself!
 * @author xcompwiz
 */
public interface SymbolValuesAPI {
	/**
	 * Sets the symbol's rarity ranking which controls the weight used when generating treasure and selecting pages for trades. Rank 0 is very common, 1 is
	 * common, 2 is uncommon, and 3 and up is rare. Higher numbers result in higher rarity.
	 * @param symbol The symbol to set the weight for
	 * @param rank The ranking of the symbol as a TCG-style rarity rank.
	 */
	public void setSymbolCardRank(IAgeSymbol symbol, int rank);

	/**
	 * Can be used to prevent symbols from being tradeable. By default, any symbol with a ranking can be traded by a villager. This will not prevent treasure
	 * generation.
	 * @param symbol The symbol to affect
	 * @param flag Whether or not a village can sell the item
	 */
	public void setSymbolIsPurchasable(IAgeSymbol symbol, boolean flag);

	/**
	 * @param identifier The identifier of the symbol to use
	 * @return The weight used when generating treasure and selecting pages for trades
	 */
	public float getSymbolItemWeight(ResourceLocation identifier);

	/**
	 * Returns if the symbol is procurable through trade
	 * @param identifier The identifier of the symbol to use
	 * @return True is the symbol can be traded for. False otherwise.
	 */
	public boolean getSymbolIsTradable(ResourceLocation identifier);

	/**
	 * Can be used to set the item a villager will trade the symbol for
	 * @param symbol The symbol the villager will trade
	 * @param itemstack An instance of the item that the villager wants to trade for
	 */
	public void setSymbolTradeItem(IAgeSymbol symbol, ItemStack itemstack);

	/**
	 * Can be used to set the items a villager will trade the symbol for. This version allows the setting of multiple trade items. Both items will be required
	 * for the trade.
	 * @param symbol The symbol the villager will trade
	 * @param itemstack An instance of the first item that the villager wants to trade for
	 * @param secondary An instance of the second item that the villager wants to trade for
	 */
	public void setSymbolTradeItems(IAgeSymbol symbol, ItemStack itemstack, ItemStack secondary);

	/**
	 * Returns the asking item(s) in a trade
	 * @param identifier The identifier of the symbol to use
	 * @return The item(s) that the villager is willing to trade for as a list (which is never more than two elements).
	 */
	public List<ItemStack> getSymbolTradeItems(ResourceLocation identifier);
}
