package com.xcompwiz.mystcraft.client.audio;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSoundEvents {

	public static final SoundEvent ENTITY_METEOR_FALLING;

	private static SoundEvent getRegisteredSoundEvent(String id) {
		SoundEvent soundevent = (SoundEvent) SoundEvent.REGISTRY.getObject(new ResourceLocation(id));

		if (soundevent == null) {
			throw new IllegalStateException("Invalid Sound requested: " + id);
		} else {
			return soundevent;
		}
	}

	static {
		if (!Bootstrap.isRegistered()) {
			throw new RuntimeException("Accessed Sounds before Bootstrap!");
		}

		//XXX: = new ResourceLocation("mystcraft:entity.meteor.roar")
		ENTITY_METEOR_FALLING = getRegisteredSoundEvent("mystcraft:ambient.cave");
	}
}
