package uwaterloo.ca.lab4_202_14;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

/**
 * Created by Alex Karner on 2017-06-20.
 */

public class GameLoopTask extends TimerTask{
    private Activity myActivity;
    private Context gameloopCTX;
    private RelativeLayout gameLoopRL;



    public enum gameDirection{UP,DOWN,LEFT,RIGHT,NO_MOVEMENT}
    public static int SLOT_ISOLATION = 256;
    public static int TOP_BOUNDARY = 0;
    public static int LEFT_BOUNDARY = 0;
    public static int ROWS = 3;
    public static int COLUMNS = 3;
    public gameDirection currentGameDirection;
    public LinkedList<GameBlock> myGBList;
    public boolean createBlock;
    public boolean allFinishedMoving;
    public boolean endGameFlag;

    public GameLoopTask(Activity myActivity, Context myContext, RelativeLayout myRL){
        this.myActivity = myActivity;
        gameloopCTX = myContext;
        gameLoopRL = myRL;
        myGBList = new LinkedList<>();
        allFinishedMoving = true;
        createBlock = false;
        currentGameDirection = gameDirection.NO_MOVEMENT;   //Default direction is no movement
        endGameFlag = false;
        createBlock();
    }

    private void createBlock(){
        //Creates a new game block object and returns it.
        Random rand = new Random();
        int allSpaces[][] = new int[4][4];
        for(int i = 0;i < myGBList.size();i++) {
            allSpaces[myGBList.get(i).getRow()][myGBList.get(i).getColumn()] = 1;
        }
        int totalFreeSpaces = 0;
        for(int z = 0; z < 4; z++){
            for(int i = 0; i < 4; i++){
                if (allSpaces[z][i] == 0){
                     totalFreeSpaces = totalFreeSpaces + 1;
                }
            }
        }
        if (totalFreeSpaces == 0){
            endGameFlag = true;
        }
        if (endGameFlag == false) {
            int randomSpot = rand.nextInt(totalFreeSpaces) + 1;
            int currentFreeSpace = 0;
            int random_row = 0;
            int random_column = 0;
            for (int z = 0; z < 4; z++) {
                for (int i = 0; i < 4; i++) {
                    if (allSpaces[z][i] == 0) {
                        currentFreeSpace = currentFreeSpace + 1;
                        if (currentFreeSpace == randomSpot) {
                            random_row = z;
                            random_column = i;
                        }
                    }
                }
            }
            //Log.d("DEBUG", "New Block: Row: " + Integer.toString(random_row) + " Column: " + Integer.toString(random_column));
            GameBlock newBlock = new GameBlock(gameloopCTX, random_row, random_column, gameLoopRL);
            myGBList.add(newBlock);
        }
    }

    public GameBlock isOccupied(int row,int column){
        for(int i = 0;i < myGBList.size();i++) {
            if (myGBList.get(i).getRow() == row & myGBList.get(i).getColumn() == column) {
                return myGBList.get(i);
            }
        }
        return null;
    }

    public void setDirection(gameDirection newDirection){
        //Sets the game direction of the game loop task and the block direction
        if (endGameFlag == false){
            currentGameDirection = newDirection;                //Sets the gamelooptask direction to the new direction
            for(int i = 0;i < myGBList.size();i++){
                myGBList.get(i).setDestination(newDirection,this);

            }
            createBlock = true;
        }
        else{
            Log.d("DEBUG","END OF GAME");
        }

    }

    @Override
    public void run() {
        this.myActivity.runOnUiThread(   //Runs the timer in the main activity
                new Runnable() {
                    @Override
                    public void run() {
                        allFinishedMoving = true;
                        for(int i = 0;i < myGBList.size();i++){
                            if (myGBList.get(i).shouldDestory()){
                                myGBList.get(i).destroyBlock();
                                myGBList.remove(i);
                            }
                            try {
                                myGBList.get(i).move();
                            }
                            catch(IndexOutOfBoundsException e){}
                            if (myGBList.get(i).targetReached() == false){
                                allFinishedMoving = false;
                            }
                            if (myGBList.get(i).getTextBlockNumber() == 2048){
                                endGameFlag = true;
                            }
                        } //Moves the block towards its target every 50 ms
                        if (allFinishedMoving & createBlock){
                            for(int i = 0;i < myGBList.size();i++) {
                                myGBList.get(i).updateBlockNumber();
                            }
                            createBlock();
                            createBlock = false;
                        }
                    }

                }
        );

    }
}
