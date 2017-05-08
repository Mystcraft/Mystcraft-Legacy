package com.xcompwiz.mystcraft.portal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
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
	public static void validatePortal(World world, ChunkPos start) {
		if (world.isRemote) return;
		List<ChunkPos> blocks = new LinkedList<ChunkPos>();
		blocks.add(start);
		while (blocks.size() > 0) {
			ChunkPos coords = blocks.remove(0);
			if (world.getBlock(coords.posX, coords.posY, coords.posZ) != getPortalBlock()) continue;
			validatePortal(world, coords.posX, coords.posY, coords.posZ, blocks);
		}
	}

	public static void firePortal(World world, int i, int j, int k) {
		ChunkPos coord = getReceptacleBase(i, j, k, world.getBlockMetadata(i, j, k));
		onpulse(world, coord.posX, coord.posY, coord.posZ);
		pathto(world, i, j, k);
	}

	public static void shutdownPortal(World world, int i, int j, int k) {
		unpath(world, i, j, k);
	}

	public static ChunkPos getReceptacleBase(int i, int j, int k, int blockMetadata) {
		if (blockMetadata == 0) {
			return new ChunkPos(i, j + 1, k);
		} else if (blockMetadata == 1) {
			return new ChunkPos(i, j - 1, k);
		} else if (blockMetadata == 2) {
			return new ChunkPos(i, j, k + 1);
		} else if (blockMetadata == 3) {
			return new ChunkPos(i, j, k - 1);
		} else if (blockMetadata == 4) {
			return new ChunkPos(i + 1, j, k);
		} else if (blockMetadata == 5) { return new ChunkPos(i - 1, j, k); }
		return new ChunkPos(i, j, k);
	}

	private static void pathto(World world, int i, int j, int k) {
		List<ChunkPos> blocks = new LinkedList<ChunkPos>();
		List<ChunkPos> portals = new LinkedList<ChunkPos>();
		List<ChunkPos> repath = new LinkedList<ChunkPos>();
		List<ChunkPos> redraw = new LinkedList<ChunkPos>();
		blocks.add(new ChunkPos(i, j, k));
		while (portals.size() > 0 || blocks.size() > 0) {
			while (blocks.size() > 0) {
				ChunkPos coords = blocks.remove(0);
				directPortal(world, coords.posX + 1, coords.posY + 0, coords.posZ + 0, 5, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 1, coords.posZ + 0, 1, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 0, coords.posZ + 1, 3, blocks, portals);
				directPortal(world, coords.posX - 1, coords.posY + 0, coords.posZ + 0, 6, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY - 1, coords.posZ + 0, 2, blocks, portals);
				directPortal(world, coords.posX + 0, coords.posY + 0, coords.posZ - 1, 4, blocks, portals);
				redraw.add(coords);
			}
			if (portals.size() > 0) {
				ChunkPos coords = portals.remove(0);
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
			ChunkPos coords = repath.remove(0);
			if (world.getBlock(coords.posX, coords.posY, coords.posZ) == getPortalBlock()) {
				if (!isPortalBlockStable(world, coords.posX, coords.posY, coords.posZ)) {
					repathNeighbors(world, coords.posX, coords.posY, coords.posZ);
					world.setBlock(coords.posX, coords.posY, coords.posZ, Blocks.AIR, 0, 0);
					addSurrounding(repath, coords.posX, coords.posY, coords.posZ);
				} else {
					redraw.add(coords);
				}
			}
		}
		for (ChunkPos coords : redraw) {
			if (world.blockExists(coords.posX, coords.posY, coords.posZ)) {
				world.markBlockForUpdate(coords.posX, coords.posY, coords.posZ);
				world.notifyBlocksOfNeighborChange(coords.posX, coords.posY, coords.posZ, world.getBlock(coords.posX, coords.posY, coords.posZ));
			}
		}
	}

	private static void repathNeighbors(World world, int i, int j, int k) {
		TileEntity tileentity = getTileEntity(world, i, j, k);
		List<ChunkPos> blocks = new LinkedList<ChunkPos>();
		blocks.add(new ChunkPos(i, j, k));
		world.setBlockMetadataWithNotify(i, j, k, 8, 2);
		while (blocks.size() > 0) {
			ChunkPos coords = blocks.remove(0);
			redirectPortal(world, tileentity, coords.posX + 1, coords.posY + 0, coords.posZ + 0, 5, blocks);
			redirectPortal(world, tileentity, coords.posX + 0, coords.posY + 1, coords.posZ + 0, 1, blocks);
			redirectPortal(world, tileentity, coords.posX + 0, coords.posY + 0, coords.posZ + 1, 3, blocks);
			redirectPortal(world, tileentity, coords.posX - 1, coords.posY + 0, coords.posZ + 0, 6, blocks);
			redirectPortal(world, tileentity, coords.posX + 0, coords.posY - 1, coords.posZ + 0, 2, blocks);
			redirectPortal(world, tileentity, coords.posX + 0, coords.posY + 0, coords.posZ - 1, 4, blocks);
		}
	}

	private static void redirectPortal(World world, TileEntity tileentity, int i, int j, int k, int meta, List<ChunkPos> blocks) {
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
		List<ChunkPos> blocks = new LinkedList<ChunkPos>();
		List<ChunkPos> notify = new LinkedList<ChunkPos>();
		blocks.add(new ChunkPos(i, j, k));
		while (blocks.size() > 0) {
			ChunkPos coords = blocks.remove(0);
			depolarize(world, coords.posX + 1, coords.posY + 0, coords.posZ + 0, blocks);
			depolarize(world, coords.posX + 0, coords.posY + 1, coords.posZ + 0, blocks);
			depolarize(world, coords.posX + 0, coords.posY + 0, coords.posZ + 1, blocks);
			depolarize(world, coords.posX - 1, coords.posY + 0, coords.posZ + 0, blocks);
			depolarize(world, coords.posX + 0, coords.posY - 1, coords.posZ + 0, blocks);
			depolarize(world, coords.posX + 0, coords.posY + 0, coords.posZ - 1, blocks);
			notify.add(coords);
		}
		for (ChunkPos coords : notify) {
			if (world.blockExists(coords.posX, coords.posY, coords.posZ)) {
				world.markBlockForUpdate(coords.posX, coords.posY, coords.posZ);
				world.notifyBlocksOfNeighborChange(coords.posX, coords.posY, coords.posZ, world.getBlock(coords.posX, coords.posY, coords.posZ));
			}
		}
	}

	private static void onpulse(World world, int i, int j, int k) {
		LinkedList<ChunkPos> set = new LinkedList<ChunkPos>();
		Stack<ChunkPos> validate = new Stack<ChunkPos>();
		addSurrounding(set, i, j, k);
		while (set.size() > 0) {
			ChunkPos coords = set.remove(0);
			expandPortal(world, coords.posX, coords.posY, coords.posZ, set, validate);
		}
		while (validate.size() > 0) {
			ChunkPos coords = validate.pop();
			i = coords.posX;
			j = coords.posY;
			k = coords.posZ;
			if (!checkPortalTension(world, i, j, k)) {
				world.setBlock(i, j, k, Blocks.AIR, 0, 0);
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

	private static void validatePortal(World world, int i, int j, int k, Collection<ChunkPos> blocks) {
		if (!isPortalBlockStable(world, i, j, k)) {
			world.setBlock(i, j, k, Blocks.AIR);
			addSurrounding(blocks, i, j, k);
		}
	}

	private static void addSurrounding(Collection<ChunkPos> set, int i, int j, int k) {
		set.add(new ChunkPos(i + 1, j + 0, k + 0));
		set.add(new ChunkPos(i - 1, j + 0, k + 0));
		set.add(new ChunkPos(i + 0, j + 1, k + 0));
		set.add(new ChunkPos(i + 0, j - 1, k + 0));
		set.add(new ChunkPos(i + 0, j + 0, k + 1));
		set.add(new ChunkPos(i + 0, j + 0, k - 1));

		set.add(new ChunkPos(i + 1, j + 1, k + 0));
		set.add(new ChunkPos(i - 1, j + 1, k + 0));
		set.add(new ChunkPos(i + 1, j - 1, k + 0));
		set.add(new ChunkPos(i - 1, j - 1, k + 0));
		set.add(new ChunkPos(i + 0, j + 1, k + 1));
		set.add(new ChunkPos(i + 0, j + 1, k - 1));
		set.add(new ChunkPos(i + 0, j - 1, k + 1));
		set.add(new ChunkPos(i + 0, j - 1, k - 1));
		set.add(new ChunkPos(i + 1, j + 0, k + 1));
		set.add(new ChunkPos(i - 1, j + 0, k + 1));
		set.add(new ChunkPos(i + 1, j + 0, k - 1));
		set.add(new ChunkPos(i - 1, j + 0, k - 1));
	}

	private static void expandPortal(World world, int i, int j, int k, Collection<ChunkPos> set, Stack<ChunkPos> created) {
		if (!world.isAirBlock(i, j, k)) return;
		int score = isValidLinkPortalBlock(world.getBlock(i + 1, j + 0, k + 0)) + isValidLinkPortalBlock(world.getBlock(i - 1, j + 0, k + 0)) + isValidLinkPortalBlock(world.getBlock(i + 0, j + 1, k + 0)) + isValidLinkPortalBlock(world.getBlock(i + 0, j - 1, k + 0)) + isValidLinkPortalBlock(world.getBlock(i + 0, j + 0, k + 1)) + isValidLinkPortalBlock(world.getBlock(i + 0, j + 0, k - 1));
		if (score > 1) {
			world.setBlock(i, j, k, getPortalBlock(), 0, 0);
			created.push(new ChunkPos(i, j, k));
			addSurrounding(set, i, j, k);
		}
	}

	private static void directPortal(World world, int i, int j, int k, int meta, List<ChunkPos> blocks, List<ChunkPos> portals) {
		if (isValidLinkPortalBlock(world.getBlock(i, j, k)) == 0) return;
		if (world.getBlockMetadata(i, j, k) != 0) return;
		world.setBlockMetadataWithNotify(i, j, k, meta, 0);
		if (world.getBlock(i, j, k) == getPortalBlock()) {
			portals.add(new ChunkPos(i, j, k));
		} else {
			blocks.add(new ChunkPos(i, j, k));
		}
	}

	private static void depolarize(World world, int i, int j, int k, List<ChunkPos> blocks) {
		Block block = world.getBlock(i, j, k);
		if (isValidLinkPortalBlock(block) == 0) return;
		if (world.getBlockMetadata(i, j, k) == 0) return;
		world.setBlockMetadataWithNotify(i, j, k, 0, 0);
		if (block == getPortalBlock() && !isPortalBlockStable(world, i, j, k)) {
			world.setBlock(i, j, k, Blocks.AIR, 0, 2);
		}
		blocks.add(new ChunkPos(i, j, k));
	}

	public static TileEntity getTileEntity(IBlockAccess blockaccess, int i, int j, int k) {
		HashSet<ChunkPos> visited = new HashSet<ChunkPos>();
		Block block = blockaccess.getBlock(i, j, k);
		while (block != getReceptacleBlock()) {
			if (isValidLinkPortalBlock(block) == 0) return null;
			ChunkPos pos = new ChunkPos(i, j, k);
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
