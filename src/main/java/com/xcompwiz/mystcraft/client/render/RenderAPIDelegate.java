package com.xcompwiz.mystcraft.client.render;

import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.internal.IRenderAPI;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.client.linkeffects.LinkPanelEffectManager;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.words.DrawableWordManager;
import com.xcompwiz.util.VectorPool;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RenderAPIDelegate implements IRenderAPI {

	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderEffect(ILinkPanelEffect renderer) {
		LinkPanelEffectManager.registerEffect(renderer);
	}

	@SideOnly(Side.CLIENT)
	public void drawSymbol(float x, float y, float zLevel, float scale, IAgeSymbol symbol) {
		GuiUtils.drawSymbol(FMLClientHandler.instance().getClient().renderEngine, zLevel, symbol, scale, x, y);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawWord(float x, float y, float zLevel, float scale, String word) {
		GuiUtils.drawWord(FMLClientHandler.instance().getClient().renderEngine, zLevel, DrawableWordManager.getDrawableWord(word), scale, x, y);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawColor(float x, float y, float zLevel, float radius, Color color) {
		DniColorRenderer.render(color.toAWT(), VectorPool.getFreeVector(x, y, zLevel), radius);
	}

}
