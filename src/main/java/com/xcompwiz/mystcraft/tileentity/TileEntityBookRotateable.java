package com.xcompwiz.mystcraft.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;

import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.network.packet.MPacketMessage;

public class TileEntityBookRotateable extends TileEntityBook implements IMessageReceiver, ITileEntityRotateable {

	private short	yaw;
	private short	pitch;

	public TileEntityBookRotateable() {
		tileEntityInvalid = false;
		yaw = 0;
		pitch = 0;
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	public void setPitch(int pitch) {
		this.pitch = (short) (pitch % 360);
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public short getPitch() {
		return pitch;
	}

	@Override
	public void setYaw(int yaw) {
		this.yaw = (short) (yaw % 360);
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public short getYaw() {
		return this.yaw;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.setYaw(NBTUtils.readNumber(nbttagcompound.getTag("Yaw")).intValue());
		this.setPitch(NBTUtils.readNumber(nbttagcompound.getTag("Pitch")).intValue());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("Yaw", yaw);
		nbttagcompound.setShort("Pitch", pitch);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setShort("Yaw", yaw);
		nbttagcompound.setShort("Pitch", pitch);
		if (getBook() != null) nbttagcompound.setTag("Item", getBook().writeToNBT(new NBTTagCompound()));
		return MPacketMessage.createPacket(this, nbttagcompound);
	}

	@Override
	public void processMessageData(NBTTagCompound nbttagcompound) {
		yaw = nbttagcompound.getShort("Yaw");
		pitch = nbttagcompound.getShort("Pitch");
		if (nbttagcompound.hasKey("Item")) {
			setBook(ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("Item")));
		} else {
			setBook(null);
		}
		markDirty();
	}

	public void link(Entity player) {
		ItemStack book = getBook();
		if (book == null) return;
		if (!(book.getItem() instanceof ItemLinking)) return;
		((ItemLinking) book.getItem()).activate(book, worldObj, player);
	}
}
