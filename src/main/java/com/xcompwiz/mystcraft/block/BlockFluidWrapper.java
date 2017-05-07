package com.xcompwiz.mystcraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidWrapper extends BlockFluidClassic {

	public BlockFluidWrapper(Fluid fluid, Material material) {
		super(fluid, material);
		this.setRenderPass(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	public IIcon getIcon(int par1, int par2) {
		return par1 != 0 && par1 != 1 ? this.getFluid().getFlowingIcon() : this.getFluid().getStillIcon();
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor() {
		return this.getFluid().getColor();
	}

	@Override
	public int getRenderColor(int par1) {
		return this.getFluid().getColor();
	}

	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called when first determining what to render.
	 */
	@Override
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		return this.getFluid().getColor();
	}

	@Override
	protected void flowIntoBlock(World world, int x, int y, int z, int meta) {
		if (meta < 0) { return; }
		if (!canFlowInto(world, x, y, z)) return;
		if (displaceIfPossible(world, x, y, z)) {
			world.setBlock(x, y, z, this, meta, 3);
		}
	}

	/* IFluidBlock */
	@Override
	public Fluid getFluid() {
		return FluidRegistry.getFluid(fluidName);
	}
}
