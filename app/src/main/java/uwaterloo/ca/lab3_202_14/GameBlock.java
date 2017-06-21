package uwaterloo.ca.lab3_202_14;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Alex Karner on 2017-06-21.
 */

public class GameBlock extends ImageView {
    private float IMAGE_SCALE = 1f;
    private int myCoordX;
    private int myCoordY;

    public GameBlock(Context myContext, int coordX, int coordY){
        super(myContext);
        myCoordX = coordX;
        myCoordY = coordY;
        this.setX(myCoordX);
        this.setY(myCoordY);
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
    }
}
