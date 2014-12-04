package com.xcompwiz.mystcraft.item;

import java.util.Collection;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.item.IItemPageAcceptor;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.treasure.WeightProviderSymbolItem;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBoosterPack extends Item {

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:booster");
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World worldObj, int x, int y, int z, int face, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
		if (worldObj.isRemote) return false;
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (world.isRemote) return itemstack;
		ItemStack newitemstack = generateBooster(entityplayer.getRNG(), 7, 4, 4, 1);
		if (newitemstack == null) return itemstack;
		itemstack.stackSize--;
		if (itemstack.stackSize <= 0) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			itemstack = newitemstack;
		} else {
			if (!entityplayer.inventory.addItemStackToInventory(newitemstack)) {
				++itemstack.stackSize;
				return itemstack;
			}
		}
		return itemstack;
	}

	//XXX: Generalize to allow for alternate rank sets (any rank, >=2, etc)
	public static ItemStack generateBooster(Random rand, int verycommon, int common, int uncommon, int rare) {
		ItemStack itemstack = new ItemStack(ModItems.folder, 1, 0);
		IItemPageAcceptor item = (IItemPageAcceptor) itemstack.getItem();

		Collection<IAgeSymbol> symbols_vc = SymbolManager.getSymbolsByRank(0);
		Collection<IAgeSymbol> symbols_c = SymbolManager.getSymbolsByRank(1);
		Collection<IAgeSymbol> symbols_uc = SymbolManager.getSymbolsByRank(2);
		Collection<IAgeSymbol> symbols_r = SymbolManager.getSymbolsByRank(3, null);

		for (int i = 0; i < verycommon; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_vc, WeightProviderSymbolItem.instance);
			item.addPage(null, itemstack, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < common; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_c, WeightProviderSymbolItem.instance);
			item.addPage(null, itemstack, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < uncommon; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_uc, WeightProviderSymbolItem.instance);
			item.addPage(null, itemstack, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < rare; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_r, WeightProviderSymbolItem.instance);
			item.addPage(null, itemstack, Page.createSymbolPage(symbol.identifier()));
		}
		return itemstack;
	}
}
