package com.imagecraft.color;


public class Xyz {
	private double x;
	private double y;
	private double z;

	public Xyz(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public CieLab toCieLab() {
		double xx = x / 95.007d;
		double yy = y / 100.000d;
		double zz = z / 108.883d;

		xx = convert(xx);
		yy = convert(yy);
		zz = convert(zz);

		double cie_l = (116 * yy) - 16;
		double cie_a = 500 * (xx - yy);
		double cie_b = 200 * (yy - zz);

		return new CieLab(cie_l, cie_a, cie_b);
	}

	private double convert(double color) {
		if (color > 0.008856d) {
			color = Math.pow(color, 1d / 3d);
		} else {
			color = (7.787 * color) + (16d / 116d);
		}
		return color;
	}
}
