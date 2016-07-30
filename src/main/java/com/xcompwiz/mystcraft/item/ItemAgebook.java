package com.xcompwiz.mystcraft.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.item.IItemWritable;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAgebook extends ItemLinking implements IItemWritable, IItemPageProvider, IItemOnLoadable {

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:agebook");
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return itemstack.isItemEnchanted() ? EnumRarity.rare : EnumRarity.epic;
	}

	@Override
	protected void initialize(World world, ItemStack itemstack, Entity entity) {
		if (itemstack.stackTagCompound == null) {
			itemstack.stackTagCompound = new NBTTagCompound();
			//An empty book is defaulted to having a single link panel in it. This code is fallback only; it doesn't happen in normal gameplay (only creative/NEI)
			LinkOptions.setFlag(itemstack.stackTagCompound, LinkPropertyAPI.FLAG_GENERATE_PLATFORM, true);
			this.addPages(itemstack, getDefaultPages(itemstack));
		}
	}

	@Override
	public void validate(World worldObj, ItemStack itemstack, Entity entity) {
		super.validate(worldObj, itemstack, entity);
		if (itemstack.stackTagCompound != null) {
			if (!itemstack.stackTagCompound.hasKey("Pages")) addPages(itemstack, getDefaultPages(itemstack));
		}
	}

	private Collection<ItemStack> getDefaultPages(ItemStack itemstack) {
		Collection<ItemStack> collection = Collections.singleton(Page.createLinkPage());
		Integer dimid = LinkOptions.getDimensionUID(itemstack.stackTagCompound);
		if (dimid == null) return collection;
		AgeData data = AgeData.getAge(dimid, false); //TODO: We're always assuming we are the server when grabbing age data on Agebook load
		if (data == null) return collection;
		collection = data.getPages();
		return collection;
	}

	public static void initializeCompound(ItemStack itemstack, int dimId, AgeData agedata) {
		itemstack.setTagCompound(new NBTTagCompound());
		LinkOptions.setDimensionUID(itemstack.stackTagCompound, dimId);
		LinkOptions.setUUID(itemstack.stackTagCompound, agedata.getUUID());
		LinkOptions.setDisplayName(itemstack.stackTagCompound, agedata.getAgeName());
		LinkOptions.setFlag(itemstack.stackTagCompound, LinkPropertyAPI.FLAG_GENERATE_PLATFORM, true);
		((ItemAgebook)itemstack.getItem()).addPages(itemstack, agedata.getPages());
	}

	public static void create(ItemStack agebook, EntityPlayer player, List<ItemStack> pages, String pendingtitle) {
		agebook.stackTagCompound = new NBTTagCompound();
		((ItemAgebook)agebook.getItem()).addPages(agebook, pages);
		((ItemAgebook)agebook.getItem()).addAuthor(agebook, player);
		((ItemAgebook)agebook.getItem()).setDisplayName(player, agebook, pendingtitle);
		if (pages.isEmpty()) return;
		ItemStack linkpanel = pages.get(0);
		if (Page.isLinkPanel(linkpanel)) Page.applyLinkPanel(linkpanel, agebook);
	}

	public static boolean isNewAgebook(ItemStack itemstack) {
		if (!(itemstack.getItem() instanceof ItemAgebook)) return false;
		if (itemstack.stackTagCompound == null) return false;
		Integer dimid = LinkOptions.getDimensionUID(itemstack.stackTagCompound);
		if (dimid != null) return false;
		List<ItemStack> pages = ((ItemAgebook)itemstack.getItem()).getPageList(null, itemstack);
		if (pages.isEmpty()) return false;
		if (!Page.isLinkPanel(pages.get(0))) return false;
		return true;
	}

	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer player) {
		player.addStat(ModAchievements.agebook, 1);
	}

	@Override
	public void activate(ItemStack itemstack, World worldObj, Entity entity) {
		if (worldObj.isRemote) return;
		this.checkFirstLink(itemstack, worldObj, entity);
		super.activate(itemstack, worldObj, entity);
	}

	private void checkFirstLink(ItemStack itemstack, World worldObj, Entity entity) {
		if (itemstack.stackTagCompound == null) return;
		Integer dimid = LinkOptions.getDimensionUID(itemstack.stackTagCompound);
		if (dimid != null) return;
		dimid = DimensionUtils.createAge();
		AgeData agedata = AgeData.getAge(dimid, false);
		LinkOptions.setDimensionUID(itemstack.stackTagCompound, dimid);
		LinkOptions.setUUID(itemstack.stackTagCompound, agedata.getUUID());
		agedata.setAgeName(LinkOptions.getDisplayName(itemstack.stackTagCompound));
		String seed = LinkOptions.getProperty(itemstack.stackTagCompound, "Seed");
		if (seed != null) agedata.setSeed(Long.parseLong(seed));
		updatePageList(itemstack);
		agedata.setPages(getPageList(null, itemstack));
	}

	@Override
	public String getDisplayName(EntityPlayer player, ItemStack itemstack) {
		return LinkOptions.getDisplayName(itemstack.stackTagCompound);
	}

	@Override
	public void setDisplayName(EntityPlayer player, ItemStack itemstack, String name) {
		LinkOptions.setDisplayName(itemstack.stackTagCompound, name);
		AgeData data = getAgeData(itemstack, player.worldObj.isRemote);
		if (data != null) data.setAgeName(name);
	}

	public void setSeed(EntityPlayer player, ItemStack itemstack, long seed) {
		LinkOptions.setProperty(itemstack.stackTagCompound, "Seed", Long.toString(seed));
		AgeData data = getAgeData(itemstack, player.worldObj.isRemote);
		if (data != null) data.setSeed(seed);
	}
	
	@Override
	public boolean writeSymbol(EntityPlayer player, ItemStack itemstack, String symbol) {
		if (isVisited(itemstack, player.worldObj.isRemote)) return false;
		if (itemstack.stackTagCompound == null) return false;
		NBTTagCompound nbttagcompound = itemstack.stackTagCompound;
		Collection<ItemStack> list = NBTUtils.readItemStackCollection(nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND), new ArrayList<ItemStack>());
		for (ItemStack page : list) {
			if (Page.isBlank(page)) {
				Page.setSymbol(page, symbol);
				nbttagcompound.setTag("Pages", NBTUtils.writeItemStackCollection(new NBTTagList(), list));
				this.addAuthor(itemstack, player);
				return true;
			}
		}
		return false;
	}

	private void addPages(ItemStack itemstack, Collection<ItemStack> pages) {
		if (itemstack.stackTagCompound == null) return;
		NBTTagCompound nbttagcompound = itemstack.stackTagCompound;
		Collection<ItemStack> list = NBTUtils.readItemStackCollection(nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND), new ArrayList<ItemStack>());
		list.addAll(pages);
		nbttagcompound.setTag("Pages", NBTUtils.writeItemStackCollection(new NBTTagList(), list));
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, ItemStack itemstack) {
		if (itemstack.stackTagCompound == null) return Collections.EMPTY_LIST;
		NBTTagCompound nbttagcompound = itemstack.stackTagCompound;
		return NBTUtils.readItemStackCollection(nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND), new ArrayList<ItemStack>());
	}

	private void setPageList(ItemStack itemstack, List<ItemStack> pagelist) {
		if (itemstack.stackTagCompound == null) return;
		NBTTagCompound nbttagcompound = itemstack.stackTagCompound;
		nbttagcompound.setTag("Pages", NBTUtils.writeItemStackCollection(new NBTTagList(), pagelist));
	}

	private void updatePageList(ItemStack itemstack) {
		setPageList(itemstack, SymbolRemappings.remap(getPageList(null, itemstack)));
	}

	private void addAuthor(ItemStack itemstack, EntityPlayer player) {
		if (itemstack.stackTagCompound == null) return;
		NBTTagCompound nbttagcompound = itemstack.stackTagCompound;
		Collection<String> list = NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), new ArrayList<String>());
		list.add(player.getDisplayName());
		nbttagcompound.setTag("Authors", NBTUtils.writeStringCollection(new NBTTagList(), list));
	}

	@Override
	public Collection<String> getAuthors(ItemStack itemstack) {
		if (itemstack.stackTagCompound == null) return Collections.EMPTY_LIST;
		NBTTagCompound nbttagcompound = itemstack.stackTagCompound;
		return NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), new ArrayList<String>());
	}

	private AgeData getAgeData(ItemStack itemstack, boolean isRemote) {
		if (itemstack.stackTagCompound == null) return null;
		Integer uid = LinkOptions.getDimensionUID(itemstack.stackTagCompound);
		if (uid == null) return null;
		return AgeData.getAge(uid, isRemote);
	}

	private boolean isVisited(ItemStack itemstack, boolean isRemote) {
		AgeData agedata = getAgeData(itemstack, isRemote);
		if (agedata == null) return false;
		return agedata.isVisited();
	}

	@Override
	public ItemStack onLoad(ItemStack itemstack) {
		updatePageList(itemstack);
		this.initialize(null, itemstack, null);
		this.validate(null, itemstack, null);
		if (getPageList(null, itemstack).isEmpty()) addPages(itemstack, getDefaultPages(itemstack)); //TODO: Make this an "onItemEnteredPlayerInventory" call as well
		return itemstack;
	}
}
