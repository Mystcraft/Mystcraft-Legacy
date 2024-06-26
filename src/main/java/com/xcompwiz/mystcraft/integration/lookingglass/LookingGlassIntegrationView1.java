package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.IWorldViewAPI;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class LookingGlassIntegrationView1 {

	@SideOnly(Side.CLIENT)
	public static void onAPIGetClient(Object apiinst) {
		IWorldViewAPI viewapi = (IWorldViewAPI) apiinst;
		InternalAPI.render.registerRenderEffect(new DynamicLinkPanelRenderer(new LookingGlassWrapper1(viewapi)));
	}
}
