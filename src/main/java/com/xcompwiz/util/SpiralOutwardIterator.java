package com.xcompwiz.util;

public class SpiralOutwardIterator {
	private int layer = 1;
	private int leg = 0;
	public int x = 0;
	public int y = 0;

	public void step() {
		switch (leg) {
		case 0:
			++x;
			if (x == layer)
				++leg;
			break;
		case 1:
			++y;
			if (y == layer)
				++leg;
			break;
		case 2:
			--x;
			if (-x == layer)
				++leg;
			break;
		case 3:
			--y;
			if (-y == layer) {
				leg = 0;
				++layer;
			}
			break;
		}
	}
}
