package com.xcompwiz.mystcraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.util.Color;
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
import com.xcompwiz.mystcraft.client.render.CloudRendererMyst;
import com.xcompwiz.mystcraft.client.render.WeatherRendererMyst;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugTaskCallback;
import com.xcompwiz.mystcraft.debug.DefaultValueCallback;
import com.xcompwiz.mystcraft.instability.InstabilityController;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.profiling.ChunkProfiler;
import com.xcompwiz.mystcraft.world.profiling.ChunkProfilerManager;
import com.xcompwiz.util.SpiralOutwardIterator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AgeController implements AgeDirector {

	private static final int								MINCHUNKS			= 400;

	private World											world;
	private BiomeProvider									chunkManager;
	private SkyRendererMyst									skyrenderer;
	private CloudRendererMyst								cloudrenderer;
	private WeatherRendererMyst								weatherrenderer;
	private AgeData											agedata;
	private InstabilityController							instabilityController;
	private InstabilityBonusManager							instabilitybonusmanager;

	private Random											symbolseedrand;

	private IBiomeController								biomeController;
	private IWeatherController								weatherController;
	private ITerrainGenerator								genTerrain;
	private ILightingController								lightingController;

	private List<ICelestial>								celestials;
	private List<ICelestial>								suns;
	private List<ITerrainAlteration>						terrainalterations;
	private List<IChunkProviderFinalization>				chunkfinalizers;
	private List<IPopulate>									populateFuncs;
	private List<ITerrainFeatureLocator>					featureLocators;
	private List<IStaticColorProvider>						foliageColorProviders;
	private List<IStaticColorProvider>						grassColorProviders;
	private List<IStaticColorProvider>						waterColorProviders;
	private List<IDynamicColorProvider>						cloudColorProviders;
	private List<IDynamicColorProvider>						fogColorProviders;
	private List<IDynamicColorProvider>						skyColorProviders;
	private List<ISpawnModifier>							creatureAffecters;
	private List<IEnvironmentalEffect>						envEffects;

	private HashMap<String, List<IDynamicColorProvider>>	dynamicColorLists	= new HashMap<String, List<IDynamicColorProvider>>();
	private HashMap<String, List<IStaticColorProvider>>		staticColorLists	= new HashMap<String, List<IStaticColorProvider>>();

	private Float											cloudHeight;
	private Double											horizon;
	private Integer											groundlevel;
	private Integer											sealevel;
	private Boolean											renderHorizon;
	private Boolean											renderVoid;
	private Boolean											pvpEnabled;
	private HashMap<String, Modifier>						modifiers;
	private HashMap<String, Modifier>						globalMods;

	private int												nextprofiled;
	private int												symbolinstability;
	private Integer											blockinstability	= null;
	private HashMap<IAgeSymbol, Integer>					symbolcounts		= new HashMap<IAgeSymbol, Integer>();
	protected int											debuginstability	= 0;

	private Semaphore										semaphore			= new Semaphore(1, true);
	private boolean											rebuilding;

	public AgeController(World worldObj, AgeData age) {
		world = worldObj;
		agedata = age;
		chunkManager = new BiomeProviderMyst(this);
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

		dynamicColorLists.clear();
		staticColorLists.clear();

		biomeController = null;
		weatherController = null;
		genTerrain = null;
		lightingController = null;
		celestials = new ArrayList<ICelestial>();
		suns = new ArrayList<ICelestial>();
		terrainalterations = new ArrayList<ITerrainAlteration>();
		chunkfinalizers = new ArrayList<IChunkProviderFinalization>();
		populateFuncs = new ArrayList<IPopulate>();
		featureLocators = new ArrayList<ITerrainFeatureLocator>();
		cloudColorProviders = new ArrayList<IDynamicColorProvider>();
		fogColorProviders = new ArrayList<IDynamicColorProvider>();
		skyColorProviders = new ArrayList<IDynamicColorProvider>();
		foliageColorProviders = new ArrayList<IStaticColorProvider>();
		grassColorProviders = new ArrayList<IStaticColorProvider>();
		waterColorProviders = new ArrayList<IStaticColorProvider>();
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

		dynamicColorLists.put(IDynamicColorProvider.CLOUD, cloudColorProviders);
		dynamicColorLists.put(IDynamicColorProvider.FOG, fogColorProviders);
		dynamicColorLists.put(IDynamicColorProvider.SKY, skyColorProviders);

		staticColorLists.put(IStaticColorProvider.FOLIAGE, foliageColorProviders);
		staticColorLists.put(IStaticColorProvider.GRASS, grassColorProviders);
		staticColorLists.put(IStaticColorProvider.WATER, waterColorProviders);

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

		lightingController.generateLightBrightnessTable(this.world.provider.getLightBrightnessTable());
		agedata.markVisited();
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

	public void registerDebugInfo(DebugNode node) {
		final ChunkProfiler profiler = this.getChunkProfiler();
		final InstabilityBonusManager bonusmanager = getInstabilityBonusManager();
//@formatter:off
		node.getOrCreateNode("experimental").addChild("mark_dead", new DebugTaskCallback() {
			@Override
			public void run(ICommandSender agent, Object... args) {
				int dimid = world.provider.getDimension();
				if (DimensionUtils.markDimensionDead(dimid)) {
					agent.sendMessage(new TextComponentString("Dimension " + dimid + " marked as dead."));
				} else {
					agent.sendMessage(new TextComponentString("ERROR: Could not mark dimension " + dimid + " as dead."));
				}
			}
		});

		node.addChild("profiled_chunks", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return "" + profiler.getCount(); }});
		node = node.getOrCreateNode("instability");
		node.addChild("symbols", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return "" + symbolinstability; }});
		node.addChild("debug", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return "" + debuginstability; } @Override public void set(ICommandSender agent, String value) { debuginstability = Integer.getInteger(value); }});
		node.addChild("book", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return "" + agedata.getBaseInstability(); }});
		node.addChild("total", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return "" + (symbolinstability + (blockinstability == null ? 0 : blockinstability) + agedata.getBaseInstability() + getInstabilityBonusManager().getResult()); }});
		node.addChild("bonus_total", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return "" + bonusmanager.getResult(); }});
		node.addChild("blocks_total", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return "" + blockinstability; }});
//@formatter:on	
		bonusmanager.registerDebugInfo(node.getOrCreateNode("bonus"));
		profiler.registerDebugInfo(node.getOrCreateNode("blocks"));
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
		int profiled = getChunkProfiler().getCount();
		if (profiled < MINCHUNKS || profiled > nextprofiled || blockinstability == null) {
			nextprofiled = profiled + 100;
			updateProfiledInstability();
		}
		if (blockinstability == null) return 0;
		int score = debuginstability + symbolinstability + blockinstability + agedata.getBaseInstability() + getInstabilityBonusManager().getResult();
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
		int chunksneeded = MINCHUNKS - getChunkProfiler().getCount();
		if (chunksneeded > 0 && ChunkProfilerManager.getSize() < chunksneeded) {
			expandChunkProfile();
		}
		if (getChunkProfiler().getCount() > MINCHUNKS) blockinstability = profiler.calculateInstability();
	}

	private void expandChunkProfile() {
		if (!(world instanceof WorldServer)) return;
		ChunkProfiler profiler = getChunkProfiler();

		BlockPos blockPos = world.getSpawnPoint();
		ChunkPos chunkPos = new ChunkPos(blockPos);

		ChunkProviderServer chunkProvider = ((WorldServer) this.world).getChunkProvider();
		IChunkProvider chunkgen = chunkProvider;
		IChunkLoader chunkloader = chunkProvider.chunkLoader;
		SpiralOutwardIterator iter = new SpiralOutwardIterator();

		int chunksneeded = MINCHUNKS - profiler.getCount();
		while (chunksneeded > 0 && ChunkProfilerManager.getSize() < chunksneeded) {
			iter.step();
			int chunkX = chunkPos.chunkXPos + iter.x;
			int chunkZ = chunkPos.chunkZPos + iter.y;
			if (safeLoadChunk(chunkloader, world, chunkX, chunkZ) == null) {
				chunkgen.provideChunk(chunkX, chunkZ);
			}
			chunksneeded = MINCHUNKS - profiler.getCount();
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

	public Vec3d getFogColor(Entity entity, Biome biome, float time, float celestial_angle, float partialtick) {
		validate();
		if (fogColorProviders == null || fogColorProviders.size() == 0) { return null; }
		Color color = null;
		for (IDynamicColorProvider mod : fogColorProviders) {
			Color op = mod.getColor(entity, biome, time, celestial_angle, partialtick);
			if (op == null) continue;
			if (color == null) {
				color = op;
			} else {
				color = color.average(op);
			}
		}
		return new Vec3d(color.r, color.g, color.b);
	}

	public Vec3d getCloudColor(Entity entity, Biome biome, float time, float celestial_angle, float partialtick) {
		validate();
		if (cloudColorProviders == null || cloudColorProviders.size() == 0) { return null; }
		Color color = null;
		for (IDynamicColorProvider mod : cloudColorProviders) {
			Color op = mod.getColor(entity, biome, time, celestial_angle, partialtick);
			if (op == null) continue;
			if (color == null) {
				color = op;
			} else {
				color = color.average(op);
			}
		}
		return new Vec3d(color.r, color.g, color.b);
	}

	@Override
	public ColorGradient getSunriseSunsetColor() {
		Modifier sunset = globalMods.get("sunset");
		if (sunset == null) return null;
		return sunset.asGradient();
	}

	public Vec3d getSkyColor(Entity entity, Biome biome, float time, float celestial_angle, float partialtick) {
		validate();
		if (skyColorProviders == null || skyColorProviders.size() == 0) { return null; }
		Color color = null;
		for (IDynamicColorProvider mod : skyColorProviders) {
			Color op = mod.getColor(entity, biome, time, celestial_angle, partialtick);
			if (op == null) continue;
			if (color == null) {
				color = op;
			} else {
				color = color.average(op);
			}
		}
		return new Vec3d(color.r, color.g, color.b);
	}

	public Color getStaticColor(String string, Biome biome, BlockPos pos) {
		validate();
		List<IStaticColorProvider> list = staticColorLists.get(string);
		if (list == null || list.size() == 0) { return null; }
		Color color = null;
		for (IStaticColorProvider mod : list) {
			Color op = mod.getStaticColor(this.world, biome, pos);
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
			instabilityController = new InstabilityController((WorldProviderMyst) world.provider, this, world);
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
		ChunkProfiler chunkprofiler = (ChunkProfiler) this.world.getPerWorldStorage().getOrLoadData(ChunkProfiler.class, ChunkProfiler.ID);
		if (chunkprofiler == null) {
			chunkprofiler = new ChunkProfiler(ChunkProfiler.ID);
			this.world.getPerWorldStorage().setData(ChunkProfiler.ID, chunkprofiler);
		}
		return chunkprofiler;
	}

	public void modifyBiomeAt(Biome biome, int x, int z) {
		validate();
		//TODO: Biome Modifications
//		if (biomemodifiers != null && biomemodifiers.size() > 0) {
//			for (IBiomeAlteration mod : biomemodifiers) {
//				mod.modifyBiomesAt(aBiome, x, z, xSize, zSize, usecache);
//			}
//		}
	}

	public void modifyBiomesAt(Biome aBiome[], int x, int z, int xSize, int zSize, boolean usecache) {
		validate();
		//TODO: Biome Modifications
//		if (biomemodifiers != null && biomemodifiers.size() > 0) {
//			for (IBiomeAlteration mod : biomemodifiers) {
//				mod.modifyBiomesAt(aBiome, x, z, xSize, zSize, usecache);
//			}
//		}
	}

	public void modifyGenerationBiomesAt(Biome aBiome[], int x, int z, int xSize, int zSize) {
		validate();
		//TODO: Biome Modifications
//		if (biomemodifiers != null && biomemodifiers.size() > 0) {
//			for (IBiomeAlteration mod : biomemodifiers) {
//				mod.modifyGenerationBiomesAt(aBiome, x, z, xSize, zSize, usecache);
//			}
//		}
	}

	public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer) {
		validate();
		getTerrainGenerator().generateTerrain(chunkX, chunkZ, primer);
	}

	public void modifyTerrain(int chunkX, int chunkZ, ChunkPrimer primer) {
		validate();
		if (terrainalterations != null && terrainalterations.size() > 0) {
			for (ITerrainAlteration mod : terrainalterations) {
				mod.alterTerrain(world, chunkX, chunkZ, primer);
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

	public List<SpawnListEntry> affectCreatureList(EnumCreatureType enumcreaturetype, List<SpawnListEntry> list, BlockPos pos) {
		validate();
		if (list == null) {
			list = new ArrayList<>();
		}
		return list;
	}

	public BlockPos locateTerrainFeature(World world, String s, BlockPos pos, boolean genChunks) {
		validate();
		if (featureLocators == null || featureLocators.size() == 0) {
			return null;
		}
		BlockPos found = null;
		for (ITerrainFeatureLocator mod : featureLocators) {
			found = mod.locate(world, s, pos, genChunks);
			if (found != null) {
				return found;
			}
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
		for (ICelestial sun : suns) {
			temp = sun.getAltitudeAngle(time, f1);
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
		if ((1 - highest) < lowest) {
			return highest;
		}
		return lowest;
	}

	public long getTimeToSunrise(long time) {
		Long result = null;
		Long temp;
		for (ICelestial sun : suns) {
			temp = sun.getTimeToDawn(time);
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
		return temp; //TODO: This function could be expanded upon
	}

	@SideOnly(Side.CLIENT)
	public void renderCelestials(TextureManager eng, World worldObj, float partial) {
		for (ICelestial celestial : celestials) {
			celestial.render(eng, worldObj, partial);
		}
	}

	// Symbol data functions
	@Override
	public long getSeed() {
		return agedata.getSeed();
	}

	@Override
	public BiomeProvider getWorldChunkManager() {
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
	public void registerInterface(ICelestial reg) {
		celestials.add(reg);
		if (reg.providesLight()) suns.add(reg);
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
	public void registerInterface(IDynamicColorProvider reg, String type) {
		List<IDynamicColorProvider> list = dynamicColorLists.get(type);
		if (list == null) throw new RuntimeException("Invalid Dynamic Color Provider Type");
		list.add(reg);
	}

	@Override
	public void registerInterface(IStaticColorProvider reg, String type) {
		List<IStaticColorProvider> list = staticColorLists.get(type);
		if (list == null) throw new RuntimeException("Invalid Static Color Provider Type");
		list.add(reg);
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
		if (val == null) throw new RuntimeException("Something tried to register a null modifier!");
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
