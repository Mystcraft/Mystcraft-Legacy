package com.xcompwiz.mystcraft.api.impl;

import java.util.HashMap;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.exception.APIUndefined;
import com.xcompwiz.mystcraft.api.exception.APIVersionRemoved;
import com.xcompwiz.mystcraft.api.exception.APIVersionUndefined;
import com.xcompwiz.mystcraft.api.impl.client.RenderAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.grammar.GrammarAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.instability.InstabilityAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.instability.InstabilityFactWrapper;
import com.xcompwiz.mystcraft.api.impl.item.ItemFactAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.linking.DimensionAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.linking.LinkPropertyAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.linking.LinkingAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.page.PageAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.symbol.SymbolAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.symbol.SymbolFactoryAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.symbol.SymbolValuesAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.word.WordAPIWrapper;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class APIProviderImpl implements APIInstanceProvider {
	private String	modname;

	public APIProviderImpl(String modname) {
		this.modname = modname;
	}

	public String getOwnerMod() {
		return modname;
	}

	private HashMap<String, Object>	instances	= new HashMap<String, Object>();

	@Override
	public synchronized Object getAPIInstance(String api) throws APIUndefined, APIVersionUndefined, APIVersionRemoved {
		Object ret = instances.get(api);
		if (ret != null) return ret;
		String[] splitName = api.split("-");
		if (splitName.length != 2) throw new APIUndefined(api);
		String apiname = splitName[0];
		int version = Integer.parseInt(splitName[1]);
		ret = constructAPIWrapper(modname, apiname, version);
		instances.put(api, ret);
		return ret;
	}

	private static HashMap<String, HashMap<Integer, WrapperBuilder>>	apiCtors;

	public static void init() {
		if (apiCtors != null) return;
		apiCtors = new HashMap<String, HashMap<Integer, WrapperBuilder>>();

		getCtors("dimension").put(1, new WrapperBuilder(DimensionAPIWrapper.class));
		getCtors("grammar").put(1, new WrapperBuilder(GrammarAPIWrapper.class));
		getCtors("instability").put(1, new WrapperBuilder(InstabilityAPIWrapper.class));
		getCtors("instabilityfact").put(1, new WrapperBuilder(InstabilityFactWrapper.class));
		getCtors("itemfact").put(1, new WrapperBuilder(ItemFactAPIWrapper.class));
		getCtors("linking").put(1, new WrapperBuilder(LinkingAPIWrapper.class));
		getCtors("linkingprop").put(1, new WrapperBuilder(LinkPropertyAPIWrapper.class));
		getCtors("page").put(1, new WrapperBuilder(PageAPIWrapper.class));
		getCtors("render").put(1, new WrapperBuilder(RenderAPIWrapper.class));
		getCtors("symbol").put(1, new WrapperBuilder(SymbolAPIWrapper.class));
		getCtors("symbolfact").put(1, new WrapperBuilder(SymbolFactoryAPIWrapper.class));
		getCtors("symbolvals").put(1, new WrapperBuilder(SymbolValuesAPIWrapper.class));
		getCtors("word").put(1, new WrapperBuilder(WordAPIWrapper.class));
	}

	private static HashMap<Integer, WrapperBuilder> getCtors(String apiname) {
		HashMap<Integer, WrapperBuilder> ctors = apiCtors.get(apiname);
		if (ctors == null) {
			ctors = new HashMap<Integer, WrapperBuilder>();
			apiCtors.put(apiname, ctors);
		}
		return ctors;
	}

	private static Object constructAPIWrapper(String owner, String apiname, int version) throws APIUndefined, APIVersionUndefined, APIVersionRemoved {
		if (apiCtors == null) throw new RuntimeException("Something is broken. The Mystcraft API Provider hasn't constructed properly.");
		HashMap<Integer, WrapperBuilder> ctors = apiCtors.get(apiname);
		if (ctors == null) throw new APIUndefined(apiname);
		if (!ctors.containsKey(version)) throw new APIVersionUndefined(apiname + "-" + version);
		WrapperBuilder ctor = ctors.get(version);
		if (ctor == null) throw new APIVersionRemoved(apiname + "-" + version);
		try {
			return ctor.newInstance(owner);
		} catch (Exception e) {
			LoggerUtils.error("Caught an exception while building an API wrapper. Go kick XCompWiz.");
			throw new RuntimeException("Caught an exception while building an API wrapper. Go kick XCompWiz.", e);
		}
	}
}
