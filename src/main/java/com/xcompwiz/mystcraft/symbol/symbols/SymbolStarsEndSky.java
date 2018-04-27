package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

public class SymbolStarsEndSky extends SymbolBase {

	public SymbolStarsEndSky(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller, .156F, .156F, .156F); // 2631720 = 0010 1000 0010 1000 0010 1000 = #282828
		controller.registerInterface(new SkyBackground(controller, seed, gradient));
		controller.setHorizon(0);
		controller.setDrawHorizon(false);
		controller.setDrawVoid(false);
	}

    @Override
    public boolean generatesConfigOption() {
        return true;
    }

	private static class SkyBackground extends CelestialBase {

		private ColorGradient	gradient;
		private AgeDirector		controller;

		SkyBackground(AgeDirector controller, long seed, ColorGradient gradient) {
			this.controller = controller;
			this.gradient = gradient;
		}

		@Override
		public void render(TextureManager textureManager, World worldObj, float partial) {
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder vb = tes.getBuffer();

			Color color = gradient.getColor(controller.getTime() / 12000F);
            java.awt.Color awt = color.toAWT();

            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.depthMask(false);
            textureManager.bindTexture(Vanilla.end_sky);

            for (int i = 0; i < 6; ++i) {
                GlStateManager.pushMatrix();

                if (i == 1) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 2) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 3) {
                    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 4) {
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (i == 5) {
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                vb.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                vb.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(awt.getRed(), awt.getGreen(), awt.getBlue(), 255).endVertex();
                vb.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(awt.getRed(), awt.getGreen(), awt.getBlue(), 255).endVertex();
                vb.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(awt.getRed(), awt.getGreen(), awt.getBlue(), 255).endVertex();
                vb.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(awt.getRed(), awt.getGreen(), awt.getBlue(), 255).endVertex();
                tes.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
		}
	}
}
