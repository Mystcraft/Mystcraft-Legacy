package com.xcompwiz.mystcraft.nbt;

import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.world.storage.IStorageObject;

public class NBTTagCompoundWrapper implements IStorageObject {

	private NBTTagCompound	compoundTag;
	@SuppressWarnings("unused")
	private Boolean			dirty;
	@SuppressWarnings("unused")
	private Boolean			needsResend;	// TODO: (AgeData) Use this

	public NBTTagCompoundWrapper(NBTTagCompound compoundTag, Boolean sharedDirty, Boolean sharedResend) {
		this.compoundTag = compoundTag;
		this.dirty = sharedDirty;
		this.needsResend = sharedResend;
	}

	@Override
	public boolean getBoolean(String string) {
		return compoundTag.getBoolean(string);
	}

	@Override
	public void setBoolean(String string, boolean var2) {
		compoundTag.setBoolean(string, var2);
		markDirty();
	}

	@Override
	public int getInteger(String string) {
		return compoundTag.getInteger(string);
	}

	@Override
	public void setInteger(String string, int var2) {
		compoundTag.setInteger(string, var2);
		markDirty();
	}

	private void markDirty() {
		dirty = true;
	}
}
