package com.xcompwiz.mystcraft.tileentity;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityStarFissure extends TileEntity {

	public TileEntityStarFissure() {}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

}
