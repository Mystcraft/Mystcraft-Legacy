package com.xcompwiz.mystcraft.api.impl.linking;

import java.util.Collection;

import com.xcompwiz.mystcraft.api.hook.DimensionAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

public class DimensionAPIWrapper extends APIWrapper implements DimensionAPI {

	public DimensionAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public Collection<Integer> getAllAges() {
		return InternalAPI.dimension.getAllAges();
	}

	@Override
	public boolean isMystcraftAge(int dimId) {
		return InternalAPI.dimension.isMystcraftAge(dimId);
	}

	@Override
	public int createAge() {
		return InternalAPI.dimension.createAge();
	}
}
