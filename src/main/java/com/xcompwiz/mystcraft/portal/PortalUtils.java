package com.xcompwiz.mystcraft.portal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.data.ModBlocks;

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

	public static int isValidLinkPortalBlock(Block block) {
		if (block == getFrameBlock()) return 1;
		if (block == getPortalBlock()) return 1;
		return 0;
	}

	/**
	 * Called by the link portal block to verify that it is still valid after a neighbor update
	 * @param world The world object
	 * @param start The coordinates of the portal block from which to start
	 */
	public static void validatePortal(World world, ChunkCoordinates start) {
		if (world.isRemote) return;
		List<ChunkCoordinates> blocks = new LinkedList<ChunkCoordinates>();
		blocks.add(start);
		while (blocks.size() > 0) {
			ChunkCoordinates coords = blocks.remove(0);
			if (world.getBlock(coords.posX, coords.posY, coords.posZ) != getPortalBlock()) continue;
			validatePortal(world, coords.posX, coords.posY, coords.posZ, blocks);
		}
	}

	public static void firePortal(World world, int i, int j, int k) {
		ChunkCoordinates coord = getReceptacleBase(i, j, k, world.getBlockMetadata(i, j, k));
		onpulse(world, coord.posX, coord.posY, coord.posZ);
		pathto(world, i, j, k);
	}

	public static void shutdownPortal(World world, int i, int j, int k) {
		unpath(world, i, j, k);
	}

	public static ChunkCoordinates getReceptacleBase(int i, int j, int k, int blockMetadata) {
		if (blockMetadata == 0) {
			return new ChunkCoordinates(i, j + 1, k);
		} else if (blockMetadata == 1) {
			return new ChunkCoordinates(i, j - 1, k);
		} else if (blockMetadata == 2) {
			return new ChunkCoordinates(i, j, k + 1);
		} else if (blockMetadata == 3) {
			return new ChunkCoordinates(i, j, k - 1);
		} else if (blockMetadata == 4) {
			return new ChunkCoordinates(i + 1, j, k);
		} else if (blockMetadata == 5) { return new ChunkCoordinates(i - 1, j, k); }
		return new ChunkCoordinates(i, j, k);
	}

	private static void pathto(World world, int i, int j, int k) {
		List<ChunkCoordinates> blocks = new LinkedList<ChunkCoordinates>();
		List<ChunkCoordinates> portals = new LinkedList<ChunkCoordinates>();
		List<ChunkCoordinates> repath = new LinkedList<ChunkCoordinates>();
		List<ChunkCoordinates> redraw = new LinkedList<ChunkCoordinates>();
		blocks.add(new ChunkCoordinates(i, j, k));
		while (portals.size() > 0 || blocks.size() > 0) {
			while (blocks.size() > 0) {
				ChunkCoordinates coords = blocks.remove(0);
				directPortal(world, coords.posX + 1, coords.posY + 0, coords.posZ + 0, 5, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 1, coords.posZ + 0, 1, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 0, coords.posZ + 1, 3, blocks, portals);
				directPortal(world, coords.posX - 1, coords.posY + 0, coords.posZ + 0, 6, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY - 1, coords.posZ + 0, 2, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 0, coords.posZ - 1, 4, blocks, portals);
				redraw.add(coords);
			}
			if (portals.size() > 0) {
				ChunkCoordinates coords = portals.remove(0);
				directPortal(world, coords.posX + 1, coords.posY + 0, coords.posZ + 0, 5, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 1, coords.posZ + 0, 1, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 0, coords.posZ + 1, 3, blocks, portals);
				directPortal(world, coords.posX - 1, coords.posY + 0, coords.posZ + 0, 6, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY - 1, coords.posZ + 0, 2, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 0, coords.posZ - 1, 4, blocks, portals);
				if (world.getBlock(coords.posX, coords.posY, coords.posZ) == getPortalBlock()) {
					repath.add(coords);
				}
			}
		}
		while (repath.size() > 0) {
			ChunkCoordinates coords = repath.remove(0);
			if (world.getBlock(coords.posX, coords.posY, coords.posZ) == getPortalBlock()) {
				if (!isPortalBlockStable(world, coords.posX, coords.posY, coords.posZ)) {
					repathNeighbors(world, coords.posX, coords.posY, coords.posZ);
					world.setBlock(coords.posX, coords.posY, coords.posZ, Blocks.air, 0, 0);
					addSurrounding(repath, coords.posX, coords.posY, coords.posZ);
				} else {
					redraw.add(coords);
				}
			}
		}
		for (ChunkCoordinates coords : redraw) {
			if (world.blockExists(coords.posX, coords.posY, coords.posZ)) {
				world.markBlockForUpdate(coords.posX, coords.posY, coords.posZ);
				world.notifyBlocksOfNeighborChange(coords.posX, coords.posY, coords.posZ, world.getBlock(coords.posX, coords.posY, coords.posZ));
			}
		}
	}

	private static void repathNeighbors(World world, int i, int j, int k) {
		TileEntity tileentity = getTileEntity(world, i, j, k);
		List<ChunkCoordinates> blocks = new LinkedList<ChunkCoordinates>();
		blocks.add(new ChunkCoordinates(i, j, k));
		world.setBlockMetadataWithNotify(i, j, k, 8, 2);
		while (blocks.size() > 0) {
			ChunkCoordinates coords = blocks.remove(0);
			redirectPortal(world, tileentity, coords.posX + 1, coords.posY + 0, coords.posZ + 0, 5, blocks);
			redirectPortal(world, tileentity, coords.posX + 0, coords.posY + 1, coords.posZ + 0, 1, blocks);
			redirectPortal(world, tileentity, coords.posX + 0, coords.posY + 0, coords.posZ + 1, 3, blocks);
			redirectPortal(world, tileentity, coords.posX - 1, coords.posY + 0, coords.posZ + 0, 6, blocks);
			redirectPortal(world, tileentity, coords.posX + 0, coords.posY - 1, coords.posZ + 0, 2, blocks);
			redirectPortal(world, tileentity, coords.posX + 0, coords.posY + 0, coords.posZ - 1, 4, blocks);
		}
	}

	private static void redirectPortal(World world, TileEntity tileentity, int i, int j, int k, int meta, List<ChunkCoordinates> blocks) {
		if (isValidLinkPortalBlock(world.getBlock(i, j, k)) == 0) return;
		if (world.getBlockMetadata(i, j, k) == meta) {
			for (int m = 1; m < 7; ++m) {
				if (m == meta) continue;
				world.setBlockMetadataWithNotify(i, j, k, m, 2);
				TileEntity local = getTileEntity(world, i, j, k);
				if (local == tileentity || (local != null && tileentity == null)) return; // Portal is valid; return
			}
			world.setBlockMetadataWithNotify(i, j, k, 0, 2);
		}
	}

	private static void unpath(World world, int i, int j, int k) {
		List<ChunkCoordinates> blocks = new LinkedList<ChunkCoordinates>();
		List<ChunkCoordinates> notify = new LinkedList<ChunkCoordinates>();
		blocks.add(new ChunkCoordinates(i, j, k));
		while (blocks.size() > 0) {
			ChunkCoordinates coords = blocks.remove(0);
			depolarize(world, coords.posX + 1, coords.posY + 0, coords.posZ + 0, blocks);
			depolarize(world, coords.posX + 0, coords.posY + 1, coords.posZ + 0, blocks);
			depolarize(world, coords.posX + 0, coords.posY + 0, coords.posZ + 1, blocks);
			depolarize(world, coords.posX - 1, coords.posY + 0, coords.posZ + 0, blocks);
			depolarize(world, coords.posX + 0, coords.posY - 1, coords.posZ + 0, blocks);
			depolarize(world, coords.posX + 0, coords.posY + 0, coords.posZ - 1, blocks);
			notify.add(coords);
		}
		for (ChunkCoordinates coords : notify) {
			if (world.blockExists(coords.posX, coords.posY, coords.posZ)) {
				world.markBlockForUpdate(coords.posX, coords.posY, coords.posZ);
				world.notifyBlocksOfNeighborChange(coords.posX, coords.posY, coords.posZ, world.getBlock(coords.posX, coords.posY, coords.posZ));
			}
		}
	}

	private static void onpulse(World world, int i, int j, int k) {
		LinkedList<ChunkCoordinates> set = new LinkedList<ChunkCoordinates>();
		Stack<ChunkCoordinates> validate = new Stack<ChunkCoordinates>();
		addSurrounding(set, i, j, k);
		while (set.size() > 0) {
			ChunkCoordinates coords = set.remove(0);
			expandPortal(world, coords.posX, coords.posY, coords.posZ, set, validate);
		}
		while (validate.size() > 0) {
			ChunkCoordinates coords = validate.pop();
			i = coords.posX;
			j = coords.posY;
			k = coords.posZ;
			if (!checkPortalTension(world, i, j, k)) {
				world.setBlock(i, j, k, Blocks.air, 0, 0);
			}
		}
	}


	private static boolean isPortalBlockStable(World world, int i, int j, int k) {
		if (world.isRemote) return true;
		if (!checkPortalTension(world, i, j, k)) return false;
		if (getTileEntity(world, i, j, k) == null) return false;
		return true;
	}

	private static boolean checkPortalTension(World world, int i, int j, int k) {
		if (world.isRemote) return true;
		int score = 0;
		if (isValidLinkPortalBlock(world.getBlock(i + 1, j + 0, k + 0)) > 0 && isValidLinkPortalBlock(world.getBlock(i - 1, j + 0, k + 0)) > 0) {
			++score;
		}
		if (isValidLinkPortalBlock(world.getBlock(i + 0, j + 1, k + 0)) > 0 && isValidLinkPortalBlock(world.getBlock(i + 0, j - 1, k + 0)) > 0) {
			++score;
		}
		if (isValidLinkPortalBlock(world.getBlock(i + 0, j + 0, k + 1)) > 0 && isValidLinkPortalBlock(world.getBlock(i + 0, j + 0, k - 1)) > 0) {
			++score;
		}
		return score > 1; // NOTE: score == 2 yields forcefield walls
	}

	private static void validatePortal(World world, int i, int j, int k, Collection<ChunkCoordinates> blocks) {
		if (!isPortalBlockStable(world, i, j, k)) {
			world.setBlock(i, j, k, Blocks.air);
			addSurrounding(blocks, i, j, k);
		}
	}

	private static void addSurrounding(Collection<ChunkCoordinates> set, int i, int j, int k) {
		set.add(new ChunkCoordinates(i + 1, j + 0, k + 0));
		set.add(new ChunkCoordinates(i - 1, j + 0, k + 0));
		set.add(new ChunkCoordinates(i + 0, j + 1, k + 0));
		set.add(new ChunkCoordinates(i + 0, j - 1, k + 0));
		set.add(new ChunkCoordinates(i + 0, j + 0, k + 1));
		set.add(new ChunkCoordinates(i + 0, j + 0, k - 1));

		set.add(new ChunkCoordinates(i + 1, j + 1, k + 0));
		set.add(new ChunkCoordinates(i - 1, j + 1, k + 0));
		set.add(new ChunkCoordinates(i + 1, j - 1, k + 0));
		set.add(new ChunkCoordinates(i - 1, j - 1, k + 0));
		set.add(new ChunkCoordinates(i + 0, j + 1, k + 1));
		set.add(new ChunkCoordinates(i + 0, j + 1, k - 1));
		set.add(new ChunkCoordinates(i + 0, j - 1, k + 1));
		set.add(new ChunkCoordinates(i + 0, j - 1, k - 1));
		set.add(new ChunkCoordinates(i + 1, j + 0, k + 1));
		set.add(new ChunkCoordinates(i - 1, j + 0, k + 1));
		set.add(new ChunkCoordinates(i + 1, j + 0, k - 1));
		set.add(new ChunkCoordinates(i - 1, j + 0, k - 1));
	}

	private static void expandPortal(World world, int i, int j, int k, Collection<ChunkCoordinates> set, Stack<ChunkCoordinates> created) {
		if (!world.isAirBlock(i, j, k)) return;
		int score = isValidLinkPortalBlock(world.getBlock(i + 1, j + 0, k + 0)) + isValidLinkPortalBlock(world.getBlock(i - 1, j + 0, k + 0)) + isValidLinkPortalBlock(world.getBlock(i + 0, j + 1, k + 0)) + isValidLinkPortalBlock(world.getBlock(i + 0, j - 1, k + 0)) + isValidLinkPortalBlock(world.getBlock(i + 0, j + 0, k + 1)) + isValidLinkPortalBlock(world.getBlock(i + 0, j + 0, k - 1));
		if (score > 1) {
			world.setBlock(i, j, k, getPortalBlock(), 0, 0);
			created.push(new ChunkCoordinates(i, j, k));
			addSurrounding(set, i, j, k);
		}
	}

	private static void directPortal(World world, int i, int j, int k, int meta, List<ChunkCoordinates> blocks, List<ChunkCoordinates> portals) {
		if (isValidLinkPortalBlock(world.getBlock(i, j, k)) == 0) return;
		if (world.getBlockMetadata(i, j, k) != 0) return;
		world.setBlockMetadataWithNotify(i, j, k, meta, 0);
		if (world.getBlock(i, j, k) == getPortalBlock()) {
			portals.add(new ChunkCoordinates(i, j, k));
		} else {
			blocks.add(new ChunkCoordinates(i, j, k));
		}
	}

	private static void depolarize(World world, int i, int j, int k, List<ChunkCoordinates> blocks) {
		Block block = world.getBlock(i, j, k);
		if (isValidLinkPortalBlock(block) == 0) return;
		if (world.getBlockMetadata(i, j, k) == 0) return;
		world.setBlockMetadataWithNotify(i, j, k, 0, 0);
		if (block == getPortalBlock() && !isPortalBlockStable(world, i, j, k)) {
			world.setBlock(i, j, k, Blocks.air, 0, 2);
		}
		blocks.add(new ChunkCoordinates(i, j, k));
	}

	public static TileEntity getTileEntity(IBlockAccess blockaccess, int i, int j, int k) {
		HashSet<ChunkCoordinates> visited = new HashSet<ChunkCoordinates>();
		Block block = blockaccess.getBlock(i, j, k);
		while (block != getReceptacleBlock()) {
			if (isValidLinkPortalBlock(block) == 0) return null;
			ChunkCoordinates pos = new ChunkCoordinates(i, j, k);
			if (!visited.add(pos)) { return null; }
			int meta = blockaccess.getBlockMetadata(i, j, k);
			if (meta == 0) {
				return null;
			} else if (meta == 1) {
				--j;
			} else if (meta == 2) {
				++j;
			} else if (meta == 3) {
				--k;
			} else if (meta == 4) {
				++k;
			} else if (meta == 5) {
				--i;
			} else if (meta == 6) {
				++i;
			} else {
				return null;
			}
			block = blockaccess.getBlock(i, j, k);
		}
		return blockaccess.getTileEntity(i, j, k);
	}
}
