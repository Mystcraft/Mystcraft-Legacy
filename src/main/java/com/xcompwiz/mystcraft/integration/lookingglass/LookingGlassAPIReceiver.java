package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.ILookingGlassAPI1;
import com.xcompwiz.mystcraft.client.shaders.ShaderUtils;
import com.xcompwiz.mystcraft.core.InternalAPI;

public class LookingGlassAPIReceiver {

	public static void register1(ILookingGlassAPI1 api) {
		ShaderUtils.registerShaders();
		InternalAPI.render.registerRenderEffect(new DynamicLinkPanelRenderer(/*api*/));
	}
}
