package com.xcompwiz.mystcraft.client.render;

import com.xcompwiz.mystcraft.block.BlockBookstand;
import com.xcompwiz.mystcraft.data.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.model.ModelBookstand;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookstand;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RenderBookstand extends TileEntitySpecialRenderer<TileEntityBookstand> {

	private ModelBook		book;
	private ModelBookstand	stand;

	public RenderBookstand() {
		stand = new ModelBookstand();
		book = new ModelBook();
	}

	@Override
	public void renderTileEntityAt(TileEntityBookstand te, double x, double y, double z, float partialTicks, int destroyStage) {
		x += 0.5;
		z += 0.5;
		bindTexture(Entities.bookstand);
		GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.5, z);
        GlStateManager.rotate(180, 0, 0, 1);
        IBlockState bookstandState = ModBlocks.bookstand.getStateFromMeta(te.getBlockMetadata());
        int rotationIndex = bookstandState.getValue(BlockBookstand.ROTATION_INDEX);
        GlStateManager.rotate(45 * rotationIndex, 0, 1, 0);
        stand.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625F);
        GlStateManager.popMatrix();

        ItemStack display = te.getDisplayItem();
        if (display.isEmpty()) {
            return;
        }
        if (display.getItem() == ModItems.agebook) {
            bindTexture(Entities.agebook);
        } else if (display.getItem() == ModItems.linkbook) {
            bindTexture(Entities.linkbook);
        } else {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.55F, z);
        GlStateManager.rotate(90 + 45 * rotationIndex, 0, -1, 0);
        GlStateManager.rotate(120F, 0, 0, 1);
        GlStateManager.scale(0.8, 0.8, 0.8);
        book.render(null, 0.0f, 0.0f, 0.0f, 1.05f /* Open */, 0.0f, 0.0625F);
        GlStateManager.popMatrix();

        if (Mystcraft.renderlabels && Mystcraft.serverLabels) {
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

}
