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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemBoosterPack extends Item {

	public ItemBoosterPack() {
		setUnlocalizedName("myst.booster");
		setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote) {
			return EnumActionResult.PASS;
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack held = playerIn.getHeldItem(handIn);
		if(worldIn.isRemote) {
			return ActionResult.newResult(EnumActionResult.PASS, held);
		}
		ItemStack newBooster = generateBooster(ItemStack.EMPTY, playerIn.getRNG(), 7, 4, 4, 1);
		if(newBooster.isEmpty()) {
			return ActionResult.newResult(EnumActionResult.PASS, held);
		}
		held.shrink(1);
		if(held.isEmpty()) {
			playerIn.setHeldItem(handIn, ItemStack.EMPTY);
			held = newBooster;
		} else {
			if(!playerIn.inventory.addItemStackToInventory(newBooster)) {
				held.grow(1);
				return ActionResult.newResult(EnumActionResult.PASS, held);
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, held);
	}

	//XXX: Generalize to allow for alternate rank sets (any rank, >=2, etc)
	@Nonnull
	public static ItemStack generateBooster(@Nonnull ItemStack itemstack, Random rand, int verycommon, int common, int uncommon, int rare) {
		if (itemstack.isEmpty()) {
			itemstack = new ItemStack(ModItems.folder, 1, 0);
		}
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

	private static void addRandomPages(Random rand, IItemPageAcceptor item, @Nonnull ItemStack itemstack, int count, Collection<IAgeSymbol> collection) {
		for (int i = 0; i < count; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, collection, WeightProviderSymbolItem.instance);
			if (checker(symbol, collection)) continue;
			item.addPage(null, itemstack, Page.createSymbolPage(symbol.identifier()));
		}
	}

	private static boolean checker(IAgeSymbol symbol, Collection<IAgeSymbol> collection) {
		if (symbol == null) {
			LoggerUtils.error("Symbol from random selection null (%s)", collection.toString());
			return true;
		}
		return false;
	}
}
