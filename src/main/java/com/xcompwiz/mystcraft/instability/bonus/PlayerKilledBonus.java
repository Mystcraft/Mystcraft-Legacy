package com.xcompwiz.mystcraft.instability.bonus;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnEntityDeath;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerChangedDimension;
import com.xcompwiz.mystcraft.instability.bonus.EventManager.IOnPlayerLoggedIn;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager.IInstabilityBonus;
import com.xcompwiz.mystcraft.network.NetworkUtils;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class PlayerKilledBonus implements IInstabilityBonus, IOnEntityDeath, IOnPlayerChangedDimension, IOnPlayerLoggedIn {

	private InstabilityBonusManager	bonusmanager;

	private int		max;
	private int		min;
	private float	decayrate;
	private float	current;

	private String	playername;
	private int		dimensionid;

	public PlayerKilledBonus(InstabilityBonusManager bonusmanager, World worldObj, String playername, Integer max, Float decayrate) {
		this.bonusmanager = bonusmanager;
		current = 0;
		this.playername = playername;
		this.max = max;
		this.min = 0;
		this.decayrate = decayrate;
		this.dimensionid = worldObj.provider.dimensionId;

		EventManager eventmgr = EventManager.get();
		eventmgr.register((IOnEntityDeath)this);
		eventmgr.register((IOnPlayerChangedDimension)this);
		eventmgr.register((IOnPlayerLoggedIn)this);
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
		if (event.entity.dimension == dimensionid && event.entity.getCommandSenderName().equals(playername)) {
			if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer) {
				current = max;
				announce("instability.bonus.death", playername, event.source.getEntity().getCommandSenderName());
			} else {
				current = Math.max(max/2, current);
				announce("instability.bonus.death.partial", playername);
			}
		}
	}

	@Override
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (event.player.dimension == dimensionid && event.player.getCommandSenderName().equals(playername)) {
			announce("instability.bonus.death.alert", playername);
		}
	}

	@Override
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		if (event.toDim == dimensionid && event.player.getCommandSenderName().equals(playername)) {
			announce("instability.bonus.death.alert", playername);
		}
	}

	private void announce(String string, Object... args) {
		if (!bonusmanager.isInstabilityEnabled()) return;
		IChatComponent chatcomponent = new ChatComponentTranslation(string, args);
		NetworkUtils.sendMessageToPlayersInWorld(chatcomponent, this.dimensionid);
	}
}
