package uwaterloo.ca.lab4_202_14;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Alex Karner on 2017-06-29.
 */

abstract class GameBlockTemplate extends ImageView {
    GameBlockTemplate(Context myContext){
        super(myContext);
    }
    abstract public void setDestination(GameLoopTask.gameDirection newDir,GameLoopTask loopTask);
    abstract public void move();
}
