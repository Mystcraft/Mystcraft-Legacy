package com.xcompwiz.mystcraft.api.impl;

import java.util.HashMap;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.client.IRenderAPI;
import com.xcompwiz.mystcraft.api.grammar.IGrammarAPI;
import com.xcompwiz.mystcraft.api.impl.client.RenderAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.grammar.GrammarAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.instability.InstabilityAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.instability.InstabilityFactory;
import com.xcompwiz.mystcraft.api.impl.item.ItemFactory;
import com.xcompwiz.mystcraft.api.impl.linking.DimensionAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.linking.LinkingAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.page.PageAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.symbol.SymbolAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.symbol.SymbolFactory;
import com.xcompwiz.mystcraft.api.instability.IInstabilityAPI;
import com.xcompwiz.mystcraft.api.instability.IInstabilityFactory;
import com.xcompwiz.mystcraft.api.item.IItemFactory;
import com.xcompwiz.mystcraft.api.linking.IDimensionAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkingAPI;
import com.xcompwiz.mystcraft.api.symbol.ISymbolAPI;
import com.xcompwiz.mystcraft.api.symbol.ISymbolFactory;
import com.xcompwiz.mystcraft.api.word.IWordAPI;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;
import com.xcompwiz.mystcraft.oldapi.internal.IPageAPI;
import com.xcompwiz.mystcraft.oldapi.internal.ISymbolValuesAPI;
import com.xcompwiz.mystcraft.page.SortingUtils.ComparatorItemSymbolAlphabetical;

public class InternalAPI {

	//FIXME: $ Change these to BaseImpls
	public static IDimensionAPI			dimension;
	public static ILinkingAPI			linking;
	public static ILinkPropertyAPI		linkProperties;
	public static IInstabilityAPI		instability;
	public static IInstabilityFactory	instabilityFact;
	public static ISymbolAPI			symbol;
	public static IWordAPI				word;
	public static ISymbolValuesAPI		symbolValues;
	public static IGrammarAPI			grammar;
	public static ISymbolFactory		symbolFact;
	public static IPageAPI				page;
	public static IItemFactory			itemFact;
	public static IRenderAPI			render;

	public static void initAPI() {
		LinkingAPIDelegate linking_delegate = new LinkingAPIDelegate();
		dimension = new DimensionAPIDelegate();
		linking = linking_delegate;
		linkProperties = linking_delegate;

		instability = new InstabilityAPIDelegate();
		instabilityFact = new InstabilityFactory();

		SymbolAPIDelegate symbol_delegate = new SymbolAPIDelegate();
		symbol = symbol_delegate;
		word = symbol_delegate;
		symbolValues = symbol_delegate;
		grammar = new GrammarAPIDelegate();
		symbolFact = new SymbolFactory();

		page = new PageAPIDelegate();
		itemFact = new ItemFactory();

		render = new RenderAPIDelegate();

		//TODO: Why do I do this?
		ComparatorItemSymbolAlphabetical.instance.compare(null, null);

		APIProviderImpl.init();
	}

	private static HashMap<String, APIInstanceProvider>	instances	= new HashMap<String, APIInstanceProvider>();

	public synchronized static APIInstanceProvider getAPIProviderInstance(String modId) {
		APIInstanceProvider instance = instances.get(modId);
		if (instance == null) {
			instance = new APIProviderImpl(modId);
			instances.put(modId, instance);
		}
		return instance;
	}
}
