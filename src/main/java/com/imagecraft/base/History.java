package com.imagecraft.base;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

import com.imagecraft.exception.HistoryException;
import com.imagecraft.image.PositionAndState;

public class History {

	int maxHistory;
	Deque<List<PositionAndState>> eventList;

	public History(int maxHistory) {
		this.maxHistory = maxHistory;
		eventList = new ArrayDeque<List<PositionAndState>>();

		if (this.maxHistory <= 0) {
			this.maxHistory = 5;
		}
	}

	public void add(List<PositionAndState> history) {
		eventList.push(history);
		if (eventList.size() > maxHistory) {
			eventList.removeLast();
		}
	}

	public void undo(ICommandSender sender) throws HistoryException {
		if (eventList.size() == 0) {
			throw new HistoryException("Cannot undo!");
		}

		List<PositionAndState> history = eventList.removeFirst();
		World world = sender.getEntityWorld();

		for (PositionAndState historyComponent : history) {
			world.setBlockState(historyComponent.getPosition(),
					historyComponent.getState());
		}
	}

}
