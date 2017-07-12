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
        blockNumber = (rand.nextInt(2)+1)*2;            //Randomizes block number to be either 2 and 4
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
        //Finds the block to the right of the inputted row and column
        for(int i = column+1; i <= GameLoopTask.COLUMNS; i++){      //For loop goes through all columns to the right of block inputted
            GameBlock blockRight = loopTask.isOccupied(row,i);      //Sees if the current block in the for loop is occupied
            if (blockRight != null) {                               //If the current block to the right is there return it
                return blockRight;
            }
        }
        return null;                                                //else return null
    }

    public GameBlock FindNextBlockLeft(int row,int column, GameLoopTask loopTask){
        //Finds the block to the left of the inputted row and column
        for(int i = column-1; i >= 0; i--){                         //For loop goes through all columns to the left of block inputted
            GameBlock blockLeft = loopTask.isOccupied(row,i);       //Sees if the current block in the for loop is occupied
            if (blockLeft != null) {                                //If the current block to the left is there return it
                return blockLeft;
            }
        }
        return null;                                                //else return null
    }

    public GameBlock FindNextBlockDown(int row,int column, GameLoopTask loopTask){
        //Finds the block below the inputted row and column
        for(int i = row+1; i <= GameLoopTask.ROWS; i++){            //For loop goes through all rows below block inputted
            GameBlock blockDown = loopTask.isOccupied(i,column);    //Sees if the current block in for loop is occupied
            if (blockDown != null) {                                //If the current block below is there return it
                return blockDown;
            }
        }
        return null;                                                //else return null
    }

    public GameBlock FindNextBlockUp(int row,int column, GameLoopTask loopTask){
        //Finds the block above the inputted row and column
        for(int i = row-1; i >= 0; i--){                            //For loop goes through all rows above block inputted
            GameBlock blockUp = loopTask.isOccupied(i,column);      //Sees if the current block in for loop is occupied
            if (blockUp != null) {                                  //If the current block above is there return it
                return blockUp;
            }
        }
        return null;                                                //else return null
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
                GameBlock currentBlock = FindNextBlockLeft(myRow, GameLoopTask.COLUMNS+1, loopTask);                 //Finds first block at the right of grid in a certain row
                GameBlock nextBlock = FindNextBlockLeft(currentBlock.myRow, currentBlock.myColumn, loopTask);        //Finds block to the left of current block
                while (currentBlock != null) {                                                                       //while there is still a block in this row
                    if (nextBlock != null) {                                                                         //If there is still a block next to current block
                        //If the current block number is the same as the next block number and the last two blocks were not a pair
                        if (currentBlock.blockNumberTextView.getText() == nextBlock.blockNumberTextView.getText() & !pair) {
                            pairs += 1;                                                                              //Add to the number of pairs
                            pair = true;
                            if (myColumn < nextBlock.myColumn) {                                                     //If myblock is before next block then there is a pair after current block
                                pairAfter = true;
                            }
                        } else {
                            pair = false;
                        }
                    }
                    if (currentBlock.myColumn > myColumn) {                                                         //If current block is greater than my column that add to the number of blocks in way
                        blocksInWay += 1;
                    }
                    currentBlock = nextBlock;
                    if (currentBlock != null) {
                        nextBlock = FindNextBlockLeft(currentBlock.myRow, currentBlock.myColumn, loopTask);         //Finds block to the left of current block as long as current block exists
                    }
                }
                if (pairs == 0) {                                                                                   //If there are no pairs then set my target to be the right side of grid minus blocks in way
                    targetMyColumn = GameLoopTask.COLUMNS - blocksInWay;
                }
                else if (pairs == 1) {                                                                              //else if pairs is equal to 1
                    GameBlock blockRight = FindNextBlockRight(myRow, myColumn, loopTask);                           //Find block right of my block
                    if (blockRight == null) {                                                                       //If there is no block to the right then set my target to column 3
                        targetMyColumn = GameLoopTask.COLUMNS;
                    }
                    else if (pairAfter) {                                                                           //else if there is a pair to the right set target column to column 3 - blocks in way + 1
                        targetMyColumn = GameLoopTask.COLUMNS + 1 - blocksInWay;
                    }
                    else if (blockRight.blockNumberTextView.getText() == blockNumberTextView.getText()) {           //else if the block to the right has same number combine with it
                        targetMyColumn = GameLoopTask.COLUMNS + 1 - blocksInWay;
                        blockRight.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else {                                                                                          //else there are blocks in way so set my target to the left of them
                        targetMyColumn = GameLoopTask.COLUMNS - blocksInWay;
                    }
                }
                else if (pairs == 2) {                                                                              //If pairs equals two
                    GameBlock blockRight = FindNextBlockRight(myRow, myColumn, loopTask);                           //Find block right of my block
                    if (myColumn == 0) {                                                                            //If my Column equals 0
                        targetMyColumn = GameLoopTask.COLUMNS - 1;                                                  //Then set target to total columns - 1
                        blockRight.blockNumber = blockNumber * 2;                                                   //double the block number of the block to the right
                        deleteFlag = true;                                                                          //set delete flag
                    }
                    else if (myColumn == 1) {                                                                       //else if my column equals 1
                        targetMyColumn = GameLoopTask.COLUMNS - 1;                                                  //Then set target to total columns - 1
                    }
                    else if (myColumn == 2) {                                                                       //else if my column equals 2
                        targetMyColumn = GameLoopTask.COLUMNS;                                                      //Then set target to total columns
                        blockRight.blockNumber = blockNumber * 2;                                                   //double the block number of the block to the right
                        deleteFlag = true;                                                                          //set delete flag
                    }
                }
            }
            if (myDir == LEFT) {
                GameBlock currentBlock = FindNextBlockRight(myRow, -1, loopTask);                                   //Finds first block at the left of grid in a certain row
                GameBlock nextBlock = FindNextBlockRight(currentBlock.myRow, currentBlock.myColumn, loopTask);      //Finds block to the right of current block
                while (currentBlock != null) {                                                                      //while there is still a block in this row
                    if (nextBlock != null) {                                                                        //If there is still a block next to current block
                        //If the current block number is the same as the next block number and the last two blocks were not a pair
                        if (currentBlock.blockNumberTextView.getText() == nextBlock.blockNumberTextView.getText() & !pair) {
                            pairs += 1;                                                                             //Add to the number of pairs
                            pair = true;
                            if (myColumn > nextBlock.myColumn) {                                                    //If myblock is before next block then there is a pair after current block
                                pairAfter = true;
                            }
                        } else {
                            pair = false;
                        }
                    }
                    if (currentBlock.myColumn < myColumn) {                                                         //If current block column is less than my column that add to the number of blocks
                        blocksInWay += 1;
                    }
                    currentBlock = nextBlock;
                    if (currentBlock != null) {
                        nextBlock = FindNextBlockRight(currentBlock.myRow, currentBlock.myColumn, loopTask);        //Finds block to the right of current block as long as current block exists
                    }
                }
                if (pairs == 0) {                                                                                   //If there are no pairs then set my target to be the left side of grid plus blocks in way
                    targetMyColumn = blocksInWay;
                }
                else if (pairs == 1) {                                                                              //else if pairs is equal to 1
                    GameBlock blockLeft = FindNextBlockLeft(myRow, myColumn, loopTask);                             //Find block left of my block
                    if (blockLeft == null) {                                                                        //If there is no block to the learn then set my target to column 0
                        targetMyColumn = 0;
                    }
                    else if (pairAfter) {                                                                           //else if there is a pair to the left set target column to blocks in way minus 1
                        targetMyColumn = blocksInWay-1;
                    }
                    else if (blockLeft.blockNumberTextView.getText() == blockNumberTextView.getText()) {            //else if the block to the left has same number combine with it
                        targetMyColumn = blocksInWay-1;
                        blockLeft.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else {                                                                                          //else there are blocks in way so set my target to the right of them
                    targetMyColumn = blocksInWay;
                    }
                }
                else if (pairs == 2) {                                                                              //If pairs equals two
                    GameBlock blockLeft = FindNextBlockLeft(myRow, myColumn, loopTask);                             //Find block left of my block
                    if (myColumn == 3) {                                                                            //If my Column equals 3
                        targetMyColumn = 1;                                                                         //Then set target to 1
                        blockLeft.blockNumber = blockNumber * 2;                                                    //double the block number of the block to the left
                        deleteFlag = true;                                                                          //set delete flag
                    }
                    else if (myColumn == 2) {                                                                       //else if my column equals 2
                        targetMyColumn = 1;                                                                         //Then set target to 1
                    }
                    else if (myColumn == 1) {                                                                       //else if my column equals 1
                        targetMyColumn = 0;                                                                         //Then set target to 0
                        blockLeft.blockNumber = blockNumber * 2;                                                    //double the block number of the block to the left
                        deleteFlag = true;                                                                          //set delete flag
                    }
                }
            }

            if (myDir == UP) {
                GameBlock currentBlock = FindNextBlockDown(-1, myColumn, loopTask);                                 //Finds first block at the top of grid in a certain column
                GameBlock nextBlock = FindNextBlockDown(currentBlock.myRow, currentBlock.myColumn, loopTask);       //Find block below current block
                while (currentBlock != null) {                                                                      //while there is still a block in this column
                    if (nextBlock != null) {                                                                        //If there is still a block next to current block
                        //If the current block number is the same as the next block number and the last two blocks were not a pair
                        if (currentBlock.blockNumberTextView.getText() == nextBlock.blockNumberTextView.getText() & !pair) {
                            pairs += 1;                                                                             //Add to the number of pairs
                            pair = true;
                            if (myRow > nextBlock.myRow) {                                                          //If myblock is before next block then there is a pair after current block
                                pairAfter = true;
                            }
                        } else {
                            pair = false;
                        }
                    }
                    if (currentBlock.myRow < myRow) {                                                               //If current block row is less than my row that add to the number of blocks
                        blocksInWay += 1;
                    }
                    currentBlock = nextBlock;
                    if (currentBlock != null) {
                        nextBlock = FindNextBlockDown(currentBlock.myRow, currentBlock.myColumn, loopTask);         //Finds block below current block as long as current block exists
                    }
                }
                if (pairs == 0) {                                                                                   //If there are no pairs then set my target to be the top side of grid plus blocks in way
                    targetMyRow = blocksInWay;
                }
                else if (pairs == 1) {                                                                              //else if pairs is equal to 1
                    GameBlock blockUp = FindNextBlockUp(myRow, myColumn, loopTask);                                 //Find block above of my block
                    if (blockUp == null) {                                                                          //If there is no block above then set my target to row 0
                        targetMyRow = 0;
                    }
                    else if (pairAfter) {                                                                           //else if there is a pair above set target row to blocks in way minus 1
                        targetMyRow = blocksInWay-1;
                    }
                    else if (blockUp.blockNumberTextView.getText() == blockNumberTextView.getText()) {              //else if the block above has same number combine with it
                        targetMyRow = blocksInWay-1;
                        blockUp.blockNumber = blockNumber * 2;
                        deleteFlag = true;                                                                          //set delete flag
                    }
                    else {                                                                                          //else there are blocks in way so set my target below them
                        targetMyRow = blocksInWay;
                    }
                }
                else if (pairs == 2) {                                                                              //If pairs equals two
                    GameBlock blockUp = FindNextBlockUp(myRow, myColumn, loopTask);                                 //Find block above my block
                    if (myRow == 3) {                                                                               //If my row equals 3
                        targetMyRow = 1;                                                                            //Then set target to 1
                        blockUp.blockNumber = blockNumber * 2;                                                      //double the block number of the block above
                        deleteFlag = true;                                                                          //set delete flag
                    }
                    else if (myRow == 2) {                                                                          //else if my row equals 2
                        targetMyRow = 1;                                                                            //Then set target to 1
                    }
                    else if (myRow == 1) {                                                                          //else if my row equals 1
                        targetMyRow = 0;                                                                            //Then set target to 0
                        blockUp.blockNumber = blockNumber * 2;                                                      //double the block number of the block above
                        deleteFlag = true;                                                                          //set delete flag
                    }
                }
            }

            if (myDir == DOWN) {
                GameBlock currentBlock = FindNextBlockUp(GameLoopTask.ROWS+1, myColumn, loopTask);                  //Finds first block at the bottom of grid in a certain column
                GameBlock nextBlock = FindNextBlockUp(currentBlock.myRow, currentBlock.myColumn, loopTask);         //Find block above current block
                while (currentBlock != null) {                                                                      //while there is still a block in this column
                    if (nextBlock != null) {                                                                        //If there is still a block next to current block
                        //If the current block number is the same as the next block number and the last two blocks were not a pair
                        if (currentBlock.blockNumberTextView.getText() == nextBlock.blockNumberTextView.getText() & !pair) {
                            pairs += 1;                                                                             //Add to the number of pairs
                            pair = true;
                            if (myRow < nextBlock.myRow) {                                                          //If myblock is before next block then there is a pair after current block
                                pairAfter = true;
                            }
                        } else {
                            pair = false;
                        }
                    }
                    if (currentBlock.myRow > myRow) {                                                               //If current block row is greater than my row that add to the number of blocks
                        blocksInWay += 1;
                    }
                    currentBlock = nextBlock;
                    if (currentBlock != null) {
                        nextBlock = FindNextBlockUp(currentBlock.myRow, currentBlock.myColumn, loopTask);           //Finds block below current block as long as current block exists
                    }
                }
                if (pairs == 0) {                                                                                   //If there are no pairs then set my target to be the bottom of grid minus blocks in way
                    targetMyRow = GameLoopTask.ROWS - blocksInWay;
                }
                else if (pairs == 1) {                                                                              //else if pairs is equal to 1
                    GameBlock blockDown = FindNextBlockDown(myRow, myColumn, loopTask);                             //Finds block below of my block
                    if (blockDown == null) {                                                                        //If there is no block to the below then set my target to total number of rows
                        targetMyRow = GameLoopTask.ROWS;
                    }
                    else if (pairAfter) {                                                                           //else if there is a pair to the below set target row to total rows - blocks in way + 1
                        targetMyRow = GameLoopTask.ROWS-blocksInWay+1;
                    }
                    else if (blockDown.blockNumberTextView.getText() == blockNumberTextView.getText()) {            //else if the block below has same number combine with it
                        targetMyRow = GameLoopTask.ROWS-blocksInWay+1;
                        blockDown.blockNumber = blockNumber * 2;
                        deleteFlag = true;
                    }
                    else {                                                                                          //else there are blocks in way so set my target above them
                        targetMyRow = GameLoopTask.ROWS-blocksInWay;
                    }
                }
                else if (pairs == 2) {                                                                              //If pairs equals two
                    GameBlock blockDown = FindNextBlockDown(myRow, myColumn, loopTask);                             //Find block below my block
                    if (myRow == 0) {                                                                               //If my Column equals 3
                        targetMyRow = GameLoopTask.ROWS - 1;                                                        //Then set target to total rows - 1
                        blockDown.blockNumber = blockNumber * 2;                                                    //double the block number of the block below
                        deleteFlag = true;                                                                          //set delete flag
                    }
                    else if (myRow == 1) {                                                                          //else if my row equals 1
                        targetMyRow = GameLoopTask.ROWS - 1;                                                        //Then set target to total rows - 1
                    }
                    else if (myRow == 2) {                                                                          //else if my row equals 1
                        targetMyRow = GameLoopTask.ROWS;                                                            //Set target row to total number of rows
                        blockDown.blockNumber = blockNumber * 2;                                                    //double the block number of the block below
                        deleteFlag = true;                                                                          //set delete flag
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
        //Returns current blocks row
        return myRow;
    }

    public int getColumn(){
        //Returns current blocks column
        return myColumn;
    }
    public int getTextBlockNumber(){
        //returns current blocks textview number
        return Integer.parseInt(blockNumberTextView.getText().toString());
    }

    public boolean shouldDestory(){
        //Determines whether block should be destroyed
        if (deleteFlag & myColumn == targetMyColumn & myRow == targetMyRow){
            return true;
        }
        else {
            return false;
        }
    }

    public void destroyBlock(){
        //Removes block from view
        gameBlockRL.removeView(this);
        gameBlockRL.removeView(blockNumberTextView);
    }

    public boolean targetReached(){
        //Determines if block has reached target and returns result
        if (myColumn == targetMyColumn & myRow == targetMyRow){
            return true;
        }
        return false;
    }

    public void updateBlockNumber(){
        //Updates block number text view
        blockNumberTextView.setText(String.valueOf(blockNumber));
    }
}

