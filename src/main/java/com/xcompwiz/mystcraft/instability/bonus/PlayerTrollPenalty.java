package com.xcompwiz.mystcraft.instability.bonus;

import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnEntityDeath;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerChangedDimension;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerLoggedIn;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerLoggedOut;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager.IInstabilityBonus;
import com.xcompwiz.mystcraft.network.NetworkUtils;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class PlayerTrollPenalty implements IInstabilityBonus, IOnEntityDeath, IOnPlayerChangedDimension, IOnPlayerLoggedIn, IOnPlayerLoggedOut {

	private InstabilityBonusManager bonusmanager;
	private String name;

	private int max;
	private int min;
	private float decayrate;
	private float current;

	private String playername;
	private int dimensionid;
	private boolean playerisinworld;

	public PlayerTrollPenalty(InstabilityBonusManager bonusmanager, Integer dimensionId, String playername, Integer max, Float decayrate) {
		this.bonusmanager = bonusmanager;
		current = 0;
		this.playername = playername;
		this.max = max;
		this.min = 0;
		this.decayrate = decayrate;
		this.dimensionid = dimensionId;
		this.name = "Player: " + playername;

		EventManager eventmgr = EventManager.get();
		eventmgr.register((IOnEntityDeath) this);
		eventmgr.register((IOnPlayerChangedDimension) this);
		eventmgr.register((IOnPlayerLoggedIn) this);
		eventmgr.register((IOnPlayerLoggedOut) this);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getValue() {
		return (int) current;
	}

	@Override
	public void tick(World world) {
		if (playerisinworld) {
			current = Math.min(max, current + decayrate);
		} else {
			current = Math.max(min, current - decayrate);
		}
	}

	@Override
	public void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity().dimension == dimensionid && event.getEntity().getName().equals(playername)) {
			current = min;
			announce("instability.bonus.troll.death", playername);
		}
	}

	@Override
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (event.player.dimension == dimensionid && event.player.getName().equals(playername)) {
			playerisinworld = true;
			announce("instability.bonus.troll.alert", playername);
		}
	}

	@Override
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		if (event.player.dimension == dimensionid && event.player.getName().equals(playername)) {
			playerisinworld = false;
			announce("instability.bonus.troll.left", playername);
		}
	}

	@Override
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		if (event.fromDim == dimensionid && event.player.getName().equals(playername)) {
			playerisinworld = false;
			announce("instability.bonus.troll.left", playername);
		}
		if (event.toDim == dimensionid && event.player.getName().equals(playername)) {
			playerisinworld = true;
			announce("instability.bonus.troll.alert", playername);
		}
	}

	private void announce(String string, Object... args) {
		if (!bonusmanager.isInstabilityEnabled())
			return;
		ITextComponent cmp = new TextComponentTranslation(string, args);
		NetworkUtils.sendMessageToPlayersInWorld(cmp, this.dimensionid);
	}
}
