package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.MystObjects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.xcompwiz.mystcraft.data.Sounds.*;

public class ModSounds {

	public static void init() {
		SOUND_LINK_POP = registerSound(KEY_NAME_POP);
		SOUND_LINK_LINK = registerSound(KEY_NAME_LINK);
		SOUND_LINK_DISARM = registerSound(KEY_NAME_DISARM);
		SOUND_LINK_FOLLOWING = registerSound(KEY_NAME_FOLLOWING);
		SOUND_LINK_INTRA_LINK = registerSound(KEY_NAME_INTRA_AGE);
		SOUND_LINK_FISSURE = registerSound(KEY_NAME_FISSURELINK);
		SOUND_LINK_PORTAL = registerSound(KEY_NAME_PORTALLINK);
		SOUND_METEOR_ROAR = registerSound(KEY_NAME_METEOR_ROAR);
	}

	private static SoundEvent registerSound(String name) {
		ResourceLocation rl = new ResourceLocation(MystObjects.MystcraftModId, name);
		SoundEvent ev = new SoundEvent(rl);
		ev.setRegistryName(rl);
		ModRegistryPrimer.queueForRegistration(ev);
		return ev;
	}

}
