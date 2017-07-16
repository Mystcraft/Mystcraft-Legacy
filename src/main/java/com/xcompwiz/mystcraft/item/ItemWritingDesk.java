package com.xcompwiz.mystcraft.item;

import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemWritingDesk extends Item {

	public ItemWritingDesk() {
		this.setHasSubtypes(true);
		setUnlocalizedName("myst.writingdesk");
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if(isInCreativeTab(tab)) {
			subItems.add(new ItemStack(this, 1, 0));
			subItems.add(new ItemStack(this, 1, 1));
		}
	}

	@Override
	@Nonnull
	public String getUnlocalizedName(@Nonnull ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		if (meta == 1) {
			return super.getUnlocalizedName() + ".top";
		}
		return super.getUnlocalizedName();
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if(world.isRemote) {
			return EnumActionResult.PASS;
		}
		ItemStack held = player.getHeldItem(hand);
		if(held.isEmpty()) return EnumActionResult.FAIL;
		int dmg = held.getItemDamage();
		if(dmg == 0) {
			return placeDesk(held, player, world, pos, side);
		} else if(dmg == 1) {
			return extendDesk(held, player, world, pos, side);
		}
		return EnumActionResult.PASS;
	}

	@Nonnull
	private EnumActionResult extendDesk(@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing face) {
		IBlockState at = world.getBlockState(pos);
		if(!at.getBlock().equals(ModBlocks.writingdesk)) {
			return EnumActionResult.PASS;
		}
		if(BlockWritingDesk.isBlockTop(at)) {
			return EnumActionResult.PASS;
		}
		EnumFacing currentFacing = BlockWritingDesk.getDirectionFromMetadata(at);
		int xOffset = 0;
		int zOffset = 0;
		if (currentFacing.getHorizontalIndex() == 0) {
			zOffset = 1;
		}
		if (currentFacing.getHorizontalIndex() == 1) {
			xOffset = -1;
		}
		if (currentFacing.getHorizontalIndex() == 2) {
			zOffset = -1;
		}
		if (currentFacing.getHorizontalIndex() == 3) {
			xOffset = 1;
		}
		if(BlockWritingDesk.isBlockFoot(at)) {
			pos = pos.add(-xOffset, 0, -zOffset);
		}

		BlockPos up = pos.up();
		if(!player.canPlayerEdit(up, face, stack) || !player.canPlayerEdit(up.add(xOffset, 0, zOffset), face, stack)) {
			return EnumActionResult.PASS;
		}
		if(isBlockReplaceable(world, up, face) && isBlockReplaceable(world, up.add(xOffset, 0, zOffset), face)) {
			IBlockState def = ModBlocks.writingdesk.getDefaultState().withProperty(BlockWritingDesk.ROTATION, currentFacing);
			world.setBlockState(up, def.withProperty(BlockWritingDesk.IS_TOP, true));
			if(world.getBlockState(up).getBlock().equals(ModBlocks.writingdesk)) {
				world.setBlockState(up.add(xOffset, 0, zOffset), def.withProperty(BlockWritingDesk.IS_TOP, true).withProperty(BlockWritingDesk.IS_FOOT, true));
			}
            if(!player.isCreative()) {
                stack.shrink(1);
            }
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Nonnull
	private EnumActionResult placeDesk(@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing face) {
		if(isBlockReplaceable(world, pos, face)) {
			pos = pos.down();
			face = EnumFacing.UP;
		}
		if(face != EnumFacing.UP) {
			return EnumActionResult.PASS;
		}
		EnumFacing facing = player.getHorizontalFacing();
        facing = facing.rotateAround(EnumFacing.Axis.Y);
		int xOffset = 0;
		int zOffset = 0;
		if (facing.getHorizontalIndex() == 0) {
			zOffset = 1;
		}
		if (facing.getHorizontalIndex() == 1) {
			xOffset = -1;
		}
		if (facing.getHorizontalIndex() == 2) {
			zOffset = -1;
		}
		if (facing.getHorizontalIndex() == 3) {
			xOffset = 1;
		}
		pos = pos.up();
		if(!player.canPlayerEdit(pos, face, stack) || !player.canPlayerEdit(pos.add(xOffset, 0, zOffset), face, stack)) {
			return EnumActionResult.PASS;
		}
		if(isBlockReplaceable(world, pos, face) && isBlockReplaceable(world, pos.add(xOffset, 0, zOffset), face)) {
			IBlockState def = ModBlocks.writingdesk.getDefaultState().withProperty(BlockWritingDesk.ROTATION, facing);
			world.setBlockState(pos, def);
			if(world.getBlockState(pos).getBlock().equals(ModBlocks.writingdesk)) {
				world.setBlockState(pos.add(xOffset, 0, zOffset), def.withProperty(BlockWritingDesk.IS_FOOT, true));
			}
			if(!player.isCreative()) {
                stack.shrink(1);
            }
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	private boolean isBlockReplaceable(World world, BlockPos pos, EnumFacing facing) {
		return world.mayPlace(ModBlocks.writingdesk, pos, true, facing, null);
	}

}
