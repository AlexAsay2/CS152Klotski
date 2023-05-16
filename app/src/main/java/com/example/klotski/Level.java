package com.example.klotski;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private Board board;
    private String name;
    private int levelNum;
    private static int moves = 0;
    private static int currentLevelNum = -1;

    public Level(int number) {
        levelNum = number;
        loadLevel();
    }

    public Board getBoard() {
        return board;
    }

    public String getName() {
        return name;
    }

    public int getLevelNum() { return levelNum; }

    public static void setCurrentLevelNum(int n) {
        Level.currentLevelNum = n;
    }

    public static int getCurrentLevelNum() {
        return Level.currentLevelNum;
    }

    public static int getMoves() {
        return Level.moves;
    }

    public static void addMove() {
        Level.moves++;
    }

    private void loadLevel() {
        List<Block> blocks = new ArrayList<>();
        moves = 0;
        switch (levelNum) {
            case 0:
                // Eazy puzzle
                blocks.add(new Block(0, 0, 1, 1));
                blocks.add(new Block(0, 1, 2, 2));
                blocks.add(new Block(0, 3, 1, 1));
                blocks.add(new Block(1, 0, 1, 1));
                blocks.add(new Block(1, 3, 1, 1));
                blocks.add(new Block(2, 0, 1, 2));
                blocks.add(new Block(2, 1, 1, 1));
                blocks.add(new Block(2, 2, 1, 1));
                blocks.add(new Block(2, 3, 1, 2));
                blocks.add(new Block(3, 1, 1, 1));
                blocks.add(new Block(3, 2, 1, 1));
                blocks.add(new Block(4, 0, 1, 1));
                blocks.add(new Block(4, 3, 1, 1));
                name = "Easy";
                break;
            case 1:
                // Classic puzzle
                blocks.add(new Block(0, 0, 1, 2));
                blocks.add(new Block(0, 1, 2, 2));
                blocks.add(new Block(0, 3, 1, 2));
                blocks.add(new Block(2, 0, 1, 2));
                blocks.add(new Block(2, 1, 2, 1));
                blocks.add(new Block(2, 3, 1, 2));
                blocks.add(new Block(3, 1, 1, 1));
                blocks.add(new Block(3, 2, 1, 1));
                blocks.add(new Block(4, 0, 1, 1));
                blocks.add(new Block(4, 3, 1, 1));
                name = "Classic";
                break;
            case 2:
                // Difficult puzzle
                blocks.add(new Block(0, 0, 1, 1));
                blocks.add(new Block(0, 1, 2, 2));
                blocks.add(new Block(0, 3, 1, 1));
                blocks.add(new Block(1, 0, 1, 2));
                blocks.add(new Block(1, 3, 1, 2));
                blocks.add(new Block(2, 1, 2, 1));
                blocks.add(new Block(3, 0, 1, 1));
                blocks.add(new Block(3, 1, 2, 1));
                blocks.add(new Block(3, 3, 1, 1));
                blocks.add(new Block(4, 1, 2, 1));
                name = "Difficult";
                break;
            case 3:
                //Test puzzle (Eventually will become Custom Level)
                blocks.add(new Block(0, 1, 2, 2));
                name = "Test Level";
                break;
            default:
                throw new IllegalArgumentException("Invalid level number");
        }

        board = new Board(blocks);
    }
}
