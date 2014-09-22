package com.xcompwiz.mystcraft.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.xcompwiz.mystcraft.core.DebugDataTracker;
import com.xcompwiz.mystcraft.core.DebugDataTracker.Callback;

public class ChunkProfiler extends WorldSavedData {
	public static final String					ID				= "MystChunkProfile";
	private static final int					MAP_LENGTH		= 256 * 256;

	private static final Collection<Block>		watchedblocks	= new HashSet<Block>();
	private static final Map<Block, Float>		factor1s		= new HashMap<Block, Float>();
	private static final Map<Block, Float>		factor2s		= new HashMap<Block, Float>();
	private static final Map<Block, Integer>	freevals		= new HashMap<Block, Integer>();

	//XXX: Move out of here.
	//TODO: (API) Make accessible to API
	//TODO: (Instability) Make metadata aware
	public static void setInstabilityFactors(Block block, float factor1, float factor2, int free) {
		watchedblocks.add(block);
		factor1s.put(block, factor1);
		factor2s.put(block, factor2);
		freevals.put(block, free);
	}

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

	private String							debugname;

	private ChunkProfileData				solid_prepop	= new ChunkProfileData();
	private ChunkProfileData				solid			= new ChunkProfileData();
	private Map<Block, ChunkProfileData>	blockmaps;
	private int								count			= 0;
	private static boolean					outputfiles		= false;

	static {
		DebugDataTracker.register("profiler.output", new Callback() {

			@Override
			public void setState(boolean state) {
				outputfiles = state;
			}
		});
	}

	public ChunkProfiler(String id) {
		super(id);
		blockmaps = new HashMap<Block, ChunkProfileData>();
		for (Block block : watchedblocks) {
			blockmaps.put(block, new ChunkProfileData());
		}
	}

	public void baseChunk(Chunk chunk, int chunkX, int chunkZ) {
		profileChunk(chunk, solid_prepop, null);
		this.markDirty();
	}

	public int calculateInstability() {
		if (outputfiles) outputFiles();
		float instability = 0;
		int layers = solid.data.length / 256;
		HashMap<Block, Float> split = new HashMap<Block, Float>();
		//For all cells, calculate instability
		for (int y = 0; y < layers; ++y) {
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					int coords = y << 8 | z << 4 | x;
					float availability = 1 - (solid.data[coords] / (float) solid.count);
					for (Block block : watchedblocks) {
						ChunkProfileData map = blockmaps.get(block);
						if (map.count < 100) continue;
						float factor1 = factor1s.get(block);
						float factor2 = factor2s.get(block);
						float val = map.data[coords] / (float) map.count;
						val = val * availability * factor1 + val * factor2;
						if (!split.containsKey(block)) {
							split.put(block, 0.0F);
						}
						split.put(block, split.get(block) + val);
					}
				}
			}
		}
		for (Entry<Block, Float> entry : split.entrySet()) {
			DebugDataTracker.set((debugname == null ? "Unnamed" : debugname) + ".instability." + entry.getKey().getUnlocalizedName(), "" + entry.getValue());
			float val = entry.getValue();
			if (val > 0) {
				val = Math.max(0, val - freevals.get(entry.getKey()));
			}
			instability += val;
		}
		return Math.round(instability);
	}

	public void profile(Chunk chunk, int chunkX, int chunkZ) {
		profileChunk(chunk, solid, blockmaps);
		++count;
		this.markDirty();
	}

	private static void profileChunk(Chunk chunk, ChunkProfileData soliddata, Map<Block, ChunkProfileData> maps) {
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
					//int metadata = storageArrays[storagei].getExtBlockMetadata(x, y & 15, z);

					int accessibility = (block != Blocks.air ? 2 : 0);
					if (maps != null) {
						for (Map.Entry<Block, ChunkProfileData> entry : maps.entrySet()) {
							Block matchblock = entry.getKey();
							ChunkProfileData map = entry.getValue();
							if (block == matchblock) {
								++map.data[coords];
								accessibility = 1;
							}
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
			for (Map.Entry<Block, ChunkProfileData> entry : maps.entrySet()) {
				ChunkProfileData map = entry.getValue();
				++map.count;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("prepop", solid_prepop.writeToNBT(new NBTTagCompound()));
		nbt.setTag("solid", solid.writeToNBT(new NBTTagCompound()));
		if (blockmaps == null) return;
		for (Map.Entry<Block, ChunkProfileData> entry : blockmaps.entrySet()) {
			Block block = entry.getKey();
			ChunkProfileData map = entry.getValue();
			nbt.setTag(block.getUnlocalizedName(), map.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		solid_prepop.readFromNBT(nbt.getCompoundTag("prepop"));
		solid.readFromNBT(nbt.getCompoundTag("solid"));
		count = solid.count;

		if (blockmaps == null) return;
		for (Block block : blockmaps.keySet()) {
			ChunkProfileData map = new ChunkProfileData();
			map.readFromNBT(nbt.getCompoundTag(block.getUnlocalizedName()));
			blockmaps.put(block, map);
			if (map.count < count) count = map.count;
		}
	}

	private void outputFiles() {
		outputDebug(solid_prepop.data, solid_prepop.count, "logs/profiling/solid1.txt");
		outputDebug(solid.data, solid.count, "logs/profiling/solid2.txt");
		if (blockmaps != null) {
			for (Map.Entry<Block, ChunkProfileData> entry : blockmaps.entrySet()) {
				ChunkProfileData map = entry.getValue();
				Block block = entry.getKey();
				outputDebug(map.data, map.count, "logs/profiling/" + block.getUnlocalizedName() + ".txt");
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

	public void setDebugName(String name) {
		debugname = name;
	}
}
