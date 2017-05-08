package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.inventory.IFluidTankProvider;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElementFluidTank extends GuiElement {
	private List<String>		hovertext	= new ArrayList<String>();
	private boolean				mouseOver;

	private IFluidTankProvider	fluidprovider;

	public GuiElementFluidTank(IGuiMessageHandler handler, Minecraft mc, int left, int top, int width, int height, IFluidTankProvider fluidprovider) {
		super(left, top, width, height);
		this.mc = mc;
		this.fluidprovider = fluidprovider;
	}

	@Override
	public List<String> _getTooltipInfo() {
		if (hovertext != null && hovertext.size() > 0) { return hovertext; }
		return super._getTooltipInfo();
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		mouseOver = this.contains(mouseX, mouseY);

		FluidStack fluidstack = fluidprovider.getFluid();
		int tankmax = fluidprovider.getMax();

		int guiLeft = getLeft();
		int guiTop = getTop();
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0);
		mouseX -= guiLeft;
		mouseY -= guiTop;

		renderTank(fluidstack, 0, 0, xSize, ySize);

		hovertext.clear();
		if (mouseOver && fluidstack != null) {
			hovertext.add((fluidstack.getFluid().getLocalizedName(fluidstack) + ": " + fluidstack.amount + "/" + tankmax));
		}

		GL11.glPopMatrix();
	}

	private void renderTank(FluidStack fluidstack, int left, int top, int width, int height) {
		drawGradientRect(left, top, left + width, top + height, 0x99000000, 0x99000000);
		drawGradientRect(left + 1, top + 1, left + width - 1, top + height - 1, 0xFFCCCCEE, 0xFF666699);
		if (fluidstack == null) return;
		float filled = fluidstack.amount / 1000.0F;
		if (filled > 1.0F) filled = 1;
		int ltop = top + height - 1;
		int lheight = (int) ((height - 2) * (filled));
		GuiUtils.drawFluid(mc.renderEngine, fluidstack, left + 1, ltop - lheight, width - 2, lheight, this.getZLevel());
	}

}
