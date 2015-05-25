package com.xcompwiz.mystcraft.instability.bonus;

import java.util.concurrent.ConcurrentMap;

import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.google.common.collect.MapMaker;
import com.xcompwiz.mystcraft.linking.DimensionUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class EventManager {

	public interface IOnEntityDeath {
		void onEntityDeath(LivingDeathEvent event);
	}

	public interface IOnPlayerChangedDimension {
		void onPlayerChangedDimension(PlayerChangedDimensionEvent event);
	}

	public interface IOnPlayerLoggedIn {
		void onPlayerLoggedIn(PlayerLoggedInEvent event);
	}

	public interface IOnPlayerLoggedOut {
		void onPlayerLoggedOut(PlayerLoggedOutEvent event);
	}

	private static EventManager	instance;

	public static EventManager get() {
		if (instance == null) { throw new RuntimeException("Event Listener Manager for Instability Bonus System not registered."); }
		return instance;
	}

	public static void set(EventManager newinstance) {
		if (instance != null) { throw new RuntimeException("Event Listener Manager for Instability Bonus System registered multiple times."); }
		instance = newinstance;
	}

	//TODO: Clean this up to use a more generic registration method
	private static ConcurrentMap<IOnEntityDeath, Boolean>				entitydeathlisteners	= new MapMaker().weakKeys().weakValues().<IOnEntityDeath, Boolean> makeMap();
	private static ConcurrentMap<IOnPlayerChangedDimension, Boolean>	dimchangelisteners		= new MapMaker().weakKeys().weakValues().<IOnPlayerChangedDimension, Boolean> makeMap();
	private static ConcurrentMap<IOnPlayerLoggedIn, Boolean>			loggedinlisteners		= new MapMaker().weakKeys().weakValues().<IOnPlayerLoggedIn, Boolean> makeMap();
	private static ConcurrentMap<IOnPlayerLoggedOut, Boolean>			loggedoutlisteners		= new MapMaker().weakKeys().weakValues().<IOnPlayerLoggedOut, Boolean> makeMap();

	public void register(IOnEntityDeath listener) {
		entitydeathlisteners.put(listener, true);
	}

	public void register(IOnPlayerChangedDimension listener) {
		dimchangelisteners.put(listener, true);
	}

	public void register(IOnPlayerLoggedIn listener) {
		loggedinlisteners.put(listener, true);
	}

	public void register(IOnPlayerLoggedOut listener) {
		loggedoutlisteners.put(listener, true);
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		DimensionUtils.setPlayerDimensionUUID(event.player, DimensionUtils.getDimensionUUID(event.toDim));
		for (IOnPlayerChangedDimension listener : dimchangelisteners.keySet()) {
			listener.onPlayerChangedDimension(event);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		for (IOnPlayerLoggedIn listener : loggedinlisteners.keySet()) {
			listener.onPlayerLoggedIn(event);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		for (IOnPlayerLoggedOut listener : loggedoutlisteners.keySet()) {
			listener.onPlayerLoggedOut(event);
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		for (IOnEntityDeath listener : entitydeathlisteners.keySet()) {
			listener.onEntityDeath(event);
		}
	}
}
