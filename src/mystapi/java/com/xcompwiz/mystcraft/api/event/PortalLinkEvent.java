package com.xcompwiz.mystcraft.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Indicates that a link through a (Mystcraft) portal is about to occur, and allows for the link information to be modified. Listen for this event via Forge.
 */
public class PortalLinkEvent extends Event {
	/** The world object where the link originates from */
	public final World		worldObj;
	/** The Entity being linked */
	public final Entity		entity;
	/** The link info describing the link about to take place */
	public final ILinkInfo	info;

	public PortalLinkEvent(World worldObj, Entity entity, ILinkInfo info) {
		this.worldObj = worldObj;
		this.entity = entity;
		this.info = info;
	}

}
