package com.xcompwiz.mystcraft.client.gui;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.inventory.ContainerVillagerShop;

public class GuiVillagerShop extends GuiContainerElements {

	private ContainerVillagerShop	container;

	public GuiVillagerShop(InventoryPlayer playerinv, EntityVillager entity) {
		super(new ContainerVillagerShop(playerinv, entity));
		this.container = (ContainerVillagerShop) this.inventorySlots;
	}

	@Override
	public void validate() {
		xSize = 176;
		ySize = 181;
	}

	@Override
	protected void _drawBackgroundLayer(int mouseX, int mouseY, float partial) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(GUIs.villagershop);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}
