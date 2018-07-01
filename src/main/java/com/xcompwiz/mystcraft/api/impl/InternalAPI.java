package com.xcompwiz.mystcraft.api.impl;

import java.util.HashMap;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.impl.client.RenderAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.grammar.GrammarAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.instability.InstabilityAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.instability.InstabilityFactoryImpl;
import com.xcompwiz.mystcraft.api.impl.item.ItemFactoryImpl;
import com.xcompwiz.mystcraft.api.impl.linking.DimensionAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.linking.LinkingAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.page.PageAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.symbol.SymbolAPIDelegate;
import com.xcompwiz.mystcraft.api.impl.symbol.SymbolFactoryImpl;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.page.SortingUtils.ComparatorItemSymbolAlphabetical;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class InternalAPI {

	public static DimensionAPIDelegate dimension;
	public static LinkingAPIDelegate linking;
	public static LinkingAPIDelegate linkProperties;
	public static InstabilityAPIDelegate instability;
	public static SymbolAPIDelegate symbol;
	public static SymbolAPIDelegate word;
	public static SymbolAPIDelegate symbolValues;
	public static GrammarAPIDelegate grammar;
	public static PageAPIDelegate page;
	public static RenderAPIDelegate render;

	public static InstabilityFactoryImpl instabilityFact;
	public static SymbolFactoryImpl symbolFact;
	public static ItemFactoryImpl itemFact;

	public static class ProviderGiver implements APIInstanceProvider.EntryPoint {

		@Override
		public APIInstanceProvider getProviderInstance() {
			return InternalAPI.getAPIProviderInstance();
		}

	}

	public static void initAPI() {
		LinkingAPIDelegate linking_delegate = new LinkingAPIDelegate();
		dimension = new DimensionAPIDelegate();
		linking = linking_delegate;
		linkProperties = linking_delegate;

		instability = new InstabilityAPIDelegate();
		instabilityFact = new InstabilityFactoryImpl();

		SymbolAPIDelegate symbol_delegate = new SymbolAPIDelegate();
		symbol = symbol_delegate;
		word = symbol_delegate;
		symbolValues = symbol_delegate;
		grammar = new GrammarAPIDelegate();
		symbolFact = new SymbolFactoryImpl();

		page = new PageAPIDelegate();
		itemFact = new ItemFactoryImpl();

		render = new RenderAPIDelegate();

		//TODO: Why do I do this?
		ComparatorItemSymbolAlphabetical.instance.compare(ItemStack.EMPTY, ItemStack.EMPTY);

		MystObjects.entryPoint = new ProviderGiver();

		APIProviderImpl.init();
	}

	private static HashMap<String, APIInstanceProvider> instances = new HashMap<String, APIInstanceProvider>();

	public synchronized static APIInstanceProvider getAPIProviderInstance() {
		String modId = "ERROR:Unknown";
		Loader loader = Loader.instance();
		ModContainer container = null;
		if (loader != null)
			container = loader.activeModContainer();
		if (container != null)
			modId = container.getModId();
		else
			LoggerUtils.warn("Creating API Provider outside of an active Mod Container; unknown mod ownership.");
		APIInstanceProvider instance = instances.get(modId);
		if (instance == null) {
			instance = new APIProviderImpl(modId);
			instances.put(modId, instance);
		}
		return instance;
	}
}
