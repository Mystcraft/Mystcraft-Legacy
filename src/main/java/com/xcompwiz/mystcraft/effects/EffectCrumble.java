package com.xcompwiz.mystcraft.effects;

import java.util.HashMap;
import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EffectCrumble implements IEnvironmentalEffect {
	public static class BlockSpec {
		Block	block		= null;
		int		metadata	= 0;

		public BlockSpec(Block block, int metadata) {
			this.block = block;
			this.metadata = metadata;
		}
	}

	private int													updateLCG	= (new Random()).nextInt();
	private static HashMap<Block, HashMap<Integer, BlockSpec>>	map;

	public static void initMappings() {
		if (map != null) return;
		map = new HashMap<Block, HashMap<Integer, BlockSpec>>();
		registerMapping(Blocks.coal_ore, Blocks.stone);
		registerMapping(Blocks.iron_ore, Blocks.stone);
		registerMapping(Blocks.redstone_ore, Blocks.stone);
		registerMapping(Blocks.lit_redstone_ore, Blocks.stone);
		registerMapping(Blocks.gold_ore, Blocks.stone);
		registerMapping(Blocks.diamond_ore, Blocks.coal_ore);
		registerMapping(Blocks.lapis_ore, Blocks.stone);

		registerMapping(Blocks.ice, Blocks.water);

		registerMapping(Blocks.glowstone, Blocks.glass);
		registerMapping(ModBlocks.crystal, Blocks.glass);

		registerMapping(Blocks.nether_brick, Blocks.netherrack);
		registerMapping(Blocks.quartz_ore, Blocks.netherrack);
		registerMapping(Blocks.netherrack, Blocks.soul_sand);
		registerMapping(Blocks.soul_sand, Blocks.gravel);

		registerMapping(Blocks.stonebrick, Blocks.stone);
		registerMapping(Blocks.stone, Blocks.gravel);
		registerMapping(Blocks.cobblestone, Blocks.gravel);
		registerMapping(Blocks.grass, Blocks.dirt);
		registerMapping(Blocks.mycelium, Blocks.dirt);
		registerMapping(Blocks.brown_mushroom_block, Blocks.dirt);
		registerMapping(Blocks.red_mushroom_block, Blocks.dirt);
		registerMapping(Blocks.clay, Blocks.dirt);

		registerMapping(Blocks.gravel, Blocks.sand);
		registerMapping(Blocks.dirt, Blocks.sand);
		registerMapping(Blocks.glass, Blocks.sand);
		registerMapping(Blocks.sandstone, Blocks.sand);

		registerMapping(Blocks.log, Blocks.planks);
		registerMapping(Blocks.log2, Blocks.planks);
		registerMapping(Blocks.planks, Blocks.dirt);

		registerMapping(Blocks.wool, Blocks.wool);
		registerMapping(Blocks.wool, 0, Blocks.web, 0);

		registerMapping(Blocks.sapling, Blocks.air);
		registerMapping(Blocks.web, Blocks.air);
		registerMapping(Blocks.leaves, Blocks.air);
		registerMapping(Blocks.tallgrass, Blocks.air);
		registerMapping(Blocks.brown_mushroom, Blocks.air);
		registerMapping(Blocks.red_mushroom, Blocks.air);
		registerMapping(Blocks.red_flower, Blocks.air);
		registerMapping(Blocks.yellow_flower, Blocks.air);
	}

	public static void registerMapping(Block block, Block block2) {
		for (int i = 0; i < 16; ++i)
			registerMapping(block, i, block2, 0);
	}

	public static boolean registerMapping(Block block, int metadata, Block block2, int metadata2) {
		if (map == null) return false;
		HashMap<Integer, BlockSpec> metaMap = getBlockMap(block);
		metaMap.put(metadata, new BlockSpec(block2, metadata2));
		return true;
	}

	private static HashMap<Integer, BlockSpec> getBlockMap(Block block) {
		HashMap<Integer, BlockSpec> map2 = map.get(block);
		if (map2 == null) {
			map2 = new HashMap<Integer, BlockSpec>();
			map.put(block, map2);
		}
		return map2;
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int chunkX = chunk.xPosition * 16;
		int chunkZ = chunk.zPosition * 16;
		int coords;
		int x;
		int z;
		int y;

		updateLCG = updateLCG * 3 + 1013904223;
		coords = updateLCG >> 2;
		x = chunkX + (coords & 15);
		z = chunkZ + (coords >> 8 & 15);
		y = (coords >> 16 & 255);
		crumbleBlock(worldObj, x, y, z);
	}

	private static void crumbleBlock(World worldObj, int x, int y, int z) {
		Block block = worldObj.getBlock(x, y, z);
		int metadata = worldObj.getBlockMetadata(x, y, z);
		HashMap<Integer, BlockSpec> map2 = map.get(block);
		if (map2 == null) return;
		BlockSpec mapping = map2.get(metadata);
		if (mapping != null) {
			worldObj.setBlock(x, y, z, mapping.block, mapping.metadata, 2);
		}
	}
}
