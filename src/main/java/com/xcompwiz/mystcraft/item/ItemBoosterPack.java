package com.xcompwiz.mystcraft.item;

import java.util.Collection;
import java.util.Random;

import com.xcompwiz.mystcraft.api.item.IItemPageAcceptor;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.treasure.WeightProviderSymbolItem;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		ItemStack newitemstack = generateBooster(null, entityplayer.getRNG(), 7, 4, 4, 1);
		if (newitemstack == null) return itemstack;
		itemstack.stackSize--;
		if (itemstack.getCount() <= 0) {
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
	public static ItemStack generateBooster(ItemStack itemstack, Random rand, int verycommon, int common, int uncommon, int rare) {
		if (itemstack == null) itemstack = new ItemStack(ModItems.folder, 1, 0);
		IItemPageAcceptor item = (IItemPageAcceptor) itemstack.getItem();

		Collection<IAgeSymbol> symbols_vc = SymbolManager.getSymbolsByRank(0);
		Collection<IAgeSymbol> symbols_c = SymbolManager.getSymbolsByRank(1);
		Collection<IAgeSymbol> symbols_uc = SymbolManager.getSymbolsByRank(2);
		Collection<IAgeSymbol> symbols_r = SymbolManager.getSymbolsByRank(3, null);

		addRandomPages(rand, item, itemstack, verycommon, symbols_vc);
		addRandomPages(rand, item, itemstack, common, symbols_c);
		addRandomPages(rand, item, itemstack, uncommon, symbols_uc);
		addRandomPages(rand, item, itemstack, rare, symbols_r);
		return itemstack;
	}

	private static void addRandomPages(Random rand, IItemPageAcceptor item, ItemStack itemstack, int count, Collection<IAgeSymbol> collection) {
		for (int i = 0; i < count; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, collection, WeightProviderSymbolItem.instance);
			if (checkerr(symbol, collection)) continue;
			item.addPage(null, itemstack, Page.createSymbolPage(symbol.identifier()));
		}
	}

	private static boolean checkerr(IAgeSymbol symbol, Collection<IAgeSymbol> collection) {
		if (symbol == null) {
			LoggerUtils.error("Symbol from random selection null (%s)", collection.toString());
			return true;
		}
		return false;
	}
}
