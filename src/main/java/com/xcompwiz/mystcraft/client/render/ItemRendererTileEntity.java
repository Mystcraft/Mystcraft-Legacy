package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class ItemRendererTileEntity implements IItemRenderer {
	TileEntitySpecialRenderer	render;
	private TileEntity			dummytile;

	public ItemRendererTileEntity(TileEntitySpecialRenderer render, TileEntity dummy) {
		this.render = render;
		this.dummytile = dummy;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (type == ItemRenderType.ENTITY) GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
		render.renderTileEntityAt(dummytile, 0, 0, 0, 0);
	}

}
