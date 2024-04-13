package com.xcompwiz.mystcraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityUtils {

	//Copied from Item
	public static MovingObjectPosition getMovingObjectPositionFromPlayer(World worldObj, EntityPlayer entityplayer, boolean p_77621_3_) {
		float f = 1.0F;
		float f1 = entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * f;
		float f2 = entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * f;
		double d0 = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * f;
		double d1 = entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * f + (worldObj.isRemote ? entityplayer.getEyeHeight() - entityplayer.getDefaultEyeHeight() : entityplayer.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		double d2 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * f;
		Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		if (entityplayer instanceof EntityPlayerMP) {
			d3 = ((EntityPlayerMP) entityplayer).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
		return worldObj.func_147447_a(vec3, vec31, p_77621_3_, !p_77621_3_, false);
	}

}
