package com.xcompwiz.mystcraft.linking;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

public class LinkOptions implements ILinkInfo {

	private NBTTagCompound	data;

	public LinkOptions(NBTTagCompound data) {
		if (data != null) this.data = (NBTTagCompound) data.copy();
	}

	@Override
	public NBTTagCompound getTagCompound() {
		return data;
	}

	@Override
	public ILinkInfo clone() {
		return new LinkOptions(this.getTagCompound());
	}

	@Override
	public String getDisplayName() {
		return getDisplayName(data);
	}

	@Override
	public int getDimensionUID() {
		return getDimensionUID(data);
	}

	@Override
	public ChunkCoordinates getSpawn() {
		return getSpawn(data);
	}

	@Override
	public float getSpawnYaw() {
		return getSpawnYaw(data);
	}

	@Override
	public boolean getFlag(String flag) {
		return getFlag(data, flag);
	}

	@Override
	public void setFlag(String flag, boolean value) {
		data = setFlag(data, flag, value);
	}

	@Override
	public String getProperty(String prop) {
		return getProperty(data, prop);
	}

	@Override
	public void setProperty(String prop, String value) {
		data = setProperty(data, prop, value);
	}

	@Override
	public void setDisplayName(String displayname) {
		data = setDisplayName(data, displayname);
	}

	@Override
	public void setDimensionUID(int uid) {
		data = setDimensionUID(data, uid);
	}

	@Override
	public void setSpawn(ChunkCoordinates spawn) {
		data = setSpawn(data, spawn);
	}

	@Override
	public void setSpawnYaw(float spawnyaw) {
		data = setSpawnYaw(data, spawnyaw);
	}

	public static NBTTagCompound setDisplayName(NBTTagCompound nbttagcompound, String name) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		nbttagcompound.setString("agename", name);
		return nbttagcompound;
	}

	public static String getDisplayName(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null) { return nbttagcompound.getString("agename"); }
		return "???";
	}

	public static NBTTagCompound setFlag(NBTTagCompound nbttagcompound, String flag, boolean val) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		getFlagCompound(nbttagcompound).setBoolean(flag, val);
		return nbttagcompound;
	}

	public static boolean getFlag(NBTTagCompound nbttagcompound, String flag) {
		if (nbttagcompound != null && getFlagCompound(nbttagcompound).hasKey(flag)) { return getFlagCompound(nbttagcompound).getBoolean(flag); }
		return false;
	}

	public NBTTagCompound setProperty(NBTTagCompound nbttagcompound, String flag, String value) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		getPropertyCompound(nbttagcompound).setString(flag, value);
		return nbttagcompound;
	}

	public static String getProperty(NBTTagCompound nbttagcompound, String flag) {
		if (nbttagcompound != null && getPropertyCompound(nbttagcompound).hasKey(flag)) { return getPropertyCompound(nbttagcompound).getString(flag); }
		return null;
	}

	public static NBTTagCompound setDimensionUID(NBTTagCompound nbttagcompound, int uid) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		nbttagcompound.setInteger("Dimension", uid);
		return nbttagcompound;
	}

	public static int getDimensionUID(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null && nbttagcompound.hasKey("AgeUID")) { return nbttagcompound.getInteger("AgeUID"); }
		if (nbttagcompound != null && nbttagcompound.hasKey("Dimension")) { return nbttagcompound.getInteger("Dimension"); }
		return 0;
	}

	public static NBTTagCompound setSpawn(NBTTagCompound nbttagcompound, ChunkCoordinates coords) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		if (coords != null) {
			nbttagcompound.setInteger("SpawnX", coords.posX);
			nbttagcompound.setInteger("SpawnY", coords.posY);
			nbttagcompound.setInteger("SpawnZ", coords.posZ);
		}
		return nbttagcompound;
	}

	public static ChunkCoordinates getSpawn(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null && nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) { return new ChunkCoordinates(nbttagcompound.getInteger("SpawnX"), nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ")); }
		return null;
	}

	public static NBTTagCompound setSpawnYaw(NBTTagCompound nbttagcompound, float yaw) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		nbttagcompound.setFloat("SpawnYaw", yaw);
		return nbttagcompound;
	}

	public static float getSpawnYaw(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null && nbttagcompound.hasKey("SpawnYaw")) { return nbttagcompound.getFloat("SpawnYaw"); }
		return 180;
	}

	private static NBTTagCompound getFlagCompound(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey("Flags")) {
			nbttagcompound.setTag("Flags", new NBTTagCompound());
		}
		return nbttagcompound.getCompoundTag("Flags");
	}

	private static NBTTagCompound getPropertyCompound(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey("Props")) {
			nbttagcompound.setTag("Props", new NBTTagCompound());
		}
		return nbttagcompound.getCompoundTag("Props");
	}
}
