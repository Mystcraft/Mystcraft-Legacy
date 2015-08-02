package com.xcompwiz.mystcraft.core;

import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class MystcraftTickHandler {
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.END) return;
		VillagerTradeSystem.onTick();
	}
}
