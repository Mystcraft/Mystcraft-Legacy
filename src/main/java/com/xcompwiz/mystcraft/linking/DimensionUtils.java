package com.xcompwiz.mystcraft.linking;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.network.packet.MPacketDimensions;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import cpw.mods.fml.common.FMLCommonHandler;

public class DimensionUtils {
	public static int convertDimensionUIDToID(int dimUId) {
		return dimUId; // Maps dimUIDs to actual loaded dim IDs
	}

	public static String getDimensionName(WorldProvider worldProvider) {
		return worldProvider.getDimensionName();
	}

	public static int getNewDimensionUID() {
		int dimUId = DimensionManager.getNextFreeDimId();
		return dimUId;
	}

	public static AgeData createAge(int dimId) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null) throw new RuntimeException("Cannot create dimension client-side. Misuse of Mystcraft API.");
		Mystcraft.registeredDims.add(dimId);
		DimensionManager.registerDimension(dimId, Mystcraft.providerId);
		server.getConfigurationManager().sendPacketToAllPlayers(MPacketDimensions.createPacket(dimId));
		AgeData data = AgeData.getAge(dimId, false);
		return data;
	}
}
