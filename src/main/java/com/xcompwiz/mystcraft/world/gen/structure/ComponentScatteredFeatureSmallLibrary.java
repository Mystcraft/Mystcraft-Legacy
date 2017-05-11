package com.xcompwiz.mystcraft.world.gen.structure;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.block.BlockLectern;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;

import com.xcompwiz.mystcraft.treasure.LootTableHandler;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public class ComponentScatteredFeatureSmallLibrary extends ComponentScatteredFeatureMyst {

	private int						averageGroundLevel	= -1; //Used to track the library generation height
	private boolean					hasMadeChest;

	private boolean[]				lecternGenned	= new boolean[5];
	private static final int[][]	lecternCoords	= new int[][] { { 6, 2, 2 }, { 8, 2, 4 }, { 8, 2, 5 }, { 8, 2, 6 }, { 6, 2, 8 } };
	private static final int[][]	lecternTargs	= new int[][] { { 6, 3 }, { 7, 4 }, { 7, 5 }, { 7, 6 }, { 6, 7 } };

	public ComponentScatteredFeatureSmallLibrary() {}

	public ComponentScatteredFeatureSmallLibrary(Random par1Random, int x, int z) {
		super(par1Random, x, 64, z, 11, 10, 11);
	}

	@Override
	protected void writeStructureToNBT(NBTTagCompound tagCompound) {
		super.writeStructureToNBT(tagCompound);
		tagCompound.setBoolean("Chest", this.hasMadeChest);
		for (int i = 0; i < lecternGenned.length; ++i) {
			tagCompound.setBoolean("hasPlacedLectern" + i, this.lecternGenned[i]);
		}
		tagCompound.setInteger("YPos", this.averageGroundLevel);
	}

	@Override
	protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
		super.readStructureFromNBT(tagCompound, p_143011_2_);
		this.hasMadeChest = tagCompound.getBoolean("Chest");
		for (int i = 0; i < lecternGenned.length; ++i) {
			if (!tagCompound.hasKey("hasPlacedLectern" + i)) continue;
			lecternGenned[i] = tagCompound.getBoolean("hasPlacedLectern" + i);
		}
		this.averageGroundLevel = tagCompound.getInteger("YPos");
	}

	/**
	 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the end, it adds Fences...
	 */
	@Override
	public boolean addComponentParts(World worldObj, Random rand, StructureBoundingBox boundingbox) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(worldObj, boundingbox);
			if (this.averageGroundLevel < 0) { return false; }
			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 10, 0);
		}

		// 0 - Towards front
		// 1 - Towards back
		// 2 - Towards Left
		// 3 - Towards Right
		// 4 - Up Towards Right
		// 5 - Up Towards Left
		// 6 - Up Towards Front
		// 7 - Up Towards Back
		EnumFacing var0 = getStairRotation(EnumFacing.NORTH);
		EnumFacing var1 = getStairRotation(EnumFacing.WEST);
		EnumFacing var2 = getStairRotation(EnumFacing.EAST);
		EnumFacing var3 = getStairRotation(EnumFacing.SOUTH);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 0, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 1, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 2, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 3, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 3, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 5, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 5, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 5, 8, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 6, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 4, 6, 8, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 5, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 5, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 6, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 6, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 6, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 5, 6, 8, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 5, 2, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 5, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 5, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 6, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 6, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 6, 6, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 5, 2, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 5, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 5, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 6, 3, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 6, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 6, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 6, 6, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 7, 6, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 8, 5, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 8, 6, 2, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 8, 6, 4, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 8, 6, 5, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 8, 6, 7, boundingbox);
		setBlockState(worldObj, Blocks.AIR.getDefaultState(), 8, 6, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 4, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 4, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 4, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 4, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 4, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 4, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 4, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 4, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 5, 1, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 5, 1, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 5, 2, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 5, 2, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 5, 3, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 5, 3, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 5, 4, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 5, 4, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 6, 1, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 6, 1, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 6, 3, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 6, 3, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 6, 4, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 6, 4, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 7, 1, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 7, 1, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 7, 2, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 7, 2, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 7, 3, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 7, 3, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 7, 4, 2, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 7, 4, 8, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 8, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 1, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 1, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 1, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 2, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 2, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 2, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 2, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 2, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 2, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 2, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 2, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 2, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 2, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 2, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 3, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 3, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 3, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 3, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 4, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 5, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 6, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 3, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 2, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 2, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 4, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 4, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 5, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 5, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 6, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 6, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 8, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 8, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 4, 8, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 2, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 2, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 4, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 4, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 5, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 5, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 6, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 6, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 8, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 8, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 8, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 5, 9, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 2, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 2, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 4, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 4, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 5, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 5, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 6, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 6, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 8, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 8, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 8, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 6, 9, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 1, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 1, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 2, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 2, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 3, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 3, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 4, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 4, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 5, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 5, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 6, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 8, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 8, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 8, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 9, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 1, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 1, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 2, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 2, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 3, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 3, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 4, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 4, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 5, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 5, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 6, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 6, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 8, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 8, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 8, 8, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 1, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 2, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 3, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 4, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 5, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 6, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 9, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE.getDefaultState(), 7, 6, 9, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 4, 1, 8, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 4, 2, 2, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 4, 2, 8, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 4, 3, 2, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 4, 3, 8, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 4, 4, 2, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 4, 4, 8, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 8, 1, 2, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 8, 1, 8, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 8, 2, 2, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 8, 2, 8, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 8, 3, 2, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 8, 3, 8, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 8, 4, 2, boundingbox);
		setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 8, 4, 8, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 0, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 0, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 0, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 2, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 2, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 2, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 2, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 2, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 2, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 3, 8, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 3, 8, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 3, 8, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 4, 9, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 4, 9, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 4, 9, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0), 8, 9, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 8, 9, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 9, 8, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 9, 8, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 9, 8, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 9, 8, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 1, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 2, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 8, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 9, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1), 10, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 0, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 1, 0, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 1, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 1, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 2, 4, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 2, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 3, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 3, 8, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 4, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 4, 8, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 5, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 5, 8, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 5, 9, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 6, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 6, 8, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 6, 9, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 7, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 7, 8, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 7, 9, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 8, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 8, 8, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 9, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 9, 8, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2), 10, 0, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 0, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 1, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 1, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 1, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 2, 0, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 2, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 2, 4, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 2, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 3, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 3, 8, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 4, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 4, 8, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 5, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 5, 8, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 5, 9, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 6, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 6, 8, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 6, 9, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 7, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 7, 8, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 7, 9, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 8, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 8, 8, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 8, 9, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3), 9, 0, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var0).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 1, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 2, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 3, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 7, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 8, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var1).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 9, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 2, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 3, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 4, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 5, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 6, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 7, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 8, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var2).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 9, 7, 0, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 3, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 4, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 5, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 6, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 7, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 8, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 9, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, var3).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 10, 7, 10, boundingbox);
		setBlockState(worldObj, Blocks.STONE_SLAB.getStateFromMeta(3),  1, 4, 4, boundingbox);
		setBlockState(worldObj, Blocks.STONE_SLAB.getStateFromMeta(3),  1, 4, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_SLAB.getStateFromMeta(3),  1, 4, 6, boundingbox);
		setBlockState(worldObj, Blocks.STONE_SLAB.getStateFromMeta(11), 1, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_SLAB.getStateFromMeta(11), 3, 3, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_SLAB.getStateFromMeta(3),  5, 10, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_SLAB.getStateFromMeta(3),  6, 10, 5, boundingbox);
		setBlockState(worldObj, Blocks.STONE_SLAB.getStateFromMeta(3),  7, 10, 5, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 4, 5, 2, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 4, 6, 2, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 4, 6, 3, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 4, 6, 4, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 4, 6, 5, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 4, 6, 6, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 5, 5, 2, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 5, 5, 8, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 5, 6, 2, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 5, 6, 3, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 5, 6, 4, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 6, 5, 8, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 6, 6, 2, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 6, 6, 3, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 6, 6, 7, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 6, 6, 8, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 7, 5, 8, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 7, 6, 2, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 7, 6, 8, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 8, 5, 2, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 8, 5, 3, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 8, 5, 4, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 8, 5, 5, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 8, 5, 6, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 8, 5, 8, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 8, 6, 3, boundingbox);
		setBlockState(worldObj, Blocks.WEB.getDefaultState(), 8, 6, 6, boundingbox);

		for (int i = 0; i <= this.scatteredFeatureSizeX; ++i) {
			for (int j = 0; j <= this.scatteredFeatureSizeZ; ++j) {
				this.replaceAirAndLiquidDownwards(worldObj, Blocks.COBBLESTONE.getDefaultState(), i, -1, j, boundingbox);
			}
		}
		this.fillWithBlocks(worldObj, boundingbox, 0, -1, 0, scatteredFeatureSizeX, -1, scatteredFeatureSizeY, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);

		setBlockState(worldObj, Blocks.COBBLESTONE_WALL.getDefaultState(), 1, 1, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE_WALL.getDefaultState(), 1, 1, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE_WALL.getDefaultState(), 1, 2, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE_WALL.getDefaultState(), 1, 2, 6, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE_WALL.getDefaultState(), 1, 3, 4, boundingbox);
		setBlockState(worldObj, Blocks.COBBLESTONE_WALL.getDefaultState(), 1, 3, 6, boundingbox);

		this.hasMadeChest = this.generateChest(worldObj, boundingbox, rand, 4, 1, 2, LootTableHandler.MYST_TREASURE);

		for (int i = 0; i < this.lecternGenned.length; ++i) {
			this.lecternGenned[i] = this.generateStructureLectern(worldObj, boundingbox, rand, lecternCoords[i][0], lecternCoords[i][1], lecternCoords[i][2], lecternTargs[i][0], lecternTargs[i][1]);
		}

		return true;
	}

	//Duh. copied from 1.7.10 :^)
	private EnumFacing getStairRotation(EnumFacing original) {
		if (getCoordBaseMode() == null) return original;
		switch (getCoordBaseMode()) {
			case NORTH:
				if (original == EnumFacing.SOUTH) {
					return EnumFacing.EAST;
				}
				if (original == EnumFacing.EAST) {
					return EnumFacing.SOUTH;
				}
				break;
			case WEST:
				if (original == EnumFacing.NORTH) {
					return EnumFacing.SOUTH;
				}
				if (original == EnumFacing.WEST) {
					return EnumFacing.EAST;
				}
				if (original == EnumFacing.SOUTH) {
					return EnumFacing.NORTH;
				}
				if (original == EnumFacing.EAST) {
					return EnumFacing.WEST;
				}
				break;
			case EAST:
				if (original == EnumFacing.NORTH) {
					return EnumFacing.SOUTH;
				}
				if (original == EnumFacing.WEST) {
					return EnumFacing.EAST;
				}
				if (original == EnumFacing.SOUTH) {
					return EnumFacing.WEST;
				}
				if (original == EnumFacing.EAST) {
					return EnumFacing.NORTH;
				}
				break;
		}
		return original;
	}

	protected boolean generateStructureLectern(World worldObj, StructureBoundingBox boundingbox, Random rand, int i, int j, int k, int i2, int k2) {
		int ti = this.getXWithOffset(i, k);
		int tj = this.getYWithOffset(j);
		int tk = this.getZWithOffset(i, k);

		int ti2 = this.getXWithOffset(i2, k2);
		int tk2 = this.getZWithOffset(i2, k2);

		BlockPos at = new BlockPos(ti, tj, tk);
		if (boundingbox.isVecInside(at)) {
			EnumFacing horizontal = EnumFacing.fromAngle(360 - getRotation(ti, tk, ti2, tk2) + 270); //todo Hellfire> check rotation of lecterns in villages
			worldObj.setBlockState(at, ModBlocks.lectern.getDefaultState().withProperty(BlockLectern.ROTATION, horizontal));
			TileEntityLectern lectern = (TileEntityLectern) worldObj.getTileEntity(at);

			if (lectern != null) {
				lectern.setYaw(360 - getRotation(ti, tk, ti2, tk2) + 270);
				if (rand.nextFloat() < 0.6F) {
					//This is gonna be stupid, but we have to roll in advance otherwise we can't render it until you "open" the lectern
					//which is awkward aswell... so whatever.. generating..
					LootTable lootTable = worldObj.getLootTableManager().getLootTableFromLocation(LootTableHandler.MYST_TREASURE);
					LootContext lootContext = new LootContext.Builder((WorldServer)worldObj).build();
					List<ItemStack> result = lootTable.generateLootForPools(rand, lootContext);
					Collections.shuffle(result, rand);

					for (ItemStack stack : result) {
						if(lectern.canAcceptItem(0, stack)) {
							lectern.setBook(stack);
							break;
						}
					}
				}
			}

			return true;
		}
		return false;
	}

	public static int getRotation(int i, int k, int i2, int k2) {
		int deltaX = i2 - i;
		int deltaZ = -(k2 - k);
		if (deltaZ == 0) return (deltaX < 0 ? 180 : 0);
		if (deltaX == 0) return (deltaZ < 0 ? 270 : 90);
		float f = (float) deltaZ / (float) deltaX;
		return (int) (Math.atan(f) * 180 / Math.PI);
	}
}
