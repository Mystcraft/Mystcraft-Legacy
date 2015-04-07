package com.xcompwiz.mystcraft.world.gen.structure;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

abstract class ComponentScatteredFeatureMyst extends StructureComponent {
	/** The size of the bounding box for this feature in the X axis */
	protected int	scatteredFeatureSizeX;

	/** The size of the bounding box for this feature in the Y axis */
	protected int	scatteredFeatureSizeY;

	/** The size of the bounding box for this feature in the Z axis */
	protected int	scatteredFeatureSizeZ;
	protected int	field_74936_d	= -1;

	public ComponentScatteredFeatureMyst() {}

	protected ComponentScatteredFeatureMyst(Random par1Random, int par2, int par3, int par4, int par5, int par6, int par7) {
		super(0);
		this.scatteredFeatureSizeX = par5;
		this.scatteredFeatureSizeY = par6;
		this.scatteredFeatureSizeZ = par7;
		this.coordBaseMode = par1Random.nextInt(4);

		switch (this.coordBaseMode) {
		case 0:
		case 2:
			this.boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + par5 - 1, par3 + par6 - 1, par4 + par7 - 1);
			break;
		default:
			this.boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + par7 - 1, par3 + par6 - 1, par4 + par5 - 1);
		}
	}

	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setInteger("Width", this.scatteredFeatureSizeX);
		par1NBTTagCompound.setInteger("Height", this.scatteredFeatureSizeY);
		par1NBTTagCompound.setInteger("Depth", this.scatteredFeatureSizeZ);
		par1NBTTagCompound.setInteger("HPos", this.field_74936_d);
	}

	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
		this.scatteredFeatureSizeX = par1NBTTagCompound.getInteger("Width");
		this.scatteredFeatureSizeY = par1NBTTagCompound.getInteger("Height");
		this.scatteredFeatureSizeZ = par1NBTTagCompound.getInteger("Depth");
		this.field_74936_d = par1NBTTagCompound.getInteger("HPos");
	}

	/**
	 * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox. (A median of all the levels in the BB's horizontal rectangle).
	 */
	protected int getAverageGroundLevel(World worldObj, StructureBoundingBox boundingbox) {
		int i = 0;
		int j = 0;

		for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k) {
			for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l) {
				if (boundingbox.isVecInside(l, 64, k)) {
					i += Math.max(worldObj.getTopSolidOrLiquidBlock(l, k), worldObj.provider.getAverageGroundLevel());
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
				if (par2StructureBoundingBox.isVecInside(var7, 64, var6)) {
					var4 += Math.max(par1World.getTopSolidOrLiquidBlock(var7, var6), par1World.provider.getAverageGroundLevel());
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
