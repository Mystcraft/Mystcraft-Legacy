package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.model.ModelWritingDesk;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.MapData;

public class RenderWritingDesk extends TileEntitySpecialRenderer<TileEntityDesk> {

	private ModelBook bookmodel;
	private ModelWritingDesk deskmodel;

	public RenderWritingDesk() {
		deskmodel = new ModelWritingDesk();
		bookmodel = new ModelBook();
	}

	@Override
	public void render(TileEntityDesk te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		int horizontalFacingIndex = te.getBlockMetadata() & 3;
		x += 0.5;
		z += 0.5;
		bindTexture(Entities.desk);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.5, z);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(90 * horizontalFacingIndex, 0, 1, 0);
		int tableItemCount = 0;
		int paperCount = te.getPaperCount();
		deskmodel.render(null, 0.0625F, te.hasTop(), tableItemCount, paperCount);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(-90 * horizontalFacingIndex, 0, 1, 0);
		renderItems(te);
		GlStateManager.popMatrix();
	}

	private void renderItems(TileEntityDesk desk) {
		/*
		 * for (int i = 0; i < 25; ++i) { ItemStack itemstack = desk.getStackInSlot(i+4); // desk.getDisplayItem(); if (itemstack != null) {
		 * GL11.glPushMatrix(); if (i < 8) GL11.glTranslatef(0.15F, 0.3F, -0.35F+0.1F*i); else if (i < 16) GL11.glTranslatef(-0.15F, 0.3F, -0.35F+0.1F*(i-8));
		 * else if (i < 24) GL11.glTranslatef(-0.45F, 0.3F, -0.35F+0.1F*(i-16)); else if (i == 24) { GL11.glTranslatef(-0.42F, 0.6F, 0.0F);
		 * GL11.glRotatef(90.0F, 1, 0, 0); } this.bindTexture(Assets.linkbook_tex); GL11.glScalef(0.8F, 0.8F, 0.8F); bookmodel.render(null, 0.0f, 0.0f, 0.0f,
		 * 0.005f Open , 0.0f, 0.0625F); GL11.glPopMatrix(); } }
		 */
		ItemStack itemstack = desk.getDisplayItem();
		if (!itemstack.isEmpty()) {
			if (itemstack.getItem() instanceof ItemLinking) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.15F, 1, 1);
				GlStateManager.rotate(90, 0, 0, 1);
				if (itemstack.getItem() == ModItems.agebook) {
					this.bindTexture(Entities.agebook);
				} else if (itemstack.getItem() == ModItems.linkbook) {
					this.bindTexture(Entities.linkbook);
				}
				GlStateManager.scale(0.8, 0.8, 0.8);
				bookmodel.render(null, 0.0f, 0.0f, 0.0f, 1.22f /* Open */, 0.0f, 0.0625F);
				GlStateManager.popMatrix();
				return;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.35F, 1.001F, 1F);
			GlStateManager.scale(0.7F, 0.7F, 0.7F);
			GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);

			if (itemstack.getItem() == Items.FILLED_MAP) {
				this.bindTexture(Vanilla.map_background);
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder vb = tes.getBuffer();
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(0.006F, 0.006F, 0.006F);
				GlStateManager.translate(-65.0F, -107.0F, -3.0F);
				GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F);
				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				byte var5 = 7;
				vb.pos((0 - var5), (128 + var5), 0.0D).tex(0.0D, 1.0D).endVertex();
				vb.pos((128 + var5), (128 + var5), 0.0D).tex(1.0D, 1.0D).endVertex();
				vb.pos((128 + var5), (0 - var5), 0.0D).tex(1.0D, 0.0D).endVertex();
				vb.pos((0 - var5), (0 - var5), 0.0D).tex(0.0D, 0.0D).endVertex();
				tes.draw();
				MapData mapdata = Items.FILLED_MAP.getMapData(itemstack, desk.getWorld());

				if (mapdata != null) {
					Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
				}
			} else {
				GlStateManager.translate(0, 0.3, 0.02);
				GlStateManager.scale(0.7F, 0.7F, 0.7F);
				GlStateManager.pushAttrib();
				RenderHelper.enableStandardItemLighting();
				Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.popAttrib();
			}

			GlStateManager.popMatrix();
		}
	}

}
