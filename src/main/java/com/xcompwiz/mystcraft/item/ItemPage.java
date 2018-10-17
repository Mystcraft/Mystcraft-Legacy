package com.xcompwiz.mystcraft.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.api.item.IItemWritable;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.page.SortingUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPage extends Item implements IItemRenameable, IItemWritable, IItemPageProvider, IItemOnLoadable {

	public ItemPage() {
		setMaxStackSize(64);
		setHasSubtypes(true);
		setUnlocalizedName("myst.page");
		setCreativeTab(MystcraftCommonProxy.tabMystPages);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			items.add(Page.createLinkPage());

			ArrayList<String> linkproperties = new ArrayList<>();
			linkproperties.addAll(InkEffects.getProperties());
			Collections.sort(linkproperties);
			for (String property : linkproperties) {
				if (property.equals(LinkPropertyAPI.FLAG_RELATIVE))
					continue;
				items.add(Page.createLinkPage(property));
			}

			ArrayList<IAgeSymbol> symbols = SymbolManager.getAgeSymbols();
			symbols.sort(SortingUtils.ComparatorSymbolAlphabetical.instance);
			for (IAgeSymbol symbol : symbols) {
				items.add(Page.createSymbolPage(symbol.getRegistryName()));
			}
		}
	}

	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(@Nonnull ItemStack itemstack) {
		if (itemstack.getTagCompound() != null) {
			if (Page.isLinkPanel(itemstack))
				return I18n.format(this.getUnlocalizedName(itemstack) + ".panel.name");
			if (Page.isBlank(itemstack))
				return I18n.format(this.getUnlocalizedName(itemstack) + ".blank.name");
			ResourceLocation symbolId = Page.getSymbol(itemstack);
			IAgeSymbol symbol = SymbolManager.getAgeSymbol(symbolId);
			if (symbol == null) {
				return I18n.format(this.getUnlocalizedName(itemstack) + ".symbol.name") + " (Unknown: " + symbolId + ")";
			}
			return I18n.format(this.getUnlocalizedName(itemstack) + ".symbol.name") + " (" + symbol.getLocalizedName() + ")";
		}
		return I18n.format(this.getUnlocalizedName(itemstack) + ".blank.name");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack itemstack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (itemstack.getTagCompound() != null) {
			Page.getTooltip(itemstack, tooltip);
		}
	}

	@Override
	public void onUpdate(@Nonnull ItemStack itemstack, World world, Entity entity, int itemSlot, boolean flag) {
		if (world.isRemote) {
			return;
		}
		if (Page.isBlank(itemstack)) {
			itemstack.setTagCompound(null); //Hellfire> we're actively erasing potentially a lot of data?...
		}
		if (itemstack.getTagCompound() == null) {
			//itemstack = new ItemStack(Items.PAPER); Hellfire> unnecessary stack change.
			return;
		}
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) entity);
			remapItemstack(player, itemstack, itemSlot);
		}
	}

	private static void remapItemstack(@Nonnull ItemStack stack) {
		remapItemstack(null, stack, -1);
	}

	//Hellfire> pass on to here to properly change the itemstack in the player's inv
	public static void remapItemstack(EntityPlayer player, @Nonnull ItemStack itemstack, int itemSlot) {
		List<ItemStack> mapping = SymbolRemappings.remap(itemstack);
		if (mapping.size() == 0) {
			itemstack.setCount(0);
		}
		if (mapping.size() != 1 && player != null) {
			ItemStack folder = new ItemStack(ModItems.folder);
			IItemOrderablePageProvider item = (IItemOrderablePageProvider) folder.getItem();
			folder.setTagCompound(new NBTTagCompound());
			for (ItemStack mappeditemstack : mapping) {
				item.addPage(player, folder, mappeditemstack);
			}
			player.inventory.setInventorySlotContents(itemSlot, folder); //Hellfire> Update stack
		}
	}

	@Nonnull
	public static ItemStack createItemstack(@Nonnull ItemStack prototype) {
		if (prototype.getItem() == Items.PAPER) {
			prototype.shrink(1);
			return Page.createPage();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public String getDisplayName(EntityPlayer player, @Nonnull ItemStack itemstack) {
		return this.getItemStackDisplayName(itemstack);
	}

	@Override
	public void setDisplayName(EntityPlayer player, @Nonnull ItemStack itemstack, String name) {}

	@Override
	public boolean writeSymbol(EntityPlayer player, @Nonnull ItemStack itemstack, ResourceLocation symbol) {
		if (!Page.isBlank(itemstack))
			return false;
		Page.setSymbol(itemstack, symbol);
		return true;
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, @Nonnull ItemStack itemstack) {
		return Collections.singletonList(itemstack);
	}

	@Override
	@Nonnull
	public ItemStack onLoad(@Nonnull ItemStack itemstack) {
		remapItemstack(itemstack);
		return itemstack;
	}
}
