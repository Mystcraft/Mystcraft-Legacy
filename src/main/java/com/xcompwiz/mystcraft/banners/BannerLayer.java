package com.xcompwiz.mystcraft.banners;

import javax.annotation.concurrent.Immutable;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.BannerPattern;

@Immutable
public class BannerLayer {

    public final BannerPattern pattern;
    public final EnumDyeColor color;

    public BannerLayer (BannerPattern pattern, EnumDyeColor color) {

        this.pattern = pattern;
        this.color = color;
    }

    public BannerPattern getPattern () {

        return this.pattern;
    }

    public EnumDyeColor getColor () {

        return this.color;
    }
}