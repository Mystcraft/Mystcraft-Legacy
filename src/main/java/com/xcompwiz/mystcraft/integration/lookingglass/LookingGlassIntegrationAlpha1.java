package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.ILookingGlassAPI;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LookingGlassIntegrationAlpha1 {

	@SideOnly(Side.CLIENT)
	public static void onAPIGetClient(Object apiinst) {
		ILookingGlassAPI alphaapi = (ILookingGlassAPI) apiinst;
		InternalAPI.render.registerRenderEffect(new DynamicLinkPanelRenderer(alphaapi));
	}
}
