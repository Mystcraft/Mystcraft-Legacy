package com.xcompwiz.mystcraft.tileentity;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBookstand extends TileEntityBookRotateable {

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);
		if (compound.hasKey("Rotation")) {
			this.setYaw(compound.getInteger("Rotation") + 270);
		}
	}

	@Override
	public void setYaw(int rotation) {
		rotation = rotation - (rotation % 45);
		super.setYaw(rotation);
	}

	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return Block.FULL_BLOCK_AABB.offset(pos);
	}

}
