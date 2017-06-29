package uwaterloo.ca.lab4_202_14;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Alex Karner on 2017-06-21.
 */

public class GameBlock extends GameBlockTemplate {
    //This class is the block object which is controlled by hand gestures.
    //private final float IMAGE_SCALE = 1f;         //How much to scale the block picture to match background
    private final  int RIGHT_BORDER = 768;  //Max distance block can move right 490
    private final  int LEFT_BORDER = 0;     //Max distance block can move left
    private final  int BOTTOM_BORDER = 768; //Max distance block can move down 490
    private final  int TOP_BORDER = 0;      //Max distance block can move up

    private int myCoordX;
    private int myCoordY;
    private int targetmyCoordX;
    private int targetmyCoordY;
    private GameLoopTask.gameDirection myDir;
    private int V;
    private final int A = 2;

    public GameBlock(Context myContext, int coordX, int coordY){
        super(myContext);
        myCoordX = coordX;
        myCoordY = coordY;
        targetmyCoordX = myCoordX;
        targetmyCoordY = myCoordY;
        V = 0;
        this.setX(myCoordX);                            //Sets the x value of the picture location on the screen
        this.setY(myCoordY);                            //Sets the y value of the picture location on the screen
        this.setImageResource(R.drawable.gameblock);    //Sets the Imageresource to the correct PNG file
    }

    public void setDestination(GameLoopTask.gameDirection newDir){
        //Sets the target X and Y coordinates of the block from 4 standard direction (UP,LEFT,RIGHT,DOWN)
        if (myCoordX == targetmyCoordX & myCoordY == targetmyCoordY) {   //Only accepts a new direction once the current target has been meet
            myDir = newDir;
            if (myDir == GameLoopTask.gameDirection.RIGHT) {
                targetmyCoordX = RIGHT_BORDER;                  //Sets the target x coord to the RIGHT_BORDER if RIGHT enum is inputted
            }
            if (myDir == GameLoopTask.gameDirection.LEFT) {
                targetmyCoordX = LEFT_BORDER;                   //Sets the target x coord to the LEFT_BORDER if LEFT enum is inputted
            }
            if (myDir == GameLoopTask.gameDirection.UP) {
                targetmyCoordY = TOP_BORDER;                    //Sets the target y coord to the TOP_BORDER if UP enum is inputted
            }
            if (myDir == GameLoopTask.gameDirection.DOWN) {
                targetmyCoordY = BOTTOM_BORDER;                 //Sets the target y coord to the BOTTOM_BORDER if DOWN enum is inputted
            }
            Log.d("DEBUG", myDir.toString());
        }
    }

    public void move(){
        //Function moves the block towards the target x or y coord with the current speed adding the acceleration constant every time the function is called
        if ((myCoordX < targetmyCoordX & myCoordX+V > targetmyCoordX)|(myCoordY < targetmyCoordY & myCoordY+V > targetmyCoordY)|    //Checks whether the next location of the block is pass the target
            (myCoordX > targetmyCoordX & myCoordX-V < targetmyCoordX)|(myCoordY > targetmyCoordY & myCoordY-V < targetmyCoordY)|    //Will set the block to the target if it is and set speed to 0.
            (myCoordX == targetmyCoordX & myCoordY == targetmyCoordY)){
            V = 0;
            myCoordX = targetmyCoordX;
            myCoordY = targetmyCoordY;
        }

        if (myCoordX < targetmyCoordX){             //If the x coord is less then target x coord add the current speed to the block x coord to move it closer to target
            myCoordX = myCoordX + V;
        }
        else if (myCoordX > targetmyCoordX){        //If the x coord is greater then target x coord subtract the current speed from the block x coord to move it closer to target
            myCoordX = myCoordX - V;
        }
        else if (myCoordY < targetmyCoordY){         //If the y coord is less then target y coord add the current speed to the block y coord to move it closer to target
            myCoordY = myCoordY + V;
        }
        else if (myCoordY > targetmyCoordY){         //If the y coord is greater then target y coord subtract the current speed from the block y coord to move it closer to target
            myCoordY = myCoordY - V;
        }
        V = V + A;                          //Increase the speed by the constant acceleration declared
        this.setX(myCoordX);                //Set the block x picture to the updated coordinate
        this.setY(myCoordY);                //Set the block y picture to the updated coordinate
    }
}
