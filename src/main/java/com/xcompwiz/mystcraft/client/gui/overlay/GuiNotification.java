package com.xcompwiz.mystcraft.client.gui.overlay;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiNotification extends Gui {

    private static final ResourceLocation achievementBG = new ResourceLocation("textures/gui/toasts.png");
    private int width;
    private int height;
    private String title;
    private String message;
    private long notificationTime;
    private boolean permanentNotification;

    public void post(String message) {
        post(message, null);
    }

    public void post(String message, String title) {
        this.title = title;
        this.message = message;
        this.notificationTime = Minecraft.getSystemTime();
        this.permanentNotification = false;
    }

    private void updateAchievementWindowScale() {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        this.width = mc.displayWidth;
        this.height = mc.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, this.width, this.height, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.notificationTime != 0L && Minecraft.getMinecraft().player != null) {
            double d0 = (double)(Minecraft.getSystemTime() - this.notificationTime) / 3000.0D;

            if (this.permanentNotification) {
                if (d0 > 0.5D) {
                    d0 = 0.5D;
                }
            } else if (d0 < 0.0D || d0 > 1.0D) {
                this.notificationTime = 0L;
                return;
            }

            this.updateAchievementWindowScale();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            double d1 = d0 * 2.0D;

            if (d1 > 1.0D) {
                d1 = 2.0D - d1;
            }

            d1 = d1 * 4.0D;
            d1 = 1.0D - d1;

            if (d1 < 0.0D) {
                d1 = 0.0D;
            }

            d1 = d1 * d1;
            d1 = d1 * d1;

            int i = this.width - 160;
            int j = 0 - (int)(d1 * 36.0D);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            mc.getTextureManager().bindTexture(achievementBG);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(i, j, 0, 0, 160, 32);

            if (this.title == null) {
                mc.fontRenderer.drawSplitString(this.message, i + 30, j + 7, 120, -1);
            } else {
                mc.fontRenderer.drawString(this.title, i + 30, j + 7, -256);
                mc.fontRenderer.drawString(this.message, i + 30, j + 18, -1);
            }

            //RenderHelper.enableGUIStandardItemLighting();
            //GlStateManager.disableLighting();
            //GlStateManager.enableRescaleNormal();
            //GlStateManager.enableColorMaterial();
            //GlStateManager.enableLighting();
            //this.renderItem.renderItemAndEffectIntoGUI(this.theAchievement.theItemStack, i + 8, j + 8);
            //GlStateManager.disableLighting();

            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }

    public void clearAchievements() {
        this.notificationTime = 0L;
    }

}
