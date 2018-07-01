package com.xcompwiz.mystcraft.core;

import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;

import com.xcompwiz.mystcraft.world.WorldProviderMyst;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MystcraftTickHandler {

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == Phase.END)
			return;
		VillagerTradeSystem.onTick();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.END)
			return;
		World world = Minecraft.getMinecraft().world;
		if (world != null && world.provider != null && world.provider instanceof WorldProviderMyst) {
			world.provider.updateWeather(); //Duh. USED to be called both sides.
		}
	}

}
