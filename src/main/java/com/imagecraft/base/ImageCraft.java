package com.imagecraft.base;

import java.util.ArrayList;
import java.util.List;

import com.imagecraft.color.Rgba;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ImageCraft.MODID, version = ImageCraft.VERSION)
public class ImageCraft {

	public static final String MODID = "imagecraft";
	public static final String VERSION = "1.01";

	public static List<Block> colorBlocks;

	public static CreativeTabs imageCraftTab = new CreativeTabs("ImageCraft") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Items.ender_eye;
		}
	};

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			colorBlocks = new ArrayList<Block>();

			for (int red = 0; red <= 255; red += 51) {
				for (int green = 0; green <= 255; green += 51) {
					for (int blue = 0; blue <= 255; blue += 51) {
						colorBlocks.add(new ColorBlock(new Rgba(red, green,
								blue)));
					}
				}
			}

			for (int i = 0; i < colorBlocks.size(); i++) {
				renderItem.getItemModelMesher().register(
						Item.getItemFromBlock(colorBlocks.get(i)),
						0,
						new ModelResourceLocation(ImageCraft.MODID + ":"
								+ ((ColorBlock) colorBlocks.get(i)).getName(),
								"inventory"));
			}

		}

	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new ImageCommand());
	}

}
