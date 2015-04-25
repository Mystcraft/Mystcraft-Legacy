package com.xcompwiz.mystcraft.api.impl.linking;

import java.util.Collection;
import java.util.Collections;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.linking.DimensionUtils;

public class DimensionAPIDelegate {

	public Collection<Integer> getAllAges() {
		return Collections.unmodifiableCollection(Mystcraft.registeredDims);
	}

	public boolean isMystcraftAge(int dimId) {
		return Mystcraft.registeredDims.contains(dimId);
	}

	public int createAge() {
		int dimId = DimensionUtils.getNewDimensionUID();
		DimensionUtils.createAge(dimId);
		return dimId;
	}

}
