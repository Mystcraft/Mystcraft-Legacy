package com.xcompwiz.mystcraft.data;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

import com.xcompwiz.mystcraft.api.MystObjects;
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

import cpw.mods.fml.common.registry.GameRegistry;

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
		inkmixer = (new BlockInkMixer(Material.wood)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeWood).setBlockName("myst.inkmixer").setCreativeTab(CreativeTabs.tabDecorations);
		bookbinder = (new BlockBookBinder(Material.wood)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeWood).setBlockName("myst.bookbinder").setCreativeTab(CreativeTabs.tabDecorations);
		receptacle = (new BlockBookReceptacle()).setHardness(1.0F).setStepSound(Block.soundTypeGlass).setBlockName("myst.receptacle").setCreativeTab(CreativeTabs.tabDecorations);
		bookstand = (new BlockBookstand(Material.wood)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeWood).setBlockName("myst.bookstand").setCreativeTab(CreativeTabs.tabDecorations);
		lectern = (new BlockLectern(Material.wood)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeWood).setBlockName("myst.lectern").setCreativeTab(CreativeTabs.tabDecorations);
		decay = (new BlockDecay()).setStepSound(Block.soundTypeSand).setBlockName("myst.unstable");
		linkmodifier = (new BlockLinkModifier(Material.iron)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeMetal).setBlockName("myst.linkmodifier").setCreativeTab(CreativeTabs.tabDecorations);
		crystal = (new BlockCrystal()).setHardness(1.0F).setStepSound(Block.soundTypeGlass).setLightLevel(0.5F).setBlockName("myst.crystal").setCreativeTab(CreativeTabs.tabBlock);
		portal = (new BlockLinkPortal(15)).setHardness(-1F).setStepSound(Block.soundTypeGlass).setLightLevel(0.75F).setBlockName("myst.linkportal");
		writingdesk = (new BlockWritingDesk()).setHardness(2.5F).setStepSound(Block.soundTypeWood).setBlockName("myst.writing_desk");
		starfissure = (new BlockStarFissure(Material.portal)).setBlockUnbreakable().setBlockName("myst.starfissure");
		black_ink = (new BlockFluidWrapper(ModFluids.black_ink, Material.water)).setBlockName("myst.fluid");

		GameRegistry.registerBlock(inkmixer, ItemBlock.class, MystObjects.block_inkmixer);
		GameRegistry.registerBlock(bookbinder, ItemBlock.class, MystObjects.block_bookbinder);
		GameRegistry.registerBlock(receptacle, ItemBlock.class, MystObjects.block_crystal_receptacle);
		GameRegistry.registerBlock(bookstand, ItemBlock.class, MystObjects.block_bookstand);
		GameRegistry.registerBlock(lectern, ItemBlock.class, MystObjects.block_book_lectern);
		GameRegistry.registerBlock(decay, ItemDecayBlock.class, MystObjects.block_decay);
		GameRegistry.registerBlock(linkmodifier, ItemBlock.class, MystObjects.block_link_modifer);
		GameRegistry.registerBlock(crystal, ItemBlock.class, MystObjects.block_crystal);
		GameRegistry.registerBlock(portal, ItemBlock.class, MystObjects.block_portal);
		GameRegistry.registerBlock(writingdesk, ItemBlock.class, MystObjects.block_writing_desk_block);
		GameRegistry.registerBlock(starfissure, ItemBlock.class, MystObjects.block_star_fissure);
		GameRegistry.registerBlock(black_ink, ItemBlockFluid.class, "BlockFluidMyst", black_ink);

		// Set mining difficulties/tools
		decay.setHarvestLevel("pickaxe", 0, DecayHandler.BLUE);
		decay.setHarvestLevel("shovel", 0, DecayHandler.RED);
		decay.setHarvestLevel("pickaxe", 0, DecayHandler.PURPLE);
		decay.setHarvestLevel("pickaxe", 2, DecayHandler.WHITE);
		decay.setHarvestLevel("shovel", 0, DecayHandler.BLACK);
		crystal.setHarvestLevel("pickaxe", 0);
	}
}
