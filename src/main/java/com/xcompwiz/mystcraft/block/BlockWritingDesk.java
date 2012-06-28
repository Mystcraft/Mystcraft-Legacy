package com.xcompwiz.mystcraft.block;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.client.gui.GuiWritingDesk;
import com.xcompwiz.mystcraft.inventory.ContainerWritingDesk;
import com.xcompwiz.mystcraft.item.ItemWritingDesk;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWritingDesk extends Block {

	public static class GuiHandlerDesk extends GuiHandlerManager.GuiHandler {
		@Override
		public TileEntity getTileEntity(EntityPlayerMP player, World worldObj, int i, int j, int k) {
			TileEntityDesk tileentity = BlockWritingDesk.getTileEntity(worldObj, i, j, k);
			if (tileentity == null) {
				LoggerUtils.warn(String.format("Desk TileEntity Missing"));
			}
			return tileentity;
		}

		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, TileEntity tileentity, int i, int j, int k) {
			// Sends age data packet
			NetworkUtils.sendAgeData(worldObj, ((TileEntityDesk) tileentity).getStackInSlot(0), player);
			return new ContainerWritingDesk(player.inventory, (TileEntityDesk) tileentity);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			int x = data.readInt();
			int y = data.readInt();
			int z = data.readInt();
			return new GuiWritingDesk(player.inventory, BlockWritingDesk.getTileEntity(player.worldObj, x, y, z));
		}
	}

	private static final int	GuiID			= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerDesk());

	private static final int	headFootMap[][]	= { { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 0 } };

	public static Block			instance;

	public BlockWritingDesk() {
		super(Material.wood);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		float xmin = 0.0F;
		float xmax = 1.0F;
		float ymin = 0.0F;
		float ymax = 1.0F;
		float zmin = 0.0F;
		float zmax = 1.0F;
		int meta = iblockaccess.getBlockMetadata(i,j,k);
		if (isBlockTop(meta)) {
			ymax = 0.75F;
			int dir = getDirectionFromMetadata(meta);
			if (dir == 0) {
				xmin = 0.5F;
			}
			if (dir == 1) {
				zmin = 0.5F;
			}
			if (dir == 2) {
				xmax = 0.5F;
			}
			if (dir == 3) {
				zmax = 0.5F;
			}
		}
		setBlockBounds(xmin, ymin, zmin, xmax, ymax, zmax);
	}

	/**
	 * Returns the bounding box of the wired rectangular prism to render.
	 */
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return AxisAlignedBB.getBoundingBox(par2 + this.minX, par3 + this.minY, par4 + this.minZ, par2 + this.maxX, par3 + this.maxY, par4 + this.maxZ);
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return AxisAlignedBB.getBoundingBox(par2 + this.minX, par3 + this.minY, par4 + this.minZ, par2 + this.maxX, par3 + this.maxY, par4 + this.maxZ);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (i <= 1) { return Blocks.crafting_table.getBlockTextureFromSide(i); }
		return Blocks.bookshelf.getBlockTextureFromSide(i);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public void onNeighborBlockChange(World worldObj, int i, int j, int k, Block block) {
		int meta = worldObj.getBlockMetadata(i, j, k);
		if (isBlockTop(meta) && !isBlockFoot(meta)) {
			if (worldObj.getBlock(i, j - 1, k) != this) {
				if (!worldObj.isRemote) {
					dropBlockAsItem(worldObj, i, j, k, meta, 0);
				}
				worldObj.setBlock(i, j, k, Blocks.air);
			}
		}
		int direction = getDirectionFromMetadata(meta);
		if (isBlockFoot(meta)) {
			if (worldObj.getBlock(i - headFootMap[direction][0], j, k - headFootMap[direction][1]) != this) {
				worldObj.setBlock(i, j, k, Blocks.air);
			}
		} else if (worldObj.getBlock(i + headFootMap[direction][0], j, k + headFootMap[direction][1]) != this) {
			worldObj.setBlock(i, j, k, Blocks.air);
		}
	}

	/**
	 * Called when the block is attempted to be harvested
	 */
	@Override
	public void onBlockHarvested(World worldObj, int x, int y, int z, int meta, EntityPlayer player) {
		if (player.capabilities.isCreativeMode && (!isBlockTop(meta)) && worldObj.getBlock(x, y + 1, z) == this && isBlockTop(worldObj.getBlockMetadata(x, y + 1, z))) {
			worldObj.setBlockToAir(x, y + 1, z);
			int direction = getDirectionFromMetadata(meta);
			if (isBlockFoot(meta)) {
				worldObj.setBlock(x - headFootMap[direction][0], y + 1, z - headFootMap[direction][1], Blocks.air);
			} else {
				worldObj.setBlock(z + headFootMap[direction][0], y + 1, z + headFootMap[direction][1], Blocks.air);
			}
		}
	}

	@Override
	// world, x, y, z, player, side, origin?
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float posX, float pozY, float posZ) {
		if (world.isRemote) return true;
		NetworkUtils.displayGui(entityplayer, world, GuiID, i, j, k);
		return true;
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		IInventory tileentity = (IInventory) world.getTileEntity(i, j, k);
		if (tileentity != null) {
			for (int l = 0; l < tileentity.getSizeInventory(); l++) {
				ItemStack itemstack = tileentity.getStackInSlot(l);
				if (itemstack == null) {
					continue;
				}
				tileentity.setInventorySlotContents(l, null);
				float f = world.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
				float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(world, i + f, j + f1, k + f2, itemstack);
				float f3 = 0.05F;
				entityitem.motionX = (float) world.rand.nextGaussian() * f3;
				entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
				world.spawnEntityInWorld(entityitem);
			}
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
	 * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int par1) {
		return null;
	}

	/**
	 * This returns a complete list of items dropped from this block.
	 * 
	 * @param world The current world
	 * @param x X Position
	 * @param y Y Position
	 * @param z Z Position
	 * @param metadata Current metadata
	 * @param fortune Breakers fortune level
	 * @return A ArrayList containing all items this block drops
	 */
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if (isBlockTop(metadata)) {
			ret.add(new ItemStack(ItemWritingDesk.instance, 1, 1));
		} else {
			ret.add(new ItemStack(ItemWritingDesk.instance, 1, 0));
		}
		return ret;
	}

	/**
	 * Called when a user uses the creative pick block button on this block
	 * 
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
	 */
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		if (isBlockTop(world.getBlockMetadata(x, y, z))) { return new ItemStack(ItemWritingDesk.instance, 1, 1); }
		return new ItemStack(ItemWritingDesk.instance, 1, 0);
	}

	@Override
	public int getMobilityFlag() {
		return 1;
	}

	public static int getMetadataFromDirection(int i, int j) {
		if (j == 1) {
			return 0;
		} else if (i == -1) {
			return 1;
		} else if (j == -1) {
			return 2;
		} else if (i == 1) { return 3; }
		return 0;
	}

	public static int getDirectionFromMetadata(int i) {
		return i & 3;
	}

	public static boolean isBlockFoot(int i) {
		return (i & 8) != 0;
	}

	public static boolean isBlockTop(int i) {
		return (i & 4) != 0;
	}

	public static TileEntityDesk getTileEntity(World world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		if (isBlockTop(meta)) { return getTileEntity(world, i, j - 1, k); }
		if (isBlockFoot(meta)) {
			int direction = getDirectionFromMetadata(meta);
			return (TileEntityDesk) world.getTileEntity(i - headFootMap[direction][0], j, k - headFootMap[direction][1]);
		}
		return (TileEntityDesk) world.getTileEntity(i, j, k);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return !isBlockFoot(metadata) && !isBlockTop(metadata);
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityDesk();
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World worldObj, int i, int j, int k) {
		super.onBlockAdded(worldObj, i, j, k);
		if (hasTileEntity(worldObj.getBlockMetadata(i, j, k))) {
			worldObj.setTileEntity(i, j, k, this.createTileEntity(worldObj, worldObj.getBlockMetadata(i, j, k)));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemstack) {
		if (world.getBlock(i, j, k) != this) return;
		int facing = (MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) + 1) & 3;
		int xOffset = 0;
		int zOffset = 0;
		if (facing == 0) {
			zOffset = 1;
		}
		if (facing == 1) {
			xOffset = -1;
		}
		if (facing == 2) {
			zOffset = -1;
		}
		if (facing == 3) {
			xOffset = 1;
		}
		if (world.getBlock(i + xOffset, j, k + zOffset) == Blocks.air) {
			world.setBlockMetadataWithNotify(i, j, k, facing, 3);
			world.setBlock(i + xOffset, j, k + zOffset, this, facing + 8, 3);
		} else {
			world.setBlock(i,j,k, Blocks.air, 0, 3);
		}
	}

	/**
	 * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
	 * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
	 * 
	 * @return
	 */
	@Override
	public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
		super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
		TileEntity var7 = par1World.getTileEntity(par2, par3, par4);

		if (var7 != null) {
			var7.receiveClientEvent(par5, par6);
		}
		return false;
	}
}
