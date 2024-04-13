package com.xcompwiz.mystcraft.world.profiling;

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

import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugValueCallback;
import com.xcompwiz.mystcraft.debug.DebugUtils;
import com.xcompwiz.mystcraft.debug.DefaultValueCallback;
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
	private ChunkProfileData				solidmap;
	private Map<String, ChunkProfileData>	blockmaps;
	private static boolean					outputfiles		= false;

	private HashMap<String, Float>			lastsplitcalc;

	private float							accessibility[]	= null;
	private float							averages[]		= null;
	private float							filtered[]		= null;
	private boolean							nonzero[]		= null;
	private boolean							solid[]			= null;
	private float							rounded[]		= null;
	private float							maximum			= 0;
	private float							minimum			= 1;
	private float							groundsum		= 0;
	private int								groundcount		= 0;

	private Semaphore						semaphore		= new Semaphore(1, true);

	static {
		DebugUtils.register("global.profiler.file_output", new DebugValueCallback() {

			@Override
			public void set(ICommandSender agent, String state) {
				try {
					if (state.compareToIgnoreCase("0") == 0 || state.compareToIgnoreCase("false") == 0) {
						outputfiles = false;
					} else {
						outputfiles = true;
					}
				} catch (Exception e) {
				}
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
		solidmap = new ChunkProfileData();
		blockmaps = new HashMap<String, ChunkProfileData>();
		for (String blockkey : InstabilityBlockManager.getWatchedBlocks()) {
			blockmaps.put(blockkey, new ChunkProfileData());
		}
	}

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

	//TODO: Have this recalc in a separate thread and submit the results
	public HashMap<String, Float> calculateSplitInstability() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to acquire semaphore to profile chunk (interrupted)!");
		}
		int layers = solidmap.data.length / 256;
		maximum = 0;
		minimum = 1;
		groundsum = 0;
		groundcount = 0;
		if (accessibility == null || accessibility.length < layers) accessibility = new float[layers];
		if (averages == null || averages.length < layers) averages = new float[layers];
		if (filtered == null || filtered.length < layers) filtered = new float[layers];
		if (rounded == null || rounded.length < layers) rounded = new float[layers];
		if (nonzero == null || nonzero.length < layers) nonzero = new boolean[layers];
		if (solid == null || solid.length < layers) solid = new boolean[layers];
		for (int y = 0; y < layers; ++y) {
			averages[y] = 0;
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					int coords = y << 8 | z << 4 | x;
					averages[y] += (solidmap.data[coords] / (float) solidmap.count);
				}
			}
			averages[y] /= 256;
			if (minimum > averages[y]) minimum = averages[y];
			if (maximum < averages[y]) maximum = averages[y];
		}
		for (int y = 0; y < layers; ++y) {
			filtered[y] = averages[y] - minimum;
			if (filtered[y] < 0) filtered[y] = 0;
			nonzero[y] = filtered[y] > 0;
			rounded[y] = Math.round(100 * filtered[y]) / 100F;
			if (rounded[y] > 0) {
				groundsum += rounded[y];
				++groundcount;
			}
		}
		float ground = groundsum / groundcount;
		for (int y = 0; y < layers; ++y) {
			solid[y] = rounded[y] > ground;
			accessibility[y] = (solid[y] ? 1 - rounded[y] : 1);
		}
		HashMap<String, Float> split = new HashMap<String, Float>();
		//For all cells, calculate instability
		for (int y = 0; y < layers; ++y) {
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					int coords = y << 8 | z << 4 | x;
					float availability = accessibility[y];
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
		lastsplitcalc = split;
		semaphore.release();
		return split;
	}

	public void profile(Chunk chunk) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to acquire semaphore to profile chunk (interrupted)!");
		}
		profileChunk(chunk, solidmap, blockmaps);
		++count;
		this.markDirty();
		semaphore.release();
	}

	private void profileChunk(Chunk chunk, ChunkProfileData soliddata, Map<String, ChunkProfileData> maps) {
		int chunkX = chunk.xPosition << 4;
		int chunkZ = chunk.zPosition << 4;
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
					if (block.getBlocksMovement(chunk.worldObj, chunkX + x, y, chunkZ + z)) {
						accessibility = 1;
					}
					//Checks if isAir
					if (block.isAir(chunk.worldObj, chunkX + x, y, chunkZ + z)) {
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
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		ChunkProfilerManager.ensureSafeSave();
		nbt.setTag("solid", solidmap.writeToNBT(new NBTTagCompound()));
		if (blockmaps == null) return;
		for (Map.Entry<String, ChunkProfileData> entry : blockmaps.entrySet()) {
			String blockkey = entry.getKey();
			ChunkProfileData map = entry.getValue();
			nbt.setTag(blockkey, map.writeToNBT(new NBTTagCompound()));
		}
		ChunkProfilerManager.releaseSaveSafe();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		solidmap.readFromNBT(nbt.getCompoundTag("solid"));
		count = solidmap.count;
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
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to acquire semaphore to profile chunk (interrupted)!");
		}
		outputDebug(solidmap.data, solidmap.count, "logs/profiling/solid2.txt");
		if (blockmaps != null) {
			for (Map.Entry<String, ChunkProfileData> entry : blockmaps.entrySet()) {
				ChunkProfileData map = entry.getValue();
				String blockkey = entry.getKey();
				outputDebug(map.data, map.count, "logs/profiling/" + blockkey + ".txt");
			}
		}
		semaphore.release();
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
			solidmap = new ChunkProfileData();
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

	public void registerDebugInfo(DebugNode node) {
		for (final String blockkey : InstabilityBlockManager.getWatchedBlocks()) {
			node.addChild(blockkey.replaceAll("\\.", "_"), new DefaultValueCallback() {
				private ChunkProfiler	profiler;
				private String			blockkey;

				@Override
				public String get(ICommandSender agent) {
					HashMap<String, Float> split = profiler.lastsplitcalc;
					if (split == null) return "N/A";
					Float val = split.get(blockkey);
					if (val == null) return "None";
					return "" + val;
				}

				private DefaultValueCallback init(ChunkProfiler profiler, String blockkey) {
					this.profiler = profiler;
					this.blockkey = blockkey;
					return this;
				}
			}.init(this, blockkey));
		}
	}
}
