package com.xcompwiz.mystcraft.world;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkyRendererMyst extends IRenderHandler {

	private AgeController	controller;

	private int				glSkyList;
	private int				glSkyList2;

	private boolean			initialized;

	public SkyRendererMyst(WorldProviderMyst provider, AgeController controller) {
		this.controller = controller;
		this.initialized = false;
	}

	@SideOnly(Side.CLIENT)
	private void init() {
		if (this.initialized) {
			return;
		}
		this.initialized = true;

		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();

		this.glSkyList = GLAllocation.generateDisplayLists(2);
		GlStateManager.glNewList(this.glSkyList, GL11.GL_COMPILE);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		for (int k = -384; k <= 384; k += 64) {
			for (int l = -384; l <= 384; l += 64) {
				float f =  (float)  k;
				float f1 = (float) (k + 64);
				vb.pos((double)  f, 16D, (double) l)      .endVertex();
				vb.pos((double) f1, 16D, (double) l)      .endVertex();
				vb.pos((double) f1, 16D, (double)(l + 64)).endVertex();
				vb.pos((double)  f, 16D, (double)(l + 64)).endVertex();
			}
		}
		tes.draw();
		GlStateManager.glEndList();

		glSkyList2 = this.glSkyList + 1;
        GlStateManager.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {
                float f =  (float) (k + 64);
                float f1 = (float)  k;
                vb.pos((double)  f, -16D, (double) l)      .endVertex();
                vb.pos((double) f1, -16D, (double) l)      .endVertex();
                vb.pos((double) f1, -16D, (double)(l + 64)).endVertex();
                vb.pos((double)  f, -16D, (double)(l + 64)).endVertex();
            }
        }
        tes.draw();
        GlStateManager.glEndList();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		init();
		TextureManager renderEngine = mc.getTextureManager();

        GlStateManager.disableTexture2D();
        Vec3d vec3d = world.getSkyColor(mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity(), partialTicks);
        float f = (float)vec3d.xCoord;
        float f1 = (float)vec3d.yCoord;
        float f2 = (float)vec3d.zCoord;

        if (mc.gameSettings.anaglyph) {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        //GlStateManager.color(f, f1, f2);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(f, f1, f2);
        GlStateManager.callList(this.glSkyList);
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();

        controller.renderCelestials(renderEngine, world, partialTicks);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F);
        double horizonDst = mc.player.getPositionEyes(1F).yCoord - world.getHorizon();
        boolean renderHorizon = controller.shouldRenderHorizon();
        boolean renderVoid = controller.shouldRenderVoid();

        if(renderVoid) {
            if (horizonDst < 0.0D) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);
                GlStateManager.callList(this.glSkyList2);
                GlStateManager.popMatrix();
                float f19 = -((float)(horizonDst + 65.0D));
                vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                vertexbuffer.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
            }
        }
        if(renderHorizon) {
            GlStateManager.pushMatrix();
            GlStateManager.color(f, f1, f2);
            GlStateManager.translate(0.0F, -((float)(horizonDst - 16.0D)), 0.0F);
            GlStateManager.callList(this.glSkyList2);
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
	}
}
