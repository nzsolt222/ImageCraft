package com.imagecraft.color;

import java.awt.Color;

public class Rgba extends Color {

	public Rgba(int rgb) {
		super(rgb);
	}

	public Rgba(int rgba, boolean hasalpha) {
		super(rgba, hasalpha);
	}

	public Rgba(int r, int g, int b) {
		super(r, g, b);
	}

	public Rgba(int r, int g, int b, int a) {
		super(r, g, b, a);
	}

	public Xyz toXyz() {
		double r0 = getRed() / 255d;
		double g0 = getGreen() / 255d;
		double b0 = getBlue() / 255d;

		r0 = convert(r0);
		g0 = convert(g0);
		b0 = convert(b0);

		double X = r0 * 0.4124 + g0 * 0.3576 + b0 * 0.1805;
		double Y = r0 * 0.2126 + g0 * 0.7152 + b0 * 0.0722;
		double Z = r0 * 0.0193 + g0 * 0.1192 + b0 * 0.9505;

		return new Xyz(X, Y, Z);
	}

	private double convert(double color) {
		if (color > 0.04045d) {
			color = Math.pow((color + 0.055d) / 1.055d, 2.4d);
		} else {
			color = color / 12.92d;
		}
		return color * 100d;
	}

	public CieLab toCieLab() {
		return toXyz().toCieLab();
	}

}
