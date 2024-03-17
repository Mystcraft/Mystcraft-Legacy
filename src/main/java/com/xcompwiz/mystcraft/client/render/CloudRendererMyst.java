package com.xcompwiz.mystcraft.client.render;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.world.AgeController;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CloudRendererMyst extends IRenderHandler {
	private static final ResourceLocation	locationCloudsPng	= new ResourceLocation("textures/environment/clouds.png");

	private int								rendererUpdateCount;

	public CloudRendererMyst(WorldProviderMyst provider, AgeController controller) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient worldObj, Minecraft mc) {
		if (mc.gameSettings.fancyGraphics) {
			this.renderCloudsFancy(mc, worldObj, partialTicks);
		} else {
			GL11.glDisable(GL11.GL_CULL_FACE);
			float f1 = (float) (mc.renderViewEntity.lastTickPosY + (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * partialTicks);
			byte b0 = 32;
			int i = 256 / b0;
			Tessellator tessellator = Tessellator.instance;
			mc.renderEngine.bindTexture(locationCloudsPng);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Vec3 vec3 = worldObj.getCloudColour(partialTicks);
			float f2 = (float) vec3.xCoord;
			float f3 = (float) vec3.yCoord;
			float f4 = (float) vec3.zCoord;
			float f5;

			if (mc.gameSettings.anaglyph) {
				f5 = (f2 * 30.0F + f3 * 59.0F + f4 * 11.0F) / 100.0F;
				float f6 = (f2 * 30.0F + f3 * 70.0F) / 100.0F;
				float f7 = (f2 * 30.0F + f4 * 70.0F) / 100.0F;
				f2 = f5;
				f3 = f6;
				f4 = f7;
			}

			f5 = 4.8828125E-4F;
			double d0 = (this.rendererUpdateCount + partialTicks);
			double d1 = mc.renderViewEntity.prevPosX + (mc.renderViewEntity.posX - mc.renderViewEntity.prevPosX) * partialTicks + d0 * 0.029999999329447746D;
			double d2 = mc.renderViewEntity.prevPosZ + (mc.renderViewEntity.posZ - mc.renderViewEntity.prevPosZ) * partialTicks;
			int j = MathHelper.floor(d1 / 2048.0D);
			int k = MathHelper.floor(d2 / 2048.0D);
			d1 -= (j * 2048);
			d2 -= (k * 2048);
			float f8 = worldObj.provider.getCloudHeight() - f1 + 0.33F;
			float f9 = (float) (d1 * f5);
			float f10 = (float) (d2 * f5);
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_F(f2, f3, f4, 0.8F);

			for (int l = -b0 * i; l < b0 * i; l += b0) {
				for (int i1 = -b0 * i; i1 < b0 * i; i1 += b0) {
					tessellator.addVertexWithUV((l + 0), f8, (i1 + b0), ((l + 0) * f5 + f9), ((i1 + b0) * f5 + f10));
					tessellator.addVertexWithUV((l + b0), f8, (i1 + b0), ((l + b0) * f5 + f9), ((i1 + b0) * f5 + f10));
					tessellator.addVertexWithUV((l + b0), f8, (i1 + 0), ((l + b0) * f5 + f9), ((i1 + 0) * f5 + f10));
					tessellator.addVertexWithUV((l + 0), f8, (i1 + 0), ((l + 0) * f5 + f9), ((i1 + 0) * f5 + f10));
				}
			}

			tessellator.draw();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderCloudsFancy(Minecraft mc, WorldClient worldObj, float partialTicks) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glPushMatrix();
		float f1 = (float) (mc.renderViewEntity.lastTickPosY + (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * partialTicks);
		Tessellator tessellator = Tessellator.instance;
		float f2 = 12.0F;
		float f3 = 4.0F;
		double d0 = (this.rendererUpdateCount + partialTicks);
		double d1 = (mc.renderViewEntity.prevPosX + (mc.renderViewEntity.posX - mc.renderViewEntity.prevPosX) * partialTicks + d0 * 0.029999999329447746D) / f2;
		double d2 = (mc.renderViewEntity.prevPosZ + (mc.renderViewEntity.posZ - mc.renderViewEntity.prevPosZ) * partialTicks) / f2 + 0.33000001311302185D;
		float f4 = worldObj.provider.getCloudHeight() - f1 + 0.33F;
		int i = MathHelper.floor(d1 / 2048.0D);
		int j = MathHelper.floor(d2 / 2048.0D);
		d1 -= (i * 2048);
		d2 -= (j * 2048);
		mc.renderEngine.bindTexture(locationCloudsPng);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Vec3 vec3 = worldObj.getCloudColour(partialTicks);
		float f5 = (float) vec3.xCoord;
		float f6 = (float) vec3.yCoord;
		float f7 = (float) vec3.zCoord;
		float f8;
		float f9;
		float f10;

		if (mc.gameSettings.anaglyph) {
			f8 = (f5 * 30.0F + f6 * 59.0F + f7 * 11.0F) / 100.0F;
			f10 = (f5 * 30.0F + f6 * 70.0F) / 100.0F;
			f9 = (f5 * 30.0F + f7 * 70.0F) / 100.0F;
			f5 = f8;
			f6 = f10;
			f7 = f9;
		}

		f8 = (float) (d1 * 0.0D);
		f10 = (float) (d2 * 0.0D);
		f9 = 0.00390625F;
		f8 = MathHelper.floor(d1) * f9;
		f10 = MathHelper.floor(d2) * f9;
		float f11 = (float) (d1 - MathHelper.floor(d1));
		float f12 = (float) (d2 - MathHelper.floor(d2));
		byte b0 = 8;
		byte b1 = 4;
		float f13 = 9.765625E-4F;
		GL11.glScalef(f2, 1.0F, f2);

		for (int pass = 0; pass < 2; ++pass) {
			if (pass == 0) {
				GL11.glColorMask(false, false, false, false);
			} else if (mc.gameSettings.anaglyph) {
				if (EntityRenderer.anaglyphField == 0) {
					GL11.glColorMask(false, true, true, true);
				} else {
					GL11.glColorMask(true, false, false, true);
				}
			} else {
				GL11.glColorMask(true, true, true, true);
			}

			for (int l = -b1 + 1; l <= b1; ++l) {
				for (int i1 = -b1 + 1; i1 <= b1; ++i1) {
					tessellator.startDrawingQuads();
					float f14 = (l * b0);
					float f15 = (i1 * b0);
					float f16 = f14 - f11;
					float f17 = f15 - f12;

					if (f4 > -f3 - 1.0F) {
						tessellator.setColorRGBA_F(f5 * 0.7F, f6 * 0.7F, f7 * 0.7F, 0.8F);
						tessellator.setNormal(0.0F, -1.0F, 0.0F);
						tessellator.addVertexWithUV((f16 + 0.0F), (f4 + 0.0F), (f17 + b0), ((f14 + 0.0F) * f9 + f8), ((f15 + b0) * f9 + f10));
						tessellator.addVertexWithUV((f16 + b0), (f4 + 0.0F), (f17 + b0), ((f14 + b0) * f9 + f8), ((f15 + b0) * f9 + f10));
						tessellator.addVertexWithUV((f16 + b0), (f4 + 0.0F), (f17 + 0.0F), ((f14 + b0) * f9 + f8), ((f15 + 0.0F) * f9 + f10));
						tessellator.addVertexWithUV((f16 + 0.0F), (f4 + 0.0F), (f17 + 0.0F), ((f14 + 0.0F) * f9 + f8), ((f15 + 0.0F) * f9 + f10));
					}

					if (f4 <= f3 + 1.0F) {
						tessellator.setColorRGBA_F(f5, f6, f7, 0.8F);
						tessellator.setNormal(0.0F, 1.0F, 0.0F);
						tessellator.addVertexWithUV((f16 + 0.0F), (f4 + f3 - f13), (f17 + b0), ((f14 + 0.0F) * f9 + f8), ((f15 + b0) * f9 + f10));
						tessellator.addVertexWithUV((f16 + b0), (f4 + f3 - f13), (f17 + b0), ((f14 + b0) * f9 + f8), ((f15 + b0) * f9 + f10));
						tessellator.addVertexWithUV((f16 + b0), (f4 + f3 - f13), (f17 + 0.0F), ((f14 + b0) * f9 + f8), ((f15 + 0.0F) * f9 + f10));
						tessellator.addVertexWithUV((f16 + 0.0F), (f4 + f3 - f13), (f17 + 0.0F), ((f14 + 0.0F) * f9 + f8), ((f15 + 0.0F) * f9 + f10));
					}

					tessellator.setColorRGBA_F(f5 * 0.9F, f6 * 0.9F, f7 * 0.9F, 0.8F);
					int j1;

					if (l > -1) {
						tessellator.setNormal(-1.0F, 0.0F, 0.0F);

						for (j1 = 0; j1 < b0; ++j1) {
							tessellator.addVertexWithUV((f16 + j1 + 0.0F), (f4 + 0.0F), (f17 + b0), ((f14 + j1 + 0.5F) * f9 + f8), ((f15 + b0) * f9 + f10));
							tessellator.addVertexWithUV((f16 + j1 + 0.0F), (f4 + f3), (f17 + b0), ((f14 + j1 + 0.5F) * f9 + f8), ((f15 + b0) * f9 + f10));
							tessellator.addVertexWithUV((f16 + j1 + 0.0F), (f4 + f3), (f17 + 0.0F), ((f14 + j1 + 0.5F) * f9 + f8), ((f15 + 0.0F) * f9 + f10));
							tessellator.addVertexWithUV((f16 + j1 + 0.0F), (f4 + 0.0F), (f17 + 0.0F), ((f14 + j1 + 0.5F) * f9 + f8), ((f15 + 0.0F) * f9 + f10));
						}
					}

					if (l <= 1) {
						tessellator.setNormal(1.0F, 0.0F, 0.0F);

						for (j1 = 0; j1 < b0; ++j1) {
							tessellator.addVertexWithUV((f16 + j1 + 1.0F - f13), (f4 + 0.0F), (f17 + b0), ((f14 + j1 + 0.5F) * f9 + f8), ((f15 + b0) * f9 + f10));
							tessellator.addVertexWithUV((f16 + j1 + 1.0F - f13), (f4 + f3), (f17 + b0), ((f14 + j1 + 0.5F) * f9 + f8), ((f15 + b0) * f9 + f10));
							tessellator.addVertexWithUV((f16 + j1 + 1.0F - f13), (f4 + f3), (f17 + 0.0F), ((f14 + j1 + 0.5F) * f9 + f8), ((f15 + 0.0F) * f9 + f10));
							tessellator.addVertexWithUV((f16 + j1 + 1.0F - f13), (f4 + 0.0F), (f17 + 0.0F), ((f14 + j1 + 0.5F) * f9 + f8), ((f15 + 0.0F) * f9 + f10));
						}
					}

					tessellator.setColorRGBA_F(f5 * 0.8F, f6 * 0.8F, f7 * 0.8F, 0.8F);

					if (i1 > -1) {
						tessellator.setNormal(0.0F, 0.0F, -1.0F);

						for (j1 = 0; j1 < b0; ++j1) {
							tessellator.addVertexWithUV((f16 + 0.0F), (f4 + f3), (f17 + j1 + 0.0F), ((f14 + 0.0F) * f9 + f8), ((f15 + j1 + 0.5F) * f9 + f10));
							tessellator.addVertexWithUV((f16 + b0), (f4 + f3), (f17 + j1 + 0.0F), ((f14 + b0) * f9 + f8), ((f15 + j1 + 0.5F) * f9 + f10));
							tessellator.addVertexWithUV((f16 + b0), (f4 + 0.0F), (f17 + j1 + 0.0F), ((f14 + b0) * f9 + f8), ((f15 + j1 + 0.5F) * f9 + f10));
							tessellator.addVertexWithUV((f16 + 0.0F), (f4 + 0.0F), (f17 + j1 + 0.0F), ((f14 + 0.0F) * f9 + f8), ((f15 + j1 + 0.5F) * f9 + f10));
						}
					}

					if (i1 <= 1) {
						tessellator.setNormal(0.0F, 0.0F, 1.0F);

						for (j1 = 0; j1 < b0; ++j1) {
							tessellator.addVertexWithUV((f16 + 0.0F), (f4 + f3), (f17 + j1 + 1.0F - f13), ((f14 + 0.0F) * f9 + f8), ((f15 + j1 + 0.5F) * f9 + f10));
							tessellator.addVertexWithUV((f16 + b0), (f4 + f3), (f17 + j1 + 1.0F - f13), ((f14 + b0) * f9 + f8), ((f15 + j1 + 0.5F) * f9 + f10));
							tessellator.addVertexWithUV((f16 + b0), (f4 + 0.0F), (f17 + j1 + 1.0F - f13), ((f14 + b0) * f9 + f8), ((f15 + j1 + 0.5F) * f9 + f10));
							tessellator.addVertexWithUV((f16 + 0.0F), (f4 + 0.0F), (f17 + j1 + 1.0F - f13), ((f14 + 0.0F) * f9 + f8), ((f15 + j1 + 0.5F) * f9 + f10));
						}
					}

					tessellator.draw();
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	public void updateClouds() {
		++this.rendererUpdateCount;
	}
}
