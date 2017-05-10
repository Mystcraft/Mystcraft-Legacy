package com.xcompwiz.mystcraft.effects;

import java.util.HashMap;
import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EffectCrumble implements IEnvironmentalEffect {
	private int													updateLCG	= (new Random()).nextInt();
	private static HashMap<IBlockState, IBlockState>	map;

	public static void initMappings() {
		if (map != null) return;
		map = new HashMap<>();
		registerMapping(Blocks.COAL_ORE, Blocks.STONE);
		registerMapping(Blocks.IRON_ORE, Blocks.STONE);
		registerMapping(Blocks.REDSTONE_ORE, Blocks.STONE);
		registerMapping(Blocks.LIT_REDSTONE_ORE, Blocks.STONE);
		registerMapping(Blocks.GOLD_ORE, Blocks.STONE);
		registerMapping(Blocks.DIAMOND_ORE, Blocks.COAL_ORE);
		registerMapping(Blocks.LAPIS_ORE, Blocks.STONE);

		registerMapping(Blocks.ICE, Blocks.WATER);

		registerMapping(Blocks.GLOWSTONE, Blocks.GLASS);
		registerMapping(ModBlocks.crystal, Blocks.GLASS);

		registerMapping(Blocks.NETHER_BRICK, Blocks.NETHERRACK);
		registerMapping(Blocks.QUARTZ_ORE, Blocks.NETHERRACK);
		registerMapping(Blocks.NETHERRACK, Blocks.SOUL_SAND);
		registerMapping(Blocks.SOUL_SAND, Blocks.GRAVEL);

		registerMapping(Blocks.STONEBRICK, Blocks.STONE);
		registerMapping(Blocks.STONE, Blocks.GRAVEL);
		registerMapping(Blocks.COBBLESTONE, Blocks.GRAVEL);
		registerMapping(Blocks.GRASS, Blocks.DIRT);
		registerMapping(Blocks.MYCELIUM, Blocks.DIRT);
		registerMapping(Blocks.BROWN_MUSHROOM_BLOCK, Blocks.DIRT);
		registerMapping(Blocks.RED_MUSHROOM_BLOCK, Blocks.DIRT);
		registerMapping(Blocks.CLAY, Blocks.DIRT);

		registerMapping(Blocks.GRAVEL, Blocks.SAND);
		registerMapping(Blocks.DIRT, Blocks.SAND);
		registerMapping(Blocks.GLASS, Blocks.SAND);
		registerMapping(Blocks.SANDSTONE, Blocks.SAND);

		registerMapping(Blocks.LOG, Blocks.PLANKS);
		registerMapping(Blocks.LOG2, Blocks.PLANKS);
		registerMapping(Blocks.PLANKS, Blocks.DIRT);

		registerMapping(Blocks.WOOL, Blocks.WEB);
		//registerMapping(Blocks.WOOL, Blocks.WOOL);
		//registerMapping(Blocks.WOOL, 0, Blocks.WEB, 0);

		registerMapping(Blocks.SAPLING, Blocks.AIR);
		registerMapping(Blocks.WEB, Blocks.AIR);
		registerMapping(Blocks.LEAVES, Blocks.AIR);
		registerMapping(Blocks.TALLGRASS, Blocks.AIR);
		registerMapping(Blocks.BROWN_MUSHROOM, Blocks.AIR);
		registerMapping(Blocks.RED_MUSHROOM, Blocks.AIR);
		registerMapping(Blocks.RED_FLOWER, Blocks.AIR);
		registerMapping(Blocks.YELLOW_FLOWER, Blocks.AIR);
	}

	public static boolean registerMapping(Block block, Block block2) {
		return registerMapping(block.getBlockState().getBaseState(), block2.getBlockState().getBaseState());
	}
	
	public static boolean registerMapping(IBlockState block, IBlockState block2) {
		if (map == null) return false;
		map.put(block, block2);
		return true;
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
		crumbleBlock(worldObj, new BlockPos(x, y, z));
	}

	private static void crumbleBlock(World worldObj, BlockPos pos) {
		IBlockState block = worldObj.getBlockState(pos);
		IBlockState mapping = map.get(block);
		if (mapping != null) {
			worldObj.setBlockState(pos, mapping);
		}
	}
}
