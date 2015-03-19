package com.imagecraft.base;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import com.imagecraft.exception.HistoryException;
import com.imagecraft.exception.ImageException;
import com.imagecraft.exception.InvalidArgument;
import com.imagecraft.image.ColorAndBlockState;
import com.imagecraft.image.MyImage;
import com.imagecraft.image.PixelToBlock;
import com.imagecraft.image.PositionAndState;

public class ImageCommand implements ICommand {

	private String command_name = "image";
	private List aliases;
	private Arguments arguments;
	private MyImage image;
	private History historyEvents;

	public ImageCommand() {
		this.aliases = new ArrayList();
		this.historyEvents = new History(5);
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

		if (arguments.getImageWidth() == -1) {
			int scale = image.getHeight() / arguments.getImageHeight();
			arguments.setImageWidth(image.getWidth() / scale);
		} else if (arguments.getImageHeight() == -1) {
			int scale = image.getWidth() / arguments.getImageWidth();
			arguments.setImageHeight(image.getHeight() / scale);
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

		List<PositionAndState> history = new ArrayList<PositionAndState>();

		int numberOfThread = image.getHeight() > 25 ? Runtime.getRuntime()
				.availableProcessors() : 1;

		ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);

		List<PixelToBlock> converters = new ArrayList<PixelToBlock>();

		int heightInterval = 0;
		for (int i = 0; i < numberOfThread; i++) {
			heightInterval = image.getHeight() / numberOfThread;
			converters.add(new PixelToBlock(sender, image, arguments,
					new Rectangle(i * heightInterval, 0, image.getWidth(),
							heightInterval)));
		}

		if (heightInterval * numberOfThread < image.getHeight()) {
			converters.add(new PixelToBlock(sender, image, arguments,
					new Rectangle(heightInterval * numberOfThread, 0, image
							.getWidth(), image.getHeight() - heightInterval
							* numberOfThread)));
		}

		List<Future<List<PositionAndState>>> futures = null;
		try {
			futures = executor.invokeAll(converters);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0; i < futures.size(); i++) {
			List<PositionAndState> result = null;
			try {
				result = futures.get(i).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (PositionAndState positionAndState : result) {
				IBlockState previousState = changeState(sender,
						positionAndState.getPosition(),
						positionAndState.getState());
				history.add(new PositionAndState(
						positionAndState.getPosition(), previousState));
			}
		}

		executor.shutdown();

		while (!executor.isTerminated()) {
		}
		historyEvents.add(history);
	}

	private void clearImage(ICommandSender sender) {
		List<PositionAndState> history = new ArrayList<PositionAndState>();

		for (int i = 0; i < arguments.getImageHeight(); ++i) {
			for (int j = 0; j < arguments.getImageWidth(); j++) {
				BlockPos block_pos = MyImage.getBlockPos(sender, arguments, j,
						i);
				IBlockState previousState = changeState(sender, block_pos,
						Blocks.air.getDefaultState());
				history.add(new PositionAndState(block_pos, previousState));
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
}
