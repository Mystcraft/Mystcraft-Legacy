package com.xcompwiz.mystcraft.item;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.event.PortalLinkEvent;
import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.data.Sounds;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkListenerManager;
import com.xcompwiz.mystcraft.linking.LinkOptions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ItemLinking extends Item implements IItemPortalActivator {

	protected ItemLinking() {
		setMaxStackSize(1);
		setMaxDamage(10);
		setNoRepair();
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public boolean isBookEnchantable(@Nonnull ItemStack itemstack, @Nonnull ItemStack enchantbook) {
		return false;
	}

	protected abstract void initialize(@Nullable World world, @Nonnull ItemStack itemstack, @Nullable Entity entity);

	@Override
	public void onUpdate(@Nonnull ItemStack itemstack, World world, Entity entity, int itemSlot, boolean flag) {
		if (world.isRemote) {
			return;
		}
		validate(world, itemstack, entity);
	}

	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, List<String> tooltip, boolean advancedTooltip) {
		if (itemstack.getTagCompound() != null) {
			String name = LinkOptions.getDisplayName(itemstack.getTagCompound());
			if (!name.isEmpty()) {
				tooltip.add(name);
			}
		}
	}

	public void activate(@Nonnull ItemStack itemstack, World world, Entity entity) {
		if (world.isRemote) return;
		if (itemstack.getTagCompound() == null) {
			return;
		}
		ILinkInfo linkinfo = getLinkInfo(itemstack);
		if (linkinfo != null && LinkListenerManager.isLinkPermitted(world, entity, linkinfo)) {
			LinkOptions.setUUID(itemstack.getTagCompound(), DimensionUtils.getDimensionUUID(linkinfo.getDimensionUID())); //TODO: Remove me (Updates old)
			onLink(itemstack, world, entity);
			LinkController.travelEntity(world, entity, linkinfo);
		}
	}

	protected void onLink(@Nonnull ItemStack itemstack, World world, Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entity;

			if (entityplayer.inventory.getCurrentItem() != itemstack) return; //Hellfire> hard check?

			if (dropItemOnLink(itemstack)) {
				// Add book to original world
				world.spawnEntity(createEntity(world, entityplayer, itemstack));
				// Remove item from inventory
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void onPortalCollision(@Nonnull ItemStack itemstack, World worldObj, Entity entity, BlockPos pos) {
		ILinkInfo info = ((ItemLinking) itemstack.getItem()).getLinkInfo(itemstack);
		info.setFlag(LinkPropertyAPI.FLAG_MAINTAIN_MOMENTUM, true);
		info.setFlag(LinkPropertyAPI.FLAG_GENERATE_PLATFORM, false);
		info.setFlag(LinkPropertyAPI.FLAG_EXTERNAL, true);
		info.setProperty(LinkPropertyAPI.PROP_SOUND, Sounds.KEY_NAME_PORTALLINK);
		MinecraftForge.EVENT_BUS.post(new PortalLinkEvent(worldObj, entity, info));
		LinkController.travelEntity(worldObj, entity, info);
	}

	@Override
	public int getPortalColor(@Nonnull ItemStack itemstack, World worldObj) {
		ILinkInfo info = ((ItemLinking) itemstack.getItem()).getLinkInfo(itemstack);
		return DimensionUtils.getLinkColor(info);
	}

	@Override
	public boolean hasEffect(@Nonnull ItemStack stack) {
		return LinkOptions.getFlag(stack.getTagCompound(), LinkPropertyAPI.FLAG_FOLLOWING);
	}

	@Override
	public boolean hasCustomEntity(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	@Nonnull
	public Entity createEntity(World world, Entity location, @Nonnull ItemStack itemstack) {
		return new EntityLinkbook(world, location, itemstack);
	}

	public boolean dropItemOnLink(@Nonnull ItemStack itemstack) {
		return !LinkOptions.getFlag(itemstack.getTagCompound(), LinkPropertyAPI.FLAG_FOLLOWING);
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
		ItemStack inHand = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote) return ActionResult.newResult(EnumActionResult.PASS, inHand);
		playerIn.openGui(Mystcraft.instance, ModGUIs.BOOK.ordinal(), worldIn, MathHelper.floor(playerIn.posX + 0.5D), MathHelper.floor(playerIn.posY + 0.5D), MathHelper.floor(playerIn.posZ + 0.5D));
		return ActionResult.newResult(EnumActionResult.SUCCESS, inHand);
	}

	@Override
	public boolean isDamaged(@Nonnull ItemStack stack) {
		return getHealth(stack) != getMaxHealth(stack);
	}

	@Override
	public int getDamage(@Nonnull ItemStack stack) {
		return (int) getMaxHealth(stack) - (int) getHealth(stack);
	}

	@Override
	public double getDurabilityForDisplay(@Nonnull ItemStack stack) {
		return (int) getMaxHealth(stack) - (int) getHealth(stack);
	}

	@Override
	public void setDamage(@Nonnull ItemStack stack, int damage) {
		setHealth(stack, getMaxHealth(stack) - damage);
	}

	@Override
	public int getMaxDamage(@Nonnull ItemStack stack) {
		return (int) getMaxHealth(stack);
	}

	public static void setHealth(@Nonnull ItemStack book, float health) {
		if (book.isEmpty()) return;
		if (book.getTagCompound() == null) {
			book.setTagCompound(new NBTTagCompound());
		}
		book.getTagCompound().setFloat("damage", getMaxHealth(book) - health);
	}

	public static float getHealth(@Nonnull ItemStack book) {
		float health = getMaxHealth(book);
		if (book.isEmpty()) return health;
		if (book.getTagCompound() == null) return health;
		Float damage = book.getTagCompound().getFloat("damage");
		health -= damage;
		return health;
	}

	public static float getMaxHealth(@Nonnull ItemStack book) {
		float health = 10;
		if (book.isEmpty()) return health;
		if (book.getTagCompound() == null) return health;
		if (!book.getTagCompound().hasKey("MaxHealth")) book.getTagCompound().setFloat("MaxHealth", health);
		health = book.getTagCompound().getFloat("MaxHealth");
		return health;
	}

	public void validate(@Nullable World worldObj, @Nonnull ItemStack itemstack, @Nullable Entity entity) {
		if (itemstack.getTagCompound() == null) {
			this.initialize(worldObj, itemstack, entity);
		}
	}

	@Nonnull
	public String getTitle(@Nonnull ItemStack itemstack) {
		if (itemstack.getTagCompound() != null) {
			return LinkOptions.getDisplayName(itemstack.getTagCompound());
		}
		return "";
	}

	@Nullable
	public ILinkInfo getLinkInfo(@Nonnull ItemStack itemstack) {
		if (itemstack.getTagCompound() != null) {
			return new LinkOptions(itemstack.getTagCompound());
		}
		return null;
	}

	//TODO: Move to IItemWritable?
	public Collection<String> getAuthors(@Nonnull ItemStack book) {
		return Collections.emptySet();
	}

}
