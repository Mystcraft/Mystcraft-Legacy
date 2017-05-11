package com.xcompwiz.mystcraft.client.render;

import java.util.Random;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.entity.EntityLightningBoltAdv;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLightningBoltAdv extends Render<EntityLightningBoltAdv> {

	public RenderLightningBoltAdv(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityLightningBoltAdv entity, double x, double y, double z, float entityYaw, float partialTicks) {
		Tessellator var10 = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		double[] var11 = new double[8];
		double[] var12 = new double[8];
		double var13 = 0.0D;
		double var15 = 0.0D;
		Random var17 = new Random(par1EntityLightningBolt.boltVertex);
		Color color = par1EntityLightningBolt.getColor();

		for (int var18 = 7; var18 >= 0; --var18) {
			var11[var18] = var13;
			var12[var18] = var15;
			var13 += (var17.nextInt(11) - 5);
			var15 += (var17.nextInt(11) - 5);
		}

		for (int var45 = 0; var45 < 4; ++var45) {
			Random var46 = new Random(par1EntityLightningBolt.boltVertex);

			for (int var19 = 0; var19 < 3; ++var19) {
				int var20 = 7;
				int var21 = 0;

				if (var19 > 0) {
					var20 = 7 - var19;
				}

				if (var19 > 0) {
					var21 = var20 - 2;
				}

				double var22 = var11[var20] - var13;
				double var24 = var12[var20] - var15;

				for (int var26 = var20; var26 >= var21; --var26) {
					double var27 = var22;
					double var29 = var24;

					if (var19 == 0) {
						var22 += (var46.nextInt(11) - 5);
						var24 += (var46.nextInt(11) - 5);
					} else {
						var22 += (var46.nextInt(31) - 15);
						var24 += (var46.nextInt(31) - 15);
					}

					var10.startDrawing(5);
					var10.setColorRGBA_F(color.r, color.g, color.b, 0.51F);
					double var32 = 0.1D + var45 * 0.2D;

					if (var19 == 0) {
						var32 *= var26 * 0.1D + 1.0D;
					}

					double var34 = 0.1D + var45 * 0.2D;

					if (var19 == 0) {
						var34 *= (var26 - 1) * 0.1D + 1.0D;
					}

					for (int var36 = 0; var36 < 5; ++var36) {
						double var37 = par2 + 0.5D - var32;
						double var39 = par6 + 0.5D - var32;

						if (var36 == 1 || var36 == 2) {
							var37 += var32 * 2.0D;
						}

						if (var36 == 2 || var36 == 3) {
							var39 += var32 * 2.0D;
						}

						double var41 = par2 + 0.5D - var34;
						double var43 = par6 + 0.5D - var34;

						if (var36 == 1 || var36 == 2) {
							var41 += var34 * 2.0D;
						}

						if (var36 == 2 || var36 == 3) {
							var43 += var34 * 2.0D;
						}

						var10.addVertex(var41 + var22, par4 + (var26 * 16), var43 + var24);
						var10.addVertex(var37 + var27, par4 + ((var26 + 1) * 16), var39 + var29);
					}

					var10.draw();
				}
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLightningBoltAdv entity) {
		return null;
	}

	public static class Factory implements IRenderFactory<EntityLightningBoltAdv> {

		@Override
		public Render<EntityLightningBoltAdv> createRenderFor(RenderManager manager) {
			return new RenderLightningBoltAdv(manager);
		}

	}

}
