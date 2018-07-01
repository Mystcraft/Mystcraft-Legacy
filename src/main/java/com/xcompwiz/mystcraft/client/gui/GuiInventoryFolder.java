package com.xcompwiz.mystcraft.client.gui;

import java.util.Arrays;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface.PositionableItem;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.inventory.ContainerFolder;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class GuiInventoryFolder extends GuiContainerElements {

	public class GuiElementSurfaceControls extends GuiElementSurfaceControlsBase {

		public GuiElementSurfaceControls(Minecraft mc, int guiLeft, int guiTop, int width, int height) {
			super(mc, guiLeft, guiTop, width, height);
		}

		@Override
		@Nonnull
		public ItemStack getItemStack() {
			return container.getInventoryItem();
		}

		@Override
		public void place(int index, boolean single) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setByte(ContainerFolder.Messages.AddToSurface, (byte) 0);
			nbttagcompound.setBoolean("Single", single);
			nbttagcompound.setInteger("Index", index);
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}

		@Override
		public void pickup(PositionableItem collectionelement) {
			if (collectionelement.count <= 0) {
				return;
			}
			boolean iscollection = getItemStack().getItem() instanceof IItemPageCollection;
			if (iscollection) {
				NBTTagCompound itemdata = new NBTTagCompound();
				ItemStack page = collectionelement.itemstack;
				if (GuiWritingDesk.isShiftKeyDown()) {
					page = page.copy();
					page.setCount(64);
				}
				page.writeToNBT(itemdata);
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setTag(ContainerFolder.Messages.RemoveFromCollection, itemdata);
				MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
				container.processMessage(mc.player, nbttagcompound);
			} else {
				int index = collectionelement.slotId;
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setInteger(ContainerFolder.Messages.RemoveFromOrderedCollection, index);
				MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
				container.processMessage(mc.player, nbttagcompound);
			}
		}

		@Override
		public void copy(PositionableItem collectionelement) {}
	}

	private ContainerFolder container;

	private int mainTop;

	private static final int surfaceY = 132;
	private static final int buttonssize = 18;
	private static final int invsizeX = 176;
	private static final int invsizeY = 80;

	public GuiInventoryFolder(InventoryPlayer inventoryplayer, int slot) {
		super(new ContainerFolder(inventoryplayer, slot));
		this.container = (ContainerFolder) this.inventorySlots;
	}

	@Override
	public void validate() {
		xSize = invsizeX;
		ySize = surfaceY + buttonssize + invsizeY + 1;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		mainTop = guiTop + surfaceY + 1;

		GuiElementTextField txt_box = null;

		GuiElementSurfaceControls surfacemanager = new GuiElementSurfaceControls(this.mc, 0, 0, xSize, surfaceY);
		txt_box = new GuiElementTextField(surfacemanager, surfacemanager, "SearchBox", (buttonssize + 2) * 2, 0, xSize - (buttonssize + 2) * 2, buttonssize);
		addElement(txt_box);

		GuiElementPageSurface surface = new GuiElementPageSurface(surfacemanager, this.mc, 0, buttonssize + 1, xSize, surfaceY - buttonssize);
		surfacemanager.addListener(surface);
		addElement(surface);

		GuiElementButton btn_sortA = new GuiElementButtonToggle(surfacemanager, surfacemanager, "AZ", 0, 0, buttonssize, buttonssize);
		btn_sortA.setText("AZ");
		btn_sortA.setTooltip(Arrays.asList("Sort Alphabetically"));
		addElement(btn_sortA);
		GuiElementButton btn_allsym = new GuiElementButtonToggle(surfacemanager, surfacemanager, "ALL", buttonssize, 0, buttonssize, buttonssize);
		btn_allsym.setText("ALL");
		btn_allsym.setTooltip(Arrays.asList("Show all Symbols"));
		addElement(btn_allsym);

		surfacemanager.addSurfaceElement(btn_sortA);
		surfacemanager.addSurfaceElement(btn_allsym);
	}

	@Override
	protected void _drawBackgroundLayer(int mouseX, int mouseY, float f) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		//XXX: Better texture to use here?
		mc.renderEngine.bindTexture(GUIs.desk);
		drawTexturedModalRect(guiLeft, mainTop, 0, 82, invsizeX, invsizeY);
	}
}
