package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
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
                GlStateManager.color(color.r, color.g, color.b, starbrightness);
                GlStateManager.callList(this.starGLCallList);
			}

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
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
            GlStateManager.pushMatrix();
            GlStateManager.glNewList(this.starGLCallList, GL11.GL_COMPILE);
            this.renderStars();
            GlStateManager.glEndList();
            GlStateManager.popMatrix();
		}

        @SideOnly(Side.CLIENT)
		private void renderStars() {
		    Tessellator tes = Tessellator.getInstance();
            BufferBuilder vb = tes.getBuffer();
            Random random = new Random(10842L);
            vb.begin(7, DefaultVertexFormats.POSITION);

            for (int i = 0; i < 1500; ++i) {
                double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
                double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
                double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
                double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
                double d4 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d4 < 1.0D && d4 > 0.01D) {
                    d4 = 1.0D / Math.sqrt(d4);
                    d0 = d0 * d4;
                    d1 = d1 * d4;
                    d2 = d2 * d4;
                    double d5 = d0 * 100.0D;
                    double d6 = d1 * 100.0D;
                    double d7 = d2 * 100.0D;
                    double d8 = Math.atan2(d0, d2);
                    double d9 = Math.sin(d8);
                    double d10 = Math.cos(d8);
                    double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                    double d12 = Math.sin(d11);
                    double d13 = Math.cos(d11);
                    double d14 = random.nextDouble() * Math.PI * 2.0D;
                    double d15 = Math.sin(d14);
                    double d16 = Math.cos(d14);

                    for (int j = 0; j < 4; ++j) {
                        double d17 = 0.0D;
                        double d18 = (double) ((j & 2) - 1) * d3;
                        double d19 = (double) ((j + 1 & 2) - 1) * d3;
                        double d20 = 0.0D;
                        double d21 = d18 * d16 - d19 * d15;
                        double d22 = d19 * d16 + d18 * d15;
                        double d23 = d21 * d12 + 0.0D * d13;
                        double d24 = 0.0D * d12 - d21 * d13;
                        double d25 = d24 * d9 - d22 * d10;
                        double d26 = d22 * d9 + d24 * d10;
                        vb.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                    }
                }
            }
            tes.draw();
		}

		private void setStarColor(int var3, Tessellator var2) {
		    //Hellfire> apply at renderStars, change vertex format to POSITION_COLOR and supply .color to the vertex after .pos
			// var2.setColorRGBA_F(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat()*0.8F+0.2F);
		}
	}
}
