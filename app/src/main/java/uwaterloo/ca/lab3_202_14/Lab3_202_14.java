package uwaterloo.ca.lab3_202_14;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;

public class Lab3_202_14 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3_202_14);
        RelativeLayout l = (RelativeLayout)findViewById(R.id.label2);
        l.getLayoutParams().width = 1024;
        l.getLayoutParams().height = 1024;
        ImageView test = new ImageView(getApplicationContext());
        test.setImageResource(R.drawable.gameboard);
        test.setX(0);
        test.setY(0);
        l.addView(test);
        //l.setBackgroundResource(R.drawable.gameboard);

        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Display Setup
        TextView direction = new TextView(getApplicationContext());
        l.addView(direction);
        direction.setTextColor(Color.BLACK);
        direction.setTextSize(20);

        GameLoopTask myGameLoopTask= new GameLoopTask(this,getApplicationContext(),l);

        Timer myGameLoop = new Timer();
        myGameLoop.schedule(myGameLoopTask, 50, 50);

        //Sensor Setup
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        AccSensorEventListener accObject = new AccSensorEventListener(direction);
        sensorManager.registerListener(accObject, accSensor, sensorManager.SENSOR_DELAY_GAME);
    }
}

