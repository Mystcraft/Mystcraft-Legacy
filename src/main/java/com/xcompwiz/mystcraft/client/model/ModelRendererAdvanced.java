package com.xcompwiz.mystcraft.client.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;
import org.omg.CORBA.Object;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModelRendererAdvanced {
	/** The size of the texture file's width in pixels. */
	protected float						textureWidth;

	/** The size of the texture file's height in pixels. */
	protected float						textureHeight;

	/** The X offset into the texture used for displaying this model */
	protected int						textureOffsetX;

	/** The Y offset into the texture used for displaying this model */
	protected int						textureOffsetY;

	public float						rotationPointX;
	public float						rotationPointY;
	public float						rotationPointZ;
	public float						rotateAngleX;
	public float						rotateAngleY;
	public float						rotateAngleZ;
	private boolean						compiled;

	/** The GL display list rendered by the Tessellator for this model */
	private int							displayList;
	public boolean						mirror;
	public boolean						showModel;

	/** Hides the model. */
	public boolean						isHidden;
	public List<ModelElement>			elementList;
	public List<ModelRendererAdvanced>	childModels;
	public final String					modelName;
	protected ModelBase					baseModel;

	public ModelRendererAdvanced(ModelBase par1ModelBase, String par2Str) {
		this.textureWidth = 64.0F;
		this.textureHeight = 32.0F;
		this.compiled = false;
		this.displayList = 0;
		this.mirror = false;
		this.showModel = true;
		this.isHidden = false;
		this.elementList = new ArrayList<ModelElement>();
		this.baseModel = par1ModelBase;
		par1ModelBase.boxList.add((ModelRenderer)(Object)this); // How does that even work?!
		this.modelName = par2Str;
		this.setTextureSize(par1ModelBase.textureWidth, par1ModelBase.textureHeight);
	}

	public ModelRendererAdvanced(ModelBase par1ModelBase) {
		this(par1ModelBase, (String) null);
	}

	public ModelRendererAdvanced(ModelBase par1ModelBase, int par2, int par3) {
		this(par1ModelBase);
		this.setTextureOffset(par2, par3);
	}

	public void addChild(ModelRendererAdvanced par1ModelRenderer) {
		if (this.childModels == null) {
			this.childModels = new ArrayList<ModelRendererAdvanced>();
		}

		this.childModels.add(par1ModelRenderer);
	}

	public ModelRendererAdvanced setTextureOffset(int par1, int par2) {
		this.textureOffsetX = par1;
		this.textureOffsetY = par2;
		return this;
	}

	public ModelRendererAdvanced addElement(ModelElement element) {
		this.elementList.add(element);
		return this;
	}

	public ModelRendererAdvanced addBox(String name, float x, float y, float z, int width, int height, int depth) {
		name = this.modelName + "." + name;
		TextureOffset textureOffset = this.baseModel.getTextureOffset(name);
		this.elementList.add((new ModelBox(this, textureOffset.textureOffsetX, textureOffset.textureOffsetY, x, y, z, width, height, depth, 0.0F)).setName(name));
		return this;
	}

	public ModelRendererAdvanced addBox(float x, float y, float z, int width, int height, int depth) {
		this.elementList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F));
		return this;
	}

	/**
	 * Creates a textured box. Args: originX, originY, originZ, width, height, depth, scaleFactor.
	 */
	public void addBox(float x, float y, float z, int width, int height, int depth, float scalar) {
		this.elementList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, scalar));
	}

	public void setRotationPoint(float par1, float par2, float par3) {
		this.rotationPointX = par1;
		this.rotationPointY = par2;
		this.rotationPointZ = par3;
	}

	@SideOnly(Side.CLIENT)
	public void render(float par1) {
		if (!this.isHidden) {
			if (this.showModel) {
				if (!this.compiled) {
					this.compileDisplayList(par1);
				}

				Iterator<ModelRendererAdvanced> var2;
				ModelRendererAdvanced var3;

				if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
					if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
						GL11.glCallList(this.displayList);

						if (this.childModels != null) {
							var2 = this.childModels.iterator();

							while (var2.hasNext()) {
								var3 = var2.next();
								var3.render(par1);
							}
						}
					} else {
						GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
						GL11.glCallList(this.displayList);

						if (this.childModels != null) {
							var2 = this.childModels.iterator();

							while (var2.hasNext()) {
								var3 = var2.next();
								var3.render(par1);
							}
						}

						GL11.glTranslatef(-this.rotationPointX * par1, -this.rotationPointY * par1, -this.rotationPointZ * par1);
					}
				} else {
					GL11.glPushMatrix();
					GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);

					if (this.rotateAngleZ != 0.0F) {
						GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.rotateAngleY != 0.0F) {
						GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.rotateAngleX != 0.0F) {
						GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
					}

					GL11.glCallList(this.displayList);

					if (this.childModels != null) {
						var2 = this.childModels.iterator();

						while (var2.hasNext()) {
							var3 = var2.next();
							var3.render(par1);
						}
					}

					GL11.glPopMatrix();
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderWithRotation(float par1) {
		if (!this.isHidden) {
			if (this.showModel) {
				if (!this.compiled) {
					this.compileDisplayList(par1);
				}

				GL11.glPushMatrix();
				GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);

				if (this.rotateAngleY != 0.0F) {
					GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
				}

				if (this.rotateAngleX != 0.0F) {
					GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
				}

				if (this.rotateAngleZ != 0.0F) {
					GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
				}

				GL11.glCallList(this.displayList);
				GL11.glPopMatrix();
			}
		}
	}

	/**
	 * Allows the changing of Angles after a box has been rendered
	 */
	@SideOnly(Side.CLIENT)
	public void postRender(float par1) {
		if (!this.isHidden) {
			if (this.showModel) {
				if (!this.compiled) {
					this.compileDisplayList(par1);
				}

				if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
					if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
						GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
					}
				} else {
					GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);

					if (this.rotateAngleZ != 0.0F) {
						GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.rotateAngleY != 0.0F) {
						GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.rotateAngleX != 0.0F) {
						GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
					}
				}
			}
		}
	}

	/**
	 * Compiles a GL display list for this model
	 */
	@SideOnly(Side.CLIENT)
	private void compileDisplayList(float par1) {
		this.displayList = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(this.displayList, GL11.GL_COMPILE);
		Tessellator tesselator = Tessellator.instance;

		for (ModelElement element : this.elementList) {
			element.render(tesselator, par1);
		}

		GL11.glEndList();
		this.compiled = true;
	}

	/**
	 * Returns the model renderer with the new texture parameters.
	 */
	public ModelRendererAdvanced setTextureSize(int par1, int par2) {
		this.textureWidth = par1;
		this.textureHeight = par2;
		return this;
	}
}
