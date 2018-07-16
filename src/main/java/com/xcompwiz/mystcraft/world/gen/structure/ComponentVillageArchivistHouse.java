package com.xcompwiz.mystcraft.world.gen.structure;

import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.block.BlockLectern;
import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;
import com.xcompwiz.mystcraft.treasure.LootTableHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class ComponentVillageArchivistHouse extends StructureVillagePieces.Village {

	private int averageGroundLevel = -1;                                        // Used to shift the location of the bounding box on gen

	private boolean[] lecternGenned = new boolean[2];
	private static final int[][] lecternCoords = new int[][] { { 1, 2, 1 }, { 1, 2, 2 } };
	private static final int[][] lecternTargs = new int[][] { { 2, 1 }, { 2, 2 } };

	public ComponentVillageArchivistHouse() {}

	public ComponentVillageArchivistHouse(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox box, EnumFacing face) {
		super(start, type);
		this.setCoordBaseMode(face);
		this.boundingBox = box;
	}

	// Used to generate the component
	public static ComponentVillageArchivistHouse buildComponent(StructureVillagePieces.Start start, List<StructureComponent> par1List, Random rand, int x, int y, int z, EnumFacing facing, int par7) {
		StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 12, 7, facing);
		return canVillageGoDeeper(var8) && StructureComponent.findIntersecting(par1List, var8) == null ? new ComponentVillageArchivistHouse(start, par7, rand, var8, facing) : null;
	}

	@Override
	protected void writeStructureToNBT(NBTTagCompound tagCompound) {
		super.writeStructureToNBT(tagCompound);
		for (int i = 0; i < lecternGenned.length; ++i) {
			tagCompound.setBoolean("hasPlacedLectern" + i, this.lecternGenned[i]);
		}
		tagCompound.setInteger("YPos", this.averageGroundLevel);
	}

	@Override
	protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
		super.readStructureFromNBT(tagCompound, p_143011_2_);
		for (int i = 0; i < lecternGenned.length; ++i) {
			if (!tagCompound.hasKey("hasPlacedLectern" + i))
				continue;
			lecternGenned[i] = tagCompound.getBoolean("hasPlacedLectern" + i);
		}
		this.averageGroundLevel = tagCompound.getInteger("YPos");
	}

	/**
	 * Generates the actual structure
	 */
	@Override
	public boolean addComponentParts(World worldObj, Random rand, StructureBoundingBox boundingbox) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(worldObj, boundingbox);
			if (this.averageGroundLevel < 0) {
				return true;
			}
			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 10, 0);
		}

		this.fillWithBlocks(worldObj, boundingbox, 1, 1, 1, 8, 6, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false); // Inner air
		this.fillWithBlocks(worldObj, boundingbox, 1, 0, 1, 7, 0, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false); // Floor
		this.fillWithBlocks(worldObj, boundingbox, 0, 0, 0, 0, 3, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false); // Side
		// wall
		this.fillWithBlocks(worldObj, boundingbox, 0, 0, 0, 7, 3, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false); // Front
		// wall
		this.fillWithBlocks(worldObj, boundingbox, 1, 2, 0, 7, 2, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false); // Block line

		this.fillWithBlocks(worldObj, boundingbox, 8, 0, 0, 8, 3, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false); // Windowed
		// Wall
		this.fillWithBlocks(worldObj, boundingbox, 8, 2, 1, 8, 2, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false); // Block line
		this.fillWithBlocks(worldObj, boundingbox, 8, 3, 2, 8, 3, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false); // Block line

		this.fillWithBlocks(worldObj, boundingbox, 0, 0, 6, 8, 3, 6, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false); // Back
		// Wall

		// Roof
		this.fillWithBlocks(worldObj, boundingbox, 0, 6, 3, 8, 6, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldObj, boundingbox, 0, 5, 2, 8, 5, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldObj, boundingbox, 0, 4, 1, 8, 4, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldObj, boundingbox, 2, 4, 2, 6, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false); // Inner air

		for (int i = -1; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				setBlockState(worldObj, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), j, 4 + i, i, boundingbox);
				setBlockState(worldObj, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH), j, 4 + i, 6 - i, boundingbox);
			}
		}

		// Windows
		this.setBlockState(worldObj, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 0, boundingbox);
		this.setBlockState(worldObj, Blocks.GLASS_PANE.getDefaultState(), 3, 2, 0, boundingbox);
		this.setBlockState(worldObj, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 2, boundingbox);
		this.setBlockState(worldObj, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 3, boundingbox);
		this.setBlockState(worldObj, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 4, boundingbox);

		// Door
		generateDoor(worldObj, boundingbox, rand, 6, 1, 0, EnumFacing.WEST, Blocks.OAK_DOOR);
		if (this.getBlockStateFromPos(worldObj, 6, 0, -1, boundingbox).getBlock() == Blocks.AIR && this.getBlockStateFromPos(worldObj, 6, -1, -1, boundingbox).getBlock() != Blocks.AIR) {
			this.setBlockState(worldObj, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST), 6, 0, -1, boundingbox);
		}

		// Internal
		this.placeTorch(worldObj, EnumFacing.NORTH, 1, 3, 1, boundingbox);
		this.placeTorch(worldObj, EnumFacing.NORTH, 6, 4, 2, boundingbox);
		this.placeTorch(worldObj, EnumFacing.SOUTH, 5, 3, 5, boundingbox);

		this.setBlockState(worldObj, Blocks.OAK_FENCE.getDefaultState(), 6, 1, 4, boundingbox);
		this.setBlockState(worldObj, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 6, 2, 4, boundingbox);
		this.setBlockState(worldObj, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), 6, 1, 5, boundingbox);
		this.setBlockState(worldObj, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), 7, 1, 5, boundingbox);
		this.setBlockState(worldObj, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST), 7, 1, 4, boundingbox);

		this.setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 1, 1, 1, boundingbox);
		this.setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 1, 1, 2, boundingbox);

		this.setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 1, 1, 3, boundingbox);
		this.setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 1, 2, 3, boundingbox);
		this.setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 1, 1, 4, boundingbox);
		this.setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 1, 2, 4, boundingbox);

		this.setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 1, 1, 5, boundingbox);
		this.setBlockState(worldObj, Blocks.PLANKS.getDefaultState(), 1, 2, 5, boundingbox);

		this.setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 2, 1, 5, boundingbox);
		this.setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 2, 2, 5, boundingbox);
		this.setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 3, 1, 5, boundingbox);
		this.setBlockState(worldObj, Blocks.BOOKSHELF.getDefaultState(), 3, 2, 5, boundingbox);

		if (Mystcraft.villageDeskGen) {
			this.placeDeskAt(worldObj, 4, 1, 1, 4, 2, boundingbox);
		}

		// Generates foundation and clears area above
		for (int i = 0; i < 7; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.clearCurrentPositionBlocksUpwards(worldObj, j, 7, i, boundingbox);
				this.replaceAirAndLiquidDownwards(worldObj, Blocks.COBBLESTONE.getDefaultState(), j, -1, i, boundingbox);
			}
		}

		// =====================
		// ==Generate Treasure==
		// =====================
		LootTable lootTable = worldObj.getLootTableManager().getLootTableFromLocation(LootTableHandler.MYST_TREASURE);
		LootContext lootContext = new LootContext.Builder((WorldServer)worldObj).build();

		for (int i = 0; i < this.lecternGenned.length; ++i) {
			if (!this.lecternGenned[i]) {
				this.lecternGenned[i] = this.generateStructureLectern(worldObj, boundingbox, rand, lecternCoords[i][0], lecternCoords[i][1], lecternCoords[i][2], lecternTargs[i][0], lecternTargs[i][1], lootTable, lootContext);
			}
		}
		// =====================
		// ==Generate Villager==
		// =====================
		if (Mystcraft.archivistEnabled()) {
			this.spawnVillagers(worldObj, boundingbox, 4, 2, 4, 1); // x,y,z villager count
		}
		return true;
	}

	//Duh. copied from 1.7.10 :^)
	private EnumFacing getStairRotation(EnumFacing original) {
		if (getCoordBaseMode() == null)
			return original;
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

	protected boolean generateStructureLectern(World worldObj, StructureBoundingBox boundingbox, Random rand, int i, int j, int k, int i2, int k2, LootTable lootTable, LootContext lootContext) {
		int ti = this.getXWithOffset(i, k);
		int tj = this.getYWithOffset(j);
		int tk = this.getZWithOffset(i, k);

		int ti2 = this.getXWithOffset(i2, k2);
		int tk2 = this.getZWithOffset(i2, k2);

		BlockPos at = new BlockPos(ti, tj, tk);
		if (boundingbox.isVecInside(at)) {
			EnumFacing horizontal = EnumFacing.fromAngle(360 - getRotation(ti, tk, ti2, tk2) + 90); //todo Hellfire> check rotation of lecterns in villages
			worldObj.setBlockState(at, ModBlocks.lectern.getDefaultState().withProperty(BlockLectern.ROTATION, horizontal));
			TileEntityLectern lectern = (TileEntityLectern) worldObj.getTileEntity(at);

			if (lectern != null) {
				lectern.setYaw(360 - getRotation(ti, tk, ti2, tk2) + 90);
				ItemStack item = LootTableHandler.generateLecternItem(lectern, rand, lootTable, lootContext);
				if (item != null)
					lectern.setBook(item);
			}

			return true;
		}
		return false;
	}

	public static int getRotation(int i, int k, int i2, int k2) {
		int deltaX = i2 - i;
		int deltaZ = -(k2 - k);
		if (deltaZ == 0)
			return (deltaX < 0 ? 180 : 0);
		if (deltaX == 0)
			return (deltaZ < 0 ? 270 : 90);
		float f = (float) deltaZ / (float) deltaX;
		return (int) (Math.atan(f) * 180 / Math.PI);
	}

	private void placeDeskAt(World worldObj, int i, int j, int k, int i2, int k2, StructureBoundingBox boundingbox) {
		Block block = ModBlocks.writingdesk;
		int ti = this.getXWithOffset(i, k);
		int tk = this.getZWithOffset(i, k);
		int ti2 = this.getXWithOffset(i2, k2);
		int tk2 = this.getZWithOffset(i2, k2);
		EnumFacing facing = BlockWritingDesk.getMetadataFromDirection(ti2 - ti, tk2 - tk);
		this.setBlockState(worldObj, block.getDefaultState().withProperty(BlockWritingDesk.ROTATION, facing), i, j, k, boundingbox);
		this.setBlockState(worldObj, block.getDefaultState().withProperty(BlockWritingDesk.ROTATION, facing).withProperty(BlockWritingDesk.IS_FOOT, true), i2, j, k2, boundingbox);
	}

	@Override
	protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof) {
		return Mystcraft.instance.archivist;
	}

}
