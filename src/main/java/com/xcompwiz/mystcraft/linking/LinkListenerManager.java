package com.xcompwiz.mystcraft.linking;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAllow;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnd;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnterWorld;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventExitWorld;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventStart;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

import cpw.mods.fml.common.eventhandler.Event;

public class LinkListenerManager {
	public static boolean isLinkPermitted(World world, Entity entity, ILinkInfo info) {
		Event event = new LinkEventAllow(world, entity, info.clone());
		if (MinecraftForge.EVENT_BUS.post(event)) { return false; }
		return true;
	}

	public static void onLinkStart(World world, Entity entity, ILinkInfo info) {
		Event event = new LinkEventStart(world, entity, info.clone());
		MinecraftForge.EVENT_BUS.post(event);
	}

	public static void onExitWorld(Entity entity, ILinkInfo info) {
		Event event = new LinkEventExitWorld(entity, info.clone());
		MinecraftForge.EVENT_BUS.post(event);
	}

	public static void onEnterWorld(World origin, World destination, Entity entity, ILinkInfo info) {
		Event event = new LinkEventEnterWorld(origin, destination, entity, info.clone());
		MinecraftForge.EVENT_BUS.post(event);
	}

	public static void onLinkEnd(World origin, World destination, Entity entity, ILinkInfo info) {
		Event event = new LinkEventEnd(origin, destination, entity, info.clone());
		MinecraftForge.EVENT_BUS.post(event);
	}
}
