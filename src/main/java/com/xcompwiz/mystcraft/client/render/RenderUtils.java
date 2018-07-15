package com.xcompwiz.mystcraft.client.render;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderUtils {

	@SideOnly(Side.CLIENT)
	public static BufferedImage scale(BufferedImage in, double scale, int scaleOperation) {
		int w = in.getWidth();
		int h = in.getHeight();
		//Take faith that the user knows to only multiply with stuff that results in well defined whole integer numbers
		BufferedImage after = new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		AffineTransformOp scaleOp = new AffineTransformOp(at, scaleOperation);
		return scaleOp.filter(in, after);
	}

}
