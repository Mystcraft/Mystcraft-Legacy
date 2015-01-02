package com.xcompwiz.mystcraft.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.gui.GuiBook;
import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.ContainerBook;
import com.xcompwiz.mystcraft.inventory.InventoryBook;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.network.packet.MPacketMessage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityLinkbook extends EntityLiving implements IInventory, IMessageReceiver {

	public static class GuiHandlerBookEntity extends GuiHandlerManager.GuiHandler {
		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, Entity entity) {
			NetworkUtils.sendAgeData(worldObj, ((EntityLinkbook) entity).inventory.getStackInSlot(0), player);

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			((EntityLinkbook) entity).writeEntityToNBT(nbttagcompound);
			player.playerNetServerHandler.sendPacket(MPacketMessage.createPacket(entity, nbttagcompound));
			return new ContainerBook(player.inventory, ((EntityLinkbook) entity).inventory);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			int entityId = data.readInt();
			Entity entity = Mystcraft.sidedProxy.getEntityByID(player.worldObj, entityId);
			if (entity != null && entity instanceof EntityLinkbook) { return new GuiBook(player.inventory, (EntityLinkbook) entity); }
			return null;
		}
	}

	private static final int	GuiID		= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerBookEntity());

	private int					decaytimer;
	public InventoryBook		inventory;

	private static final int	bookitemID	= 4;
	private static final int	agenameID	= 20;

	public EntityLinkbook(World world) {
		super(world);
		setSize(0.7F, 0.2F);
		renderDistanceWeight = 8.0D;
		dataWatcher.addObject(bookitemID, Integer.valueOf(0)); // Book type
		dataWatcher.addObject(agenameID, String.valueOf("")); // Age name
		yOffset = 0.0F;
		inventory = new InventoryBook(this);
		newPosRotationIncrements = 0;
	}

	public EntityLinkbook(World world, double d, double d1, double d2) {
		this(world);
		setPosition(d, d1, d2);
	}

	public EntityLinkbook(World world, Entity entity, ItemStack item) {
		this(world);
		inventory.setBook(item);
		dataWatcher.updateObject(bookitemID, Item.getIdFromItem(item.getItem()));
		dataWatcher.updateObject(agenameID, LinkOptions.getDisplayName(inventory.getBook().stackTagCompound));
		setLocationAndAngles(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, entity.rotationYaw, entity.rotationPitch);
		this.motionX = entity.motionX;
		this.motionY = entity.motionY;
		this.motionZ = entity.motionZ;
		posX -= MathHelper.cos((rotationYaw / 180F) * 3.141593F) * 0.16F;
		posY -= 0.10000000149011612D;
		posZ -= MathHelper.sin((rotationYaw / 180F) * 3.141593F) * 0.16F;
		setPosition(posX, posY, posZ);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	public Item getItem() {
		return Item.getItemById(dataWatcher.getWatchableObjectInt(bookitemID));
	}

	public String getAgeName() {
		return dataWatcher.getWatchableObjectString(agenameID);
	}

	@Override
	protected String getHurtSound() {
		return null;
	}

	@Override
	protected String getDeathSound() {
		return null;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (damagesource == DamageSource.inWall) return false;
		if (damagesource.isFireDamage()) {
			i *= 2;
			setFire((int) i);
		}
		super.attackEntityFrom(damagesource, i);
		return true;
	}

	@Override
	public void setHealth(float par1) {
		super.setHealth(par1);
		updateBookHealth();
	}

	private void updateBookHealth() {
		if (inventory == null) return;
		if (this.inventory.getBook() != null) {
			ItemLinking.setHealth(this.inventory.getBook(), this.getHealth());
		}
	}

	private float getBookHealth() {
		ItemStack book = null;
		if (this.inventory != null) {
			book = this.inventory.getBook();
		}
		return ItemLinking.getHealth(book);
	}

	@Override
	public void knockBack(Entity entity, float i, double d, double d1) {}

	@Override
	protected void updateEntityActionState() {}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("DecayTimer", decaytimer);
		nbttagcompound.setTag("Item", this.inventory.getBook().writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		decaytimer = nbttagcompound.getInteger("DecayTimer");
		if (nbttagcompound.hasKey("Book Data")) {
			this.inventory.setBook(new ItemStack(nbttagcompound.getInteger("Book Type") == 0 ? ModItems.agebook : ModItems.linkbook, 1, 0));
			this.inventory.getBook().stackTagCompound = nbttagcompound.getCompoundTag("Book Data");
		} else {
			NBTTagCompound item = nbttagcompound.getCompoundTag("Item");
			this.inventory.setBook(ItemStack.loadItemStackFromNBT(item));
		}
		if (this.inventory.getBook() == null) {
			this.setDead();
		}
		dataWatcher.updateObject(bookitemID, Item.getIdFromItem(inventory.getBook().getItem()));
		dataWatcher.updateObject(agenameID, LinkOptions.getDisplayName(inventory.getBook().stackTagCompound));
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		if (entity instanceof EntityMinecartHopper) {
			if (inventory.getBook() == null) return;
			EntityMinecartHopper hoppercart = (EntityMinecartHopper) entity;
			ItemStack itemstack1 = TileEntityHopper.func_145889_a(hoppercart, inventory.getBook(), -1);
			inventory.setBook(itemstack1);
			return;
		}
		super.collideWithEntity(entity);
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		if (worldObj.isRemote) return true;
		if (entityplayer.isSneaking() && entityplayer.inventory.getCurrentItem() == null) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, inventory.getBook());
			entityplayer.inventory.markDirty();
			setDead();
			return true;
		}
		NetworkUtils.displayGui(entityplayer, worldObj, GuiID, this);
		return true;
	}

	@Override
	public void onUpdate() {
		this.setHealth(this.getBookHealth());

		super.onUpdate();

		this.noClip = this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);

		if (worldObj.isRemote) return;
		++decaytimer;
		if (decaytimer % 10000 == 0) {
			attackEntityFrom(DamageSource.starve, 1);
		}
		if (isWet()) {
			attackEntityFrom(DamageSource.drown, 1);
		}
		if (inventory.getBook() == null) {
			setDead();
		}
	}

	/**
	 * Decrements the entity's air supply when underwater
	 */
	@Override
	protected int decreaseAirSupply(int par1) {
		return par1 - 2;
	}

	@Override
	public void processMessageData(NBTTagCompound nbttagcompound) {
		readEntityFromNBT(nbttagcompound);
	}

	public void linkEntity(Entity entity) {
		inventory.linkEntity(entity);
	}

	@Override
	public int getSizeInventory() {
		if (inventory == null) return 0;
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (inventory == null) return null;
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventory == null) return null;
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (inventory == null) return null;
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (inventory == null) return;
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInventoryName() {
		if (inventory == null) return "";
		return inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		if (inventory == null) return false;
		return inventory.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		if (inventory == null) return 0;
		return inventory.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		if (inventory == null) return;
		inventory.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (inventory == null) return false;
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openInventory() {
		if (inventory == null) return;
		inventory.openInventory();
	}

	@Override
	public void closeInventory() {
		if (inventory == null) return;
		inventory.closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (inventory == null) return false;
		return inventory.isItemValidForSlot(i, itemstack);
	}
}
