package com.imagecraft.base;

import com.imagecraft.exception.InvalidArgument;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class Arguments {

	private String imagePath;
	private int imageWidth;
	private int imageHeight;
	private BlockPos startPos;
	private boolean left;
	private boolean up;
	private String distance;
	private int alpha;
	private String subCommand;
	private String scaleType;
	private boolean undoSubcommand;

	Arguments(ICommandSender sender, String[] args) throws InvalidArgument {

		if(args.length == 1 && args[0].equalsIgnoreCase("undo"))
		{
			undoSubcommand = true;
			return;
		}
		else
		{
			undoSubcommand = false;
		}
		
		if (args.length < 3) {
			throw new InvalidArgument("Not enough required parameter!");
		}

		setDefaultArguments(sender);

		imagePath = args[0];

		try {
			imageWidth = Integer.valueOf(args[1]);
			imageHeight = Integer.valueOf(args[2]);
		} catch (NumberFormatException e) {
			throw new InvalidArgument("Invalid width or height parameter!");
		}

		processOptionalArguments(sender, args);
	}

	public static String getUsage() {
		return "<path> <width> <height> [left right up forward pos clear scale distance alpha]";
	}

	private void setDefaultArguments(ICommandSender sender) {
		imagePath = "";
		imageWidth = 0;
		imageHeight = 0;
		startPos = getDefaultStartImagePos(sender);
		left = false;
		up = true;
		distance = "cie94";
		alpha = 10;
		subCommand = "build";
		scaleType = "nearest";
	}

	private BlockPos getDefaultStartImagePos(ICommandSender sender) {
		Entity entity = sender.getCommandSenderEntity();
		EnumFacing facing = entity.getHorizontalFacing();
		Vec3i facingDir = facing.getDirectionVec();
		Vec3 pos = sender.getPositionVector().addVector(facingDir.getX(),
				facingDir.getY(), facingDir.getZ());
		return new BlockPos(pos);
	}

	private void processOptionalArguments(ICommandSender sender, String[] args)
			throws InvalidArgument {
		for (int i = 3; i < args.length; ++i) {
			if (args[i].equalsIgnoreCase("left")) {
				left = false;
			} else if (args[i].equalsIgnoreCase("right")) {
				left = true;
			} else if (args[i].equalsIgnoreCase("up")) {
				up = true;
			} else if (args[i].equalsIgnoreCase("forward")) {
				up = false;
			} else if (args[i].equalsIgnoreCase("clear")) {
				subCommand = "clear";
			} else if (args[i].equalsIgnoreCase("scale")) {
				i = processScaleCommand(args, i);
			} else if (args[i].equalsIgnoreCase("alpha")) {
				i = processAlphaCommand(args, i);
			} else if (args[i].equalsIgnoreCase("distance")) {
				i = processDistanceCommand(args, i);
			} else if (args[i].equalsIgnoreCase("pos")) {
				i = processPosCommand(args, i);
			} else {
				throw new InvalidArgument("Unknown argument: " + args[i]);
			}
		}
	}

	private int processPosCommand(String[] args, int i) throws InvalidArgument {
		try {
			if (hasArgument(i, 3, args.length) == false) {
				throw new InvalidArgument("Invalid pos parameter!");
			}

			int posX, posY, posZ;
			try {
				posX = Integer.valueOf(args[i + 1]);
				posY = Integer.valueOf(args[i + 2]);
				posZ = Integer.valueOf(args[i + 3]);
			} catch (NumberFormatException e) {
				throw new InvalidArgument("Invalid pos parameter!");
			}
			i += 3;
			startPos = new BlockPos(posX, posY, posZ);
		} catch (NumberFormatException e) {
			throw new InvalidArgument("Invalid pos parameter arguments!");
		}
		return i;
	}

	private int processDistanceCommand(String[] args, int i)
			throws InvalidArgument {
		if (hasArgument(i, 1, args.length) == false) {
			throw new InvalidArgument("Invalid distance parameter!");
		}

		distance = args[i + 1];
		++i;

		if (!isOneOf(distance, "cie76", "cie94", "ciede2000")) {
			throw new InvalidArgument("Invalid distance parameter: " + distance);
		}
		return i;
	}

	private int processAlphaCommand(String[] args, int i)
			throws InvalidArgument {
		if (hasArgument(i, 1, args.length) == false) {
			throw new InvalidArgument("Invalid alpha parameter!");
		}

		try {
			alpha = Integer.valueOf(args[i + 1]);
			++i;
		} catch (NumberFormatException e) {
			throw new InvalidArgument("Invalid alpha parameter!");
		}
		return i;
	}

	private int processScaleCommand(String[] args, int i)
			throws InvalidArgument {
		if (hasArgument(i, 1, args.length) == false) {
			throw new InvalidArgument("Invalid scale parameter!");
		}

		scaleType = args[i + 1].toLowerCase();
		++i;
		if (!isOneOf(scaleType, "smooth", "bicubic", "nearest")) {
			throw new InvalidArgument("Invalid scale parameter: " + scaleType);
		}
		return i;
	}

	private boolean hasArgument(int actual, int db, int max_size) {
		return actual + db < max_size;
	}

	private boolean isOneOf(String value, String... values) {
		for (String actualValue : values) {
			if (value.equalsIgnoreCase(actualValue)) {
				return true;
			}
		}
		return false;
	}

	public String getImagePath() {
		return imagePath;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public BlockPos getStartPos() {
		return startPos;
	}

	public boolean isLeft() {
		return left;
	}

	public boolean isUp() {
		return up;
	}

	public String getDistance() {
		return distance;
	}

	public int getAlpha() {
		return alpha;
	}

	public String getSubCommand() {
		return subCommand;
	}

	public String getScaleType() {
		return scaleType;
	}

	public boolean isUndoSubcommand() {
		return undoSubcommand;
	}

}
