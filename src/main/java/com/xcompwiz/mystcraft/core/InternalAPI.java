package com.xcompwiz.mystcraft.core;

import java.util.HashMap;

import com.xcompwiz.mystcraft.api.MystAPI;
import com.xcompwiz.mystcraft.api.client.IRenderAPI;
import com.xcompwiz.mystcraft.api.grammar.IGrammarAPI;
import com.xcompwiz.mystcraft.api.linking.IDimensionAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkingAPI;
import com.xcompwiz.mystcraft.api.word.IWordAPI;
import com.xcompwiz.mystcraft.client.render.RenderAPIDelegate;
import com.xcompwiz.mystcraft.grammar.GrammarAPIDelegate;
import com.xcompwiz.mystcraft.instability.InstabilityAPIDelegate;
import com.xcompwiz.mystcraft.instability.InstabilityFactory;
import com.xcompwiz.mystcraft.item.ItemFactory;
import com.xcompwiz.mystcraft.linking.DimensionAPIDelegate;
import com.xcompwiz.mystcraft.linking.LinkingAPIDelegate;
import com.xcompwiz.mystcraft.page.PageAPIDelegate;
import com.xcompwiz.mystcraft.page.SortingUtils.ComparatorItemSymbolAlphabetical;
import com.xcompwiz.mystcraft.symbol.SymbolAPIDelegate;
import com.xcompwiz.mystcraft.symbol.SymbolFactory;

public class InternalAPI {

	public static class MystAPIInstance extends MystAPI {
		public final String	modId;

		public MystAPIInstance(String modId) {
			this.modId = modId;
		}

		@Override
		public IDimensionAPI getDimensionAPI() {
			return InternalAPI.dimension;
		}

		@Override
		public ILinkingAPI getLinkingAPI() {
			return InternalAPI.linking;
		}

		@Override
		public IGrammarAPI getGrammarAPI() {
			return InternalAPI.grammar;
		}

		@Override
		public IWordAPI getWordAPI() {
			return InternalAPI.symbol;
		}

		@Override
		public IRenderAPI getRenderAPI() {
			return InternalAPI.render;
		}
	}

	public static DimensionAPIDelegate		dimension;
	public static LinkingAPIDelegate		linking;
	public static LinkingAPIDelegate		linkProperties;
	public static InstabilityAPIDelegate	instability;
	public static InstabilityFactory		instabilityFact;
	public static SymbolAPIDelegate			symbol;
	public static SymbolAPIDelegate			symbolValues;
	public static GrammarAPIDelegate		grammar;
	public static SymbolFactory				symbolFact;
	public static PageAPIDelegate			page;
	public static ItemFactory				itemFact;
	public static RenderAPIDelegate			render;

	private static HashMap<String, MystAPI>	instances	= new HashMap<String, MystAPI>();

	public static void initAPI() {
		LinkingAPIDelegate linking_delegate = new LinkingAPIDelegate();
		dimension = new DimensionAPIDelegate();
		linking = linking_delegate;
		linkProperties = linking_delegate;

		instability = new InstabilityAPIDelegate();
		instabilityFact = new InstabilityFactory();

		SymbolAPIDelegate symbol_delegate = new SymbolAPIDelegate();
		symbol = symbol_delegate;
		symbolValues = symbol_delegate;
		grammar = new GrammarAPIDelegate();
		symbolFact = new SymbolFactory();

		page = new PageAPIDelegate();
		itemFact = new ItemFactory();

		render = new RenderAPIDelegate();

		ComparatorItemSymbolAlphabetical.instance.compare(null, null);
	}

	public synchronized static MystAPI getAPIInstance(String modId) {
		MystAPI instance = instances.get(modId);
		if (instance == null) {
			instance = createAPIInstance(modId);
			instances.put(modId, instance);
		}
		return instance;
	}

	private static MystAPI createAPIInstance(String modId) {
		MystAPIInstance mystAPI = new MystAPIInstance(modId);
		return mystAPI;
	}

}
