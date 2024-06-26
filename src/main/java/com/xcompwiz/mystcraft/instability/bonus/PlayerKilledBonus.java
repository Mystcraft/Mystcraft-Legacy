package com.xcompwiz.mystcraft.instability.bonus;

import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnEntityDeath;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerChangedDimension;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerLoggedIn;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager.IInstabilityBonus;
import com.xcompwiz.mystcraft.network.NetworkUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class PlayerKilledBonus implements IInstabilityBonus, IOnEntityDeath, IOnPlayerChangedDimension, IOnPlayerLoggedIn {

	private InstabilityBonusManager bonusmanager;
	private String name;

	private int max;
	private int min;
	private float decayrate;
	private float current;

	private String playername;
	private int dimensionid;

	public PlayerKilledBonus(InstabilityBonusManager bonusmanager, Integer dimensionid, String playername, Integer max, Float decayrate) {
		this.bonusmanager = bonusmanager;
		current = 0;
		this.playername = playername;
		this.max = max;
		this.min = 0;
		this.decayrate = decayrate;
		this.dimensionid = dimensionid;
		this.name = "Player Killed: " + playername;

		EventManager eventmgr = EventManager.get();
		eventmgr.register((IOnEntityDeath) this);
		eventmgr.register((IOnPlayerChangedDimension) this);
		eventmgr.register((IOnPlayerLoggedIn) this);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getValue() {
		return -(int) current;
	}

	@Override
	public void tick(World world) {
		current = Math.max(min, current - decayrate);
	}

	@Override
	public void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity().dimension == dimensionid && event.getEntity().getName().equals(playername)) {
			if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
				current = max;
				announce("instability.bonus.death", playername, event.getSource().getTrueSource().getName());
			} else {
				current = Math.max(max / 2, current);
				announce("instability.bonus.death.partial", playername);
			}
		}
	}

	@Override
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (event.player.dimension == dimensionid && event.player.getName().equals(playername)) {
			announce("instability.bonus.death.alert", playername);
		}
	}

	@Override
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		if (event.toDim == dimensionid && event.player.getName().equals(playername)) {
			announce("instability.bonus.death.alert", playername);
		}
	}

	private void announce(String string, Object... args) {
		if (!bonusmanager.isInstabilityEnabled())
			return;
		ITextComponent chatcomponent = new TextComponentTranslation(string, args);
		NetworkUtils.sendMessageToPlayersInWorld(chatcomponent, this.dimensionid);
	}
}
