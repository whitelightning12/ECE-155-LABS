package uwaterloo.ca.lab3_202_14;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Lab3_202_14 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3_202_14);
        LinearLayout l = (LinearLayout)findViewById(R.id.label2);
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Display Setup
        TextView direction = new TextView(getApplicationContext());
        l.addView(direction);
        direction.setTextColor(Color.BLACK);
        direction.setTextSize(20);

        //Sensor Setup
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        AccSensorEventListener accObject = new AccSensorEventListener(direction);
        sensorManager.registerListener(accObject, accSensor, sensorManager.SENSOR_DELAY_GAME);
    }
}

