package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SymbolObelisks extends SymbolBase {

	public SymbolObelisks(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);
		controller.registerInterface(new Populator(block));
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class Populator implements IPopulate {
		private IBlockState state;

		public Populator(BlockDescriptor descriptor) {
			if (descriptor == null) {
				this.state = Blocks.OBSIDIAN.getDefaultState();
			} else {
				this.state = descriptor.blockstate;
			}
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			if (rand.nextInt(128) == 0) {
				int j6 = i + rand.nextInt(16);
				int k10 = j + rand.nextInt(16);
				generate(worldObj, rand, new BlockPos(j6, worldObj.getHeight(j6, k10), k10));
			}
			return false;
		}

		private void generate(World worldObj, Random random, BlockPos base) {
			if (base.getY() == 0) {
				return;
			}
			int height = 12;
			int width = 4;
			int maxDeep = 5;

			base = base.up();
			boolean foundBase = false;
			for (int y = base.getY(); y > base.getY() - maxDeep && !foundBase; --y) {
				foundBase = true;
				for (int z = 0; z < width; ++z) {
					for (int x = 0; x < width; ++x) {
						BlockPos posAt = new BlockPos(base.getX() + x, y, base.getZ() + z);
						worldObj.setBlockState(posAt, state, 2);
						Material material = worldObj.getBlockState(posAt.down()).getMaterial();
						if (worldObj.isAirBlock(posAt.down()) || worldObj.getBlockState(posAt.down()).getBlock().equals(Blocks.SNOW) || material == Material.WATER || material == Material.LAVA) {
							foundBase = false;
						}
					}
				}
			}

			// Second layer
			base = base.add(1, 0, 1);
			width = 2;
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < width; ++z) {
					for (int x = 0; x < width; ++x) {
						worldObj.setBlockState(base.add(x, y, z), state, 2);
					}
				}
			}
		}
	}
}
