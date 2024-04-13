package com.xcompwiz.mystcraft.client.render;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.world.AgeController;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeatherRendererMyst extends IRenderHandler {
	private static final ResourceLocation	locationRainPng	= new ResourceLocation("textures/environment/rain.png");
	private static final ResourceLocation	locationSnowPng	= new ResourceLocation("textures/environment/snow.png");

	private int								rendererUpdateCount;

	private float[]							rainXCoords;
	private float[]							rainYCoords;
	private Random							random			= new Random();

	public WeatherRendererMyst(WorldProviderMyst provider, AgeController controller) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient worldObj, Minecraft mc) {
		float rain_strength = worldObj.getRainStrength(partialTicks);

		if (rain_strength > 0.0F) {
			mc.entityRenderer.enableLightmap(partialTicks);

			if (this.rainXCoords == null) {
				this.rainXCoords = new float[1024];
				this.rainYCoords = new float[1024];

				for (int x = 0; x < 32; ++x) {
					for (int z = 0; z < 32; ++z) {
						float x2 = (z - 16);
						float z2 = (x - 16);
						float distance = MathHelper.sqrt_float(x2 * x2 + z2 * z2);
						this.rainXCoords[x << 5 | z] = -z2 / distance;
						this.rainYCoords[x << 5 | z] = x2 / distance;
					}
				}
			}

			GL11.glPushMatrix();
			GL11.glRotatef(0, 1, 0, 0);
			EntityLivingBase entitylivingbase = mc.renderViewEntity;
			WorldClient worldclient = worldObj;
			int entityX = MathHelper.floor_double(entitylivingbase.posX);
			int entityY = MathHelper.floor_double(entitylivingbase.posY);
			int entityZ = MathHelper.floor_double(entitylivingbase.posZ);
			Tessellator tessellator = Tessellator.instance;
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
			mc.getTextureManager().bindTexture(locationSnowPng);
			double smoothedX = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * partialTicks;
			double smoothedY = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * partialTicks;
			double smoothedZ = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * partialTicks;
			int ismoothedY = MathHelper.floor_double(smoothedY);
			byte iteration_count = 5;

			if (mc.gameSettings.fancyGraphics) {
				iteration_count = 10;
			}

			byte pass_param = -1;
			float softTick = this.rendererUpdateCount + partialTicks;

			if (mc.gameSettings.fancyGraphics) {
				iteration_count = 10;
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			for (int zPos = entityZ - iteration_count; zPos <= entityZ + iteration_count; ++zPos) {
				for (int xPos = entityX - iteration_count; xPos <= entityX + iteration_count; ++xPos) {
					int coords = (zPos - entityZ + 16) * 32 + xPos - entityX + 16;
					float rainX = this.rainXCoords[coords] * 0.5F;
					float rainZ = this.rainYCoords[coords] * 0.5F;
					BiomeGenBase biomegenbase = worldclient.getBiomeGenForCoords(xPos, zPos);

					if (biomegenbase.canSpawnLightningBolt() || biomegenbase.getEnableSnow()) {
						int height = worldclient.getPrecipitationHeight(xPos, zPos);
						int lowY = entityY - iteration_count;
						int higY = entityY + iteration_count;

						if (lowY < height) {
							lowY = height;
						}

						if (higY < height) {
							higY = height;
						}

						float scale = 1.0F;
						int i3 = height;

						if (height < ismoothedY) {
							i3 = ismoothedY;
						}

						if (lowY != higY) {
							this.random.setSeed((xPos * xPos * 3121 + xPos * 45238971 ^ zPos * zPos * 418711 + zPos * 13761));
							float temperature = biomegenbase.getFloatTemperature(xPos, lowY, zPos);

							if (worldclient.getWorldChunkManager().getTemperatureAtHeight(temperature, height) >= 0.15F) {
								if (pass_param != 0) {
									if (pass_param >= 0) {
										tessellator.draw();
									}

									pass_param = 0;
									mc.getTextureManager().bindTexture(locationRainPng);
									tessellator.startDrawingQuads();
								}

								float rate = ((this.rendererUpdateCount + xPos * xPos * 3121 + xPos * 45238971 + zPos * zPos * 418711 + zPos * 13761 & 31) + partialTicks) / 32.0F * (3.0F + this.random.nextFloat());
								double xOffset = (xPos + 0.5F) - entitylivingbase.posX;
								double zOffset = (zPos + 0.5F) - entitylivingbase.posZ;
								float distance = MathHelper.sqrt_double(xOffset * xOffset + zOffset * zOffset) / iteration_count;
								tessellator.setBrightness(worldclient.getLightBrightnessForSkyBlocks(xPos, i3, zPos, 0));
								tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, ((1.0F - distance * distance) * 0.5F + 0.5F) * rain_strength);
								tessellator.setTranslation(-smoothedX * 1.0D, -smoothedY * 1.0D, -smoothedZ * 1.0D);
								tessellator.addVertexWithUV((xPos - rainX) + 0.5D, lowY, (zPos - rainZ) + 0.5D, (0.0F * scale), (lowY * scale / 4.0F + rate * scale));
								tessellator.addVertexWithUV((xPos + rainX) + 0.5D, lowY, (zPos + rainZ) + 0.5D, (1.0F * scale), (lowY * scale / 4.0F + rate * scale));
								tessellator.addVertexWithUV((xPos + rainX) + 0.5D, higY, (zPos + rainZ) + 0.5D, (1.0F * scale), (higY * scale / 4.0F + rate * scale));
								tessellator.addVertexWithUV((xPos - rainX) + 0.5D, higY, (zPos - rainZ) + 0.5D, (0.0F * scale), (higY * scale / 4.0F + rate * scale));
								tessellator.setTranslation(0.0D, 0.0D, 0.0D);
							} else {
								if (pass_param != 1) {
									if (pass_param >= 0) {
										tessellator.draw();
									}

									pass_param = 1;
									mc.getTextureManager().bindTexture(locationSnowPng);
									tessellator.startDrawingQuads();
								}

								float rate = ((this.rendererUpdateCount & 511) + partialTicks) / 512.0F;
								float drift = this.random.nextFloat() + softTick * 0.01F * (float) this.random.nextGaussian();
								float variable_rate = this.random.nextFloat() + softTick * (float) this.random.nextGaussian() * 0.001F;
								double xOffset = (xPos + 0.5F) - entitylivingbase.posX;
								double zOffset = (zPos + 0.5F) - entitylivingbase.posZ;
								float distance = MathHelper.sqrt_double(xOffset * xOffset + zOffset * zOffset) / iteration_count;
								tessellator.setBrightness((worldclient.getLightBrightnessForSkyBlocks(xPos, i3, zPos, 0) * 3 + 15728880) / 4);
								tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, ((1.0F - distance * distance) * 0.3F + 0.5F) * rain_strength);
								tessellator.setTranslation(-smoothedX * 1.0D, -smoothedY * 1.0D, -smoothedZ * 1.0D);
								tessellator.addVertexWithUV((xPos - rainX) + 0.5D, lowY, (zPos - rainZ) + 0.5D, (0.0F * scale + drift), (lowY * scale / 4.0F + rate * scale + variable_rate));
								tessellator.addVertexWithUV((xPos + rainX) + 0.5D, lowY, (zPos + rainZ) + 0.5D, (1.0F * scale + drift), (lowY * scale / 4.0F + rate * scale + variable_rate));
								tessellator.addVertexWithUV((xPos + rainX) + 0.5D, higY, (zPos + rainZ) + 0.5D, (1.0F * scale + drift), (higY * scale / 4.0F + rate * scale + variable_rate));
								tessellator.addVertexWithUV((xPos - rainX) + 0.5D, higY, (zPos - rainZ) + 0.5D, (0.0F * scale + drift), (higY * scale / 4.0F + rate * scale + variable_rate));
								tessellator.setTranslation(0.0D, 0.0D, 0.0D);
							}
						}
					}
				}
			}

			if (pass_param >= 0) {
				tessellator.draw();
			}
			GL11.glPopMatrix();

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			mc.entityRenderer.disableLightmap(partialTicks);
		}
	}

	public void updateClouds() {
		++this.rendererUpdateCount;
	}
}
