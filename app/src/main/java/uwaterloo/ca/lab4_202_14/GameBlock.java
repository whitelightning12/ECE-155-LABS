package uwaterloo.ca.lab4_202_14;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import static uwaterloo.ca.lab4_202_14.GameLoopTask.SLOT_ISOLATION;
import static uwaterloo.ca.lab4_202_14.GameLoopTask.gameDirection.*;

/**
 * Created by Alex Karner on 2017-06-21.
 */

public class GameBlock extends GameBlockTemplate {
    //This class is the block object which is controlled by hand gestures.
    private int myRow;
    private int myColumn;
    private int targetMyRow;
    private int targetMyColumn;
    private int myCoordX;
    private int myCoordY;
    private GameLoopTask.gameDirection myDir;
    private int V;
    private final int A = 2;
    private RelativeLayout gameBlockRL;
    public int blockNumber;
    private TextView blockNumberTextView;
    public boolean deleteFlag;

    public GameBlock(Context myContext, int row, int column, RelativeLayout myRL){
        super(myContext);
        myRow = row;
        myColumn = column;
        targetMyRow = row;
        targetMyColumn = column;
        V = 0;
        Random rand = new Random();
        blockNumber = (rand.nextInt(2)+1)*2;
        blockNumberTextView = new TextView(myContext.getApplicationContext());
        blockNumberTextView.setText(String.valueOf(blockNumber));
        blockNumberTextView.bringToFront();
        blockNumberTextView.setTextSize(50);
        blockNumberTextView.setTextColor(Color.BLACK);
        gameBlockRL = myRL;
        gameBlockRL.addView(this);                      //adds the block to the screen so it is visible
        gameBlockRL.addView(blockNumberTextView);
        blockNumberTextView.setX(myColumn*SLOT_ISOLATION+85);
        blockNumberTextView.setY(myRow*SLOT_ISOLATION+30);
        myCoordX = myColumn*SLOT_ISOLATION;
        myCoordY = myRow*SLOT_ISOLATION;
        this.setX(myCoordX);                            //Sets the x value of the picture location on the screen
        this.setY(myCoordY);                            //Sets the y value of the picture location on the screen
        this.setImageResource(R.drawable.gameblock);    //Sets the Imageresource to the correct PNG file
        deleteFlag = false;
    }

    public GameBlock FindNextBlockRight(int row,int column, GameLoopTask loopTask){
        for(int i = column+1; i <= GameLoopTask.COLUMNS; i++){
            //Log.d("DEBUG", "Row: " + Integer.toString(row) + " Column: " + Integer.toString(i));
            GameBlock blockRight = loopTask.isOccupied(row,i);
            if (blockRight != null) {
                //Log.d("DEBUG", "Row: " + Integer.toString(blockRight.getColumn()) + " Column: " + Integer.toString(blockRight.getColumn()) + "   " + Integer.toString(blockRight.blockNumber));
                return blockRight;
            }
        }
        return null;
    }

    public GameBlock FindNextBlockLeft(int row,int column, GameLoopTask loopTask){
        for(int i = column-1; i >= 0; i--){
            //Log.d("DEBUG", "Row: " + Integer.toString(row) + " Column: " + Integer.toString(i));
            GameBlock blockLeft = loopTask.isOccupied(row,i);
            if (blockLeft != null) {
                //Log.d("DEBUG", "Row: " + Integer.toString(blockLeft.getColumn()) + " Column: " + Integer.toString(blockLeft.getColumn()) + "   " + Integer.toString(blockLeft.blockNumber));
                return blockLeft;
            }
        }
        return null;
    }

    public GameBlock FindNextBlockDown(int row,int column, GameLoopTask loopTask){
        for(int i = row+1; i <= GameLoopTask.ROWS; i++){
            //Log.d("DEBUG", "Row: " + Integer.toString(row) + " Column: " + Integer.toString(i));
            GameBlock blockDown = loopTask.isOccupied(i,column);
            if (blockDown != null) {
                //Log.d("DEBUG", "Row: " + Integer.toString(blockDown.getColumn()) + " Column: " + Integer.toString(blockDown.getColumn()) + "   " + Integer.toString(blockDown.blockNumber));
                return blockDown;
            }
        }
        return null;
    }

    public GameBlock FindNextBlockUp(int row,int column, GameLoopTask loopTask){
        for(int i = row-1; i >= 0; i--){
            //Log.d("DEBUG", "Row: " + Integer.toString(row) + " Column: " + Integer.toString(i));
            GameBlock blockUp = loopTask.isOccupied(i,column);
            if (blockUp != null) {
                //Log.d("DEBUG", "Row: " + Integer.toString(blockUp.getColumn()) + " Column: " + Integer.toString(blockUp.getColumn()) + "   " + Integer.toString(blockUp.blockNumber));
                return blockUp;
            }
        }
        return null;
    }

    public void setDestination(GameLoopTask.gameDirection newDir,GameLoopTask loopTask) {
        //Sets the target X and Y coordinates of the block from 4 standard direction (UP,LEFT,RIGHT,DOWN)

        if (loopTask.allFinishedMoving) {   //Only accepts a new direction once the current target has been meet for all blocks
            myDir = newDir;
            int pairs = 0;
            int blocksInWay = 0;
            boolean pairAfter = false;
            boolean pair = false;
            if (myDir == RIGHT) {
                GameBlock currentBlock = FindNextBlockLeft(myRow, GameLoopTask.COLUMNS+1, loopTask);
                GameBlock nextBlock = FindNextBlockLeft(currentBlock.myRow, currentBlock.myColumn, loopTask);
                while (currentBlock != null) {
                    if (nextBlock != null) {
                        if (currentBlock.blockNumberTextView.getText() == nextBlock.blockNumberTextView.getText() & !pair) {
                            pairs += 1;
                            pair = true;
                            if (myColumn < nextBlock.myColumn) {
                                pairAfter = true;
                            }
                        } else {
                            pair = false;
                        }
                    }
                    if (currentBlock.myColumn > myColumn) {
                        blocksInWay += 1;
                    }
                    currentBlock = nextBlock;
                    if (currentBlock != null) {
                        nextBlock = FindNextBlockLeft(currentBlock.myRow, currentBlock.myColumn, loopTask);
                    }
                }
                Log.d("DEBUG", "Pairs: " + Integer.toString(pairs) + " blocksInWay: " + Integer.toString(blocksInWay) + " pairAfter: " + Boolean.toString(pairAfter) );
                if (pairs == 0) {
                    targetMyColumn = GameLoopTask.COLUMNS - blocksInWay;
                }
                else if (pairs == 1) {
                    GameBlock blockRight = FindNextBlockRight(myRow, myColumn, loopTask);
                    if (blockRight == null) {
                        targetMyColumn = GameLoopTask.COLUMNS;
                    }
                    else if (pairAfter) {
                        targetMyColumn = GameLoopTask.COLUMNS + 1 - blocksInWay;
                    }
                    else if (blockRight.blockNumberTextView.getText() == blockNumberTextView.getText()) {
                        targetMyColumn = GameLoopTask.COLUMNS + 1 - blocksInWay;
                        blockRight.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else {
                        targetMyColumn = GameLoopTask.COLUMNS - blocksInWay;
                    }
                }
                else if (pairs == 2) {
                    GameBlock blockRight = FindNextBlockRight(myRow, myColumn, loopTask);
                    if (myColumn == 0) {
                        targetMyColumn = GameLoopTask.COLUMNS - 1;
                        blockRight.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else if (myColumn == 1) {
                        targetMyColumn = GameLoopTask.COLUMNS - 1;
                    }
                    else if (myColumn == 2) {
                        targetMyColumn = GameLoopTask.COLUMNS;
                        blockRight.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                }
            }
            if (myDir == LEFT) {
                GameBlock currentBlock = FindNextBlockRight(myRow, -1, loopTask);
                GameBlock nextBlock = FindNextBlockRight(currentBlock.myRow, currentBlock.myColumn, loopTask);
                while (currentBlock != null) {
                    if (nextBlock != null) {
                        if (currentBlock.blockNumberTextView.getText() == nextBlock.blockNumberTextView.getText() & !pair) {
                            pairs += 1;
                            pair = true;
                            if (myColumn > nextBlock.myColumn) {
                                pairAfter = true;
                            }
                        } else {
                            pair = false;
                        }
                    }
                    if (currentBlock.myColumn < myColumn) {
                        blocksInWay += 1;
                    }
                    currentBlock = nextBlock;
                    if (currentBlock != null) {
                        nextBlock = FindNextBlockRight(currentBlock.myRow, currentBlock.myColumn, loopTask);
                    }
                }
                Log.d("DEBUG", "Pairs: " + Integer.toString(pairs) + " blocksInWay: " + Integer.toString(blocksInWay) + " pairAfter: " + Boolean.toString(pairAfter) );
                if (pairs == 0) {
                    targetMyColumn = blocksInWay;
                }
                else if (pairs == 1) {
                    GameBlock blockLeft = FindNextBlockLeft(myRow, myColumn, loopTask);
                    if (blockLeft == null) {
                        targetMyColumn = 0;
                    }
                    else if (pairAfter) {
                        targetMyColumn = blocksInWay-1;
                    }
                    else if (blockLeft.blockNumberTextView.getText() == blockNumberTextView.getText()) {
                        targetMyColumn = blocksInWay-1;
                        blockLeft.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else {
                        targetMyColumn = blocksInWay;
                    }
                }
                else if (pairs == 2) {
                    GameBlock blockLeft = FindNextBlockLeft(myRow, myColumn, loopTask);
                    if (myColumn == 3) {
                        targetMyColumn = 1;
                        blockLeft.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else if (myColumn == 2) {
                        targetMyColumn = 1;
                    }
                    else if (myColumn == 1) {
                        targetMyColumn = 0;
                        blockLeft.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                }
            }

            if (myDir == UP) {
                GameBlock currentBlock = FindNextBlockDown(-1, myColumn, loopTask);
                GameBlock nextBlock = FindNextBlockDown(currentBlock.myRow, currentBlock.myColumn, loopTask);
                while (currentBlock != null) {
                    if (nextBlock != null) {
                        if (currentBlock.blockNumberTextView.getText() == nextBlock.blockNumberTextView.getText() & !pair) {
                            pairs += 1;
                            pair = true;
                            if (myRow > nextBlock.myRow) {
                                pairAfter = true;
                            }
                        } else {
                            pair = false;
                        }
                    }
                    if (currentBlock.myRow < myRow) {
                        blocksInWay += 1;
                    }
                    currentBlock = nextBlock;
                    if (currentBlock != null) {
                        nextBlock = FindNextBlockDown(currentBlock.myRow, currentBlock.myColumn, loopTask);
                    }
                }
                Log.d("DEBUG", "Pairs: " + Integer.toString(pairs) + " blocksInWay: " + Integer.toString(blocksInWay) + " pairAfter: " + Boolean.toString(pairAfter) );
                if (pairs == 0) {
                    targetMyRow = blocksInWay;
                }
                else if (pairs == 1) {
                    GameBlock blockUp = FindNextBlockUp(myRow, myColumn, loopTask);
                    if (blockUp == null) {
                        targetMyRow = 0;
                    }
                    else if (pairAfter) {
                        targetMyRow = blocksInWay-1;
                    }
                    else if (blockUp.blockNumberTextView.getText() == blockNumberTextView.getText()) {
                        targetMyRow = blocksInWay-1;
                        blockUp.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else {
                        targetMyRow = blocksInWay;
                    }
                }
                else if (pairs == 2) {
                    GameBlock blockUp = FindNextBlockUp(myRow, myColumn, loopTask);
                    if (myRow == 3) {
                        targetMyRow = 1;
                        blockUp.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else if (myRow == 2) {
                        targetMyRow = 1;
                    }
                    else if (myRow == 1) {
                        targetMyRow = 0;
                        blockUp.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                }
            }

            if (myDir == DOWN) {
                GameBlock currentBlock = FindNextBlockUp(GameLoopTask.ROWS+1, myColumn, loopTask);
                GameBlock nextBlock = FindNextBlockUp(currentBlock.myRow, currentBlock.myColumn, loopTask);
                while (currentBlock != null) {
                    if (nextBlock != null) {
                        if (currentBlock.blockNumberTextView.getText() == nextBlock.blockNumberTextView.getText() & !pair) {
                            pairs += 1;
                            pair = true;
                            if (myRow < nextBlock.myRow) {
                                pairAfter = true;
                            }
                        } else {
                            pair = false;
                        }
                    }
                    if (currentBlock.myRow > myRow) {
                        blocksInWay += 1;
                    }
                    currentBlock = nextBlock;
                    if (currentBlock != null) {
                        nextBlock = FindNextBlockUp(currentBlock.myRow, currentBlock.myColumn, loopTask);
                    }
                }
                Log.d("DEBUG", "Pairs: " + Integer.toString(pairs) + " blocksInWay: " + Integer.toString(blocksInWay) + " pairAfter: " + Boolean.toString(pairAfter) );
                if (pairs == 0) {
                    targetMyRow = GameLoopTask.ROWS - blocksInWay;
                }
                else if (pairs == 1) {
                    GameBlock blockDown = FindNextBlockDown(myRow, myColumn, loopTask);
                    if (blockDown == null) {
                        targetMyRow = GameLoopTask.ROWS;
                    }
                    else if (pairAfter) {
                        targetMyRow = GameLoopTask.ROWS-blocksInWay+1;
                    }
                    else if (blockDown.blockNumberTextView.getText() == blockNumberTextView.getText()) {
                        targetMyRow = GameLoopTask.ROWS-blocksInWay+1;
                        blockDown.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else {
                        targetMyRow = GameLoopTask.ROWS-blocksInWay;
                    }
                }
                else if (pairs == 2) {
                    GameBlock blockDown = FindNextBlockDown(myRow, myColumn, loopTask);
                    if (myRow == 0) {
                        targetMyRow = GameLoopTask.ROWS - 1;
                        blockDown.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else if (myRow == 1) {
                        targetMyRow = GameLoopTask.ROWS - 1;
                    }
                    else if (myRow == 2) {
                        targetMyRow = GameLoopTask.ROWS;
                        blockDown.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                }
            }
        }
    }

    public void move(){
        //Function moves the block towards the target x or y coord with the current speed adding the acceleration constant every time the function is called
        if ((myCoordX < targetMyColumn*SLOT_ISOLATION & myCoordX+V > targetMyColumn*SLOT_ISOLATION)|(myCoordY < targetMyRow*SLOT_ISOLATION & myCoordY+V > targetMyRow*SLOT_ISOLATION)|    //Checks whether the next location of the block is pass the target
            (myCoordX > targetMyColumn*SLOT_ISOLATION & myCoordX-V < targetMyColumn*SLOT_ISOLATION)|(myCoordY > targetMyRow*SLOT_ISOLATION & myCoordY-V < targetMyRow*SLOT_ISOLATION)|    //Will set the block to the target if it is and set speed to 0.
            (myCoordX == targetMyColumn*SLOT_ISOLATION & myCoordY == targetMyRow*SLOT_ISOLATION)){
            V = 0;
            myCoordX = targetMyColumn*SLOT_ISOLATION;
            myCoordY = targetMyRow*SLOT_ISOLATION;
            myColumn = targetMyColumn;
            myRow = targetMyRow;
        }

        if (myCoordX < targetMyColumn*SLOT_ISOLATION){             //If the x coord is less then target x coord add the current speed to the block x coord to move it closer to target
            myCoordX = myCoordX + V;
        }
        else if (myCoordX > targetMyColumn*SLOT_ISOLATION){        //If the x coord is greater then target x coord subtract the current speed from the block x coord to move it closer to target
            myCoordX = myCoordX - V;
        }
        else if (myCoordY < targetMyRow*SLOT_ISOLATION){         //If the y coord is less then target y coord add the current speed to the block y coord to move it closer to target
            myCoordY = myCoordY + V;
        }
        else if (myCoordY > targetMyRow*SLOT_ISOLATION){         //If the y coord is greater then target y coord subtract the current speed from the block y coord to move it closer to target
            myCoordY = myCoordY - V;
        }
        V = V + A;                          //Increase the speed by the constant acceleration declared
        this.setX(myCoordX);                //Set the block x picture to the updated coordinate
        this.setY(myCoordY);                //Set the block y picture to the updated coordinate
        blockNumberTextView.setX(myCoordX+85);
        blockNumberTextView.setY(myCoordY+30);

    }

    public int getRow(){
        return myRow;
    }

    public int getColumn(){
        return myColumn;
    }

    public int getBlockNumber(){
        return blockNumber;
    }
    public int getTextBlockNumber(){
        return Integer.parseInt(blockNumberTextView.getText().toString());
    }

    public boolean shouldDestory(){
        if (deleteFlag & myColumn == targetMyColumn & myRow == targetMyRow){
            return true;
        }
        else {
            return false;
        }
    }

    public void destroyBlock(){
        gameBlockRL.removeView(this);
        gameBlockRL.removeView(blockNumberTextView);
    }

    public boolean targetReached(){
        if (myColumn == targetMyColumn & myRow == targetMyRow){
            return true;
        }
        return false;
    }

    public void updateBlockNumber(){
        blockNumberTextView.setText(String.valueOf(blockNumber));
    }
}

