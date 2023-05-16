package com.example.klotski;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameplayActivity extends AppCompatActivity {
    private Board board;
    private GridLayout gridLayout;
    private int blockSize;
    private int numRows;
    private int numCols;
    private float startX;
    private float startY;
    private boolean moving;
    private Block movingBlock;
    private int movingBlockStartRow;
    private int movingBlockStartCol;
    private Chronometer timer;
    private TextView stepsView;
    private long timeElapsed;
    private Level level;
    private HighScoreManager highScoreManager;
    private List<Point> prevPoints = new ArrayList<>();
    private long pausedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.darkbrown)));

        highScoreManager = new HighScoreManager(this);

        // Get the current level and load its board
        int levelNum = Level.getCurrentLevelNum();
        level = new Level(levelNum);
        board = level.getBoard();
        numRows = board.getNumRows();
        numCols = board.getNumCols();

        centerTitle();
        getSupportActionBar().setTitle(level.getName());

        // Set up the grid layout to display the blocks
        gridLayout = findViewById(R.id.grid_layout);
        gridLayout.setColumnCount(numCols);
        gridLayout.setRowCount(numRows);
        blockSize = getResources().getDimensionPixelSize(R.dimen.block_size);

        moving = false;

        //Set up Views of Grid Layout
        updateBlockViews();

        //Set up touch listeners for every View in GridLayout
        setupTouchListeners();

        // Set up the view to display the timer
        timer = findViewById(R.id.chronometer);
        timer.start();

        //Set up the view to display moves
        stepsView = findViewById(R.id.movesView);
        stepsView.setText("Moves: " + level.getMoves());

        //Set up restart button

        Button button = findViewById(R.id.button);
        button.setText("Restart");
        Context c = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Are you sure you want to restart?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(GameplayActivity.this, GameplayActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Resume timer
                        timer.setBase(SystemClock.elapsedRealtime() - pausedTime);
                        timer.start();
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.show();
                //Pause timer
                timer.stop();
                pausedTime = SystemClock.elapsedRealtime() - timer.getBase();


            }
        });


    }

    private void updateBlockViews() {
        //Reset GridLayout
        gridLayout.removeAllViews();

        //Set up HashMap to keep track of all corresponding positions to a block, since board array only stores Board in upper left position
        HashMap<Block, HashMap<Point,Integer>> map = new HashMap<>();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Block block = board.getBlock(row, col);
                //Check if upper left corner of block exists here
                if (block != null) {
                    HashMap<Point, Integer> temp = new HashMap<>();
                    //2x2 block
                    if(block.getHeight() == 2 && block.getWidth() == 2){
                        gridLayout.addView(new BlockView(this, block, blockSize, R.drawable.red1));
                        temp.put(new Point(row+1,col),R.drawable.red2);
                        temp.put(new Point(row,col+1),R.drawable.red3);
                        temp.put(new Point(row+1,col+1),R.drawable.red4);
                        map.put(block,temp);
                    }else if(block.getHeight() == 1 && block.getWidth() == 2){ //1x2 block
                        gridLayout.addView(new BlockView(this, block, blockSize, R.drawable.yellow1));
                        temp.put(new Point(row,col+1),R.drawable.yellow2);
                        map.put(block,temp);
                    }else if(block.getHeight() == 2 && block.getWidth() == 1){ //2x1 block
                        gridLayout.addView(new BlockView(this, block, blockSize, R.drawable.yellow3));
                        temp.put(new Point(row+1,col),R.drawable.yellow4);
                        map.put(block,temp);
                    }else{ //1x1 block
                        gridLayout.addView(new BlockView(this, block, blockSize, R.drawable.green));
                    }
                } else {
                    boolean found = false;
                    //Check if position exists in HashMap
                    for(Block b : map.keySet()){
                        HashMap<Point,Integer> temp = map.get(b);
                        for(Point p : temp.keySet()){
                            if(p.x == row && p.y == col){
                                gridLayout.addView(new BlockView(this, b, blockSize, temp.get(p)));
                                found = true;
                                break;
                            }
                        }
                    }
                    //Position is not related to any block, it is an empty space
                    if(!found) {
                        gridLayout.addView(new BlockView(this, null, blockSize, R.drawable.blankspace));
                    }

                }
            }
        }
        //Reset touch listeners, since current touch listeners based on previous GridLayout setup
        setupTouchListeners();
    }

    private void setupTouchListeners() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            child.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        //set movingBlock to the block user wants to move
                        if (!moving && v instanceof BlockView) {
                            startX = event.getX();
                            startY = event.getY();
                            movingBlock = ((BlockView) v).getBlock();
                            if(movingBlock == null) {
                                return true;
                            }
                            movingBlockStartRow = movingBlock.getRow();
                            movingBlockStartCol = movingBlock.getCol();
                            moving = true;
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        //keep track of touched blockview positions with prevPoints
                        if(moving){
                            float dx = event.getX() - startX;
                            float dy = event.getY() - startY;
                            int newRow = movingBlockStartRow + Math.round(dy / blockSize);
                            int newCol = movingBlockStartCol + Math.round(dx / blockSize);
                            if(!prevPoints.contains(new Point(newRow, newCol)) && !(newRow == movingBlockStartRow && newCol == movingBlockStartCol)){
                                prevPoints.add(new Point(newRow,newCol));
                            }
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        float dx = event.getX() - startX;
                        float dy = event.getY() - startY;
                        int newRow = movingBlockStartRow + Math.round(dy / blockSize);
                        int newCol = movingBlockStartCol + Math.round(dx / blockSize);
                        if(!prevPoints.contains(new Point(newRow, newCol)) && !(newRow == movingBlockStartRow && newCol == movingBlockStartCol)){
                            prevPoints.add(new Point(newRow,newCol));
                        }
                        if (moving) {
                            moving = false;
                            if(prevPoints.size() == 0) return true;
                            //Checking if prevPoints contains any invalid moves
                            //If it does, restrict prevPoints to just the legal moves before
                            for(int i = 0; i < prevPoints.size(); i++){
                                if(!board.isLegalMove(movingBlock, prevPoints.get(i).x, prevPoints.get(i).y)){
                                    prevPoints = prevPoints.subList(0,i);
                                    break;
                                }
                            }
                            Block b = null;

                            //Loop in reverse order through prevPoints until there is a legal move that can be made
                            //May be redundant with the previous loop (last second bug fix addition)
                            //not enough time to test if catch statement is still needed

                            while(prevPoints.size() != 0) {
                                try {
                                    b = board.moveBlock(movingBlock, prevPoints.get(prevPoints.size()-1).x, prevPoints.get(prevPoints.size()-1).y);
                                    break;
                                } catch (IllegalArgumentException e) {
                                    prevPoints.remove(prevPoints.get(prevPoints.size()-1));
                                    if(prevPoints.size() == 0) {
                                        return true;
                                    }
                                }
                            }
                            prevPoints.clear();
                            if(b != null) {
                                level.addMove();
                            }
                            stepsView.setText("Moves: " + level.getMoves());

                            //Update GridLayout
                            updateBlockViews();


                            //Check for win
                            if (board.isWinningState()) {
                                timer.stop();
                                timeElapsed = SystemClock.elapsedRealtime() - timer.getBase();
                                showWinDialog();
                            }
                        }
                    }
                    return true;
                }
            });
        }
    }

    private void showWinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!");
        int highScore = highScoreManager.getHighScore(level.getLevelNum());

        if (level.getMoves() < highScore || highScore == 0) {
            highScoreManager.setHighScore(level.getLevelNum(), level.getMoves());
            builder.setMessage("You won in " + level.getMoves() + " moves, a new high score!\n\nDuration: " + timeElapsed / 1000 + " seconds");
        }else{
            builder.setMessage("You won in " + level.getMoves() + " moves!\n\nDuration: " + timeElapsed / 1000 + " seconds");
        }

        builder.setPositiveButton("Return to Start Screen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameplayActivity.this, StartScreenActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Restart Level", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameplayActivity.this, GameplayActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    //centerTitle() credit to THEPATEL - https://stackoverflow.com/a/42465387
    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }
                    }