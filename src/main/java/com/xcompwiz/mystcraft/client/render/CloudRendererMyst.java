package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import com.xcompwiz.mystcraft.world.AgeController;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CloudRendererMyst extends IRenderHandler {

	private static final ResourceLocation locationCloudsPng	=
			new ResourceLocation("textures/environment/clouds.png");

	private int	rendererUpdateCount;

	public CloudRendererMyst(WorldProviderMyst provider, AgeController controller) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
	    Entity entity = mc.getRenderViewEntity();
        double p0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double p1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double p2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;

		if (mc.gameSettings.fancyGraphics) {
			this.renderCloudsFancy(mc, world, partialTicks, p0, p1, p2);
		} else {
            GlStateManager.disableCull();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder BufferBuilder = tessellator.getBuffer();
            mc.renderEngine.bindTexture(locationCloudsPng);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Vec3d vec3d = world.getCloudColour(partialTicks);
            float f =  (float) vec3d.x;
            float f1 = (float) vec3d.y;
            float f2 = (float) vec3d.z;

            if (mc.gameSettings.anaglyph){
                float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                f = f3;
                f1 = f4;
                f2 = f5;
            }
            double d2 = (double)((float)this.rendererUpdateCount + partialTicks);
            double d0 = p0 + d2 * 0.029999999329447746D;
            int k = MathHelper.floor(d0 / 2048.0D);
            int l = MathHelper.floor(p2 / 2048.0D);
            d0 = d0 - (double)(k * 2048);
            double lvt_22_1_ = p2 - (double)(l * 2048);
            float f6 = world.provider.getCloudHeight() - (float) p1 + 0.33F;
            float f7 = (float)(d0 * 4.8828125E-4D);
            float f8 = (float)(lvt_22_1_ * 4.8828125E-4D);
            BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

            for (int i1 = -256; i1 < 256; i1 += 32) {
                for (int j1 = -256; j1 < 256; j1 += 32) {
                    BufferBuilder.pos((double)(i1 + 0),  (double)f6, (double)(j1 + 32)).tex((double)((float)(i1 + 0)  * 4.8828125E-4F + f7), (double)((float)(j1 + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                    BufferBuilder.pos((double)(i1 + 32), (double)f6, (double)(j1 + 32)).tex((double)((float)(i1 + 32) * 4.8828125E-4F + f7), (double)((float)(j1 + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                    BufferBuilder.pos((double)(i1 + 32), (double)f6, (double)(j1 + 0)) .tex((double)((float)(i1 + 32) * 4.8828125E-4F + f7), (double)((float)(j1 + 0)  * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                    BufferBuilder.pos((double)(i1 + 0),  (double)f6, (double)(j1 + 0)) .tex((double)((float)(i1 + 0)  * 4.8828125E-4F + f7), (double)((float)(j1 + 0)  * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                }
            }

            tessellator.draw();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderCloudsFancy(Minecraft mc, WorldClient world, float partialTicks, double p0, double p1, double p2) {
	    GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder = tessellator.getBuffer();
        double d0 = (double)((float) this.rendererUpdateCount + partialTicks);
        double d1 = (p0 + d0 * 0.029999999329447746D) / 12.0D;
        double d2 = p2 / 12.0D + 0.33000001311302185D;
        float f2 = world.provider.getCloudHeight() - (float) p1 + 0.33F;
        int i = MathHelper.floor(d1 / 2048.0D);
        int j = MathHelper.floor(d2 / 2048.0D);
        d1 = d1 - (double)(i * 2048);
        d2 = d2 - (double)(j * 2048);
        mc.renderEngine.bindTexture(locationCloudsPng);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Vec3d vec3d = world.getCloudColour(partialTicks);
        float f3 = (float)vec3d.x;
        float f4 = (float)vec3d.y;
        float f5 = (float)vec3d.z;

        if (mc.gameSettings.anaglyph) {
            float f6 = (f3 * 30.0F + f4 * 59.0F + f5 * 11.0F) / 100.0F;
            float f7 = (f3 * 30.0F + f4 * 70.0F) / 100.0F;
            float f8 = (f3 * 30.0F + f5 * 70.0F) / 100.0F;
            f3 = f6;
            f4 = f7;
            f5 = f8;
        }
        float f25 = f3 * 0.9F;
        float f26 = f4 * 0.9F;
        float f27 = f5 * 0.9F;
        float f9 = f3 * 0.7F;
        float f10 = f4 * 0.7F;
        float f11 = f5 * 0.7F;
        float f12 = f3 * 0.8F;
        float f13 = f4 * 0.8F;
        float f14 = f5 * 0.8F;
        float f15 = 0.00390625F;
        float f16 = (float)MathHelper.floor(d1) * 0.00390625F;
        float f17 = (float)MathHelper.floor(d2) * 0.00390625F;
        float f18 = (float)(d1 - (double)MathHelper.floor(d1));
        float f19 = (float)(d2 - (double)MathHelper.floor(d2));
        int k = 8;
        int l = 4;
        float f20 = 9.765625E-4F;
        GlStateManager.scale(12.0F, 1.0F, 12.0F);

        for (int i1 = 0; i1 < 2; ++i1) {
            if (i1 == 0) {
                GlStateManager.colorMask(false, false, false, false);
            } else if (mc.gameSettings.anaglyph) {
                if (EntityRenderer.anaglyphField == 0) {
                    GlStateManager.colorMask(false, true, true, true);
                } else {
                    GlStateManager.colorMask(true, false, false, true);
                }
            } else {
                GlStateManager.colorMask(true, true, true, true);
            }

            for (int j1 = -3; j1 <= 4; ++j1) {
                for (int k1 = -3; k1 <= 4; ++k1) {
                    BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    float f21 = (float)(j1 * 8);
                    float f22 = (float)(k1 * 8);
                    float f23 = f21 - f18;
                    float f24 = f22 - f19;

                    if (f2 > -5.0F) {
                        BufferBuilder.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        BufferBuilder.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        BufferBuilder.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        BufferBuilder.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }

                    if (f2 <= 5.0F) {
                        BufferBuilder.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 8.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        BufferBuilder.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 8.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        BufferBuilder.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        BufferBuilder.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }

                    if (j1 > -1) {
                        for (int l1 = 0; l1 < 8; ++l1) {
                            BufferBuilder.pos((double)(f23 + (float)l1 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)l1 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + (float)l1 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)l1 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + (float)l1 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)l1 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + (float)l1 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)l1 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (j1 <= 1) {
                        for (int i2 = 0; i2 < 8; ++i2) {
                            BufferBuilder.pos((double)(f23 + (float)i2 + 1.0F - 9.765625E-4F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)i2 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + (float)i2 + 1.0F - 9.765625E-4F), (double)(f2 + 4.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)i2 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + (float)i2 + 1.0F - 9.765625E-4F), (double)(f2 + 4.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)i2 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + (float)i2 + 1.0F - 9.765625E-4F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)i2 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (k1 > -1) {
                        for (int j2 = 0; j2 < 8; ++j2) {
                            BufferBuilder.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + (float)j2 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)j2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F), (double)(f24 + (float)j2 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)j2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + (float)j2 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)j2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + (float)j2 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)j2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }

                    if (k1 <= 1) {
                        for (int k2 = 0; k2 < 8; ++k2) {
                            BufferBuilder.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + (float)k2 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)k2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F), (double)(f24 + (float)k2 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)k2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + (float)k2 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)k2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            BufferBuilder.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + (float)k2 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)k2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
                    }

                    tessellator.draw();
                }
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
	}

	public void updateClouds() {
		++this.rendererUpdateCount;
	}
}
