package com.example.klotski;

import android.util.Log;

import java.util.List;

public class Board {
    private final int numRows = 5;
    private final int numCols = 4;
    private final Block[][] grid = new Block[numRows][numCols];
    private Block trappedBlock;



    public Board(List<Block> blocks) {
        for (Block block : blocks) {
            grid[block.getRow()][block.getCol()] = block;
            //In typical Klotski, the trapped block is the solo 2x2 block
            if (block.getWidth() == 2 && block.getHeight() == 2) {
                trappedBlock = block;
            }
        }
    }

    public boolean isLegalMove(Block block, int newRow, int newCol) {
        //Preventing movement to same spot
        if(newRow == block.getRow() && newCol == block.getCol()) return false;

        //Preventing diagonal movement
        if(newRow != block.getRow() && newCol != block.getCol()) return false;

        //Ensuring the desired location is within bounds of board
        if (newRow < 0 || newRow + block.getHeight() > numRows || newCol < 0 || newCol + block.getWidth() > numCols) {
            return false;
        }

        //Preventing overlap of blocks in other corner positions
        if(block.getHeight() == 2 || block.getWidth() == 2) {
            if(newCol > 0
                    && newRow < numRows-1
                    && grid[newRow+1][newCol-1] != null
                    && grid[newRow+1][newCol-1] != block
                    && grid[newRow+1][newCol-1].getWidth() == 2
                    && block.getHeight() == 2
                    && !block.compareType(grid[newRow+1][newCol-1])) return false;
            if(newRow > 0
                    && newCol < numCols-1
                    && grid[newRow-1][newCol+1] != null
                    && grid[newRow-1][newCol+1] != block
                    && grid[newRow-1][newCol+1].getHeight() == 2
                    && block.getWidth() == 2
                    && !block.compareType(grid[newRow-1][newCol+1])) return false;
        }

        //Preventing overlap of blocks in upper left corner position
        if(newRow > 0
                && newCol > 0
                && grid[newRow-1][newCol-1] != null
                && grid[newRow-1][newCol-1] != block
                && grid[newRow-1][newCol-1].getWidth() == 2
                && grid[newRow-1][newCol-1].getHeight() == 2) return false;

        if(newRow > 0
                && grid[newRow-1][newCol] != null
                && grid[newRow-1][newCol] != block
                && grid[newRow-1][newCol].getHeight() == 2) return false;

        if(newCol > 0
                && grid[newRow][newCol-1] != null
                && grid[newRow][newCol-1] != block
                && grid[newRow][newCol-1].getWidth() == 2) return false;
        for (int i = newRow; i < newRow + block.getHeight(); i++) {
            for (int j = newCol; j < newCol + block.getWidth(); j++) {
                if(grid[i][j] != null && grid[i][j] != block){
                    return false;
                }
            }
        }

        return true;  // the move is legal
    }

    public Block moveBlock(Block block, int newRow, int newCol) {
        if (!isLegalMove(block, newRow, newCol)) {
            throw new IllegalArgumentException("Illegal move");
        }
        grid[block.getRow()][block.getCol()] = null;
        block.moveTo(newRow, newCol);
        grid[block.getRow()][block.getCol()] = block;
        return grid[block.getRow()][block.getCol()];
    }

    public Block getBlock(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            throw new IllegalArgumentException("Invalid block position");
        }
        return grid[row][col];
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }



    public boolean isWinningState() {
        //Upper left corner of 2x2 block at bottom center of board
        return trappedBlock.getCol() == 1 && trappedBlock.getRow() == 3;
    }
}
