package com.xcompwiz.mystcraft.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMeteor extends ModelBase {
	ModelRenderer[]	parts	= new ModelRenderer[15];

	public ModelMeteor() {
		int step = 1;
		for (int i = 0; i < this.parts.length; ++i) {
			int scale = (int) (this.parts.length * step - Math.floor(Math.abs(this.parts.length / 2 - i)));
			this.parts[i] = new ModelRenderer(this, 0, 0);
			this.parts[i].addBox(-scale / 2, -this.parts.length / 2 + i * step, -scale / 2, scale, step, scale);
		}
	}

	/**
	 * Sets the models various rotation angles.
	 */
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6) {
		// for (int i = 0; i < this.parts.length; ++i) {
		// this.parts[i].rotateAngleX = 0.2F * MathHelper.sin(par3 * 0.3F + (float)i) + 0.4F;
		// }
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
		this.setRotationAngles(par2, par3, par4, par5, par6, par7);
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, 0.0F, 0.0F);
		for (int i = 0; i < this.parts.length; ++i) {
			this.parts[i].render(par7);
		}

		GL11.glPopMatrix();
	}
}
