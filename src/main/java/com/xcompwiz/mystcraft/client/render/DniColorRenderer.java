package com.xcompwiz.mystcraft.client.render;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.util.MathBezier;
import com.xcompwiz.util.Vector;
import com.xcompwiz.util.VectorPool;

/**
 * Renders the color "eyes" that the D'ni used to represent colors Created by heldplayer for use in his Mystcraft-NEI plugin and he kindly gave me permission to
 * include it in Mystcraft directly :)
 * @author heldplayer
 */
public class DniColorRenderer {

	static Vector[] points = new Vector[4];

	public static void render(Color color, Vector center, double radius) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(3.0F);

		GL11.glColor3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);

		float[] HSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		int H = (int) (HSB[0] * 360.0F);

		double eyelidRadius = radius;
		float eyeRadius = (1.0F + (1.0F - HSB[1]) * 3.0F) * (float) radius / 20.0F;

		boolean outerEyelid = false;
		boolean innerEyelid = false;
		boolean eye = false;
		if (HSB[1] > 0.3F && HSB[2] > 0.5F) {
			if (H <= 28 || H > 274) {
				int temp = (H + 85) % 360;
				eyelidRadius = MathBezier.abs(MathBezier.cos((temp / 114.0F)) * radius);
				outerEyelid = true;
				innerEyelid = true;
			} else if (H <= 114) {
				int temp = H - 30;
				outerEyelid = true;
				eyelidRadius = MathBezier.abs(MathBezier.sin((temp / 86.0F)) * radius);
				eye = true;
			} else if (H <= 274) {
				int temp = H - 114;
				innerEyelid = true;
				eyelidRadius = MathBezier.abs(MathBezier.cos((temp / 160.0F)) * radius);
				eye = true;
			}
		} else {
			float mod = MathBezier.min(HSB[1], HSB[2]) / 0.3F;
			eyeRadius *= HSB[2] / 0.3F;
			if (H <= 28 || H > 274) {
				int temp = (H + 85) % 360;
				if (mod < 0.3F) {
					eyelidRadius = MathBezier.abs(MathBezier.max(MathBezier.cos((temp / 114.0F)), 1.0F - mod / 0.3F) * radius);
					eye = true;
				} else {
					eyelidRadius = MathBezier.abs(MathBezier.min(MathBezier.cos((temp / 114.0F)), (mod - 0.3F) / 0.7F) * radius);
					innerEyelid = true;
				}
				outerEyelid = true;
			} else if (H <= 114) {
				int temp = H - 30;
				outerEyelid = true;
				eyelidRadius = MathBezier.abs(MathBezier.max(MathBezier.cos((temp / 86.0F)), 1.0F - mod) * radius);
				eye = true;
			} else if (H <= 274) {
				int temp = H - 114;
				innerEyelid = true;
				eyelidRadius = MathBezier.abs(MathBezier.max(MathBezier.cos((temp / 160.0F)), 1.0F - mod) * radius);
				eye = true;
			}
		}

		if (eyeRadius > eyelidRadius) {
			eyeRadius = (float) eyelidRadius;
		}
		eyelidRadius *= 4.0D / 3.0D;

		int max = 20;

		Vector top = VectorPool.getFreeVector(center.posX, center.posY - radius, center.posZ);
		Vector bottom = VectorPool.getFreeVector(center.posX, center.posY + radius, center.posZ);
		Vector left = VectorPool.getFreeVector(center.posX - radius, center.posY, center.posZ);
		Vector right = VectorPool.getFreeVector(center.posX + radius, center.posY, center.posZ);

		if (eye) {
			GL11.glBegin(GL11.GL_LINE_LOOP);
			for (int i = 0; i <= max; i++) {
				float pos = (float) i / (float) max * 4.0F;
				GL11.glVertex3d(center.posX + eyeRadius * MathBezier.cos(pos), center.posY + eyeRadius * MathBezier.sin(pos), center.posZ);
			}
			GL11.glEnd();
		}

		if (outerEyelid) {
			Vector vec1 = VectorPool.getFreeVector(center.posX - radius * 0.95D, center.posY - eyelidRadius, center.posZ);
			Vector vec2 = VectorPool.getFreeVector(center.posX + radius * 0.95D, center.posY - eyelidRadius, center.posZ);

			setPoints(points, left, vec1, vec2, right);

			GL11.glBegin(GL11.GL_LINE_STRIP);
			for (int i = 0; i <= max; ++i) {
				double pos = (double) i / (double) max;
				Vector point = MathBezier.bezier(points, pos);
				GL11.glVertex3d(point.posX, point.posY, point.posZ);
			}
			GL11.glEnd();

			top = MathBezier.bezier(points, 0.5D);

			vec1 = VectorPool.getFreeVector(center.posX - radius * 0.95D, center.posY + eyelidRadius, center.posZ);
			vec2 = VectorPool.getFreeVector(center.posX + radius * 0.95D, center.posY + eyelidRadius, center.posZ);

			setPoints(points, left, vec1, vec2, right);

			GL11.glBegin(GL11.GL_LINE_STRIP);
			for (int i = 0; i <= max; ++i) {
				double pos = (double) i / (double) max;
				Vector point = MathBezier.bezier(points, pos);
				GL11.glVertex3d(point.posX, point.posY, point.posZ);
			}
			GL11.glEnd();

			bottom = MathBezier.bezier(points, 0.5D);
		}

		if (innerEyelid && !outerEyelid) {
			Vector vec1 = VectorPool.getFreeVector(top.posX - eyelidRadius, center.posY - radius * 0.95D, top.posZ);
			Vector vec2 = VectorPool.getFreeVector(bottom.posX - eyelidRadius, center.posY + radius * 0.95D, bottom.posZ);

			setPoints(points, top, vec1, vec2, bottom);

			GL11.glBegin(GL11.GL_LINE_STRIP);
			for (int i = 0; i <= max; ++i) {
				double pos = (double) i / (double) max;
				Vector point = MathBezier.bezier(points, pos);
				GL11.glVertex3d(point.posX, point.posY, point.posZ);
			}
			GL11.glEnd();

			vec1 = VectorPool.getFreeVector(top.posX + eyelidRadius, center.posY - radius * 0.95D, top.posZ);
			vec2 = VectorPool.getFreeVector(bottom.posX + eyelidRadius, center.posY + radius * 0.95D, bottom.posZ);

			setPoints(points, top, vec1, vec2, bottom);

			GL11.glBegin(GL11.GL_LINE_STRIP);
			for (int i = 0; i <= max; ++i) {
				double pos = (double) i / (double) max;
				Vector point = MathBezier.bezier(points, pos);
				GL11.glVertex3d(point.posX, point.posY, point.posZ);
			}
			GL11.glEnd();
		} else if (innerEyelid) { // Draws the line in the middle
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(top.posX, top.posY, top.posZ);
			GL11.glVertex3d(bottom.posX, bottom.posY, bottom.posZ);
			GL11.glEnd();
		}

		GL11.glBegin(GL11.GL_LINE_LOOP); // Draws the outer circle
		for (int i = 0; i <= max; ++i) {
			float pos = (float) i / (float) max * 4.0F;
			GL11.glVertex3d(center.posX + radius * MathBezier.cos(pos), center.posY + radius * MathBezier.sin(pos), center.posZ);
		}
		GL11.glEnd();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		VectorPool.unuseVectors();
	}

	private static void setPoints(Vector[] pointsarray, Vector... points) {
		for (int i = 0; i < points.length; ++i) {
			pointsarray[i] = points[i];
		}
	}
}
