package uwaterloo.ca.lab3_202_14;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

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
    public GameBlock newBlock;

    public GameLoopTask(Activity myActivity, Context myContext, RelativeLayout myRL){
        this.myActivity = myActivity;
        gameloopCTX = myContext;
        gameLoopRL = myRL;
        newBlock = createBlock();
        currentGameDirection = gameDirection.NO_MOVEMENT;   //Default direction is no movement
    }

    private GameBlock createBlock(){
        //Creates a new game block object and returns it.
        GameBlock newBlock = new GameBlock(gameloopCTX, 0, 0);
        gameLoopRL.addView(newBlock);       //adds the block to the screen so it is visible
        return newBlock;
    }

    public void setDirection(gameDirection newDirection){
        //Sets the game direction of the game loop task and the block direction
        currentGameDirection = newDirection;                //Sets the gamelooptask direction to the new direction
        newBlock.setBlockDirection(newDirection);           //Sets the blocks direction to the new direction
    }

    @Override
    public void run() {
        this.myActivity.runOnUiThread(   //Runs the timer in the main activity
                new Runnable() {
                    @Override
                    public void run() {
                        newBlock.move(); //Moves the block towards its target every 50 ms
                    }
                }
        );

    }
}
