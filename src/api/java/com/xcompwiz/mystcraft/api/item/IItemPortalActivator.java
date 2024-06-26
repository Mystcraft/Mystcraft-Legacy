package com.xcompwiz.mystcraft.api.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This interface can be applied to an Item data class to create items which can be used to activate Mystcraft portals. The processing of the teleport is up to
 * you.
 * @author xcompwiz
 */
public interface IItemPortalActivator {

	/**
	 * Allows you to handle what should happen when an entity collides with the portal
	 * @param itemstack The itemstack in the portal receptacle
	 * @param worldObj The world the portal is in
	 * @param entity The entity colliding with the portal
	 * @param pos The coordinate of the block collided with
	 */
	public void onPortalCollision(ItemStack itemstack, World worldObj, Entity entity, BlockPos pos);

	/**
	 * This allows you to provide your own color for the portal. It is only called when the terrain is being built, so use a fixed color (per itemstack).
	 * @param itemstack The itemstack in the portal receptacle
	 * @param worldObj The world the portal is in
	 * @return A number depicting a color (ex. 0xFFFFFF)
	 */
	@SideOnly(Side.CLIENT)
	public int getPortalColor(ItemStack itemstack, World worldObj);

}
