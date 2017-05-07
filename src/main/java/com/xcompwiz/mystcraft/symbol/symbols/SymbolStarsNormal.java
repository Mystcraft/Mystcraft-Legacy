package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SymbolStarsNormal extends SymbolBase {

	public SymbolStarsNormal(String identifier) {
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

		private Random			rand;
		private int				starGLCallList;
		private boolean			initialized;

		private long			period;
		private float			angle;
		private float			offset	= 0;
		private ColorGradient	gradient;
		private AgeDirector	controller;

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
		public void render(TextureManager texturemanager, World worldObj, float partial) {
			if (!initialized) initialize();
			// Draw Stars
			float invertRain = 1.0F - worldObj.getRainStrength(partial);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glPushMatrix();
			GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(getCelestialPeriod(worldObj.getWorldTime(), partial) * 360.0F, 1.0F, 0.0F, 0.0F);

			float starbrightness = worldObj.getStarBrightness(partial) * invertRain;
			if (starbrightness > 0.0F) {
				Color color = gradient.getColor(controller.getTime() / 12000F);
				GL11.glColor4f(color.r, color.g, color.b, starbrightness);
				GL11.glCallList(this.starGLCallList);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
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
			this.starGLCallList = GLAllocation.generateDisplayLists(1);
			GL11.glPushMatrix();
			GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
			this.renderStars();
			GL11.glEndList();
			GL11.glPopMatrix();
		}

		private void renderStars() {
			Tessellator tess = Tessellator.instance;
			tess.startDrawingQuads();

			for (int i = 0; i < 1500; ++i) {
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

					setStarColor(i, tess);
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
						tess.addVertex(var14 + var57, var16 + var53, var18 + var61);
					}
				}
			}

			tess.draw();
		}

		private void setStarColor(int var3, Tessellator var2) {
			// var2.setColorRGBA_F(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat()*0.8F+0.2F);
		}
	}
}
