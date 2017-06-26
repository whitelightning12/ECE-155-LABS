package uwaterloo.ca.lab3_202_14;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        l.getLayoutParams().height = 6000;
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
        direction.setX(300);
        direction.setY(700);

        final GameLoopTask myGameLoopTask= new GameLoopTask(this,getApplicationContext(),l);

        Timer myGameLoop = new Timer();
        myGameLoop.schedule(myGameLoopTask, 50, 50);

        //Sensor Setup
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        AccSensorEventListener accObject = new AccSensorEventListener(direction,myGameLoopTask);
        sensorManager.registerListener(accObject, accSensor, sensorManager.SENSOR_DELAY_GAME);

        Button LEFTButton = new Button(getApplicationContext());
        LEFTButton.setText("LEFT");
        l.addView(LEFTButton);
        LEFTButton.setY(700);

        LEFTButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.LEFT);
            }
        });

        Button RIGHTButton = new Button(getApplicationContext());
        RIGHTButton.setText("RIGHT");
        l.addView(RIGHTButton);
        RIGHTButton.setY(900);

        RIGHTButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.RIGHT);
            }
        });

        Button UPButton = new Button(getApplicationContext());
        UPButton.setText("UP");
        l.addView(UPButton);
        UPButton.setY(1100);

        UPButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.UP);
            }
        });

        Button DOWNButton = new Button(getApplicationContext());
        DOWNButton.setText("DOWN");
        l.addView(DOWNButton);
        DOWNButton.setY(1300);

        DOWNButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.DOWN);
            }
        });
    }
}

