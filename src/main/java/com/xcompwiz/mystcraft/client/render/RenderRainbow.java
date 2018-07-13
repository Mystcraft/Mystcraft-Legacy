package com.xcompwiz.mystcraft.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

// A huge thank you to Briman0094 for writing this code!
// Minor edits by XCompWiz
// Moar edits by HellFirePvP

public class RenderRainbow {
	/**
	 * hue from 0f to 360f
	 * @param hue
	 * @param sat
	 * @param val
	 * @return array [R, G, B] each over [0, 256)
	 */
	public static int[] hsvToRGB(float hue, float sat, float val) {
		float progress = hue / 360f;
		int r, g, b;
		r = 0;
		g = 0;
		b = 0;
		if (progress >= 0 && progress < 1f / 6f) {
			float subProg = progress / (1f / 6f);
			r = 255;
			g = (int) (subProg * 255f);
			b = 0;
		} else if (progress >= 1f / 6f && progress < 2f / 6f) {
			float subProg = (progress - (1f / 6f)) / (1f / 6f);
			r = 255 - (int) (subProg * 255f);
			g = 255;
			b = 0;
		} else if (progress >= 2f / 6f && progress < 3f / 6f) {
			float subProg = (progress - (2f / 6f)) / (1f / 6f);
			r = 0;
			g = 255;
			b = (int) (subProg * 255f);
		} else if (progress >= 3f / 6f && progress < 4f / 6f) {
			float subProg = (progress - (3f / 6f)) / (1f / 6f);
			r = 0;
			g = 255 - (int) (subProg * 255f);
			b = 255;
		} else if (progress >= 4f / 6f && progress < 5f / 6f) {
			float subProg = (progress - (4f / 6f)) / (1f / 6f);
			r = (int) (subProg * 255f);
			g = 0;
			b = 255;
		} else if (progress >= 5f / 6f && progress <= 1) {
			float subProg = (progress - (5f / 6f)) / (1f / 6f);
			r = 255;
			g = 0;
			b = 255 - (int) (subProg * 255f);
		}
		int finalMin = (int) (val - sat);
		int finalMax = (int) val;
		if (finalMax < finalMin)
			finalMax = finalMin;
		if (finalMin < 0)
			finalMin = 0;
		if (finalMax > 255)
			finalMax = 255;
		r = normalize(r, 0, 255, finalMin, finalMax);
		g = normalize(g, 0, 255, finalMin, finalMax);
		b = normalize(b, 0, 255, finalMin, finalMax);
		return new int[] { r, g, b };
	}

	public static int normalize(int value, int initMin, int initMax, int finalMin, int finalMax) {
		float val = ((float) (value - initMin)) / ((float) (initMax - initMin));
		val *= (finalMax - finalMin);
		val += finalMin;
		if (val < finalMin)
			val = finalMin;
		if (val > finalMax)
			val = finalMax;
		return (int) val;
	}

	public static float quadratic(float x) {
		return (float) Math.sqrt(1 - (x * x));
	}

	public static void renderRainbow(float skyAngle, float width) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(skyAngle, 0, 1, 0);
		GlStateManager.rotate(45, 1, 0, 0);
		GlStateManager.pushMatrix();
		doRenderRainbow(width);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180, 1, 0, 0);
		doRenderRainbow(width);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	public static void doRenderRainbow(float width) {
		/*
		 * float brightness; float angleDegrees = (float) (celestialAngle * (180.0 / Math.PI)); if (angleDegrees >= 270 && angleDegrees < 360) { brightness =
		 * ((angleDegrees - 270f) / 90f); } else if (angleDegrees >= 0 && angleDegrees <= 90) { brightness = 1f - (angleDegrees / 90f); } else { brightness =
		 * 0f; }
		 */
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder vb = tes.getBuffer();
		float numStrips = 50;
		float stripWidth = width / numStrips;

		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		for (float line = 0; line < numStrips; ++line) {
			float hueProg = (line / numStrips);
			float hue = (hueProg * 240f);
			int color[] = hsvToRGB(hue, 255, 200);
			float resolution = 100f;
			float patchHeight = 0.5F;
			float rainbowWidth = stripWidth * numStrips;
			float yHeight;
			float yHeight2;
			for (float zPos = -resolution; zPos <= resolution; zPos += patchHeight) {
				yHeight = quadratic(zPos / resolution) * resolution;
				yHeight2 = quadratic((zPos - patchHeight) / resolution) * resolution;
				vb.pos((line * stripWidth) - stripWidth - (rainbowWidth / 2f), yHeight2, zPos).color(color[0] / 255f, color[1] / 255f, color[2] / 255f, 0.2f).endVertex();
				vb.pos((line * stripWidth) + stripWidth - (rainbowWidth / 2f), yHeight2, zPos).color(color[0] / 255f, color[1] / 255f, color[2] / 255f, 0.2f).endVertex();
				vb.pos((line * stripWidth) + stripWidth - (rainbowWidth / 2f), yHeight, zPos + patchHeight).color(color[0] / 255f, color[1] / 255f, color[2] / 255f, 0.2f).endVertex();
				vb.pos((line * stripWidth) - stripWidth - (rainbowWidth / 2f), yHeight, zPos + patchHeight).color(color[0] / 255f, color[1] / 255f, color[2] / 255f, 0.2f).endVertex();
			}
		}
		tes.draw();
		GlStateManager.disableColorMaterial();
		GlStateManager.popMatrix();
	}
}
