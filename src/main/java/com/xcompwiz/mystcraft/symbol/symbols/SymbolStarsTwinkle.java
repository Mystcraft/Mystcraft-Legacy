package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SymbolStarsTwinkle extends SymbolBase {

	public SymbolStarsTwinkle(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Number period = controller.popModifier(ModifierUtils.FACTOR).asNumber();
		Number angle = controller.popModifier(ModifierUtils.ANGLE).asNumber();
		ColorGradient gradient = ModifierUtils.popGradient(controller, 1, 1, 1);
		controller.registerInterface(new SkyBackground(controller, seed, period, angle, gradient));
	}

	private class SkyBackground extends CelestialBase {

		private static final int	layerscount	= 10;
		private Random				rand;
		private int[]				starGLCallList;
		private long[]				offsets;
		private boolean				initialized;

		private long				period;
		private float				angle;
		private float				offset		= 0;
		private ColorGradient		gradient;
		private AgeDirector		controller;

		SkyBackground(AgeDirector controller, long seed, Number period, Number angle, ColorGradient gradient) {
			this.controller = controller;
			rand = new Random(seed);
			if (period == null) {
				period = 1.8 * rand.nextDouble() + 0.2;
			}
			this.period = (long) (period.doubleValue() * 240000L);
			if (angle == null) {
				angle = rand.nextDouble() * 360.0F;
			}
			this.angle = -angle.floatValue();
			this.gradient = gradient;
		}

		@Override
		public void render(TextureManager texturemanager, World world, float partial) {
			if (!initialized) {
				initialize();
			}
            // Draw Stars
            float invertRain = 1.0F - world.getRainStrength(partial);
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(getCelestialPeriod(world.getWorldTime(), partial) * 360.0F, 1.0F, 0.0F, 0.0F);

            float starbrightness = world.getStarBrightness(partial) * invertRain;
            if (starbrightness > 0.0F) {
                Color color = gradient.getColor(controller.getTime() / 12000F);
                for (int i = 0; i < this.starGLCallList.length; ++i) {
                    GlStateManager.color(color.r, color.g, color.b, starbrightness * getLayerBrightness(i, world, partial));
                    GlStateManager.callList(this.starGLCallList[i]);
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
		}

		private float getLayerBrightness(int i, World worldObj, float partial) {
			long time = worldObj.getWorldTime() + offsets[i];
			time = (time % 100L);
			float f1 = (time + partial) / 100L;
			float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.25F);

			if (f2 < 0.0F) {
				f2 = 0.0F;
			}
			if (f2 > 1.0F) {
				f2 = 1.0F;
			}

			return f2;
		}

		public float getCelestialPeriod(long time, float partialTime) {
			if (period == 0) return offset;
			int i = (int) (time % period);
			float f = (i + partialTime) / period + offset;

			if (f < 0.0F) ++f;
			if (f > 1.0F) --f;

			float f1 = f;
			f = 1.0F - (float) ((Math.cos(f * Math.PI) + 1.0D) / 2D);
			f = f1 + (f - f1) / 3F;
			return f;
		}

		@SideOnly(Side.CLIENT)
		private void initialize() {
			initialized = true;
			this.starGLCallList = new int[layerscount];
			this.offsets = new long[this.starGLCallList.length];
			for (int i = 0; i < this.offsets.length; ++i) {
				this.offsets[i] = rand.nextLong();
			}
			this.starGLCallList[0] = GLAllocation.generateDisplayLists(this.starGLCallList.length);
			for (int i = 0; i < this.starGLCallList.length; ++i) {
				this.starGLCallList[i] = this.starGLCallList[0] + i;
				GlStateManager.pushMatrix();
				GlStateManager.glNewList(this.starGLCallList[i], GL11.GL_COMPILE);
				this.renderStars();
				GlStateManager.glEndList();
				GlStateManager.popMatrix();
			}
		}

		private void renderStars() {
			Tessellator tes = Tessellator.getInstance();
			VertexBuffer vb = tes.getBuffer();
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

			for (int i = 0; i < 100; ++i) {
				double var4 = (rand.nextFloat() * 2.0F - 1.0F);
				double var6 = (rand.nextFloat() * 2.0F - 1.0F);
				double var8 = (rand.nextFloat() * 2.0F - 1.0F);
				double var10 = (0.15F + rand.nextFloat() * 0.1F);
				double var12 = var4 * var4 + var6 * var6 + var8 * var8;

				if (var12 < 1.0D && var12 > 0.01D) {
					var12 = 1.0D / Math.sqrt(var12);
					var4 *= var12;
					var6 *= var12;
					var8 *= var12;
					double var14 = var4 * 100.0D;
					double var16 = var6 * 100.0D;
					double var18 = var8 * 100.0D;
					double var20 = Math.atan2(var4, var8);
					double var22 = Math.sin(var20);
					double var24 = Math.cos(var20);
					double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
					double var28 = Math.sin(var26);
					double var30 = Math.cos(var26);
					double var32 = rand.nextDouble() * Math.PI * 2.0D;
					double var34 = Math.sin(var32);
					double var36 = Math.cos(var32);

					java.awt.Color c = getRandomColor();
					for (int var38 = 0; var38 < 4; ++var38) {
						double var39 = 0.0D;
						double var41 = ((var38 & 2) - 1) * var10;
						double var43 = ((var38 + 1 & 2) - 1) * var10;
						double var47 = var41 * var36 - var43 * var34;
						double var49 = var43 * var36 + var41 * var34;
						double var53 = var47 * var28 + var39 * var30;
						double var55 = var39 * var28 - var47 * var30;
						double var57 = var55 * var22 - var49 * var24;
						double var61 = var49 * var22 + var55 * var24;
						vb.pos(var14 + var57, var16 + var53, var18 + var61).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
					}
				}
			}

			tes.draw();
		}

		private java.awt.Color getRandomColor() {
			return new java.awt.Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat() * 0.8F + 0.2F);
		}

	}
}
