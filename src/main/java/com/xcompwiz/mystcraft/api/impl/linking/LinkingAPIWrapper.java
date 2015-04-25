package com.xcompwiz.mystcraft.api.impl.linking;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.api.linking.ILinkingAPI;

public class LinkingAPIWrapper extends APIWrapper implements ILinkingAPI {

	public LinkingAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public boolean isLinkAllowed(Entity entity, ILinkInfo linkinfo) {
		return InternalAPI.linking.isLinkAllowed(entity, linkinfo);
	}

	@Override
	public void linkEntity(Entity entity, ILinkInfo linkInfo) {
		InternalAPI.linking.linkEntity(entity, linkInfo);
	}

	@Override
	public ILinkInfo createLinkInfoFromPosition(World worldObj, Entity location) {
		return InternalAPI.linking.createLinkInfoFromPosition(worldObj, location);
	}

	@Override
	public ILinkInfo createLinkInfo(NBTTagCompound linkInfo) {
		return InternalAPI.linking.createLinkInfo(linkInfo);
	}
}
