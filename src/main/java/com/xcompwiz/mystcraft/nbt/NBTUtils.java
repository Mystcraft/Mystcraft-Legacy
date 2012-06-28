package com.xcompwiz.mystcraft.nbt;

import java.util.Collection;
import java.util.HashMap;

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

	public static HashMap<String, Byte> readByteMapFromNBT(NBTTagCompound tagcompound, HashMap<String, Byte> map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).byteValue());
		}
		return map;
	}

	public static NBTTagCompound writeByteMapToNBT(NBTTagCompound tagcompound, HashMap<String, Byte> map) {
		for (String key : map.keySet()) {
			tagcompound.setByte(key, map.get(key));
		}
		return tagcompound;
	}

	public static HashMap<String, Integer> readIntMapFromNBT(NBTTagCompound tagcompound, HashMap<String, Integer> map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).intValue());
		}
		return map;
	}

	public static NBTTagCompound writeIntMapToNBT(NBTTagCompound tagcompound, HashMap<String, Integer> map) {
		for (String key : map.keySet()) {
			tagcompound.setInteger(key, map.get(key));
		}
		return tagcompound;
	}

	public static HashMap<String, Float> readFloatMapFromNBT(NBTTagCompound tagcompound, HashMap<String, Float> map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).floatValue());
		}
		return map;
	}

	public static NBTTagCompound writeFloatMapToNBT(NBTTagCompound tagcompound, HashMap<String, Float> map) {
		for (String key : map.keySet()) {
			tagcompound.setFloat(key, map.get(key));
		}
		return tagcompound;
	}
}
