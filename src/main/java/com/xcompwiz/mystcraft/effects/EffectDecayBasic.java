package com.xcompwiz.mystcraft.effects;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.xcompwiz.mystcraft.api.instability.InstabilityDirector;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.block.BlockDecay;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EffectDecayBasic implements IEnvironmentalEffect {

	private InstabilityDirector controller;
	private int updateLCG;

	private int maxscore = 1000000;
	private DecayHandler.DecayType decayType;
	private int min;
	private Integer max;
	private Set<Material> bannedmats = new HashSet<Material>();

	public EffectDecayBasic(InstabilityDirector controller, DecayHandler.DecayType type, int min, Integer max) {
		this.controller = controller;
		this.updateLCG = (new Random()).nextInt();

		this.decayType = type;
		this.min = min;
		this.max = max;
	}

	public void banMaterial(Material material) {
		bannedmats.add(material);
	}

	protected void placeBlock(World world, BlockPos pos, int minY, Integer maxY, DecayHandler.DecayType type) {
		if (maxY == null) {
			maxY = world.getHeight(pos.getX(), pos.getZ());
			if (maxY <= minY)
				maxY = world.provider.getAverageGroundLevel();
		}
		if (maxY < minY)
			return;
		if (maxY == minY) {
			pos = new BlockPos(pos.getX(), minY, pos.getZ());
		} else {
			pos = new BlockPos(pos.getX(), (pos.getY() % (maxY - minY)) + minY, pos.getZ());
		}
		Material material = world.getBlockState(pos).getMaterial();
		while (bannedmats.contains(material)) {
			pos = pos.down();
			if (pos.getY() < minY) {
				return;
			}
			material = world.getBlockState(pos).getMaterial();
		}
		world.setBlockState(pos, ModBlocks.decay.getDefaultState().withProperty(BlockDecay.DECAY_META, type), 2);
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int chunkX = chunk.x * 16;
		int chunkZ = chunk.z * 16;
		int score = controller.getInstabilityScore();

		if (worldObj.rand.nextInt(maxscore) < score) {
			updateLCG = updateLCG * 3 + 1013904223;
			int coords = updateLCG >> 2;
			int x = chunkX + (coords & 15);
			int z = chunkZ + (coords >> 8 & 15);
			int y = (coords >> 16 & 255);
			placeBlock(worldObj, new BlockPos(x, y, z), min, max, decayType);
		}
	}
}
