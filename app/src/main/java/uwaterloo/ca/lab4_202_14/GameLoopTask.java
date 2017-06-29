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
    public gameDirection currentGameDirection;
    public LinkedList<GameBlock> myGBList;

    public GameLoopTask(Activity myActivity, Context myContext, RelativeLayout myRL){
        this.myActivity = myActivity;
        gameloopCTX = myContext;
        gameLoopRL = myRL;
        myGBList = new LinkedList<>();
        createBlock();
        currentGameDirection = gameDirection.NO_MOVEMENT;   //Default direction is no movement
    }

    private void createBlock(){
        //Creates a new game block object and returns it.
        Random rand = new Random();
        int random_x = rand.nextInt(4);
        int random_y = rand.nextInt(4);
        GameBlock newBlock = new GameBlock(gameloopCTX, random_x*256, random_y*256);
        gameLoopRL.addView(newBlock);       //adds the block to the screen so it is visible
        myGBList.add(newBlock);
    }

    public void setDirection(gameDirection newDirection){
        //Sets the game direction of the game loop task and the block direction
        currentGameDirection = newDirection;                //Sets the gamelooptask direction to the new direction
        Log.d("DEBUG","Set Direction Called");
        for(int i = 0;i < myGBList.size();i++){
            myGBList.get(i).setDestination(newDirection);

        }
        createBlock();
    }

    @Override
    public void run() {
        this.myActivity.runOnUiThread(   //Runs the timer in the main activity
                new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0;i < myGBList.size();i++){
                            myGBList.get(i).move();
                        } //Moves the block towards its target every 50 ms
                    }
                }
        );

    }
}
