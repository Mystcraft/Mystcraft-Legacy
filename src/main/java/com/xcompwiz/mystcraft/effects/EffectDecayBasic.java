package com.xcompwiz.mystcraft.effects;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.api.instability.InstabilityDirector;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.data.ModBlocks;

public class EffectDecayBasic implements IEnvironmentalEffect {

	private InstabilityDirector	controller;
	private int						updateLCG;

	private int						maxscore	= 1000000;
	private int						metadata;
	private int						min;
	private Integer					max;
	private Set<Material>			bannedmats	= new HashSet<Material>();

	public EffectDecayBasic(InstabilityDirector controller, int metadata, int min, Integer max) {
		this.controller = controller;
		this.updateLCG = (new Random()).nextInt();

		this.metadata = metadata;
		this.min = min;
		this.max = max;
	}

	public void banMaterial(Material material) {
		bannedmats.add(material);
	}

	protected void placeBlock(World world, int x, int y, int z, int minY, Integer maxY, int metadata) {
		if (maxY == null) {
			maxY = world.getHeightValue(x, z);
			if (maxY <= minY) maxY = world.provider.getAverageGroundLevel();
		}
		if (maxY < minY) return;
		if (maxY == minY) {
			y = minY;
		} else {
			y = (y % (maxY - minY)) + minY;
		}
		Material material = world.getBlock(x, y, z).getMaterial();
		while (bannedmats.contains(material)) {
			--y;
			if (y < minY) { return; }
			material = world.getBlock(x, y, z).getMaterial();
		}
		world.setBlock(x, y, z, ModBlocks.decay, metadata, 2);
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int chunkX = chunk.xPosition * 16;
		int chunkZ = chunk.zPosition * 16;
		int score = controller.getInstabilityScore();

		if (worldObj.rand.nextInt(maxscore) < score) {
			updateLCG = updateLCG * 3 + 1013904223;
			int coords = updateLCG >> 2;
			int x = chunkX + (coords & 15);
			int z = chunkZ + (coords >> 8 & 15);
			int y = (coords >> 16 & 255);
			placeBlock(worldObj, x, y, z, min, max, metadata);
		}
	}
}
