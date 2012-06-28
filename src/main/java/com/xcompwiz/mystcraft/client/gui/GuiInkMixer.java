package com.xcompwiz.mystcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.render.DniColorRenderer;
import com.xcompwiz.mystcraft.data.Assets;
import com.xcompwiz.mystcraft.data.LoaderFluids;
import com.xcompwiz.mystcraft.inventory.ContainerInkMixer;
import com.xcompwiz.mystcraft.network.MPacketGuiMessage;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.symbol.Color;
import com.xcompwiz.mystcraft.symbol.ColorGradient;
import com.xcompwiz.mystcraft.tileentity.TileEntityInkMixer;
import com.xcompwiz.util.Vector;
import com.xcompwiz.util.VectorPool;

public class GuiInkMixer extends GuiContainer {

	private ContainerInkMixer	container;
	private int					frame	= 0;

	private static final int	basinR	= 900;
	private static final int	basinX	= 88;
	private static final int	basinY	= 49;

	public GuiInkMixer(InventoryPlayer inventoryplayer, TileEntityInkMixer tileentity) {
		super(new ContainerInkMixer(inventoryplayer, tileentity));
		this.container = (ContainerInkMixer) this.inventorySlots;
		guiLeft = 0;
	}

	@Override
	public void initGui() {
		super.initGui();
		xSize = 176;
		ySize = 181;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.container.updateCraftResult();
	}

	@Override
	protected void keyTyped(char c, int i) {
		if (i == 1 || (i == mc.gameSettings.keyBindInventory.getKeyCode())) {
			mc.thePlayer.closeScreen();
		} else {
			super.keyTyped(c, i);
		}
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		int x = i - basinX - guiLeft;
		int y = j - basinY - guiTop;
		if (x * x + y * y < basinR) {
			if (mc.thePlayer.inventory.getItemStack() != null) {
				// NOTE: We're client side, so we need to communicate what we want done to the server
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setBoolean("Consume", true);
				nbttagcompound.setBoolean("Single", (k == 1));
				MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
				container.processMessage(mc.thePlayer, nbttagcompound);
				return;
			}
		}
		super.mouseClicked(i, j, k);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(Assets.gui_mixer);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft + 54, guiTop + 16, 179, 16, 66, 65);
		if (container.hasInk()) renderTank(guiLeft + 54, guiTop + 16, 66, 65);

		mc.renderEngine.bindTexture(Assets.gui_mixer);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), guiLeft + 8, guiTop + (ySize - 96) + 2, 0x404040);
	}

	private void renderTank(int left, int top, int width, int height) {
		Fluid fluid = LoaderFluids.black_ink;
		GuiUtils.drawFluid(mc.renderEngine, fluid, left, top, width, height, this.zLevel);
		ColorGradient gradient = container.getPropertyGradient();
		if (gradient != null && gradient.getColorCount() > 0) {
			frame += 1;
			// if (frame > gradient.getLength()*300) frame = 0;
			Color colorobj = gradient.getColor(frame / 300F);
			int color = colorobj.asInt();
			drawGradientRect(left, top, left + width, top + height, 0x40000000 + color, 0xB0000000 + color);

			Vector center = VectorPool.getFreeVector(left + width / 2 + 1, top + height / 2 + 1, 0);
			DniColorRenderer.render(colorobj.toAWT(), center, 20.0D);

		}
	}
}
