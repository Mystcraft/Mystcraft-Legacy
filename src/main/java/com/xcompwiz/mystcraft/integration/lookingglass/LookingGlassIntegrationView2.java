package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.view.IWorldViewAPI2;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LookingGlassIntegrationView2 {

	@SideOnly(Side.CLIENT)
	public static void onAPIGetClient(Object apiinst) {
		IWorldViewAPI2 viewapi = (IWorldViewAPI2) apiinst;
		InternalAPI.render.registerRenderEffect(new DynamicLinkPanelRenderer(new LookingGlassWrapper2(viewapi)));
	}
}
