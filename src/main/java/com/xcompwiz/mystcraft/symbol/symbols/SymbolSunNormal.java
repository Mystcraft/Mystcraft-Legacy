package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.world.logic.ISun;
import com.xcompwiz.mystcraft.data.Assets;
import com.xcompwiz.mystcraft.symbol.ColorGradient;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SunsetRenderer;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SymbolSunNormal extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		Number period = controller.popModifier(ModifierUtils.FACTOR).asNumber();
		Number angle = controller.popModifier(ModifierUtils.ANGLE).asNumber();
		Number offset = controller.popModifier(ModifierUtils.PHASE).asNumber();
		ColorGradient sunset = controller.popModifier(ModifierUtils.SUNSET).asGradient();
		controller.registerInterface(new CelestialObject(controller, seed, period, angle, offset, sunset));
	}

	@Override
	public String identifier() {
		return "SunNormal";
	}

	private static class CelestialObject extends SunsetRenderer implements ISun {
		private Random	rand;

		private long	period;
		private float	angle;
		private float	offset;

		CelestialObject(IAgeController controller, long seed, Number period, Number angle, Number offset, ColorGradient gradient) {
			super(controller, gradient);
			rand = new Random(seed);
			if (period == null) {
				period = 0.4 * rand.nextDouble() + 0.8;
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
			// Draw Sun
			Tessellator tessellator = Tessellator.instance;
			float invertRain = 1.0F - worldObj.getRainStrength(partial);
			float celestial_period = this.getCelestialPeriod(worldObj.getWorldTime(), partial);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, invertRain);
			GL11.glPushMatrix();
			GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(celestial_period * 360.0F, 1.0F, 0.0F, 0.0F);

			float size = 30.0F;
			eng.bindTexture(Assets.normal_sun);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((-size), 100.0D, (-size), 0.0D, 0.0D);
			tessellator.addVertexWithUV(size, 100.0D, (-size), 1.0D, 0.0D);
			tessellator.addVertexWithUV(size, 100.0D, size, 1.0D, 1.0D);
			tessellator.addVertexWithUV((-size), 100.0D, size, 0.0D, 1.0D);
			tessellator.draw();

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
			this.renderHorizon(eng, worldObj, celestial_period, angle, partial, 1.0F);
		}

		@Override
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

		@Override
		public Long getTimeToSunrise(long time) {
			if (period == 0) return null;
			long current = time % period;
			long next = (long) (period * Math.abs(0.75F - offset));
			if (current > next) next += period;
			return next - current;
		}
	}
}
