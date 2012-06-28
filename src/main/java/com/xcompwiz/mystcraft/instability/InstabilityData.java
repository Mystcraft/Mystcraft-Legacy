package com.xcompwiz.mystcraft.instability;

import net.minecraftforge.common.config.Configuration;

import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.effects.EffectBlindness;
import com.xcompwiz.mystcraft.effects.EffectCrumble;
import com.xcompwiz.mystcraft.instability.providers.InstabilityProvider;
import com.xcompwiz.mystcraft.instability.providers.ProviderBlindness;
import com.xcompwiz.mystcraft.instability.providers.ProviderConfusion;
import com.xcompwiz.mystcraft.instability.providers.ProviderCrumble;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayBlack;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayBlue;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayPurple;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayRed;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayWhite;
import com.xcompwiz.mystcraft.instability.providers.ProviderExplosion;
import com.xcompwiz.mystcraft.instability.providers.ProviderFatigue;
import com.xcompwiz.mystcraft.instability.providers.ProviderHunger;
import com.xcompwiz.mystcraft.instability.providers.ProviderLightning;
import com.xcompwiz.mystcraft.instability.providers.ProviderMeteor;
import com.xcompwiz.mystcraft.instability.providers.ProviderPoison;
import com.xcompwiz.mystcraft.instability.providers.ProviderScorched;
import com.xcompwiz.mystcraft.instability.providers.ProviderSlow;
import com.xcompwiz.mystcraft.instability.providers.ProviderWeakness;

public class InstabilityData {
	private static class deckcost {

		public static int	basic		= 0;
		public static int	harsh		= 0;
		public static int	destructive	= 1000;
		public static int	eating		= 5000;
		public static int	death		= 10000;

	}

	private static class stability {
		public static int	blindness			= 300;
		public static int	blindness_global	= 500;
		public static int	burning				= 100;
		public static int	burning_global		= 200;
		public static int	crumble				= 1000;
		public static int	crumblebedrock		= 2000;
		public static int	decayBlack			= 2000;
		public static int	decayBlue			= 1000;
		public static int	decayGreen			= 1000;
		public static int	decayPurple			= 1000;
		public static int	decayRed			= 1000;
		public static int	decayWhite			= 2000;
		public static int	enemyregen_global	= 200;
		public static int	erosion				= 1000;
		public static int	explosions			= 200;
		public static int	fatigue				= 100;
		public static int	fatigue_global		= 200;
		public static int	hunger				= 100;
		public static int	hunger_global		= 200;
		public static int	lightning			= 200;
		public static int	meteors				= 300;
		public static int	nausea				= 300;
		public static int	nausea_global		= 500;
		public static int	poison				= 100;
		public static int	poison_global		= 200;
		public static int	slow				= 100;
		public static int	slow_global			= 200;
		public static int	weakness			= 100;
		public static int	weakness_global		= 200;
		public static int	volcano				= 400;
	}

	public static class missing {
		public static int	controller	= 0;
	}

	public static class extra {
		public static int	controller	= 200;
	}

	public static class symbol {
		public static int	accelerated	= 250;
		public static int	charged		= -stability.lightning;
		public static int	meteors		= -stability.meteors;
		public static int	explosion	= -stability.explosions;
		public static int	scorched	= -stability.burning;
	}

	public static float	clearPercentage	= 0.20F;

	public static void loadDebugConfigs(Configuration config) {
		deckcost.basic = config(config, deckcost.basic, "deck.basic");
		deckcost.harsh = config(config, deckcost.harsh, "deckcost.harsh");
		deckcost.destructive = config(config, deckcost.destructive, "deckcost.destructive");
		deckcost.eating = config(config, deckcost.eating, "deckcost.eating");
		deckcost.death = config(config, deckcost.death, "deckcost.death");

		stability.blindness = config(config, stability.blindness, "stability.blindness");
		stability.blindness_global = config(config, stability.blindness_global, "stability.blindness_global");
		stability.burning = config(config, stability.burning, "stability.burning");
		stability.burning_global = config(config, stability.burning_global, "stability.burning_global");
		stability.crumble = config(config, stability.crumble, "stability.crumble");
		stability.crumblebedrock = config(config, stability.crumblebedrock, "stability.crumblebedrock");
		stability.decayBlack = config(config, stability.decayBlack, "stability.decayBlack");
		stability.decayBlue = config(config, stability.decayBlue, "stability.decayBlue");
		stability.decayGreen = config(config, stability.decayGreen, "stability.decayGreen");
		stability.decayRed = config(config, stability.decayRed, "stability.decayRed");
		stability.decayPurple = config(config, stability.decayPurple, "stability.decayPurple");
		stability.enemyregen_global = config(config, stability.enemyregen_global, "stability.enemyregen_global");
		//stability.erosion = config(config, stability.erosion, "stability.erosion");
		stability.explosions = config(config, stability.explosions, "stability.explosions");
		stability.fatigue = config(config, stability.fatigue, "stability.fatigue");
		stability.fatigue_global = config(config, stability.fatigue_global, "stability.fatigue_global");
		stability.hunger = config(config, stability.hunger, "stability.hunger");
		stability.hunger_global = config(config, stability.hunger_global, "stability.hunger_global");
		stability.lightning = config(config, stability.lightning, "stability.lightning");
		stability.meteors = config(config, stability.meteors, "stability.meteors");
		stability.nausea = config(config, stability.nausea, "stability.nausea");
		stability.nausea_global = config(config, stability.nausea_global, "stability.nausea_global");
		stability.poison = config(config, stability.poison, "stability.poison");
		stability.poison_global = config(config, stability.poison_global, "stability.poison_global");
		stability.slow = config(config, stability.slow, "stability.slow");
		stability.slow_global = config(config, stability.slow_global, "stability.slow_global");
		//stability.volcano = config(config, stability.volcano, "stability.volcano");
		stability.weakness = config(config, stability.weakness, "stability.weakness");
		stability.weakness_global = config(config, stability.weakness_global, "stability.weakness_global");
	}

	private static int config(Configuration config, int value, String string) {
		return config.get(MystConfig.CATEGORY_DEBUG, string, value).getInt(value);
	}

	private static class InstabilityProviderContainerObject {

		private String	identifier;

		private InstabilityProviderContainerObject(String identifier, IInstabilityProvider provider, int activationcost) {
			this.identifier = identifier;
			InternalAPI.instability.registerInstability(identifier, provider, activationcost);
		}

		public InstabilityProviderContainerObject add(String deck, int count) {
			InstabilityManager.addCards(deck, identifier, count);
			return this;
		}

		public static InstabilityProviderContainerObject create(String identifier, IInstabilityProvider provider, int activationcost) {
			return new InstabilityProviderContainerObject(identifier, provider, activationcost);
		}
	}

	public static void initialize() {
		//FIXME: !!! Implement missing Instability effects
		InstabilityManager.setDeckCost("basic", deckcost.basic);
		InstabilityManager.setDeckCost("harsh", deckcost.harsh);
		InstabilityManager.setDeckCost("destructive", deckcost.destructive);
		InstabilityManager.setDeckCost("eating", deckcost.eating);
		InstabilityManager.setDeckCost("death", deckcost.death);
		//InstabilityProviderContainerObject.create("blindness", new ProviderBlindness(), stability.blindness).add("eating", 1);
		//InstabilityProviderContainerObject.create("blindness", new ProviderBlindness(), stability.blindness_global).add("eating", 1);
		InstabilityProviderContainerObject.create("blindness,g", new InstabilityProvider(false, EffectBlindness.class), stability.blindness_global).add("death", 1);
		InstabilityProviderContainerObject.create("burning", new ProviderScorched(), stability.burning).add("basic", 1);
		//InstabilityProviderContainerObject.create("burning,g", new ProviderScorched(true), stability.burning_global).add("harsh", 1);
		InstabilityProviderContainerObject.create("crumble", new InstabilityProvider(false, EffectCrumble.class), stability.crumble).add("destructive", 6).add("eating", 2);
		//InstabilityProviderContainerObject.create("crumblebedrock", new ProviderCrumbleBedrock(), stability.crumblebedrock).add("eating", 3).add("death", 1);
		InstabilityProviderContainerObject.create("decayblack", new ProviderDecayBlack(), stability.decayBlack).add("eating", 1).add("death", 3);
		InstabilityProviderContainerObject.create("decayblue", new ProviderDecayBlue(), stability.decayBlue).add("eating", 3);
		// InstabilityProviderContainerObject.create("decaygreen", new ProviderDecayGreen(), stability.decayGreen).add("eating", 3);
		InstabilityProviderContainerObject.create("decaypurple", new ProviderDecayPurple(), stability.decayPurple).add("eating", 3);
		InstabilityProviderContainerObject.create("decayred", new ProviderDecayRed(), stability.decayRed).add("eating", 3);
		InstabilityProviderContainerObject.create("decaywhite", new ProviderDecayWhite(), stability.decayWhite).add("eating", 1).add("death", 3);
		//InstabilityProviderContainerObject.create("enemyregen,g", new ProviderEnemyRegeneration(true), stability.enemyregen_global).add("basic", 4).add("harsh", 2);
		//InstabilityProviderContainerObject.create("erosion", new ProviderErosion(), stability.erosion);
		InstabilityProviderContainerObject.create("explosions", new ProviderExplosion(), stability.explosions).add("destructive", 8);
		InstabilityProviderContainerObject.create("fatigue", new ProviderFatigue(), stability.fatigue).add("basic", 8);
		//InstabilityProviderContainerObject.create("fatigue,g", new ProviderFatigue(true), stability.fatigue_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("hunger", new ProviderHunger(), stability.hunger).add("basic", 8);
		//InstabilityProviderContainerObject.create("hunger,g", new ProviderHunger(true), stability.hunger_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("lightning", new ProviderLightning(), stability.lightning).add("harsh", 6);
		InstabilityProviderContainerObject.create("meteors", new ProviderMeteor(), stability.meteors).add("destructive", 4);
		InstabilityProviderContainerObject.create("nausea", new ProviderConfusion(), stability.nausea).add("eating", 1);
		//InstabilityProviderContainerObject.create("nausea,g", new ProviderConfusion(true), stability.nausea_global).add("death", 1);
		InstabilityProviderContainerObject.create("poison", new ProviderPoison(), stability.poison).add("basic", 8).add("harsh", 2);
		//InstabilityProviderContainerObject.create("poison,g", new ProviderPoison(true), stability.poison_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("slow", new ProviderSlow(), stability.slow).add("basic", 8).add("harsh", 1);
		//InstabilityProviderContainerObject.create("slow,g", new ProviderSlow(true), stability.slow_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("weakness", new ProviderWeakness(), stability.weakness).add("basic", 8).add("harsh", 2);
		//InstabilityProviderContainerObject.create("weakness,g", new ProviderWeakness(true), stability.weakness_global).add("harsh", 5);

		EffectCrumble.initMappings();
	}
}
