package com.xcompwiz.mystcraft.client.model;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelLinkModifier extends ModelBase {
	Collection<ModelRenderer>	components	= new ArrayList<ModelRenderer>();

	public ModelLinkModifier() {
		textureWidth = 128;
		textureHeight = 64;

		ModelRenderer component = null;
		//Base
		component = addComponent(0, 0);
		component.addBox(0F, -1F, 0F, 16, 12, 16);
		component.setRotationPoint(-8F, 13F, -8F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0F, 0F, 0F);

		//????
		component = addComponent(0, 30);
		component.addBox(0F, -1F, 0F, 14, 2, 14);
		component.setRotationPoint(-7F, 15F, -7F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0F, 0F, 0F);

		//Book
		component = addComponent(64, 10);
		component.addBox(2F, -5F, -4.5F, 8, 4, 2);
		component.setRotationPoint(-6F, 12F, 5F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0.1570796F, 0F, 0F);

		//Button
		component = addComponent(0, 0);
		component.addBox(0F, -3F, -3F, 2, 2, 2);
		component.setRotationPoint(4.440892E-16F, 14F, -2F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0.0349066F, 0F, 0F);

		//Button
		component = addComponent(0, 0);
		component.addBox(0F, -3F, -3F, 2, 2, 2);
		component.setRotationPoint(2.2F, 14F, -2F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0.0349066F, 0F, 0F);

		//Button
		component = addComponent(0, 0);
		component.addBox(0F, -3F, -3F, 2, 2, 2);
		component.setRotationPoint(-6.6F, 14F, -2F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0.0349066F, 0F, 0F);

		//Button
		component = addComponent(0, 0);
		component.addBox(0F, -3F, -3F, 2, 2, 2);
		component.setRotationPoint(-4.4F, 14F, -2F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0.0349066F, 0F, 0F);

		//Button
		component = addComponent(0, 0);
		component.addBox(0F, -3F, -3F, 2, 2, 2);
		component.setRotationPoint(-2.2F, 14F, -2F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0.0349066F, 0F, 0F);

		//Button
		component = addComponent(0, 0);
		component.addBox(0F, -3F, -3F, 2, 2, 2);
		component.setRotationPoint(4.4F, 14F, -2F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0.0349066F, 0F, 0F);

		//The Crystal Thingy
		component = addComponent(64, 0);
		component.addBox(0F, -2F, -7F, 12, 3, 7);
		component.setRotationPoint(-6F, 12F, 5F);
		component.setTextureSize(128, 64);
		component.mirror = true;
		setRotation(component, 0.1570796F, 0F, 0F);
	}

	private ModelRenderer addComponent(int i, int j) {
		ModelRenderer component = new ModelRenderer(this, i, j);
		components.add(component);
		return component;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		for (ModelRenderer component : components) {
			component.render(f5);
		}
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
