package com.xcompwiz.mystcraft.core;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.data.ModLinkEffects;
import com.xcompwiz.mystcraft.error.MystcraftStartupChecker;
import com.xcompwiz.mystcraft.inventory.CreativeTabMyst;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.profiling.InstabilityDataCalculator;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Collections;

public class MystcraftCommonProxy {

	public static CreativeTabMyst tabMystCommon = null;
	public static CreativeTabMyst tabMystPages = null;

	public Entity getEntityByID(World worldObj, int id) {
		if (worldObj instanceof WorldServer)
			return worldObj.getEntityByID(id);
		return null;
	}

	public MinecraftServer getMCServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance(); // Works when remote?
	}

	public void spawnParticle(String particle, double x, double y, double z, double motionX, double motionY, double motionZ) {}

	public void preinit() {}

	public void init() {}

	public void postInit() {}

	public void createCreativeTabs() {
		tabMystCommon = new CreativeTabMyst("mystcraft.common", true);

		tabMystPages = new CreativeTabMyst("mystcraft.pages");
		tabMystPages.setHasSearchBar(true);

		ArrayList<String> linkproperties = new ArrayList<>();
		linkproperties.addAll(InkEffects.getProperties());
		Collections.sort(linkproperties);
		for (String property : linkproperties) {
			if (property.equals(LinkPropertyAPI.FLAG_RELATIVE))
				continue;
			ModLinkEffects.isPropertyAllowed(property);
			//HellFire> have consistency remain with this creating config entries...
		}
	}

	public void initShaders() {}

	public boolean isClientSideAvailable() {
		return false;
	}

	private InstabilityDataCalculator instabilitycalculator;

	/**
	 * Server-side only logic to start up the instability calculator Instability profiling is done differently client-side. (See
	 * {@link MystcraftStartupChecker})
	 * @param mcserver The server object for the game
	 */
	public void startBaselineProfiling(MinecraftServer mcserver) {
		if (InstabilityDataCalculator.isDisabled())
			return;
		instabilitycalculator = new InstabilityDataCalculator(mcserver, mcserver.getWorld(0).getMapStorage());
		MinecraftForge.EVENT_BUS.register(instabilitycalculator);
	}

	/**
	 * Server-side only logic to stop the instability calculator Instability profiling is done differently client-side. (See {@link MystcraftStartupChecker})
	 */
	public void stopBaselineProfiling() {
		if (instabilitycalculator != null) {
			instabilitycalculator.shutdown();
			MinecraftForge.EVENT_BUS.unregister(instabilitycalculator);
			instabilitycalculator = null;
		}
	}
}
