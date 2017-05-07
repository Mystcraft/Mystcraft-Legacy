package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

import net.minecraftforge.fml.relauncher.SideOnly;

public class LookingGlassIntegrationView2 {

	@SideOnly(Side.CLIENT)
	public static void onAPIGetClient(Object apiinst) {
		WorldViewAPI2 viewapi = (WorldViewAPI2) apiinst;
		InternalAPI.render.registerRenderEffect(new DynamicLinkPanelRenderer(new LookingGlassWrapper2(viewapi)));
	}
}
