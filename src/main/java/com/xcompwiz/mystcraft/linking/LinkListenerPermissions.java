package com.xcompwiz.mystcraft.linking;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAllow;
import com.xcompwiz.mystcraft.command.CommandTPX;
import com.xcompwiz.mystcraft.nbt.NBTDataContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LinkListenerPermissions {
	private static final String						data_file		= "mystcraft-linking-permissions";

	private static HashMap<String, Set<Integer>>	permitEntry		= new HashMap<String, Set<Integer>>();
	private static HashMap<String, Set<Integer>>	restrictEntry	= new HashMap<String, Set<Integer>>();

	private static HashMap<String, Set<Integer>>	permitDepart	= new HashMap<String, Set<Integer>>();
	private static HashMap<String, Set<Integer>>	restrictDepart	= new HashMap<String, Set<Integer>>();

	@SubscribeEvent
	public void isLinkPermitted(LinkEventAllow event) {
		if (CommandTPX.isOpTP(event.info)) return;
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;
			Integer dimid = event.info.getDimensionUID();
			if (dimid == null) return;
			if (!(canPlayerLeaveDimension(player, event.origin.provider.getDimension()) && canPlayerEnterDimension(player, dimid))) {
				event.setCanceled(true);
			}
		}
	}

	private boolean canPlayerEnterDimension(EntityPlayer player, int dim) {
		// if (this.opEntry.contains(dim)) {
		// net.minecraft.server.MinecraftServer mcServer = Mystcraft.sidedProxy.getMCServer();
		// Set ops = mcServer.getPlayerList().getOps();
		// if (!ops.contains(player.username)) {
		// return false;
		// }
		// }
		Set<Integer> restricted = getRestrictedSet(player.getDisplayNameString());
		if (restricted != null && restricted.contains(dim)) return false;
		Set<Integer> permitted = getPermittedSet(player.getDisplayNameString());
		if (permitted != null && !permitted.contains(dim)) return false;

		return true;
	}

	private boolean canPlayerLeaveDimension(EntityPlayer player, int dim) {
		Set<Integer> restricted = restrictDepart.get(player.getDisplayName());
		if (restricted != null && restricted.contains(dim)) return false;
		Set<Integer> permitted = permitDepart.get(player.getDisplayName());
		if (permitted != null && !permitted.contains(dim)) return false;

		return true;
	}

	// public static void setOpOnlyEntry(int dimId, boolean flag) {
	// if (flag) {
	// opEntry.add(dimId);
	// } else {
	// opEntry.remove(dimId);
	// }
	// saveState();
	// }

	public static void restrictPlayerEntry(String playername, Integer dim) {
		if (dim == null) { // Restrict all
			restrictEntry.remove(playername);
			permitEntry.put(playername, new HashSet<Integer>());
		} else {
			Set<Integer> permitted = getPermittedSet(playername);
			if (permitted != null) permitted.remove(dim);
			Set<Integer> restricted = getRestrictedSet(playername);
			if (restricted == null) {
				restricted = new HashSet<Integer>();
				restrictEntry.put(playername, restricted);
			}
			restricted.add(dim);
		}
		saveState();
	}

	public static void permitPlayerEntry(String playername, Integer dim) {
		if (dim == null) { // Permit all
			restrictEntry.remove(playername);
			permitEntry.remove(playername);
		} else {
			Set<Integer> permitted = getPermittedSet(playername);
			if (permitted != null) permitted.add(dim);
			Set<Integer> restricted = getRestrictedSet(playername);
			if (restricted != null) restricted.remove(dim);
		}
		saveState();
	}

	public static void permitPlayerDepart(String playername, Integer dim) {
		if (dim == null) { // Permit all
			restrictDepart.remove(playername);
			permitDepart.remove(playername);
		} else {
			Set<Integer> permitted = permitDepart.get(playername);
			if (permitted != null) permitted.add(dim);
			Set<Integer> restricted = restrictDepart.get(playername);
			if (restricted != null) restricted.remove(dim);
		}
		saveState();
	}

	public static void restrictPlayerDepart(String playername, Integer dim) {
		if (dim == null) { // Restrict all
			restrictDepart.remove(playername);
			permitDepart.put(playername, new HashSet<Integer>());
		} else {
			Set<Integer> permitted = permitDepart.get(playername);
			if (permitted != null) permitted.remove(dim);
			Set<Integer> restricted = restrictDepart.get(playername);
			if (restricted == null) {
				restricted = new HashSet<Integer>();
				restrictDepart.put(playername, restricted);
			}
			restricted.add(dim);
		}
		saveState();
	}

	public static void loadState() {
		MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (mcServer == null) return;
		NBTDataContainer data = (NBTDataContainer) Mystcraft.getStorage(true).getOrLoadData(NBTDataContainer.class, data_file);
		if (data == null) {
			data = new NBTDataContainer("dummy");
		}
		permitDepart = readHashMapFromNBT(data.data.getCompoundTag("PermitData"));
		permitEntry = readHashMapFromNBT(data.data.getCompoundTag("PermitEntry"));
		restrictDepart = readHashMapFromNBT(data.data.getCompoundTag("RestrictDepart"));
		restrictEntry = readHashMapFromNBT(data.data.getCompoundTag("RestrictEntry"));
	}

	private static void saveState() {
		MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (mcServer == null) return;
		NBTDataContainer data = (NBTDataContainer) Mystcraft.getStorage(true).getOrLoadData(NBTDataContainer.class, data_file);
		if (data == null) {
			data = new NBTDataContainer(data_file);
			Mystcraft.getStorage(true).setData(data_file, data);
		}
		data.data.setTag("PermitDepart", writeHashMapToNBT(permitDepart));
		data.data.setTag("PermitEntry", writeHashMapToNBT(permitEntry));
		data.data.setTag("RestrictDepart", writeHashMapToNBT(restrictDepart));
		data.data.setTag("RestrictEntry", writeHashMapToNBT(restrictEntry));
		data.markDirty();
	}

	private static NBTTagCompound writeHashMapToNBT(HashMap<String, Set<Integer>> map) {
		NBTTagCompound compound = new NBTTagCompound();
		for (Entry<String, Set<Integer>> entry : map.entrySet()) {
			compound.setTag(entry.getKey(), writeSetToNBT(entry.getValue()));
		}
		return compound;
	}

	private static NBTTagList writeSetToNBT(Set<Integer> set) {
		NBTTagList list = new NBTTagList();
		for (Integer dim : set) {
			list.appendTag(new NBTTagInt(dim));
		}
		return list;
	}

	//XXX: Refactor to NBTUtils
	private static HashMap<String, Set<Integer>> readHashMapFromNBT(NBTTagCompound data) {
		HashMap<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
		for (String tagname : data.getKeySet()) {
			NBTTagList list = data.getTagList(tagname, Constants.NBT.TAG_INT);
			map.put(tagname, readSetFromNBT(list));
		}
		return map;
	}

	private static Set<Integer> readSetFromNBT(NBTTagList data) {
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < data.tagCount(); ++i) {
			NBTTagInt tag = (NBTTagInt) data.removeTag(0);
			set.add(tag.getInt());
		}
		return set;
	}

	private static Set<Integer> getRestrictedSet(String username) {
		return restrictEntry.get(username);
	}

	private static Set<Integer> getPermittedSet(String username) {
		return permitEntry.get(username);
	}
}
