package com.xcompwiz.mystcraft.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.util.Color;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class InkEffects {

	public static class CompareItemStack implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack paramT1, ItemStack paramT2) {
			if (paramT1 == paramT2)
				return 0;
			if (ItemStack.areItemStacksEqual(paramT1, paramT2))
				return 0;
			if (Item.getIdFromItem(paramT1.getItem()) < Item.getIdFromItem(paramT2.getItem()))
				return -1;
			if (Item.getIdFromItem(paramT1.getItem()) > Item.getIdFromItem(paramT2.getItem()))
				return 1;
			if (paramT1.getItemDamage() < paramT2.getItemDamage())
				return -1;
			if (paramT1.getItemDamage() > paramT2.getItemDamage())
				return 1;
			if (paramT1.getCount() < paramT2.getCount())
				return -1;
			if (paramT1.getCount() > paramT2.getCount())
				return 1;
			if (!paramT1.hasTagCompound())
				return -1;
			if (!paramT2.hasTagCompound())
				return 1;
			return paramT1.toString().compareTo(paramT2.toString());
		}
	}

	private static final HashMap<String, Color> colormap = new HashMap<String, Color>();
	private static Map<ItemStack, Map<String, Float>> itemstack_bindings = new TreeMap<ItemStack, Map<String, Float>>(new CompareItemStack());
	private static Map<String, Map<String, Float>> oredict_bindings = new HashMap<String, Map<String, Float>>();
	private static Map<Item, Map<String, Float>> itemId_bindings = new HashMap<Item, Map<String, Float>>();

	public static Set<String> getProperties() {
		return colormap.keySet();
	}

	public static void registerProperty(String key, Color color) {
		colormap.put(key, color);
	}

	public static Color getPropertyColor(String key) {
		return colormap.get(key);
	}

	public static String getLocalizedName(String property) {
		return I18n.format(getUnlocalizedName(property));
	}

	public static String getUnlocalizedName(String property) {
		return "linkeffect." + property.replaceAll(" ", "").toLowerCase() + ".name";
	}

	public static Map<String, Float> getItemEffects(ItemStack itemstack) {
		ItemStack clone = itemstack.copy();
		clone.setCount(1);
		Map<String, Float> map = itemstack_bindings.get(clone);
		int[] ids = OreDictionary.getOreIDs(itemstack);
		for (int id : ids) {
			if (map == null)
				map = oredict_bindings.get(OreDictionary.getOreName(id));
		}
		if (map == null)
			map = itemId_bindings.get(itemstack.getItem());
		if (map == null)
			return null;
		return Collections.unmodifiableMap(map);
	}

	private static void addPropertyToMap(Map<String, Float> itemmap, String property, float probability) {
		Float f = itemmap.get(property);
		if (f != null)
			probability += f;
		itemmap.put(property, probability);

		// Sanity checking
		float total = 0;
		for (Entry<String, Float> entry : itemmap.entrySet()) {
			total += entry.getValue();
		}
		if (total > 1) {
			throw new RuntimeException("ERROR: Total of all ink property probabilities from an item cannot exceed 1!");
		}
	}

	public static void addPropertyToItem(ItemStack itemstack, String property, float probability) {
		itemstack = itemstack.copy();
		itemstack.setCount(1);
		Map<String, Float> itemmap = itemstack_bindings.get(itemstack);
		if (itemmap == null) {
			itemmap = new HashMap<String, Float>();
			itemstack_bindings.put(itemstack, itemmap);
		}
		addPropertyToMap(itemmap, property, probability);
	}

	public static void addPropertyToItem(String name, String property, float probability) {
		Map<String, Float> itemmap = oredict_bindings.get(name);
		if (itemmap == null) {
			itemmap = new HashMap<String, Float>();
			oredict_bindings.put(name, itemmap);
		}
		addPropertyToMap(itemmap, property, probability);
	}

	public static void addPropertyToItem(Item item, String property, float probability) {
		Map<String, Float> itemmap = itemId_bindings.get(item);
		if (itemmap == null) {
			itemmap = new HashMap<String, Float>();
			itemId_bindings.put(item, itemmap);
		}
		addPropertyToMap(itemmap, property, probability);
	}

	public static void init() {
		registerProperty(LinkPropertyAPI.FLAG_INTRA_LINKING, new Color(0, 1, 0));
		registerProperty(LinkPropertyAPI.FLAG_INTRA_LINKING_ONLY, new Color(1, 1, 1));
		registerProperty(LinkPropertyAPI.FLAG_GENERATE_PLATFORM, new Color(0.5F, 0.5F, 0.5F));
		registerProperty(LinkPropertyAPI.FLAG_MAINTAIN_MOMENTUM, new Color(0, 0, 1));
		registerProperty(LinkPropertyAPI.FLAG_DISARM, new Color(1, 0, 0));
		registerProperty(LinkPropertyAPI.FLAG_RELATIVE, new Color(0.6F, 0, 0.6F));

		addPropertyToItem(new ItemStack(Items.GUNPOWDER), LinkPropertyAPI.FLAG_DISARM, 0.2F);

		addPropertyToItem(new ItemStack(Items.MUSHROOM_STEW), LinkPropertyAPI.FLAG_DISARM, 0.05F);

		addPropertyToItem(new ItemStack(Items.CLAY_BALL), LinkPropertyAPI.FLAG_GENERATE_PLATFORM, 0.25F);

		addPropertyToItem(new ItemStack(Items.EXPERIENCE_BOTTLE), LinkPropertyAPI.FLAG_INTRA_LINKING, 0.15F);

		addPropertyToItem("dyeBlack", "", 0.5F); // Black Dye

		addPropertyToItem(new ItemStack(Items.ENDER_PEARL), LinkPropertyAPI.FLAG_INTRA_LINKING, 0.15F);
		addPropertyToItem(new ItemStack(Items.ENDER_PEARL), LinkPropertyAPI.FLAG_DISARM, 0.15F);

		addPropertyToItem(new ItemStack(Items.FEATHER), LinkPropertyAPI.FLAG_MAINTAIN_MOMENTUM, 0.15F);

		// addPropertyToItem(Items.ghastTear, LinkOptions.FLAG_RELATIVE, 0.15F);

		addPropertyToItem(Items.FIRE_CHARGE, LinkPropertyAPI.FLAG_DISARM, 0.25F);

		// addPropertyToItem(Items.redstone), "Self-Powered", 1.0F);
		// addPropertyToItem(Items.dyePowder, 4, "", 0.0F); //Lapis Lazuli

		addPropertyToItem("dustBrass", LinkPropertyAPI.FLAG_DISARM, 0.15F);
		addPropertyToItem("dustBronze", LinkPropertyAPI.FLAG_DISARM, 0.15F);
		// addPropertyToItem("dustCoal", LinkOptions.FLAG_INTRA_LINKING, 0.5F);
		addPropertyToItem("dustTin", LinkPropertyAPI.FLAG_GENERATE_PLATFORM, 0.1F);
		addPropertyToItem("dustTin", LinkPropertyAPI.FLAG_INTRA_LINKING, 0.1F);
		addPropertyToItem("dustIron", LinkPropertyAPI.FLAG_GENERATE_PLATFORM, 0.15F);
		addPropertyToItem("dustIron", LinkPropertyAPI.FLAG_INTRA_LINKING, 0.15F);
		addPropertyToItem("dustLead", LinkPropertyAPI.FLAG_DISARM, 0.2F);
		addPropertyToItem("dustLead", LinkPropertyAPI.FLAG_INTRA_LINKING, 0.2F);
		addPropertyToItem("dustSilver", LinkPropertyAPI.FLAG_GENERATE_PLATFORM, 0.2F);
		addPropertyToItem("dustSilver", LinkPropertyAPI.FLAG_INTRA_LINKING, 0.2F);
		addPropertyToItem("dustDiamond", LinkPropertyAPI.FLAG_INTRA_LINKING, 0.25F);
		addPropertyToItem("dustDiamond", LinkPropertyAPI.FLAG_MAINTAIN_MOMENTUM, 0.1F);
		addPropertyToItem("dustDiamond", LinkPropertyAPI.FLAG_GENERATE_PLATFORM, 0.1F);
		addPropertyToItem("dustGold", LinkPropertyAPI.FLAG_INTRA_LINKING, 0.25F);
		addPropertyToItem("dustGold", LinkPropertyAPI.FLAG_GENERATE_PLATFORM, 0.1F);
		addPropertyToItem("dustGold", LinkPropertyAPI.FLAG_DISARM, 0.1F);
		// addPropertyToItem("dustCopper", LinkOptions.FLAG_RELATIVE, 0.05F);
	}
}
