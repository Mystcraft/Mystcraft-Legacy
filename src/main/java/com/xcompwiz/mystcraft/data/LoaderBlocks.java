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
import com.xcompwiz.mystcraft.block.BlockFluidMyst;
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

public class LoaderBlocks {

	public static void loadConfigs(MystConfig config) {}

	public static void init() {
		BlockInkMixer.instance = (new BlockInkMixer(Material.wood)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeWood).setBlockName("myst.inkmixer").setCreativeTab(CreativeTabs.tabDecorations);
		BlockBookBinder.instance = (new BlockBookBinder(Material.wood)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeWood).setBlockName("myst.bookbinder").setCreativeTab(CreativeTabs.tabDecorations);
		BlockBookReceptacle.instance = (new BlockBookReceptacle()).setHardness(1.0F).setStepSound(Block.soundTypeGlass).setBlockName("myst.receptacle").setCreativeTab(CreativeTabs.tabDecorations);
		BlockBookstand.instance = (new BlockBookstand(Material.wood)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeWood).setBlockName("myst.bookstand").setCreativeTab(CreativeTabs.tabDecorations);
		BlockLectern.instance = (new BlockLectern(Material.wood)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeWood).setBlockName("myst.lectern").setCreativeTab(CreativeTabs.tabDecorations);
		BlockDecay.instance = (new BlockDecay()).setStepSound(Block.soundTypeSand).setBlockName("myst.unstable");
		BlockLinkModifier.instance = (new BlockLinkModifier(Material.iron)).setHardness(2.0F).setResistance(2F).setStepSound(Block.soundTypeMetal).setBlockName("myst.linkmodifier").setCreativeTab(CreativeTabs.tabDecorations);
		BlockCrystal.instance = (new BlockCrystal()).setHardness(1.0F).setStepSound(Block.soundTypeGlass).setLightLevel(0.5F).setBlockName("myst.crystal").setCreativeTab(CreativeTabs.tabBlock);
		BlockLinkPortal.instance = (new BlockLinkPortal(15)).setHardness(-1F).setStepSound(Block.soundTypeGlass).setLightLevel(0.75F).setBlockName("myst.linkportal");
		BlockWritingDesk.instance = (new BlockWritingDesk()).setHardness(2.5F).setStepSound(Block.soundTypeWood).setBlockName("myst.writing_desk");
		BlockStarFissure.instance = (new BlockStarFissure(Material.portal)).setBlockUnbreakable().setBlockName("myst.starfissure");
		BlockFluidMyst.instance = (new BlockFluidMyst(LoaderFluids.black_ink, Material.water)).setBlockName("myst.fluid");

		GameRegistry.registerBlock(BlockInkMixer.instance, ItemBlock.class, MystObjects.block_inkmixer);
		GameRegistry.registerBlock(BlockBookBinder.instance, ItemBlock.class, MystObjects.block_bookbinder);
		GameRegistry.registerBlock(BlockBookReceptacle.instance, ItemBlock.class, MystObjects.block_crystal_receptacle);
		GameRegistry.registerBlock(BlockBookstand.instance, ItemBlock.class, MystObjects.block_bookstand);
		GameRegistry.registerBlock(BlockLectern.instance, ItemBlock.class, MystObjects.block_book_lectern);
		GameRegistry.registerBlock(BlockDecay.instance, ItemDecayBlock.class, MystObjects.block_decay);
		GameRegistry.registerBlock(BlockLinkModifier.instance, ItemBlock.class, MystObjects.block_link_modifer);
		GameRegistry.registerBlock(BlockCrystal.instance, ItemBlock.class, MystObjects.block_crystal);
		GameRegistry.registerBlock(BlockLinkPortal.instance, ItemBlock.class, MystObjects.block_portal);
		GameRegistry.registerBlock(BlockWritingDesk.instance, ItemBlock.class, MystObjects.block_writing_desk_block);
		GameRegistry.registerBlock(BlockStarFissure.instance, ItemBlock.class, MystObjects.block_star_fissure);
		GameRegistry.registerBlock(BlockFluidMyst.instance, ItemBlockFluid.class, "BlockFluidMyst", BlockFluidMyst.instance);

		// Set mining difficulties/tools
		BlockDecay.instance.setHarvestLevel("pickaxe", 0, DecayHandler.BLUE);
		BlockDecay.instance.setHarvestLevel("shovel", 0, DecayHandler.RED);
		BlockDecay.instance.setHarvestLevel("pickaxe", 0, DecayHandler.PURPLE);
		BlockDecay.instance.setHarvestLevel("pickaxe", 2, DecayHandler.WHITE);
		BlockDecay.instance.setHarvestLevel("shovel", 0, DecayHandler.BLACK);
		BlockCrystal.instance.setHarvestLevel("pickaxe", 0);
	}
}
