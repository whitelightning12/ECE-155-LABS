package uwaterloo.ca.lab3_202_14;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Alex Karner on 2017-06-21.
 */

public class GameBlock extends ImageView {
    private float IMAGE_SCALE = 1f;
    private int myCoordX;
    private int myCoordY;
    private int targetmyCoordX;
    private int targetmyCoordY;
    private GameLoopTask.gameDirection myDir;
    private int V;
    private final int A = 10;

    public GameBlock(Context myContext, int coordX, int coordY){
        super(myContext);
        myCoordX = coordX;
        myCoordY = coordY;
        targetmyCoordX = myCoordX;
        targetmyCoordY = myCoordY;
        V = 0;
        this.setX(myCoordX);
        this.setY(myCoordY);
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
    }

    public void setBlockDirection(GameLoopTask.gameDirection newDir){
        if (myCoordX == targetmyCoordX & myCoordY == targetmyCoordY) {
            myDir = newDir;
            if (myDir == GameLoopTask.gameDirection.RIGHT) {
                targetmyCoordX = 500;
            }
            if (myDir == GameLoopTask.gameDirection.LEFT) {
                targetmyCoordX = 0;
            }
            if (myDir == GameLoopTask.gameDirection.UP) {
                targetmyCoordY = 0;
            }
            if (myDir == GameLoopTask.gameDirection.DOWN) {
                targetmyCoordY = 500;
            }
            Log.d("DEBUG1", myDir.toString());
        }
    }

    public void move(){
        if ((myCoordX < targetmyCoordX & myCoordX+V > targetmyCoordX)|(myCoordY < targetmyCoordY & myCoordY+V > targetmyCoordY)|
            (myCoordX > targetmyCoordX & myCoordX-V < targetmyCoordX)|(myCoordY > targetmyCoordY & myCoordY-V < targetmyCoordY)|
            (myCoordX == targetmyCoordX & myCoordY == targetmyCoordY)){
            V = 0;
            myCoordX = targetmyCoordX;
            myCoordY = targetmyCoordY;
        }

        if (myCoordX < targetmyCoordX){
            myCoordX = myCoordX + V;
        }
        else if (myCoordX > targetmyCoordX){
            myCoordX = myCoordX - V;
        }
        else if (myCoordY < targetmyCoordY){
            myCoordY = myCoordY + V;
        }
        else if (myCoordY > targetmyCoordY){
            myCoordY = myCoordY - V;
        }
        V = V + A;
        this.setX(myCoordX);
        this.setY(myCoordY);
    }
}
