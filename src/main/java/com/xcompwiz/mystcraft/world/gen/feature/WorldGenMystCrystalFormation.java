package com.xcompwiz.mystcraft.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WorldGenMystCrystalFormation extends WorldGeneratorAdv {

	static final byte otherCoordPairs[] = { 2, 0, 0, 1, 2, 1 };
	private World worldObj;
	private IBlockState block;
	private BlockPos crystalPos;

	public WorldGenMystCrystalFormation(Block block) {
		this(block.getDefaultState());
	}

	public WorldGenMystCrystalFormation(IBlockState state) {
		this.block = state;
	}

	private void generateLine(Random rand) {
		float angle1 = (rand.nextFloat() * 140.0F + 15.0F);
		int length = rand.nextInt(7) + 6;
		int[] end = new int[] {
				crystalPos.getX() + (int) (length * Math.cos(angle1 * Math.PI / 180)),
				crystalPos.getY() + (int) (length * Math.sin(angle1 * Math.PI / 180)),
				crystalPos.getZ() + rand.nextInt(7) - 3
		};
		int[] start = new int[] {
				crystalPos.getX(),
				crystalPos.getY(),
				crystalPos.getZ()
		};
		placeBlockLine(start, end, block);
	}

	private void placeBlockLine(int ai[], int ai1[], IBlockState state) {
		int ai2[] = { 0, 0, 0 };
		byte byte0 = 0;
		int j = 0;
		for (; byte0 < 3; ++byte0) {
			ai2[byte0] = ai1[byte0] - ai[byte0];
			if (Math.abs(ai2[byte0]) > Math.abs(ai2[j])) {
				j = byte0;
			}
		}

		if (ai2[j] == 0) {
			return;
		}
		byte byte1 = otherCoordPairs[j];
		byte byte2 = otherCoordPairs[j + 3];
		byte byte3;
		if (ai2[j] > 0) {
			byte3 = 1;
		} else {
			byte3 = -1;
		}
		double d = (double) ai2[byte1] / (double) ai2[j];
		double d1 = (double) ai2[byte2] / (double) ai2[j];
		int ai3[] = { 0, 0, 0 };
		int k = 0;
		for (int l = ai2[j] + byte3; k != l; k += byte3) {
			ai3[j] = MathHelper.floor((ai[j] + k) + 0.5D);
			ai3[byte1] = MathHelper.floor(ai[byte1] + k * d + 0.5D);
			ai3[byte2] = MathHelper.floor(ai[byte2] + k * d1 + 0.5D);
			drawSphere(new BlockPos(ai3[0], ai3[1], ai3[2]), state);// setBlockAndMetadata(worldObj, ai3[0], ai3[1], ai3[2], i, 0);
		}
	}

	private void drawSphere(BlockPos pos, IBlockState state) {
		setBlock(pos, state);
		for (EnumFacing face : EnumFacing.VALUES) {
			setBlock(pos.offset(face), state);
		}
	}

	private void setBlock(BlockPos pos, IBlockState state) {
		if (!worldObj.getBlockState(pos).getBlock().equals(Blocks.BEDROCK)) {
			placeBlock(worldObj, pos, state, 3);
		}
	}

	@Override
	public boolean doGeneration(World world, Random rand, BlockPos pos) {
		worldObj = world;
		crystalPos = new BlockPos(pos.getX(), 0, pos.getZ());

		if (!validLocation())
			return false;
		int count = rand.nextInt(3) + 1;
		for (int i1 = 0; i1 < count; ++i1) {
			generateLine(rand);
		}

		return true;
	}

	private boolean validLocation() {
		IBlockState state = worldObj.getBlockState(crystalPos);
		while (state.getMaterial() != Material.AIR) {
			crystalPos = crystalPos.up();
			if (crystalPos.getY() > worldObj.getHeight()) {
				return false;
			}
			state = worldObj.getBlockState(crystalPos);
		}
		while (!state.getMaterial().isLiquid() && state.getMaterial() != Material.AIR) {
			crystalPos = crystalPos.up();
		}
		crystalPos = crystalPos.down(2);
		return worldObj.getBlockState(crystalPos).getMaterial() != Material.AIR;
	}
}
