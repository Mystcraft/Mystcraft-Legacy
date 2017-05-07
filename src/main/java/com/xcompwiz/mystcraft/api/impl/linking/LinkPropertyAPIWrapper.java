package com.xcompwiz.mystcraft.api.impl.linking;

import java.util.Collection;
import java.util.Map;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LinkPropertyAPIWrapper extends APIWrapper implements LinkPropertyAPI {

	public LinkPropertyAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public void registerLinkProperty(String identifier, Color color) {
		InternalAPI.linkProperties.registerLinkProperty(identifier, color);
	}

	@Override
	public Collection<String> getLinkProperties() {
		return InternalAPI.linkProperties.getLinkProperties();
	}

	@Override
	public Color getLinkPropertyColor(String identifier) {
		return InternalAPI.linkProperties.getLinkPropertyColor(identifier);
	}

	@Override
	public ColorGradient getPropertiesGradient(Map<String, Float> properties) {
		return InternalAPI.linkProperties.getPropertiesGradient(properties);
	}

	@Override
	public void addPropertyToItem(ItemStack itemstack, String property, float probability) {
		InternalAPI.linkProperties.addPropertyToItem(itemstack, property, probability);
	}

	@Override
	public void addPropertyToItem(String name, String property, float probability) {
		InternalAPI.linkProperties.addPropertyToItem(name, property, probability);
	}

	@Override
	public void addPropertyToItem(Item item, String property, float probability) {
		InternalAPI.linkProperties.addPropertyToItem(item, property, probability);
	}

	@Override
	public Map<String, Float> getPropertiesForItem(ItemStack itemstack) {
		return InternalAPI.linkProperties.getPropertiesForItem(itemstack);
	}

}
