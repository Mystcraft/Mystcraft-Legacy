package com.xcompwiz.mystcraft;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;

import com.google.common.collect.ImmutableList;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.command.CommandCreateAgebook;
import com.xcompwiz.mystcraft.command.CommandCreateDim;
import com.xcompwiz.mystcraft.command.CommandDebug;
import com.xcompwiz.mystcraft.command.CommandMystPermissions;
import com.xcompwiz.mystcraft.command.CommandRegenerateChunk;
import com.xcompwiz.mystcraft.command.CommandReprofile;
import com.xcompwiz.mystcraft.command.CommandSpawnMeteor;
import com.xcompwiz.mystcraft.command.CommandTPX;
import com.xcompwiz.mystcraft.command.CommandTime;
import com.xcompwiz.mystcraft.command.CommandToggleDownfall;
import com.xcompwiz.mystcraft.command.CommandToggleWorldInstability;
import com.xcompwiz.mystcraft.config.MystConfig;
import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.core.MystcraftEventHandler;
import com.xcompwiz.mystcraft.core.TaskQueueManager;
import com.xcompwiz.mystcraft.data.GrammarRules;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.data.ModFluids;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.data.ModLinkEffects;
import com.xcompwiz.mystcraft.data.ModRecipes;
import com.xcompwiz.mystcraft.data.ModSymbols;
import com.xcompwiz.mystcraft.data.ModSymbolsFluids;
import com.xcompwiz.mystcraft.data.ModWords;
import com.xcompwiz.mystcraft.data.SymbolRules;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.imc.IMCHandler;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.instability.InstabilityManager;
import com.xcompwiz.mystcraft.instability.bonus.EventManager;
import com.xcompwiz.mystcraft.linking.LinkListenerBasic;
import com.xcompwiz.mystcraft.linking.LinkListenerEffects;
import com.xcompwiz.mystcraft.linking.LinkListenerForgeServer;
import com.xcompwiz.mystcraft.linking.LinkListenerPermissions;
import com.xcompwiz.mystcraft.network.MystcraftConnectionHandler;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketActivateItem;
import com.xcompwiz.mystcraft.network.packet.MPacketAgeData;
import com.xcompwiz.mystcraft.network.packet.MPacketConfigs;
import com.xcompwiz.mystcraft.network.packet.MPacketDimensions;
import com.xcompwiz.mystcraft.network.packet.MPacketExplosion;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.network.packet.MPacketMessage;
import com.xcompwiz.mystcraft.network.packet.MPacketOpenWindow;
import com.xcompwiz.mystcraft.network.packet.MPacketParticles;
import com.xcompwiz.mystcraft.network.packet.MPacketProfilingState;
import com.xcompwiz.mystcraft.network.packet.MPacketSpawnLightningBolt;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;
import com.xcompwiz.mystcraft.treasure.TreasureGenWrapper;
import com.xcompwiz.mystcraft.villager.MerchantRecipeProviderItem;
import com.xcompwiz.mystcraft.villager.VillageCreationHandlerArchivistHouse;
import com.xcompwiz.mystcraft.villager.VillagerArchivist;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;
import com.xcompwiz.mystcraft.world.agedata.AgeData;
import com.xcompwiz.mystcraft.world.gen.structure.ComponentScatteredFeatureSmallLibrary;
import com.xcompwiz.mystcraft.world.gen.structure.ComponentVillageArchivistHouse;
import com.xcompwiz.mystcraft.world.gen.structure.MapGenScatteredFeatureMyst;
import com.xcompwiz.mystcraft.world.gen.structure.StructureScatteredFeatureStartMyst;
import com.xcompwiz.mystcraft.world.profiling.ChunkProfilerManager;
import com.xcompwiz.mystcraft.world.profiling.InstabilityDataCalculator;
import com.xcompwiz.mystcraft.world.profiling.MystWorldGenerator;
import com.xcompwiz.mystcraft.world.storage.FileUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

@Mod(modid = MystObjects.MystcraftModId, version = "@VERSION@", name = "Mystcraft", useMetadata = true, dependencies = "required-after:Forge@[10.12.1.1083,)")
public class Mystcraft {

	@Instance(MystObjects.MystcraftModId)
	public static Mystcraft				instance;

	@SidedProxy(clientSide = "com.xcompwiz.mystcraft.client.MystcraftClientProxy", serverSide = "com.xcompwiz.mystcraft.core.MystcraftCommonProxy")
	public static MystcraftCommonProxy	sidedProxy;

	public static int					difficulty			= 2;

	public static boolean				instabilityEnabled	= true;
	public static boolean				renderlabels		= false;
	public static boolean				fastRainbows		= true;
	private static boolean				spawnmeteorEnabled	= false;
	public static boolean				respawnInAges		= true;
	public static boolean				villageDeskGen		= true;

	public static boolean				serverLabels;

	public static int					providerId;
	public static Collection<Integer>	registeredDims;
	public static LinkedList<Integer>	deadDims;

	public static int					archivistId;
	private VillagerArchivist			archivist;

	private ChunkProfilerManager		profilingThread;

	public static int					inkcost				= 50;
	public static Set<String>			validInks;

	public static MapStorage			clientStorage		= null;

	/** Forces the Dimension UUID check on login */
	public static boolean				requireUUID			= false;

	public static int					homeDimension		= 0;

	@EventHandler
	public void load(FMLPreInitializationEvent event) {
		// Init API
		InternalAPI.initAPI();

		// Init packet handling
		MystcraftPacketHandler.registerPacketHandler(new MPacketDimensions(), (byte) 10); // 10
		MystcraftPacketHandler.registerPacketHandler(new MPacketConfigs(), (byte) 20); // 25
		MystcraftPacketHandler.registerPacketHandler(new MPacketProfilingState(), (byte) 21); // 25
		MystcraftPacketHandler.registerPacketHandler(new MPacketParticles(), (byte) 25); // 20
		MystcraftPacketHandler.registerPacketHandler(new MPacketMessage(), (byte) 132); // 132
		MystcraftPacketHandler.registerPacketHandler(new MPacketGuiMessage(), (byte) 140); // 140
		MystcraftPacketHandler.registerPacketHandler(new MPacketOpenWindow(), (byte) 134); // 134
		MystcraftPacketHandler.registerPacketHandler(new MPacketActivateItem(), (byte) 137); // 137
		MystcraftPacketHandler.registerPacketHandler(new MPacketAgeData(), (byte) 135); // 135
		MystcraftPacketHandler.registerPacketHandler(new MPacketExplosion(), (byte) 100); // 100
		MystcraftPacketHandler.registerPacketHandler(new MPacketSpawnLightningBolt(), (byte) 101); // 101

		FMLCommonHandler.instance().bus().register(new MystcraftConnectionHandler());
		MystcraftPacketHandler.bus = NetworkRegistry.INSTANCE.newEventDrivenChannel(MystcraftPacketHandler.CHANNEL);
		MystcraftPacketHandler.bus.register(new MystcraftPacketHandler());

		// Register Event Handler
		MinecraftForge.EVENT_BUS.register(new MystcraftEventHandler());
		EventManager eventmanager = new EventManager();
		MinecraftForge.EVENT_BUS.register(eventmanager);
		FMLCommonHandler.instance().bus().register(eventmanager);
		EventManager.set(eventmanager);

		// Load configs
		File configroot = event.getSuggestedConfigurationFile().getParentFile();
		File configfile = new File(configroot, "mystcraft/core.cfg");

		File oldconfigfile = new File(configroot, "Mystcraft.txt");
		if (oldconfigfile.exists()) {
			configfile.getParentFile().mkdirs();
			if (!configfile.exists()) oldconfigfile.renameTo(configfile);
		}

		MystConfig config = new MystConfig(configfile);
		MystConfig balanceconfig = new MystConfig(new File(configroot, "mystcraft/balance.cfg"));
		SymbolManager.setConfig(new MystConfig(new File(configroot, "mystcraft/symbols.cfg")));
		InstabilityManager.setConfig(new MystConfig(new File(configroot, "mystcraft/instabilities.cfg")));
		ModSymbolsFluids.setConfig(balanceconfig);
		InstabilityDataCalculator.setBalanceConfig(balanceconfig);

		spawnmeteorEnabled = config.get(MystConfig.CATEGORY_GENERAL, "commands.spawnmeteor.enabled", spawnmeteorEnabled).getBoolean(spawnmeteorEnabled);

		difficulty = balanceconfig.getOptional(MystConfig.CATEGORY_INSTABILITY, "global.difficulty", difficulty);
		instabilityEnabled = balanceconfig.get(MystConfig.CATEGORY_INSTABILITY, "global.enabled", instabilityEnabled).getBoolean(instabilityEnabled);

		renderlabels = config.get(MystConfig.CATEGORY_RENDER, "renderlabels", renderlabels, "If set to false on the server config, this will override client settings.").getBoolean(renderlabels);
		fastRainbows = config.get(MystConfig.CATEGORY_RENDER, "fast_rainbows", fastRainbows).getBoolean(fastRainbows);

		respawnInAges = config.get(MystConfig.CATEGORY_GENERAL, "respawning.respawnInAges", respawnInAges).getBoolean(respawnInAges);
		villageDeskGen = config.get(MystConfig.CATEGORY_GENERAL, "generation.villageDeskGen", villageDeskGen).getBoolean(villageDeskGen);

		requireUUID = config.get(MystConfig.CATEGORY_GENERAL, "teleportation.requireUUIDTest", requireUUID, "If set to true, the dimension matching test will be strict. This will force new players to the \"home\" dimension.").getBoolean(requireUUID);
		homeDimension = config.get(MystConfig.CATEGORY_GENERAL, "teleportation.homedim", homeDimension).getInt();

		archivistId = config.get(MystConfig.CATEGORY_GENERAL, "ids.villager.archivist", 1210950779).getInt();
		providerId = config.get(MystConfig.CATEGORY_GENERAL, "ids.dim_provider", 1210950779).getInt();

		serverLabels = renderlabels;

		ModFluids.loadConfigs(config);
		ModItems.loadConfigs(config);
		ModBlocks.loadConfigs(config);
		ModRecipes.loadConfigs(config);
		ModLinkEffects.setConfigs(config);
		InstabilityDataCalculator.loadConfigs(config);

		if (config.hasChanged()) config.save();

		MapGenStructureIO.registerStructure(StructureScatteredFeatureStartMyst.class, MapGenScatteredFeatureMyst.stringId);
		MapGenStructureIO.func_143031_a(ComponentScatteredFeatureSmallLibrary.class, "TeMystSL");
		MapGenStructureIO.func_143031_a(ComponentVillageArchivistHouse.class, "ViMystAH");

		// Pre-init symbol system
		SymbolRemappings.initialize();
		GrammarRules.initialize();

		// Bind dim provider to id
		DimensionManager.registerProviderType(Mystcraft.providerId, WorldProviderMyst.class, false);
		GameRegistry.registerWorldGenerator(new MystWorldGenerator(), Integer.MAX_VALUE);

		sidedProxy.preinit();

		// Link Listeners
		MinecraftForge.EVENT_BUS.register(new LinkListenerBasic());
		MinecraftForge.EVENT_BUS.register(new LinkListenerPermissions());
		MinecraftForge.EVENT_BUS.register(new LinkListenerEffects());
		MinecraftForge.EVENT_BUS.register(new LinkListenerForgeServer());

		// Init Items/Blocks
		ModFluids.init();
		ModItems.init();
		ModBlocks.init();
		InkEffects.init();

		FluidContainerRegistry.registerFluidContainer(ModFluids.black_ink, new ItemStack(ModItems.inkvial, 1, 0), new ItemStack(Items.glass_bottle));
		ModSymbolsFluids.init();

		// Init Achievements
		ModAchievements.init();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLInterModComms.sendMessage("LookingGlass", "API", "com.xcompwiz.mystcraft.integration.lookingglass.LookingGlassIntegration.register");
		// Init Recipes
		ModRecipes.addRecipes(CraftingManager.getInstance());

		// Init TileEntities
		TileEntity.addMapping(com.xcompwiz.mystcraft.tileentity.TileEntityLectern.class, "LinkbookLectern");
		TileEntity.addMapping(com.xcompwiz.mystcraft.tileentity.TileEntityBookstand.class, "LinkbookStand");
		TileEntity.addMapping(com.xcompwiz.mystcraft.tileentity.TileEntityStarFissure.class, "StarFissure");
		TileEntity.addMapping(com.xcompwiz.mystcraft.tileentity.TileEntityDesk.class, "WritingDesk");
		TileEntity.addMapping(com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle.class, "CrystalBlock");
		TileEntity.addMapping(com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier.class, "LinkModifier");
		TileEntity.addMapping(com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder.class, "myst.BookBinder");
		TileEntity.addMapping(com.xcompwiz.mystcraft.tileentity.TileEntityInkMixer.class, "myst.InkMixer");

		// Init Entities
		EntityRegistry.registerModEntity(com.xcompwiz.mystcraft.entity.EntityLinkbook.class, "myst.book", 219, this, 64, 10, true);
		EntityRegistry.registerModEntity(com.xcompwiz.mystcraft.entity.EntityFallingBlock.class, "myst.block", 218, this, 16, 60, false);
		EntityRegistry.registerModEntity(com.xcompwiz.mystcraft.entity.EntityMeteor.class, "myst.meteor", 217, this, 192, 30, false);

		// Init Symbol System
		ModSymbols.initialize();
		ModWords.initialize();
		SymbolRules.initialize();
		//register instability data 
		InstabilityData.initialize();

		// Init Archivist
		if (archivistEnabled()) {
			archivist = new VillagerArchivist();
			VillagerRegistry.instance().registerVillagerId(archivistId);
			VillagerRegistry.instance().registerVillageTradeHandler(archivistId, archivist);
		}
		VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandlerArchivistHouse());

		// Client-Side: Register visuals
		sidedProxy.init();
	}

	public static boolean archivistEnabled() {
		return archivistId != 0;
	}

	@EventHandler
	public void handleIMC(IMCEvent event) {
		ImmutableList<IMCMessage> messages = event.getMessages();
		IMCHandler.process(messages);
	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event) {
		sidedProxy.postInit();

		ModSymbols.generateBiomeSymbols();
		ModSymbolsFluids.modsLoaded();
		SymbolManager.buildCardRanks();
		SymbolManager.registerRules();
		GrammarGenerator.buildGrammar();

		InstabilityDataCalculator.loadBalanceData();

		// Treasure object
		ChestGenHooks treasureinfo = ChestGenHooks.getInfo(MystObjects.MYST_TREASURE);
		treasureinfo.setMin(4);
		treasureinfo.setMax(8);
		treasureinfo.addItem(new WeightedRandomChestContent(Items.paper, 0, 1, 8, 50));
		treasureinfo.addItem(new WeightedRandomChestContent(Items.leather, 0, 1, 8, 50));
		treasureinfo.addItem(new WeightedRandomChestContent(ModItems.inkvial, 0, 1, 2, 50));
		treasureinfo.addItem(new WeightedRandomChestContent(ModItems.booster, 0, 1, 4, 1000));
		//treasureinfo.addItem(new TreasureGenBooster(7, 4, 4, 1, 1000));
		// 11 commons, 3 uncommon, 1 rare, and a basic land
		//if (archivist != null) archivist.registerRecipe(new MerchantRecipeProviderBooster(7, 4, 4, 1));
		if (archivist != null) archivist.registerRecipe(new MerchantRecipeProviderItem(new ItemStack(Items.emerald, 10), null, new ItemStack(ModItems.booster, 1)));

		TreasureGenWrapper mystTreasureSub = new TreasureGenWrapper(MystObjects.MYST_TREASURE, 10);
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(mystTreasureSub);
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(mystTreasureSub);
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(mystTreasureSub);
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(mystTreasureSub);

		sidedProxy.createCreativeTabs();

		// Create pages for all symbols for symbol tab, treasure gen, and
		// merchant handler
		ArrayList<IAgeSymbol> symbols = SymbolManager.getAgeSymbols();
		for (IAgeSymbol symbol : symbols) {
			// Create treasure gen entry
			int maxStack = SymbolManager.getSymbolTreasureMaxStack(symbol);
			int chance = SymbolManager.getSymbolItemWeight(symbol.identifier());
			if (chance != 0 && maxStack != 0) treasureinfo.addItem(new WeightedRandomChestContent(Page.createSymbolPage(symbol.identifier()), 1, maxStack, chance));
		}
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		MinecraftServer mcserver = event.getServer();
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandTPX());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandCreateDim());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandCreateAgebook());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandToggleWorldInstability());
		if (spawnmeteorEnabled) ((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandSpawnMeteor());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandToggleDownfall());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandTime());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandMystPermissions());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandRegenerateChunk());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandReprofile());
		((ServerCommandManager) mcserver.getCommandManager()).registerCommand(new CommandDebug());

		profilingThread = new ChunkProfilerManager();
		profilingThread.start();

		sidedProxy.startBaselineProfiling(mcserver);
		registerDimensions(mcserver.worldServerForDimension(0).getSaveHandler());
		LinkListenerPermissions.loadState();
	}

	@EventHandler
	public void serverStop(FMLServerStoppedEvent event) {
		TaskQueueManager.onServerStop();
		sidedProxy.stopBaselineProfiling();
		if (profilingThread != null) {
			profilingThread.halt();
			profilingThread = null;
		}
		unregisterDimensions();
		Mystcraft.clientStorage = null;
	}

	public static MapStorage getStorage(boolean isServer) {
		World overworld = null;
		if (isServer) {
			overworld = DimensionManager.getWorld(0);
		}
		if (overworld == null) {
			if (clientStorage == null) throw new RuntimeException("Client-Side Storage Missing (Attempted as " + (isServer ? "server" : "remote") + ")");
			return clientStorage;
		}
		return overworld.mapStorage;
	}

	public static long getLevelSeed() {
		MinecraftServer mcServer = MinecraftServer.getServer();
		if (mcServer == null) return 0;
		return mcServer.worldServerForDimension(0).getSeed();
	}

	public static void unregisterDimensions() {
		deadDims = null;
		if (registeredDims == null) return;
		for (Integer dimId : registeredDims) {
			DimensionManager.unregisterDimension(dimId);
		}
		registeredDims = null;
	}

	public static void registerDimensions(ISaveHandler savehandler) {
		registeredDims = FileUtils.getExistingAgeList(savehandler.getMapFileFromName("dummy").getParentFile());
		deadDims = new LinkedList<Integer>();
		MapStorage tempstorage = new MapStorage(savehandler);
		for (Integer dimId : registeredDims) {
			DimensionManager.registerDimension(dimId, providerId);
			AgeData data = AgeData.getAge(dimId, tempstorage);
			if (data.isDead()) deadDims.add(dimId);
		}
	}

	@EventHandler
	public void handleNameChanges(FMLMissingMappingsEvent event) {
		for (MissingMapping elem : event.get()) {
			if (elem.name.equals("Mystcraft:notebook")) elem.remap(ModItems.folder);
		}
	}
}
