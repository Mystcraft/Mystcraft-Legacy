package com.xcompwiz.mystcraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBookstand extends TileEntityBookDisplay {

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		if (nbttagcompound.hasKey("Rotation")) {
			int rot = nbttagcompound.getInteger("Rotation") + 270;
			this.setYaw(rot);
		}
	}

	@Override
	public void setYaw(int rotation) {
		rotation = rotation - (rotation % 45);
		super.setYaw(rotation);
	}

	@Override
	public String getInventoryName() {
		return "Bookstand";
	}

	/**
	 * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity} Defaults
	 * to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block at this location.
	 * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}
}
