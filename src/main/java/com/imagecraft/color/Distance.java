package com.imagecraft.color;

import com.google.common.annotations.Beta;

public class Distance {

	public static double cie76(Rgba color1, Rgba color2) {
		return cie76(color1.toCieLab(), color2.toCieLab());
	}

	public static double cie76(CieLab cieLab, CieLab cieLab2) {

		double diff = distance(cieLab.getL(), cieLab2.getL())
				+ distance(cieLab.getA(), cieLab2.getA())
				+ distance(cieLab.getB(), cieLab2.getB());

		return Math.sqrt(diff);
	}

	private static double distance(double a, double b) {
		return (a - b) * (a - b);
	}

	public static double cie94(Rgba color1, Rgba color2) {
		return cie94(color1.toCieLab(), color2.toCieLab());
	}

	public static double cie94(CieLab cieLab, CieLab cieLab2) {
		CieLab a = cieLab;
		CieLab b = cieLab2;

		double delta_l = Math.abs(a.getL() - b.getL());
		double delta_a = Math.abs(a.getA() - b.getA());
		double delta_b = Math.abs(a.getB() - b.getB());

		double c1 = Math.sqrt(Math.pow(a.getA(), 2) + Math.pow(a.getB(), 2));
		double c2 = Math.sqrt(Math.pow(b.getA(), 2) + Math.pow(b.getB(), 2));
		double delta_c = c1 - c2;

		double delta_h = Math.pow(delta_a, 2) + Math.pow(delta_b, 2)
				- Math.pow(delta_c, 2);
		delta_h = ((delta_h < 0) ? 0 : Math.sqrt(delta_h));

		final double sl = 1.0d;
		final double kc = 1.0d;
		final double kh = 1.0d;

		// final double kL = 1.0;
		// final double k1 = 0.045;
		// final double k2 = 0.015;

		double kL = 2.0d;
		double k1 = 0.048d;
		double k2 = 0.014d;

		double sc = 1.0d + k1 * c1;
		double sh = 1.0d + k2 * c1;

		double i = Math.pow(delta_l / (kL * sl), 2)
				+ Math.pow(delta_c / (kc * sc), 2)
				+ Math.pow(delta_h / (kh * sh), 2);

		double final_result = ((i < 0) ? 0 : Math.sqrt(i));
		return final_result;
	}

	@Beta
	public static double ciede2000(Rgba color1, Rgba color2) {
		return ciede2000(color1.toCieLab(), color2.toCieLab());
	}

	@Beta
	public static double ciede2000(CieLab cieLab, CieLab cieLab2) {
		// https://github.com/THEjoezack/ColorMine/blob/master/ColorMine/ColorSpaces/Comparisons/CieDe2000Comparison.cs
		double k_L = 1.0;
		double k_C = 1.0;
		double k_H = 1.0;

		CieLab lab1 = cieLab2;
		CieLab lab2 = cieLab;

		double c_star_1_ab = Math.sqrt(lab1.getA() * lab1.getA() + lab1.getB()
				* lab1.getB());
		double c_star_2_ab = Math.sqrt(lab2.getA() * lab2.getA() + lab2.getB()
				* lab2.getB());

		double c_star_avrage_ab = (c_star_1_ab + c_star_2_ab) / 2.;

		double c_star_avrage_ab_pot7 = c_star_avrage_ab * c_star_avrage_ab
				* c_star_avrage_ab;

		c_star_avrage_ab_pot7 *= c_star_avrage_ab_pot7 * c_star_avrage_ab;

		double G = 0.5 * (1 - Math.sqrt(c_star_avrage_ab_pot7
				/ (c_star_avrage_ab_pot7 + 6103515625d)));

		double a1_prime = (1 + G) * lab1.getA();
		double a2_prime = (1 + G) * lab2.getA();

		double C_prime_1 = Math.sqrt(a1_prime * a1_prime + lab1.getB()
				* lab1.getB());
		double C_prime_2 = Math.sqrt(a2_prime * a2_prime + lab2.getB()
				* lab2.getB());

		double pi = Math.PI;
		double h_prime_1 = ((Math.atan2(lab1.getB(), a1_prime) * 180. / pi) + 360d) % 360d;

		double h_prime_2 = ((Math.atan2(lab2.getB(), a2_prime) * 180. / pi) + 360d) % 360d;

		double delta_L_prime = lab2.getL() - lab1.getL();
		double delta_C_prime = C_prime_2 - C_prime_1;

		double h_bar = Math.abs(h_prime_1 - h_prime_2);
		double delta_h_prime;

		if (C_prime_1 * C_prime_2 == 0) {
			delta_h_prime = 0;
		} else {
			if (h_bar <= 180.) {
				delta_h_prime = h_prime_2 - h_prime_1;
			} else if (h_bar > 180. && h_prime_2 <= h_prime_1) {
				delta_h_prime = h_prime_2 - h_prime_1 + 360.0;
			} else {
				delta_h_prime = h_prime_2 - h_prime_1 - 360.0;
			}
		}

		double delta_H_prime = 2 * Math.sqrt(C_prime_1 * C_prime_2)
				* Math.sqrt(delta_h_prime * pi / 360.);

		double L_prime_avrage = (lab1.getL() + lab2.getL()) / 2.;
		double C_prime_avrage = (C_prime_1 + C_prime_2) / 2.;

		double h_prime_avrage;
		if (C_prime_1 * C_prime_2 == 0) {
			h_prime_avrage = 0;
		} else {
			if (h_bar <= 180.) {
				h_prime_avrage = (h_prime_1 + h_prime_2) / 2.;
			} else if (h_bar > 180. && (h_prime_1 + h_prime_2) < 360.) {
				h_prime_avrage = (h_prime_1 + h_prime_2 + 360.) / 2.;
			} else {
				h_prime_avrage = (h_prime_1 + h_prime_2 - 360.) / 2;
			}
		}

		double L_prime_avrage_minus_50_square = (L_prime_avrage - 50);
		L_prime_avrage_minus_50_square *= L_prime_avrage_minus_50_square;

		double S_L = 1. + ((0.015 * L_prime_avrage_minus_50_square) / Math
				.sqrt(20 + L_prime_avrage_minus_50_square));

		double S_C = 1 + 0.045 * C_prime_avrage;

		double T = 1 - 0.17 * Math.cos(Math.toRadians(h_prime_avrage - 30))
				+ 0.24 * Math.cos(Math.toRadians(h_prime_avrage * 2)) + 0.32
				* Math.cos(Math.toRadians(h_prime_avrage * 3 + 6)) - 0.2
				* Math.cos(Math.toRadians(h_prime_avrage * 4 - 63));

		double S_H = 1 + 0.015 * T * C_prime_avrage;
		double h_prime_avrage_minus_275_div_25_square = (h_prime_avrage - 275) / (25.);
		h_prime_avrage_minus_275_div_25_square *= h_prime_avrage_minus_275_div_25_square;

		double delta_theta = 30 * Math
				.exp(-h_prime_avrage_minus_275_div_25_square);

		double C_prime_avrage_pot_7 = C_prime_avrage * C_prime_avrage
				* C_prime_avrage;
		C_prime_avrage_pot_7 *= C_prime_avrage_pot_7;
		double R_C = 2 * Math.sqrt(C_prime_avrage_pot_7
				/ (C_prime_avrage_pot_7 + 6103515625d));
		double R_T = -Math.sin(Math.toRadians(2 * delta_theta)) * R_C;

		double delta_L_prime_div_k_L_S_L = delta_L_prime / (S_L * k_L);
		double delta_C_prime_div_k_C_S_C = delta_C_prime / (S_C * k_C);
		double delta_H_prime_div_k_H_S_H = delta_H_prime / (S_H * k_H);

		double CIEDE2000 = Math.sqrt(delta_L_prime_div_k_L_S_L
				* delta_L_prime_div_k_L_S_L + delta_C_prime_div_k_C_S_C
				* delta_C_prime_div_k_C_S_C + delta_H_prime_div_k_H_S_H
				* delta_H_prime_div_k_H_S_H + R_T * delta_C_prime_div_k_C_S_C
				* delta_H_prime_div_k_H_S_H);

		return CIEDE2000;
	}
}
