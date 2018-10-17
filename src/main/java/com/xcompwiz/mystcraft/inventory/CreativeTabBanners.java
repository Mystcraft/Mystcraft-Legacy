package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.banners.BannerLayer;
import com.xcompwiz.mystcraft.banners.BannerUtils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.NonNullList;

public class CreativeTabBanners extends CreativeTabs {

    private static ItemStack DISPLAY = null;
	private ArrayList<BannerPattern> patterns = new ArrayList<BannerPattern>();

    public CreativeTabBanners (String id) {

        super(id);
        this.setBackgroundImageName("item_search.png");
    }

    @Override
    public ItemStack getTabIconItem () {

        return this.getIconItemStack();
    }

    @Override
    public ItemStack getIconItemStack () {

        if (DISPLAY == null)
            DISPLAY = ItemBanner.makeBanner(EnumDyeColor.RED, BannerUtils.makePatternNBTList(new BannerLayer(patterns.get(0), EnumDyeColor.YELLOW)));

        return DISPLAY;
    }

    @Override
    public boolean hasSearchBar () {
        return true;
    }
    
    @Override
    public void displayAllRelevantItems (NonNullList<ItemStack> itemList) {
        super.displayAllRelevantItems(itemList);

        for (final BannerPattern pattern : patterns)
        	itemList.add(ItemBanner.makeBanner(EnumDyeColor.RED, BannerUtils.makePatternNBTList(new BannerLayer(pattern, EnumDyeColor.YELLOW))));
    }

	public void registerPattern(BannerPattern pattern) {
		patterns.add(pattern);
	}
}