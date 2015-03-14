package com.imagecraft.exception;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class BaseException extends Exception {

	public BaseException(String message) {
		super(message);
	}

	public ChatComponentText getAsChat() {
		ChatComponentText text = new ChatComponentText(getMessage());
		text.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));
		return text;
	}

}
