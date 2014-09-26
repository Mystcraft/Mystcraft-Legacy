package com.xcompwiz.mystcraft.instability.bonus;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.World;

import com.xcompwiz.mystcraft.world.AgeController;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

public class InstabilityBonusManager {

	//XXX: Move to own (for API)
	public interface IInstabilityBonus {
		public int getValue();

		public void tick(World world);
	}

	//XXX: Move to own (for API)
	public interface IInstabilityBonusProvider {
		public void register(InstabilityBonusManager bonusmanager, World world);
	}

	public static final InstabilityBonusManager		ZERO			= new InstabilityBonusManager();

	private static Set<IInstabilityBonusProvider>	bonusproviders	= new HashSet<IInstabilityBonusProvider>();

	public static void registerBonusProvider(IInstabilityBonusProvider provider) {
		bonusproviders.add(provider);
	}

	private Set<IInstabilityBonus>	bonuses	= new HashSet<IInstabilityBonus>();
	private int						total;

	private AgeController	controller;

	public InstabilityBonusManager() {}

	public InstabilityBonusManager(WorldProviderMyst provider, AgeController agecontroller) {
		this.controller = agecontroller;
		for (IInstabilityBonusProvider bprovider : bonusproviders) {
			bprovider.register(this, provider.worldObj);
		}
	}

	public void register(IInstabilityBonus provider) {
		bonuses.add(provider);
	}

	public int getResult() {
		return total;
	}

	public void tick(World world) {
		int total = 0;
		for (IInstabilityBonus bonus : bonuses) {
			bonus.tick(world);
			total += bonus.getValue();
		}
		this.total = total;
	}

	public boolean isInstabilityEnabled() {
		return controller.isInstabilityEnabled();
	}
}
