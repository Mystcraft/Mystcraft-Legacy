package com.xcompwiz.mystcraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWritingDesk extends ModelBase {
	// fields
	ModelRenderer			bottomShelf;
	ModelRenderer			middleShelf;
	ModelRenderer			deskTop;
	ModelRenderer			deskMiddle;
	ModelRenderer			deskLeft;
	ModelRenderer			deskRight;
	ModelRenderer			deskBack;
	ModelRenderer			deskMiddleBottom;

	ModelRenderer			deskTopBack;
	ModelRenderer			deskTopLeft;
	ModelRenderer			deskTopRight;
	ModelRenderer			deskTopTop;
	ModelRenderer			angleLeft;
	ModelRenderer			angleRight;
	ModelRenderer			cupboardLeft;
	ModelRenderer			cupboardRight;

	ModelRenderer			lampRod;
	ModelRenderer			lamp1;
	ModelRenderer			lamp2;

	ModelRenderer			book1;
	ModelRenderer			book2;
	ModelRenderer			book3;
	ModelRenderer			book4;
	ModelRenderer			book5;
	ModelRenderer			book6;
	ModelRenderer			book7;
	ModelRenderer			book8;
	ModelRenderer			book9;
	ModelRenderer			book10;
	ModelRenderer			book11;
	ModelRenderer			book12;
	ModelRenderer			book13;
	ModelRenderer			book14;
	ModelRenderer			book15;
	ModelRenderer			book16;

	ModelRenderer			paper1;
	ModelRenderer			paper2;
	ModelRenderer			paper3;
	ModelRenderer			paperStack1;
	ModelRenderer			paperStack2;

	private ModelRenderer[]	books	= new ModelRenderer[16];

	public ModelWritingDesk() {
		textureWidth = 256;
		textureHeight = 128;

		bottomShelf = new ModelRenderer(this, 0, 34);
		bottomShelf.addBox(0F, 0F, 0F, 14, 1, 15);
		bottomShelf.setRotationPoint(-7F, 23F, -8F);
		bottomShelf.setTextureSize(256, 128);
		bottomShelf.mirror = true;
		setRotation(bottomShelf, 0F, 0F, 0F);
		middleShelf = new ModelRenderer(this, 0, 17);
		middleShelf.addBox(0F, 0F, 0F, 30, 2, 15);
		middleShelf.setRotationPoint(-7F, 15F, -8F);
		middleShelf.setTextureSize(256, 128);
		middleShelf.mirror = true;
		setRotation(middleShelf, 0F, 0F, 0F);
		deskTop = new ModelRenderer(this, 0, 0);
		deskTop.addBox(0F, 0F, 0F, 32, 1, 16);
		deskTop.setRotationPoint(-8F, 8F, -8F);
		deskTop.setTextureSize(256, 128);
		deskTop.mirror = true;
		setRotation(deskTop, 0F, 0F, 0F);
		deskMiddle = new ModelRenderer(this, 94, 36);
		deskMiddle.addBox(0F, 0F, 0F, 2, 6, 15);
		deskMiddle.setRotationPoint(7F, 9F, -8F);
		deskMiddle.setTextureSize(256, 128);
		deskMiddle.mirror = true;
		setRotation(deskMiddle, 0F, 0F, 0F);
		deskLeft = new ModelRenderer(this, 90, 1);
		deskLeft.addBox(0F, 0F, 0F, 1, 15, 16);
		deskLeft.setRotationPoint(-8F, 9F, -8F);
		deskLeft.setTextureSize(256, 128);
		deskLeft.mirror = true;
		setRotation(deskLeft, 0F, 0F, 0F);
		deskRight = new ModelRenderer(this, 90, 1);
		deskRight.addBox(0F, 0F, 0F, 1, 15, 16);
		deskRight.setRotationPoint(23F, 9F, -8F);
		deskRight.setTextureSize(256, 128);
		deskRight.mirror = false;
		setRotation(deskRight, 0F, 0F, 0F);
		deskBack = new ModelRenderer(this, 128, 0);
		deskBack.addBox(0F, 0F, 0F, 30, 15, 1);
		deskBack.setRotationPoint(-7F, 9F, 7F);
		deskBack.setTextureSize(256, 128);
		deskBack.mirror = true;
		setRotation(deskBack, 0F, 0F, 0F);
		deskMiddleBottom = new ModelRenderer(this, 77, 42);
		deskMiddleBottom.addBox(0F, 0F, 0F, 1, 7, 15);
		deskMiddleBottom.setRotationPoint(7F, 17F, -8F);
		deskMiddleBottom.setTextureSize(256, 128);
		deskMiddleBottom.mirror = true;
		setRotation(deskMiddleBottom, 0F, 0F, 0F);

		book1 = new ModelRenderer(this, 0, 17);
		book1.addBox(-1F, -6F, 0F, 1, 6, 4);
		book1.setRotationPoint(-4.5F, 23F, -7.3F);
		book1.setTextureSize(256, 128);
		book1.mirror = true;
		setRotation(book1, 0F, 0F, 0.4886922F);
		book2 = new ModelRenderer(this, 20, 50);
		book2.addBox(0F, 0F, 0F, 2, 6, 4);
		book2.setRotationPoint(-1.8F, 17.9F, -6.9F);
		book2.setTextureSize(256, 128);
		book2.mirror = true;
		setRotation(book2, 0F, 0F, 0F);
		book3 = new ModelRenderer(this, 32, 50);
		book3.addBox(0F, 0F, 0F, 1, 6, 4);
		book3.setRotationPoint(-0.2F, 17.5F, -7.3F);
		book3.setTextureSize(256, 128);
		book3.mirror = true;
		setRotation(book3, 0F, 0F, 0F);
		book4 = new ModelRenderer(this, 42, 50);
		book4.addBox(0F, 0F, 0F, 1, 6, 4);
		book4.setRotationPoint(1F, 17.8F, -7.3F);
		book4.setTextureSize(256, 128);
		book4.mirror = true;
		setRotation(book4, 0F, 0F, 0F);
		book5 = new ModelRenderer(this, 0, 17);
		book5.addBox(0F, 0F, 0F, 1, 6, 4);
		book5.setRotationPoint(2.2F, 17.5F, -7.5F);
		book5.setTextureSize(256, 128);
		book5.mirror = true;
		setRotation(book5, 0F, 0F, 0F);
		book6 = new ModelRenderer(this, 0, 50);
		book6.addBox(0F, 0F, 0F, 1, 6, 4);
		book6.setRotationPoint(3.3F, 17.1F, -7.3F);
		book6.setTextureSize(256, 128);
		book6.mirror = true;
		setRotation(book6, 0F, 0F, 0F);
		book7 = new ModelRenderer(this, 0, 34);
		book7.addBox(0F, 0F, 0F, 2, 6, 4);
		book7.setRotationPoint(3.7F, 17.9F, -6.9F);
		book7.setTextureSize(256, 128);
		book7.mirror = true;
		setRotation(book7, 0F, 0F, 0F);
		book8 = new ModelRenderer(this, 10, 50);
		book8.addBox(0F, 0F, 0F, 1, 6, 4);
		book8.setRotationPoint(5.8F, 17.5F, -7.3F);
		book8.setTextureSize(256, 128);
		book8.mirror = true;
		setRotation(book8, 0F, 0F, 0F);
		book9 = new ModelRenderer(this, 0, 50);
		book9.addBox(0F, 0F, 0F, 1, 6, 4);
		book9.setRotationPoint(-6.8F, 9.5F, -7.5F);
		book9.setTextureSize(256, 128);
		book9.mirror = true;
		setRotation(book9, 0F, 0F, 0F);
		book10 = new ModelRenderer(this, 10, 50);
		book10.addBox(0F, 0F, 0F, 1, 6, 4);
		book10.setRotationPoint(-5.7F, 9.1F, -7.3F);
		book10.setTextureSize(256, 128);
		book10.mirror = true;
		setRotation(book10, 0F, 0F, 0F);
		book11 = new ModelRenderer(this, 0, 34);
		book11.addBox(0F, 0F, 0F, 2, 6, 4);
		book11.setRotationPoint(-5.3F, 9.9F, -6.9F);
		book11.setTextureSize(256, 128);
		book11.mirror = true;
		setRotation(book11, 0F, 0F, 0F);
		book12 = new ModelRenderer(this, 32, 50);
		book12.addBox(0F, 0F, 0F, 1, 6, 4);
		book12.setRotationPoint(-3.2F, 9.5F, -7.3F);
		book12.setTextureSize(256, 128);
		book12.mirror = true;
		setRotation(book12, 0F, 0F, 0F);
		book13 = new ModelRenderer(this, 43, 34);
		book13.addBox(0F, -5F, 0F, 1, 5, 4);
		book13.setRotationPoint(-0.5F, 15F, -7.5F);
		book13.setTextureSize(256, 128);
		book13.mirror = true;
		setRotation(book13, 0F, 0F, -0.3490659F);
		book14 = new ModelRenderer(this, 0, 17);
		book14.addBox(0F, 0F, 0F, 1, 6, 4);
		book14.setRotationPoint(0.5F, 9.1F, -7.3F);
		book14.setTextureSize(256, 128);
		book14.mirror = true;
		setRotation(book14, 0F, 0F, 0F);
		book15 = new ModelRenderer(this, 20, 50);
		book15.addBox(0F, 0F, 0F, 2, 6, 4);
		book15.setRotationPoint(0.9F, 9.9F, -6.9F);
		book15.setTextureSize(256, 128);
		book15.mirror = true;
		setRotation(book15, 0F, 0F, 0F);
		book16 = new ModelRenderer(this, 42, 50);
		book16.addBox(0F, 0F, 0F, 1, 6, 4);
		book16.setRotationPoint(3F, 9.5F, -7.3F);
		book16.setTextureSize(256, 128);
		book16.mirror = true;
		setRotation(book16, 0F, 0F, 0F);

		paper1 = new ModelRenderer(this, 0, 60);
		paper1.addBox(-2F, 0F, 0F, 7, 0, 5);
		paper1.setRotationPoint(16.5F, 14.8F, -6.3F);
		paper1.setTextureSize(256, 128);
		paper1.mirror = true;
		setRotation(paper1, 0F, -0.1047198F, 0F);
		paper2 = new ModelRenderer(this, 0, 60);
		paper2.addBox(-2F, 0F, 0F, 7, 0, 5);
		paper2.setRotationPoint(16.5F, 14.8F, -4.9F);
		paper2.setTextureSize(256, 128);
		paper2.mirror = true;
		setRotation(paper2, 0F, 0.2094395F, 0F);
		paper3 = new ModelRenderer(this, 0, 60);
		paper3.addBox(-2F, 0F, 0F, 7, 0, 5);
		paper3.setRotationPoint(16.5F, 14.8F, -5.9F);
		paper3.setTextureSize(256, 128);
		paper3.mirror = true;
		setRotation(paper3, 0F, 0F, 0F);
		paperStack1 = new ModelRenderer(this, 0, 60);
		paperStack1.addBox(-2F, 0F, 0F, 7, 1, 5);
		paperStack1.setRotationPoint(16.5F, 13F, -5F);
		paperStack1.setTextureSize(256, 128);
		paperStack1.mirror = true;
		setRotation(paperStack1, 0F, -0.0174533F, 0F);
		paperStack2 = new ModelRenderer(this, 0, 60);
		paperStack2.addBox(0F, 0F, 0F, 7, 1, 5);
		paperStack2.setRotationPoint(14.5F, 14F, -5F);
		paperStack2.setTextureSize(256, 128);
		paperStack2.mirror = true;
		setRotation(paperStack2, 0F, 0.0698132F, 0F);

		deskTopBack = new ModelRenderer(this, 128, 16);
		deskTopBack.addBox(0F, 0F, 0F, 32, 12, 1);
		deskTopBack.setRotationPoint(-8F, -4F, 7F);
		deskTopBack.setTextureSize(256, 128);
		deskTopBack.mirror = true;
		setRotation(deskTopBack, 0F, 0F, 0F);
		deskTopTop = new ModelRenderer(this, 128, 29);
		deskTopTop.addBox(0F, 0F, 0F, 30, 1, 6);
		deskTopTop.setRotationPoint(-7F, -4F, 1F);
		deskTopTop.setTextureSize(256, 128);
		deskTopTop.mirror = true;
		setRotation(deskTopTop, 0F, 0F, 0F);
		deskTopLeft = new ModelRenderer(this, 146, 40);
		deskTopLeft.addBox(0F, 0F, 0F, 1, 12, 6);
		deskTopLeft.setRotationPoint(-8F, -4F, 1F);
		deskTopLeft.setTextureSize(256, 128);
		deskTopLeft.mirror = true;
		setRotation(deskTopLeft, 0F, 0F, 0F);
		deskTopRight = new ModelRenderer(this, 146, 40);
		deskTopRight.addBox(0F, 0F, 0F, 1, 12, 6);
		deskTopRight.setRotationPoint(23F, -4F, 1F);
		deskTopRight.setTextureSize(256, 128);
		deskTopRight.mirror = true;
		setRotation(deskTopRight, 0F, 0F, 0F);
		angleLeft = new ModelRenderer(this, 128, 40);
		angleLeft.addBox(0F, 0F, 0F, 1, 15, 8);
		angleLeft.setRotationPoint(-7.99F, -4F, 1F);
		angleLeft.setTextureSize(256, 128);
		angleLeft.mirror = true;
		setRotation(angleLeft, -0.6457718F, 0F, 0F);
		angleRight = new ModelRenderer(this, 128, 40);
		angleRight.addBox(0F, 0F, 0F, 1, 15, 8);
		angleRight.setRotationPoint(22.99F, -4F, 1F);
		angleRight.setTextureSize(256, 128);
		angleRight.mirror = false;
		setRotation(angleRight, -0.6457718F, 0F, 0F);
		cupboardRight = new ModelRenderer(this, 182, 40);
		cupboardRight.addBox(0F, 0F, 0F, 7, 11, 4);
		cupboardRight.setRotationPoint(16F, -3F, 3F);
		cupboardRight.setTextureSize(256, 128);
		cupboardRight.mirror = true;
		setRotation(cupboardRight, 0F, 0F, 0F);
		cupboardLeft = new ModelRenderer(this, 160, 40);
		cupboardLeft.addBox(0F, 0F, 0F, 7, 11, 4);
		cupboardLeft.setRotationPoint(-7F, -3F, 3F);
		cupboardLeft.setTextureSize(256, 128);
		cupboardLeft.mirror = true;
		setRotation(cupboardLeft, 0F, 0F, 0F);

		lampRod = new ModelRenderer(this, 194, 16);
		lampRod.addBox(0F, 0F, 0F, 1, 4, 1);
		lampRod.setRotationPoint(7.5F, -3.9F, 5F);
		lampRod.setTextureSize(256, 128);
		lampRod.mirror = true;
		setRotation(lampRod, -1.134464F, 0F, 0F);
		lamp1 = new ModelRenderer(this, 198, 16);
		lamp1.addBox(0F, 0F, -2F, 5, 2, 2);
		lamp1.setRotationPoint(5.5F, -2.3F, 2.5F);
		lamp1.setTextureSize(256, 128);
		lamp1.mirror = true;
		setRotation(lamp1, -0.0523599F, 0F, 0F);
		lamp2 = new ModelRenderer(this, 198, 16);
		lamp2.addBox(0F, 0F, 0F, 5, 2, 2);
		lamp2.setRotationPoint(5.5F, -2.3F, 2.5F);
		lamp2.setTextureSize(256, 128);
		lamp2.mirror = true;
		setRotation(lamp2, -0.2094395F, 0F, 0F);

		int i = 0;
		books[i++] = book9;
		books[i++] = book10;
		books[i++] = book11;
		books[i++] = book12;
		books[i++] = book13;
		books[i++] = book14;
		books[i++] = book15;
		books[i++] = book16;

		books[i++] = book8;
		books[i++] = book7;
		books[i++] = book6;
		books[i++] = book5;
		books[i++] = book4;
		books[i++] = book3;
		books[i++] = book2;
		books[i++] = book1;
	}

	public void render(Entity entity, float scale, boolean backing, int bookcount, int papercount) {
		bottomShelf.render(scale);
		middleShelf.render(scale);
		deskTop.render(scale);
		deskMiddle.render(scale);
		deskLeft.render(scale);
		deskRight.render(scale);
		deskBack.render(scale);
		deskMiddleBottom.render(scale);

		if (backing) {
			deskTopBack.render(scale);
			deskTopLeft.render(scale);
			deskTopRight.render(scale);
			deskTopTop.render(scale);
			angleLeft.render(scale);
			angleRight.render(scale);
			cupboardLeft.render(scale);
			cupboardRight.render(scale);

			// lampRod.render(scale);
			// lamp1.render(scale);
			// lamp2.render(scale);
		}

		for (int i = 0; i < bookcount && i < books.length; ++i) {
			books[i].render(scale);
		}

		if (papercount > 0) paper2.render(scale);
		if (papercount > 1) paper3.render(scale);
		if (papercount > 2) paper1.render(scale);
		if (papercount > 27) paperStack2.render(scale);
		if (papercount > 47) paperStack1.render(scale);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.render(entity, f5, false, 0, 0);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
