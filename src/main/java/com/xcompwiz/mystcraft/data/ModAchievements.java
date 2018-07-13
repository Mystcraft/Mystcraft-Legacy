package com.xcompwiz.mystcraft.data;

import java.util.Map;

import com.xcompwiz.mystcraft.advancements.TriggerEnterMystDimension;
import com.xcompwiz.mystcraft.advancements.TriggerWrite;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ModAchievements {

	public static TriggerWrite TRIGGER_WRITE = new TriggerWrite();
	public static TriggerEnterMystDimension TRIGGER_ENTER_MYST_DIM = new TriggerEnterMystDimension();

	public static void init() {
		Map<ResourceLocation, ICriterionTrigger<?>> criteriaTriggerRegistry = ReflectionHelper.getPrivateValue(CriteriaTriggers.class, null, "REGISTRY", "field_192139_s");
		criteriaTriggerRegistry.put(TRIGGER_WRITE.getId(), TRIGGER_WRITE);
		criteriaTriggerRegistry.put(TRIGGER_ENTER_MYST_DIM.getId(), TRIGGER_ENTER_MYST_DIM);
	}

}
