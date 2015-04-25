package com.xcompwiz.mystcraft.api.impl;

import java.util.HashMap;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.exception.APIUndefined;
import com.xcompwiz.mystcraft.api.exception.APIVersionRemoved;
import com.xcompwiz.mystcraft.api.exception.APIVersionUndefined;
import com.xcompwiz.mystcraft.api.impl.client.RenderAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.grammar.GrammarAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.instability.InstabilityAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.linking.LinkingAPIWrapper;
import com.xcompwiz.mystcraft.api.impl.symbol.SymbolAPIWrapper;
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
		getCtors("linking").put(1, new WrapperBuilder(LinkingAPIWrapper.class));
		getCtors("render").put(1, new WrapperBuilder(RenderAPIWrapper.class));
		getCtors("grammar").put(1, new WrapperBuilder(GrammarAPIWrapper.class));
		getCtors("instability").put(1, new WrapperBuilder(InstabilityAPIWrapper.class));
		getCtors("symbol").put(1, new WrapperBuilder(SymbolAPIWrapper.class));
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
