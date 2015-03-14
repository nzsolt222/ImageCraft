package com.imagecraft.base;

import com.imagecraft.color.Distance;
import com.imagecraft.color.Rgba;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
