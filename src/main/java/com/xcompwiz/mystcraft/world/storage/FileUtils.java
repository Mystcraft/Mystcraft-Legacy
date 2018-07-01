package com.xcompwiz.mystcraft.world.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraftforge.common.DimensionManager;

public class FileUtils {

	public static Set<Integer> getExistingAgeList(File dataDir) {
		HashSet<Integer> list = new HashSet<Integer>();
		File[] var2 = dataDir.listFiles();
		int var4 = var2.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			File var6 = var2[var5];

			if (var6.getName().startsWith("agedata_") && var6.getName().endsWith(".dat")) {
				try {
					String dimStr = var6.getName();
					dimStr = dimStr.substring(8, dimStr.length() - 4);
					list.add(Integer.parseInt(dimStr));
				} catch (Exception e) {
					LoggerUtils.warn("Error parsing dim id from " + var6.getName());
				}
			}
		}

		return list;
	}

	/**
	 * I never thought I would write this function
	 * @param dimid
	 * @return
	 */
	public static boolean deleteAgeChunkData(Integer dimid) {
		File rootdir = DimensionManager.getCurrentSaveRootDirectory();
		File dimensionfolder = new File(rootdir, WorldProviderMyst.getSaveFolderName(dimid));
		if (!dimensionfolder.exists())
			return true;
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(dimensionfolder);
		} catch (IOException e) {
			LoggerUtils.error("Failed to delete dimension chunk data for dimension " + dimid);
			return false;
		}
		if (!dimensionfolder.exists())
			return true;
		return false;
	}

}
