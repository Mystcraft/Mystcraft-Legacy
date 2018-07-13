package com.xcompwiz.mystcraft.block;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidInk extends BlockFluidClassic {

	public BlockFluidInk(Fluid fluid, Material material) {
		super(fluid, material);
		setLightOpacity(3);
	}

	/**
	 * Returns the percentage of the liquid block that is air, based on the given
	 * flow decay of the liquid
	 */
	public static float getLiquidHeightPercent(int meta) {
		if (meta >= 8) {
			meta = 0;
		}

		return (float) (meta + 1) / 9.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
		Vec3d viewport = net.minecraft.client.renderer.ActiveRenderInfo.projectViewFromEntity(entity, partialTicks);

		if (state.getMaterial().isLiquid()) {
			float height = 0.0F;
			if (state.getBlock() instanceof BlockFluidInk) {
				height = getLiquidHeightPercent(state.getValue(LEVEL)) - 0.11111111F;
			}
			float f1 = (float) (pos.getY() + 1) - height;
			if (viewport.y > (double) f1) {
				BlockPos upPos = pos.up();
				IBlockState upState = world.getBlockState(upPos);
				return upState.getBlock().getFogColor(world, upPos, upState, entity, originalColor, partialTicks);
			}
		}

		return super.getFogColor(world, pos, state, entity, originalColor, partialTicks);
	}
	
    /**
     * Called when the entity is inside this block, may be used to determined if the entity can breathing,
     * display material overlays, or if the entity can swim inside a block.
     *
     * @param world that is being tested.
     * @param blockpos position thats being tested.
     * @param iblockstate state at world/blockpos
     * @param entity that is being tested.
     * @param yToTest, primarily for testingHead, which sends the the eye level of the entity, other wise it sends a y that can be tested vs liquid height.
     * @param materialIn to test for.
     * @param testingHead when true, its testing the entities head for vision, breathing ect... otherwise its testing the body, for swimming and movement adjustment.
     * @return null for default behavior, true if the entity is within the material, false if it was not.
     */
   @Nullable
   public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead)
   {
	   if (materialIn == Material.WATER)
		   return true;
       return null;
   }
}
