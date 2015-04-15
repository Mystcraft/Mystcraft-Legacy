package com.xcompwiz.mystcraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.network.packet.MPacketMessage;

public class TileEntityRotatable extends TileEntity implements IMessageReceiver, ITileEntityRotateable {

	private short	yaw;

	public TileEntityRotatable() {
		tileEntityInvalid = false;
		yaw = 0;
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public void setYaw(int yaw) {
		this.yaw = (short) (yaw % 360);
		this.markDirty();
		if (worldObj != null) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public short getYaw() {
		return this.yaw;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.setYaw(NBTUtils.readNumber(nbttagcompound.getTag("Yaw")).intValue());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("Yaw", yaw);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setShort("Yaw", yaw);
		return MPacketMessage.createPacket(this, nbttagcompound);
	}

	@Override
	public void processMessageData(NBTTagCompound nbttagcompound) {
		yaw = nbttagcompound.getShort("Yaw");
		markDirty();
	}
}
