package com.xcompwiz.util;

/**
 * Written by heldplayer as part of the DniColorRenderer
 * @author heldplayer
 */
public class MathBezier {

	private static float[]	sinTable	= new float[65536];

	static {
		for (int i = 0; i < 65536; ++i) {
			sinTable[i] = (float) Math.sin(i * Math.PI * 2.0D / 65536.0D);
		}
	}

	/**
	 * Returns the sin of an angle in Quaternary degrees
	 */
	public static final float sin(float angle) {
		return sinTable[(int) (angle * 16384.0F) & 65535];
	}

	/**
	 * Returns the sin of an angle in Quaternary degrees
	 */
	public static final float cos(float angle) {
		return sinTable[(int) (angle * 16384.0F + 16384.0F) & 65535];
	}

	/**
	 * Returns the max of the 2
	 */
	public static int max(int number1, int number2) {
		return number1 > number2 ? number1 : number2;
	}

	/**
	 * Returns the max of the 2
	 */
	public static float max(float number1, float number2) {
		return number1 > number2 ? number1 : number2;
	}

	/**
	 * Returns the min of the 2
	 */
	public static int min(int number1, int number2) {
		return number1 < number2 ? number1 : number2;
	}

	/**
	 * Returns the min of the 2
	 */
	public static float min(float number1, float number2) {
		return number1 < number2 ? number1 : number2;
	}

	/**
	 * Returns the absolute value
	 */
	public static int abs(int number) {
		return number < 0 ? -number : number;
	}

	/**
	 * Returns the absolute value
	 */
	public static float abs(float number) {
		return number < 0.0F ? -number : number;
	}

	/**
	 * Returns the absolute value
	 */
	public static double abs(double number) {
		return number < 0.0D ? -number : number;
	}

	public static float lerp(float origin, float target, int steps, int maxSteps) {
		return origin + (target - origin) * steps / maxSteps;
	}

	public static double lerp(double origin, double target, int steps, int maxSteps) {
		return origin + (target - origin) * steps / maxSteps;
	}

	/**
	 * Gets a point on a bezier curve
	 */
	public static Vector bezier(Vector[] input, double t) {
		if (input.length < 2) { throw new RuntimeException("Need more input points"); }
		double d = (1.0D - t);
		if (input.length == 2) {
			Vector result = VectorPool.getFreeVector(0, 0, 0);
			for (int i = 0; i < input.length; i++) {
				Vector vec = input[i].clone();
				if (i == 0) vec.multiply(t);
				if (i == 1) vec.multiply(d);
				result.add(vec);
			}
			return result;
		}
		if (input.length == 3) {
			Vector result = VectorPool.getFreeVector(0, 0, 0);
			for (int i = 0; i < input.length; i++) {
				Vector vec = input[i].clone();
				if (i == 0) vec.multiply(t * t);
				if (i == 1) vec.multiply(d * t * 2.0D);
				if (i == 2) vec.multiply(d * d);
				result.add(vec);
			}
			return result;
		}
		if (input.length == 4) {
			Vector result = VectorPool.getFreeVector(0, 0, 0);
			for (int i = 0; i < input.length; i++) {
				Vector vec = input[i].clone();
				if (i == 0) vec.multiply(t * t * t);
				if (i == 1) vec.multiply(d * t * t * 3.0D);
				if (i == 2) vec.multiply(d * d * t * 3.0D);
				if (i == 3) vec.multiply(d * d * d);
				result.add(vec);
			}
			return result;
		}

		throw new RuntimeException("Unknown bezier function");
	}
}
