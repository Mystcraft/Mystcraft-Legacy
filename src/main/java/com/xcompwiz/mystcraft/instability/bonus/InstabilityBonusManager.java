package com.xcompwiz.mystcraft.instability.bonus;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.World;

import com.xcompwiz.mystcraft.world.IAgeController;
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

	private static Set<IInstabilityBonusProvider>	bonusproviders	= new HashSet<IInstabilityBonusProvider>();

	public static void registerBonusProvider(IInstabilityBonusProvider provider) {
		bonusproviders.add(provider);
	}

	private Set<IInstabilityBonus>	bonuses	= new HashSet<IInstabilityBonus>();
	private int						total;

	public InstabilityBonusManager(WorldProviderMyst provider, IAgeController agecontroller) {
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

	//TODO: Change value tracking to allow saving through this object
	public void tick(World world) {
		int total = 0;
		for (IInstabilityBonus bonus : bonuses) {
			bonus.tick(world);
			total += bonus.getValue();
		}
		this.total = total;
	}
}
