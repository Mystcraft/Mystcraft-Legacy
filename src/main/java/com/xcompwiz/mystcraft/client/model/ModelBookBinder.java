package com.xcompwiz.mystcraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBookBinder extends ModelBase {
	//fields
	ModelRenderer right;
	ModelRenderer base;
	ModelRenderer bar1;
	ModelRenderer brace2;
	ModelRenderer bar2;
	ModelRenderer book;
	ModelRenderer left;
	ModelRenderer brace1;

	public ModelBookBinder() {
		textureWidth = 128;
		textureHeight = 64;

		right = new ModelRenderer(this, 64, 0);
		right.addBox(0F, 0F, 0F, 3, 4, 14);
		right.setRotationPoint(5F, 10F, -7F);
		right.setTextureSize(128, 64);
		right.mirror = false;
		setRotation(right, 0F, 0F, 0F);
		base = new ModelRenderer(this, 0, 0);
		base.addBox(0F, 0F, 0F, 16, 10, 16);
		base.setRotationPoint(-8F, 14F, -8F);
		base.setTextureSize(128, 64);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		bar1 = new ModelRenderer(this, 84, 22);
		bar1.addBox(0F, 0F, 0F, 14, 1, 1);
		bar1.setRotationPoint(-7F, 11.5F, 4F);
		bar1.setTextureSize(128, 64);
		bar1.mirror = true;
		setRotation(bar1, 0F, 0F, 0F);
		brace2 = new ModelRenderer(this, 84, 4);
		brace2.addBox(0F, 0F, 0F, 1, 4, 14);
		brace2.setRotationPoint(1F, 10F, -7F);
		brace2.setTextureSize(128, 64);
		brace2.mirror = false;
		setRotation(brace2, 0F, 0F, 0F);
		bar2 = new ModelRenderer(this, 84, 22);
		bar2.addBox(0F, 0F, 0F, 14, 1, 1);
		bar2.setRotationPoint(-7F, 11.5F, -5F);
		bar2.setTextureSize(128, 64);
		bar2.mirror = true;
		setRotation(bar2, 0F, 0F, 0F);
		book = new ModelRenderer(this, 100, 0);
		book.addBox(0F, 0F, 0F, 2, 5, 7);
		book.setRotationPoint(-1F, 9F, -3.5F);
		book.setTextureSize(128, 64);
		book.mirror = true;
		setRotation(book, 0F, 0F, 0F);
		left = new ModelRenderer(this, 64, 0);
		left.addBox(0F, 0F, 0F, 3, 4, 14);
		left.setRotationPoint(-8F, 10F, -7F);
		left.setTextureSize(128, 64);
		left.mirror = true;
		setRotation(left, 0F, 0F, 0F);
		brace1 = new ModelRenderer(this, 84, 4);
		brace1.addBox(0F, 0F, 0F, 1, 4, 14);
		brace1.setRotationPoint(-2F, 10F, -7F);
		brace1.setTextureSize(128, 64);
		brace1.mirror = true;
		setRotation(brace1, 0F, 0F, 0F);
	}

	@Override
	public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		right.render(f5);
		base.render(f5);
		bar1.render(f5);
		brace2.render(f5);
		bar2.render(f5);
		book.render(f5);
		left.render(f5);
		brace1.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
