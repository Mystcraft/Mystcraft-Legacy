package com.xcompwiz.mystcraft.instability.bonus;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnEntityDeath;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerChangedDimension;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerLoggedIn;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerLoggedOut;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager.IInstabilityBonus;
import com.xcompwiz.mystcraft.network.NetworkUtils;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class PlayerTrollPenalty implements IInstabilityBonus, IOnEntityDeath, IOnPlayerChangedDimension, IOnPlayerLoggedIn, IOnPlayerLoggedOut {

	private InstabilityBonusManager	bonusmanager;

	private int						max;
	private int						min;
	private float					decayrate;
	private float					current;

	private String					playername;
	private int						dimensionid;
	private boolean					playerisinworld;

	public PlayerTrollPenalty(InstabilityBonusManager bonusmanager, World worldObj, String playername, Integer max, Float decayrate) {
		this.bonusmanager = bonusmanager;
		current = 0;
		this.playername = playername;
		this.max = max;
		this.min = 0;
		this.decayrate = decayrate;
		this.dimensionid = worldObj.provider.dimensionId;

		EventManager eventmgr = EventManager.get();
		eventmgr.register((IOnEntityDeath) this);
		eventmgr.register((IOnPlayerChangedDimension) this);
		eventmgr.register((IOnPlayerLoggedIn) this);
		eventmgr.register((IOnPlayerLoggedOut) this);
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
		if (event.entity.dimension == dimensionid && event.entity.getCommandSenderName().equals(playername)) {
			current = min;
			announce("instability.bonus.troll.death", playername);
		}
	}

	@Override
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (event.player.dimension == dimensionid && event.player.getCommandSenderName().equals(playername)) {
			playerisinworld = true;
			announce("instability.bonus.troll.alert", playername);
		}
	}

	@Override
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		if (event.player.dimension == dimensionid && event.player.getCommandSenderName().equals(playername)) {
			playerisinworld = false;
			announce("instability.bonus.troll.left", playername);
		}
	}

	@Override
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		if (event.fromDim == dimensionid && event.player.getCommandSenderName().equals(playername)) {
			playerisinworld = false;
			announce("instability.bonus.troll.left", playername);
		}
		if (event.toDim == dimensionid && event.player.getCommandSenderName().equals(playername)) {
			playerisinworld = true;
			announce("instability.bonus.troll.alert", playername);
		}
	}

	private void announce(String string, Object... args) {
		if (!bonusmanager.isInstabilityEnabled()) return;
		IChatComponent chatcomponent = new ChatComponentTranslation(string, args);
		NetworkUtils.sendMessageToPlayersInWorld(chatcomponent, this.dimensionid);
	}
}
