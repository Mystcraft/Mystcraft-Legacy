package com.xcompwiz.mystcraft.tileentity;

import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.network.packet.MPacketMessage;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRotatable extends TileEntityBase implements ITileEntityRotateable {

	private short yaw = 0;

	@Override
	public void setYaw(int yaw) {
		this.yaw = (short) (yaw % 360);
		markForUpdate();
	}

	@Override
	public short getYaw() {
		return this.yaw;
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);
		this.setYaw(NBTUtils.readNumber(compound.getTag("Yaw")).intValue());
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setShort("Yaw", yaw);
	}

}
