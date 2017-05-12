package com.xcompwiz.mystcraft.client.linkeffects;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

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
	public void render(int i, int j, int k, int l, ILinkInfo linkInfo, @Nonnull ItemStack bookclone) {
		if (linkInfo.getFlag(LinkPropertyAPI.FLAG_DISARM)) {
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
				children = new ArrayList<>();
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
			float alpha = (color >> 24 & 255) / 255.0F;
			float red   = (color >> 16 & 255) / 255.0F;
			float green = (color >> 8  & 255) / 255.0F;
			float blue  = (color       & 255) / 255.0F;

            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);

            Tessellator tes = Tessellator.getInstance();
            VertexBuffer vb = tes.getBuffer();

			long seed = rand.nextLong();
			Random lrand = new Random();
			double factor;
			// Draw base lines
			lrand.setSeed(seed);
			GlStateManager.color(red, green, blue, alpha);
			GlStateManager.glLineWidth(8F);
			vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
			vb.pos(par1, par2, 0).endVertex();
            for (int i = 1; i < points; ++i) {
                factor = 2 * (1 - i / points);
                vb.pos(interpolate(par1, par3, ((float) i / points)) + lrand.nextGaussian() * factor,
                        interpolate(par2, par4, ((float) i / points)) + lrand.nextGaussian() * factor, 0).endVertex();
            }
            vb.pos(par3, par4, 0);
            tes.draw();

			// Draw top lines
			lrand.setSeed(seed);
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.glLineWidth(2F);
            vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
            vb.pos(par1, par2, 0).endVertex();
            for (int i = 1; i < points; ++i) {
                factor = 2 * (1 - i / points);
                vb.pos(interpolate(par1, par3, ((float) i / points)) + lrand.nextGaussian() * factor,
                        interpolate(par2, par4, ((float) i / points)) + lrand.nextGaussian() * factor, 0.0D).endVertex();
            }
            vb.pos(par3, par4, 0).endVertex();
            tes.draw();

			// Clean up
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
		}
	}
}
