package com.xcompwiz.mystcraft.client.render;

import com.xcompwiz.mystcraft.block.BlockBookReceptacle;
import com.xcompwiz.mystcraft.data.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RenderBookReceptacle extends TileEntitySpecialRenderer<TileEntityBookReceptacle> {

	private ModelBook	bookmodel;

	public RenderBookReceptacle() {
		bookmodel = new ModelBook();
	}

	@Override
	public void render(TileEntityBookReceptacle te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		x += 0.5;
		z += 0.5;
		ItemStack display = te.getDisplayItem();
		if(display.isEmpty()) {
			return;
		}
		if (display.getItem() == ModItems.agebook) {
			bindTexture(Entities.agebook);
		} else if (display.getItem() == ModItems.linkbook) {
			bindTexture(Entities.linkbook);
		} else {
			//TODO: API Allow IItemPortalActivator items to control rendering somehow (IItemPortalActivator2?)
			bindTexture(Entities.linkbook);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.5, z);
		IBlockState state = ModBlocks.receptacle.getStateFromMeta(te.getBlockMetadata());
		EnumFacing rotation = state.getValue(BlockBookReceptacle.ROTATION);
		switch (rotation) {
			case DOWN:
				break;
			case UP:
				GlStateManager.rotate(-90, 1, 0, 0);
				GlStateManager.rotate(90, 0, 1, 0);
				break;
			case NORTH:
				GlStateManager.rotate(-90, 0, 1, 0);
				break;
			case SOUTH:
				GlStateManager.rotate(90, 0, 1, 0);
				break;
			case WEST:
				break;
			case EAST:
				GlStateManager.rotate(180, 0, 1, 0);
				break;
		}
		GlStateManager.rotate(te.getPitch(), 1, 0, 0);
		GlStateManager.rotate(te.getYaw(), 0, 1, 0);
		GlStateManager.scale(0.8F, 0.8F, 0.8F);
		bookmodel.render(null, 0.1f, 0.1f, 0.1f, 0.005f /* Closed */, 0.0f, 0.0625F);
		GlStateManager.popMatrix();
		if (Mystcraft.renderlabels && Mystcraft.serverLabels) {
			renderLabel(te, te.getBookTitle(), x, y + 1.25F, z, 25);
		}
	}

	protected void renderLabel(TileEntity entity, String s, double x, double y, double z, int maxDst) {
		if (s == null || s.isEmpty()) {
			return;
		}
		double f = entity.getDistanceSq(rendererDispatcher.entityX, rendererDispatcher.entityY, rendererDispatcher.entityZ);
		f = MathHelper.sqrt(f); //Because squaredDst
		if (f > maxDst) {
			return;
		}
		FontRenderer fontrenderer = getFontRenderer();
		float f1 = 1.6F;
		float f2 = 0.01666667F * f1;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-rendererDispatcher.entityYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(rendererDispatcher.entityPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-f2, -f2, f2);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder vb = tes.getBuffer();
		byte byte0 = 0;
		int j = fontrenderer.getStringWidth(s) / 2;
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		vb.pos(-j - 1, -1 + byte0, 0.0D).color(0, 0, 0, 0.25F).endVertex();
		vb.pos(-j - 1,  8 + byte0, 0.0D).color(0, 0, 0, 0.25F).endVertex();
		vb.pos( j + 1,  8 + byte0, 0.0D).color(0, 0, 0, 0.25F).endVertex();
		vb.pos( j + 1, -1 + byte0, 0.0D).color(0, 0, 0, 0.25F).endVertex();
		tes.draw();
		GlStateManager.enableTexture2D();
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, 0x20ffffff);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}

}
