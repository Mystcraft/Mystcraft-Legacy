package com.xcompwiz.mystcraft.nbt;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import com.xcompwiz.mystcraft.item.ItemStackUtils;

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

	public static String readString(NBTBase tag) {
		if (tag == null) { return ""; }
		if (tag instanceof NBTTagString) { return ((NBTTagString) tag).func_150285_a_(); }
		return tag.toString();
	}

	public static void readInventoryArray(NBTTagList tagList, ItemStack[] inventory) {
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < inventory.length) {
				inventory[byte0] = ItemStackUtils.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	public static NBTTagList writeInventoryArray(NBTTagList nbttaglist, ItemStack[] inventory) {
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

	public static <T extends Map<String, Byte>> T readByteMap(NBTTagCompound tagcompound, T map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).byteValue());
		}
		return map;
	}

	public static NBTTagCompound writeByteMap(NBTTagCompound tagcompound, Map<String, Byte> map) {
		for (String key : map.keySet()) {
			tagcompound.setByte(key, map.get(key));
		}
		return tagcompound;
	}

	public static <T extends Map<String, Integer>> T readIntMap(NBTTagCompound tagcompound, T map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).intValue());
		}
		return map;
	}

	public static NBTTagCompound writeIntMap(NBTTagCompound tagcompound, Map<String, Integer> map) {
		for (String key : map.keySet()) {
			tagcompound.setInteger(key, map.get(key));
		}
		return tagcompound;
	}

	public static <T extends Map<String, Float>> T readFloatMap(NBTTagCompound tagcompound, T map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, readNumber(tagcompound.getTag(tagname)).floatValue());
		}
		return map;
	}

	public static NBTTagCompound writeFloatMap(NBTTagCompound tagcompound, Map<String, Float> map) {
		for (String key : map.keySet()) {
			tagcompound.setFloat(key, map.get(key));
		}
		return tagcompound;
	}

	public static <T extends Map<String, String>> T readStringMap(NBTTagCompound tagcompound, T map) {
		Collection<String> tagnames = tagcompound.func_150296_c();

		for (String tagname : tagnames) {
			map.put(tagname, tagcompound.getString(tagname));
		}
		return map;
	}

	public static NBTTagCompound writeStringMap(NBTTagCompound tagcompound, Map<String, String> map) {
		for (String key : map.keySet()) {
			tagcompound.setString(key, map.get(key));
		}
		return tagcompound;
	}

	public static NBTTagList writeStringCollection(NBTTagList nbttaglist, Collection<String> collection) {
		for (String str : collection) {
			if (str == null) continue;
			nbttaglist.appendTag(new NBTTagString(str));
		}
		return nbttaglist;
	}

	public static <T extends Collection<String>> T readStringCollection(NBTTagList nbttaglist, T collection) {
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			collection.add(nbttaglist.getStringTagAt(i));
		}
		return collection;
	}

	public static NBTTagList writeTagCompoundCollection(NBTTagList nbttaglist, Collection<NBTTagCompound> collection) {
		for (NBTTagCompound tag : collection) {
			if (tag == null) continue;
			nbttaglist.appendTag(tag);
		}
		return nbttaglist;
	}

	public static <T extends Collection<NBTTagCompound>> T readTagCompoundCollection(NBTTagList nbttaglist, T collection) {
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			collection.add(nbttaglist.getCompoundTagAt(i));
		}
		return collection;
	}

	public static NBTTagList writeItemStackCollection(NBTTagList nbttaglist, Collection<ItemStack> collection) {
		for (ItemStack itemstack : collection) {
			if (itemstack == null) continue;
			nbttaglist.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
		}
		return nbttaglist;
	}

	public static <T extends Collection<ItemStack>> T readItemStackCollection(NBTTagList nbttaglist, T collection) {
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			collection.add(ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i)));
		}
		return collection;
	}

	/**
	 * Reads a compressed NBTTagCompound from the InputStream
	 */
	public static NBTTagCompound readNBTTagCompound(ByteBuf data) throws IOException {
		short length = data.readShort();

		if (length < 0) { return null; }
		byte[] abyte = new byte[length];
		data.readBytes(abyte);
		return CompressedStreamTools.func_152457_a(abyte, new NBTSizeTracker(2097152L));
	}

	/**
	 * Writes a compressed NBTTagCompound to the OutputStream
	 * @throws IOException
	 */
	public static int writeNBTTagCompound(NBTTagCompound nbttagcompound, ByteBuf data) throws IOException {
		if (nbttagcompound == null) {
			data.writeShort(-1);
		} else {
			byte[] abyte = CompressedStreamTools.compress(nbttagcompound);
			data.writeShort((short) abyte.length);
			data.writeBytes(abyte);
			return abyte.length;
		}
		return 0;
	}

	public static NBTTagCompound forceGetCompound(NBTTagCompound nbt, String key) {
		NBTTagCompound tagcompound = nbt.getCompoundTag(key);
		nbt.setTag(key, tagcompound);
		return tagcompound;
	}
}
