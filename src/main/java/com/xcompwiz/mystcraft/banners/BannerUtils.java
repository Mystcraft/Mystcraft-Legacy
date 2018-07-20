package com.xcompwiz.mystcraft.banners;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.MystObjects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraftforge.common.util.EnumHelper;

public class BannerUtils {

	public static BannerPattern addBasicPattern(String name) {
		final Class<?>[] paramTypes = { String.class, String.class };
		final Object[] paramValues = { MystObjects.MystcraftModId + "_" + name, MystObjects.MystcraftModId + "." + name };
		BannerPattern pattern = EnumHelper.addEnum(BannerPattern.class, name.toUpperCase(), paramTypes, paramValues);
		Mystcraft.sidedProxy.registerBannerPattern(name, pattern);
		return pattern;
	}

	public static NBTTagList makePatternNBTList(BannerLayer... bannerLayers) {
		final NBTTagList patterns = new NBTTagList();
		for (final BannerLayer layer : bannerLayers) {
			final NBTTagCompound tag = new NBTTagCompound();
			tag.setString("Pattern", layer.pattern.getHashname());
			tag.setInteger("Color", layer.color.getDyeDamage());
			patterns.appendTag(tag);
		}
		return patterns;
	}
}
