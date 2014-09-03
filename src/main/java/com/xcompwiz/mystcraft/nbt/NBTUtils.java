package com.xcompwiz.mystcraft.nbt;

import java.util.Collection;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public final class NBTUtils {
	public static Number readNumber(NBTBase tag) {
		if (tag == null) { return 0; }
		if (tag instanceof NBTTagByte) { return ((NBTTagByte) tag).func_150290_f(); }
		if (tag instanceof NBTTagShort) { return ((NBTTagShort) tag).func_150289_e(); }
		if (tag instanceof NBTTagInt) { return ((NBTTagInt) tag).func_150287_d(); }
		if (tag instanceof NBTTagLong) { return ((NBTTagLong) tag).func_150291_c(); }
		if (tag instanceof NBTTagFloat) { return ((NBTTagFloat) tag).func_150288_h(); }
		if (tag instanceof NBTTagDouble) { return ((NBTTagDouble) tag).func_150286_g(); }
		return 0;
	}

	public static void readInventoryArrayFromNBT(NBTTagList tagList, ItemStack[] inventory) {
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < inventory.length) {
				inventory[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	public static NBTTagList writeInventoryArrayToNBT(NBTTagList nbttaglist, ItemStack[] inventory) {
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				NBTTagCompound slot = new NBTTagCompound();
				slot.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(slot);
				nbttaglist.appendTag(slot);
			}
		}
		return nbttaglist;
	}

	public static <T extends Map<String, Byte>> T readByteMapFromNBT(NBTTagCompound tagcompound, T map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).byteValue());
		}
		return map;
	}

	public static NBTTagCompound writeByteMapToNBT(NBTTagCompound tagcompound, Map<String, Byte> map) {
		for (String key : map.keySet()) {
			tagcompound.setByte(key, map.get(key));
		}
		return tagcompound;
	}

	public static <T extends Map<String, Integer>> T readIntMapFromNBT(NBTTagCompound tagcompound, T map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).intValue());
		}
		return map;
	}

	public static NBTTagCompound writeIntMapToNBT(NBTTagCompound tagcompound, Map<String, Integer> map) {
		for (String key : map.keySet()) {
			tagcompound.setInteger(key, map.get(key));
		}
		return tagcompound;
	}

	public static <T extends Map<String, Float>> T readFloatMapFromNBT(NBTTagCompound tagcompound, T map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).floatValue());
		}
		return map;
	}

	public static NBTTagCompound writeFloatMapToNBT(NBTTagCompound tagcompound, Map<String, Float> map) {
		for (String key : map.keySet()) {
			tagcompound.setFloat(key, map.get(key));
		}
		return tagcompound;
	}

	public static NBTTagList writeStringListToNBT(NBTTagList nbttaglist, Collection<String> list) {
		nbttaglist = new NBTTagList();
		for (String str : list) {
			if (str == null) continue;
			nbttaglist.appendTag(new NBTTagString(str));
		}
		return nbttaglist;
	}

	public static <T extends Collection<String>> T readStringListFromNBT(NBTTagList nbttaglist, T list) {
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			list.add(nbttaglist.getStringTagAt(i));
		}
		return list;
	}
}
