package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

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

public class SymbolSunNormal extends SymbolBase {

	public SymbolSunNormal(ResourceLocation identifier) {
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
				if (this.period == 0)
					offset = offset.floatValue() / 2 + 0.25F;
			}
			this.offset = offset.floatValue() - 0.5F;
		}

		@Override
		public boolean providesLight() {
			return true;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void render(TextureManager eng, World world, float partial) {
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder vb = tes.getBuffer();

			float celestial_period = this.getAltitudeAngle(world.getWorldTime(), partial);
			GlStateManager.enableTexture2D();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.pushMatrix();
			float f16 = 1.0F - world.getRainStrength(partial);
			GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
			GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(celestial_period * 360.0F, 1.0F, 0.0F, 0.0F);
			float size = 30.0F;
			eng.bindTexture(Vanilla.normal_sun);
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);
			vb.pos((double) (-size), 100.0D, (double) (-size)).tex(0.0D, 0.0D).endVertex();
			vb.pos((double) size, 100.0D, (double) (-size)).tex(1.0D, 0.0D).endVertex();
			vb.pos((double) size, 100.0D, (double) size).tex(1.0D, 1.0D).endVertex();
			vb.pos((double) (-size), 100.0D, (double) size).tex(0.0D, 1.0D).endVertex();
			tes.draw();
			GlStateManager.popMatrix();

			this.renderHorizon(eng, world, celestial_period, angle, partial, 1.0F);
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

		@Override
		public Long getTimeToDawn(long time) {
			if (period == 0)
				return null;
			long current = time % period;
			long next = (long) (period * Math.abs(0.75F - offset));
			if (current > next)
				next += period;
			return next - current;
		}
	}
}
