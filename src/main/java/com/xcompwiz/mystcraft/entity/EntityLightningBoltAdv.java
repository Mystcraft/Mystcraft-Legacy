package com.xcompwiz.mystcraft.entity;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.util.Color;

public class EntityLightningBoltAdv extends EntityLightningBolt {
	private Color	color;

	public EntityLightningBoltAdv(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
		this.color = new Color(0.45F, 0.45F, 0.5F);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}
}
