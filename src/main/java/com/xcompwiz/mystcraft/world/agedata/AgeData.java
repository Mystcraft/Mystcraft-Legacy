package com.xcompwiz.mystcraft.world.agedata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.world.storage.StorageObject;
import com.xcompwiz.mystcraft.data.GrammarRules;
import com.xcompwiz.mystcraft.debug.DebugFlags;
import com.xcompwiz.mystcraft.grammar.GrammarTree;
import com.xcompwiz.mystcraft.nbt.NBTTagCompoundWrapper;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;

// XXX: (AgeData) can we move to this class being server-side, and then having sub-objects which are sent to the clients?
// Needed things on client-side:
// Visuals (symbol list)
// Symbol states (ex. weather)
public class AgeData extends WorldSavedData {
	public static class AgeDataData {
		public String				agename;
		public Set<String>			authors	= new HashSet<String>();
		public long					seed;
		public UUID					uuid;
		public short				instability;
		public boolean				instabilityEnabled;
		public BlockPos				spawn;
		public List<ItemStack>		pages	= new ArrayList<ItemStack>();
		public List<String>			symbols	= new ArrayList<String>();
		public boolean				visited;
		public boolean				dead;
		public NBTTagCompound		datacompound;
		public long					worldtime;
		public String				version;
		public Map<String, NBTBase>	cruft	= new HashMap<String, NBTBase>();
	}

	private String				agename;
	private Set<String>			authors;
	private long				seed;
	private UUID				uuid;
	private short				instability;
	private boolean				instabilityEnabled;
	private long				worldtime;
	private BlockPos			spawn;
	private List<ItemStack>		pages			= new ArrayList<ItemStack>();
	private List<String>		symbols			= new ArrayList<String>();
	private boolean				visited;
	private boolean				dead;
	private NBTTagCompound		datacompound;

	public Map<String, NBTBase>	cruft			= new HashMap<String, NBTBase>();

	private boolean				updated;
	private Boolean				sharedDirty		= Boolean.FALSE;
	private boolean				needsResend;
	private Boolean				sharedResend	= Boolean.FALSE;

	public AgeData(String s) {
		super(s);
		datacompound = new NBTTagCompound();
	}

	/**
	 * Whether this MapDataBase needs saving to disk.
	 */
	@Override
	public boolean isDirty() {
		if (sharedDirty) return true;
		return super.isDirty();
	}

	@Override
	public void setDirty(boolean par1) {
		super.setDirty(par1);
		if (!par1) {
			sharedDirty = false;
		}
	}

	public boolean needsResend() {
		if (sharedResend) return true;
		return needsResend;
	}

	public void resent() {
		needsResend = false;
		sharedResend = false;
	}

	public void markNeedsResend() {
		needsResend = true;
	}

	public boolean isUpdated() {
		return updated;
	}

	public boolean isVisited() {
		return visited;
	}

	public void markVisited() {
		if (!visited) this.markDirty();
		visited = true;
		updated = false;
	}

	public boolean isDead() {
		return dead;
	}

	/**
	 * This should only be called from DimensionUtils
	 */
	public void markDead() {
		this.dead = true;
	}

	public void onConstruct() {
		updated = false;
	}

	public void setAgeName(String name) {
		agename = name;
		markDirty();
	}

	public String getAgeName() {
		return agename;
	}

	public Set<String> getAuthors() {
		if (authors == null) return Collections.emptySet();
		return Collections.unmodifiableSet(authors);
	}

	public void addAuthor(String name) {
		if (authors == null) authors = new HashSet<String>();
		authors.add(name);
	}
	
	public void setSeed(long seed) {
		if (this.visited) return;
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}

	public UUID getUUID() {
		return uuid;
	}

	public BlockPos getSpawn() {
		return spawn;
	}

	public void setSpawn(BlockPos spawn) {
		this.spawn = spawn;
	}

	public boolean isInstabilityEnabled() {
		return instabilityEnabled;
	}

	public void setInstabilityEnabled(boolean instabilityEnabled) {
		this.instabilityEnabled = instabilityEnabled;
		this.markDirty();
	}

	// Called by AgeController to calc instability
	public int getBaseInstability() {
		// TODO: (Crafting) Calc instability from pages
		return instability;
	}

	/**
	 * This should be called to finalize the book. The AgeData will then store the pages as they were when the age was created. Any changes to the book itself
	 * will be handled separately. This allows for books to be created with all of the symbols of the age (via admin command), as well as opening doors to
	 * rewriting.
	 * @param pages
	 */
	// Called on first link
	public void setPages(List<ItemStack> pages) {
		this.pages.clear();
		this.pages.addAll(pages);
		this.markDirty();
	}

	// Primarily used when displaying written pages
	public List<ItemStack> getPages() {
		return Collections.unmodifiableList(pages);
	}

	// Used by AgeController to add symbols without adding pages
	public void addSymbol(String symbol, int instability) {
		symbols.add(symbol);
		this.instability += instability;
		this.markDirty();
	}

	// Called by AgeController when building the age logic
	public List<String> getSymbols(boolean isRemote) {
		if (!visited && !isRemote) { // On first link, build the symbol list
			symbols.clear();
			for (int i = 0; i < pages.size(); ++i) {
				String symbol = Page.getSymbol(pages.get(i));
				if (symbol != null) {
					symbols.add(symbol);
				}
			}
			GrammarTree tree = new GrammarTree(GrammarRules.ROOT);
			tree.parseTerminals(symbols, new Random(this.seed));
			if (DebugFlags.grammar) {
				System.out.println(" == Parsed Tree ==");
				tree.print();
			}
			symbols = tree.getExpanded(new Random(this.seed));
			if (DebugFlags.grammar) {
				System.out.println(" == Produced Tree ==");
				tree.print();
			}
		}
		return Collections.unmodifiableList(symbols);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		AgeDataData loadeddata = AgeDataLoaderManager.load(nbttagcompound);

		agename = loadeddata.agename;
		authors = loadeddata.authors;
		seed = loadeddata.seed;
		uuid = loadeddata.uuid;
		visited = loadeddata.visited;
		dead = loadeddata.dead;
		worldtime = loadeddata.worldtime;

		instability = loadeddata.instability;
		instabilityEnabled = loadeddata.instabilityEnabled;

		setSpawn(loadeddata.spawn);

		pages = loadeddata.pages;
		symbols = loadeddata.symbols;

		datacompound = loadeddata.datacompound;
		cruft = loadeddata.cruft;

		if (datacompound == null) {
			datacompound = new NBTTagCompound();
		}

		// Handle SymbolRemappings
		SymbolRemappings.remap(pages);
		SymbolRemappings.remap(symbols);

		updated = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString("Version", "4.3"); //XXX: Current agedata version stored in multiple locations

		nbttagcompound.setString("AgeName", agename);
		nbttagcompound.setLong("Seed", seed);
		nbttagcompound.setString("UUID", uuid.toString());
		nbttagcompound.setShort("BaseIns", instability);
		nbttagcompound.setBoolean("InstabilityEnabled", instabilityEnabled);
		nbttagcompound.setBoolean("Visited", visited);
		nbttagcompound.setBoolean("Dead", dead);
		nbttagcompound.setTag("DataCompound", datacompound);
		nbttagcompound.setLong("WorldTime", worldtime);

		if (spawn != null) {
			nbttagcompound.setInteger("SpawnX", spawn.getX());
			nbttagcompound.setInteger("SpawnY", spawn.getY());
			nbttagcompound.setInteger("SpawnZ", spawn.getZ());
		}

		NBTTagList list = new NBTTagList();
		for (ItemStack page : pages) {
			list.appendTag(page.getTagCompound());
		}
		nbttagcompound.setTag("Pages", list);

		nbttagcompound.setTag("Symbols", NBTUtils.writeStringCollection(new NBTTagList(), symbols));
		if (authors != null) nbttagcompound.setTag("Authors", NBTUtils.writeStringCollection(new NBTTagList(), authors));

		if (cruft != null && cruft.size() > 0) {
			NBTTagCompound cruftnbt = new NBTTagCompound();
			for (Entry<String, NBTBase> elem : cruft.entrySet()) {
				cruftnbt.setTag(elem.getKey(), elem.getValue());
			}
			nbttagcompound.setTag("Cruft", cruftnbt);
		}
		return nbttagcompound;
	}

	public static AgeData getMPAgeData(int uid) {
		MapStorage storage = Mystcraft.getStorage(false);
		String s = getStringID(uid);
		if (storage == null) throw new RuntimeException("Mystcraft could not retrieve the agedata file.  The storage object is null.");
		AgeData age = (AgeData) storage.getOrLoadData(AgeData.class, s);
		if (age == null) {
			age = new AgeData(s);
			storage.setData(s, age);
			age.markDirty();
		}
		return age;
	}

	public static AgeData getAge(int uid, boolean isRemote) {
		MapStorage storage = Mystcraft.getStorage(!isRemote);
		if (storage == null) throw new RuntimeException("Mystcraft could not load the agedata file.  The storage object is null. (Attempted as " + (isRemote ? "remote" : "server") + ")");
		return getAge(uid, storage);
	}
	public static AgeData getAge(int uid, MapStorage storage) {
		if (!DimensionManager.isDimensionRegistered(uid)) return null;
		if (DimensionManager.getProviderType(uid) != Mystcraft.dimensionType) return null;
		if (storage == null) throw new RuntimeException("Mystcraft could not load the agedata file.  The storage object is null.)");
		String s = getStringID(uid);
		AgeData age = (AgeData) storage.getOrLoadData(AgeData.class, s);
		if (age == null) {
			age = new AgeData(s);
			storage.setData(s, age);
			age.agename = (new StringBuilder()).append("Age ").append(uid).toString();
			age.seed = Mystcraft.getLevelSeed(storage) + new Random(uid).nextLong();
			age.uuid = UUID.randomUUID();
			age.worldtime = 0;
			age.setSpawn(null);
			age.instability = 0;
			age.instabilityEnabled = true;
			age.visited = false;
			age.dead = false;
			age.updated = false;
			age.markDirty();
		}
		return age;
	}

	public void recreate(int uid) {
		this.agename = (new StringBuilder()).append("Age ").append(uid).toString();
		this.seed = this.seed + new Random(uid).nextLong();
		this.uuid = UUID.randomUUID();
		this.worldtime = 0;
		this.setSpawn(null);
		this.instability = 0;
		this.instabilityEnabled = true;
		this.visited = false;
		this.dead = false;
		this.updated = false;
		this.pages.clear();
		this.symbols.clear();
		if (authors != null) this.authors.clear();
		this.datacompound = new NBTTagCompound();
		this.markDirty();
	}

	public static String getStringID(int uid) {
		return (new StringBuilder()).append("agedata_").append(uid).toString();
	}

	public StorageObject getStorageObject(String string) {
		if (!datacompound.hasKey(string)) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			datacompound.setTag(string, nbttagcompound);
		}
		return new NBTTagCompoundWrapper(datacompound.getCompoundTag(string), sharedDirty, sharedResend);
	}

	public void setWorldTime(long time) {
		this.worldtime = time;
		this.markDirty();
	}

	public long getWorldTime() {
		return worldtime;
	}

	public NBTBase popCruft(String string) {
		if (cruft == null) return null;
		return cruft.remove(string);
	}
}
