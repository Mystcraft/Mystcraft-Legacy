package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolStarsEndSky extends SymbolBase {

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller, .156F, .156F, .156F); // 2631720 = 0010 1000 0010 1000 0010 1000 = #282828
		controller.registerInterface(new SkyBackground(controller, seed, gradient));
		controller.setHorizon(0);
		controller.setDrawHorizon(false);
		controller.setDrawVoid(false);
	}

	@Override
	public String identifier() {
		return "StarsEndSky";
	}

	private static class SkyBackground extends CelestialBase {

		private ColorGradient	gradient;
		private AgeDirector	controller;

		SkyBackground(AgeDirector controller, long seed, ColorGradient gradient) {
			this.controller = controller;
			this.gradient = gradient;
		}

		@Override
		public void render(TextureManager textureManager, World worldObj, float partial) {
			Tessellator var21 = Tessellator.instance;
			int iColor = 0;
			Color color = gradient.getColor(controller.getTime() / 12000F);
			iColor = color.asInt();

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_FOG);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderHelper.disableStandardItemLighting();
			GL11.glDepthMask(false);

			textureManager.bindTexture(Vanilla.end_sky);

			for (int i = 0; i < 6; ++i) {
				GL11.glPushMatrix();

				if (i == 1) {
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (i == 2) {
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (i == 3) {
					GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
				}

				if (i == 4) {
					GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
				}

				if (i == 5) {
					GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				}

				var21.startDrawingQuads();
				var21.setColorOpaque_I(iColor);
				var21.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
				var21.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
				var21.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
				var21.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
				var21.draw();
				GL11.glPopMatrix();
			}

			// GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
	}
}
