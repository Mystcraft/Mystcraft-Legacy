package com.xcompwiz.mystcraft.client.linkeffects;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LinkRendererDisarm implements ILinkPanelEffect {
	private Random	rand	= new Random();
	private long	lasttime;
	private Point	effect;

	@Override
	public void onOpen() {
		lasttime = System.currentTimeMillis();
	}

	@Override
	public void render(int i, int j, int k, int l, ILinkInfo linkInfo) {
		if (linkInfo.getFlag(ILinkPropertyAPI.FLAG_DISARM)) {
			long now = System.currentTimeMillis();
			long delta = now - lasttime;
			if ((delta > 3000 && rand.nextInt(3000) == 0) || delta > 8000) {
				generateLightningEffect(i, j, k, l);
				lasttime = now;
				delta = 0;
			}
			if (effect != null) {
				effect.drawLightning();
				if (delta > 300) effect = null;
			}
		}
	}

	private void generateLightningEffect(int left, int top, int sizex, int sizey) {
		effect = generateCenterPoint(left, top, sizex, sizey);
		int offset = rand.nextInt(4);
		for (int i = 0; i < rand.nextInt(6) + 3; ++i) {
			effect.addChild(generateEndPoint((i + offset) % 4, left, top, sizex, sizey));
		}
	}

	private Point generateCenterPoint(int left, int top, int sizex, int sizey) {
		return new Point(rand.nextInt(sizex / 2) + left + sizex / 4, rand.nextInt(sizey / 2) + top + sizey / 4);
	}

	private Point generateEndPoint(int side, int left, int top, int sizex, int sizey) {
		switch (side) {
		case 0:
			return new Point(left, rand.nextInt(sizey) + top);
		case 1:
			return new Point(rand.nextInt(sizex) + left, top);
		case 2:
			return new Point(sizex + left, rand.nextInt(sizey) + top);
		case 3:
			return new Point(rand.nextInt(sizex) + left, sizey + top);
		}
		return new Point(rand.nextInt(sizex) + left, rand.nextInt(sizey) + top);
	}

	public class Point {
		private int					x;
		private int					y;
		private ArrayList<Point>	children;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void addChild(Point child) {
			if (children == null) {
				children = new ArrayList<Point>();
			}
			children.add(child);
		}

		public void drawLightning() {
			double x = this.x + rand.nextGaussian() * 2;
			double y = this.y + rand.nextGaussian() * 2;
			for (Point p : children) {
				drawLine(x, y, p.x + rand.nextGaussian() * 3, p.y + rand.nextGaussian() * 3);
			}
		}

		private double interpolate(double start, double end, float f) {

			return start + ((end - start) * f);
		}

		// TODO: (Visuals) zLevels
		private void drawLine(double par1, double par2, double par3, double par4) {
			int color = 0xFFFF3333;
			int points = rand.nextInt(50);
			float var10 = (color >> 24 & 255) / 255.0F;
			float var6 = (color >> 16 & 255) / 255.0F;
			float var7 = (color >> 8 & 255) / 255.0F;
			float var8 = (color & 255) / 255.0F;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			// GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			Tessellator var9 = Tessellator.instance;

			long seed = rand.nextLong();
			Random lrand = new Random();
			double factor;
			// Draw base lines
			lrand.setSeed(seed);
			GL11.glColor4f(var6, var7, var8, var10);
			GL11.glLineWidth(8.0f);
			var9.startDrawing(GL11.GL_LINE_STRIP);
			var9.addVertex(par1, par2, 0.0D);
			for (int i = 1; i < points; ++i) {
				factor = 2 * (1 - i / points);
				var9.addVertex(interpolate(par1, par3, ((float) i / points)) + lrand.nextGaussian() * factor, interpolate(par2, par4, ((float) i / points)) + lrand.nextGaussian() * factor, 0.0D);
			}
			var9.addVertex(par3, par4, 0.0D);
			var9.draw();
			// Draw top lines
			lrand.setSeed(seed);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glLineWidth(2.0f);
			var9.startDrawing(GL11.GL_LINE_STRIP);
			var9.addVertex(par1, par2, 0.0D);
			for (int i = 1; i < points; ++i) {
				factor = 2 * (1 - i / points);
				var9.addVertex(interpolate(par1, par3, ((float) i / points)) + lrand.nextGaussian() * factor, interpolate(par2, par4, ((float) i / points)) + lrand.nextGaussian() * factor, 0.0D);
			}
			var9.addVertex(par3, par4, 0.0D);
			var9.draw();

			// Clean up
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}
}
