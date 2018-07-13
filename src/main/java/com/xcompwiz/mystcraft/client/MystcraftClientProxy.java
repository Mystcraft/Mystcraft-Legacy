package com.xcompwiz.mystcraft.client;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.client.entityfx.ParticleProviderLink;
import com.xcompwiz.mystcraft.client.entityfx.ParticleUtils;
import com.xcompwiz.mystcraft.client.gui.overlay.GuiNotification;
import com.xcompwiz.mystcraft.client.linkeffects.LinkRendererDisarm;
import com.xcompwiz.mystcraft.client.model.ModelInkMixer;
import com.xcompwiz.mystcraft.client.render.PageBuilder;
import com.xcompwiz.mystcraft.client.render.RenderBookReceptacle;
import com.xcompwiz.mystcraft.client.render.RenderBookstand;
import com.xcompwiz.mystcraft.client.render.RenderFallingBlock;
import com.xcompwiz.mystcraft.client.render.RenderLectern;
import com.xcompwiz.mystcraft.client.render.RenderLightningBoltAdv;
import com.xcompwiz.mystcraft.client.render.RenderLinkbook;
import com.xcompwiz.mystcraft.client.render.RenderMeteor;
import com.xcompwiz.mystcraft.client.render.RenderModel;
import com.xcompwiz.mystcraft.client.render.RenderStarFissure;
import com.xcompwiz.mystcraft.client.render.RenderWritingDesk;
import com.xcompwiz.mystcraft.client.shaders.ShaderUtils;
import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.data.ModFluids;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.entity.EntityFallingBlock;
import com.xcompwiz.mystcraft.entity.EntityLightningBoltAdv;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.entity.EntityMeteor;
import com.xcompwiz.mystcraft.error.MystcraftStartupChecker;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookstand;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;
import com.xcompwiz.mystcraft.tileentity.TileEntityInkMixer;
import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;
import com.xcompwiz.mystcraft.tileentity.TileEntityStarFissure;
import com.xcompwiz.mystcraft.world.profiling.InstabilityDataCalculator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MystcraftClientProxy extends MystcraftCommonProxy {

	private MystcraftStartupChecker startupchecker;

	@Override
	public boolean isClientSideAvailable() {
		return true;
	}

	@Override
	public Entity getEntityByID(World worldObj, int id) {
		if (worldObj instanceof WorldClient)
			return worldObj.getEntityByID(id);
		return super.getEntityByID(worldObj, id);
	}

	public GuiNotification getNotificationGui() {
		return this.startupchecker.getNotificationGui();
	}

	@Override
	public void preinit() {
		OBJLoader.INSTANCE.addDomain(MystObjects.MystcraftModId);

		startupchecker = new MystcraftStartupChecker();
		MinecraftForge.EVENT_BUS.register(startupchecker);
		MinecraftForge.EVENT_BUS.register(new PageBuilder());
		MinecraftForge.EVENT_BUS.register(this); //Placed in here to keep rendering registration in 1 place

		ModFluids.registerModels();
		registerEntityRenderers();
	}

	@Override
	public void init() {
		ModBlocks.registerModelColors();
		ModItems.registerModelColors();

		registerTileEntityRenderers();

		InternalAPI.render.registerRenderEffect(new LinkRendererDisarm());

		ParticleUtils.registerParticle("link", new ParticleProviderLink());
	}

	@SubscribeEvent
	public void initModels(ModelRegistryEvent event) {
		ModBlocks.registerModels();
		ModItems.registerModels();
	}

	private void registerEntityRenderers() {
		LoggerUtils.info("Adding Entity Renderers");
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingBlock.class, new RenderFallingBlock.Factory());
		RenderingRegistry.registerEntityRenderingHandler(EntityLightningBoltAdv.class, new RenderLightningBoltAdv.Factory());
		RenderingRegistry.registerEntityRenderingHandler(EntityLinkbook.class, new RenderLinkbook.Factory());
		RenderingRegistry.registerEntityRenderingHandler(EntityMeteor.class, new RenderMeteor.Factory());
	}

	private void registerTileEntityRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDesk.class, new RenderWritingDesk());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStarFissure.class, new RenderStarFissure());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBookReceptacle.class, new RenderBookReceptacle());
		//Just for the pages on it since i can't do that much detailing on the blockmodel
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInkMixer.class, new RenderModel<>(new ModelInkMixer(), Entities.inkmixer));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBookstand.class, new RenderBookstand());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLectern.class, new RenderLectern());
	}

	@Override
	public void initShaders() {
		ShaderUtils.registerShaders();
	}

	@Override
	public void spawnParticle(String particle, double x, double y, double z, double motionX, double motionY, double motionZ) {
		ParticleUtils.spawnParticle(particle, x, y, z, motionX, motionY, motionZ);
	}

	@Override
	public void startBaselineProfiling(MinecraftServer mcserver) {
		if (InstabilityDataCalculator.isPerSave())
			super.startBaselineProfiling(mcserver);
	}

	@Override
	public void stopBaselineProfiling() {
		if (InstabilityDataCalculator.isPerSave())
			super.stopBaselineProfiling();
	}
	
	@Override
	public void addScheduledTask(Runnable runnable) {
		Minecraft.getMinecraft().addScheduledTask(runnable);
	}
}
