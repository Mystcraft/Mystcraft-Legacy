package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ICelestial;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.symbol.SunsetRenderer;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SymbolMoonNormal extends SymbolBase {

	public SymbolMoonNormal(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Number period = controller.popModifier(ModifierUtils.FACTOR).asNumber();
		Number angle = controller.popModifier(ModifierUtils.ANGLE).asNumber();
		Number offset = controller.popModifier(ModifierUtils.PHASE).asNumber();
		ColorGradient sunset = controller.popModifier(ModifierUtils.SUNSET).asGradient();
		controller.registerInterface(new CelestialObject(controller, seed, period, angle, offset, sunset));
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private static class CelestialObject extends SunsetRenderer implements ICelestial {
		private Random rand;

		private long period;
		private float angle;
		private float offset;

		CelestialObject(AgeDirector controller, long seed, Number period, Number angle, Number offset, ColorGradient gradient) {
			super(controller, gradient);
			rand = new Random(seed);
			if (period == null) {
				period = 1.8 * rand.nextDouble() + 0.2;
			}
			this.period = (long) (period.doubleValue() * 24000L);
			if (angle == null) {
				angle = rand.nextDouble() * 360.0F;
			}
			this.angle = -angle.floatValue();
			if (offset != null) {
				offset = offset.floatValue() / 360F;
			}
			if (offset == null) {
				offset = rand.nextFloat();
				if (this.period == 0)
					offset = offset.floatValue() / 2 + 0.25F;
			}
			this.offset = offset.floatValue() - 0.5F;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void render(TextureManager eng, World worldObj, float partial) {
			// Draw Moon
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder vb = tes.getBuffer();

			float invertRain = 1.0F - worldObj.getRainStrength(partial);
			float celestial_period = getAltitudeAngle(worldObj.getWorldTime(), partial);
			GlStateManager.enableTexture2D();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			GlStateManager.color(1F, 1F, 1F, invertRain);
			GlStateManager.pushMatrix();
			GlStateManager.rotate(angle, 0, 1, 0);
			GlStateManager.rotate(celestial_period * 360F, 1, 0, 0);

			float size = 20.0F;
			int moonphase = getMoonPhase(worldObj.getWorldTime(), partial);
			int k = moonphase % 4;
			int i1 = moonphase / 4 % 2;
			float f22 = (float) (k + 0) / 4.0F;
			float f23 = (float) (i1 + 0) / 2.0F;
			float f24 = (float) (k + 1) / 4.0F;
			float f14 = (float) (i1 + 1) / 2.0F;
			eng.bindTexture(Vanilla.normal_moon);
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos((double) (-size), -100.0D, (double) size).tex((double) f24, (double) f14).endVertex();
			vb.pos((double) size, -100.0D, (double) size).tex((double) f22, (double) f14).endVertex();
			vb.pos((double) size, -100.0D, (double) (-size)).tex((double) f22, (double) f23).endVertex();
			vb.pos((double) (-size), -100.0D, (double) (-size)).tex((double) f24, (double) f23).endVertex();
			tes.draw();

			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.popMatrix();
			if (this.gradient != null) {
				this.renderHorizon(eng, worldObj, celestial_period, angle, partial, 0.3F);
			}
		}

		@Override
		public float getAltitudeAngle(long time, float partialTime) {
			if (period == 0)
				return offset;
			int i = (int) (time % period);
			float f = (i + partialTime) / period + offset;

			if (f < 0.0F)
				++f;
			if (f > 1.0F)
				--f;

			float f1 = f;
			f = 1.0F - (float) ((Math.cos(f * Math.PI) + 1.0D) / 2D);
			f = f1 + (f - f1) / 3F;
			return f;
		}

		public int getMoonPhase(long time, float partialTime) {
			if (period == 0)
				return 0;
			return (int) (time / period) % 8;
		}

		@Override
		public boolean providesLight() {
			return false;
		}

		@Override
		public Long getTimeToDawn(long time) {
			return null;
		}
	}
}
