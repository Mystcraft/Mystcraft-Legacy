package com.xcompwiz.mystcraft.villager;

import java.util.List;
import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

import com.xcompwiz.mystcraft.world.gen.structure.ComponentVillageArchivistHouse;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class VillageCreationHandlerArchivistHouse implements IVillageCreationHandler {

	// @Override
	// public StructureVillagePieceWeight getVillagePieceWeight(Random random, int i) {
	// return new StructureVillagePieceWeight(getComponentClass(), 20,
	// MathHelper.getRandomIntegerInRange(random, i, i+1));
	// }
	//
	// @Override
	// public Class<?> getComponentClass() {
	// return ComponentVillageArchivistHouse.class;
	// }
	//
	// @Override
	// public Object buildComponent(StructureVillagePieceWeight villagePiece, ComponentVillageStartPiece startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
	// return ComponentVillageArchivistHouse.buildComponent(startPiece, pieces,
	// random, p1, p2, p3, p4, p5);
	// }

	@Override
	public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
		return ComponentVillageArchivistHouse.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
	}

	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i) {
		return new PieceWeight(getComponentClass(), 20, MathHelper.getRandomIntegerInRange(random, i, i + 1));
	}

	@Override
	public Class<? extends net.minecraft.world.gen.structure.StructureComponent> getComponentClass() {
		return ComponentVillageArchivistHouse.class;
	}

}
