package com.xcompwiz.mystcraft.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.item.IItemWritable;
import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemAgebook extends ItemLinking implements IItemWritable, IItemPageProvider, IItemOnLoadable {

	public ItemAgebook() {
		setUnlocalizedName("myst.agebook");
		setCreativeTab(MystcraftCommonProxy.tabMystCommon);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
		}
	}

	@Override
	@Nonnull
	public EnumRarity getRarity(@Nonnull ItemStack itemstack) {
		return itemstack.isItemEnchanted() ? EnumRarity.RARE : EnumRarity.EPIC;
	}

	@Override
	protected void initialize(World world, @Nonnull ItemStack itemstack, Entity entity) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
			//An empty book is defaulted to having a single link panel in it. This code is fallback only; it doesn't happen in normal gameplay (only creative/NEI)
			LinkOptions.setFlag(itemstack.getTagCompound(), LinkPropertyAPI.FLAG_GENERATE_PLATFORM, true);
			this.addPages(itemstack, getDefaultPages(itemstack));
		}
	}

	@Override
	public void validate(World worldObj, @Nonnull ItemStack itemstack, Entity entity) {
		super.validate(worldObj, itemstack, entity);
		if (itemstack.getTagCompound() != null) {
			if (!itemstack.getTagCompound().hasKey("Pages")) {
				addPages(itemstack, getDefaultPages(itemstack));
			}
		}
	}

	private Collection<ItemStack> getDefaultPages(@Nonnull ItemStack itemstack) {
		Collection<ItemStack> collection = Collections.singleton(Page.createLinkPage());
		Integer dimid = LinkOptions.getDimensionUID(itemstack.getTagCompound());
		if (dimid == null)
			return collection;
		AgeData data = AgeData.getAge(dimid, false); //TODO: We're always assuming we are the server when grabbing age data on Agebook load
		if (data == null)
			return collection;
		collection = data.getPages();
		return collection;
	}

	public static void initializeCompound(@Nonnull ItemStack itemstack, int dimId, AgeData agedata) {
		itemstack.setTagCompound(new NBTTagCompound());
		LinkOptions.setDimensionUID(itemstack.getTagCompound(), dimId);
		LinkOptions.setUUID(itemstack.getTagCompound(), agedata.getUUID());
		LinkOptions.setDisplayName(itemstack.getTagCompound(), agedata.getAgeName());
		LinkOptions.setFlag(itemstack.getTagCompound(), LinkPropertyAPI.FLAG_GENERATE_PLATFORM, true);
		((ItemAgebook) itemstack.getItem()).addPages(itemstack, agedata.getPages());
	}

	public static void create(@Nonnull ItemStack agebook, EntityPlayer player, List<ItemStack> pages, String pendingtitle) {
		agebook.setTagCompound(new NBTTagCompound());

		((ItemAgebook) agebook.getItem()).addPages(agebook, pages);
		((ItemAgebook) agebook.getItem()).addAuthor(agebook, player);
		((ItemAgebook) agebook.getItem()).setDisplayName(player, agebook, pendingtitle);
		if (pages.isEmpty())
			return;

		ItemStack linkpanel = pages.get(0);
		if (Page.isLinkPanel(linkpanel)) {
			Page.applyLinkPanel(linkpanel, agebook);
		}
	}

	public static boolean isNewAgebook(@Nonnull ItemStack itemstack) {
		if (!(itemstack.getItem() instanceof ItemAgebook)) {
			return false;
		}
		if (itemstack.getTagCompound() == null) {
			return false;
		}
		Integer dimid = LinkOptions.getDimensionUID(itemstack.getTagCompound());
		if (dimid != null) {
			return false;
		}
		List<ItemStack> pages = ((ItemAgebook) itemstack.getItem()).getPageList(null, itemstack);
		return !pages.isEmpty() && Page.isLinkPanel(pages.get(0));
	}

	@Override
	public void onCreated(@Nonnull ItemStack stack, World world, EntityPlayer player) {}

	@Override
	public void activate(@Nonnull ItemStack itemstack, World worldObj, Entity entity) {
		if (worldObj.isRemote)
			return;

		checkFirstLink(itemstack, worldObj, entity);

		super.activate(itemstack, worldObj, entity);
	}

	private void checkFirstLink(@Nonnull ItemStack itemstack, World worldObj, Entity entity) {
		if (itemstack.getTagCompound() == null)
			return;
		Integer dimid = LinkOptions.getDimensionUID(itemstack.getTagCompound());
		if (dimid != null)
			return;
		dimid = DimensionUtils.createAge();
		AgeData agedata = AgeData.getAge(dimid, false);
		LinkOptions.setDimensionUID(itemstack.getTagCompound(), dimid);
		LinkOptions.setUUID(itemstack.getTagCompound(), agedata.getUUID());
		agedata.setAgeName(LinkOptions.getDisplayName(itemstack.getTagCompound()));
		String seed = LinkOptions.getProperty(itemstack.getTagCompound(), "Seed");
		if (seed != null) {
			agedata.setSeed(Long.parseLong(seed));
		} else {
			LinkOptions.setProperty(itemstack.getTagCompound(), "Seed", Long.toString(agedata.getSeed()));
		}
		updatePageList(itemstack);
		agedata.setPages(getPageList(null, itemstack));
	}

	@Override
	public String getDisplayName(EntityPlayer player, @Nonnull ItemStack itemstack) {
		return LinkOptions.getDisplayName(itemstack.getTagCompound());
	}

	@Override
	public void setDisplayName(EntityPlayer player, @Nonnull ItemStack itemstack, String name) {
		LinkOptions.setDisplayName(itemstack.getTagCompound(), name);
		AgeData data = getAgeData(itemstack, player.world.isRemote);
		if (data != null) {
			data.setAgeName(name);
		}
	}

	public void setSeed(EntityPlayer player, @Nonnull ItemStack itemstack, long seed) {
		AgeData data = getAgeData(itemstack, player.world.isRemote);
		if (data != null) {
			data.setSeed(seed);
			LinkOptions.setProperty(itemstack.getTagCompound(), "Seed", Long.toString(data.getSeed()));
		} else {
			LinkOptions.setProperty(itemstack.getTagCompound(), "Seed", Long.toString(seed));
		}
	}

	@Override
	public boolean writeSymbol(EntityPlayer player, @Nonnull ItemStack itemstack, ResourceLocation symbol) {
		if (isVisited(itemstack, player.world.isRemote))
			return false;
		if (itemstack.getTagCompound() == null)
			return false;
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		Collection<ItemStack> list = NBTUtils.readItemStackCollection(nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
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

	private void addPages(@Nonnull ItemStack itemstack, Collection<ItemStack> pages) {
		if (itemstack.getTagCompound() == null)
			return;
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		Collection<ItemStack> list = NBTUtils.readItemStackCollection(nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
		list.addAll(pages);
		nbttagcompound.setTag("Pages", NBTUtils.writeItemStackCollection(new NBTTagList(), list));
	}

	@Override
	public List<ItemStack> getPageList(@Nullable EntityPlayer player, @Nonnull ItemStack itemstack) {
		if (itemstack.getTagCompound() == null)
			return Collections.emptyList();
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		return NBTUtils.readItemStackCollection(nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
	}

	private void setPageList(@Nonnull ItemStack itemstack, List<ItemStack> pagelist) {
		if (itemstack.getTagCompound() == null)
			return;
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		nbttagcompound.setTag("Pages", NBTUtils.writeItemStackCollection(new NBTTagList(), pagelist));
	}

	private void updatePageList(@Nonnull ItemStack itemstack) {
		setPageList(itemstack, SymbolRemappings.remap(getPageList(null, itemstack)));
	}

	private void addAuthor(@Nonnull ItemStack itemstack, EntityPlayer player) {
		if (itemstack.getTagCompound() == null)
			return;
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		Collection<String> list = NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), new ArrayList<>());
		list.add(player.getDisplayNameString());
		nbttagcompound.setTag("Authors", NBTUtils.writeStringCollection(new NBTTagList(), list));
	}

	@Override
	public Collection<String> getAuthors(@Nonnull ItemStack itemstack) {
		if (itemstack.getTagCompound() == null)
			return Collections.emptyList();
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		return NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), new ArrayList<>());
	}

	@Nullable
	private AgeData getAgeData(ItemStack itemstack, boolean isRemote) {
		if (itemstack.getTagCompound() == null)
			return null;
		Integer uid = LinkOptions.getDimensionUID(itemstack.getTagCompound());
		if (uid == null)
			return null;
		return AgeData.getAge(uid, isRemote);
	}

	private boolean isVisited(ItemStack itemstack, boolean isRemote) {
		AgeData agedata = getAgeData(itemstack, isRemote);
		return agedata != null && agedata.isVisited();
	}

	@Override
	@Nonnull
	public ItemStack onLoad(@Nonnull ItemStack itemstack) {
		updatePageList(itemstack);
		this.initialize(null, itemstack, null);
		this.validate(null, itemstack, null);
		if (getPageList(null, itemstack).isEmpty())
			addPages(itemstack, getDefaultPages(itemstack)); //TODO: Make this an "onItemEnteredPlayerInventory" call as well
		return itemstack;
	}
}
