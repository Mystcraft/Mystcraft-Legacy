package com.xcompwiz.mystcraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityDummy extends Entity {

	public EntityDummy(World worldObj, int x, int y, int z, int yaw, int pitch) {
		super(worldObj);
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.rotationPitch = pitch;
		this.rotationYaw = yaw;
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound data) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound data) {}

	@Override
	public void onUpdate() {
		this.setDead();
	}
}
