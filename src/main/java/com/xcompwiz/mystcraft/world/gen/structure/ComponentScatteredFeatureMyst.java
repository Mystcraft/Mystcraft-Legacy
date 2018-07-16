package com.xcompwiz.mystcraft.world.gen.structure;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;

abstract class ComponentScatteredFeatureMyst extends StructureComponent {

	/** The position of the bounding box for this feature in the X axis */
	protected int width;
	/** The position of the bounding box for this feature in the Y axis */
	protected int height;
	/** The position of the bounding box for this feature in the Z axis */
	protected int depth;

	protected int horizontalPos = -1;

	public ComponentScatteredFeatureMyst() {}

	protected ComponentScatteredFeatureMyst(Random par1Random, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
		super(0);
		this.width = sizeX;
		this.height = sizeY;
		this.depth = sizeZ;
		setCoordBaseMode(EnumFacing.HORIZONTALS[par1Random.nextInt(EnumFacing.HORIZONTALS.length)]);

		switch (getCoordBaseMode()) {
		case NORTH:
		case SOUTH:
			this.boundingBox = new StructureBoundingBox(x, y, z, x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1);
			break;
		default:
			this.boundingBox = new StructureBoundingBox(x, y, z, x + sizeZ - 1, y + sizeY - 1, z + sizeX - 1);
		}
	}

	//HellFire> who came up with the naming of these to be width, height, depth ??...
	@Override
	protected void writeStructureToNBT(NBTTagCompound tagCompound) {
		tagCompound.setInteger("Width", this.width);
		tagCompound.setInteger("Height", this.height);
		tagCompound.setInteger("Depth", this.depth);
		tagCompound.setInteger("HPos", this.horizontalPos);
	}

	@Override
	protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
		this.width = tagCompound.getInteger("Width");
		this.height = tagCompound.getInteger("Height");
		this.depth = tagCompound.getInteger("Depth");
		this.horizontalPos = tagCompound.getInteger("HPos");
	}
	
	public BlockPos getMappedBlockPos(int localX, int localY, int localZ) {
		int x = this.getXWithOffset(localX, localZ);
		int y = this.getYWithOffset(localY);
		int z = this.getZWithOffset(localX, localZ);
		return new BlockPos(x, y, z);
	}

	/**
	 * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox. (A median of all the levels in the BB's horizontal rectangle).
	 */
	protected int getAverageGroundLevel(World worldObj, StructureBoundingBox boundingbox) {
		int i = 0;
		int j = 0;

		for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k) {
			for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l) {
				if (boundingbox.isVecInside(new Vec3i(l, 64, k))) {
					i += Math.max(worldObj.getTopSolidOrLiquidBlock(new BlockPos(l, 0, k)).getY(), worldObj.provider.getAverageGroundLevel());
					++j;
				}
			}
		}

		if (j == 0) {
			return -1;
		}
		return i / j;
	}

	protected boolean shouldBuildHere(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3) {
		if (this.horizontalPos >= 0)
			return true;
		int var4 = 0;
		int var5 = 0;

		for (int var6 = this.boundingBox.minZ; var6 <= this.boundingBox.maxZ; ++var6) {
			for (int var7 = this.boundingBox.minX; var7 <= this.boundingBox.maxX; ++var7) {
				if (par2StructureBoundingBox.isVecInside(new Vec3i(var7, 64, var6))) {
					var4 += Math.max(par1World.getTopSolidOrLiquidBlock(new BlockPos(var7, 0, var6)).getY(), par1World.provider.getAverageGroundLevel());
					++var5;
				}
			}
		}

		if (var5 == 0)
			return false;

		this.horizontalPos = var4 / var5;
		this.boundingBox.offset(0, this.horizontalPos - this.boundingBox.minY + par3, 0);
		return true;

	}
}
