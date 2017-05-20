package com.xcompwiz.mystcraft.client;

import java.util.ArrayList;
import java.util.Collections;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.client.entityfx.ParticleProviderLink;
import com.xcompwiz.mystcraft.client.entityfx.ParticleUtils;
import com.xcompwiz.mystcraft.client.gui.overlay.GuiNotification;
import com.xcompwiz.mystcraft.client.linkeffects.LinkRendererDisarm;
import com.xcompwiz.mystcraft.client.model.ModelInkMixer;
import com.xcompwiz.mystcraft.client.render.*;
import com.xcompwiz.mystcraft.client.shaders.ShaderUtils;
import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.data.*;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.entity.EntityFallingBlock;
import com.xcompwiz.mystcraft.entity.EntityLightningBoltAdv;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.entity.EntityMeteor;
import com.xcompwiz.mystcraft.error.MystcraftStartupChecker;
import com.xcompwiz.mystcraft.inventory.CreativeTabMyst;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.tileentity.*;
import com.xcompwiz.mystcraft.world.profiling.InstabilityDataCalculator;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MystcraftClientProxy extends MystcraftCommonProxy {

	private MystcraftStartupChecker	startupchecker;

	@Override
	public boolean isClientSideAvailable() {
		return true;
	}

	@Override
	public Entity getEntityByID(World worldObj, int id) {
		if (worldObj instanceof WorldClient) return worldObj.getEntityByID(id);
		return super.getEntityByID(worldObj, id);
	}

	public GuiNotification getNotificationGui() {
		return this.startupchecker.getNotificationGui();
	}

	@Override
	public void preinit() {
        startupchecker = new MystcraftStartupChecker();
        MinecraftForge.EVENT_BUS.register(startupchecker);

        ModFluids.registerModels();
		registerEntityRenderers();
    }

	@Override
	public void init() {

        ModBlocks.registerModels();
        ModItems.registerModels();

		registerTileEntityRenderers();

		MinecraftForge.EVENT_BUS.register(new PageBuilder());

		InternalAPI.render.registerRenderEffect(new LinkRendererDisarm());

		ParticleUtils.registerParticle("link", new ParticleProviderLink());
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
	public void createCreativeTabs() {
		// Basic creative tab
		CreativeTabMyst creativeTab = new CreativeTabMyst("mystcraft.common", true);
		creativeTab.registerItemStack(new ItemStack(ModItems.agebook, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModItems.unlinked, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModItems.folder, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModItems.portfolio, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModItems.booster, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModItems.desk, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModItems.desk, 1, 1));
		creativeTab.registerItemStack(new ItemStack(ModBlocks.bookstand, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModBlocks.lectern, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModBlocks.crystal, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModBlocks.receptacle, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModBlocks.inkmixer, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModBlocks.bookbinder, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModBlocks.linkmodifier, 1, 0));
		creativeTab.registerItemStack(new ItemStack(ModItems.inkvial, 1, 0));
		//creativeTab.registerItemStack(new ItemStack(ModBlocks.black_ink, 1, 0));

		// Symbol creative tab
		CreativeTabMyst pageTab = new CreativeTabMyst("mystcraft.pages");
		pageTab.setHasSearchBar(true);
		pageTab.registerItemStack(Page.createLinkPage());
		ArrayList<String> linkproperties = new ArrayList<>();
		linkproperties.addAll(InkEffects.getProperties());
		Collections.sort(linkproperties);
		for (String property : linkproperties) {
			if(property.equals(LinkPropertyAPI.FLAG_RELATIVE)) continue;
			ModLinkEffects.isPropertyAllowed(property);
			pageTab.registerItemStack(Page.createLinkPage(property));
		}
		ModPageCollections.addSymbolPages(pageTab);
	}

	@Override
	public void startBaselineProfiling(MinecraftServer mcserver) {
		if (InstabilityDataCalculator.isPerSave()) super.startBaselineProfiling(mcserver);
	}

	@Override
	public void stopBaselineProfiling() {
		if (InstabilityDataCalculator.isPerSave()) super.stopBaselineProfiling();		
	}
}
