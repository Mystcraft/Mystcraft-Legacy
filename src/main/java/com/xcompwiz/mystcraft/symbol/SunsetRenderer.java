package com.xcompwiz.mystcraft.symbol;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SunsetRenderer {
	private float colorsSunriseSunset[] = new float[4];
	protected AgeDirector controller;
	protected ColorGradient gradient;

	public SunsetRenderer(AgeDirector controller, ColorGradient gradient) {
		this.controller = controller;
		this.gradient = gradient;
	}

	@SideOnly(Side.CLIENT)
	protected float[] getSunriseSunsetColors(float celestial_angle, float partialtick) {
		float f2 = 0.4F;
		float f3 = MathHelper.cos(celestial_angle * 3.141593F * 2.0F) - 0.0F;
		float f4 = -0F;
		if (f3 >= f4 - f2 && f3 <= f4 + f2) {
			float f5 = ((f3 - f4) / f2) * 0.5F + 0.5F;
			float f6 = 1.0F - (1.0F - MathHelper.sin(f5 * 3.141593F)) * 0.99F;
			f6 *= f6;
			ColorGradient gradient = this.gradient;
			Color color = null;
			if (gradient == null) {
				gradient = controller.getSunriseSunsetColor();
			}
			if (gradient != null && gradient.getColorCount() > 0) {
				color = gradient.getColor(controller.getTime() / 12000F);
			}
			if (color == null) {
				colorsSunriseSunset[0] = f5 * 0.3F + 0.7F;
				colorsSunriseSunset[1] = f5 * f5 * 0.7F + 0.2F;
				colorsSunriseSunset[2] = f5 * f5 * 0.0F + 0.2F;
			} else {
				colorsSunriseSunset[0] = color.r;
				colorsSunriseSunset[1] = color.g;
				colorsSunriseSunset[2] = color.b;
			}
			colorsSunriseSunset[3] = f6;
			return colorsSunriseSunset;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	protected void renderHorizon(TextureManager eng, World worldObj, float celestial_period, float angle, float partial, float alpha) {
		float celestial_radians = celestial_period * (float) Math.PI * 2.0F;
		Tessellator tessellator = Tessellator.instance;
		RenderHelper.disableStandardItemLighting();
		float[] horizoncolors = getSunriseSunsetColors(celestial_period, partial);
		if (horizoncolors != null) {
			float horizonRed = horizoncolors[0];
			float horizonGreen = horizoncolors[1];
			float horizonBlue = horizoncolors[2];
			horizoncolors[3] *= alpha;

			if (Minecraft.getMinecraft().gameSettings.anaglyph) {
				float red = (horizonRed * 30.0F + horizonGreen * 59.0F + horizonBlue * 11.0F) / 100.0F;
				float green = (horizonRed * 30.0F + horizonGreen * 70.0F) / 100.0F;
				float blue = (horizonRed * 30.0F + horizonBlue * 70.0F) / 100.0F;
				horizonRed = red;
				horizonGreen = green;
				horizonBlue = blue;
			}

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glPushMatrix();
			GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(MathHelper.sin(celestial_radians) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-angle, 0.0F, 0.0F, 1.0F);
			tessellator.startDrawing(6);
			tessellator.setColorRGBA_F(horizonRed, horizonGreen, horizonBlue, horizoncolors[3]);
			tessellator.addVertex(0.0D, 100.0D, 0.0D);
			tessellator.setColorRGBA_F(horizoncolors[0], horizoncolors[1], horizoncolors[2], 0.0F);

			byte var26 = 16;
			for (int var27 = 0; var27 <= var26; ++var27) {
				float var13 = var27 * (float) Math.PI * 2.0F / var26;
				float var14 = MathHelper.sin(var13);
				float var15 = MathHelper.cos(var13);
				tessellator.addVertex((var14 * 120.0F), (var15 * 120.0F), (-var15 * 40.0F * horizoncolors[3]));
			}
			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glShadeModel(GL11.GL_FLAT);
		}
	}
}
