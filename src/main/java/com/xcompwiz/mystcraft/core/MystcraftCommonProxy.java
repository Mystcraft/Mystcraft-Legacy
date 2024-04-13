package com.xcompwiz.mystcraft.core;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import com.xcompwiz.mystcraft.error.MystcraftStartupChecker;
import com.xcompwiz.mystcraft.world.profiling.InstabilityDataCalculator;

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

	private InstabilityDataCalculator	instabilitycalculator;

	/**
	 * Server-side only logic to start up the instability calculator
	 * Instability profiling is done differently client-side. (See {@link MystcraftStartupChecker})
	 * @param mcserver The server object for the game
	 */
	public void startBaselineProfiling(MinecraftServer mcserver) {
		if (InstabilityDataCalculator.isDisabled()) return;
		instabilitycalculator = new InstabilityDataCalculator(mcserver, mcserver.worldServerForDimension(0).mapStorage);
		FMLCommonHandler.instance().bus().register(instabilitycalculator);
		MinecraftForge.EVENT_BUS.register(instabilitycalculator);
	}

	/**
	 * Server-side only logic to stop the instability calculator
	 * Instability profiling is done differently client-side. (See {@link MystcraftStartupChecker})
	 */
	public void stopBaselineProfiling() {
		if (instabilitycalculator != null) {
			instabilitycalculator.shutdown();
			MinecraftForge.EVENT_BUS.unregister(instabilitycalculator);
			FMLCommonHandler.instance().bus().unregister(instabilitycalculator);
			instabilitycalculator = null;
		}
	}
}
