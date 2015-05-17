package com.xcompwiz.mystcraft.core;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class MystcraftCommonProxy {
	public Entity getEntityByID(World worldObj, int id) {
		if (worldObj instanceof WorldServer) return ((WorldServer) worldObj).getEntityByID(id);
		return null;
	}

	public MinecraftServer getMCServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance(); // Works when remote?
	}

	public void spawnParticle(String particle, double x, double y, double z, double motionX, double motionY, double motionZ) {}

	public void preinit() {}

	public void init() {}

	public void postInit() {}

	public void createCreativeTabs() {}

	public void initShaders() {}

	public boolean isClientSideAvailable() {
		return false;
	}
}
