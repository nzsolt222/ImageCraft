package com.imagecraft.image;

import net.minecraft.block.state.IBlockState;

import com.imagecraft.color.CieLab;
import com.imagecraft.color.Rgba;

public class ColorAndBlockState {
	private Rgba color;
	private CieLab cieColor;
	private IBlockState state;

	public ColorAndBlockState(Rgba color, IBlockState state) {
		super();

		this.color = color;
		this.state = state;
		this.cieColor = color.toCieLab();
	}

	public Rgba getRgbaColor() {
		return color;
	}

	public CieLab getCieLabColor() {
		return cieColor;
	}

	public IBlockState getState() {
		return state;
	}
}
