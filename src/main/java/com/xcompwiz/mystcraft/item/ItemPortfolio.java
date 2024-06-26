package com.xcompwiz.mystcraft.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPortfolio extends Item implements IItemPageCollection, IItemRenameable, IItemOnLoadable {

	public ItemPortfolio() {
		setMaxStackSize(1);
		setUnlocalizedName("myst.portfolio");
		setCreativeTab(MystcraftCommonProxy.tabMystCommon);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack itemstack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String name = this.getDisplayName(Minecraft.getMinecraft().player, itemstack);
		if (name != null) {
			tooltip.add(name);
		}
	}

	@Override
	@Nonnull
	public EnumRarity getRarity(@Nonnull ItemStack itemstack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
		ItemStack held = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote && held.getCount() == 1) {
			playerIn.openGui(Mystcraft.instance, ModGUIs.PORTFOLIO.ordinal(), worldIn, MathHelper.floor(playerIn.posX + 0.5D), MathHelper.floor(playerIn.posY + 0.5D), MathHelper.floor(playerIn.posZ + 0.5D));
		}
		return ActionResult.newResult(EnumActionResult.PASS, held);
	}

	@Override
	@Nullable
	public String getDisplayName(@Nullable EntityPlayer player, @Nonnull ItemStack stack) {
		if (stack.isEmpty())
			return null;
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if (!stack.getTagCompound().hasKey("Name")) {
			return null;
		}
		return stack.getTagCompound().getString("Name");
	}

	@Override
	public void setDisplayName(EntityPlayer player, @Nonnull ItemStack stack, String name) {
		if (stack.isEmpty())
			return;
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if (name == null || name.equals("")) {
			stack.getTagCompound().removeTag("Name");
		} else {
			stack.getTagCompound().setString("Name", name);
		}
	}

	public static boolean isItemValid(@Nonnull ItemStack itemstack) {
		return !itemstack.isEmpty() && itemstack.getItem().equals(ModItems.page);
	}

	@Override
	@Nonnull
	public ItemStack remove(EntityPlayer player, @Nonnull ItemStack itemstack, @Nonnull ItemStack page) {
		if (itemstack.isEmpty() || page.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		Collection<NBTTagCompound> compounds = NBTUtils.readTagCompoundCollection(itemstack.getTagCompound().getTagList("Collection", Constants.NBT.TAG_COMPOUND), new LinkedList<>());
		NBTTagCompound nbt = new NBTTagCompound();
		int count = page.getCount();
		page.setCount(1);
		page.writeToNBT(nbt);

		int removed = 0;
		//page.stackSize = 0;
		// Hellfire> The old way of doing this was critical and shouldn't be done anymore. the stack looses all data
		// 'potentially' upon setting 0 due to read/write actions. i don't wanna risk that. Changed to use arbitrary integer instead.
		while (removed < count && compounds.remove(nbt)) {
			removed++;
		}
		if (removed <= 0) {
			return ItemStack.EMPTY;
		}
		itemstack.getTagCompound().setTag("Collection", NBTUtils.writeTagCompoundCollection(new NBTTagList(), compounds));
		page.setCount(removed);
		return page;
	}

	@Override
	@Nonnull
	public ItemStack addPage(EntityPlayer player, @Nonnull ItemStack itemstack, @Nonnull ItemStack page) {
		if (itemstack.isEmpty() || page.isEmpty()) {
			return page;
		}
		if (page.getItem() instanceof IItemPageCollection) {
			if (page.getCount() != 1)
				return page;
			IItemPageCollection otheritem = (IItemPageCollection) page.getItem();
			List<ItemStack> pages = otheritem.getItems(player, page);
			for (ItemStack p : pages) {
				ItemStack out = this.addPage(player, itemstack, otheritem.remove(player, page, p));
				if (!out.isEmpty()) {
					otheritem.addPage(player, page, out);
				}
			}
			return page;
		}
		if (page.getItem() instanceof IItemOrderablePageProvider) {
			if (page.getCount() != 1)
				return page;
			IItemOrderablePageProvider otheritem = (IItemOrderablePageProvider) page.getItem();
			List<ItemStack> pages = otheritem.getPageList(player, page);
			for (int i = 0; i < pages.size(); ++i) {
				ItemStack out = this.addPage(player, itemstack, otheritem.removePage(player, page, i));
				if (!out.isEmpty()) {
					otheritem.addPage(player, page, out);
				}
			}
			return page;
		}
		if (!isItemValid(page))
			return page;
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		Collection<NBTTagCompound> compounds = NBTUtils.readTagCompoundCollection(itemstack.getTagCompound().getTagList("Collection", Constants.NBT.TAG_COMPOUND), new LinkedList<>());
		NBTTagCompound nbt = new NBTTagCompound();
		int count = page.getCount();
		page.setCount(1);

		for (int i = 0; i < count; ++i) {
			page.writeToNBT(nbt);
			compounds.add(nbt);
		}
		itemstack.getTagCompound().setTag("Collection", NBTUtils.writeTagCompoundCollection(new NBTTagList(), compounds));
		return ItemStack.EMPTY;
	}

	@Override
	@Nullable
	public List<ItemStack> getItems(EntityPlayer player, @Nonnull ItemStack itemstack) {
		if (itemstack.isEmpty())
			return null;
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		Collection<NBTTagCompound> compounds = NBTUtils.readTagCompoundCollection(itemstack.getTagCompound().getTagList("Collection", Constants.NBT.TAG_COMPOUND), new LinkedList<>());
		List<ItemStack> items = new ArrayList<>();
		for (NBTTagCompound nbt : compounds) {
			ItemStack page = new ItemStack(nbt);
			items.add(page);
		}
		return items;
	}

	@Override
	@Nonnull
	public ItemStack onLoad(@Nonnull ItemStack itemstack) {
		updatePages(itemstack);
		return itemstack;
	}
	
	/**
	 * Performs some basic housekeeping. Handles symbol remappings
	 */
	public static void updatePages(@Nonnull ItemStack itemstack) {
		Collection<NBTTagCompound> compounds = NBTUtils.readTagCompoundCollection(itemstack.getTagCompound().getTagList("Collection", Constants.NBT.TAG_COMPOUND), new LinkedList<>());
		List<ItemStack> items = new ArrayList<>();
		for (NBTTagCompound nbt : compounds) {
			ItemStack page = new ItemStack(nbt);
			List<ItemStack> results = SymbolRemappings.remap(page);
			items.addAll(results);
		}
		
		compounds.clear();
		for (ItemStack page : items) {
			NBTTagCompound nbt = new NBTTagCompound();
			page.writeToNBT(nbt);
			compounds.add(nbt);
		}
		itemstack.getTagCompound().setTag("Collection", NBTUtils.writeTagCompoundCollection(new NBTTagList(), compounds));
	}
}
