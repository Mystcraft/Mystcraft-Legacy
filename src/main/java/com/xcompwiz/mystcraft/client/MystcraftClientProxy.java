package com.xcompwiz.mystcraft.client;

import java.util.ArrayList;
import java.util.Collections;

import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.client.entityfx.ParticleProviderLink;
import com.xcompwiz.mystcraft.client.entityfx.ParticleUtils;
import com.xcompwiz.mystcraft.client.gui.overlay.GuiNotification;
import com.xcompwiz.mystcraft.client.linkeffects.LinkRendererDisarm;
import com.xcompwiz.mystcraft.client.model.ModelBookBinder;
import com.xcompwiz.mystcraft.client.model.ModelInkMixer;
import com.xcompwiz.mystcraft.client.model.ModelLinkModifier;
import com.xcompwiz.mystcraft.client.render.ItemRendererPage;
import com.xcompwiz.mystcraft.client.render.ItemRendererTileEntity;
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
import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookstand;
import com.xcompwiz.mystcraft.tileentity.TileEntityInkMixer;
import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;
import com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier;
import com.xcompwiz.mystcraft.world.profiling.InstabilityDataCalculator;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
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

		ModBlocks.registerModels();
		ModItems.registerModels();
		ModFluids.registerModels();
	}

	@Override
	public void init() {
		registerEntityRenderers();
		registerTileEntityRenderers();

		InternalAPI.render.registerRenderEffect(new LinkRendererDisarm());

		ParticleUtils.registerParticle("link", new ParticleProviderLink());

		MinecraftForgeClient.registerItemRenderer(ModItems.page, new ItemRendererPage());
		MinecraftForgeClient.registerItemRenderer(ModItems.inkvial, new ItemRendererMask());
	}

	private void registerEntityRenderers() {
		LoggerUtils.info("Adding Entity Renderers");
		Render render;
		render = new RenderFallingBlock();
		render.setRenderManager(RenderManager.instance);
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingBlock.class, render);

		render = new RenderLinkbook();
		render.setRenderManager(RenderManager.instance);
		RenderingRegistry.registerEntityRenderingHandler(EntityLinkbook.class, render);

		render = new RenderLightningBoltAdv();
		render.setRenderManager(RenderManager.instance);
		RenderingRegistry.registerEntityRenderingHandler(EntityLightningBoltAdv.class, render);

		render = new RenderMeteor();
		render.setRenderManager(RenderManager.instance);
		RenderingRegistry.registerEntityRenderingHandler(EntityMeteor.class, render);
	}

	private void registerTileEntityRenderers() {
		TileEntitySpecialRenderer render;
		render = new RenderWritingDesk();
		cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(com.xcompwiz.mystcraft.tileentity.TileEntityDesk.class, render);
		// MinecraftForgeClient.registerItemRenderer(com.xcompwiz.mystcraft.block.BlockWritingDesk.instance, new
		// ItemRendererLectern(render, new TileEntityDesk()));

		render = new RenderLectern();
		cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(com.xcompwiz.mystcraft.tileentity.TileEntityLectern.class, render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(com.xcompwiz.mystcraft.data.ModBlocks.lectern), new ItemRendererTileEntity(render, new TileEntityLectern()));

		render = new RenderBookstand();
		cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(com.xcompwiz.mystcraft.tileentity.TileEntityBookstand.class, render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(com.xcompwiz.mystcraft.data.ModBlocks.bookstand), new ItemRendererTileEntity(render, new TileEntityBookstand()));

		render = new RenderModel(new ModelBookBinder(), Entities.bookbinder);
		cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder.class, render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(com.xcompwiz.mystcraft.data.ModBlocks.bookbinder), new ItemRendererTileEntity(render, new TileEntityBookBinder()));

		render = new RenderModel(new ModelInkMixer(), Entities.inkmixer);
		cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(com.xcompwiz.mystcraft.tileentity.TileEntityInkMixer.class, render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(com.xcompwiz.mystcraft.data.ModBlocks.inkmixer), new ItemRendererTileEntity(render, new TileEntityInkMixer()));

		render = new RenderModel(new ModelLinkModifier(), Entities.linkmodifier);
		cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier.class, render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(com.xcompwiz.mystcraft.data.ModBlocks.linkmodifier), new ItemRendererTileEntity(render, new TileEntityLinkModifier()));

		render = new RenderStarFissure();
		cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(com.xcompwiz.mystcraft.tileentity.TileEntityStarFissure.class, render);

		render = new RenderBookReceptacle();
		cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle.class, render);
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
		creativeTab.registerItemStack(new ItemStack(ModBlocks.black_ink, 1, 0));

		// Symbol creative tab
		CreativeTabMyst pageTab = new CreativeTabMyst("mystcraft.pages");
		pageTab.setHasSearchBar(true);
		pageTab.registerItemStack(Page.createLinkPage());
		ArrayList<String> linkproperties = new ArrayList<String>();
		linkproperties.addAll(InkEffects.getProperties());
		Collections.sort(linkproperties);
		for (String property : linkproperties) {
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
