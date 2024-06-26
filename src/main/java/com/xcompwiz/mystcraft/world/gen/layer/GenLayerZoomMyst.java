package com.xcompwiz.mystcraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerZoomMyst extends GenLayer {
	public GenLayerZoomMyst(long par1, GenLayer par3GenLayer) {
		super(par1);
		super.parent = par3GenLayer;
	}

	/**
	 * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall amounts, or biomeList[] indices based on the
	 * particular GenLayer subclass. int areaX, int areaY, int areaWidth, int areaHeight
	 */
	@Override
	public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
		int biomeTileOffsetX = areaX >> 1;
		int biomeTileOffsetY = areaY >> 1;
		int biomeTileWidth  = (areaWidth  >> 1) + 3;
		int biomeTileHeight = (areaHeight >> 1) + 3;
		int[] var9 = this.parent.getInts(biomeTileOffsetX, biomeTileOffsetY, biomeTileWidth, biomeTileHeight);
		int[] var10 = IntCache.getIntCache(biomeTileWidth * 2 * biomeTileHeight * 2);
		int var11 = biomeTileWidth << 1;
		int var13;

		for (int stepX = 0; stepX < biomeTileHeight - 1; ++stepX) {
			var13 = stepX << 1;
			int var14 = var13 * var11;
			int var15 = var9[0 + (stepX + 0) * biomeTileWidth];
			int var16 = var9[0 + (stepX + 1) * biomeTileWidth];

			for (int var17 = 0; var17 < biomeTileWidth - 1; ++var17) {
				this.initChunkSeed((var17 + biomeTileOffsetX << 1), (stepX + biomeTileOffsetY << 1));
				int var18 = var9[var17 + 1 + (stepX + 0) * biomeTileWidth];
				int var19 = var9[var17 + 1 + (stepX + 1) * biomeTileWidth];
				var10[var14] = var15;
				var10[var14++ + var11] = this.selectRandom(var15, var16);
				var10[var14] = this.selectRandom(var15, var18);
				var10[var14++ + var11] = this.selectModeOrRandom(var15, var18, var16, var19);
				var15 = var18;
				var16 = var19;
			}
		}

		int[] var20 = IntCache.getIntCache(areaWidth * areaHeight);

		for (var13 = 0; var13 < areaHeight; ++var13) {
			System.arraycopy(var10, (var13 + (areaY & 1)) * (biomeTileWidth << 1) + (areaX & 1), var20, var13 * areaWidth, areaWidth);
		}

		return var20;
	}

	public static GenLayer magnify(long seedShift, GenLayer parent, int magnificationCount) {
		GenLayer layer = parent;

		for (int i = 0; i < magnificationCount; ++i) {
			layer = new GenLayerZoomMyst(seedShift + i, layer);
		}

		return layer;
	}
}
