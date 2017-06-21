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

    public GameLoopTask(Activity myActivity, Context myContext, RelativeLayout myRL){
        this.myActivity = myActivity;
        gameloopCTX = myContext;
        gameLoopRL = myRL;
        createBlock();
    }

    private void createBlock(){
        GameBlock newBlock = new GameBlock(gameloopCTX, 0, 0);
        gameLoopRL.addView(newBlock);
    }

    @Override
    public void run() {
        this.myActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DEBUG",String.valueOf(System.currentTimeMillis()));
                    }
                }
        );

    }
}
