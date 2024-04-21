package com.xcompwiz.mystcraft.api.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	 * @param x The x coordinate of the block collided with
	 * @param y The y coordinate of the block collided with
	 * @param z The z coordinate of the block collided with
	 */
	public void onPortalCollision(ItemStack itemstack, World worldObj, Entity entity, int x, int y, int z);

	/**
	 * This allows you to provide your own color for the portal. It is only called when the terrain is being built, so use a fixed color (per itemstack).
	 * @param itemstack The itemstack in the portal receptacle
	 * @param worldObj The world the portal is in
	 * @return A number depicting a color (ex. 0xFFFFFF)
	 */
	@SideOnly(Side.CLIENT)
	public int getPortalColor(ItemStack itemstack, World worldObj);

}
