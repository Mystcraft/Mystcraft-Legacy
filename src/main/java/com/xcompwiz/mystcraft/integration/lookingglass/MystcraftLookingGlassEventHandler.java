package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.event.ClientWorldInfoEvent;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketAgeData;
import com.xcompwiz.mystcraft.world.WorldInfoUtils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MystcraftLookingGlassEventHandler {

	@SubscribeEvent
	public void onClientViewDimension(ClientWorldInfoEvent event) {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(event.dim);
		if (world == null) { //Can very well be null.
			return;
		}
		if (WorldInfoUtils.isMystcraftAge(world)) {
			MystcraftPacketHandler.CHANNEL.sendTo(new MPacketAgeData(event.dim), event.player);
		}
	}
}
