package com.xcompwiz.mystcraft.integration.lookingglass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.xcompwiz.lookingglass.client.proxyworld.ProxyWorldManager;
import com.xcompwiz.lookingglass.client.proxyworld.WorldView;
import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.linking.DimensionUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Based on code from Ken Butler/shadowking97
 */
@SideOnly(Side.CLIENT)
public class DynamicLinkPanelRenderer implements ILinkPanelEffect {
	public static int	shaderARB;
	public static int	vertexARB;
	public static int	fragmentARB;

	public static int	textureLoc;
	public static int	damageLoc;
	public static int	resLoc;
	public static int	timeLoc;
	public static int	waveScaleLoc;
	public static int	colorScaleLoc;
	public static int	linkColorLoc;

	private WorldView	activeview;

	public DynamicLinkPanelRenderer() {}

	@Override
	public void render(int left, int top, int width, int height, ILinkInfo linkinfo, ItemStack bookclone) {
		if (linkinfo == null) return;
		Integer dimid = linkinfo.getDimensionUID();
		if (dimid == null) return;
		if (activeview != null) {
			if (activeview.worldObj.provider.dimensionId != dimid || !compareCoords(activeview.coords, linkinfo.getSpawn())) {
				ProxyWorldManager.freeView(activeview);
				activeview = null;
			}
		}
		if (activeview == null) {
			ChunkCoordinates spawn = linkinfo.getSpawn();
			activeview = ProxyWorldManager.getWorldView(dimid, spawn);
			if (activeview != null) activeview.grab();
		}
		if (activeview == null) return;
		int texture = activeview.texture;
		if (texture == 0) return;

		WorldClient proxyworld = ProxyWorldManager.getProxyworld(dimid);

		float bookDamage = 0;
		if (bookclone != null) bookDamage = ((float) bookclone.getItemDamageForDisplay()) / bookclone.getMaxDamage();

		activeview.last_world_time = proxyworld.getTotalWorldTime();

		if (OpenGlHelper.shadersSupported) {
			float sinceOpened = 0;
			if (activeview.openTime >= 0) sinceOpened = (Minecraft.getSystemTime() - activeview.openTime) * 0.0003f;

			int color = DimensionUtils.getLinkColor(linkinfo);
			float linkColorR = ((color >> 16) & 255) / 255F;
			float linkColorG = ((color >> 8) & 255) / 255F;
			float linkColorB = (color & 255) / 255F;

			activeview.waveScale += (proxyworld.rand.nextDouble() - 0.5d) / 10;
			if (activeview.waveScale > 1) activeview.waveScale = 1;
			if (activeview.waveScale < 0) activeview.waveScale = 0;
			activeview.colorScale += (proxyworld.rand.nextDouble() - 0.5d) / 10;
			if (activeview.colorScale > 1) activeview.colorScale = 1;
			if (activeview.colorScale < 0.5F) activeview.colorScale = 0.5F;

			ARBShaderObjects.glUseProgramObjectARB(shaderARB);

			ARBShaderObjects.glUniform1iARB(textureLoc, 14);
			GL13.glActiveTexture(GL13.GL_TEXTURE14);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

			ARBShaderObjects.glUniform1fARB(timeLoc, sinceOpened);
			ARBShaderObjects.glUniform2fARB(resLoc, width, height);
			ARBShaderObjects.glUniform1fARB(damageLoc, (bookDamage - 0.5f) * 2f);
			ARBShaderObjects.glUniform1fARB(waveScaleLoc, 0);
			ARBShaderObjects.glUniform1fARB(colorScaleLoc, activeview.colorScale);
			ARBShaderObjects.glUniform4fARB(linkColorLoc, linkColorR, linkColorG, linkColorB, 1f);

			GL13.glActiveTexture(GL13.GL_TEXTURE0);
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setColorRGBA_F(0, 0, 0, 1);
		tessellator.startDrawingQuads();
		if (OpenGlHelper.shadersSupported) {
			tessellator.addVertexWithUV(left, height + top, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(width + left, height + top, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(width + left, top, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(left, top, 0.0D, 0.0D, 0.0D);
		} else {
			tessellator.addVertexWithUV(left, top, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(width + left, top, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(width + left, height + top, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(left, height + top, 0.0D, 0.0D, 0.0D);
		}
		tessellator.draw();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		if (OpenGlHelper.shadersSupported) {
			ARBShaderObjects.glUseProgramObjectARB(0);

			GL13.glActiveTexture(GL13.GL_TEXTURE0);

		}
	}

	private boolean compareCoords(ChunkCoordinates coords, ChunkCoordinates spawn) {
		if (coords == spawn) return true;
		if (coords == null && spawn != null) return false;
		return coords.equals(spawn);
	}

	@Override
	public void onOpen() {}
}