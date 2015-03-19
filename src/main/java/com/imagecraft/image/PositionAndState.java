package com.imagecraft.image;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class PositionAndState {
	private BlockPos position;
	private IBlockState state;
	
	public PositionAndState(BlockPos position, IBlockState state) {
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
