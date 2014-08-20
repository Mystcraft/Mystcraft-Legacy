package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolObelisks extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);
		controller.registerInterface(new Populator(block));
	}

	@Override
	public String identifier() {
		return "Obelisks";
	}

	private class Populator implements IPopulate {
		private Block					block;
		private byte					metadata;

		public Populator(BlockDescriptor descriptor) {
			if (descriptor == null) {
				this.block = Blocks.obsidian;
				this.metadata = 0;
			} else {
				this.block = descriptor.block;
				this.metadata = descriptor.metadata;
			}
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			if (rand.nextInt(128) == 0) {
				int j6 = i + rand.nextInt(16);
				int k10 = j + rand.nextInt(16);
				generate(worldObj, rand, j6, worldObj.getHeightValue(j6, k10), k10);
			}
			return false;
		}

		private void generate(World worldObj, Random random, int baseX, int baseY, int baseZ) {
			if (baseY == 0) return;
			int height = 12;
			int width = 4;
			int maxDeep = 5;

			++baseY;
			boolean foundBase = false;
			for (int y = baseY; y > baseY - maxDeep && !foundBase; --y) {
				foundBase = true;
				for (int z = 0; z < width; ++z) {
					for (int x = 0; x < width; ++x) {
						worldObj.setBlock(baseX + x, y, baseZ + z, block, metadata, 2);
						Material material = worldObj.getBlock(baseX + x, y - 1, baseZ + z).getMaterial();
						if (worldObj.isAirBlock(baseX + x, y - 1, baseZ + z) || worldObj.getBlock(baseX + x, y - 1, baseZ + z) == Blocks.snow || material == Material.water || material == Material.lava) {
							foundBase = false;
						}
					}
				}
			}

			// Second layer
			++baseX;
			++baseZ;
			width = 2;
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < width; ++z) {
					for (int x = 0; x < width; ++x) {
						worldObj.setBlock(baseX + x, baseY + y, baseZ + z, block, metadata, 2);
					}
				}
			}
		}
	}
}
