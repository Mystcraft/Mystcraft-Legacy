package com.xcompwiz.mystcraft.integration.lookingglass;

import net.minecraftforge.common.MinecraftForge;

import com.xcompwiz.lookingglass.api.APIInstanceProvider;
import com.xcompwiz.lookingglass.api.APIUndefined;
import com.xcompwiz.lookingglass.api.APIVersionRemoved;
import com.xcompwiz.lookingglass.api.APIVersionUndefined;
import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class LookingGlassIntegration {

	public static void register(APIInstanceProvider provider) {
		LoggerUtils.info("LookingGlass API Provider Received");
		Mystcraft.sidedProxy.initShaders();

		MinecraftForge.EVENT_BUS.register(new MystcraftLookingGlassEventHandler());

		getAlphaAPI(provider);
	}

	private static void getAlphaAPI(APIInstanceProvider provider) {
		try {
			Object apiinst = provider.getAPIInstance("view-1");
			if (Mystcraft.sidedProxy.isClientSideAvailable()) LookingGlassIntegrationView1.onAPIGetClient(apiinst);
		} catch (APIUndefined e) {
			// The API we requested doesn't exist.  Give up with a nice log message.
			LoggerUtils.warn("This version of Mystcraft is probably out of date. Please check for updates. LookingGlass integration failure.");
		} catch (APIVersionUndefined e) {
			// The API we requested exists, but the version we wanted is missing in the local environment. We can try falling back to an older version.
			LoggerUtils.warn("For some reason, LookingGlass cannot count to 1. Integration failure.");
		} catch (APIVersionRemoved e) {
			// The API we requested exists, but the version we wanted has been removed and is no longer supported. Better update.
			LoggerUtils.warn("This version of Mystcraft is probably out of date. Please check for updates. LookingGlass integration failure.");
		}
	}

}
