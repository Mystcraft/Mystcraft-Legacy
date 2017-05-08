package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.MystObjects.Blocks;
import com.xcompwiz.mystcraft.block.BlockBookBinder;
import com.xcompwiz.mystcraft.block.BlockBookReceptacle;
import com.xcompwiz.mystcraft.block.BlockBookstand;
import com.xcompwiz.mystcraft.block.BlockCrystal;
import com.xcompwiz.mystcraft.block.BlockDecay;
import com.xcompwiz.mystcraft.block.BlockFluidWrapper;
import com.xcompwiz.mystcraft.block.BlockInkMixer;
import com.xcompwiz.mystcraft.block.BlockLectern;
import com.xcompwiz.mystcraft.block.BlockLinkModifier;
import com.xcompwiz.mystcraft.block.BlockLinkPortal;
import com.xcompwiz.mystcraft.block.BlockStarFissure;
import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler;
import com.xcompwiz.mystcraft.item.ItemBlockFluid;
import com.xcompwiz.mystcraft.item.ItemDecayBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
	public static Block	inkmixer;
	public static Block	bookbinder;
	public static Block	receptacle;
	public static Block	bookstand;
	public static Block	lectern;
	public static Block	decay;
	public static Block	linkmodifier;
	public static Block	crystal;
	public static Block	portal;
	public static Block	writingdesk;
	public static Block	starfissure;
	public static Block	black_ink;

	public static void loadConfigs(MystConfig config) {}

	public static void init() {
		inkmixer = new BlockInkMixer();
		bookbinder = new BlockBookBinder();
		receptacle = new BlockBookReceptacle();
		bookstand = new BlockBookstand();
		lectern = new BlockLectern();
		decay = new BlockDecay();
		linkmodifier = new BlockLinkModifier();
		crystal = new BlockCrystal();
		portal = new BlockLinkPortal();
		writingdesk = new BlockWritingDesk();
		starfissure = new BlockStarFissure(Material.PORTAL);
		black_ink = new BlockFluidWrapper(ModFluids.black_ink, new MaterialLiquid(MapColor.WATER));//).setUnlocalizedName("myst.fluid");

		inkmixer.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.inkmixer));
		bookbinder.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.bookbinder));
		receptacle.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.crystal_receptacle));
		bookstand.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.bookstand));
		lectern.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.book_lectern));
		decay.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.decay));
		linkmodifier.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.link_modifer));
		crystal.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.crystal));
		portal.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.portal));
		writingdesk.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.writing_desk_block));
		starfissure.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.star_fissure));

		black_ink.setRegistryName(new ResourceLocation(MystObjects.MystcraftModId, Blocks.fluidblock_black_ink));

		GameRegistry.register(inkmixer);
		GameRegistry.register(new ItemBlock(inkmixer));
		GameRegistry.register(bookbinder);
		GameRegistry.register(new ItemBlock(bookbinder));
		GameRegistry.register(receptacle);
		GameRegistry.register(new ItemBlock(receptacle));
		GameRegistry.register(bookstand);
		GameRegistry.register(new ItemBlock(bookstand));
		GameRegistry.register(lectern);
		GameRegistry.register(new ItemBlock(lectern));
		GameRegistry.register(decay);
		GameRegistry.register(new ItemDecayBlock(decay));
		GameRegistry.register(linkmodifier);
		GameRegistry.register(new ItemBlock(linkmodifier));
		GameRegistry.register(crystal);
		GameRegistry.register(new ItemBlock(crystal));
		GameRegistry.register(portal);
		GameRegistry.register(new ItemBlock(portal));
		GameRegistry.register(writingdesk);
		GameRegistry.register(new ItemBlock(writingdesk));
		GameRegistry.register(starfissure);
		GameRegistry.register(new ItemBlock(starfissure));
		GameRegistry.register(black_ink);
		GameRegistry.register(new ItemBlockFluid(black_ink));

		// Set mining difficulties/tools
		decay.setHarvestLevel("pickaxe",   0, decay.getStateFromMeta(DecayHandler.BLUE));
		decay.setHarvestLevel("shovel",    0, decay.getStateFromMeta(DecayHandler.RED));
		decay.setHarvestLevel("pickaxe",   0, decay.getStateFromMeta(DecayHandler.PURPLE));
		decay.setHarvestLevel("pickaxe",   2, decay.getStateFromMeta(DecayHandler.WHITE));
		decay.setHarvestLevel("shovel",    0, decay.getStateFromMeta(DecayHandler.BLACK));
		crystal.setHarvestLevel("pickaxe", 0);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {

	}

}
