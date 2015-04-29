package com.xcompwiz.mystcraft.symbol;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.world.biome.WorldChunkManager;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.api.world.logic.ICelestial;
import com.xcompwiz.mystcraft.api.world.logic.IChunkProviderFinalization;
import com.xcompwiz.mystcraft.api.world.logic.IDynamicColorProvider;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.api.world.logic.ILightingController;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ISpawnModifier;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainFeatureLocator;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;
import com.xcompwiz.mystcraft.api.world.logic.IWeatherController;
import com.xcompwiz.mystcraft.api.world.logic.Modifier;

public class SymbolProfiler implements AgeDirector {

	private HashMap<Class<?>, HashSet<IAgeSymbol>>	registrations	= new HashMap<Class<?>, HashSet<IAgeSymbol>>();
	private HashSet<IAgeSymbol>						cloudHeight		= new HashSet<IAgeSymbol>();
	private HashSet<IAgeSymbol>						horizon			= new HashSet<IAgeSymbol>();
	private HashSet<IAgeSymbol>						grndlevel		= new HashSet<IAgeSymbol>();
	private HashSet<IAgeSymbol>						sealevel		= new HashSet<IAgeSymbol>();
	private HashSet<IAgeSymbol>						renderHorizon	= new HashSet<IAgeSymbol>();
	private HashSet<IAgeSymbol>						renderVoid		= new HashSet<IAgeSymbol>();
	private HashSet<IAgeSymbol>						pvpEnabled		= new HashSet<IAgeSymbol>();
	private HashMap<String, HashSet<IAgeSymbol>>	setsModifier	= new HashMap<String, HashSet<IAgeSymbol>>();
	private HashMap<String, HashSet<IAgeSymbol>>	popsModifier	= new HashMap<String, HashSet<IAgeSymbol>>();
	private HashSet<IAgeSymbol>						clearsModifiers	= new HashSet<IAgeSymbol>();
	private HashSet<IAgeSymbol>						addsInstability	= new HashSet<IAgeSymbol>();
	private IAgeSymbol								agent			= null;

	public void startProfiling(IAgeSymbol symbol) {
		if (this.agent != null) throw new RuntimeException("Attempting to profile multiple symbols at once!");
		this.agent = symbol;
	}

	public void endProfiling(IAgeSymbol symbol) {
		if (this.agent != symbol) throw new RuntimeException("Mismatch in symbol profiler termination!");
		this.agent = null;
	}

	public void remove(IAgeSymbol symbol) {
		for (HashSet<IAgeSymbol> list : registrations.values()) {
			list.remove(symbol);
		}
		for (HashSet<IAgeSymbol> list : setsModifier.values()) {
			list.remove(symbol);
		}
		for (HashSet<IAgeSymbol> list : popsModifier.values()) {
			list.remove(symbol);
		}
		cloudHeight.remove(symbol);
		horizon.remove(symbol);
		grndlevel.remove(symbol);
		sealevel.remove(symbol);
		renderHorizon.remove(symbol);
		renderVoid.remove(symbol);
		pvpEnabled.remove(symbol);
		clearsModifiers.remove(symbol);
		addsInstability.remove(symbol);
	}

	public HashSet<IAgeSymbol> getSymbolsProviding(Class<?> interface1) {
		return getInterfaceList(interface1);
	}

	private HashSet<IAgeSymbol> getInterfaceList(Class<?> class1) {
		if (!registrations.containsKey(class1)) registrations.put(class1, new HashSet<IAgeSymbol>());
		return registrations.get(class1);
	}

	@Override
	public long getTime() {
		return 0;
	}

	@Override
	public int getInstabilityScore() {
		return 0;
	}

	@Override
	public float getCloudHeight() {
		return 0;
	}

	@Override
	public double getHorizon() {
		return 0;
	}

	@Override
	public int getAverageGroundLevel() {
		return 0;
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public long getSeed() {
		return 0;
	}

	@Override
	public ColorGradient getSunriseSunsetColor() {
		return null;
	}

	@Override
	public WorldChunkManager getWorldChunkManager() {
		return null;
	}

	@Override
	public void addInstability(int instability) {
		if (agent == null) return;
		addsInstability.add(agent);
	}

	@Override
	public void setCloudHeight(float height) {
		if (agent == null) return;
		cloudHeight.add(agent);
	}

	@Override
	public void setHorizon(double height) {
		if (agent == null) return;
		horizon.add(agent);
	}

	@Override
	public void setAverageGroundLevel(int height) {
		if (agent == null) return;
		grndlevel.add(agent);
	}

	@Override
	public void setSeaLevel(int height) {
		if (agent == null) return;
		sealevel.add(agent);
	}

	@Override
	public void setDrawHorizon(boolean flag) {
		if (agent == null) return;
		renderHorizon.add(agent);
	}

	@Override
	public void setDrawVoid(boolean flag) {
		if (agent == null) return;
		renderVoid.add(agent);
	}

	@Override
	public void setPvPEnabled(boolean flag) {
		if (agent == null) return;
		pvpEnabled.add(agent);
	}

	@Override
	public void registerInterface(IBiomeController controller) {
		if (agent == null) return;
		getInterfaceList(IBiomeController.class).add(agent);
	}

	@Override
	public void registerInterface(ITerrainGenerator terrainGen) {
		if (agent == null) return;
		getInterfaceList(ITerrainGenerator.class).add(agent);
	}

	@Override
	public void registerInterface(ILightingController reg) {
		if (agent == null) return;
		getInterfaceList(ILightingController.class).add(agent);
	}

	@Override
	public void registerInterface(IWeatherController reg) {
		if (agent == null) return;
		getInterfaceList(IWeatherController.class).add(agent);
	}

	@Override
	public void registerInterface(ITerrainAlteration reg) {
		if (agent == null) return;
		getInterfaceList(ITerrainAlteration.class).add(agent);
	}

	@Override
	public void registerInterface(IChunkProviderFinalization reg) {
		if (agent == null) return;
		getInterfaceList(IChunkProviderFinalization.class).add(agent);
	}

	@Override
	public void registerInterface(IPopulate reg) {
		if (agent == null) return;
		getInterfaceList(IPopulate.class).add(agent);
	}

	@Override
	public void registerInterface(ITerrainFeatureLocator reg) {
		if (agent == null) return;
		getInterfaceList(ITerrainFeatureLocator.class).add(agent);
	}

	@Override
	public void registerInterface(ISpawnModifier reg) {
		if (agent == null) return;
		getInterfaceList(ISpawnModifier.class).add(agent);
	}

	@Override
	public void registerInterface(IDynamicColorProvider reg, String type) {
		if (agent == null) return;
		getInterfaceList(IDynamicColorProvider.class).add(agent);
	}

	@Override
	public void registerInterface(IStaticColorProvider reg, String type) {
		if (agent == null) return;
		getInterfaceList(IStaticColorProvider.class).add(agent);
	}

	@Override
	public void registerInterface(IEnvironmentalEffect reg) {
		if (agent == null) return;
		getInterfaceList(IEnvironmentalEffect.class).add(agent);
	}

	@Override
	public void registerInterface(ICelestial reg) {
		if (agent == null) return;
		getInterfaceList(ICelestial.class).add(agent);
	}

	private HashSet<IAgeSymbol> getModifierList(HashMap<String, HashSet<IAgeSymbol>> map, String id) {
		if (!map.containsKey(id)) map.put(id, new HashSet<IAgeSymbol>());
		return map.get(id);
	}

	@Override
	public void setModifier(String id, Object obj) {
		if (agent == null) return;
		getModifierList(setsModifier, id).add(agent);
	}

	@Override
	public void setModifier(String id, Modifier obj) {
		if (agent == null) return;
		getModifierList(setsModifier, id).add(agent);
	}

	@Override
	public Modifier popModifier(String id) {
		if (agent == null) return null;
		getModifierList(popsModifier, id).add(agent);
		return new Modifier();
	}

	@Override
	public void clearModifiers() {
		if (agent == null) return;
		clearsModifiers.add(agent);
	}
}
