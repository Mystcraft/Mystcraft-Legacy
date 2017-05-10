package com.xcompwiz.mystcraft.api.impl.linking;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkListenerManager;
import com.xcompwiz.mystcraft.linking.LinkOptions;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LinkingAPIDelegate {

	public boolean isLinkAllowed(Entity entity, ILinkInfo linkinfo) {
		return LinkListenerManager.isLinkPermitted(entity.world, entity, linkinfo);
	}

	public void linkEntity(Entity entity, ILinkInfo linkInfo) {
		LinkController.travelEntity(entity.world, entity, linkInfo);
	}

	public ILinkInfo createLinkInfoFromPosition(World world, Entity location) {
		LinkOptions link = new LinkOptions(null);
		link.setDimensionUID(world.provider.getDimension());
		link.setTargetUUID(DimensionUtils.getDimensionUUID(world.provider.getDimension()));
		link.setSpawn(new BlockPos(location));
		link.setSpawnYaw(location.rotationYaw);
		link.setDisplayName(DimensionUtils.getDimensionName(world.provider));
		return link;
	}

	public ILinkInfo createLinkInfo(NBTTagCompound linkInfo) {
		return new LinkOptions(linkInfo);
	}

	public void registerLinkProperty(String identifier, Color color) {
		InkEffects.registerProperty(identifier, color);
	}

	public Collection<String> getLinkProperties() {
		return Collections.unmodifiableCollection(InkEffects.getProperties());
	}

	public Color getLinkPropertyColor(String identifier) {
		return InkEffects.getPropertyColor(identifier);
	}

	private static final Color	defaultColor	= new Color(1, 1, 1);
	private static final Color	emptyColor		= new Color(0, 0, 0);

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

	public void addPropertyToItem(ItemStack itemstack, String property, float probability) {
		InkEffects.addPropertyToItem(itemstack, property, probability);
	}

	public void addPropertyToItem(String name, String property, float probability) {
		InkEffects.addPropertyToItem(name, property, probability);
	}

	public void addPropertyToItem(Item item, String property, float probability) {
		InkEffects.addPropertyToItem(item, property, probability);
	}

	public Map<String, Float> getPropertiesForItem(ItemStack itemstack) {
		return InkEffects.getItemEffects(itemstack);
	}
}
