package com.xcompwiz.mystcraft.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WorldGenMystCrystalFormation extends WorldGeneratorAdv {
	static final byte	otherCoordPairs[]	= { 2, 0, 0, 1, 2, 1 };
	private World		worldObj;
	private Block		block;
	private byte		metadata;
	int					crystalbase[];
	int					baserange			= 2;

	public WorldGenMystCrystalFormation(Block block) {
		this(block, (byte) 0);
	}

	public WorldGenMystCrystalFormation(Block block, byte metadata) {
		this.block = block;
		this.metadata = metadata;
	}

	void generateLine(Random rand) {
		float angle1 = (rand.nextFloat() * 140.0F + 15.0F);
		int length = rand.nextInt(7) + 6;
		int endpoint[] = { crystalbase[0] + (int) (length * Math.cos(angle1 * Math.PI / 180)), crystalbase[1] + (int) (length * Math.sin(angle1 * Math.PI / 180)), crystalbase[2] + rand.nextInt(7) - 3, };
		placeBlockLine(crystalbase, endpoint, block, metadata);
	}

	void placeBlockLine(int ai[], int ai1[], Block block, byte metadata) {
		int ai2[] = { 0, 0, 0 };
		byte byte0 = 0;
		int j = 0;
		for (; byte0 < 3; ++byte0) {
			ai2[byte0] = ai1[byte0] - ai[byte0];
			if (Math.abs(ai2[byte0]) > Math.abs(ai2[j])) {
				j = byte0;
			}
		}

		if (ai2[j] == 0) { return; }
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
			ai3[j] = MathHelper.floor_double((ai[j] + k) + 0.5D);
			ai3[byte1] = MathHelper.floor_double(ai[byte1] + k * d + 0.5D);
			ai3[byte2] = MathHelper.floor_double(ai[byte2] + k * d1 + 0.5D);
			drawSphere(ai3[0], ai3[1], ai3[2], block, metadata);// setBlockAndMetadata(worldObj, ai3[0], ai3[1], ai3[2], i, 0);
		}
	}

	void drawSphere(int i, int j, int k, Block block, byte metadata) {
		setBlock(i, j, k, block, metadata, 3);
		setBlock(i + 1, j, k, block, metadata, 3);
		setBlock(i - 1, j, k, block, metadata, 3);
		setBlock(i, j + 1, k, block, metadata, 3);
		setBlock(i, j - 1, k, block, metadata, 3);
		setBlock(i, j, k + 1, block, metadata, 3);
		setBlock(i, j, k - 1, block, metadata, 3);
	}

	private void setBlock(int i, int j, int k, Block block, byte metadata, int mask) {
		if (worldObj.getBlock(i, j, k) != Blocks.bedrock) {
			placeBlock(worldObj, i, j, k, block, metadata, mask);
		}
	}

	@Override
	public boolean doGeneration(World world, Random rand, int i, int j, int k) {
		worldObj = world;
		crystalbase = new int[3];
		crystalbase[0] = i;
		crystalbase[1] = 0;
		crystalbase[2] = k;

		if (!validLocation()) return false;
		int count = rand.nextInt(3) + 1;
		for (int i1 = 0; i1 < count; ++i1) {
			generateLine(rand);
		}

		return true;
	}

	private boolean validLocation() {
		Block i = worldObj.getBlock(crystalbase[0], crystalbase[1], crystalbase[2]);
		while (i == Blocks.air) {
			++crystalbase[1];
			if (crystalbase[1] > worldObj.getHeight()) return false;
			i = worldObj.getBlock(crystalbase[0], crystalbase[1], crystalbase[2]);
		}
		while (i != Blocks.water && i != Blocks.water && i != Blocks.air) {
			++crystalbase[1];
			i = worldObj.getBlock(crystalbase[0], crystalbase[1], crystalbase[2]);
		}
		crystalbase[1] -= 2;
		return worldObj.getBlock(crystalbase[0], crystalbase[1], crystalbase[2]) != Blocks.air;
	}
}
