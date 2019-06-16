package com.xcompwiz.mystcraft.linking;

import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAllow;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnd;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventFailed;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventStart;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LinkListenerManager {

	public static boolean isLinkPermitted(World world, Entity entity, ILinkInfo info) {
		Event event = new LinkEventAllow(world, entity, info.clone());
		return !MinecraftForge.EVENT_BUS.post(event);
	}

	public static void onLinkStart(World world, Entity entity, ILinkInfo info) {
		Event event = new LinkEventStart(world, entity, info.clone());
		MinecraftForge.EVENT_BUS.post(event);
	}

	public static void onLinkFailed(World origin, Entity entity, ILinkInfo info) {
		Event event = new LinkEventFailed(origin, entity, info.clone());
		MinecraftForge.EVENT_BUS.post(event);
	}
	public static void onLinkEnd(World origin, World destination, Entity entity, ILinkInfo info) {
		Event event = new LinkEventEnd(origin, destination, entity, info.clone());
		MinecraftForge.EVENT_BUS.post(event);
	}

}
