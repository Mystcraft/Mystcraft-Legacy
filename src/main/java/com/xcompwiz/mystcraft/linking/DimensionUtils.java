package com.xcompwiz.mystcraft.linking;

import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.network.packet.MPacketDimensions;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.storage.FileUtils;

import cpw.mods.fml.common.FMLCommonHandler;

public class DimensionUtils {
	private static final String	PLAYER_DIM_UUID_TAG	= "myst.dimUUID";

	public static int getNewDimensionUID() {
		int dimUId = DimensionManager.getNextFreeDimId();
		return dimUId;
	}

	public static Integer createAge() {
		Integer dimid = recycleDimension();
		if (dimid != null) return dimid;
		dimid = getNewDimensionUID();
		createAge(dimid);
		return dimid;
	}

	private static Integer recycleDimension() {
		if (Mystcraft.deadDims == null || Mystcraft.deadDims.isEmpty()) return null;
		Integer dimid = null;
		for (int i = 0; i < Mystcraft.deadDims.size(); ++i) {
			if (DimensionManager.getWorld(Mystcraft.deadDims.get(i)) != null) continue;
			dimid = Mystcraft.deadDims.remove(i);
			break;
		}
		if (dimid == null) return null;
		if (!FileUtils.deleteAgeChunkData(dimid)) {
			Mystcraft.deadDims.add(dimid);
			return null;
		}
		AgeData data = AgeData.getAge(dimid, false);
		data.recreate(dimid);
		return dimid;
	}

	public static AgeData createAge(int dimId) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null) throw new RuntimeException("Cannot create dimension client-side. Misuse of Mystcraft API.");
		DimensionManager.registerDimension(dimId, Mystcraft.providerId);
		Mystcraft.registeredDims.add(dimId);
		server.getConfigurationManager().sendPacketToAllPlayers(MPacketDimensions.createPacket(dimId));
		AgeData data = AgeData.getAge(dimId, false);
		return data;
	}

	public static boolean markDimensionDead(int dimId) {
		if (Mystcraft.homeDimension == dimId) return false; //TODO: !Throw exception on this case
		if (!DimensionManager.isDimensionRegistered(dimId)) return false;
		if (!Mystcraft.registeredDims.contains(dimId)) return false;
		AgeData.getAge(dimId, false).markDead();
		Mystcraft.deadDims.add(dimId);
		return true;
	}

	public static String getDimensionName(WorldProvider worldProvider) {
		return worldProvider.getDimensionName();
	}

	public static boolean isDimensionVisited(Integer dimId) {
		if (dimId == null) return false;
		if (!DimensionManager.isDimensionRegistered(dimId)) return false;
		return true;
	}

	public static boolean isDimensionDead(int dimId) {
		if (!DimensionManager.isDimensionRegistered(dimId)) return true;
		if (!Mystcraft.registeredDims.contains(dimId)) return false;
		return AgeData.getAge(dimId, false).isDead();
	}

	public static int getLinkColor(ILinkInfo info) {
		if (info == null) return 0x000000;
		Random rand = new Random(info.getDisplayName().hashCode());
		int color = 0;
		color += (rand.nextInt(256));
		color += (rand.nextInt(256) << 8);
		color += (rand.nextInt(256) << 16);
		return color;
	}

	public static UUID getDimensionUUID(int dimId) {
		if (!DimensionManager.isDimensionRegistered(dimId)) return null;
		if (!Mystcraft.registeredDims.contains(dimId)) return new UUID(dimId, 0);
		return AgeData.getAge(dimId, false).getUUID();
	}

	public static boolean checkDimensionUUID(int dimid, UUID uuid) {
		if (uuid == null) return true;
		UUID targetuuid = getDimensionUUID(dimid);
		if (targetuuid == null) return true;
		if (targetuuid.equals(uuid)) return true;
		return false;
	}

	//TODO: Move this to a player helper class?
	public static UUID getPlayerDimensionUUID(EntityPlayer player) {
		NBTTagCompound nbt = player.getEntityData();
		if (nbt == null) return null;
		nbt = nbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		if (nbt == null) return null;
		if (nbt.hasKey(PLAYER_DIM_UUID_TAG)) return UUID.fromString(nbt.getString(PLAYER_DIM_UUID_TAG));
		return null;
	}

	//TODO: Move this to a player helper class?
	public static void setPlayerDimensionUUID(EntityPlayer player, UUID uuid) {
		NBTTagCompound nbt = player.getEntityData();
		if (!nbt.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			nbt.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
		}
		nbt = nbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		if (uuid != null) {
			nbt.setString(PLAYER_DIM_UUID_TAG, uuid.toString());
		} else {
			nbt.removeTag(PLAYER_DIM_UUID_TAG);
		}
	}

	public static void ejectPlayerFromDimension(EntityPlayer player) {
		if (isDimensionDead(Mystcraft.homeDimension)) throw new RuntimeException("The Mystcraft Home Dimension is flagged as dead. This is a serious problem...");
		ILinkInfo link = new LinkOptions(null);
		link.setDimensionUID(Mystcraft.homeDimension);
		link.setFlag(LinkPropertyAPI.FLAG_TPCOMMAND, true);
		LinkController.travelEntity(player.worldObj, player, link);
	}
}
