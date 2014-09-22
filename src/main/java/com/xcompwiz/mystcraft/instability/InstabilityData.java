package com.xcompwiz.mystcraft.instability;

import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;

import com.xcompwiz.mystcraft.block.BlockCrystal;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.effects.EffectCrumble;
import com.xcompwiz.mystcraft.effects.EffectPotion;
import com.xcompwiz.mystcraft.effects.EffectPotionEnemy;
import com.xcompwiz.mystcraft.instability.bonus.BonusProvider;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager;
import com.xcompwiz.mystcraft.instability.bonus.PlayerKilledBonus;
import com.xcompwiz.mystcraft.instability.bonus.PlayerTrollPenalty;
import com.xcompwiz.mystcraft.instability.providers.InstabilityProvider;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayBlack;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayBlue;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayPurple;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayRed;
import com.xcompwiz.mystcraft.instability.providers.ProviderDecayWhite;
import com.xcompwiz.mystcraft.instability.providers.ProviderExplosion;
import com.xcompwiz.mystcraft.instability.providers.ProviderLightning;
import com.xcompwiz.mystcraft.instability.providers.ProviderMeteor;
import com.xcompwiz.mystcraft.instability.providers.ProviderScorched;
import com.xcompwiz.mystcraft.world.ChunkProfiler;

public class InstabilityData {
	private static class deckcost {

		public static int	basic		= 0;
		public static int	harsh		= 2500;
		public static int	destructive	= 8000;
		public static int	eating		= 10000;
		public static int	death		= 15000;

	}

	private static class stability {
		public static int	blindness			= 1000;
		public static int	blindness_global	= 1500;
		public static int	burning				= 500;
		public static int	burning_global		= 1000;
		public static int	crumble				= 2000;
		public static int	crumblebedrock		= 5000;
		public static int	decayBlack			= 5000;
		public static int	decayBlue			= 2000;
		public static int	decayPurple			= 2000;
		public static int	decayRed			= 2000;
		public static int	decayWhite			= 5000;
		public static int	enemyregen_global	= 1000;
		public static int	enemyresist_global	= 1000;
		public static int	erosion				= 2000;
		public static int	explosions			= 1000;
		public static int	fatigue				= 500;
		public static int	fatigue_global		= 1000;
		public static int	hunger				= 500;
		public static int	hunger_global		= 1000;
		public static int	lightning			= 1000;
		public static int	meteors				= 1000;
		public static int	nausea				= 1000;
		public static int	nausea_global		= 1500;
		public static int	poison				= 500;
		public static int	poison_global		= 1000;
		public static int	slow				= 500;
		public static int	slow_global			= 1000;
		public static int	weakness			= 500;
		public static int	weakness_global		= 1000;
		public static int	wither				= 1000;
		public static int	wither_global		= 2000;
	}

	public static class missing {
		public static int	controller	= 0;
	}

	public static class extra {
		public static int	controller	= 500;
	}

	public static class symbol {
		public static int	accelerated	= 1000;
		public static int	caveworld	= 1000;
		public static int	bright		= 500;
		public static int	charged		= -500;
		public static int	meteors		= -1000;
		public static int	explosion	= -500;
		public static int	scorched	= -500;
	}

	public static float	clearPercentage	= 0.20F;

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
		ChunkProfiler.setInstabilityFactors(Blocks.coal_ore, 5, 1, 200);
		ChunkProfiler.setInstabilityFactors(Blocks.iron_ore, 60, 1, 500);
		ChunkProfiler.setInstabilityFactors(Blocks.redstone_ore, 250, 2, 500);
		ChunkProfiler.setInstabilityFactors(Blocks.gold_ore, 750, 4, 500);
		ChunkProfiler.setInstabilityFactors(Blocks.diamond_ore, 4000, 20, 1000);

		ChunkProfiler.setInstabilityFactors(BlockCrystal.instance, 20, 4, 0);
		ChunkProfiler.setInstabilityFactors(Blocks.glowstone, 50, 4, 0);
		ChunkProfiler.setInstabilityFactors(Blocks.quartz_ore, 20, 4, 0);

		//TODO: (Instability) Implement missing Instability effects
		InstabilityManager.setDeckCost("basic", deckcost.basic);
		InstabilityManager.setDeckCost("harsh", deckcost.harsh);
		InstabilityManager.setDeckCost("destructive", deckcost.destructive);
		InstabilityManager.setDeckCost("eating", deckcost.eating);
		InstabilityManager.setDeckCost("death", deckcost.death);
		InstabilityProviderContainerObject.create("blindness", new InstabilityProvider(true, EffectPotion.class, false, Potion.blindness.id, 60), stability.blindness).add("eating", 1);
		InstabilityProviderContainerObject.create("blindness,g", new InstabilityProvider(true, EffectPotion.class, true, Potion.blindness.id, 60), stability.blindness_global).add("death", 1);
		InstabilityProviderContainerObject.create("enemyregen,g", new InstabilityProvider(true, EffectPotionEnemy.class, true, Potion.regeneration.id, 200), stability.enemyregen_global).add("basic", 5).add("harsh", 2);
		InstabilityProviderContainerObject.create("enemyresist,g", new InstabilityProvider(true, EffectPotionEnemy.class, true, Potion.resistance.id, 200), stability.enemyresist_global).add("basic", 2).add("harsh", 1);
		InstabilityProviderContainerObject.create("fatigue", new InstabilityProvider(true, EffectPotion.class, false, Potion.digSlowdown.id, 80), stability.fatigue).add("basic", 5);
		InstabilityProviderContainerObject.create("fatigue,g", new InstabilityProvider(true, EffectPotion.class, true, Potion.digSlowdown.id, 80), stability.fatigue_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("hunger", new InstabilityProvider(true, EffectPotion.class, false, Potion.hunger.id, 80), stability.hunger).add("basic", 8).add("harsh", 2);
		InstabilityProviderContainerObject.create("hunger,g", new InstabilityProvider(true, EffectPotion.class, true, Potion.hunger.id, 80), stability.hunger_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("nausea", new InstabilityProvider(true, EffectPotion.class, false, Potion.confusion.id, 60), stability.nausea).add("eating", 1);
		InstabilityProviderContainerObject.create("nausea,g", new InstabilityProvider(true, EffectPotion.class, true, Potion.confusion.id, 60), stability.nausea_global).add("death", 1);
		InstabilityProviderContainerObject.create("poison", new InstabilityProvider(true, EffectPotion.class, false, Potion.poison.id, 80), stability.poison).add("basic", 9).add("harsh", 3);
		InstabilityProviderContainerObject.create("poison,g", new InstabilityProvider(true, EffectPotion.class, true, Potion.poison.id, 80), stability.poison_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("slow", new InstabilityProvider(true, EffectPotion.class, false, Potion.moveSlowdown.id, 80), stability.slow).add("basic", 6).add("harsh", 1);
		InstabilityProviderContainerObject.create("slow,g", new InstabilityProvider(true, EffectPotion.class, true, Potion.moveSlowdown.id, 80), stability.slow_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("weakness", new InstabilityProvider(true, EffectPotion.class, false, Potion.weakness.id, 80), stability.weakness).add("basic", 8).add("harsh", 2);
		InstabilityProviderContainerObject.create("weakness,g", new InstabilityProvider(true, EffectPotion.class, true, Potion.weakness.id, 80), stability.weakness_global).add("harsh", 5);
		InstabilityProviderContainerObject.create("wither", new InstabilityProvider(true,EffectPotion.class, false, Potion.wither.id, 30), stability.wither).add("harsh", 1).add("destructive", 1).add("eating", 2);
		InstabilityProviderContainerObject.create("wither,g", new InstabilityProvider(true, EffectPotion.class, true, Potion.wither.id, 30), stability.wither_global).add("destructive", 1).add("eating", 1).add("death", 1);

		InstabilityProviderContainerObject.create("burning", new ProviderScorched(), stability.burning).add("harsh", 1);
		//InstabilityProviderContainerObject.create("burning,g", new ProviderScorched(true), stability.burning_global).add("destructive", 1);
		EffectCrumble.initMappings();
		InstabilityProviderContainerObject.create("crumble", new InstabilityProvider(false, EffectCrumble.class), stability.crumble).add("destructive", 6);
		//InstabilityProviderContainerObject.create("crumblebedrock", new InstabilityProvider(false, EffectCrumble.class), stability.crumblebedrock).add("eating", 3).add("death", 1);
		InstabilityProviderContainerObject.create("decayblack", new ProviderDecayBlack(), stability.decayBlack).add("eating", 1).add("death", 3);
		InstabilityProviderContainerObject.create("decayblue", new ProviderDecayBlue(), stability.decayBlue).add("eating", 2).add("death", 1);
		InstabilityProviderContainerObject.create("decaypurple", new ProviderDecayPurple(), stability.decayPurple).add("eating", 2).add("death", 1);
		InstabilityProviderContainerObject.create("decayred", new ProviderDecayRed(), stability.decayRed).add("eating", 2).add("death", 1);
		InstabilityProviderContainerObject.create("decaywhite", new ProviderDecayWhite(), stability.decayWhite).add("eating", 1).add("death", 3);
		//InstabilityProviderContainerObject.create("erosion", new ProviderErosion(), stability.erosion);
		InstabilityProviderContainerObject.create("explosions", new ProviderExplosion(), stability.explosions).add("destructive", 8);
		InstabilityProviderContainerObject.create("lightning", new ProviderLightning(), stability.lightning).add("harsh", 4).add("destructive", 4);
		InstabilityProviderContainerObject.create("meteors", new ProviderMeteor(), stability.meteors).add("destructive", 4);

		InstabilityBonusManager.registerBonusProvider(new BonusProvider(PlayerKilledBonus.class, "Direwolf20", 10000, 0.1F));
		InstabilityBonusManager.registerBonusProvider(new BonusProvider(PlayerKilledBonus.class, "Soaryn", 10000, 0.1F));

		InstabilityBonusManager.registerBonusProvider(new BonusProvider(PlayerTrollPenalty.class, "Direwolf20", 10000, 0.6F));
		InstabilityBonusManager.registerBonusProvider(new BonusProvider(PlayerTrollPenalty.class, "Soaryn", 10000, 0.6F));
	}
}
