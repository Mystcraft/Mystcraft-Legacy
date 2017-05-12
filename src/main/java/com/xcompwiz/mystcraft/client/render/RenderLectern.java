package com.xcompwiz.mystcraft.client.render;

import com.xcompwiz.mystcraft.block.BlockLectern;
import com.xcompwiz.mystcraft.data.ModBlocks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;

public class RenderLectern extends TileEntitySpecialRenderer<TileEntityLectern> {

	private ModelBook		book;
	private ModelLectern	lectern;

	public RenderLectern() {
		lectern = new ModelLectern();
		book = new ModelBook();
	}

	@Override
	public void renderTileEntityAt(TileEntityLectern te, double x, double y, double z, float partialTicks, int destroyStage) {
		x += 0.5;
		z += 0.5;
		bindTexture(Entities.lectern);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		EnumFacing teRotation = ModBlocks.lectern.getStateFromMeta(te.getBlockMetadata()).getValue(BlockLectern.ROTATION);
		if(teRotation.getAxis() == EnumFacing.Axis.Z) teRotation = teRotation.getOpposite();
		GlStateManager.rotate(teRotation.getHorizontalAngle() + 90, 0, 1, 0);
		lectern.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625F);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(teRotation.getHorizontalAngle() + 90, 0, 1, 0);
		renderItem(te);
		GlStateManager.popMatrix();

		if(Mystcraft.renderlabels && Mystcraft.serverLabels && !te.getDisplayItem().isEmpty() && te.getDisplayItem().getItem() instanceof ItemLinking) {
			renderLabel(te, te.getBookTitle(), x, y + 1.25F, z, 25);
		}
	}

	private void renderLabel(TileEntity entity, String s, double x, double y, double z, int maxDst) {
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
		VertexBuffer vb = tes.getBuffer();
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

	private void renderItem(TileEntityLectern lectern) {
		ItemStack itemstack = lectern.getDisplayItem();

		if (!itemstack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.255F, 0);
			GlStateManager.rotate(110.0F, 0.0F, 0.0F, 1.0F);

			if (itemstack.getItem() instanceof ItemLinking) {
				if (itemstack.getItem() == ModItems.agebook) {
					this.bindTexture(Entities.agebook);
				} else if (itemstack.getItem() == ModItems.linkbook) {
					this.bindTexture(Entities.linkbook);
				}
				GlStateManager.scale(0.8F, 0.8F, 0.8F);
				book.render(null, 0.0f, 0.0f, 0.0f, 1.22f /* Open */, 0.0f, 0.0625F);
				GlStateManager.popMatrix();
				return;
			}

			GlStateManager.translate(0, 0.2F, 0);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);

			if (itemstack.getItem() == Items.FILLED_MAP) {
				this.bindTexture(Vanilla.map_background);
				Tessellator tes = Tessellator.getInstance();
				VertexBuffer vb = tes.getBuffer();
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(0.006F, 0.006F, 0.006F);
				GlStateManager.translate(-65.0F, -107.0F, -3.0F);
				GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F);
				byte var5 = 7;
				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vb.pos((0 - var5),   (128 + var5), 0.0D).tex(0.0D, 1.0D).endVertex();
				vb.pos((128 + var5), (128 + var5), 0.0D).tex(1.0D, 1.0D).endVertex();
				vb.pos((128 + var5), (0 - var5),   0.0D).tex(1.0D, 0.0D).endVertex();
				vb.pos((0 - var5),   (0 - var5),   0.0D).tex(0.0D, 0.0D).endVertex();
				tes.draw();
				MapData mapdata = Items.FILLED_MAP.getMapData(itemstack, lectern.getWorld());

				if (mapdata != null) {
					Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
				}
			} else {
			    //Hellfire> compass *should* update its animation automatically since it's dynamically calculated upon model retrieval
				//if (itemstack.getItem().equals(Items.COMPASS)) {
				//	TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
				//	texturemanager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				//	TextureAtlasSprite textureatlassprite = ((TextureMap) texturemanager.getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entity.getEntityItem()).getIconName());

				//	if (textureatlassprite instanceof TextureCompass) {
				//		TextureCompass texturecompass = (TextureCompass) textureatlassprite;
				//		double d0 = texturecompass.currentAngle;
				//		double d1 = texturecompass.angleDelta;
				//		texturecompass.currentAngle = 0.0D;
				//		texturecompass.angleDelta = 0.0D;
				//		texturecompass.updateCompass(lectern.getWorldObj(), lectern.xCoord, lectern.zCoord, MathHelper.wrapAngleTo180_float((180 + lectern.getYaw())), false, true);
				//		texturecompass.currentAngle = d0;
				//		texturecompass.angleDelta = d1;
				//	}
				//}

				Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);

				//if (itemstack.getItem().equals(Items.COMPASS)) {
				//	TextureAtlasSprite textureatlassprite1 = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)).getAtlasSprite(Items.COMPASS.getIconIndex(entity.getEntityItem()).getIconName());

				//	if (textureatlassprite1.getFrameCount() > 0) {
				//		textureatlassprite1.updateAnimation();
				//	}
				//}
			}

			GlStateManager.popMatrix();
		}
	}

}
