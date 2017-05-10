package com.xcompwiz.mystcraft.entity;

import com.xcompwiz.mystcraft.api.util.Color;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

public class EntityLightningBoltAdv extends EntityLightningBolt {

	private Color color;

	public EntityLightningBoltAdv(World world, double x, double y, double z, boolean effectOnly) {
		super(world, x, y, z, effectOnly);
		this.color = new Color(0.45F, 0.45F, 0.5F);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}
}
