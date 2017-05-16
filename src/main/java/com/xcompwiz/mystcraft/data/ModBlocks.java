package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.MystObjects.Blocks;
import com.xcompwiz.mystcraft.block.BlockBookBinder;
import com.xcompwiz.mystcraft.block.BlockBookReceptacle;
import com.xcompwiz.mystcraft.block.BlockBookstand;
import com.xcompwiz.mystcraft.block.BlockCrystal;
import com.xcompwiz.mystcraft.block.BlockDecay;
import com.xcompwiz.mystcraft.block.BlockInkMixer;
import com.xcompwiz.mystcraft.block.BlockLectern;
import com.xcompwiz.mystcraft.block.BlockLinkModifier;
import com.xcompwiz.mystcraft.block.BlockLinkPortal;
import com.xcompwiz.mystcraft.block.BlockStarFissure;
import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler;
import com.xcompwiz.mystcraft.item.ItemDecayBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
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
		starfissure = new BlockStarFissure();
		black_ink = new BlockFluidClassic(ModFluids.black_ink, new MaterialLiquid(MapColor.WATER));//).setUnlocalizedName("myst.fluid");

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
		registerItemBlock(inkmixer);
		GameRegistry.register(bookbinder);
        registerItemBlock(bookbinder);
		GameRegistry.register(receptacle);
        registerItemBlock(receptacle);
		GameRegistry.register(bookstand);
        registerItemBlock(bookstand);
		GameRegistry.register(lectern);
        registerItemBlock(lectern);
		GameRegistry.register(decay);
        ItemBlock ib = new ItemDecayBlock(decay);
        ib.setRegistryName(decay.getRegistryName());
        GameRegistry.register(ib);
		GameRegistry.register(linkmodifier);
        registerItemBlock(linkmodifier);
		GameRegistry.register(crystal);
        registerItemBlock(crystal);
		GameRegistry.register(portal);
        registerItemBlock(portal);
		GameRegistry.register(writingdesk);
        //registerItemBlock(writingdesk);
		GameRegistry.register(starfissure);
        registerItemBlock(starfissure);
		GameRegistry.register(black_ink);
		//GameRegistry.register(new ItemBlockFluid(black_ink)); Hellfire> it's not necessary to add a fluid itemblock.. I'd suggest we don't atm.

		// Set mining difficulties/tools
		decay.setHarvestLevel("pickaxe",   0, decay.getStateFromMeta(DecayHandler.DecayType.BLUE.getIndex()));
		decay.setHarvestLevel("shovel",    0, decay.getStateFromMeta(DecayHandler.DecayType.RED.getIndex()));
		decay.setHarvestLevel("pickaxe",   0, decay.getStateFromMeta(DecayHandler.DecayType.PURPLE.getIndex()));
		decay.setHarvestLevel("pickaxe",   2, decay.getStateFromMeta(DecayHandler.DecayType.WHITE.getIndex()));
		decay.setHarvestLevel("shovel",    0, decay.getStateFromMeta(DecayHandler.DecayType.BLACK.getIndex()));
		crystal.setHarvestLevel("pickaxe", 0);
	}

	private static void registerItemBlock(Block b) {
        ItemBlock ib = new ItemBlock(b);
        ib.setRegistryName(b.getRegistryName());
        GameRegistry.register(ib);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		//BlockModelShapes bms = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();

		ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		imm.register(Item.getItemFromBlock(inkmixer), 0, mrlItemBlockModel("blockinkmixer"));
		imm.register(Item.getItemFromBlock(bookbinder), 0, mrlItemBlockModel("blockbookbinder"));
		imm.register(Item.getItemFromBlock(receptacle), 0, mrlItemBlockModel("blockbookreceptacle"));
		imm.register(Item.getItemFromBlock(bookstand), 0, mrlItemBlockModel("blockbookstand"));
		imm.register(Item.getItemFromBlock(lectern), 0, mrlItemBlockModel("blocklectern"));
		imm.register(Item.getItemFromBlock(linkmodifier), 0, mrlItemBlockModel("blocklinkmodifier"));
		imm.register(Item.getItemFromBlock(crystal), 0, mrlItemBlockModel("blockcrystal"));
		imm.register(Item.getItemFromBlock(portal), 0, mrlItemBlockModel("portal"));

		//ModelBakery.registerItemVariants(Item.getItemFromBlock(writingdesk),
        //        new ResourceLocation(MystObjects.MystcraftModId, "writingdesk_base"),
        //        new ResourceLocation(MystObjects.MystcraftModId, "writingdesk_top"));
		//imm.register(Item.getItemFromBlock(writingdesk), (stack) -> {
		//    if(stack.getItemDamage() == 0) {
		//        return mrlItemBlockModel("writingdesk_base");
        //    } else {
		//        return mrlItemBlockModel("writingdesk_top");
        //    }
        //});

        ModelBakery.registerItemVariants(Item.getItemFromBlock(decay),
                new ResourceLocation(MystObjects.MystcraftModId, "decay_black"),
                new ResourceLocation(MystObjects.MystcraftModId, "decay_red"),
                new ResourceLocation(MystObjects.MystcraftModId, "decay_green"),
                new ResourceLocation(MystObjects.MystcraftModId, "decay_blue"),
                new ResourceLocation(MystObjects.MystcraftModId, "decay_purple"),
                new ResourceLocation(MystObjects.MystcraftModId, "decay_yellow"),
                new ResourceLocation(MystObjects.MystcraftModId, "decay_white"));
		imm.register(Item.getItemFromBlock(decay), (stack) -> {
            DecayHandler.DecayType dt = DecayHandler.DecayType.values()[MathHelper.clamp(stack.getItemDamage(), 0, DecayHandler.DecayType.values().length - 1)];
            return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "decay_" + dt.getName()), "inventory");
        });

		BlockColors colors = Minecraft.getMinecraft().getBlockColors();
		colors.registerBlockColorHandler((state, world, pos, tint) -> {
			if(world instanceof World) {
				return ModFluids.black_ink.getColor((World) world, pos);
			} else {
				return ModFluids.black_ink.getColor();
			}
		}, black_ink);
		colors.registerBlockColorHandler(((state, worldIn, pos, tint) -> BlockLinkPortal.colorMultiplier(worldIn, pos)), portal);
	}

	@SideOnly(Side.CLIENT)
	private static ModelResourceLocation mrlItemBlockModel(String name) {
		return new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, name), "inventory");
	}

}
