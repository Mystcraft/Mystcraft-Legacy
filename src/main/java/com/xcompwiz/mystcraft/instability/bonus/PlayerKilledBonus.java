package com.xcompwiz.mystcraft.instability.bonus;

import net.minecraft.world.World;

import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager.IInstabilityBonus;

public class PlayerKilledBonus implements IInstabilityBonus {

	public PlayerKilledBonus(InstabilityBonusManager bonusmanager, World worldObj, String playername, int amount, float decayrate) {
	}

	@Override
	public int getValue() {
		return 0;
	}

	@Override
	public void tick(World world) {
	}

}
