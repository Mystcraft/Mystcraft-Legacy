package com.xcompwiz.mystcraft.world;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Vec3;
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
		if (this.initialized) return;
		this.initialized = true;
		Tessellator var5 = Tessellator.instance;
		this.glSkyList = GLAllocation.generateDisplayLists(2);
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		byte var7 = 64;
		int var8 = 256 / var7 + 2;
		float var6 = 16.0F;
		int var9;
		int var10;

		for (var9 = -var7 * var8; var9 <= var7 * var8; var9 += var7) {
			for (var10 = -var7 * var8; var10 <= var7 * var8; var10 += var7) {
				var5.startDrawingQuads();
				var5.addVertex((var9 + 0), var6, (var10 + 0));
				var5.addVertex((var9 + var7), var6, (var10 + 0));
				var5.addVertex((var9 + var7), var6, (var10 + var7));
				var5.addVertex((var9 + 0), var6, (var10 + var7));
				var5.draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.glSkyList + 1;
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		var6 = -16.0F;
		var5.startDrawingQuads();

		for (var9 = -var7 * var8; var9 <= var7 * var8; var9 += var7) {
			for (var10 = -var7 * var8; var10 <= var7 * var8; var10 += var7) {
				var5.addVertex((var9 + var7), var6, (var10 + 0));
				var5.addVertex((var9 + 0), var6, (var10 + 0));
				var5.addVertex((var9 + 0), var6, (var10 + var7));
				var5.addVertex((var9 + var7), var6, (var10 + var7));
			}
		}

		var5.draw();
		GL11.glEndList();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		init();
		TextureManager renderEngine = mc.getTextureManager();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Vec3 var2 = world.getSkyColor(mc.renderViewEntity, partialTicks);
		float skyRed = (float) var2.xCoord;
		float skyGreen = (float) var2.yCoord;
		float skyBlue = (float) var2.zCoord;

		if (mc.gameSettings.anaglyph) {
			float red = (skyRed * 30.0F + skyGreen * 59.0F + skyBlue * 11.0F) / 100.0F;
			float green = (skyRed * 30.0F + skyGreen * 70.0F) / 100.0F;
			float blue = (skyRed * 30.0F + skyBlue * 70.0F) / 100.0F;
			skyRed = red;
			skyGreen = green;
			skyBlue = blue;
		}

		// Sky color base
		GL11.glColor3f(skyRed, skyGreen, skyBlue);
		Tessellator tessellator = Tessellator.instance;
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glColor3f(skyRed, skyGreen, skyBlue);
		GL11.glCallList(this.glSkyList);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		controller.renderCelestials(renderEngine, world, partialTicks);

		// Draw horizon cutoff
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.0F, 0.0F, 0.0F);

		double horizonDist = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();
		boolean renderHorizon = controller.shouldRenderHorizon();
		boolean renderVoid = controller.shouldRenderVoid();

		if (renderVoid) {
			if (horizonDist < 0.0D) {
				GL11.glPushMatrix();
				GL11.glTranslatef(0.0F, 12.0F, 0.0F);
				GL11.glCallList(this.glSkyList2);
				GL11.glPopMatrix();
				float var10 = 1.0F;
				float var11 = -((float) (horizonDist + 65.0D));
				float var12 = -var10;
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA_I(0, 255);
				tessellator.addVertex((-var10), var11, var10);
				tessellator.addVertex(var10, var11, var10);
				tessellator.addVertex(var10, var12, var10);
				tessellator.addVertex((-var10), var12, var10);
				tessellator.addVertex((-var10), var12, (-var10));
				tessellator.addVertex(var10, var12, (-var10));
				tessellator.addVertex(var10, var11, (-var10));
				tessellator.addVertex((-var10), var11, (-var10));
				tessellator.addVertex(var10, var12, (-var10));
				tessellator.addVertex(var10, var12, var10);
				tessellator.addVertex(var10, var11, var10);
				tessellator.addVertex(var10, var11, (-var10));
				tessellator.addVertex((-var10), var11, (-var10));
				tessellator.addVertex((-var10), var11, var10);
				tessellator.addVertex((-var10), var12, var10);
				tessellator.addVertex((-var10), var12, (-var10));
				tessellator.addVertex((-var10), var12, (-var10));
				tessellator.addVertex((-var10), var12, var10);
				tessellator.addVertex(var10, var12, var10);
				tessellator.addVertex(var10, var12, (-var10));
				tessellator.draw();
			}
		}
		if (renderHorizon) {
			GL11.glColor3f(skyRed, skyGreen, skyBlue);
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, -((float) (horizonDist + 16.0D)), 0.0F);
			GL11.glCallList(this.glSkyList2);
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
	}
}
