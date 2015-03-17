package com.imagecraft.base;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class HistoryComponent {
	private BlockPos position;
	private IBlockState state;
	
	public HistoryComponent(BlockPos position, IBlockState state) {
		super();
		this.position = position;
		this.state = state;
	}

	public BlockPos getPosition() {
		return position;
	}

	public IBlockState getState() {
		return state;
	}
	
}
