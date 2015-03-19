package com.imagecraft.base;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

import com.imagecraft.exception.InvalidArgument;

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
		setDefaultArguments(sender);
		processArguments(sender, args);
	}

	public static String getUsage() {
		return "[path w h left right up forward pos clear scale distance alpha undo]";
	}

	private void setDefaultArguments(ICommandSender sender) {
		imagePath = "";
		imageWidth = -1;
		imageHeight = -1;

		startPos = getDefaultStartImagePos(sender);
		left = false;
		up = true;
		distance = "cie94";
		alpha = 10;
		subCommand = "build";
		scaleType = "nearest";
		undoSubcommand = false;
	}

	private BlockPos getDefaultStartImagePos(ICommandSender sender) {
		Entity entity = sender.getCommandSenderEntity();
		EnumFacing facing = entity.getHorizontalFacing();
		Vec3i facingDir = facing.getDirectionVec();
		Vec3 pos = sender.getPositionVector().addVector(facingDir.getX(),
				facingDir.getY(), facingDir.getZ());
		return new BlockPos(pos);
	}

	private void processArguments(ICommandSender sender, String[] args)
			throws InvalidArgument {

		if (args.length == 1 && args[0].equalsIgnoreCase("undo")) {
			undoSubcommand = true;
			return;
		} else {
			undoSubcommand = false;
		}

		boolean hasPathCommand = false;
		boolean hasWorHCommand = false;

		for (int i = 0; i < args.length; ++i) {
			if (args[i].equalsIgnoreCase("path")) {
				i = processPathCommand(args, i);
				hasPathCommand = true;
			} else if (args[i].equalsIgnoreCase("w")) {
				i = proccessWCommand(args, i);
				hasWorHCommand = true;
			} else if (args[i].equalsIgnoreCase("h")) {
				i = processHCommand(args, i);
				hasWorHCommand = true;
			} else if (args[i].equalsIgnoreCase("left")) {
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

		if (hasPathCommand == false) {
			throw new InvalidArgument("You have to specify the path.");
		}

		if (hasWorHCommand == false) {
			throw new InvalidArgument(
					"You have to specify the width or height.");
		}

	}

	private int processPathCommand(String[] args, int i) throws InvalidArgument {
		if (hasArgument(i, 1, args.length) == false) {
			throw new InvalidArgument("Invalid path parameter!");
		}
		imagePath = args[i + 1];
		++i;
		return i;
	}

	private int processHCommand(String[] args, int i) throws InvalidArgument {
		if (hasArgument(i, 1, args.length) == false) {
			throw new InvalidArgument("Invalid h parameter!");
		}

		try {
			imageHeight = Integer.valueOf(args[i + 1]);
			++i;
		} catch (NumberFormatException e) {
			throw new InvalidArgument("Invalid h parameter!");
		}
		return i;
	}

	private int proccessWCommand(String[] args, int i) throws InvalidArgument {
		if (hasArgument(i, 1, args.length) == false) {
			throw new InvalidArgument("Invalid w parameter!");
		}

		try {
			imageWidth = Integer.valueOf(args[i + 1]);
			++i;
		} catch (NumberFormatException e) {
			throw new InvalidArgument("Invalid w parameter!");
		}
		return i;
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

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
}
