package com.xcompwiz.mystcraft.client.gui.element.data;

import javax.annotation.Nonnull;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class GuiIconItemStack implements IGuiIcon {

	public interface IItemStackProvider {

		@Nonnull
		public ItemStack getItemStack(GuiIconItemStack caller);

	}

	private IItemStackProvider provider;
	private String id;
	@Nonnull
	private ItemStack itemstack = ItemStack.EMPTY;

	public GuiIconItemStack(@Nonnull ItemStack itemstack) {
		this.itemstack = itemstack;
	}

	public GuiIconItemStack(IItemStackProvider provider, String id) {
		this.provider = provider;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Nonnull
	private ItemStack getItemStack() {
		if (provider != null) {
			return provider.getItemStack(this);
		}
		return itemstack;
	}

	@Override
	public void render(Minecraft mc, int guiLeft, int guiTop, int xSize, int ySize, float zLevel) {
		ItemStack itemstack = this.getItemStack();
		if (itemstack.isEmpty())
			return;
		float smallest = Math.min(xSize, ySize);
		float scale = smallest / 16.F;

		String itemslottext = null;
		if (itemstack.getCount() <= 0) {
			itemslottext = TextFormatting.RED + Integer.toString(itemstack.getCount());
		}

		GuiUtils.startGlScissor(guiLeft, guiTop, xSize, ySize);

		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 1);
		GlStateManager.scale(scale, scale, 1);
		GlStateManager.enableDepth();
		RenderItem ri = Minecraft.getMinecraft().getRenderItem();
		float prev = ri.zLevel;
		ri.zLevel = zLevel;
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		FontRenderer custom = itemstack.getItem().getFontRenderer(itemstack);
		if (custom != null) {
			fr = custom;
		}
		ri.renderItemAndEffectIntoGUI(itemstack, 0, 0);
		ri.renderItemOverlayIntoGUI(fr, itemstack, 0, 0, itemslottext);
		ri.zLevel = prev;
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();

		GuiUtils.endGlScissor();
	}

}
