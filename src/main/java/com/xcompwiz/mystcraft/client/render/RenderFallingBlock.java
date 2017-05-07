package com.xcompwiz.mystcraft.client.render;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.entity.EntityFallingBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderFallingBlock extends Render {
	private RenderBlocks	renderBlocks;

	public RenderFallingBlock() {
		renderBlocks = new RenderBlocks();
		shadowSize = 0.5F;
	}

	public void doRenderFallingBlock(EntityFallingBlock entityfalling, double d, double d1, double d2, float f, float f1) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		this.getEntityTexture(entityfalling);
		GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator tessellator;

		Block block = entityfalling.block;
		if (block != null && block instanceof BlockAnvil && block.getRenderType() == 35) {
			this.renderBlocks.blockAccess = entityfalling.getWorld();
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setTranslation(((-MathHelper.floor_double(entityfalling.posX)) - 0.5F), ((-MathHelper.floor_double(entityfalling.posY)) - 0.5F), ((-MathHelper.floor_double(entityfalling.posZ)) - 0.5F));
			this.renderBlocks.renderBlockAnvilMetadata((BlockAnvil) block, MathHelper.floor_double(entityfalling.posX), MathHelper.floor_double(entityfalling.posY), MathHelper.floor_double(entityfalling.posZ), entityfalling.metadata);
			tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();
		} else if (block != null && block.getRenderType() == 27) {
			this.renderBlocks.blockAccess = entityfalling.getWorld();
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setTranslation(((-MathHelper.floor_double(entityfalling.posX)) - 0.5F), ((-MathHelper.floor_double(entityfalling.posY)) - 0.5F), ((-MathHelper.floor_double(entityfalling.posZ)) - 0.5F));
			this.renderBlocks.renderBlockDragonEgg((BlockDragonEgg) block, MathHelper.floor_double(entityfalling.posX), MathHelper.floor_double(entityfalling.posY), MathHelper.floor_double(entityfalling.posZ));
			tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();
		} else if (block != null) {
			this.renderBlocks.setRenderBoundsFromBlock(block);
			this.renderBlocks.renderBlockSandFalling(block, entityfalling.getWorld(), MathHelper.floor_double(entityfalling.posX), MathHelper.floor_double(entityfalling.posY), MathHelper.floor_double(entityfalling.posZ), entityfalling.metadata);
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		doRenderFallingBlock((EntityFallingBlock) entity, d, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TextureMap.locationBlocksTexture;
	}
}
