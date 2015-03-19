package com.imagecraft.image;

import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import com.imagecraft.base.Arguments;
import com.imagecraft.base.ColorBlock;
import com.imagecraft.base.ImageCraft;
import com.imagecraft.color.CieLab;
import com.imagecraft.color.Distance;
import com.imagecraft.color.Rgba;

public class PixelToBlock implements Callable<List<PositionAndState>> {

	private static List<ColorAndBlockState> pixelBlocks;

	static {
		pixelBlocks = new ArrayList<ColorAndBlockState>();

		for (int i = 0; i < ImageCraft.colorBlocks.size(); i++) {
			ColorBlock block = (ColorBlock) ImageCraft.colorBlocks.get(i);
			pixelBlocks.add(new ColorAndBlockState(block.getColor(), block
					.getDefaultState()));
		}
	}

	private List<PositionAndState> blocks;
	private ICommandSender sender;
	private MyImage image;
	private Rectangle rectangle;
	private Arguments arguments;

	public PixelToBlock(ICommandSender sender, MyImage image,
			Arguments arguments, Rectangle rectangle) {
		super();

		this.sender = sender;
		this.image = image;
		this.rectangle = rectangle;
		this.arguments = arguments;

		blocks = new ArrayList<PositionAndState>();
	}

	@Override
	public List<PositionAndState> call() throws Exception {
		for (int i = rectangle.x; i < rectangle.x + rectangle.height; ++i) {
			for (int j = rectangle.y; j < rectangle.width; j++) {
				Rgba color = image.getColor(i, j);
				BlockPos block_pos = MyImage.getBlockPos(sender, arguments, j,
						i);
				IBlockState state;

				if (color.getAlpha() < arguments.getAlpha()) {
					state = Blocks.air.getDefaultState();
				} else {
					int min_index = getMinDistanceIndex(color);
					state = pixelBlocks.get(min_index).getState();
				}

				blocks.add(new PositionAndState(block_pos, state));
			}
		}
		return blocks;
	}

	private int getMinDistanceIndex(Rgba color) {
		double minDistance = Double.MAX_VALUE;
		int minIndex = 0;

		for (int i = 0; i < pixelBlocks.size(); i++) {
			CieLab pixelColor = pixelBlocks.get(i).getCieLabColor();

			double currentDistance;
			try {
				Method m = Distance.class.getDeclaredMethod(
						arguments.getDistance(), CieLab.class, CieLab.class);
				currentDistance = (Double) m.invoke(null, pixelColor,
						color.toCieLab());

				if (currentDistance < minDistance) {
					minDistance = currentDistance;
					minIndex = i;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}

		}
		return minIndex;
	}
}
