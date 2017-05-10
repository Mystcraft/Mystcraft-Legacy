package com.xcompwiz.mystcraft.instability.bonus;

import java.util.HashSet;
import java.util.Set;

import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DefaultValueCallback;
import com.xcompwiz.mystcraft.world.AgeController;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public class InstabilityBonusManager {

	//XXX: Move to own (for API)
	public interface IInstabilityBonus {

		public String getName();

		public int getValue();

		public void tick(World world);

	}

	//XXX: Move to own (for API)
	public interface IInstabilityBonusProvider {

		public void register(InstabilityBonusManager bonusmanager, Integer dimId);

	}

	public static final InstabilityBonusManager		ZERO			= new InstabilityBonusManager();

	private static Set<IInstabilityBonusProvider>	bonusproviders	= new HashSet<IInstabilityBonusProvider>();

	public static void registerBonusProvider(IInstabilityBonusProvider provider) {
		bonusproviders.add(provider);
	}

	private Set<IInstabilityBonus>	bonuses	= new HashSet<IInstabilityBonus>();
	private int						total;

	private AgeController			controller;

	public InstabilityBonusManager() {}

	public InstabilityBonusManager(WorldProviderMyst provider, AgeController agecontroller) {
		this.controller = agecontroller;
		for (IInstabilityBonusProvider bprovider : bonusproviders) {
			bprovider.register(this, provider.getDimension());
		}
	}

	//XXX: Encapsulate this to prevent external registration?
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

	public void registerDebugInfo(DebugNode node) {
		for (final IInstabilityBonus bonus : this.bonuses) {
			node.addChild(bonus.getName().replaceAll("\\.", "_").replaceAll(" ", ""), new DefaultValueCallback() {
				@Override
				public String get(ICommandSender agent) {
					return "" + bonus.getValue();
				}
			});
		}
	}
}
