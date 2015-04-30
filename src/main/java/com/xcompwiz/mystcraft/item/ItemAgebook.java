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
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAgebook extends ItemLinking implements IItemWritable, IItemPageProvider {

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
	protected void initialize(World worldObj, ItemStack itemstack, Entity entity) {
		if (worldObj.isRemote) return;
		if (itemstack.stackTagCompound == null) {
			itemstack.stackTagCompound = new NBTTagCompound();
			//An empty book is defaulted to having a single link panel in it. This code is fallback only; it doesn't happen in normal gameplay (only creative/NEI)
			LinkOptions.setFlag(itemstack.stackTagCompound, LinkPropertyAPI.FLAG_GENERATE_PLATFORM, true);
			((ItemAgebook)itemstack.getItem()).addPages(itemstack, Collections.singleton(Page.createLinkPage()));
		}
	}

	public static void initializeCompound(ItemStack itemstack, int dimId, AgeData bookdata) {
		itemstack.setTagCompound(new NBTTagCompound());
		LinkOptions.setDimensionUID(itemstack.stackTagCompound, dimId);
		LinkOptions.setDisplayName(itemstack.stackTagCompound, bookdata.getAgeName());
		LinkOptions.setFlag(itemstack.stackTagCompound, LinkPropertyAPI.FLAG_GENERATE_PLATFORM, true);
		((ItemAgebook)itemstack.getItem()).addPages(itemstack, bookdata.getPages());
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

	public static boolean isNewAgebook(ItemStack agebook) {
		if (agebook.stackTagCompound == null) return false;
		Integer dimid = LinkOptions.getDimensionUID(agebook.stackTagCompound);
		if (dimid != null) return false;
		List<ItemStack> pages = ((ItemAgebook)agebook.getItem()).getPageList(null, agebook);
		if (pages.isEmpty()) return false;
		if (!Page.isLinkPanel(pages.get(0))) return false;
		return true;
	}

	@Override
	@Deprecated
	//TODO: Replace with an onLoad handler? Can we get such an event through Forge?
	public void onUpdate(ItemStack itemstack, World worldObj, Entity entity, int i, boolean flag) {
		super.onUpdate(itemstack, worldObj, entity, i, flag);
		if (worldObj.isRemote) return;
		if (itemstack.stackTagCompound != null) {
			Integer dimid = LinkOptions.getDimensionUID(itemstack.stackTagCompound);
			if (dimid == null) return;
			AgeData data = AgeData.getAge(dimid, worldObj.isRemote);
			if (data == null) return;
			if (!itemstack.stackTagCompound.hasKey("Pages")) addPages(itemstack, data.getPages());
			if (!itemstack.stackTagCompound.hasKey("Authors")) addAuthors(itemstack, data.getAuthors());
		}
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
		dimid = DimensionUtils.getNewDimensionUID();
		AgeData agedata = DimensionUtils.createAge(DimensionUtils.convertDimensionUIDToID(dimid));
		LinkOptions.setDimensionUID(itemstack.stackTagCompound, dimid);
		agedata.setAgeName(LinkOptions.getDisplayName(itemstack.stackTagCompound));
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

	private void addAuthor(ItemStack itemstack, EntityPlayer player) {
		if (itemstack.stackTagCompound == null) return;
		NBTTagCompound nbttagcompound = itemstack.stackTagCompound;
		Collection<String> list = NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), new ArrayList<String>());
		list.add(player.getDisplayName());
		nbttagcompound.setTag("Authors", NBTUtils.writeStringCollection(new NBTTagList(), list));
	}

	private void addAuthors(ItemStack itemstack, Collection<String> authors) {
		if (itemstack.stackTagCompound == null) return;
		NBTTagCompound nbttagcompound = itemstack.stackTagCompound;
		Collection<String> list = NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), new ArrayList<String>());
		list.addAll(authors);
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
}
