package com.xcompwiz.mystcraft.client.render;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.model.ModelLectern;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;

public class RenderLectern extends TileEntitySpecialRenderer {

	private ModelBook		book;
	private ModelLectern	lectern;

	public RenderLectern() {
		lectern = new ModelLectern();
		book = new ModelBook();
	}

	public void render(TileEntityLectern tileentity, double d, double d1, double d2, float f) {
		d += 0.5;
		d2 += 0.5;
		this.bindTexture(Entities.lectern);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glRotatef(90 - tileentity.getYaw(), 0, 1, 0);
		lectern.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625F);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		renderItem(tileentity);
		GL11.glPopMatrix();

		if (Mystcraft.renderlabels && Mystcraft.serverLabels && tileentity.getDisplayItem() != null && tileentity.getDisplayItem().getItem() instanceof ItemLinking) renderLabel(tileentity, tileentity.getBookTitle(), d, d1 + 1.25F, d2, 25);
	}

	protected void renderLabel(TileEntity entity, String s, double d, double d1, double d2, int i) {
		if (s == null) return;
		double f = entity.getDistanceFrom(field_147501_a.field_147560_j, field_147501_a.field_147561_k, field_147501_a.field_147558_l);
		if (f > i) { return; }
		FontRenderer fontrenderer = func_147498_b();
		float f1 = 1.6F;
		float f2 = 0.01666667F * f1;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-field_147501_a.field_147562_h, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(field_147501_a.field_147563_i, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f2, -f2, f2);
		GL11.glDisable(2896 /* GL_LIGHTING */);
		GL11.glDepthMask(false);
		GL11.glDisable(2929 /* GL_DEPTH_TEST */);
		GL11.glEnable(3042 /* GL_BLEND */);
		GL11.glBlendFunc(770, 771);
		Tessellator tessellator = Tessellator.instance;
		byte byte0 = 0;
		GL11.glDisable(3553 /* GL_TEXTURE_2D */);
		tessellator.startDrawingQuads();
		int j = fontrenderer.getStringWidth(s) / 2;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
		tessellator.addVertex(-j - 1, -1 + byte0, 0.0D);
		tessellator.addVertex(-j - 1, 8 + byte0, 0.0D);
		tessellator.addVertex(j + 1, 8 + byte0, 0.0D);
		tessellator.addVertex(j + 1, -1 + byte0, 0.0D);
		tessellator.draw();
		GL11.glEnable(3553 /* GL_TEXTURE_2D */);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, 0x20ffffff);
		GL11.glEnable(2929 /* GL_DEPTH_TEST */);
		GL11.glDepthMask(true);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, -1);
		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glDisable(3042 /* GL_BLEND */);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityLectern lectern) {
		ItemStack itemstack = lectern.getDisplayItem();

		if (itemstack != null) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0.255F, 0);
			GL11.glRotatef(90.0F - lectern.getYaw(), 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);

			if (itemstack.getItem() instanceof ItemLinking) {
				if (itemstack.getItem() == ModItems.agebook) {
					this.bindTexture(Entities.agebook);
				} else if (itemstack.getItem() == ModItems.linkbook) {
					this.bindTexture(Entities.linkbook);
				}
				GL11.glScalef(0.8F, 0.8F, 0.8F);
				book.render(null, 0.0f, 0.0f, 0.0f, 1.22f /* Open */, 0.0f, 0.0625F);
				GL11.glPopMatrix();
				return;
			}

			GL11.glTranslatef(0, 0.2F, 0);
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			EntityItem entity = new EntityItem(lectern.getWorldObj(), 0.0D, 0.0D, 0.0D, itemstack);
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
				MapData mapdata = Items.filled_map.getMapData(entity.getEntityItem(), lectern.getWorldObj());

				if (mapdata != null) {
					Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().func_148250_a(mapdata, true);
				}
			} else {
				if (entity.getEntityItem().getItem() == Items.compass) {
					TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
					texturemanager.bindTexture(TextureMap.locationItemsTexture);
					TextureAtlasSprite textureatlassprite = ((TextureMap) texturemanager.getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entity.getEntityItem()).getIconName());

					if (textureatlassprite instanceof TextureCompass) {
						TextureCompass texturecompass = (TextureCompass) textureatlassprite;
						double d0 = texturecompass.currentAngle;
						double d1 = texturecompass.angleDelta;
						texturecompass.currentAngle = 0.0D;
						texturecompass.angleDelta = 0.0D;
						texturecompass.updateCompass(lectern.getWorldObj(), lectern.xCoord, lectern.zCoord, MathHelper.wrapAngleTo180_float((180 + lectern.getYaw())), false, true);
						texturecompass.currentAngle = d0;
						texturecompass.angleDelta = d1;
					}
				}

				RenderItem.renderInFrame = true;
				RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
				RenderItem.renderInFrame = false;

				if (entity.getEntityItem().getItem() == Items.compass) {
					TextureAtlasSprite textureatlassprite1 = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entity.getEntityItem()).getIconName());

					if (textureatlassprite1.getFrameCount() > 0) {
						textureatlassprite1.updateAnimation();
					}
				}
			}

			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		render((TileEntityLectern) tileentity, d, d1, d2, f);
	}
}
