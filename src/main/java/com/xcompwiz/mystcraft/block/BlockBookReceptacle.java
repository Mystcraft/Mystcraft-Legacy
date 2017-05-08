package com.xcompwiz.mystcraft.block;

import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.portal.PortalUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBook;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBookReceptacle extends BlockContainer {

	private IIcon	iconFace;

	public BlockBookReceptacle() {
		super(Material.glass);
		setTickRandomly(false);
		useNeighborBrightness = true;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2) {
		if (par1 == par2) { return iconFace; }
		return blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("mystcraft:crystal");
		this.iconFace = par1IconRegister.registerIcon("mystcraft:book_receptacle");
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two adjacent blocks and also whether the
	 * player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
	 */
	@Override
	public int getRenderBlockPass() {
		return 0;
	}

	/**
	 * The type of render function that is called for this block
	 */
	@Override
	public int getRenderType() {
		return 0;
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
	}

	/**
	 * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
	 */
	@Override
	public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5) {
		if (par5 == 0) {// && par1World.getBlock(par2, par3+1, par4) != myst_BlockCrystal.instance) {
			return false;
		}
		if (par5 == 1 && par1World.getBlock(par2, par3 - 1, par4) != ModBlocks.crystal) { return false; }
		if (par5 == 2 && par1World.getBlock(par2, par3, par4 + 1) != ModBlocks.crystal) { return false; }
		if (par5 == 3 && par1World.getBlock(par2, par3, par4 - 1) != ModBlocks.crystal) { return false; }
		if (par5 == 4 && par1World.getBlock(par2 + 1, par3, par4) != ModBlocks.crystal) { return false; }
		if (par5 == 5 && par1World.getBlock(par2 - 1, par3, par4) != ModBlocks.crystal) { return false; }
		return canPlaceBlockAt(par1World, par2, par3, par4);
	}

	/**
	 * Returns the bounding box of the wired rectangular prism to render.
	 */
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		int i = par1World.getBlockMetadata(par2, par3, par4);
		float f = 0.375F;

		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
		if (i == 1) {
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
		}

		if (i == 2) {
			setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		}

		if (i == 3) {
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		}

		if (i == 4) {
			setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}

		if (i == 5) {
			setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(par2 + this.minX, par3 + this.minY, par4 + this.minZ, par2 + this.maxX, par3 + this.maxY, par4 + this.maxZ);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
		return box;
	}

	/**
	 * Adds to the supplied array any colliding bounding boxes with the passed in bounding box. Args: world, x, y, z, axisAlignedBB, arrayList
	 */
	@Override
	public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
		setBlockBoundsBasedOnState(world, i, j, k);
		super.addCollisionBoxesToList(world, i, j, k, axisalignedbb, list, entity);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		int i = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
		float f = 0.375F;

		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
		if (i == 1) {
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
		}

		if (i == 2) {
			setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		}

		if (i == 3) {
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		}

		if (i == 4) {
			setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}

		if (i == 5) {
			setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}
	}

	/**
	 * called before onBlockPlacedBy by ItemBlock and ItemReed WAS: updateBlockMetadata; func_85104_a
	 */
	@Override
	public int onBlockPlaced(World world, int i, int j, int k, int face, float par6, float par7, float par8, int metadata) {
		if (face == 1) {
			return face;
		} else if (face == 2) {
			return face;
		} else if (face == 3) {
			return face;
		} else if (face == 4) {
			return face;
		} else if (face == 5) { return face; }
		return 0;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		super.onBlockAdded(par1World, par2, par3, par4);
		updateTileEntityOrientation(par1World, par2, par3, par4);
	}

	private void updateTileEntityOrientation(World world, int i, int j, int k) {
		TileEntityBookReceptacle book = (TileEntityBookReceptacle) world.getTileEntity(i, j, k);
		int metadata = world.getBlockMetadata(i, j, k);

		if (metadata == 1) {
			book.setPitch(90);
			book.setYaw(-90);
		} else if (metadata == 2) {
			book.setYaw(270);
		} else if (metadata == 3) {
			book.setYaw(90);
		} else if (metadata == 4) {
			book.setYaw(0);
		} else if (metadata == 5) {
			book.setYaw(180);
		}

		book.markDirty();
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor
	 * blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block block) {
		ChunkPos coord = PortalUtils.getReceptacleBase(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4));
		if (par1World.getBlock(coord.posX, coord.posY, coord.posZ) != ModBlocks.crystal) {
			dropBlockAsItem(par1World, par2, par3, par4, 0, 0);
			par1World.setBlock(par2, par3, par4, Blocks.AIR);
		}

		super.onNeighborBlockChange(par1World, par2, par3, par4, block);
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		if (!world.isRemote) {
			TileEntityBookReceptacle book = (TileEntityBookReceptacle) world.getTileEntity(i, j, k);
			if (book == null) { return; }
			ItemStack itemstack = book.getBook();
			if (itemstack != null) {
				EntityItem entity = new EntityItem(world, i, j, k, itemstack);
				world.spawnEntityInWorld(entity);
			}
			book.setBook(null);
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	@Override
	// world, x, y, z, player, side, origin?
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float posX, float pozY, float posZ) {
		TileEntityBookReceptacle book = (TileEntityBookReceptacle) world.getTileEntity(i, j, k);
		if (book == null) { return false; }
		ItemStack itemstack = book.getBook();
		if (itemstack == null) {
			itemstack = entityplayer.inventory.getCurrentItem();
			if (itemstack != null && (itemstack.getItem() instanceof IItemPortalActivator)) {
				if (!world.isRemote) {
					entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
					book.setBook(itemstack);
				}
				return true;
			}
		} else {
			if (!world.isRemote && entityplayer.inventory.getCurrentItem() == null) {
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, itemstack);
				entityplayer.inventory.markDirty();
				book.setBook(null);
			}
			return true;
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityBookReceptacle();
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from getComparatorInputOverride instead of the actual redstone
	 * signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal strength when this block inputs to a
	 * comparator.
	 */
	@Override
	public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
		return Container.calcRedstoneFromInventory(this.getInventory(par1World, par2, par3, par4));
	}

	private IInventory getInventory(World worldObj, int i, int j, int k) {
		return (TileEntityBook) worldObj.getTileEntity(i, j, k);
	}
}
