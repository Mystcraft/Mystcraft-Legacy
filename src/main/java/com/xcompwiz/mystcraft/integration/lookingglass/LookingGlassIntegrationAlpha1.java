package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.ILookingGlassAPI;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

public class LookingGlassIntegrationAlpha1 {

	public static void onAPIGet(Object apiinst) {
		ILookingGlassAPI alphaapi = (ILookingGlassAPI) apiinst;
		InternalAPI.render.registerRenderEffect(new DynamicLinkPanelRenderer(alphaapi));
	}

}
