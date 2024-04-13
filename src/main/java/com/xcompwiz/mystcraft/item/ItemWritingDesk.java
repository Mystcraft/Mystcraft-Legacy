package com.xcompwiz.mystcraft.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.data.ModBlocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWritingDesk extends Item {
	private IIcon	icontop;

	public ItemWritingDesk() {
		this.setHasSubtypes(true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:writingdesk");
		icontop = register.registerIcon("mystcraft:deskext");
	}

	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	public IIcon getIconFromDamage(int meta) {
		if (meta == 1) { return icontop; }
		return this.itemIcon;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		super.getSubItems(item, tabs, list);
		list.add(new ItemStack(item, 1, 1));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		if (meta == 1) return super.getUnlocalizedName() + ".top";
		return super.getUnlocalizedName();
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int face, float par8, float par9, float par10) {
		if (entityplayer.worldObj.isRemote) return false;
		if (itemstack.getItemDamage() == 0) { return placeDesk(itemstack, entityplayer, world, i, j, k, face); }
		if (itemstack.getItemDamage() == 1) { return extendDesk(itemstack, entityplayer, world, i, j, k, face); }
		return false;
	}

	private boolean extendDesk(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int face) {
		Block block = ModBlocks.writingdesk;
		if (world.getBlock(i, j, k) != block) return false;
		int meta = world.getBlockMetadata(i, j, k);
		if (BlockWritingDesk.isBlockTop(meta)) return false;
		int xOffset = 0;
		int zOffset = 0;
		if ((meta & 3) == 0) {
			zOffset = 1;
		}
		if ((meta & 3) == 1) {
			xOffset = -1;
		}
		if ((meta & 3) == 2) {
			zOffset = -1;
		}
		if ((meta & 3) == 3) {
			xOffset = 1;
		}
		if (BlockWritingDesk.isBlockFoot(meta)) {
			meta = meta & 3;
			i -= xOffset;
			k -= zOffset;
		}
		++j;
		if (!entityplayer.canPlayerEdit(i, j, k, face, itemstack) || !entityplayer.canPlayerEdit(i + xOffset, j, k + zOffset, face, itemstack)) { return false; }
		if (isBlockReplaceable(world, i, j, k) && isBlockReplaceable(world, i + xOffset, j, k + zOffset)) {
			world.setBlock(i, j, k, block, meta + 4, 3);
			if (world.getBlock(i, j, k) == block) {
				world.setBlock(i + xOffset, j, k + zOffset, block, meta + 8 + 4, 3);
			}
			itemstack.stackSize--;
			return true;
		}
		return false;
	}

	private boolean placeDesk(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int face) {
		if (isBlockReplaceable(world, i, j, k)) {
			--j;
			face = 1;
		}
		if (face != 1) { return false; }
		Block block = ModBlocks.writingdesk;
		int facing = (MathHelper.floor_double(((entityplayer.rotationYaw * 4F) / 360F) + 0.5D) + 1) & 3;
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
		++j;
		if (!entityplayer.canPlayerEdit(i, j, k, face, itemstack) || !entityplayer.canPlayerEdit(i + xOffset, j, k + zOffset, face, itemstack)) { return false; }
		if (isBlockReplaceable(world, i, j, k) && isBlockReplaceable(world, i + xOffset, j, k + zOffset)) {
			world.setBlock(i, j, k, block, facing, 3);
			if (world.getBlock(i, j, k) == block) {
				world.setBlock(i + xOffset, j, k + zOffset, block, facing + 8, 3);
			}
			itemstack.stackSize--;
			return true;
		}
		return false;
	}

	private boolean isBlockReplaceable(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (world.isAirBlock(x, y, z)) return true;
		if (block.isReplaceable(world, x, y, z)) return true;
		if (block == Blocks.snow || block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush) return true;
		return false;
	}
}
