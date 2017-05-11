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

	/** The size of the bounding box for this feature in the X axis */
	protected int	scatteredFeatureSizeX;
	/** The size of the bounding box for this feature in the Y axis */
	protected int	scatteredFeatureSizeY;
	/** The size of the bounding box for this feature in the Z axis */
	protected int	scatteredFeatureSizeZ;

	protected int	field_74936_d	= -1;

	public ComponentScatteredFeatureMyst() {}

	protected ComponentScatteredFeatureMyst(Random par1Random, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
		super(0);
		this.scatteredFeatureSizeX = x;
		this.scatteredFeatureSizeY = y;
		this.scatteredFeatureSizeZ = z;
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

	@Override
	protected void writeStructureToNBT(NBTTagCompound tagCompound) {
		tagCompound.setInteger("Width", this.scatteredFeatureSizeX);
		tagCompound.setInteger("Height", this.scatteredFeatureSizeY);
		tagCompound.setInteger("Depth", this.scatteredFeatureSizeZ);
		tagCompound.setInteger("HPos", this.field_74936_d);
	}

	@Override
	protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
		this.scatteredFeatureSizeX = tagCompound.getInteger("Width");
		this.scatteredFeatureSizeY = tagCompound.getInteger("Height");
		this.scatteredFeatureSizeZ = tagCompound.getInteger("Depth");
		this.field_74936_d = tagCompound.getInteger("HPos");
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

		if (j == 0) { return -1; }
		return i / j;
	}

	protected boolean shouldBuildHere(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3) {
		if (this.field_74936_d >= 0) return true;
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

		if (var5 == 0) return false;

		this.field_74936_d = var4 / var5;
		this.boundingBox.offset(0, this.field_74936_d - this.boundingBox.minY + par3, 0);
		return true;

	}
}
