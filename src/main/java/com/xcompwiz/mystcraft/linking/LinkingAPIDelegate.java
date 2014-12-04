package com.xcompwiz.mystcraft.linking;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.api.linking.ILinkingAPI;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;

public class LinkingAPIDelegate implements ILinkingAPI, ILinkPropertyAPI {

	@Override
	public boolean isLinkAllowed(Entity entity, ILinkInfo linkinfo) {
		return LinkListenerManager.isLinkPermitted(entity.worldObj, entity, linkinfo);
	}

	@Override
	public void linkEntity(Entity entity, ILinkInfo linkInfo) {
		LinkController.travelEntity(entity.worldObj, entity, linkInfo);
	}

	@Override
	public ILinkInfo createLinkInfoFromPosition(World world, Entity location) {
		LinkOptions link = new LinkOptions(null);
		link.setDimensionUID(world.provider.dimensionId);
		link.setSpawn(new ChunkCoordinates((int) Math.floor(location.posX), (int) Math.floor(location.posY), (int) Math.floor(location.posZ)));
		link.setSpawnYaw(location.rotationYaw);
		link.setDisplayName(DimensionUtils.getDimensionName(world.provider));
		return link;
	}

	@Override
	public ILinkInfo createLinkInfo(NBTTagCompound linkInfo) {
		return new LinkOptions(linkInfo);
	}

	@Override
	public void registerLinkProperty(String identifier, Color color) {
		InkEffects.registerProperty(identifier, color);
	}

	@Override
	public Collection<String> getLinkProperties() {
		return Collections.unmodifiableCollection(InkEffects.getProperties());
	}

	@Override
	public Color getLinkPropertyColor(String identifier) {
		return InkEffects.getPropertyColor(identifier);
	}

	private static final Color	defaultColor	= new Color(1, 1, 1);
	private static final Color	emptyColor		= new Color(0, 0, 0);

	@Override
	public ColorGradient getPropertiesGradient(Map<String, Float> properties) {
		ColorGradient gradient = new ColorGradient();
		float max = 1.0F;
		float total = 0;
		for (Entry<String, Float> entry : properties.entrySet()) {
			Color color = InkEffects.getPropertyColor(entry.getKey());
			if (entry.getValue() < 0.001F) continue;
			if (color == null) color = defaultColor;
			float interval = entry.getValue() * max;
			total += interval;
			if (interval > 0.3F) {
				gradient.pushColor(color, (interval - 0.3F));
				interval = 0.3F;
			}
			gradient.pushColor(color, interval);
		}
		if (total < max - 0.01F) {
			float interval = max - total;
			if (interval > 0.3F) {
				gradient.pushColor(emptyColor, (interval - 0.3F));
				interval = 0.3F;
			}
			gradient.pushColor(emptyColor, interval);
		}
		return gradient;
	}

	@Override
	public void addPropertyToItem(ItemStack itemstack, String property, float probability) {
		InkEffects.addPropertyToItem(itemstack, property, probability);
	}

	@Override
	public void addPropertyToItem(String name, String property, float probability) {
		InkEffects.addPropertyToItem(name, property, probability);
	}

	@Override
	public void addPropertyToItem(Item item, String property, float probability) {
		InkEffects.addPropertyToItem(item, property, probability);
	}

	@Override
	public Map<String, Float> getPropertiesForItem(ItemStack itemstack) {
		return InkEffects.getItemEffects(itemstack);
	}
}
