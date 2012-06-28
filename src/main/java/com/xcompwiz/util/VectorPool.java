package com.xcompwiz.util;

import java.util.LinkedList;

/**
 * Written by heldplayer as part of the DniColorRenderer
 * 
 * @author heldplayer
 * 
 */
public final class VectorPool {

	private static LinkedList<Vector>	usedVectors		= new LinkedList<Vector>();
	private static LinkedList<Vector>	unusedVectors	= new LinkedList<Vector>();

	public static Vector getFreeVector() {
		if (unusedVectors.size() == 0) {
			Vector vector = new Vector(0, 0, 0);
			usedVectors.add(vector);
			return vector;
		}
		Vector vector = unusedVectors.remove(0);
		usedVectors.add(vector);
		return vector;
	}

	public static Vector getFreeVector(double x, double y, double z) {
		if (unusedVectors.size() == 0) {
			Vector vector = new Vector(x, y, z);
			usedVectors.add(vector);
			return vector;
		}
		Vector vector = unusedVectors.remove(0);
		vector.posX = x;
		vector.posY = y;
		vector.posZ = z;
		usedVectors.add(vector);
		return vector;
	}

	public static void unuseVectors() {
		unusedVectors.clear();
		unusedVectors.addAll(usedVectors);
		usedVectors.clear();
	}
}
