package com.xcompwiz.mystcraft.world.gen.structure;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureScatteredFeatureStartMyst extends StructureStart {
	public StructureScatteredFeatureStartMyst() {}

	public StructureScatteredFeatureStartMyst(World worldObj, Random random, int chunkX, int chunkZ) {
		//TODO: (Structures) Revise this to allow for variable scattered features
		ComponentScatteredFeatureSmallLibrary library = new ComponentScatteredFeatureSmallLibrary(random, chunkX * 16, chunkZ * 16);
		this.components.add(library);

		this.updateBoundingBox();
	}
}
