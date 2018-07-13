package com.xcompwiz.mystcraft.api.impl.client;

import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.hook.RenderAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.util.Color;

import net.minecraft.util.ResourceLocation;

public class RenderAPIWrapper extends APIWrapper implements RenderAPI {

	public RenderAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public void registerRenderEffect(ILinkPanelEffect renderer) {
		InternalAPI.render.registerRenderEffect(renderer);
	}

	@Override
	public void drawWord(float x, float y, float zLevel, float scale, String word) {
		InternalAPI.render.drawWord(x, y, zLevel, scale, word);
	}

	@Override
	public void drawColorEye(float x, float y, float zLevel, float radius, Color color) {
		InternalAPI.render.drawColorEye(x, y, zLevel, radius, color);
	}

	@Override
	public void drawSymbol(float x, float y, float zLevel, float scale, ResourceLocation identifier) {
		InternalAPI.render.drawSymbol(x, y, zLevel, scale, InternalAPI.symbol.getSymbol(identifier));
	}

}
