package com.xcompwiz.mystcraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.api.world.logic.IChunkProviderFinalization;
import com.xcompwiz.mystcraft.api.world.logic.ICloudColorProvider;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.api.world.logic.IFogColorProvider;
import com.xcompwiz.mystcraft.api.world.logic.ILightingController;
import com.xcompwiz.mystcraft.api.world.logic.IMoon;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ISkyColorProvider;
import com.xcompwiz.mystcraft.api.world.logic.ISkyDoodad;
import com.xcompwiz.mystcraft.api.world.logic.ISpawnModifier;
import com.xcompwiz.mystcraft.api.world.logic.IStarfield;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.api.world.logic.ISun;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainFeatureLocator;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;
import com.xcompwiz.mystcraft.api.world.logic.IWeatherController;
import com.xcompwiz.mystcraft.api.world.logic.Modifier;
import com.xcompwiz.mystcraft.client.render.CloudRendererMyst;
import com.xcompwiz.mystcraft.client.render.WeatherRendererMyst;
import com.xcompwiz.mystcraft.core.DebugDataTracker;
import com.xcompwiz.mystcraft.data.DebugFlags;
import com.xcompwiz.mystcraft.instability.InstabilityController;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.util.SpiralOutwardIterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AgeController implements IAgeController {
	private World								world;
	private WorldChunkManager					chunkManager;
	private SkyRendererMyst						skyrenderer;
	private CloudRendererMyst					cloudrenderer;
	private WeatherRendererMyst					weatherrenderer;
	private AgeData								agedata;
	private InstabilityController				instabilityController;
	private InstabilityBonusManager				instabilitybonusmanager;

	private Random								symbolseedrand;

	private IBiomeController					biomeController;
	private IWeatherController					weatherController;
	private ITerrainGenerator					genTerrain;
	private ILightingController					lightingController;

	private List<ISun>							suns;
	private List<IMoon>							moons;
	private List<IStarfield>					starfields;
	private List<ISkyDoodad>					skyDoodads;
	private List<ITerrainAlteration>			terrainalterations;
	private List<IChunkProviderFinalization>	chunkfinalizers;
	private List<IPopulate>						populateFuncs;
	private List<ITerrainFeatureLocator>		featureLocators;
	private List<IFogColorProvider>				fogColorProviders;
	private List<ISkyColorProvider>				skyColorProviders;
	private List<IStaticColorProvider>			staticColorProviders;
	private List<ICloudColorProvider>			cloudColorProvider;
	private List<ISpawnModifier>				creatureAffecters;
	private List<IEnvironmentalEffect>			envEffects;

	private Float								cloudHeight;
	private Double								horizon;
	private Integer								groundlevel;
	private Integer								sealevel;
	private Boolean								renderHorizon;
	private Boolean								renderVoid;
	private Boolean								pvpEnabled;
	private HashMap<String, Modifier>			modifiers;
	private HashMap<String, Modifier>			globalMods;

	private int									symbolinstability;
	private Integer								blockinstability	= null;
	private HashMap<IAgeSymbol, Integer>		symbolcounts		= new HashMap<IAgeSymbol, Integer>();

	private Semaphore							semaphore			= new Semaphore(1, true);
	private boolean								rebuilding;

	public AgeController(World worldObj, AgeData age) {
		world = worldObj;
		agedata = age;
		chunkManager = new WorldChunkManagerMyst(this);
		skyrenderer = new SkyRendererMyst((WorldProviderMyst) world.provider, this);
		cloudrenderer = new CloudRendererMyst((WorldProviderMyst) world.provider, this);
		weatherrenderer = new WeatherRendererMyst((WorldProviderMyst) world.provider, this);
		reconstruct();
	}

	// XXX: The current system uses jit rebuilding. Perhaps a signaled builder would be better, with a hold function to prevent access during the build?
	private void validate() {
		try {
			if (agedata.isUpdated()) {
				semaphore.acquire();
				rebuilding = true;
				reconstruct();
				rebuilding = false;
				semaphore.release();
			}
			while (rebuilding) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
		}
	}

	private synchronized void reconstruct() {
		agedata.onConstruct();
		long seed = agedata.getSeed();
		symbolseedrand = new Random(seed);

		biomeController = null;
		weatherController = null;
		genTerrain = null;
		lightingController = null;
		suns = new ArrayList<ISun>();
		moons = new ArrayList<IMoon>();
		starfields = new ArrayList<IStarfield>();
		skyDoodads = new ArrayList<ISkyDoodad>();
		terrainalterations = new ArrayList<ITerrainAlteration>();
		chunkfinalizers = new ArrayList<IChunkProviderFinalization>();
		populateFuncs = new ArrayList<IPopulate>();
		featureLocators = new ArrayList<ITerrainFeatureLocator>();
		fogColorProviders = new ArrayList<IFogColorProvider>();
		skyColorProviders = new ArrayList<ISkyColorProvider>();
		cloudColorProvider = new ArrayList<ICloudColorProvider>();
		staticColorProviders = new ArrayList<IStaticColorProvider>();
		creatureAffecters = new ArrayList<ISpawnModifier>();
		envEffects = new ArrayList<IEnvironmentalEffect>();
		cloudHeight = null;
		horizon = null;
		groundlevel = null;
		sealevel = null;
		renderHorizon = null;
		renderVoid = null;
		pvpEnabled = null;
		globalMods = new HashMap<String, Modifier>();
		modifiers = new HashMap<String, Modifier>();
		symbolinstability = 0;
		// conflicts = new ArrayList<ISun>();
		instabilityController = null;

		// Read Symbols
		List<String> symbols = new ArrayList<String>(agedata.getSymbols(world.isRemote));
		for (String name : symbols) {
			IAgeSymbol symbol = SymbolManager.getAgeSymbol(name);
			if (symbol == null) {
				LoggerUtils.error("Attempting to generate age containing unmatched symbol " + name + ".  Results are undefined.");
			} else {
				addSymbol(symbol);
			}
		}

		if (biomeController == null) {
			IAgeSymbol symbol = SymbolManager.findAgeSymbolImplementing(new Random(agedata.getSeed()), IBiomeController.class);
			addSymbol(symbol);
			agedata.addSymbol(symbol.identifier(), InstabilityData.missing.controller);
		}
		if (genTerrain == null) {
			IAgeSymbol symbol = SymbolManager.findAgeSymbolImplementing(new Random(agedata.getSeed()), ITerrainGenerator.class);
			addSymbol(symbol);
			agedata.addSymbol(symbol.identifier(), InstabilityData.missing.controller);
		}
		if (lightingController == null) {
			IAgeSymbol symbol = SymbolManager.findAgeSymbolImplementing(new Random(agedata.getSeed()), ILightingController.class);
			addSymbol(symbol);
			agedata.addSymbol(symbol.identifier(), 0);
		}
		if (weatherController == null) {
			IAgeSymbol symbol = SymbolManager.findAgeSymbolImplementing(new Random(agedata.getSeed()), IWeatherController.class);
			addSymbol(symbol);
			agedata.addSymbol(symbol.identifier(), InstabilityData.missing.controller);
		}

		weatherController.setDataObject(agedata.getStorageObject("weather"));
		for (Modifier mod : modifiers.values()) {
			symbolinstability += mod.dangling;
		}
		globalMods.putAll(modifiers);
		modifiers.clear();

		lightingController.generateLightBrightnessTable(this.world.provider.lightBrightnessTable);
		agedata.markVisited();

		if (DebugFlags.instability) DebugDataTracker.set(agedata.getAgeName() + ".instability.symbols", "" + symbolinstability);
		if (DebugFlags.instability) DebugDataTracker.set(agedata.getAgeName() + ".instability.book", "" + agedata.getBaseInstability());
	}

	private void addSymbol(IAgeSymbol symbol) {
		symbol.registerLogic(this, symbolseedrand.nextLong());
		Integer count = symbolcounts.get(symbol);
		if (count == null) {
			count = 1;
		} else {
			++count;
		}
		symbolcounts.put(symbol, count);
		symbolinstability += symbol.instabilityModifier(count);
	}

	@Override
	public long getTime() {
		return world.getWorldTime();
	}

	public boolean isInstabilityEnabled() {
		return Mystcraft.instabilityEnabled && agedata.isInstabilityEnabled();
	}

	@Override
	public int getInstabilityScore() {
		if (rebuilding) throw new RuntimeException("Someone is trying to grab the world instability score before the world is built!");
		ChunkProfiler profiler = getChunkProfiler();
		if (profiler.getCount() < 400 || blockinstability == null) {
			updateProfiledInstability();
		}
		int score = symbolinstability + blockinstability + agedata.getBaseInstability() + getInstabilityBonusManager().getResult();
		if (DebugFlags.instability) DebugDataTracker.set(agedata.getAgeName() + ".instability", "" + (symbolinstability + blockinstability + agedata.getBaseInstability() + getInstabilityBonusManager().getResult()));
		if (DebugFlags.instability) DebugDataTracker.set(agedata.getAgeName() + ".instability.bonus", "" + getInstabilityBonusManager().getResult());
		int difficulty = Mystcraft.difficulty;
		switch (difficulty) {
		case 0:
			score *= 0.25F;
			break;
		case 1:
			score *= 0.5F;
			break;
		case 2:
			break;
		case 3:
			score *= 1.75F;
			break;
		}
		return score;
	}

	public void updateProfiledInstability() {
		ChunkProfiler profiler = getChunkProfiler();
		if (profiler.getCount() < 400) {
			expandChunkProfile();
		}
		blockinstability = profiler.calculateInstability();
		if (DebugFlags.instability) DebugDataTracker.set(agedata.getAgeName() + ".instability.blocks", "" + blockinstability);
		if (DebugFlags.instability) DebugDataTracker.set(agedata.getAgeName() + ".profiled", "" + profiler.getCount());
	}

	private void expandChunkProfile() {
		if (!(world instanceof WorldServer)) return;
		ChunkProfiler profiler = getChunkProfiler();

		ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
		chunkcoordinates.posX >>= 4;
		chunkcoordinates.posZ >>= 4;

		IChunkProvider chunkgen = ((WorldServer) this.world).theChunkProviderServer;
		IChunkLoader chunkloader = ((WorldServer) this.world).theChunkProviderServer.currentChunkLoader;
		SpiralOutwardIterator iter = new SpiralOutwardIterator();
		while (profiler.getCount() <= 400) {
			iter.step();
			int chunkX = chunkcoordinates.posX + iter.x;
			int chunkZ = chunkcoordinates.posZ + iter.y;
			if (safeLoadChunk(chunkloader, world, chunkX, chunkZ) == null) {
				chunkgen.loadChunk(chunkX, chunkZ);
			}
		}
	}

	private Chunk safeLoadChunk(IChunkLoader chunkloader, World worldObj, int par1, int par2) {
		if (chunkloader == null) { return null; }
		try {
			return chunkloader.loadChunk(worldObj, par1, par2);
		} catch (Exception exception) {
			return null;
		}
	}

	// public boolean hasConflicts(){return conflicts.size() > 0;}
	// public List<AgeSymbol> getConflicts(){return new ArrayList(conflicts);}

	// Use Functions//
	@Override
	public float getCloudHeight() {
		return cloudHeight != null ? cloudHeight : 128;
	}

	@Override
	public double getHorizon() {
		return horizon != null ? horizon : 63;
	}

	@Override
	public int getAverageGroundLevel() {
		return groundlevel != null ? groundlevel : 64;
	}

	@Override
	public int getSeaLevel() {
		return sealevel != null ? sealevel : 63;
	}

	public boolean shouldRenderHorizon() {
		return renderHorizon != null ? renderHorizon : true;
	}

	public boolean shouldRenderVoid() {
		return renderVoid != null ? renderVoid : true;
	}

	public boolean isPvPEnabled() {
		return pvpEnabled != null ? pvpEnabled : true;
	}

	public Vec3 getFogColor(float celestial_angle, float time) {
		validate();
		if (fogColorProviders == null || fogColorProviders.size() == 0) { return null; }
		Vec3 color = null;
		for (IFogColorProvider mod : fogColorProviders) {
			Color op = mod.getFogColor(celestial_angle, time);
			if (op == null) continue;
			if (color == null) {
				color = Vec3.createVectorHelper(op.r, op.g, op.b);
			} else {
				color.xCoord = (color.xCoord + op.r) / 2;
				color.yCoord = (color.yCoord + op.g) / 2;
				color.zCoord = (color.zCoord + op.b) / 2;
			}
		}
		return color;
	}

	public Vec3 getCloudColor(float time, float celestial_angle) {
		validate();
		if (cloudColorProvider == null || cloudColorProvider.size() == 0) { return null; }
		Vec3 color = null;
		for (ICloudColorProvider mod : cloudColorProvider) {
			Color op = mod.getCloudColor(time, celestial_angle);
			if (op == null) continue;
			if (color == null) {
				color = Vec3.createVectorHelper(op.r, op.g, op.b);
			} else {
				color.xCoord = (color.xCoord + op.r) / 2;
				color.yCoord = (color.yCoord + op.g) / 2;
				color.zCoord = (color.zCoord + op.b) / 2;
			}
		}
		return color;
	}

	@Override
	public ColorGradient getSunriseSunsetColor() {
		Modifier sunset = globalMods.get("sunset");
		if (sunset == null) return null;
		return sunset.asGradient();
	}

	public Vec3 getSkyColor(Entity entity, BiomeGenBase biome, float time, float celestial_angle) {
		validate();
		if (skyColorProviders == null || skyColorProviders.size() == 0) { return null; }
		Vec3 color = null;
		for (ISkyColorProvider mod : skyColorProviders) {
			Color op = mod.getSkyColor(entity, biome, time, celestial_angle);
			if (op == null) continue;
			if (color == null) {
				color = Vec3.createVectorHelper(op.r, op.g, op.b);
			} else {
				color.xCoord = (color.xCoord + op.r) / 2;
				color.yCoord = (color.yCoord + op.g) / 2;
				color.zCoord = (color.zCoord + op.b) / 2;
			}
		}
		return color;
	}

	public Color getStaticColor(String string, BiomeGenBase biome, int x, int y, int z) {
		validate();
		if (staticColorProviders == null || staticColorProviders.size() == 0) { return null; }
		Color color = null;
		for (IStaticColorProvider mod : staticColorProviders) {
			Color op = mod.getStaticColor(string, this.world, biome, x, y, z);
			if (op == null) continue;
			if (color == null) {
				color = new Color(op.r, op.g, op.b);
			} else {
				color.average(op);
			}
		}
		return color;
	}

	private InstabilityController getInstabilityController() {
		if (instabilityController == null) {
			instabilityController = new InstabilityController((WorldProviderMyst) world.provider, this);
		}
		return instabilityController;
	}

	private InstabilityBonusManager getInstabilityBonusManager() {
		if (instabilitybonusmanager == null) {
			instabilitybonusmanager = new InstabilityBonusManager((WorldProviderMyst) world.provider, this);
		}
		return instabilitybonusmanager;
	}

	public IBiomeController getBiomeController() {
		validate();
		return biomeController;
	}

	public IWeatherController getWeatherController() {
		validate();
		return weatherController;
	}

	public ILightingController getLightingController() {
		validate();
		return lightingController;
	}

	public ITerrainGenerator getTerrainGenerator() {
		validate();
		return genTerrain;
	}

	public ChunkProfiler getChunkProfiler() {
		ChunkProfiler chunkprofiler = (ChunkProfiler) this.world.perWorldStorage.loadData(ChunkProfiler.class, ChunkProfiler.ID);
		if (chunkprofiler == null) {
			chunkprofiler = new ChunkProfiler(ChunkProfiler.ID);
			this.world.perWorldStorage.setData(ChunkProfiler.ID, chunkprofiler);
		}
		chunkprofiler.setDebugName(this.agedata.getAgeName());
		return chunkprofiler;
	}

	public void generateTerrain(int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
		validate();
		getTerrainGenerator().generateTerrain(chunkX, chunkZ, blocks, metadata);
	}

	public void modifyTerrain(int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
		validate();
		if (terrainalterations != null && terrainalterations.size() > 0) {
			for (ITerrainAlteration mod : terrainalterations) {
				mod.alterTerrain(world, chunkX, chunkZ, blocks, metadata);
			}
		}
	}

	public void finalizeChunk(Chunk chunk, int chunkX, int chunkZ) {
		validate();
		if (chunkfinalizers != null && chunkfinalizers.size() > 0) {
			for (IChunkProviderFinalization mod : chunkfinalizers) {
				mod.finalizeChunk(chunk, chunkX, chunkZ);
			}
		}
	}

	public void populate(World worldObj, Random rand, int i, int j) {
		validate();
		if (populateFuncs != null && populateFuncs.size() > 0) {
			boolean flag = false;
			for (IPopulate mod : populateFuncs) {
				flag = flag || mod.populate(worldObj, rand, i, j, flag);
			}
		}
	}

	public void tickBlocksAndAmbiance(Chunk chunk) {
		getWeatherController().tick(world, chunk);
		if (envEffects != null && envEffects.size() > 0) {
			for (IEnvironmentalEffect effect : envEffects) {
				effect.tick(world, chunk);
			}
		}
		getInstabilityController().tick(world, chunk);
	}

	public void tick() {
		if (world.isRemote) return;
		getInstabilityBonusManager().tick(world);
	}

	public List<SpawnListEntry> affectCreatureList(EnumCreatureType enumcreaturetype, List<SpawnListEntry> list, int i, int j, int k) {
		validate();
		if (list == null) {
			list = new ArrayList<SpawnListEntry>();
		}
		return list;
	}

	public ChunkPosition locateTerrainFeature(World world, String s, int i, int j, int k) {
		validate();
		if (featureLocators == null || featureLocators.size() == 0) { return null; }
		ChunkPosition found = null;
		for (ITerrainFeatureLocator mod : featureLocators) {
			found = mod.locate(world, s, i, j, k);
			if (found != null) { return found; }
		}
		return found;
	}

	public int scaleLighting(int blockLightValue) {
		validate();
		return getLightingController().scaleLighting(blockLightValue);
	}

	public void generateLightBrightnessTable(float[] lightBrightnessTable) {
		validate();
		getLightingController().generateLightBrightnessTable(lightBrightnessTable);
	}

	public float calculateCelestialAngle(long time, float f1) {
		validate();
		float lowest = 0.5F;
		float highest = 0.5F;
		float temp;
		for (ISun sun : suns) {
			temp = sun.getCelestialPeriod(time, f1);
			while (temp < 0.0F)
				temp++;
			while (temp > 1.0F)
				--temp;
			if (temp > highest) {
				highest = temp;
			}
			if (temp < lowest) {
				lowest = temp;
			}
		}
		if ((1 - highest) < lowest) return highest;
		return lowest;
	}

	public long getTimeToSunrise(long time) {
		Long result = null;
		Long temp;
		for (ISun sun : suns) {
			temp = sun.getTimeToSunrise(time);
			if (temp == null) continue;
			if (result == null || temp < result) {
				result = temp;
			}
		}
		if (result == null) {
			long dayinterval = 24000L;
			return (dayinterval - (time % dayinterval));
		}
		return result;
	}

	public float getTemperatureAtHeight(float temp, int y) {
		return temp;
	}

	@SideOnly(Side.CLIENT)
	public void renderStarfields(TextureManager eng, World worldObj, float partial) {
		for (IStarfield starfield : starfields) {
			starfield.render(eng, worldObj, partial);
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderSkyDoodads(TextureManager eng, World worldObj, float partial) {
		for (ISkyDoodad doodad : skyDoodads) {
			doodad.render(eng, worldObj, partial);
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderSuns(TextureManager eng, World worldObj, float partial) {
		for (ISun sun : suns) {
			sun.render(eng, worldObj, partial);
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderMoons(TextureManager eng, World worldObj, float partial) {
		for (IMoon moon : moons) {
			moon.render(eng, worldObj, partial);
		}
	}

	// Symbol data functions
	@Override
	public long getSeed() {
		return agedata.getSeed();
	}

	@Override
	public WorldChunkManager getWorldChunkManager() {
		return this.chunkManager;
	}

	public SkyRendererMyst getSkyRenderer() {
		return this.skyrenderer;
	}

	public CloudRendererMyst getCloudRenderer() {
		return this.cloudrenderer;
	}

	public WeatherRendererMyst getWeatherRenderer() {
		return this.weatherrenderer;
	}

	// Registration Functions//
	@Override
	public void addInstability(int instability) {
		this.symbolinstability += instability;
	}

	@Override
	public void setCloudHeight(float height) {
		if (cloudHeight == null) {
			cloudHeight = height;
		} else {
			cloudHeight = (cloudHeight + height) / 2;
		}
	}

	@Override
	public void setHorizon(double height) {
		if (horizon == null) {
			horizon = height;
		} else {
			horizon = (horizon + height) / 2;
		}
	}

	@Override
	public void setAverageGroundLevel(int height) {
		if (groundlevel != null) {
			groundlevel = (groundlevel + height) / 2;
		} else {
			groundlevel = height;
		}
	}

	@Override
	public void setSeaLevel(int height) {
		if (sealevel != null) {
			sealevel = (sealevel + height) / 2;
		} else {
			sealevel = height;
		}
	}

	@Override
	public void setDrawHorizon(boolean flag) {
		renderHorizon = flag;
	}

	@Override
	public void setDrawVoid(boolean flag) {
		renderVoid = flag;
	}

	@Override
	public void setPvPEnabled(boolean flag) {
		pvpEnabled = flag;
	}

	@Override
	public void registerInterface(IBiomeController controller) {
		if (biomeController != null) {
			// conflicts.add(agent);
			symbolinstability += InstabilityData.extra.controller;
		}
		biomeController = controller;
	}

	@Override
	public void registerInterface(ITerrainGenerator terrainGen) {
		if (genTerrain != null) {
			// conflicts.add(agent);
			symbolinstability += InstabilityData.extra.controller;
		}
		genTerrain = terrainGen;
	}

	@Override
	public void registerInterface(ILightingController reg) {
		if (lightingController != null) {
			// conflicts.add(agent);
			symbolinstability += InstabilityData.extra.controller;
		}
		lightingController = reg;
	}

	@Override
	public void registerInterface(IWeatherController reg) {
		if (weatherController != null) {
			// conflicts.add(agent);
			symbolinstability += InstabilityData.extra.controller;
		}
		weatherController = reg;
	}

	@Override
	public void registerInterface(ISun reg) {
		suns.add(reg);
	}

	@Override
	public void registerInterface(IMoon reg) {
		moons.add(reg);
	}

	@Override
	public void registerInterface(IStarfield reg) {
		starfields.add(reg);
	}

	@Override
	public void registerInterface(ISkyDoodad reg) {
		skyDoodads.add(reg);
	}

	@Override
	public void registerInterface(ITerrainAlteration reg) {
		terrainalterations.add(reg);
	}

	@Override
	public void registerInterface(IChunkProviderFinalization reg) {
		chunkfinalizers.add(reg);
	}

	@Override
	public void registerInterface(IPopulate reg) {
		populateFuncs.add(reg);
	}

	@Override
	public void registerInterface(ITerrainFeatureLocator reg) {
		featureLocators.add(reg);
	}

	@Override
	public void registerInterface(IFogColorProvider reg) {
		fogColorProviders.add(reg);
	}

	@Override
	public void registerInterface(ISkyColorProvider reg) {
		skyColorProviders.add(reg);
	}

	@Override
	public void registerInterface(IStaticColorProvider reg) {
		staticColorProviders.add(reg);
	}

	@Override
	public void registerInterface(ICloudColorProvider reg) {
		cloudColorProvider.add(reg);
	}

	@Override
	public void registerInterface(ISpawnModifier reg) {
		creatureAffecters.add(reg);
	}

	@Override
	public void registerInterface(IEnvironmentalEffect reg) {
		envEffects.add(reg);
	}

	@Override
	public Modifier popModifier(String id) {
		if (modifiers.containsKey(id)) return modifiers.remove(id);
		return new Modifier();
	}

	@Override
	public void setModifier(String id, Object obj) {
		setModifier(id, new Modifier(obj));
	}

	@Override
	public void setModifier(String id, Modifier val) {
		if (modifiers.containsKey(id)) {
			symbolinstability += modifiers.get(id).dangling;
		}
		modifiers.put(id, val);
	}

	@Override
	public void clearModifiers() {
		for (Modifier mod : modifiers.values()) {
			symbolinstability += mod.dangling * InstabilityData.clearPercentage;
		}
		modifiers.clear();
	}
}
