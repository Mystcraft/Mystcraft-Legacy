package com.xcompwiz.mystcraft.nbt;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.google.common.io.ByteStreams;
import com.xcompwiz.mystcraft.item.ItemStackUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NBTUtils {

	@Nonnull
	public static Number readNumber(@Nullable NBTBase tag) {
		if (tag == null) {
			return 0;
		}
		if (tag instanceof NBTTagByte) {
			return ((NBTTagByte) tag).getByte();
		}
		if (tag instanceof NBTTagShort) {
			return ((NBTTagShort) tag).getShort();
		}
		if (tag instanceof NBTTagInt) {
			return ((NBTTagInt) tag).getInt();
		}
		if (tag instanceof NBTTagLong) {
			return ((NBTTagLong) tag).getLong();
		}
		if (tag instanceof NBTTagFloat) {
			return ((NBTTagFloat) tag).getFloat();
		}
		if (tag instanceof NBTTagDouble) {
			return ((NBTTagDouble) tag).getDouble();
		}
		return 0;
	}

	@Nonnull
	public static String readString(@Nullable NBTBase tag) {
		if (tag == null) {
			return "";
		}
		if (tag instanceof NBTTagString) {
			return ((NBTTagString) tag).getString();
		}
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
			if (!inventory[i].isEmpty()) {
				NBTTagCompound slot = new NBTTagCompound();
				slot.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(slot);
				nbttaglist.appendTag(slot);
			}
		}
		return nbttaglist;
	}

	public static <T extends Map<String, Byte>> T readByteMap(NBTTagCompound tagcompound, T map) {
		for (String tagname : tagcompound.getKeySet()) {
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
		for (String tagname : tagcompound.getKeySet()) {
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
		for (String tagname : tagcompound.getKeySet()) {
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
		for (String tagname : tagcompound.getKeySet()) {
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
			collection.add(new ItemStack(nbttaglist.getCompoundTagAt(i)));
		}
		return collection;
	}

	/**
	 * Reads a compressed NBTTagCompound from the InputStream
	 */
	@Nullable
	public static NBTTagCompound readNBTTagCompound(ByteBuf data) throws IOException {
		boolean present = data.readBoolean();
		if(!present) {
			return null;
		}
		return CompressedStreamTools.read(new ByteBufInputStream(data), NBTSizeTracker.INFINITE);
	}

	/**
	 * Writes a compressed NBTTagCompound to the OutputStream
	 * @throws IOException
	 */
	public static void writeNBTTagCompound(@Nullable NBTTagCompound nbttagcompound, ByteBuf data) throws IOException {
		if (nbttagcompound == null) {
			data.writeBoolean(false);
		} else {
			data.writeBoolean(true);
			CompressedStreamTools.write(nbttagcompound, new ByteBufOutputStream(data)); //Doesn't need to be closed since it doesn't buffer.
		}
	}

	@Nonnull
	public static NBTTagCompound forceGetCompound(@Nonnull NBTTagCompound nbt, String key) {
		NBTTagCompound tagcompound = nbt.getCompoundTag(key);
		nbt.setTag(key, tagcompound);
		return tagcompound;
	}
}
