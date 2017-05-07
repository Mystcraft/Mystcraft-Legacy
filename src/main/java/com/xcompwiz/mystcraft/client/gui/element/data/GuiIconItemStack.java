package com.xcompwiz.mystcraft.client.gui.element.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public class GuiIconItemStack implements IGuiIcon {
	public interface IItemStackProvider {
		public ItemStack getItemStack(GuiIconItemStack caller);
	}

	private static RenderItem	itemrenderer	= new RenderItem();

	private IItemStackProvider	provider;
	private String				id;
	private ItemStack			itemstack;

	public GuiIconItemStack(ItemStack itemstack) {
		this.itemstack = itemstack;
	}

	public GuiIconItemStack(IItemStackProvider provider, String id) {
		this.provider = provider;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	private ItemStack getItemStack() {
		if (provider != null) return provider.getItemStack(this);
		return itemstack;
	}

	@Override
	public void render(Minecraft mc, int guiLeft, int guiTop, int xSize, int ySize, float zLevel) {
		ItemStack itemstack = this.getItemStack();
		if (itemstack == null) return;
		float smallest = Math.min(xSize, ySize);
		float scale = smallest / 16.F;

		String itemslottext = null;
		if (itemstack.getCount() <= 0) {
			itemslottext = EnumChatFormatting.RED + Integer.toString(itemstack.getCount());
		}

		GuiUtils.startGlScissor(guiLeft, guiTop, xSize, ySize);

		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 1);
		GL11.glScalef(scale, scale, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		itemrenderer.zLevel = zLevel;
		itemrenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, 0, 0);
		itemrenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, 0, 0, itemslottext);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();

		GuiUtils.endGlScissor();
	}

}
