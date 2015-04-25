package com.xcompwiz.mystcraft.api.impl.linking;

import java.util.Collection;
import java.util.Collections;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.linking.IDimensionAPI;
import com.xcompwiz.mystcraft.linking.DimensionUtils;

public class DimensionAPIDelegate implements IDimensionAPI {

	@Override
	public Collection<Integer> getAllAges() {
		return Collections.unmodifiableCollection(Mystcraft.registeredDims);
	}

	@Override
	public boolean isMystcraftAge(int dimId) {
		return Mystcraft.registeredDims.contains(dimId);
	}

	@Override
	public int createAge() {
		int dimId = DimensionUtils.getNewDimensionUID();
		DimensionUtils.createAge(dimId);
		return dimId;
	}

}
