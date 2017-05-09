package com.xcompwiz.mystcraft.portal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xcompwiz.mystcraft.block.BlockBookReceptacle;
import com.xcompwiz.mystcraft.block.BlockCrystal;
import com.xcompwiz.mystcraft.block.BlockLinkPortal;
import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class PortalUtils {

	public static Block getPortalBlock() {
		return ModBlocks.portal;
	}

	public static Block getFrameBlock() {
		return ModBlocks.crystal;
	}

	public static Block getReceptacleBlock() {
		return ModBlocks.receptacle;
	}

	public static int isValidLinkPortalBlock(IBlockState blockstate) {
		if (blockstate.getBlock() == getFrameBlock()) return 1;
		if (blockstate.getBlock() == getPortalBlock()) return 1;
		return 0;
	}

	private static EnumFacing getBlockFacing(IBlockState blockstate) {
		if (blockstate.getBlock() == getFrameBlock()) return blockstate.getValue(BlockCrystal.SOURCE_DIRECTION);
		if (blockstate.getBlock() == getPortalBlock()) return blockstate.getValue(BlockLinkPortal.SOURCE_DIRECTION);
		if (blockstate.getBlock() == getReceptacleBlock()) return blockstate.getValue(BlockBookReceptacle.ROTATION);
		return EnumFacing.DOWN;
	}

	private static boolean isBlockActive(IBlockState blockstate) {
		if (blockstate.getBlock() == getFrameBlock()) return blockstate.getValue(BlockCrystal.IS_PART_OF_PORTAL);
		if (blockstate.getBlock() == getPortalBlock()) return blockstate.getValue(BlockLinkPortal.IS_PART_OF_PORTAL);
		return false;
	}

	private static IBlockState getDirectedState(IBlockState blockstate, int m) {
		if (blockstate.getBlock() == getFrameBlock()) return blockstate.withProperty(BlockCrystal.IS_PART_OF_PORTAL, true).withProperty(BlockCrystal.SOURCE_DIRECTION, EnumFacing.values()[m]);
		if (blockstate.getBlock() == getPortalBlock()) return blockstate.withProperty(BlockLinkPortal.IS_PART_OF_PORTAL, true).withProperty(BlockLinkPortal.SOURCE_DIRECTION, EnumFacing.values()[m]);
		return blockstate;
	}

	/**
	 * Called by the link portal block to verify that it is still valid after a neighbor update
	 * @param world The world object
	 * @param start The coordinates of the portal block from which to start
	 */
	public static void validatePortal(World world, BlockPos start) {
		if (world.isRemote) return;
		List<BlockPos> blocks = new LinkedList<BlockPos>();
		blocks.add(start);
		while (blocks.size() > 0) {
			BlockPos coords = blocks.remove(0);
			if (world.getBlockState(coords).getBlock() != getPortalBlock()) continue;
			validatePortal(world, coords, blocks);
		}
	}

	public static void firePortal(World world, BlockPos pos) {
		BlockPos coord = getReceptacleBase(pos, getBlockFacing(world.getBlockState(pos)));
		onpulse(world, coord);
		pathto(world, pos);
	}

	public static void shutdownPortal(World world, BlockPos pos) {
		unpath(world, pos);
	}

	public static BlockPos getReceptacleBase(BlockPos pos, EnumFacing facing) {
		return pos.offset(facing.getOpposite());
	}

	private static void pathto(World world, BlockPos pos) {
		List<BlockPos> blocks = new LinkedList<BlockPos>();
		List<BlockPos> portals = new LinkedList<BlockPos>();
		List<BlockPos> repath = new LinkedList<BlockPos>();
		List<BlockPos> redraw = new LinkedList<BlockPos>();
		blocks.add(pos);
		while (portals.size() > 0 || blocks.size() > 0) {
			while (blocks.size() > 0) {
				BlockPos coords = blocks.remove(0);
				directPortal(world, coords.east(), 5, blocks, portals);
				directPortal(world, coords.up(), 1, blocks, portals);
				directPortal(world, coords.south(), 3, blocks, portals);
				directPortal(world, coords.west(), 6, blocks, portals);
				directPortal(world, coords.down(), 2, blocks, portals);
				directPortal(world, coords.north(), 4, blocks, portals);
				redraw.add(coords);
			}
			if (portals.size() > 0) {
				BlockPos coords = portals.remove(0);
				directPortal(world, coords.east(), 5, blocks, portals);
				directPortal(world, coords.up(), 1, blocks, portals);
				directPortal(world, coords.south(), 3, blocks, portals);
				directPortal(world, coords.west(), 6, blocks, portals);
				directPortal(world, coords.down(), 2, blocks, portals);
				directPortal(world, coords.north(), 4, blocks, portals);
				if (world.getBlockState(coords).getBlock() == getPortalBlock()) {
					repath.add(coords);
				}
			}
		}
		while (repath.size() > 0) {
			BlockPos coords = repath.remove(0);
			if (world.getBlockState(coords).getBlock() == getPortalBlock()) {
				if (!isPortalBlockStable(world, coords)) {
					repathNeighbors(world, coords);
					world.setBlockState(coords, Blocks.AIR.getDefaultState(), 0);
					addSurrounding(repath, coords);
				} else {
					redraw.add(coords);
				}
			}
		}
		for (BlockPos coords : redraw) {
			if (world.isChunkGeneratedAt(coords.getX() >> 4, coords.getZ() >> 4)) {
				IBlockState blockState = world.getBlockState(coords);
				world.notifyBlockUpdate(coords, blockState, blockState, 3);
			}
		}
	}

	private static void repathNeighbors(World world, BlockPos pos) {
		TileEntity tileentity = getTileEntity(world, pos);
		List<BlockPos> blocks = new LinkedList<BlockPos>();
		blocks.add(pos);
		//world.setBlockMetadataWithNotify(pos, 8, 2);
		while (blocks.size() > 0) {
			BlockPos coords = blocks.remove(0);
			redirectPortal(world, tileentity, coords.east(), 5, blocks);
			redirectPortal(world, tileentity, coords.up(), 1, blocks);
			redirectPortal(world, tileentity, coords.south(), 3, blocks);
			redirectPortal(world, tileentity, coords.west(), 6, blocks);
			redirectPortal(world, tileentity, coords.down(), 2, blocks);
			redirectPortal(world, tileentity, coords.north(), 4, blocks);
		}
	}

	private static void redirectPortal(World world, TileEntity tileentity, BlockPos pos, int meta, List<BlockPos> blocks) {
		IBlockState blockstate = world.getBlockState(pos);
		if (isValidLinkPortalBlock(blockstate) == 0) return;
		if (getBlockFacing(blockstate).ordinal() + 1 == meta) {
			for (int m = 1; m < 7; ++m) {
				if (m == meta) continue;
				world.setBlockState(pos, getDirectedState(blockstate, m - 1), 2);
				TileEntity local = getTileEntity(world, pos);
				if (local == tileentity || (local != null && tileentity == null)) return; // Portal is valid; return
			}
			world.setBlockState(pos, blockstate.getBlock().getDefaultState(), 2);
		}
	}

	private static void unpath(World world, BlockPos pos) {
		List<BlockPos> blocks = new LinkedList<BlockPos>();
		List<BlockPos> notify = new LinkedList<BlockPos>();
		blocks.add(new BlockPos(pos));
		while (blocks.size() > 0) {
			BlockPos coords = blocks.remove(0);
			depolarize(world, coords.east(), blocks);
			depolarize(world, coords.up(), blocks);
			depolarize(world, coords.south(), blocks);
			depolarize(world, coords.west(), blocks);
			depolarize(world, coords.down(), blocks);
			depolarize(world, coords.north(), blocks);
			notify.add(coords);
		}
		for (BlockPos coords : notify) {
			if (world.isChunkGeneratedAt(coords.getX() >> 4, coords.getZ() >> 4)) {
				IBlockState blockState = world.getBlockState(coords);
				world.notifyBlockUpdate(coords, blockState, blockState, 3);
			}
		}
	}

	private static void onpulse(World world, BlockPos pos) {
		LinkedList<BlockPos> set = new LinkedList<BlockPos>();
		Stack<BlockPos> validate = new Stack<BlockPos>();
		addSurrounding(set, pos);
		while (set.size() > 0) {
			BlockPos coords = set.remove(0);
			expandPortal(world, coords, set, validate);
		}
		while (validate.size() > 0) {
			BlockPos coords = validate.pop();
			if (!checkPortalTension(world, coords)) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
			}
		}
	}

	private static boolean isPortalBlockStable(World world, BlockPos pos) {
		if (world.isRemote) return true;
		if (!checkPortalTension(world, pos)) return false;
		if (getTileEntity(world, pos) == null) return false;
		return true;
	}

	private static boolean checkPortalTension(World world, BlockPos pos) {
		if (world.isRemote) return true;
		int score = 0;
		if (isValidLinkPortalBlock(world.getBlockState(pos.east())) > 0 && isValidLinkPortalBlock(world.getBlockState(pos.west())) > 0) {
			++score;
		}
		if (isValidLinkPortalBlock(world.getBlockState(pos.up())) > 0 && isValidLinkPortalBlock(world.getBlockState(pos.down())) > 0) {
			++score;
		}
		if (isValidLinkPortalBlock(world.getBlockState(pos.south())) > 0 && isValidLinkPortalBlock(world.getBlockState(pos.north())) > 0) {
			++score;
		}
		return score > 1; // NOTE: score == 2 yields forcefield walls
	}

	private static void validatePortal(World world, BlockPos pos, Collection<BlockPos> blocks) {
		if (!isPortalBlockStable(world, pos)) {
			world.setBlockToAir(pos);
			addSurrounding(blocks, pos);
		}
	}

	private static void addSurrounding(Collection<BlockPos> set, BlockPos pos) {
		set.add(new BlockPos(pos.west()));
		set.add(new BlockPos(pos.east()));
		set.add(new BlockPos(pos.up()));
		set.add(new BlockPos(pos.down()));
		set.add(new BlockPos(pos.south()));
		set.add(new BlockPos(pos.north()));

		set.add(new BlockPos(pos.west().up()));
		set.add(new BlockPos(pos.east().up()));
		set.add(new BlockPos(pos.west().down()));
		set.add(new BlockPos(pos.east().down()));
		set.add(new BlockPos(pos.south().up()));
		set.add(new BlockPos(pos.north().up()));
		set.add(new BlockPos(pos.south().down()));
		set.add(new BlockPos(pos.north().down()));
		set.add(new BlockPos(pos.west().south()));
		set.add(new BlockPos(pos.east().south()));
		set.add(new BlockPos(pos.west().north()));
		set.add(new BlockPos(pos.east().north()));
	}

	private static void expandPortal(World world, BlockPos pos, Collection<BlockPos> set, Stack<BlockPos> created) {
		if (!world.isAirBlock(pos)) return;
		int score = isValidLinkPortalBlock(world.getBlockState(pos.east())) + isValidLinkPortalBlock(world.getBlockState(pos.west())) + isValidLinkPortalBlock(world.getBlockState(pos.up())) + isValidLinkPortalBlock(world.getBlockState(pos.down())) + isValidLinkPortalBlock(world.getBlockState(pos.south())) + isValidLinkPortalBlock(world.getBlockState(pos.north()));
		if (score > 1) {
			world.setBlockState(pos, getPortalBlock().getDefaultState(), 0);
			created.push(pos);
			addSurrounding(set, pos);
		}
	}

	private static void directPortal(World world, BlockPos pos, int meta, List<BlockPos> blocks, List<BlockPos> portals) {
		IBlockState blockState = world.getBlockState(pos);
		if (isValidLinkPortalBlock(blockState) == 0) return;
		if (isBlockActive(blockState)) return;
		if (blockState.getBlock() == getPortalBlock()) {
			portals.add(pos);
		} else {
			blocks.add(pos);
		}
	}

	private static void depolarize(World world, BlockPos pos, List<BlockPos> blocks) {
		IBlockState blockstate = world.getBlockState(pos);
		if (isValidLinkPortalBlock(blockstate) == 0) return;
		if (!isBlockActive(blockstate)) return;
		world.setBlockState(pos, blockstate.getBlock().getDefaultState(), 0);
		if (blockstate.getBlock() == getPortalBlock() && !isPortalBlockStable(world, pos)) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
		}
		blocks.add(pos);
	}

	public static TileEntity getTileEntity(IBlockAccess blockaccess, BlockPos pos) {
		HashSet<BlockPos> visited = new HashSet<BlockPos>();
		IBlockState blockstate = blockaccess.getBlockState(pos);
		while (blockstate.getBlock() != getReceptacleBlock()) {
			if (isValidLinkPortalBlock(blockstate) == 0) return null;
			if (!visited.add(pos)) { return null; }
			pos = getReceptacleBase(pos, getBlockFacing(blockstate));
			blockstate = blockaccess.getBlockState(pos);
		}
		return blockaccess.getTileEntity(pos);
	}
}
