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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.DebugFlags;
import com.xcompwiz.mystcraft.data.GrammarRules;
import com.xcompwiz.mystcraft.grammar.GrammarTree;
import com.xcompwiz.mystcraft.nbt.NBTTagCompoundWrapper;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;
import com.xcompwiz.mystcraft.world.storage.IStorageObject;

// XXX: (AgeData) can we move to this class being server-side, and then having sub-objects which are sent to the clients?
// Needed things on client-side:
// Visuals (symbol list)
// Symbol states (ex. weather)
public class AgeData extends WorldSavedData {
	public static class AgeDataData {
		public String				agename;
		public Set<String>			authors	= new HashSet<String>();
		public long					seed;
		public short				instability;
		public boolean				instabilityEnabled;
		public ChunkCoordinates		spawn;
		public List<ItemStack>		pages	= new ArrayList<ItemStack>();
		public List<String>			symbols	= new ArrayList<String>();
		public boolean				visited;
		public NBTTagCompound		datacompound;
		public long					worldtime;
		public String				version;
		public Map<String, NBTBase>	cruft	= new HashMap<String, NBTBase>();
	}

	private String				agename;
	private Set<String>			authors;
	private long				seed;
	private short				instability;
	private boolean				instabilityEnabled;
	private long				worldtime;
	private ChunkCoordinates	spawn;
	private List<ItemStack>		pages			= new ArrayList<ItemStack>();
	private List<String>		symbols			= new ArrayList<String>();
	private boolean				visited;
	private NBTTagCompound		datacompound;

	public Map<String, NBTBase>	cruft			= new HashMap<String, NBTBase>();

	private boolean				updated;
	private Boolean				sharedDirty		= new Boolean(false);
	private boolean				needsResend;
	private Boolean				sharedResend	= new Boolean(false);

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
		if (par1 == false) {
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
		if (visited != true) this.markDirty();
		visited = true;
		updated = false;
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
		if (authors == null) return null;
		return Collections.unmodifiableSet(authors);
	}

	public void addAuthor(String name) {
		if (authors == null) authors = new HashSet<String>();
		authors.add(name);
	}

	public long getSeed() {
		return seed;
	}

	public ChunkCoordinates getSpawn() {
		return spawn;
	}

	public void setSpawn(ChunkCoordinates spawn) {
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

	// Primarily used when displaying written pages
	// TODO: (Sorting) Move this into the books themselves
	public List<ItemStack> getPages() {
		return Collections.unmodifiableList(pages);
	}

	// Called on book assembly
	// TODO: (Sorting) Move this into the books themselves
	public void addPages(List<ItemStack> pages) {
		this.pages.addAll(pages);
		this.markDirty();
	}

	/**
	 * Writes the symbol to the first blank page in the book Returns false on failure
	 * 
	 * @param symbol The identifier of the symbol to write
	 * @return true if successfully wrote the symbol to the book
	 */
	// TODO: (Sorting) Move this into the books themselves
	public boolean writeSymbol(String symbol) {
		if (this.visited) return false;
		for (ItemStack page : pages) {
			if (Page.isBlank(page)) {
				Page.setSymbol(page, symbol);
				if (visited == true) {
					// TODO: (Rewriting)
				}
				this.markDirty();
				return true;
			}
		}
		return false;
	}

	// Used by AgeController to add symbols without adding pages
	public void addSymbol(String symbol, int instability) {
		symbols.add(symbol);
		this.instability += instability;
		this.markDirty();
	}

	/**
	 * This should be called to finalize the book. The AgeData will then store the pages as they were when the age was created. Any changes to the book itself
	 * will be handled separately. This allows for books to be created with all of the symbols of the age (via admin command), as well as opening doors to
	 * rewriting.
	 * 
	 * @param pages
	 */
	//TODO: (PageStorage) When revising the storage of pages, push the pages list of the written book via a function to "finalize" the book
	public void setPages(List<ItemStack> pages) {
		this.pages.clear();
		this.pages.addAll(pages);
		this.markDirty();
	}

	// Called by AgeController when building the age logic
	public List<String> getSymbols(boolean isRemote) {
		if (visited == false && !isRemote) { // On first link, build the symbol list
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
		visited = loadeddata.visited;
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
		for (int i = 0; i < pages.size();) {
			ItemStack page = pages.remove(i);
			List<ItemStack> mapping = SymbolRemappings.remap(page);
			pages.addAll(i, mapping);
			if (mapping.size() > 0 && page.equals(mapping.get(0))) {
				++i;
			}
		}
		for (int i = 0; i < symbols.size();) {
			String symbol = symbols.remove(i);
			List<String> mapping = SymbolRemappings.remap(symbol);
			symbols.addAll(i, mapping);
			if (mapping.size() > 0 && symbol.equals(mapping.get(0))) {
				++i;
			}
		}

		updated = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString("Version", "4.2");
		if (agename == null || agename.isEmpty()) {
			agename = "Unnamed Age";
		}
		nbttagcompound.setString("AgeName", agename);
		nbttagcompound.setLong("Seed", seed);
		nbttagcompound.setShort("BaseIns", instability);
		nbttagcompound.setBoolean("InstabilityEnabled", instabilityEnabled);
		nbttagcompound.setBoolean("Visited", visited);
		nbttagcompound.setTag("DataCompound", datacompound);
		nbttagcompound.setLong("WorldTime", worldtime);

		if (getSpawn() != null) {
			nbttagcompound.setInteger("SpawnX", spawn.posX);
			nbttagcompound.setInteger("SpawnY", spawn.posY);
			nbttagcompound.setInteger("SpawnZ", spawn.posZ);
		}

		NBTTagList list = new NBTTagList();
		for (ItemStack page : pages) {
			list.appendTag(page.stackTagCompound);
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
	}

	public static AgeData getMPAgeData(int uid) {
		MapStorage storage = Mystcraft.getStorage(false);
		String s = getStringID(uid);
		if (storage == null) throw new RuntimeException("Mystcraft could not retrieve the agedata file.  The storage object is null.");
		AgeData age = (AgeData) storage.loadData(AgeData.class, s);
		if (age == null) {
			age = new AgeData(s);
			storage.setData(s, age);
			age.markDirty();
		}
		return age;
	}

	public static AgeData getAge(int uid, boolean isRemote) {
		if (DimensionManager.getProviderType(uid) != Mystcraft.providerId) return null;
		String s = getStringID(uid);
		MapStorage storage = Mystcraft.getStorage(!isRemote);
		if (storage == null) throw new RuntimeException("Mystcraft could not load the agedata file.  The storage object is null. (Attempted as " + (isRemote ? "remote" : "server") + ")");
		AgeData age = (AgeData) storage.loadData(AgeData.class, s);
		if (age == null) {
			age = new AgeData(s);
			storage.setData(s, age);
			age.agename = (new StringBuilder()).append("Age ").append(uid).toString();
			age.seed = Mystcraft.getLevelSeed(!isRemote) + new Random(uid).nextLong();
			age.setSpawn(null);
			age.instability = 0;
			age.instabilityEnabled = true;
			age.visited = false;
			age.updated = false;
			age.markDirty();
		}
		return age;
	}

	public static String getStringID(int uid) {
		return (new StringBuilder()).append("agedata_").append(uid).toString();
	}

	public IStorageObject getStorageObject(String string) {
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
