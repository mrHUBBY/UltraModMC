package com.hubby.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Simple class used to pass the results around
 * for a block and its state and position
 * @author davidleistiko
 *
 */
public class HubbyBlockResult {
    
    /**
     * The actual block
     */
    protected Block _block;
    
    /**
     * The current state for the block
     */
    protected IBlockState _blockState;
    
    /**
     * The position of the block
     */
    protected BlockPos _blockPos;
    
    /**
     * Constructor
     * @param pos - the position for the <code>Block</code>
     * @param state - the current state for the <code>Block</code>
     * @param block - the actual <code>Block</code>
     */
    public HubbyBlockResult(BlockPos pos, IBlockState state, Block block) {
        _block = block;
        _blockState = state;
        _blockPos = pos;
    }
    
    /**
     * Constructor
     * Sets the result by looking up the block info for the specified pos
     * @param pos - the block position to lookup
     */
    public HubbyBlockResult(BlockPos pos) {
        setBlockPos(pos);
    }
    
    /**
     * Default Constructor
     */
    public HubbyBlockResult() {
        _blockPos = null;
        _blockState = null;
        _block = null;
    }
    
    /**
     * Returns if this result should be considered valid
     * @return boolean - are we valid?
     */
    public boolean isValid() {
        return _blockPos != null && _blockState != null && _block != null;
    }
    
    /**
     * By settings the <code>BlockPos</code>, the state and block
     * are updated as well
     * @param pos - the new pos to set
     */
    public void setBlockPos(BlockPos pos) {
        World world = HubbyUtils.getClientWorld();
        _blockPos = pos;
        _blockState = world.getBlockState(pos);
        _block = world.getBlockState(pos).getBlock();
    }
    
    /**
     * Returns the position for the block
     * @return BlockPos - the block's position
     */
    public BlockPos getBlockPos() {
        return _blockPos;
    }
    
    /**
     * Returns the current state for the block
     * @return IBlockState - the block's state
     */
    public IBlockState getBlockState() {
        return _blockState;
    }
    
    /**
     * Returns the actual block
     * @return Block - the block
     */
    public Block getBlock() {
        return _block;
    }
}