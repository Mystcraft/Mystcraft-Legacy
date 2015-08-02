package com.xcompwiz.mystcraft.item;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.xcompwiz.mystcraft.api.event.PortalLinkEvent;
import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.client.gui.GuiBook;
import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.data.Sounds;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.inventory.ContainerBook;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkListenerManager;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.network.NetworkUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemLinking extends Item implements IItemPortalActivator {

	public static class GuiHandlerBookItem extends GuiHandlerManager.GuiHandler {
		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, ItemStack itemstack, int slot) {
			return new ContainerBook(player.inventory, slot);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			byte slot = data.readByte();
			ItemStack itemstack = player.inventory.getStackInSlot(slot);
			if (itemstack != null) { return new GuiBook(player.inventory, slot); }
			return null;
		}
	}

	private static final int	GuiID	= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerBookItem());

	protected ItemLinking() {
		setMaxStackSize(1);
		setMaxDamage(10);
		canRepair = false;
	}

	/**
	 * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public boolean isBookEnchantable(ItemStack itemstack, ItemStack enchantbook) {
		return false;
	}

	protected abstract void initialize(World world, ItemStack itemstack, Entity entity);

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
		if (world.isRemote) { return; }
		validate(world, itemstack, entity);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean advancedTooltip) {
		if (itemstack.stackTagCompound != null) {
			String name = LinkOptions.getDisplayName(itemstack.stackTagCompound);
			if (name != null && !name.equals("")) list.add(name);
		}
	}

	public void activate(ItemStack itemstack, World world, Entity entity) {
		if (world.isRemote) return;
		if (itemstack.stackTagCompound == null) return;
		ILinkInfo linkinfo = this.getLinkInfo(itemstack);
		if (LinkListenerManager.isLinkPermitted(world, entity, linkinfo)) {
			LinkOptions.setUUID(itemstack.stackTagCompound, DimensionUtils.getDimensionUUID(linkinfo.getDimensionUID())); //TODO: Remove me (Updates old)
			onLink(itemstack, world, entity);
			LinkController.travelEntity(world, entity, linkinfo);
		}
	}

	protected void onLink(ItemStack itemstack, World world, Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entity;
			if (entityplayer.inventory.getCurrentItem() != itemstack) return;
			if (dropItemOnLink(itemstack)) {
				// Add book to original world
				world.spawnEntityInWorld(createEntity(world, entityplayer, itemstack));
				// Remove item from inventory
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			}
		}
	}

	@Override
	public void onPortalCollision(ItemStack book, World worldObj, Entity entity, int par2, int par3, int par4) {
		ILinkInfo info = ((ItemLinking) book.getItem()).getLinkInfo(book);
		info.setFlag(LinkPropertyAPI.FLAG_MAINTAIN_MOMENTUM, true);
		info.setFlag(LinkPropertyAPI.FLAG_GENERATE_PLATFORM, false);
		info.setFlag(LinkPropertyAPI.FLAG_EXTERNAL, true);
		info.setProperty(LinkPropertyAPI.PROP_SOUND, Sounds.PORTALLINK);
		MinecraftForge.EVENT_BUS.post(new PortalLinkEvent(worldObj, entity, info));
		LinkController.travelEntity(worldObj, entity, info);
	}

	@Override
	public int getPortalColor(ItemStack itemstack, World worldObj) {
		ILinkInfo info = ((ItemLinking) itemstack.getItem()).getLinkInfo(itemstack);
		return DimensionUtils.getLinkColor(info);
	}

	@Override
	public boolean hasEffect(ItemStack itemstack, int pass) {
		return LinkOptions.getFlag(itemstack.stackTagCompound, LinkPropertyAPI.FLAG_FOLLOWING);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityLinkbook(world, location, itemstack);
	}

	public boolean dropItemOnLink(ItemStack itemstack) {
		return !LinkOptions.getFlag(itemstack.stackTagCompound, LinkPropertyAPI.FLAG_FOLLOWING);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			NetworkUtils.displayGui(entityplayer, world, GuiID, itemstack);
		}
		return itemstack;
	}

	@Override
	public boolean isDamaged(ItemStack stack) {
		return getHealth(stack) != getMaxHealth(stack);
	}

	@Override
	public int getDamage(ItemStack stack) {
		return (int) getMaxHealth(stack) - (int) getHealth(stack);
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getDisplayDamage(ItemStack stack) {
		return (int) getMaxHealth(stack) - (int) getHealth(stack);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		setHealth(stack, getMaxHealth(stack) - damage);
	}

	/**
	 * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in this item, but can be overridden here for other sources such as NBT.
	 * @param stack The itemstack that is damaged
	 * @return the damage value
	 */
	@Override
	public int getMaxDamage(ItemStack stack) {
		return (int) getMaxHealth(stack);
	}

	public static void setHealth(ItemStack book, float health) {
		if (book == null) return;
		if (book.stackTagCompound == null) book.stackTagCompound = new NBTTagCompound();
		book.stackTagCompound.setFloat("damage", getMaxHealth(book) - health);
	}

	public static float getHealth(ItemStack book) {
		float health = getMaxHealth(book);
		if (book == null) return health;
		if (book.stackTagCompound == null) return health;
		Float damage = book.stackTagCompound.getFloat("damage");
		if (damage != null) health -= damage;
		return health;
	}

	public static float getMaxHealth(ItemStack book) {
		float health = 10;
		if (book == null) return health;
		if (book.stackTagCompound == null) return health;
		if (!book.stackTagCompound.hasKey("MaxHealth")) book.stackTagCompound.setFloat("MaxHealth", health);
		health = book.stackTagCompound.getFloat("MaxHealth");
		return health;
	}

	public void validate(World worldObj, ItemStack itemstack, Entity entity) {
		if (itemstack.stackTagCompound == null) {
			this.initialize(worldObj, itemstack, entity);
		}
	}

	public String getTitle(ItemStack itemstack) {
		if (itemstack.stackTagCompound != null) { return LinkOptions.getDisplayName(itemstack.stackTagCompound); }
		return "";
	}

	public ILinkInfo getLinkInfo(ItemStack itemstack) {
		if (itemstack.stackTagCompound != null) {
			ILinkInfo info = new LinkOptions(itemstack.stackTagCompound);
			return info;
		}
		return null;
	}

	//TODO: Move to IItemWritable?
	public Collection<String> getAuthors(ItemStack book) {
		return Collections.EMPTY_SET;
	}
}
