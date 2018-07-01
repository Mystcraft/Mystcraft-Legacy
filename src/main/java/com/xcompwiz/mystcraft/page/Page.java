package com.xcompwiz.mystcraft.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.nbt.NBTUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Page {

	public static void setQuality(@Nonnull ItemStack page, String trait, int quality) {
		getQualityStruct(page).setInteger(trait, quality);
	}

	public static int getTotalQuality(@Nonnull ItemStack page) {
		NBTTagCompound compound = getQualityStruct(page);
		int sum = 0;
		for (String tagname : compound.getKeySet()) {
			sum += compound.getInteger(tagname);
		}
		return sum;
	}

	@Nullable
	public static Integer getQuality(@Nonnull ItemStack page, String trait) {
		NBTTagCompound data = getQualityStruct(page);
		if (data.hasKey(trait))
			return data.getInteger(trait);
		return null;
	}

	//Hellfire> NEEDS to be never null. otherwise we'd sometimes get an invalid model location from the page and we crash
	@Nonnull
	private static NBTTagCompound getData(@Nonnull ItemStack item) {
		if (item.isEmpty())
			return new NBTTagCompound();
		NBTTagCompound tag = item.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			item.setTagCompound(tag);
		}
		return tag;
	}

	@Nonnull
	private static NBTTagCompound getQualityStruct(@Nonnull ItemStack page) {
		NBTTagCompound data = getData(page);
		if (!data.hasKey("Quality")) {
			data.setTag("Quality", new NBTTagCompound());
		}
		return data.getCompoundTag("Quality");
	}

	public static boolean isBlank(@Nonnull ItemStack page) {
		return !isLinkPanel(page) && getSymbol(page) == null;
	}

	public static boolean isLinkPanel(@Nonnull ItemStack page) {
		return getData(page).hasKey("linkpanel");
	}

	public static void makeLinkPanel(@Nonnull ItemStack page) {
		if (page.isEmpty())
			return;

		if (page.getTagCompound() == null) {
			page.setTagCompound(createDefault());
		}
		NBTTagCompound data = getData(page);
		if (!data.hasKey("linkpanel")) {
			data.setTag("linkpanel", new NBTTagCompound());
		}
	}

	public static void addLinkProperty(@Nonnull ItemStack page, String linkproperty) {
		if (page.isEmpty())
			return;

		if (page.getTagCompound() == null) {
			page.setTagCompound(createDefault());
		}
		NBTTagCompound data = getData(page);
		if (!data.hasKey("linkpanel")) {
			data.setTag("linkpanel", new NBTTagCompound());
		}
		NBTTagCompound linkpanel = data.getCompoundTag("linkpanel");
		NBTTagList list = linkpanel.getTagList("properties", Constants.NBT.TAG_STRING);
		list.appendTag(new NBTTagString(linkproperty));
		linkpanel.setTag("properties", list);
	}

	@Nullable
	public static Collection<String> getLinkProperties(@Nonnull ItemStack page) {
		if (page.isEmpty() || page.getTagCompound() == null) {
			return null;
		}
		NBTTagCompound data = getData(page);
		if (!data.hasKey("linkpanel")) {
			return null;
		}
		NBTTagCompound linkpanel = data.getCompoundTag("linkpanel");
		return NBTUtils.readStringCollection(linkpanel.getTagList("properties", Constants.NBT.TAG_STRING), new ArrayList<String>());
	}

	//XXX: This is a weird way to do this now
	public static void applyLinkPanel(@Nonnull ItemStack linkpanel, @Nonnull ItemStack linkingitem) {
		Collection<String> properties = getLinkProperties(linkpanel);
		if (properties == null)
			return;
		for (String property : properties) {
			LinkOptions.setFlag(linkingitem.getTagCompound(), property, true);
		}
	}

	public static void setSymbol(@Nonnull ItemStack page, ResourceLocation symbol) {
		if (page.isEmpty() || page.getTagCompound() == null) {
			return;
		}
		NBTTagCompound data = getData(page);
		if (symbol == null) {
			data.removeTag("symbol");
		} else {
			data.setString("symbol", symbol.toString());
		}
	}

	@Nullable
	public static ResourceLocation getSymbol(@Nonnull ItemStack page) {
		if (page.isEmpty() || page.getTagCompound() == null) {
			return null;
		}
		NBTTagCompound data = getData(page);
		String symbol = data.getString("symbol");
		if (symbol.isEmpty()) {
			return null;
		} else {
			return new ResourceLocation(symbol);
		}
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

	@Nonnull
	public static NBTTagCompound createDefault() {
		return new NBTTagCompound();
	}

	@Nonnull
	public static ItemStack createPage() {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.setTagCompound(createDefault());
		return page;
	}

	@Nonnull
	public static ItemStack createLinkPage() {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.setTagCompound(createDefault());
		makeLinkPanel(page);
		return page;
	}

	@Nonnull
	public static ItemStack createLinkPage(String property) {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.setTagCompound(createDefault());
		addLinkProperty(page, property);
		return page;
	}

	@Nonnull
	public static ItemStack createSymbolPage(ResourceLocation symbol) {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.setTagCompound(createDefault());
		setSymbol(page, symbol);
		return page;
	}

	@Nonnull
	public static ItemStack createPage(NBTTagCompound pagedata) {
		ItemStack page = new ItemStack(ModItems.page, 1, 0);
		page.setTagCompound(pagedata);
		return page;
	}

}
