package com.xcompwiz.mystcraft.api.impl;

import java.util.HashMap;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
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
import com.xcompwiz.mystcraft.page.SortingUtils.ComparatorItemSymbolAlphabetical;

public class InternalAPI {

	public static DimensionAPIDelegate		dimension;
	public static LinkingAPIDelegate		linking;
	public static LinkingAPIDelegate		linkProperties;
	public static InstabilityAPIDelegate	instability;
	public static SymbolAPIDelegate			symbol;
	public static SymbolAPIDelegate			word;
	public static SymbolAPIDelegate			symbolValues;
	public static GrammarAPIDelegate		grammar;
	public static PageAPIDelegate			page;
	public static RenderAPIDelegate			render;

	public static InstabilityFactory		instabilityFact;
	public static SymbolFactory				symbolFact;
	public static ItemFactory				itemFact;

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
