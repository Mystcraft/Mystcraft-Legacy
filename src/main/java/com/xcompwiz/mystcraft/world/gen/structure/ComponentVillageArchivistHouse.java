package com.xcompwiz.mystcraft.world.gen.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.common.ChestGenHooks;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;

public class ComponentVillageArchivistHouse extends StructureVillagePieces.Village {
	private int						averageGroundLevel	= -1;										// Used to shift the location of the bounding box on gen

	private boolean[]				lecternGenned		= new boolean[2];
	private static final int[][]	lecternCoords		= new int[][] { { 1, 2, 1 }, { 1, 2, 2 } };
	private static final int[][]	lecternTargs		= new int[][] { { 2, 1 }, { 2, 2 } };

	public ComponentVillageArchivistHouse() {}

	public ComponentVillageArchivistHouse(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5) {
		super(par1ComponentVillageStartPiece, par2);
		this.coordBaseMode = par5;
		this.boundingBox = par4StructureBoundingBox;
	}

	// Used to generate the component
	public static ComponentVillageArchivistHouse buildComponent(StructureVillagePieces.Start par0ComponentVillageStartPiece, List<StructureComponent> par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7) {
		StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 9, 12, 7, par6);
		return canVillageGoDeeper(var8) && StructureComponent.findIntersecting(par1List, var8) == null ? new ComponentVillageArchivistHouse(par0ComponentVillageStartPiece, par7, par2Random, var8, par6) : null;
	}

	@Override
	protected void func_143012_a(NBTTagCompound nbttagcompound) {
		super.func_143012_a(nbttagcompound);

		for (int i = 0; i < lecternGenned.length; ++i) {
			nbttagcompound.setBoolean("hasPlacedLectern" + i, this.lecternGenned[i]);
		}
		nbttagcompound.setInteger("YPos", this.averageGroundLevel);
	}

	@Override
	protected void func_143011_b(NBTTagCompound nbttagcompound) {
		super.func_143011_b(nbttagcompound);

		for (int i = 0; i < lecternGenned.length; ++i) {
			if (!nbttagcompound.hasKey("hasPlacedLectern" + i)) continue;
			lecternGenned[i] = nbttagcompound.getBoolean("hasPlacedLectern" + i);
		}
        this.averageGroundLevel = nbttagcompound.getInteger("YPos");
	}

	/**
	 * Generates the actual structure
	 */
	@Override
	public boolean addComponentParts(World worldObj, Random rand, StructureBoundingBox boundingbox) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(worldObj, boundingbox);
			if (this.averageGroundLevel < 0) { return true; }
			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 10, 0);
		}

		this.fillWithBlocks(worldObj, boundingbox, 1, 1, 1, 8, 6, 6, Blocks.air, Blocks.air, false); // Inner air
		this.fillWithBlocks(worldObj, boundingbox, 1, 0, 1, 7, 0, 5, Blocks.planks, Blocks.planks, false); // Floor
		this.fillWithBlocks(worldObj, boundingbox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false); // Side
																														// wall
		this.fillWithBlocks(worldObj, boundingbox, 0, 0, 0, 7, 3, 0, Blocks.cobblestone, Blocks.cobblestone, false); // Front
																														// wall
		this.fillWithBlocks(worldObj, boundingbox, 1, 2, 0, 7, 2, 0, Blocks.planks, Blocks.planks, false); // Block line

		this.fillWithBlocks(worldObj, boundingbox, 8, 0, 0, 8, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false); // Windowed
																														// Wall
		this.fillWithBlocks(worldObj, boundingbox, 8, 2, 1, 8, 2, 5, Blocks.planks, Blocks.planks, false); // Block line
		this.fillWithBlocks(worldObj, boundingbox, 8, 3, 2, 8, 3, 4, Blocks.planks, Blocks.planks, false); // Block line

		this.fillWithBlocks(worldObj, boundingbox, 0, 0, 6, 8, 3, 6, Blocks.cobblestone, Blocks.cobblestone, false); // Back
																														// Wall

		// Roof
		this.fillWithBlocks(worldObj, boundingbox, 0, 6, 3, 8, 6, 3, Blocks.planks, Blocks.planks, false);
		this.fillWithBlocks(worldObj, boundingbox, 0, 5, 2, 8, 5, 4, Blocks.planks, Blocks.planks, false);
		this.fillWithBlocks(worldObj, boundingbox, 0, 4, 1, 8, 4, 5, Blocks.planks, Blocks.planks, false);
		this.fillWithBlocks(worldObj, boundingbox, 2, 4, 2, 6, 4, 4, Blocks.air, Blocks.air, false); // Inner air
		int stairf = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
		int stairb = this.getMetadataWithOffset(Blocks.oak_stairs, 2);
		for (int i = -1; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.placeBlockAtCurrentPosition(worldObj, Blocks.oak_stairs, stairf, j, 4 + i, i, boundingbox);
				this.placeBlockAtCurrentPosition(worldObj, Blocks.oak_stairs, stairb, j, 4 + i, 6 - i, boundingbox);
			}
		}

		// Windows
		this.placeBlockAtCurrentPosition(worldObj, Blocks.glass_pane, 0, 2, 2, 0, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.glass_pane, 0, 3, 2, 0, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.glass_pane, 0, 8, 2, 2, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.glass_pane, 0, 8, 2, 3, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.glass_pane, 0, 8, 2, 4, boundingbox);

		// Door
		this.placeDoorAtCurrentPosition(worldObj, boundingbox, rand, 6, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
		if (this.getBlockAtCurrentPosition(worldObj, 6, 0, -1, boundingbox) == Blocks.air && this.getBlockAtCurrentPosition(worldObj, 6, -1, -1, boundingbox) != Blocks.air) {
			this.placeBlockAtCurrentPosition(worldObj, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 6, 0, -1, boundingbox);
		}

		// Internal
		this.placeBlockAtCurrentPosition(worldObj, Blocks.torch, 0, 1, 3, 2, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.torch, 0, 6, 3, 1, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.torch, 0, 5, 3, 5, boundingbox);

		this.placeBlockAtCurrentPosition(worldObj, Blocks.fence, 0, 6, 1, 4, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.wooden_pressure_plate, 0, 6, 2, 4, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 3), 6, 1, 5, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 3), 7, 1, 5, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 0), 7, 1, 4, boundingbox);

		this.placeBlockAtCurrentPosition(worldObj, Blocks.planks, 0, 1, 1, 1, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.planks, 0, 1, 1, 2, boundingbox);

		this.placeBlockAtCurrentPosition(worldObj, Blocks.bookshelf, 0, 1, 1, 3, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.bookshelf, 0, 1, 2, 3, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.bookshelf, 0, 1, 1, 4, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.bookshelf, 0, 1, 2, 4, boundingbox);

		this.placeBlockAtCurrentPosition(worldObj, Blocks.planks, 0, 1, 1, 5, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.planks, 0, 1, 2, 5, boundingbox);

		this.placeBlockAtCurrentPosition(worldObj, Blocks.bookshelf, 0, 2, 1, 5, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.bookshelf, 0, 2, 2, 5, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.bookshelf, 0, 3, 1, 5, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, Blocks.bookshelf, 0, 3, 2, 5, boundingbox);

		if (Mystcraft.villageDeskGen) {
			this.placeDeskAt(worldObj, 4, 1, 2, 4, 1, boundingbox);
		}

		// Generates foundation and clears area above
		for (int i = 0; i < 7; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.clearCurrentPositionBlocksUpwards(worldObj, j, 7, i, boundingbox);
				this.func_151554_b(worldObj, Blocks.cobblestone, 0, j, -1, i, boundingbox);
			}
		}

		// =====================
		// ==Generate Treasure==
		// =====================
		ChestGenHooks info = ChestGenHooks.getInfo(MystObjects.MYST_TREASURE);
		for (int i = 0; i < this.lecternGenned.length; ++i) {
			if (!this.lecternGenned[i]) {
				this.lecternGenned[i] = this.generateStructureLectern(worldObj, boundingbox, rand, lecternCoords[i][0], lecternCoords[i][1], lecternCoords[i][2], lecternTargs[i][0], lecternTargs[i][1], info);
			}
		}
		// =====================
		// ==Generate Villager==
		// =====================
		if (Mystcraft.archivistEnabled()) this.spawnVillagers(worldObj, boundingbox, 4, 2, 4, 1); // x,y,z villager count
		return true;
	}

	protected boolean generateStructureLectern(World worldObj, StructureBoundingBox boundingbox, Random rand, int i, int j, int k, int i2, int k2, ChestGenHooks info) {
		int ti = this.getXWithOffset(i, k);
		int tj = this.getYWithOffset(j);
		int tk = this.getZWithOffset(i, k);

		int ti2 = this.getXWithOffset(i2, k2);
		int tk2 = this.getZWithOffset(i2, k2);

		if (boundingbox.isVecInside(ti, tj, tk)) {
			worldObj.setBlock(ti, tj, tk, ModBlocks.lectern);
			TileEntityLectern lectern = (TileEntityLectern) worldObj.getTileEntity(ti, tj, tk);

			if (lectern != null) {
				lectern.setYaw(360 - getRotation(ti, tk, ti2, tk2) + 270);
				if (rand.nextFloat() < 0.6F) {
					ItemStack item = null;
					while (!lectern.isItemValidForSlot(0, item)) {
						item = info.getOneItem(rand);
						if (item.getCount() > 0) item.stackSize = 1;
					}
					lectern.setBook(item);
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

	private void placeDeskAt(World worldObj, int i, int j, int k, int i2, int k2, StructureBoundingBox boundingbox) {
		Block block = ModBlocks.writingdesk;
		int ti = this.getXWithOffset(i, k);
		int tk = this.getZWithOffset(i, k);
		int ti2 = this.getXWithOffset(i2, k2);
		int tk2 = this.getZWithOffset(i2, k2);
		int facing = BlockWritingDesk.getMetadataFromDirection(ti2 - ti, tk2 - tk);
		this.placeBlockAtCurrentPosition(worldObj, block, facing, i, j, k, boundingbox);
		this.placeBlockAtCurrentPosition(worldObj, block, facing + 8, i2, j, k2, boundingbox);
	}

	/**
	 * Returns the villager type to spawn in this component, based on the number of villagers already spawned.
	 */
	@Override
	protected int getVillagerType(int par1) {
		return Mystcraft.archivistId;
	}
}
