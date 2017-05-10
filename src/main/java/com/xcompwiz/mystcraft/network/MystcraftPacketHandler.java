package com.xcompwiz.mystcraft.network;

import com.xcompwiz.mystcraft.network.packet.*;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MystcraftPacketHandler {

	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("Mystcraft");

	public static void init() {
		int index = 0;

		//Recipient side last.
		CHANNEL.registerMessage(MPacketAgeData.class, MPacketAgeData.class, index++, Side.CLIENT);
		CHANNEL.registerMessage(MPacketConfigs.class, MPacketConfigs.class, index++, Side.CLIENT);
		CHANNEL.registerMessage(MPacketDimensions.class, MPacketDimensions.class, index++, Side.CLIENT);
		CHANNEL.registerMessage(MPacketProfilingState.class, MPacketProfilingState.class, index++, Side.CLIENT);
		CHANNEL.registerMessage(MPacketExplosion.class, MPacketExplosion.class, index++, Side.CLIENT);
		CHANNEL.registerMessage(MPacketSpawnLightningBolt.class, MPacketSpawnLightningBolt.class, index++, Side.CLIENT);
		CHANNEL.registerMessage(MPacketParticles.class, MPacketParticles.class, index++, Side.CLIENT);
		CHANNEL.registerMessage(MPacketGuiMessage.class, MPacketGuiMessage.class, index++, Side.CLIENT);

		CHANNEL.registerMessage(MPacketGuiMessage.class, MPacketGuiMessage.class, index++, Side.SERVER);
	}

}
