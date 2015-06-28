package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ICelestial;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.symbol.SunsetRenderer;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SymbolMoonNormal extends SymbolBase {

	public SymbolMoonNormal(String identifier) {
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

	private static class CelestialObject extends SunsetRenderer implements ICelestial {
		private Random	rand;

		private long	period;
		private float	angle;
		private float	offset;

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
				if (this.period == 0) offset = offset.floatValue() / 2 + 0.25F;
			}
			this.offset = offset.floatValue() - 0.5F;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void render(TextureManager eng, World worldObj, float partial) {
			// Draw Moon
			Tessellator tessellator = Tessellator.instance;
			float invertRain = 1.0F - worldObj.getRainStrength(partial);
			float celestial_period = getAltitudeAngle(worldObj.getWorldTime(), partial);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, invertRain);
			GL11.glPushMatrix();
			GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(celestial_period * 360.0F, 1.0F, 0.0F, 0.0F);

			float var12 = 20.0F;
			int moonphase = getMoonPhase(worldObj.getWorldTime(), partial);
			int x = moonphase % 4;
			int y = moonphase / 4 % 2;
			float x1 = (x + 0) / 4.0F;
			float y1 = (y + 0) / 2.0F;
			float x2 = (x + 1) / 4.0F;
			float y2 = (y + 1) / 2.0F;
			eng.bindTexture(Vanilla.normal_moon);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((-var12), 100.0D, (-var12), x2, y2);
			tessellator.addVertexWithUV(var12, 100.0D, (-var12), x1, y2);
			tessellator.addVertexWithUV(var12, 100.0D, (var12), x1, y1);
			tessellator.addVertexWithUV((-var12), 100.0D, (var12), x2, y1);
			tessellator.draw();

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
			if (this.gradient != null) this.renderHorizon(eng, worldObj, celestial_period, angle, partial, 0.3F);
		}

		@Override
		public float getAltitudeAngle(long time, float partialTime) {
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

		public int getMoonPhase(long time, float partialTime) {
			if (period == 0) return 0;
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
