package com.xcompwiz.mystcraft.linking;

import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnd;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LinkListenerForgeServer {

	@SubscribeEvent
	public void onLinkEnd(LinkEventEnd event) {
		if (event.entity instanceof EntityPlayer) {
			FMLCommonHandler.instance().firePlayerChangedDimensionEvent((EntityPlayer) event.entity, event.origin.provider.getDimension(), event.destination.provider.getDimension());
		}
	}

}
