package com.xcompwiz.mystcraft.data;

import java.util.Map;

import com.xcompwiz.mystcraft.advancements.TriggerEnterMystDimensionQuinn;
import com.xcompwiz.mystcraft.advancements.TriggerEnterMystDimensionSafely;
import com.xcompwiz.mystcraft.advancements.TriggerWrite;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ModAchievements {

	public static TriggerWrite TRIGGER_WRITE = new TriggerWrite();
	public static TriggerEnterMystDimensionSafely TRIGGER_ENTER_MYST_DIM_SAFE = new TriggerEnterMystDimensionSafely();
	public static TriggerEnterMystDimensionQuinn TRIGGER_ENTER_MYST_DIM_QUINN = new TriggerEnterMystDimensionQuinn();

	public static void init() {
		Map<ResourceLocation, ICriterionTrigger<?>> criteriaTriggerRegistry = ReflectionHelper.getPrivateValue(CriteriaTriggers.class, null, "REGISTRY", "field_192139_s");
		criteriaTriggerRegistry.put(TRIGGER_WRITE.getId(), TRIGGER_WRITE);
		criteriaTriggerRegistry.put(TRIGGER_ENTER_MYST_DIM_SAFE.getId(), TRIGGER_ENTER_MYST_DIM_SAFE);
		criteriaTriggerRegistry.put(TRIGGER_ENTER_MYST_DIM_QUINN.getId(), TRIGGER_ENTER_MYST_DIM_QUINN);
	}

}
