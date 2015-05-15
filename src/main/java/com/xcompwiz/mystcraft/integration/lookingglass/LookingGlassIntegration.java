package com.xcompwiz.mystcraft.integration.lookingglass;

import net.minecraftforge.common.MinecraftForge;

import com.xcompwiz.lookingglass.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.client.shaders.ShaderUtils;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class LookingGlassIntegration {

	public static void register(APIInstanceProvider provider) {
		LoggerUtils.info("LookingGlass API Provider Received");
		ShaderUtils.registerShaders();

		MinecraftForge.EVENT_BUS.register(new MystcraftLookingGlassEventHandler());

		InternalAPI.render.registerRenderEffect(new DynamicLinkPanelRenderer(/*api*/));
	}
}
