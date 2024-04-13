package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import cpw.mods.fml.client.FMLClientHandler;

public class ItemRendererPage implements IItemRenderer {
	final float	held_width			= 1.0F;
	final float	held_height			= held_width * 4 / 3;
	final float	held_offset			= 0.0F;
	final float	held_symbolscale	= held_width * 0.9F;
	final float	held_sx				= -held_offset - held_width - (held_symbolscale - held_width) / 2;
	final float	held_sy				= -held_symbolscale;

	final float	entity_width		= 1.0F;
	final float	entity_height		= entity_width * 4 / 3;
	final float	entity_offset		= -entity_width / 2;
	final float	entity_symbolscale	= entity_width * .95F;
	final float	entity_sx			= -entity_height / 2 + (entity_height - entity_symbolscale) / 2;
	final float	entity_sy			= (entity_height - entity_symbolscale) / 2;

	public ItemRendererPage() {}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY) return true;
		if (type == ItemRenderType.ENTITY) return true;
		if (type == ItemRenderType.EQUIPPED) return true;
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) return true;
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		if (type == ItemRenderType.INVENTORY) return false;
		if (type == ItemRenderType.ENTITY) return true;
		if (type == ItemRenderType.EQUIPPED) return false;
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) return false;
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if (type == ItemRenderType.INVENTORY) {
			GuiUtils.drawPage(FMLClientHandler.instance().getClient().renderEngine, 10, item, 16.0F, 16.0F, 0.0F, 0.0F);
		} else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			TextureManager renderEngine = FMLClientHandler.instance().getClient().renderEngine;
			renderEngine.bindTexture(GUIs.book_page_left);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GL11.glRotatef(180, 0.0F, 0.0F, 1.0F);
			GuiUtils.drawTexturedModalRect(held_offset - 1, -1, 156, 0, 30, 40, 0, held_width, held_height);

			if (Page.getSymbol(item) != null) {
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(item));
				GuiUtils.drawSymbol(FMLClientHandler.instance().getClient().renderEngine, -0.01F, symbol, held_symbolscale, held_sx, held_sy);
			} else if (Page.isLinkPanel(item)) {
				GuiUtils.drawGradientRect(held_sx + held_width * 0.10F, held_sy, held_sx + held_width * 0.8F, held_sy + held_height * 0.3F, 0xFF000000, 0xFF000000, -0.01F);
			}
		} else if (type == ItemRenderType.ENTITY) {
			GL11.glTranslatef(0.0F, 0.7F, 0.0F);
			GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(90, 0.0F, 1.0F, 0.0F);
			TextureManager renderEngine = FMLClientHandler.instance().getClient().renderEngine;
			renderEngine.bindTexture(GUIs.book_page_left);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GuiUtils.drawTexturedModalRect(entity_offset, 0, 156, 0, 30, 40, 0, entity_width, entity_height);
			GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);
			GuiUtils.drawTexturedModalRect(entity_offset, 0, 156, 0, 30, 40, 0, entity_width, entity_height);

			if (Page.getSymbol(item) != null) {
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(item));
				GuiUtils.drawSymbol(FMLClientHandler.instance().getClient().renderEngine, -0.01F, symbol, entity_symbolscale, entity_sx, entity_sy);
			} else if (Page.isLinkPanel(item)) {
				GuiUtils.drawGradientRect(entity_sx + entity_width * 0.125F, entity_sy, entity_sx + entity_width * 0.85F, entity_sy + entity_height * 0.3F, 0xFF000000, 0xFF000000, -0.01F);
			}
		}
	}

}
