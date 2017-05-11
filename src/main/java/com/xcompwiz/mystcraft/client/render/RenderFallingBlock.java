package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
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

public class RenderFallingBlock extends Render<EntityFallingBlock> {

	private RenderBlocks	renderBlocks;

	public RenderFallingBlock(RenderManager renderManager) {
		super(renderManager);
		shadowSize = 0.5F;
	}

	@Override
	public void doRender(EntityFallingBlock entity, double x, double y, double z, float entityYaw, float partialTicks) {
		doRenderFallingBlock(entity, x, y, z, entityYaw, partialTicks);
	}

	public void doRenderFallingBlock(EntityFallingBlock entityfalling, double x, double y, double z, float entityYaw, float partialTicks) {
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
			tessellator.setTranslation(((-MathHelper.floor(entityfalling.posX)) - 0.5F), ((-MathHelper.floor(entityfalling.posY)) - 0.5F), ((-MathHelper.floor(entityfalling.posZ)) - 0.5F));
			this.renderBlocks.renderBlockAnvilMetadata((BlockAnvil) block, MathHelper.floor(entityfalling.posX), MathHelper.floor(entityfalling.posY), MathHelper.floor(entityfalling.posZ), entityfalling.metadata);
			tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();
		} else if (block != null && block.getRenderType() == 27) {
			this.renderBlocks.blockAccess = entityfalling.getWorld();
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setTranslation(((-MathHelper.floor(entityfalling.posX)) - 0.5F), ((-MathHelper.floor(entityfalling.posY)) - 0.5F), ((-MathHelper.floor(entityfalling.posZ)) - 0.5F));
			this.renderBlocks.renderBlockDragonEgg((BlockDragonEgg) block, MathHelper.floor(entityfalling.posX), MathHelper.floor(entityfalling.posY), MathHelper.floor(entityfalling.posZ));
			tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();
		} else if (block != null) {
			this.renderBlocks.setRenderBoundsFromBlock(block);
			this.renderBlocks.renderBlockSandFalling(block, entityfalling.getWorld(), MathHelper.floor(entityfalling.posX), MathHelper.floor(entityfalling.posY), MathHelper.floor(entityfalling.posZ), entityfalling.metadata);
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFallingBlock entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	public static class Factory implements IRenderFactory<EntityFallingBlock> {

		@Override
		public Render<EntityFallingBlock> createRenderFor(RenderManager manager) {
			return new RenderFallingBlock(manager);
		}

	}

}
