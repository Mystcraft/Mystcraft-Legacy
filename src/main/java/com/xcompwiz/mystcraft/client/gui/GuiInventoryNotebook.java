package com.xcompwiz.mystcraft.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.data.Assets;
import com.xcompwiz.mystcraft.inventory.ContainerNotebook;
import com.xcompwiz.mystcraft.network.MPacketGuiMessage;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInventoryNotebook extends GuiContainerElements {

	private int		currentScroll;
	private float	scrollfloat;
	private boolean	isScrolling;
	private boolean	wasClicking;

	// XXX: (GuiElementScrollbar) Scrollbar element?
	// XXX: (GuiElementItemSlot) If I had control over the rendering of itemslots, I could actually handle all slots on the notebook, and only display currently visible ones...  This would move the rendering limitation into rendering only, so the changes occur in only one place, the GUI

	public GuiInventoryNotebook(InventoryPlayer inventoryplayer, int slot) {
		super(new ContainerNotebook(inventoryplayer, slot));
		currentScroll = 0;
	}

	@Override
	public void validate() {
		xSize = 194;
		ySize = 181;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	protected void keyTyped(char c, int i) {
		if (i == 1 || (i == mc.gameSettings.keyBindInventory.getKeyCode())) {
			mc.thePlayer.closeScreen();
		} else {
			super.keyTyped(c, i);
		}
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int input = Mouse.getEventDWheel();

		if (input != 0) {
			int rows = ((ContainerNotebook) this.inventorySlots).rowCount();

			if (input > 0) {
				input = 1;
			}
			if (input < 0) {
				input = -1;
			}

			this.currentScroll -= input;

			if (this.currentScroll < 0) {
				this.currentScroll = 0;
			}

			if (this.currentScroll > rows) {
				this.currentScroll = rows;
			}
			this.scrollfloat = (float) currentScroll / (float) rows;

			((ContainerNotebook) this.inventorySlots).scrollTo(this.currentScroll);
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("ScrollTo", currentScroll);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(this.inventorySlots.windowId, nbttagcompound));
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		boolean isClicking = Mouse.isButtonDown(0);
		int sliderleft = guiLeft + xSize - 20;
		int slidertop = guiTop + 22;
		int sliderbottom = guiTop + ySize - 23;
		int sliderright = sliderleft + 14;

		if (!this.wasClicking && isClicking && mouseX >= sliderleft && mouseY >= slidertop && mouseX < sliderright && mouseY < sliderbottom) {
			this.isScrolling = true;
		}

		if (!isClicking) {
			this.isScrolling = false;
		}
		this.wasClicking = isClicking;
		if (this.isScrolling) {
			int rows = ((ContainerNotebook) this.inventorySlots).rowCount();
			this.scrollfloat = (mouseY - slidertop - 7.5F) / (sliderbottom - slidertop);

			if (this.scrollfloat < 0.0F) {
				this.scrollfloat = 0.0F;
			}

			if (this.scrollfloat > 1.0F) {
				this.scrollfloat = 1.0F;
			}

			this.currentScroll = (int) (scrollfloat * rows);
			((ContainerNotebook) this.inventorySlots).scrollTo(this.currentScroll);
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("ScrollTo", currentScroll);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(this.inventorySlots.windowId, nbttagcompound));
		}

		mc.renderEngine.bindTexture(Assets.gui_scrollable);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		this.drawTexturedModalRect(sliderleft, slidertop + (int) ((sliderbottom - slidertop) * this.scrollfloat), xSize, 0, 12, 15);
		fontRendererObj.drawString(((ContainerNotebook) this.inventorySlots).getNotebookName(), guiLeft + 8, guiTop + 8, 0x404040);
	}
}
