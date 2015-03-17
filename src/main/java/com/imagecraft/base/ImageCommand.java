package com.imagecraft.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.imagecraft.color.CieLab;
import com.imagecraft.color.Distance;
import com.imagecraft.color.Rgba;
import com.imagecraft.exception.HistoryException;
import com.imagecraft.exception.ImageException;
import com.imagecraft.exception.InvalidArgument;

public class ImageCommand implements ICommand {

	private String command_name = "image";

	private List aliases;

	private List<ColorAndBlockState> pixelBlocks;
	private Arguments arguments;
	private MyImage image;
	private History historyEvents;

	public ImageCommand() {
		this.aliases = new ArrayList();
		pixelBlocks = new ArrayList<ColorAndBlockState>();
		historyEvents = new History(5);

		for (int i = 0; i < ImageCraft.colorBlocks.size(); i++) {
			ColorBlock block = (ColorBlock) ImageCraft.colorBlocks.get(i);
			pixelBlocks.add(new ColorAndBlockState(block.getColor(), block
					.getDefaultState()));
		}
	}

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return command_name;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getName() + " " + Arguments.getUsage();
	}

	@Override
	public List getAliases() {
		return this.aliases;
	}

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {

		try {
			arguments = new Arguments(sender, args);
		} catch (InvalidArgument e1) {
			sender.addChatMessage(e1.getAsChat());
			sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
			return;
		}

		if (arguments.isUndoSubcommand()) {
			processUndoSubCommand(sender);
			return;
		}

		try {
			image = new MyImage(arguments.getImagePath());
		} catch (ImageException e1) {
			sender.addChatMessage(e1.getAsChat());
			return;
		}

		if (arguments.getSubCommand().equals("clear")) {
			clearImage(sender);
		} else {
			buildImage(sender, image);
		}

	}

	private void processUndoSubCommand(ICommandSender sender) {
		try {
			historyEvents.undo(sender);
		} catch (HistoryException e) {
			sender.addChatMessage(e.getAsChat());
		}
	}

	private void buildImage(ICommandSender sender, MyImage image) {
		resizeImage(image, arguments.getImageWidth(),
				arguments.getImageHeight());

		List<HistoryComponent> history = new ArrayList<HistoryComponent>();

		for (int i = 0; i < arguments.getImageHeight(); ++i) {
			for (int j = 0; j < arguments.getImageWidth(); j++) {
				Rgba color = image.getImageColor(i, j);
				BlockPos block_pos = getPixelPos(sender, j, i);
				IBlockState state;

				if (color.getAlpha() < arguments.getAlpha()) {
					state = Blocks.air.getDefaultState();
				} else {
					int min_index = getMinDistanceIndex(color);
					state = pixelBlocks.get(min_index).getState();
				}

				IBlockState previousState = changeState(sender, block_pos,
						state);
				history.add(new HistoryComponent(block_pos, previousState));
			}
		}
		
		historyEvents.add(history);
	}

	private void clearImage(ICommandSender sender) {
		List<HistoryComponent> history = new ArrayList<HistoryComponent>();

		for (int i = 0; i < arguments.getImageHeight(); ++i) {
			for (int j = 0; j < arguments.getImageWidth(); j++) {
				BlockPos block_pos = getPixelPos(sender, j, i);
				IBlockState previousState = changeState(sender, block_pos,
						Blocks.air.getDefaultState());
				history.add(new HistoryComponent(block_pos, previousState));
			}
		}

		historyEvents.add(history);
	}

	private IBlockState changeState(ICommandSender sender, BlockPos pos,
			IBlockState state) {
		IBlockState previousState = sender.getEntityWorld().getBlockState(pos);
		sender.getEntityWorld().setBlockState(pos, state);
		return previousState;

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

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args,
			BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	private void resizeImage(MyImage image, int newW, int newH) {
		if (arguments.getScaleType().equals("smooth")) {
			image.resizeImage(newW, newH, MyImage.SMOOTH_RESIZE);
		} else if (arguments.getScaleType().equals("bicubic")) {
			image.resizeImage(newW, newH, MyImage.BICUBIC_RESIZE);
		} else {
			image.resizeImage(newW, newH, MyImage.NEAREST_RESIZE);
		}
	}

	private BlockPos getPixelPos(ICommandSender sender, int x, int y) {
		Entity entity = sender.getCommandSenderEntity();
		EnumFacing facing = entity.getHorizontalFacing();
		BlockPos pos = arguments.getStartPos();
		boolean left = arguments.isLeft();
		boolean up = arguments.isUp();

		if (facing == EnumFacing.EAST) {
			if (left) {
				pos = pos.south(x);
			} else {
				pos = pos.north(x);
			}
			if (up) {
				pos = pos.up(y);
			} else {
				pos = pos.east(y);
			}
		}

		if (facing == EnumFacing.SOUTH) {
			if (left) {
				pos = pos.west(x);
			} else {
				pos = pos.east(x);
			}
			if (up) {
				pos = pos.up(y);
			} else {
				pos = pos.south(y);
			}
		}

		if (facing == EnumFacing.WEST) {
			if (left) {
				pos = pos.north(x);
			} else {
				pos = pos.south(x);
			}
			if (up) {
				pos = pos.up(y);
			} else {
				pos = pos.west(y);
			}
		}

		if (facing == EnumFacing.NORTH) {
			if (left) {
				pos = pos.east(x);
			} else {
				pos = pos.west(x);
			}
			if (up) {
				pos = pos.up(y);
			} else {
				pos = pos.north(y);
			}
		}

		return pos;
	}

}
