package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.client.shaders.ShaderUtils;

public class LookingGlassAPIHandler {

	public static void register(APIInstanceProvider provider) {
		ShaderUtils.registerShaders();
		InternalAPI.render.registerRenderEffect(new DynamicLinkPanelRenderer(/*api*/));
	}
}
