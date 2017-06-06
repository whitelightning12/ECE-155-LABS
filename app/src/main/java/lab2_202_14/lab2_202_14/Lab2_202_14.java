package lab2_202_14.lab2_202_14;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ca.uwaterloo.sensortoy.LineGraphView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class Lab2_202_14 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2_202_14);
        LinearLayout l = (LinearLayout)findViewById(R.id.label2);
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Graph Setup
        LineGraphView graph = new LineGraphView(getApplicationContext(),100, Arrays.asList("x","y","z"));
        l.addView(graph);
        LineGraphView graph2 = new LineGraphView(getApplicationContext(),100, Arrays.asList("x","y","z"));
        l.addView(graph2);

        final float accSensorHistory[][] = new float[100][3]; //2D array to store last 100 acc readings.
        final float accSensorHistoryFiltered[][] = new float[100][3]; //2D array to store last 100 acc readings.

        //Button Setup
        Button csvOutputButton = new Button(getApplicationContext());
        csvOutputButton.setText("Generate CSV Record for Acc Sensor");
        l.addView(csvOutputButton);

        //Display Setup
        TextView acc = new TextView(getApplicationContext());
        l.addView(acc);

        TextView accMax = new TextView(getApplicationContext());
        l.addView(accMax);

        TextView direction = new TextView(getApplicationContext());
        l.addView(direction);
        direction.setTextColor(Color.BLACK);
        direction.setTextSize(20);

        //Sensor Setup
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        AccSensorEventListener accObject = new AccSensorEventListener(acc, accMax, direction, graph,graph2, accSensorHistory,accSensorHistoryFiltered);
        sensorManager.registerListener(accObject, accSensor, sensorManager.SENSOR_DELAY_GAME);

        //Button Press Setup
        csvOutputButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                FileWriter fileWriter = null;
                PrintWriter printWriter = null;

                try{
                    File file = new File(getExternalFilesDir("Lab1 Recorded Data"), "acc_readings.csv"); //Creates folder and file if not already there.
                    Log.d("LOG", file.getPath());
                    fileWriter = new FileWriter(file);
                    printWriter = new PrintWriter(fileWriter);
                    for (int i = 0; i < 100; i++) {  //Outputs acc values into excel file in table format.
                        printWriter.println(String.format("%.2f,%.2f,%.2f", accSensorHistoryFiltered[i][0], accSensorHistoryFiltered[i][1], accSensorHistoryFiltered[i][2]));
                    }
                }

                catch(IOException e) {
                    Log.d("LOG","File Write Error: "+e.toString());
                }
                finally{
                    if(printWriter != null){
                        printWriter.flush();
                        printWriter.close();
                    }
                }
            }
        });
    }
}

