package com.xcompwiz.mystcraft.effects;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.entity.EntityLightningBoltAdv;
import com.xcompwiz.mystcraft.network.packet.MPacketSpawnLightningBolt;

public class EffectLightning implements IEnvironmentalEffect {

	private int				updateLCG	= (new Random()).nextInt();
	private ColorGradient	gradient;

	public EffectLightning() {}

	public EffectLightning(ColorGradient gradient) {
		this.gradient = gradient;
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		if (worldObj.isRemote) return;
		int chunkX = chunk.xPosition * 16;
		int chunkZ = chunk.zPosition * 16;
		int coords;
		int x;
		int z;
		int y;

		EntityLightningBoltAdv bolt = null;
		if (worldObj.isRaining() && worldObj.isThundering() && worldObj.rand.nextInt(5000) == 0) {
			updateLCG = updateLCG * 3 + 1013904223;
			coords = updateLCG >> 2;
			x = chunkX + (coords & 15);
			z = chunkZ + (coords >> 8 & 15);
			y = worldObj.getPrecipitationHeight(x, z);

			bolt = new EntityLightningBoltAdv(worldObj, x, y, z);
		} else if (worldObj.rand.nextInt(100000) == 0) {
			updateLCG = updateLCG * 3 + 1013904223;
			coords = updateLCG >> 2;
			x = chunkX + (coords & 15);
			z = chunkZ + (coords >> 8 & 15);
			y = worldObj.getPrecipitationHeight(x, z);

			bolt = new EntityLightningBoltAdv(worldObj, x, y, z);
		}
		if (bolt != null) {
			if (gradient != null && gradient.getColorCount() > 0) bolt.setColor(gradient.getColor(worldObj.getTotalWorldTime() / 12000F));
			worldObj.weatherEffects.add(bolt);
			if (worldObj instanceof WorldServer) {
				((WorldServer) worldObj).func_73046_m().getConfigurationManager().sendToAllNear(bolt.posX, bolt.posY, bolt.posZ, 512.0D, worldObj.provider.dimensionId, MPacketSpawnLightningBolt.createPacket(bolt));
			}
		}
	}

}
