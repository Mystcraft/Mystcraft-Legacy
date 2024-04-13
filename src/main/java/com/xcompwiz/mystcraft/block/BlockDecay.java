package com.xcompwiz.mystcraft.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.instability.decay.DecayHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDecay extends Block {

	public BlockDecay() {
		super(Material.sand);
		setTickRandomly(false);
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public IIcon getIcon(int side, int meta) {
		return getDecayHandler(meta).getBlockTextureFromSide(side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		DecayHandler.registerIcons(par1IconRegister);
	}

	/**
	 * Called right before the block is destroyed by a player. Args: world, x, y, z, metaData
	 */
	@Override
	public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int meta) {
		super.onBlockDestroyedByPlayer(world, i, j, k, meta);
		getDecayHandler(meta).onBlockDestroyedByPlayer(world, i, j, k);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!world.isRemote) {
			getDecayHandler(world.getBlockMetadata(i, j, k)).updateTick(world, i, j, k, random);
		}
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		getDecayHandler(world.getBlockMetadata(i, j, k)).onBlockAdded(world, i, j, k);
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage and is ignored for blocks which do not
	 * support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int par1) {
		return null;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}

	/**
	 * Called when a user uses the creative pick block button on this block
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
	}

	/**
	 * Location sensitive version of getExplosionRestance
	 * @param entity The entity that caused the explosion
	 * @param worldObj The current world
	 * @param x X Position
	 * @param y Y Position
	 * @param z Z Position
	 * @param explosionX Explosion source X Position
	 * @param explosionY Explosion source X Position
	 * @param explosionZ Explosion source X Position
	 * @return The amount of the explosion absorbed.
	 */
	@Override
	public float getExplosionResistance(Entity entity, World worldObj, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return getDecayHandler(worldObj.getBlockMetadata(x, y, z)).getExplosionResistance(entity, worldObj, x, y, z, explosionX, explosionY, explosionZ);
	}

	/**
	 * Returns the block hardness at a location.
	 * @param worldObj The current world
	 * @param x X Position
	 * @param y Y Position
	 * @param z Z Position
	 */
	@Override
	public float getBlockHardness(World worldObj, int x, int y, int z) {
		return getDecayHandler(worldObj.getBlockMetadata(x, y, z)).getBlockHardness(worldObj, x, y, z);
	}

	/**
	 * Get a light value for this block, normal ranges are between 0 and 15
	 * @param worldObj The current world
	 * @param x X Position
	 * @param y Y position
	 * @param z Z position
	 * @return The light value
	 */
	@Override
	public int getLightValue(IBlockAccess worldObj, int x, int y, int z) {
		return lightValue;
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World worldObj, int x, int y, int z, Entity entity) {
		getDecayHandler(worldObj.getBlockMetadata(x, y, z)).onEntityContact(worldObj, x, y, z, entity);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World worldObj, int x, int y, int z, Entity entity) {
		getDecayHandler(worldObj.getBlockMetadata(x, y, z)).onEntityContact(worldObj, x, y, z, entity);
	}

	private DecayHandler getDecayHandler(int meta) {
		DecayHandler handler = DecayHandler.getHandler(meta);
		if (handler == null) handler = DecayHandler.getHandler(0);
		return handler;
	}
}
