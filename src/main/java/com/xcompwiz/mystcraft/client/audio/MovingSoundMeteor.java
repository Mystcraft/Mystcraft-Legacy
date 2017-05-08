package com.xcompwiz.mystcraft.client.audio;

import com.xcompwiz.mystcraft.entity.EntityMeteor;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MovingSoundMeteor extends MovingSound {
	private final EntityMeteor	entity;

	public MovingSoundMeteor(EntityMeteor entity) {
		super(new ResourceLocation("mystcraft:entity.meteor.roar"));
		this.field_147666_i = ISound.AttenuationType.NONE;
		this.entity = entity;
		this.repeat = true;
		this.volume = 1000.0F;
		this.field_147665_h = 0;
		this.field_147663_c = 2.0F;
	}

	@Override
	public void update() {
		if (this.entity.isDead) {
			this.donePlaying = true;
		} else {
			this.xPosF = (float) this.entity.posX;
			this.yPosF = (float) this.entity.posY;
			this.zPosF = (float) this.entity.posZ;
		}
	}
}
