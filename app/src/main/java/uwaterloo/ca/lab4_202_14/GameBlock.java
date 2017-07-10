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
            //Log.d("DEBUG", "myRow: " + Integer.toString(myRow) + " myColumn: " + Integer.toString(myColumn));
            if (myDir == RIGHT) {
                if (myColumn != GameLoopTask.COLUMNS) {

                    GameBlock blockRight = FindNextBlockRight(myRow, myColumn, loopTask);

                    if (blockRight != null) {
                        if (blockRight.getBlockNumber() == blockNumber) {
                            if (blockRight.myColumn == GameLoopTask.COLUMNS) {
                                targetMyColumn = blockRight.myColumn;
                                blockRight.blockNumber = blockNumber * 2;
                                deleteFlag = true;
                            }
                            else if (FindNextBlockRight(blockRight.myRow, blockRight.myColumn, loopTask) == null){
                                targetMyColumn = GameLoopTask.COLUMNS;
                                blockRight.blockNumber = blockNumber * 2;
                                deleteFlag = true;
                            }
                            else {
                                GameBlock blockRight2 = FindNextBlockRight(blockRight.myRow, blockRight.myColumn, loopTask);
                                if (blockRight2.blockNumberTextView.getText() != blockNumberTextView.getText()) {
                                    targetMyColumn = blockRight.myColumn;
                                    blockRight.blockNumber = blockNumber * 2;
                                    deleteFlag = true;
                                } else {
                                    targetMyColumn = blockRight.myColumn - 1;
                                }
                                int space = 0;
                                for (int i = blockRight.myColumn + 1; i <= GameLoopTask.COLUMNS; i++) {
                                    if (loopTask.isOccupied(myRow, i) == null) {
                                        space = space + 1;
                                    }
                                }
                                int change_in_target = 0;
                                GameBlock nextBlock = blockRight;
                                GameBlock nextBlock2 = FindNextBlockRight(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                while (nextBlock2 != null) {
                                    if (nextBlock.blockNumberTextView.getText() == nextBlock2.blockNumberTextView.getText()) {
                                        change_in_target = 1;
                                        break;
                                    }
                                    nextBlock = FindNextBlockRight(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                    nextBlock2 = FindNextBlockRight(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                }
                                targetMyColumn = targetMyColumn + space + change_in_target;
                            }
                        } else {
                            if (blockRight.myColumn != GameLoopTask.COLUMNS) {
                                int space = 0;
                                for (int i = blockRight.myColumn + 1; i <= GameLoopTask.COLUMNS; i++) {
                                    if (loopTask.isOccupied(myRow, i) == null) {
                                        space = space + 1;
                                    }
                                }
                                int change_in_target = 0;
                                GameBlock nextBlock = blockRight;
                                GameBlock nextBlock2 = FindNextBlockRight(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                while (nextBlock2 != null) {
                                    if (nextBlock.blockNumberTextView.getText() == nextBlock2.blockNumberTextView.getText()) {
                                        change_in_target = 1;
                                        break;
                                    }
                                    nextBlock = FindNextBlockRight(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                    nextBlock2 = FindNextBlockRight(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                }
                                targetMyColumn = blockRight.myColumn - 1 + space + change_in_target;
                            } else {
                                targetMyColumn = blockRight.myColumn - 1;
                            }
                        }
                    } else {
                        targetMyColumn = GameLoopTask.COLUMNS;
                    }
                }
            }

            if (myDir == LEFT) {
                if (myColumn != 0) {

                    GameBlock blockLeft = FindNextBlockLeft(myRow, myColumn, loopTask);

                    if (blockLeft != null) {
                        if (blockLeft.getBlockNumber() == blockNumber) {
                            if (blockLeft.myColumn == 0) {
                                targetMyColumn = blockLeft.myColumn;
                                blockLeft.blockNumber = blockNumber * 2;
                                deleteFlag = true;

                            }
                            else if (FindNextBlockLeft(blockLeft.myRow, blockLeft.myColumn, loopTask) == null){
                                targetMyColumn = 0;
                                blockLeft.blockNumber = blockNumber * 2;
                                deleteFlag = true;
                            }
                            else {
                                GameBlock blockLeft2 = FindNextBlockLeft(blockLeft.myRow, blockLeft.myColumn, loopTask);
                                if (blockLeft2.blockNumberTextView.getText() != blockNumberTextView.getText()) {
                                    targetMyColumn = blockLeft.myColumn;
                                    blockLeft.blockNumber = blockNumber * 2;
                                    deleteFlag = true;
                                } else {
                                    targetMyColumn = blockLeft.myColumn + 1;
                                }
                                int space = 0;
                                for (int i = blockLeft.myColumn - 1; i >= 0; i--) {
                                    if (loopTask.isOccupied(myRow, i) == null) {
                                        space = space + 1;
                                    }
                                }
                                int change_in_target = 0;
                                GameBlock nextBlock = blockLeft;
                                GameBlock nextBlock2 = FindNextBlockLeft(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                while (nextBlock2 != null) {
                                    if (nextBlock.blockNumberTextView.getText() == nextBlock2.blockNumberTextView.getText()) {
                                        change_in_target = 1;
                                        break;
                                    }
                                    nextBlock = FindNextBlockLeft(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                    nextBlock2 = FindNextBlockLeft(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                }
                                targetMyColumn = targetMyColumn - space - change_in_target;
                            }
                        } else {
                            if (blockLeft.myColumn != 0) {
                                int space = 0;
                                for (int i = blockLeft.myColumn - 1; i >= 0; i--) {
                                    if (loopTask.isOccupied(myRow, i) == null) {
                                        space = space + 1;
                                    }
                                }
                                int change_in_target = 0;
                                GameBlock nextBlock = blockLeft;
                                GameBlock nextBlock2 = FindNextBlockLeft(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                while (nextBlock2 != null) {
                                    if (nextBlock.blockNumberTextView.getText() == nextBlock2.blockNumberTextView.getText()) {
                                        change_in_target = 1;
                                        break;
                                    }
                                    nextBlock = FindNextBlockLeft(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                    nextBlock2 = FindNextBlockLeft(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                }
                                targetMyColumn = blockLeft.myColumn + 1 - space - change_in_target;
                            } else {
                                targetMyColumn = blockLeft.myColumn + 1;
                            }
                        }
                    } else {
                        targetMyColumn = 0;
                    }
                }
            }

            if (myDir == UP) {
                if (myRow != 0) {

                    GameBlock blockAbove = FindNextBlockUp(myRow, myColumn, loopTask);

                    if (blockAbove != null) {
                        if (blockAbove.getBlockNumber() == blockNumber )  {
                            if (blockAbove.myRow == 0) {
                                targetMyRow = blockAbove.myRow;
                                blockAbove.blockNumber = blockNumber * 2;
                                deleteFlag = true;
                            }
                            else if (FindNextBlockUp(blockAbove.myRow, blockAbove.myColumn, loopTask) == null){
                                targetMyRow = 0;
                                blockAbove.blockNumber = blockNumber * 2;
                                deleteFlag = true;
                            }
                            else {
                                GameBlock blockAbove2 = FindNextBlockUp(blockAbove.myRow, blockAbove.myColumn, loopTask);
                                if (blockAbove2.blockNumberTextView.getText() != blockNumberTextView.getText()) {
                                    targetMyRow = blockAbove.myRow;
                                    blockAbove.blockNumber = blockNumber * 2;
                                    deleteFlag = true;
                                } else {
                                    targetMyRow = blockAbove.myRow + 1;
                                }
                                int space = 0;
                                for (int i = blockAbove.myRow - 1; i >= 0; i--) {
                                    if (loopTask.isOccupied(i, myRow) == null) {
                                        space = space + 1;
                                    }
                                }
                                int change_in_target = 0;
                                GameBlock nextBlock = blockAbove;
                                GameBlock nextBlock2 = FindNextBlockUp(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                while (nextBlock2 != null) {
                                    if (nextBlock.blockNumberTextView.getText() == nextBlock2.blockNumberTextView.getText()) {
                                        change_in_target = 1;
                                        break;
                                    }
                                    nextBlock = FindNextBlockUp(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                    nextBlock2 = FindNextBlockUp(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                }
                                targetMyRow = targetMyRow - space - change_in_target;
                            }
                        } else {
                            if (blockAbove.myRow != 0) {
                                int space = 0;
                                for (int i = blockAbove.myRow - 1; i >= 0; i--) {
                                    if (loopTask.isOccupied(i, myColumn) == null) {
                                        space = space + 1;
                                    }
                                }
                                int change_in_target = 0;
                                GameBlock nextBlock = blockAbove;
                                GameBlock nextBlock2 = FindNextBlockUp(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                while (nextBlock2 != null) {
                                    if (nextBlock.blockNumberTextView.getText() == nextBlock2.blockNumberTextView.getText()) {
                                        change_in_target = 1;
                                        break;
                                    }
                                    nextBlock = FindNextBlockUp(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                    nextBlock2 = FindNextBlockUp(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                }
                                targetMyRow = blockAbove.myRow + 1 - space - change_in_target;
                            } else {
                                targetMyRow = blockAbove.myRow + 1;
                            }
                        }
                    } else {
                        targetMyRow = 0;
                    }
                }
            }

            if (myDir == DOWN) {
                if (myRow != GameLoopTask.ROWS) {

                    GameBlock blockDown = FindNextBlockDown(myRow, myColumn, loopTask);

                    if (blockDown != null) {
                        if (blockDown.getBlockNumber() == blockNumber) {
                            if (blockDown.myRow == GameLoopTask.ROWS) {
                                targetMyRow = blockDown.myRow;
                                blockDown.blockNumber = blockNumber * 2;
                                deleteFlag = true;
                            }
                            else if (FindNextBlockDown(blockDown.myRow, blockDown.myColumn, loopTask) == null){
                                targetMyRow = GameLoopTask.ROWS;
                                blockDown.blockNumber = blockNumber * 2;
                                deleteFlag = true;
                            }
                            else {
                                GameBlock blockDown2 = FindNextBlockDown(blockDown.myRow, blockDown.myColumn, loopTask);
                                if (blockDown2.blockNumberTextView.getText() != blockNumberTextView.getText()) {
                                    targetMyRow = blockDown.myRow;
                                    blockDown.blockNumber = blockNumber * 2;
                                    deleteFlag = true;
                                } else {
                                    targetMyRow = blockDown.myRow - 1;
                                }
                                int space = 0;
                                for (int i = blockDown.myRow + 1; i <= GameLoopTask.ROWS; i++) {
                                    if (loopTask.isOccupied(i, myRow) == null) {
                                        space = space + 1;
                                    }
                                }
                                int change_in_target = 0;
                                GameBlock nextBlock = blockDown;
                                GameBlock nextBlock2 = FindNextBlockDown(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                while (nextBlock2 != null) {
                                    if (nextBlock.blockNumberTextView.getText() == nextBlock2.blockNumberTextView.getText()) {
                                        change_in_target = 1;
                                        break;
                                    }
                                    nextBlock = FindNextBlockDown(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                    nextBlock2 = FindNextBlockDown(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                }
                                targetMyRow = targetMyRow + space + change_in_target;
                            }
                        } else {
                            if (blockDown.myRow != GameLoopTask.ROWS) {
                                int space = 0;
                                for (int i = blockDown.myRow + 1; i <= GameLoopTask.ROWS; i++) {
                                    if (loopTask.isOccupied(i, myColumn) == null) {
                                        space = space + 1;
                                    }
                                }
                                int change_in_target = 0;
                                GameBlock nextBlock = blockDown;
                                GameBlock nextBlock2 = FindNextBlockDown(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                while (nextBlock2 != null) {
                                    if (nextBlock.blockNumberTextView.getText() == nextBlock2.blockNumberTextView.getText()) {
                                        change_in_target = 1;
                                        break;
                                    }
                                    nextBlock = FindNextBlockDown(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                    nextBlock2 = FindNextBlockDown(nextBlock.myRow, nextBlock.myColumn, loopTask);
                                }
                                targetMyRow = blockDown.myRow - 1 + space + change_in_target;
                            } else {
                                targetMyRow = blockDown.myRow - 1;
                            }
                        }
                    } else {
                        targetMyRow = GameLoopTask.ROWS;
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

