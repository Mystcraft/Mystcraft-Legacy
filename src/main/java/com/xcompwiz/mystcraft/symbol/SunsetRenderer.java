package com.xcompwiz.mystcraft.symbol;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;

import net.minecraft.client.Minecraft;
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
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder vb = tes.getBuffer();
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

			GlStateManager.disableTexture2D();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.rotate(MathHelper.sin(celestial_radians) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-angle, 0, 0, 1);

			vb.begin(6, DefaultVertexFormats.POSITION_COLOR);
			vb.pos(0.0D, 100.0D, 0.0D).color(horizonRed, horizonGreen, horizonBlue, horizoncolors[3]).endVertex();

			for (int l = 0; l <= 16; ++l) {
				float f21 = (float)l * ((float)Math.PI * 2F) / 16.0F;
				float f12 = MathHelper.sin(f21);
				float f13 = MathHelper.cos(f21);
				vb.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * horizoncolors[3])).color(horizonRed, horizonGreen, horizonBlue, 0.0F).endVertex();
			}

			tes.draw();
			GlStateManager.popMatrix();
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
	}
}
