package uwaterloo.ca.lab4_202_14;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;

public class Lab4_202_14 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_202_14);
        RelativeLayout l = (RelativeLayout)findViewById(R.id.label2);
        l.getLayoutParams().width = 1024;
        l.getLayoutParams().height = 1024;
        l.setBackgroundResource(R.drawable.gameboard);
        RelativeLayout dataLayout = (RelativeLayout)findViewById(R.id.label3);
        dataLayout.getLayoutParams().width = 1024;
        dataLayout.getLayoutParams().height = 1024;
        dataLayout.setX(0);
        dataLayout.setY(1024);

        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Display Setup
        TextView direction = new TextView(getApplicationContext());
        dataLayout.addView(direction);
        direction.setTextColor(Color.BLACK);
        direction.setTextSize(20);
        direction.setX(440);
        direction.setY(0);

        final GameLoopTask myGameLoopTask= new GameLoopTask(this,getApplicationContext(),l);

        Timer myGameLoop = new Timer();
        myGameLoop.schedule(myGameLoopTask, 10, 10);    //Schedules timer for display to update every 50 ms

        //Sensor Setup
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        AccSensorEventListener accObject = new AccSensorEventListener(direction,myGameLoopTask);
        sensorManager.registerListener(accObject, accSensor, sensorManager.SENSOR_DELAY_GAME);

        //Button Setup
        Button LEFTButton = new Button(getApplicationContext());
        LEFTButton.setText("LEFT");
        dataLayout.addView(LEFTButton);
        LEFTButton.setX(150);
        LEFTButton.setY(100);


        LEFTButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.LEFT);
            }
        });

        Button RIGHTButton = new Button(getApplicationContext());
        RIGHTButton.setText("RIGHT");
        dataLayout.addView(RIGHTButton);
        RIGHTButton.setX(150);
        RIGHTButton.setY(300);

        RIGHTButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.RIGHT);
            }
        });

        Button UPButton = new Button(getApplicationContext());
        UPButton.setText("UP");
        dataLayout.addView(UPButton);
        UPButton.setX(650);
        UPButton.setY(100);

        UPButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.UP);
            }
        });

        Button DOWNButton = new Button(getApplicationContext());
        DOWNButton.setText("DOWN");
        dataLayout.addView(DOWNButton);
        DOWNButton.setX(650);
        DOWNButton.setY(300);

        DOWNButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.DOWN);
            }
        });
    }
}

