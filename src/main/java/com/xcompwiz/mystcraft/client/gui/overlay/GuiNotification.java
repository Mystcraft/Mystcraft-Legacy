package com.xcompwiz.mystcraft.client.gui.overlay;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiNotification extends Gui {
	private Minecraft						mc;
	private RenderItem						renderitem;

	private static final ResourceLocation	background	= new ResourceLocation("textures/gui/achievement/achievement_background.png");
	private int								displayWidth;
	private int								displayHeight;
	private String							title;
	private String							message;
	private long							starttime;
	private boolean							autohide;

	private ItemStack						itemstack;

	public GuiNotification(Minecraft mc) {
		this.mc = mc;
		this.renderitem = new RenderItem();
	}

	public void post(String message) {
		post(message, null);
	}

	public void post(String message, String title) {
		this.title = title;
		this.message = message;
		this.starttime = Minecraft.getSystemTime();
		this.autohide = true;
	}

	private void func_146258_c() {
		GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		this.displayWidth = this.mc.displayWidth;
		this.displayHeight = this.mc.displayHeight;
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		this.displayWidth = scaledresolution.getScaledWidth();
		this.displayHeight = scaledresolution.getScaledHeight();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, this.displayWidth, this.displayHeight, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}

	public void render() {
		if (this.starttime != 0L && Minecraft.getMinecraft().player != null) {
			double d0 = (Minecraft.getSystemTime() - this.starttime) / 10000.0D;

			if (this.autohide) {
				if (d0 < 0.0D || d0 > 1.0D) {
					this.starttime = 0L;
					return;
				}
			} else if (d0 > 0.5D) {
				d0 = 0.5D;
			}

			this.func_146258_c();
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			double d1 = d0 * 2.0D;

			if (d1 > 1.0D) {
				d1 = 2.0D - d1;
			}

			d1 *= 4.0D;
			d1 = 1.0D - d1;

			if (d1 < 0.0D) {
				d1 = 0.0D;
			}

			d1 *= d1;
			d1 *= d1;
			int i = this.displayWidth - 160;
			int j = 0 - (int) (d1 * 36.0D);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			this.mc.getTextureManager().bindTexture(background);
			GL11.glDisable(GL11.GL_LIGHTING);
			this.drawTexturedModalRect(i, j, 96, 202, 160, 32);

			if (itemstack != null) {
				RenderHelper.enableGUIStandardItemLighting();
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				GL11.glEnable(GL11.GL_COLOR_MATERIAL);
				GL11.glEnable(GL11.GL_LIGHTING);
				this.renderitem.renderItemAndEffectIntoGUI(itemstack, i + 8, j + 8);
				GL11.glDisable(GL11.GL_LIGHTING);
				i += 22;
			}

			if (this.title == null) {
				this.mc.fontRendererObj.drawSplitString(this.message, i + 8, j + 7, this.displayWidth - i, -1);
			} else {
				this.mc.fontRendererObj.drawString(this.title, i + 8, j + 7, -256);
				this.mc.fontRendererObj.drawString(this.message, i + 8, j + 18, -1);
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	public void func_146257_b() {
		this.starttime = 0L;
	}
}
