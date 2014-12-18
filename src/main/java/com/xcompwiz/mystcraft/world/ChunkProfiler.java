package com.xcompwiz.mystcraft.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.xcompwiz.mystcraft.debug.DebugHierarchy;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugValueCallback;
import com.xcompwiz.mystcraft.instability.InstabilityBlockManager;

public class ChunkProfiler extends WorldSavedData {
	public static final String	ID			= "MystChunkProfile";
	private static final int	MAP_LENGTH	= 256 * 256;

	public static class ChunkProfileData {
		public int[]	data	= new int[MAP_LENGTH];
		public int		count	= 0;

		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setInteger("count", count);
			nbt.setIntArray("data", data);
			return nbt;
		}

		public void readFromNBT(NBTTagCompound nbt) {
			this.count = nbt.getInteger("count");
			this.data = nbt.getIntArray("data");
			if (this.data == null || this.data.length < MAP_LENGTH) {
				this.data = new int[MAP_LENGTH];
				this.count = 0;
			}
		}
	}

	private int								count;
	private ChunkProfileData				solid;
	private Map<String, ChunkProfileData>	blockmaps;
	private static boolean					outputfiles	= false;

	private Semaphore						semaphore	= new Semaphore(1, true);

	static {
		DebugHierarchy.register("profiler.output", new DebugValueCallback() {

			@Override
			public void set(ICommandSender agent, boolean state) {
				outputfiles = state;
			}

			@Override
			public String get(ICommandSender agent) {
				return Boolean.toString(outputfiles);
			}
		});
	}

	public ChunkProfiler(String id) {
		super(id);
		count = 0;
		solid = new ChunkProfileData();
		blockmaps = new HashMap<String, ChunkProfileData>();
		for (String blockkey : InstabilityBlockManager.getWatchedBlocks()) {
			blockmaps.put(blockkey, new ChunkProfileData());
		}
	}

	public void baseChunk(Chunk chunk, int chunkX, int chunkZ) {}

	public int calculateInstability() {
		if (outputfiles) outputFiles();
		if (!InstabilityBlockManager.isBaselineConstructed()) return 0;
		HashMap<String, Float> split = calculateSplitInstability();
		float instability = 0;
		for (Entry<String, Float> entry : split.entrySet()) {
			float val = entry.getValue();
			if (val > 0) {
				val = Math.max(0, val - InstabilityBlockManager.getBaseline(entry.getKey()));
			}
			instability += val;
		}
		return Math.round(instability);
	}

	public HashMap<String, Float> calculateSplitInstability() {
		int layers = solid.data.length / 256;
		HashMap<String, Float> split = new HashMap<String, Float>();
		//For all cells, calculate instability
		for (int y = 0; y < layers; ++y) {
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					int coords = y << 8 | z << 4 | x;
					float availability = 1 - (solid.data[coords] / (float) solid.count);
					for (String blockkey : InstabilityBlockManager.getWatchedBlocks()) {
						ChunkProfileData map = blockmaps.get(blockkey);
						if (map.count < 100) continue;
						float factor1 = InstabilityBlockManager.ro_factor1s.get(blockkey);
						float factor2 = InstabilityBlockManager.ro_factor2s.get(blockkey);
						float val = map.data[coords] / (float) map.count;
						val = val * availability * factor1 + val * factor2;
						if (!split.containsKey(blockkey)) {
							split.put(blockkey, 0.0F);
						}
						split.put(blockkey, split.get(blockkey) + val);
					}
				}
			}
		}
		return split;
	}

	public void profile(Chunk chunk, int chunkX, int chunkZ) {
		profileChunk(chunk, solid, blockmaps);
		++count;
		this.markDirty();
	}

	private void profileChunk(Chunk chunk, ChunkProfileData soliddata, Map<String, ChunkProfileData> maps) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to aquire semaphore to profile chunk (interrupted)!");
		}
		ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();
		int[] solidmap = soliddata.data;
		int layers = solidmap.length / 256;
		for (int y = 0; y < layers; ++y) {
			int storagei = y >> 4;
			if (storageArrays[storagei] == null) continue;
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					int coords = y << 8 | z << 4 | x;

					Block block = storageArrays[storagei].getBlockByExtId(x, y & 15, z);
					int metadata = storageArrays[storagei].getExtBlockMetadata(x, y & 15, z);

					int accessibility = (block != Blocks.air ? 2 : 0);
					if (maps != null) {
						ChunkProfileData map = maps.get(InstabilityBlockManager.getUnlocalizedKey(block, metadata));
						if (map != null) {
							++map.data[coords];
							accessibility = 1;
						}
					}
					//Checks if isPassable
					if (block.getBlocksMovement(chunk.worldObj, (chunk.xPosition << 4) + x, y, (chunk.zPosition << 4) + z)) {
						accessibility = 1;
					}
					//Checks if isAir
					if (block.isAir(chunk.worldObj, (chunk.xPosition << 4) + x, y, (chunk.zPosition << 4) + z)) {
						accessibility = 0;
					}
					solidmap[coords] += accessibility;
				}
			}
		}
		soliddata.count += 2;
		if (maps != null) {
			for (Map.Entry<String, ChunkProfileData> entry : maps.entrySet()) {
				ChunkProfileData map = entry.getValue();
				++map.count;
			}
		}
		semaphore.release();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("solid", solid.writeToNBT(new NBTTagCompound()));
		if (blockmaps == null) return;
		for (Map.Entry<String, ChunkProfileData> entry : blockmaps.entrySet()) {
			String blockkey = entry.getKey();
			ChunkProfileData map = entry.getValue();
			nbt.setTag(blockkey, map.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		solid.readFromNBT(nbt.getCompoundTag("solid"));
		count = solid.count;
		if (nbt.hasKey("tile.myst.fluid")) {
			nbt.setTag("tile.myst.fluid.myst.ink.black", nbt.getTag("tile.myst.fluid"));
		}

		if (blockmaps == null) return;
		for (String blockkey : blockmaps.keySet()) {
			ChunkProfileData map = new ChunkProfileData();
			map.readFromNBT(nbt.getCompoundTag(blockkey));
			blockmaps.put(blockkey, map);
			if (map.count < count) count = map.count;
		}
	}

	private void outputFiles() {
		outputDebug(solid.data, solid.count, "logs/profiling/solid2.txt");
		if (blockmaps != null) {
			for (Map.Entry<String, ChunkProfileData> entry : blockmaps.entrySet()) {
				ChunkProfileData map = entry.getValue();
				String blockkey = entry.getKey();
				outputDebug(map.data, map.count, "logs/profiling/" + blockkey + ".txt");
			}
		}
	}

	private static void outputDebug(int[] solidmap, int chunkcount, String filename) {
		File file = new File(Minecraft.getMinecraft().mcDataDir, filename);
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdir();
		}
		FileOutputStream fos;
		try {
			String NEW_LINE = System.getProperty("line.separator");
			fos = new FileOutputStream(file);
			BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			buffer.write(chunkcount + NEW_LINE);
			int layers = solidmap.length / 256;
			for (int y = 0; y < layers; ++y) {
				for (int z = 0; z < 16; ++z) {
					String line = "";
					for (int x = 0; x < 16; ++x) {
						int coords = y << 8 | z << 4 | x;
						if (line.length() > 0) {
							line += "\t";
						}
						line = line + solidmap[coords];
					}
					buffer.write(line + NEW_LINE);
				}
				buffer.write(NEW_LINE);
			}
			buffer.write("FIN");
			buffer.write(NEW_LINE);
			buffer.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getCount() {
		return count;
	}

	public void clear() {
		try {
			semaphore.acquire();
			count = 0;
			solid = new ChunkProfileData();
			blockmaps = new HashMap<String, ChunkProfileData>();
			for (String blockkey : InstabilityBlockManager.getWatchedBlocks()) {
				blockmaps.put(blockkey, new ChunkProfileData());
			}
			this.markDirty();
			semaphore.release();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to aquire semaphore to profile chunk!");
		}
	}
}
