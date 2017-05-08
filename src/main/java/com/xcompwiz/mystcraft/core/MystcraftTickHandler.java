package com.xcompwiz.mystcraft.core;

import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class MystcraftTickHandler {
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.END) return;
		VillagerTradeSystem.onTick();
	}
}
