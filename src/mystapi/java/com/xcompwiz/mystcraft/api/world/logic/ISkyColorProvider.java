package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.entity.Entity;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.util.Color;

public interface ISkyColorProvider {
	public abstract Color getSkyColor(Entity entity, BiomeGenBase biome, float time, float celestial_angle);
}
