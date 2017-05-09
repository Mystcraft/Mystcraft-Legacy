package com.xcompwiz.mystcraft.linking;

import java.util.UUID;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LinkOptions implements ILinkInfo {

	private NBTTagCompound data;

	public LinkOptions(@Nullable NBTTagCompound data) {
		if (data != null) {
			this.data = data.copy();
		} else {
			//FIXME Hellfire> previously it was only setting the field 'data' to a not-null value if the parameter also wasn't null.
			//That seemed dangerous? Implementations on other ends suggest that data never got set on that end and no data was ever stored
			//but data was tried to be set. So... idk what you did here. This seems safer and more stable.
			this.data = new NBTTagCompound();
		}
	}

	@Override
	@Nonnull
	public NBTTagCompound getTagCompound() {
		return data;
	}

	@Override
	public ILinkInfo clone() {
		return new LinkOptions(this.getTagCompound());
	}

	@Override
	@Nonnull
	public String getDisplayName() {
		return getDisplayName(data);
	}

	@Override
	@Nullable
	public Integer getDimensionUID() {
		return getDimensionUID(data);
	}

	@Override
	@Nullable
	public UUID getTargetUUID() {
		return getUUID(data);
	}

	@Override
	@Nullable
	public BlockPos getSpawn() {
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
	public void setDisplayName(@Nonnull String displayname) {
		data = setDisplayName(data, displayname);
	}

	@Override
	public void setDimensionUID(int uid) {
		data = setDimensionUID(data, uid);
	}

	@Override
	public void setTargetUUID(@Nullable UUID uuid) {
		data = setUUID(data, uuid);
	}

	@Override
	public void setSpawn(@Nullable BlockPos spawn) {
		data = setSpawn(data, spawn);
	}

	@Override
	public void setSpawnYaw(float spawnyaw) {
		data = setSpawnYaw(data, spawnyaw);
	}

	public static NBTTagCompound setDisplayName(@Nullable NBTTagCompound nbttagcompound, @Nonnull String name) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		nbttagcompound.setString("DisplayName", name);
		return nbttagcompound;
	}

	@Nonnull
	public static String getDisplayName(@Nullable NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null && nbttagcompound.hasKey("DisplayName")) {
			return nbttagcompound.getString("DisplayName");
		}
		if (nbttagcompound != null && nbttagcompound.hasKey("agename")) {
			return nbttagcompound.getString("agename");
		}
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
		if (nbttagcompound != null && getFlagCompound(nbttagcompound).hasKey(flag)) {
			return getFlagCompound(nbttagcompound).getBoolean(flag);
		}
		return false;
	}

	public static NBTTagCompound setProperty(NBTTagCompound nbttagcompound, String flag, String value) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		NBTTagCompound props = getPropertyCompound(nbttagcompound);
		if (value == null) {
			props.removeTag(flag);
		} else {
			props.setString(flag, value);
		}
		return nbttagcompound;
	}

	@Nullable
	public static String getProperty(NBTTagCompound nbttagcompound, String flag) {
		if (nbttagcompound != null && getPropertyCompound(nbttagcompound).hasKey(flag)) {
			return getPropertyCompound(nbttagcompound).getString(flag);
		}
		return null;
	}

	public static NBTTagCompound setDimensionUID(NBTTagCompound nbttagcompound, int uid) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		nbttagcompound.setInteger("Dimension", uid);
		return nbttagcompound;
	}

	@Nullable
	public static Integer getDimensionUID(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null && nbttagcompound.hasKey("Dimension")) {
			return nbttagcompound.getInteger("Dimension");
		}
		if (nbttagcompound != null && nbttagcompound.hasKey("AgeUID")) {
			return nbttagcompound.getInteger("AgeUID");
		}
		return null;
	}

	public static NBTTagCompound setUUID(NBTTagCompound nbttagcompound, @Nullable UUID uuid) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		if (uuid != null) {
			nbttagcompound.setString("TargetUUID", uuid.toString());
		} else {
			nbttagcompound.removeTag("TargetUUID");
		}
		return nbttagcompound;
	}

	@Nullable
	public static UUID getUUID(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null && nbttagcompound.hasKey("TargetUUID")) {
			return UUID.fromString(nbttagcompound.getString("TargetUUID"));
		}
		return null;
	}

	public static NBTTagCompound setSpawn(NBTTagCompound nbttagcompound, @Nullable BlockPos coords) {
		if (nbttagcompound == null) {
			nbttagcompound = new NBTTagCompound();
		}
		if (coords != null) {
			nbttagcompound.setInteger("SpawnX", coords.getX());
			nbttagcompound.setInteger("SpawnY", coords.getY());
			nbttagcompound.setInteger("SpawnZ", coords.getZ());
		}
		return nbttagcompound;
	}

	@Nullable
	public static BlockPos getSpawn(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null && nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
			return new BlockPos(nbttagcompound.getInteger("SpawnX"), nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ"));
		}
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
		if (nbttagcompound != null && nbttagcompound.hasKey("SpawnYaw")) {
			return nbttagcompound.getFloat("SpawnYaw");
		}
		return 180;
	}

	@Nonnull
	private static NBTTagCompound getFlagCompound(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey("Flags")) {
			nbttagcompound.setTag("Flags", new NBTTagCompound());
		}
		return nbttagcompound.getCompoundTag("Flags");
	}

	@Nonnull
	private static NBTTagCompound getPropertyCompound(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey("Props")) {
			nbttagcompound.setTag("Props", new NBTTagCompound());
		}
		return nbttagcompound.getCompoundTag("Props");
	}
}
