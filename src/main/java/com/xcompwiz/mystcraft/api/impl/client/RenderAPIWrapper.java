package com.xcompwiz.mystcraft.api.impl.client;

import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.client.IRenderAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.util.Color;

public class RenderAPIWrapper extends APIWrapper implements IRenderAPI {

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

}
