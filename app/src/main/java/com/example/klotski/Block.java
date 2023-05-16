package com.example.klotski;

public class Block {
    private int row;   // row of the top-left corner of the block on the board
    private int col;   // column of the top-left corner of the block on the board
    private int width; // number of columns covered by the block
    private int height; // number of rows covered by the block

    public Block(int row, int col, int width, int height) {
        this.row = row;
        this.col = col;
        this.width = width;
        this.height = height;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void moveTo(int newRow, int newCol) {
        row = newRow;
        col = newCol;
    }

    public boolean compareType(Block b2){
        return width == b2.getWidth() && height == b2.getHeight();
    }
}
