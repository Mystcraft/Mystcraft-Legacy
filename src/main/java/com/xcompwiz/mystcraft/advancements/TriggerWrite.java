package com.xcompwiz.mystcraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.xcompwiz.mystcraft.api.MystObjects;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Set;

public class TriggerWrite implements ICriterionTrigger<TriggerWrite.Instance> {

	public static ResourceLocation ID = new ResourceLocation(MystObjects.MystcraftModId, "writing_desk_write");
	private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
		this.listeners.computeIfAbsent(playerAdvancementsIn, Listeners::new).add(listener);
	}

	@Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
		Listeners listeners = this.listeners.get(playerAdvancementsIn);
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new Instance();
	}

	public void trigger(EntityPlayerMP player) {
		Listeners listeners = this.listeners.get(player.getAdvancements());
		if (listeners != null) {
			listeners.trigger();
		}
	}

	static class Instance extends AbstractCriterionInstance {

		Instance() {
			super(ID);
		}

	}

	private static class Listeners {

		private final PlayerAdvancements playerAdvancements;
		private final Set<Listener<Instance>> listeners = Sets.newHashSet();

		Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(ICriterionTrigger.Listener<Instance> listener) {
			this.listeners.remove(listener);
		}

		void trigger() {
			for (ICriterionTrigger.Listener<Instance> listener : Lists.newArrayList(this.listeners)) {
				listener.grantCriterion(this.playerAdvancements);
			}
		}
	}

}
