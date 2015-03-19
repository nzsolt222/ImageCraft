package com.imagecraft.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.imagecraft.color.Rgba;

public class ColorBlock extends Block {

	private String name;
	private Rgba color;

	public ColorBlock(Rgba color) {
		super(Material.rock);
		this.color = color;
		name = color.getRed() + "_" + color.getGreen() + "_" + color.getBlue();

		setUnlocalizedName(name);
		setCreativeTab(ImageCraft.imageCraftTab);
		GameRegistry.registerBlock(this, name);
	}

	public String getName() {
		return name;
	}

	public Rgba getColor() {
		return color;
	}

}
