package com.xcompwiz.mystcraft.data;

import java.util.HashSet;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.MystObjects.Fluids;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.fluids.FluidColorable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ModFluids {

	public static Fluid	black_ink;

	public static void loadConfigs(MystConfig config) {}

	public static void init() {
		Mystcraft.validInks = new HashSet<>();

		black_ink = new FluidColorable(Fluids.black_ink,
				new ResourceLocation("mystcraft:blocks/fluid_flow"),
				new ResourceLocation("mystcraft:blocks/fluid"), 0x191919);
		FluidRegistry.registerFluid(black_ink);
		black_ink.setBlock(ModBlocks.black_ink); //Hellfire> that's why blocks need to be initialized first.
		Mystcraft.validInks.add(black_ink.getName());

		FluidRegistry.addBucketForFluid(black_ink);
		//UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, black_ink);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		FluidStateMapper mapper = new FluidStateMapper(black_ink);
		Block b = black_ink.getBlock();
		if(b != null) { //Hellfire> Should be true at this point but... ehhh....
			Item item = Item.getItemFromBlock(b);
			if (item != Items.AIR) {
				ModelLoader.registerItemVariants(item);
				ModelLoader.setCustomMeshDefinition(item, mapper);
			} else {
				ModelLoader.setCustomStateMapper(b, mapper);
			}
		}
	}

	//Hellfire> Clientside thing. don't call on serverside. annotating inner classes with client-side only seems to sometimes crash servers tho...
	public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

		private final ModelResourceLocation mrl;

		FluidStateMapper(Fluid fluid) {
			this.mrl = new ModelResourceLocation(new ResourceLocation(MystObjects.MystcraftModId, "fluids"), fluid.getName());
		}

		@Override
		@Nonnull
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return mrl;
		}

		@Override
		@Nonnull
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return mrl;
		}

	}

}
