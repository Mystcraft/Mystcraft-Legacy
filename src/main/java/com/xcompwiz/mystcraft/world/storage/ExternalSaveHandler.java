package com.xcompwiz.mystcraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class ExternalSaveHandler implements ISaveHandler {
	/** The directory in which to save world data. */
	private final File			mapDataDir;
	/** The time in milliseconds when this field was initialized. Stored in the session lock file. */
	private final long			initializationTime	= MinecraftServer.getCurrentTimeMillis();

	public ExternalSaveHandler(File directory, String savename) {
		this.mapDataDir = new File(directory, savename);
		this.mapDataDir.mkdirs();
		this.setSessionLock();
	}

	/**
	 * Creates a session lock file for this process
	 */
	private void setSessionLock() {
		try {
			File file1 = new File(this.mapDataDir, "session.lock");
			DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));

			try {
				dataoutputstream.writeLong(this.initializationTime);
			} finally {
				dataoutputstream.close();
			}
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
	}

	/**
	 * Gets the File object corresponding to the base directory of this world.
	 */
	@Override
	public File getWorldDirectory() {
		return this.mapDataDir;
	}

	/**
	 * Checks the session lock to prevent save collisions
	 */
	@Override
	public void checkSessionLock() throws MinecraftException {
		try {
			File file1 = new File(this.mapDataDir, "session.lock");
			DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));

			try {
				if (datainputstream.readLong() != this.initializationTime) { throw new MinecraftException("The save is being accessed from another location, aborting"); }
			} finally {
				datainputstream.close();
			}
		} catch (IOException ioexception) {
			throw new MinecraftException("Failed to check session lock, aborting");
		}
	}

	/**
	 * Returns the chunk loader with the provided world provider
	 */
	@Override
	public IChunkLoader getChunkLoader(WorldProvider p_75763_1_) {
		throw new RuntimeException("Chunk storage is not supported on this save handler");
	}

	/**
	 * Loads and returns the world info
	 */
	@Override
	public WorldInfo loadWorldInfo() {
		throw new RuntimeException("World info is not supported on this save handler");
	}

	/**
	 * Saves the given World Info with the given NBTTagCompound as the Player.
	 */
	@Override
	public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, NBTTagCompound p_75755_2_) {}

	/**
	 * Saves the passed in world info.
	 */
	@Override
	public void saveWorldInfo(WorldInfo p_75761_1_) {}

	/**
	 * Writes the player data to disk from the specified PlayerEntityMP.
	 */
	public void writePlayerData(EntityPlayer p_75753_1_) {}

	/**
	 * Reads the player data from disk into the specified PlayerEntityMP.
	 */
	public NBTTagCompound readPlayerData(EntityPlayer p_75752_1_) {
		throw new RuntimeException("Player data is not supported on this save handler");
	}

	/**
	 * returns null if no saveHandler is relevent (eg. SMP)
	 */
	@Override
	public IPlayerFileData getPlayerNBTManager() {
		return null;
	}

	/**
	 * Returns an array of usernames for which player.dat exists for.
	 */
	public String[] getAvailablePlayerDat() {
		throw new RuntimeException("Player data is not supported on this save handler");
	}

	/**
	 * Called to flush all changes to disk, waiting for them to complete.
	 */
	@Override
	public void flush() {}

	/**
	 * Gets the file location of the given map
	 */
	@Override
	public File getMapFileFromName(String dataname) {
		return new File(this.mapDataDir, dataname + ".dat");
	}

	@Override
	public TemplateManager getStructureTemplateManager() {
		return null;
	}
}
