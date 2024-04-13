package com.xcompwiz.mystcraft.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.nbt.NBTUtils;

public abstract class Page {

	public static void setQuality(ItemStack page, String trait, int quality) {
		getQualityStruct(page).setInteger(trait, quality);
	}

	public static int getTotalQuality(ItemStack page) {
		NBTTagCompound compound = getQualityStruct(page);
		int sum = 0;
		for (Object tagname : compound.func_150296_c()) {
			sum += compound.getInteger((String) tagname);
		}
		return sum;
	}

	public static Integer getQuality(ItemStack page, String trait) {
		NBTTagCompound data = getQualityStruct(page);
		if (data.hasKey(trait)) return data.getInteger(trait);
		return null;
	}

	private static NBTTagCompound getData(ItemStack item) {
		if (item == null) return null;
		return item.stackTagCompound;
	}

	private static NBTTagCompound getQualityStruct(ItemStack page) {
		NBTTagCompound data = getData(page);
		if (data == null) return null;
		if (!data.hasKey("Quality")) {
			data.setTag("Quality", new NBTTagCompound());
		}
		return data.getCompoundTag("Quality");
	}

	public static boolean isBlank(ItemStack page) {
		return !isLinkPanel(page) && getSymbol(page) == null;
	}

	public static boolean isLinkPanel(ItemStack page) {
		NBTTagCompound data = getData(page);
		if (data == null) return false;
		return data.hasKey("linkpanel");
	}

	public static void makeLinkPanel(ItemStack page) {
		NBTTagCompound data = getData(page);
		if (data == null) {
			page.stackTagCompound = createDefault();
			data = getData(page);
		}
		if (!data.hasKey("linkpanel")) {
			data.setTag("linkpanel", new NBTTagCompound());
		}
	}

	public static void addLinkProperty(ItemStack page, String linkproperty) {
		if (page == null) return;
		NBTTagCompound data = getData(page);
		if (data == null) {
			page.stackTagCompound = createDefault();
			data = getData(page);
		}
		if (!data.hasKey("linkpanel")) {
			data.setTag("linkpanel", new NBTTagCompound());
		}
		NBTTagCompound linkpanel = data.getCompoundTag("linkpanel");
		NBTTagList list = linkpanel.getTagList("properties", Constants.NBT.TAG_STRING);
		list.appendTag(new NBTTagString(linkproperty));
		linkpanel.setTag("properties", list);
	}

	public static Collection<String> getLinkProperties(ItemStack page) {
		NBTTagCompound data = getData(page);
		if (data == null) return null;
		if (!data.hasKey("linkpanel")) return null;
		NBTTagCompound linkpanel = data.getCompoundTag("linkpanel");
		ArrayList<String> properties = NBTUtils.readStringCollection(linkpanel.getTagList("properties", Constants.NBT.TAG_STRING), new ArrayList<String>());
		return properties;
	}

	//XXX: This is a weird way to do this now
	public static void applyLinkPanel(ItemStack linkpanel, ItemStack linkingitem) {
		Collection<String> properties = getLinkProperties(linkpanel);
		if (properties == null) return;
		for (String property : properties) {
			LinkOptions.setFlag(linkingitem.stackTagCompound, property, true);
		}
	}

	public static void setSymbol(ItemStack page, String symbol) {
		NBTTagCompound data = getData(page);
		if (data == null) return;
		if (symbol == null) {
			data.removeTag("symbol");
		} else {
			data.setString("symbol", symbol);
		}
	}

	public static String getSymbol(ItemStack page) {
		NBTTagCompound data = getData(page);
		if (data == null) return null;
		String symbol = data.getString("symbol");
		if (symbol.equals("")) symbol = null;
		return symbol;
	}

	public static void getTooltip(ItemStack page, List<String> list) {
		if (isLinkPanel(page)) {
			Collection<String> properties = getLinkProperties(page);
			if (properties != null && properties.size() > 0) {
				for (String property : properties) {
					list.add(InkEffects.getLocalizedName(property));
				}
			}
		}
	}

	public static NBTTagCompound createDefault() {
		return new NBTTagCompound();
	};

	public static ItemStack createPage() {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.stackTagCompound = createDefault();
		return page;
	};

	public static ItemStack createLinkPage() {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.stackTagCompound = createDefault();
		makeLinkPanel(page);
		return page;
	};

	public static ItemStack createLinkPage(String property) {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.stackTagCompound = createDefault();
		addLinkProperty(page, property);
		return page;
	};

	public static ItemStack createSymbolPage(String symbol) {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.stackTagCompound = createDefault();
		setSymbol(page, symbol);
		return page;
	};

	public static ItemStack createPage(NBTTagCompound pagedata) {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.stackTagCompound = pagedata;
		return page;
	}

}
