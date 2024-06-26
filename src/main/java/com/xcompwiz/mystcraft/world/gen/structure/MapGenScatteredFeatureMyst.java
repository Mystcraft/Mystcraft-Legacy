package com.xcompwiz.mystcraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

// TODO: (Structures) Revise structure gen
public class MapGenScatteredFeatureMyst extends MapGenStructure {

	/** contains possible spawns for scattered features */
	private List<SpawnListEntry> scatteredFeatureSpawnList;

	/** the maximum distance between scattered features */
	private int maxDistanceBetweenScatteredFeatures;

	/** the minimum distance between scattered features */
	private int minDistanceBetweenScatteredFeatures;

	public static String stringId = "MystLibrary";

	public MapGenScatteredFeatureMyst() {
		this.scatteredFeatureSpawnList = new ArrayList<SpawnListEntry>();
		this.maxDistanceBetweenScatteredFeatures = 32;
		this.minDistanceBetweenScatteredFeatures = 8;
		this.scatteredFeatureSpawnList.add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
	}

	public MapGenScatteredFeatureMyst(Map<String, String> par1Map) {
		this();
		Iterator<Entry<String, String>> var2 = par1Map.entrySet().iterator();

		while (var2.hasNext()) {
			Entry<String, String> var3 = var2.next();

			if (var3.getKey().equals("distance")) {
				this.maxDistanceBetweenScatteredFeatures = MathHelper.getInt(var3.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1);
			}
		}
	}

	@Override
	public String getStructureName() {
		return stringId;
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
		return null;
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		int var3 = chunkX;
		int var4 = chunkZ;

		if (chunkX < 0) {
			chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
		}

		if (chunkZ < 0) {
			chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
		}

		int var5 = chunkX / this.maxDistanceBetweenScatteredFeatures;
		int var6 = chunkZ / this.maxDistanceBetweenScatteredFeatures;
		Random var7 = this.world.setRandomSeed(var5, var6, 14357617);
		var5 *= this.maxDistanceBetweenScatteredFeatures;
		var6 *= this.maxDistanceBetweenScatteredFeatures;
		var5 += var7.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
		var6 += var7.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);

		if (var3 == var5 && var4 == var6) {
//			Biome var8 = this.worldObj.getBiomeProvider().getBiomeGenAt(var3 * 16 + 8, var4 * 16 + 8);
//			Iterator var9 = biomelist.iterator();
//
//			while (var9.hasNext()) {
//				Biome var10 = (Biome) var9.next();
//
//				if (var8 == var10) { return true; }
//			}
			return true;
		}

		return false;
	}

	@Override
	protected StructureStart getStructureStart(int par1, int par2) {
		return new StructureScatteredFeatureStartMyst(this.world, this.rand, par1, par2);
	}

	/**
	 * returns possible spawns for scattered features
	 */
	public List<SpawnListEntry> getScatteredFeatureSpawnList() {
		return this.scatteredFeatureSpawnList;
	}
}
