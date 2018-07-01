package com.xcompwiz.mystcraft.integration.lookingglass;

import java.util.Random;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.xcompwiz.lookingglass.api.animator.CameraAnimatorPivot;
import com.xcompwiz.lookingglass.api.view.IWorldView;
import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.linking.DimensionUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Based on code from Ken Butler/shadowking97
 */
@SideOnly(Side.CLIENT)
public class DynamicLinkPanelRenderer implements ILinkPanelEffect {
	private final ILookingGlassWrapper apiinst;
	private Random rand;

	public static int shaderARB;
	public static int vertexARB;
	public static int fragmentARB;

	public static int textureLoc;
	public static int damageLoc;
	public static int resLoc;
	public static int timeLoc;
	public static int waveScaleLoc;
	public static int colorScaleLoc;
	public static int linkColorLoc;

	private Integer activeDim;
	private BlockPos activeCoords;
	private IWorldView activeview;
	public float colorScale = 0.5f;
	public float waveScale = 0.5f;
	private long readyTime;
	private boolean ready;

	public DynamicLinkPanelRenderer(ILookingGlassWrapper apiinst) {
		this.apiinst = apiinst;
		this.rand = new Random();
	}

	@Override
	public void render(int left, int top, int width, int height, ILinkInfo linkinfo, ItemStack bookclone) {
		if (activeview != null && (detectLinkInfoChange(linkinfo))) {
			apiinst.release(activeview);
			activeview = null;
		}
		if (linkinfo == null)
			return;
		Integer dimid = linkinfo.getDimensionUID();
		if (dimid == null)
			return;
		if (activeview == null) {
			BlockPos spawn = linkinfo.getSpawn();
			activeview = apiinst.createWorldView(dimid, spawn, 132, 83);
			if (activeview != null) {
				activeview.setAnimator(new CameraAnimatorPivot(activeview.getCamera()));
				this.activeDim = dimid;
				this.activeCoords = linkinfo.getSpawn();
			}
			colorScale = 0.5f;
			waveScale = 0.5f;
			readyTime = -1;
			ready = false;
		}
		if (activeview == null)
			return;
		int texture = activeview.getTexture();
		if (texture == 0)
			return;

		float bookDamage = 0;
		if (bookclone != null)
			bookDamage = ((float) bookclone.getItemDamage()) / bookclone.getMaxDamage();

		boolean useshaders = OpenGlHelper.shadersSupported;
		if (useshaders) {
			float sinceOpened = 0;
			if (!ready && activeview.isReady()) {
				ready = true;
				readyTime = Minecraft.getSystemTime();
			}
			if (readyTime >= 0)
				sinceOpened = (Minecraft.getSystemTime() - readyTime) * 0.0003f;

			int color = DimensionUtils.getLinkColor(linkinfo);
			float linkColorR = ((color >> 16) & 255) / 255F;
			float linkColorG = ((color >> 8) & 255) / 255F;
			float linkColorB = (color & 255) / 255F;

			waveScale += (rand.nextDouble() - 0.5d) / 10;
			if (waveScale > 1)
				waveScale = 1;
			if (waveScale < 0)
				waveScale = 0;
			colorScale += (rand.nextDouble() - 0.5d) / 10;
			if (colorScale > 1)
				colorScale = 1;
			if (colorScale < 0.5F)
				colorScale = 0.5F;

			ARBShaderObjects.glUseProgramObjectARB(shaderARB);

			ARBShaderObjects.glUniform1iARB(textureLoc, 14);
			GL13.glActiveTexture(GL13.GL_TEXTURE14);
			GlStateManager.bindTexture(texture);

			ARBShaderObjects.glUniform1fARB(timeLoc, sinceOpened);
			ARBShaderObjects.glUniform2fARB(resLoc, width, height);
			ARBShaderObjects.glUniform1fARB(damageLoc, (bookDamage - 0.5f) * 2f);
			ARBShaderObjects.glUniform1fARB(waveScaleLoc, 0);
			ARBShaderObjects.glUniform1fARB(colorScaleLoc, colorScale);
			ARBShaderObjects.glUniform4fARB(linkColorLoc, linkColorR, linkColorG, linkColorB, 1f);

			GL13.glActiveTexture(GL13.GL_TEXTURE0);
		} else if (!activeview.isReady()) {
			//TODO: if not ready and shaders off render black
			return;
		}

		Tessellator tes = Tessellator.getInstance();
		BufferBuilder vb = tes.getBuffer();

		GlStateManager.bindTexture(texture);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		if (useshaders) { //TODO: The shaders are somehow flipping the texture upside down
			vb.pos(left, height + top, 0.0D).tex(0.0D, 1.0D).endVertex();
			vb.pos(width + left, height + top, 0.0D).tex(1.0D, 1.0D).endVertex();
			vb.pos(width + left, top, 0.0D).tex(1.0D, 0.0D).endVertex();
			vb.pos(left, top, 0.0D).tex(0.0D, 0.0D).endVertex();
		} else {
			vb.pos(left, height + top, 0.0D).tex(0.0D, 0.0D).endVertex();
			vb.pos(width + left, height + top, 0.0D).tex(1.0D, 0.0D).endVertex();
			vb.pos(width + left, top, 0.0D).tex(1.0D, 1.0D).endVertex();
			vb.pos(left, top, 0.0D).tex(0.0D, 1.0D).endVertex();
		}
		tes.draw();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		if (useshaders) {
			ARBShaderObjects.glUseProgramObjectARB(0);
		}
		activeview.markDirty();
	}

	private boolean detectLinkInfoChange(ILinkInfo linkinfo) {
		if (this.activeDim == null && linkinfo == null)
			return false;
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

	private boolean compareCoords(BlockPos coords, BlockPos spawn) {
		if (coords == spawn)
			return true;
		if (coords == null && spawn != null)
			return false;
		return coords.equals(spawn);
	}

	@Override
	public void onOpen() {
		readyTime = -1;
		ready = false;
	}
}
