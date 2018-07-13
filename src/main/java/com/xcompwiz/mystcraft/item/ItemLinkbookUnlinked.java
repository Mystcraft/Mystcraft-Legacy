package com.xcompwiz.mystcraft.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLinkbookUnlinked extends Item {

	public ItemLinkbookUnlinked() {
		setMaxStackSize(16);
		setUnlocalizedName("myst.unlinkedbook");
		setCreativeTab(MystcraftCommonProxy.tabMystCommon);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack itemstack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (itemstack.getTagCompound() != null) {
			Page.getTooltip(itemstack, tooltip);
		}
	}

	public static ItemStack createItem(@Nonnull ItemStack linkpanel, @Nonnull ItemStack covermat) {
		ItemStack linkbook = new ItemStack(ModItems.unlinked);
		NBTTagCompound prev = linkpanel.getTagCompound();
		if (prev == null) {
			prev = new NBTTagCompound();
		}
		linkbook.setTagCompound(prev.copy());
		return linkbook;
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
		ItemStack inHand = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote || inHand.getCount() > 1) {
			return ActionResult.newResult(EnumActionResult.PASS, inHand);
		}
		ItemStack linkBook = new ItemStack(ModItems.linkbook);
		((ItemLinkbook) ModItems.linkbook).initialize(worldIn, linkBook, playerIn);
		Page.applyLinkPanel(inHand, linkBook);
		playerIn.setHeldItem(handIn, linkBook);
		inHand.setCount(0);
		return ActionResult.newResult(EnumActionResult.PASS, linkBook);
	}

}
