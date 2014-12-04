package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.MapData;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.model.ModelWritingDesk;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

public class RenderWritingDesk extends TileEntitySpecialRenderer {

	private ModelBook			bookmodel;
	private ModelWritingDesk	deskmodel;

	public RenderWritingDesk() {
		deskmodel = new ModelWritingDesk();
		bookmodel = new ModelBook();
	}

	public void render(TileEntityDesk desk, double d, double d1, double d2, float f) {
		int meta = desk.getBlockMetadata() & 3;
		d += 0.5;
		d2 += 0.5;
		this.bindTexture(Entities.desk);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1 + 1.5F, (float) d2);
		GL11.glRotatef(90, 1, 0, 0);
		GL11.glRotatef(90, 0, 1, 0);
		GL11.glRotatef(90, 0, 0, 1);
		GL11.glRotatef(90 * meta, 0, 1, 0);
		int tabitemcount = 0;
		int papercount = desk.getPaperCount();
		deskmodel.render(null, 0.0625F, desk.hasTop(), tabitemcount, papercount);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glRotatef(-90 * meta, 0, 1, 0);
		renderItems(desk);
		GL11.glPopMatrix();
	}

	private void renderItems(TileEntityDesk desk) {
		/*
		 * for (int i = 0; i < 25; ++i) { ItemStack itemstack = desk.getStackInSlot(i+4); // desk.getDisplayItem(); if (itemstack != null) {
		 * GL11.glPushMatrix(); if (i < 8) GL11.glTranslatef(0.15F, 0.3F, -0.35F+0.1F*i); else if (i < 16) GL11.glTranslatef(-0.15F, 0.3F, -0.35F+0.1F*(i-8));
		 * else if (i < 24) GL11.glTranslatef(-0.45F, 0.3F, -0.35F+0.1F*(i-16)); else if (i == 24) { GL11.glTranslatef(-0.42F, 0.6F, 0.0F);
		 * GL11.glRotatef(90.0F, 1, 0, 0); } this.bindTexture(Assets.linkbook_tex); GL11.glScalef(0.8F, 0.8F, 0.8F); bookmodel.render(null, 0.0f, 0.0f, 0.0f,
		 * 0.005f Open , 0.0f, 0.0625F); GL11.glPopMatrix(); } }
		 */
		ItemStack itemstack = desk.getDisplayItem(); // desk.getDisplayItem();
		if (itemstack != null) {
			if (itemstack.getItem() instanceof ItemLinking) {
				GL11.glPushMatrix();
				GL11.glTranslatef(-0.15F, 1, 1);
				GL11.glRotatef(90.0F, 0, 0, 1);
				if (itemstack.getItem() == ModItems.agebook) {
					this.bindTexture(Entities.agebook);
				} else if (itemstack.getItem() == ModItems.linkbook) {
					this.bindTexture(Entities.linkbook);
				}
				GL11.glScalef(0.8F, 0.8F, 0.8F);
				bookmodel.render(null, 0.0f, 0.0f, 0.0f, 1.22f /* Open */, 0.0f, 0.0625F);
				GL11.glPopMatrix();
				return;
			}

			GL11.glPushMatrix();
			GL11.glTranslatef(-0.35F, 1.001F, 1F);
			GL11.glScalef(0.7F, 0.7F, 0.7F);
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			EntityItem entity = new EntityItem(desk.getWorldObj(), 0.0D, 0.0D, 0.0D, itemstack);
			entity.getEntityItem().stackSize = 1;
			entity.hoverStart = 0.0F;

			if (entity.getEntityItem().getItem() == Items.filled_map) {
				this.bindTexture(Vanilla.map_background);
				Tessellator var4 = Tessellator.instance;
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GL11.glScalef(0.006F, 0.006F, 0.006F);
				GL11.glTranslatef(-65.0F, -107.0F, -3.0F);
				GL11.glNormal3f(0.0F, 0.0F, -1.0F);
				var4.startDrawingQuads();
				byte var5 = 7;
				var4.addVertexWithUV((0 - var5), (128 + var5), 0.0D, 0.0D, 1.0D);
				var4.addVertexWithUV((128 + var5), (128 + var5), 0.0D, 1.0D, 1.0D);
				var4.addVertexWithUV((128 + var5), (0 - var5), 0.0D, 1.0D, 0.0D);
				var4.addVertexWithUV((0 - var5), (0 - var5), 0.0D, 0.0D, 0.0D);
				var4.draw();
				MapData mapdata = Items.filled_map.getMapData(entity.getEntityItem(), desk.getWorldObj());

				if (mapdata != null) {
					Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().func_148250_a(mapdata, true);
				}
			} else {
				RenderItem.renderInFrame = true;
				RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
				RenderItem.renderInFrame = false;
			}

			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		render((TileEntityDesk) tileentity, d, d1, d2, f);
	}
}
