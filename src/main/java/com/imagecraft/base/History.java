package com.imagecraft.base;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

import com.imagecraft.exception.HistoryException;

public class History {

	int maxHistory;
	Deque<List<HistoryComponent>> eventList;

	public History(int maxHistory) {
		this.maxHistory = maxHistory;
		eventList = new ArrayDeque<List<HistoryComponent>>();

		if (this.maxHistory <= 0) {
			this.maxHistory = 5;
		}
	}

	public void add(List<HistoryComponent> history) {
		eventList.push(history);
		if (eventList.size() > maxHistory) {
			eventList.removeLast();
		}
	}

	public void undo(ICommandSender sender) throws HistoryException {
		if (eventList.size() == 0) {
			throw new HistoryException("Cannot undo!");
		}

		List<HistoryComponent> history = eventList.removeFirst();
		World world = sender.getEntityWorld();

		for (HistoryComponent historyComponent : history) {
			world.setBlockState(historyComponent.getPosition(),
					historyComponent.getState());
		}
	}

}
