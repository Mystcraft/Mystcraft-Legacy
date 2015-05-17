package com.xcompwiz.mystcraft.integration.lookingglass;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.xcompwiz.lookingglass.api.ILookingGlassAPI;
import com.xcompwiz.lookingglass.api.view.IWorldView;
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
	private final ILookingGlassAPI	apiinst;
	private Random					rand;

	public static int				shaderARB;
	public static int				vertexARB;
	public static int				fragmentARB;

	public static int				textureLoc;
	public static int				damageLoc;
	public static int				resLoc;
	public static int				timeLoc;
	public static int				waveScaleLoc;
	public static int				colorScaleLoc;
	public static int				linkColorLoc;

	private Integer					activeDim;
	private ChunkCoordinates		activeCoords;
	private IWorldView				activeview;
	public float					colorScale	= 0.5f;
	public float					waveScale	= 0.5f;
	private long					readyTime;
	private boolean					ready;

	public DynamicLinkPanelRenderer(ILookingGlassAPI apiinst) {
		this.apiinst = apiinst;
		this.rand = new Random();
	}

	@Override
	public void render(int left, int top, int width, int height, ILinkInfo linkinfo, ItemStack bookclone) {
		if (activeview != null && (detectLinkInfoChange(linkinfo))) {
			activeview.release();
			activeview = null;
		}
		if (linkinfo == null) return;
		Integer dimid = linkinfo.getDimensionUID();
		if (dimid == null) return;
		if (activeview == null) {
			ChunkCoordinates spawn = linkinfo.getSpawn();
			activeview = apiinst.createWorldView(dimid, spawn, 132, 83); //FIXME: Is this editing the passed in ChunkCoordinates object?
			if (activeview != null) {
				activeview.grab();
				apiinst.setPivotAnimation(activeview);
				this.activeDim = dimid;
				this.activeCoords = linkinfo.getSpawn();
			}
			colorScale = 0.5f;
			waveScale = 0.5f;
			readyTime = -1;
			ready = false;
		}
		if (activeview == null) return;
		int texture = activeview.getTexture();
		if (texture == 0) return;

		float bookDamage = 0;
		if (bookclone != null) bookDamage = ((float) bookclone.getItemDamageForDisplay()) / bookclone.getMaxDamage();

		activeview.markDirty();

		if (OpenGlHelper.shadersSupported) {
			float sinceOpened = 0;
			if (!ready && activeview.isReady()) {
				ready = true;
				readyTime = Minecraft.getSystemTime();
			}
			if (readyTime >= 0) sinceOpened = (Minecraft.getSystemTime() - readyTime) * 0.0003f;

			int color = DimensionUtils.getLinkColor(linkinfo);
			float linkColorR = ((color >> 16) & 255) / 255F;
			float linkColorG = ((color >> 8) & 255) / 255F;
			float linkColorB = (color & 255) / 255F;

			waveScale += (rand.nextDouble() - 0.5d) / 10;
			if (waveScale > 1) waveScale = 1;
			if (waveScale < 0) waveScale = 0;
			colorScale += (rand.nextDouble() - 0.5d) / 10;
			if (colorScale > 1) colorScale = 1;
			if (colorScale < 0.5F) colorScale = 0.5F;

			ARBShaderObjects.glUseProgramObjectARB(shaderARB);

			ARBShaderObjects.glUniform1iARB(textureLoc, 14);
			GL13.glActiveTexture(GL13.GL_TEXTURE14);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

			ARBShaderObjects.glUniform1fARB(timeLoc, sinceOpened);
			ARBShaderObjects.glUniform2fARB(resLoc, width, height);
			ARBShaderObjects.glUniform1fARB(damageLoc, (bookDamage - 0.5f) * 2f);
			ARBShaderObjects.glUniform1fARB(waveScaleLoc, 0);
			ARBShaderObjects.glUniform1fARB(colorScaleLoc, colorScale);
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
		}
	}

	private boolean detectLinkInfoChange(ILinkInfo linkinfo) {
		if (this.activeDim == null && linkinfo == null) return false;
		if (this.activeDim != null && linkinfo == null) {
			this.activeDim = null;
			this.activeCoords = null;
			return true;
		}
		if (this.activeDim != linkinfo.getDimensionUID() || !compareCoords(this.activeCoords, linkinfo.getSpawn())) {
			this.activeDim = null;
			this.activeCoords = null;
			return true;
		}
		return false;
	}

	private boolean compareCoords(ChunkCoordinates coords, ChunkCoordinates spawn) {
		if (coords == spawn) return true;
		if (coords == null && spawn != null) return false;
		return coords.equals(spawn);
	}

	@Override
	public void onOpen() {
		readyTime = -1;
		ready = false;
	}
}
