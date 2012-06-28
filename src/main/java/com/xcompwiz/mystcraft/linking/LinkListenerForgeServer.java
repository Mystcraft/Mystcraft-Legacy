package com.xcompwiz.mystcraft.linking;

import net.minecraft.entity.player.EntityPlayer;

import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnd;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LinkListenerForgeServer {

	@SubscribeEvent
	public void onLinkEnd(LinkEventEnd event) {
		if (event.entity instanceof EntityPlayer) {
			FMLCommonHandler.instance().firePlayerChangedDimensionEvent((EntityPlayer) event.entity, event.origin.provider.dimensionId, event.destination.provider.dimensionId);
		}
	}

}
