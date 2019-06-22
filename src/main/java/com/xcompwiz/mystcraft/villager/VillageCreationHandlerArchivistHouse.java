package com.xcompwiz.mystcraft.villager;

import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.world.gen.structure.ComponentVillageArchivistHouse;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class VillageCreationHandlerArchivistHouse implements IVillageCreationHandler {

	@Override
	public StructureVillagePieces.Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int p5) {
		return ComponentVillageArchivistHouse.buildComponent(startPiece, pieces, random, x, y, z, facing, p5);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i) {
		return new PieceWeight((Class<? extends StructureVillagePieces.Village>) getComponentClass(), 20, MathHelper.getInt(random, i, i + 1));
	}

	@Override
	public Class<?> getComponentClass() {
		return ComponentVillageArchivistHouse.class;
	}

}
