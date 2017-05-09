package com.xcompwiz.mystcraft.tileentity;

import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.network.packet.MPacketMessage;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;

public class TileEntityBookRotateable extends TileEntityBook implements ITileEntityRotateable {

	private short yaw = 0;
	private short pitch = 0;

	public void setPitch(int pitch) {
		this.pitch = (short) (pitch % 360);
		markForUpdate();
	}

	public short getPitch() {
		return pitch;
	}

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
		this.setYaw(NBTUtils.readNumber(compound.getCompoundTag("Yaw")).intValue());
		this.setPitch(NBTUtils.readNumber(compound.getCompoundTag("Pitch")).intValue());
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setShort("Yaw", this.yaw);
		compound.setShort("Pitch", this.pitch);
	}

	public void link(Entity player) {
		ItemStack book = getBook();
		if (book.isEmpty()) return;
		if (!(book.getItem() instanceof ItemLinking)) return;
		((ItemLinking) book.getItem()).activate(book, world, player);
	}
	
}
