package lab2_202_14.lab2_202_14;

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

        final float acc_readings[][] = new float[100][3]; //2D array to store last 100 acc readings.

        //Button Setup
        Button myButton = new Button(getApplicationContext());
        myButton.setText("Generate CSV Record for Acc Sensor");
        l.addView(myButton);

        //Display Setup
        TextView acc = new TextView(getApplicationContext());
        l.addView(acc);

        TextView accMax = new TextView(getApplicationContext());
        l.addView(accMax);

        //Sensor Setup
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        SensorEventListener accObject = new AccSensorEventListener(acc, accMax, graph, acc_readings);
        sensorManager.registerListener(accObject, accSensor, sensorManager.SENSOR_DELAY_GAME);

        //Button Press Setup
        myButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                FileWriter fileWriter = null;
                PrintWriter printWriter = null;

                try{
                    File file = new File(getExternalFilesDir("Lab1 Recorded Data"), "acc_readings.csv"); //Creates folder and file if not already there.
                    Log.d("LOG", file.getPath());
                    fileWriter = new FileWriter(file);
                    printWriter = new PrintWriter(fileWriter);
                    for (int i = 0; i < 100; i++) {  //Outputs acc values into excel file in table format.
                        printWriter.println(String.format("%.2f,%.2f,%.2f", acc_readings[i][0], acc_readings[i][1], acc_readings[i][2]));
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

abstract class GeneralSensor implements SensorEventListener {
    //Contains functions that each sensor needs.

    public float[] CheckMax(float newValue[],float currentMax[]){
        //Function checks if inputted array values are larger then current max values and returns updated max.
        if (Math.abs(newValue[0]) > Math.abs(currentMax[0])){
            currentMax[0] = newValue[0];
        }
        if (Math.abs(newValue[1]) > Math.abs(currentMax[1])){
            currentMax[1] = newValue[1];
        }
        if (Math.abs(newValue[2]) > Math.abs(currentMax[2])){
            currentMax[2] = newValue[2];
        }
        return currentMax;
    }

    public float CheckMax(float newValue, float currentMax){
        //Custom version of the CheckMax function for the light sensor as it only has a float variable not a array.
        if (Math.abs(newValue) > Math.abs(currentMax)){
            currentMax = newValue;
        }
        return currentMax;
    }

    public void UpdateText(float currentValues[],float maxValues[], TextView currentReadingOutput,TextView maxReadingOutput, String sensorName){
        //Updates text for the acc, mf and rv sensors.
        currentReadingOutput.setText("Current "+sensorName+" Reading:\n("+currentValues[0]+", "+currentValues[1]+", "+currentValues[2]+")");
        maxReadingOutput.setText("Max "+sensorName+" Reading:\n("+maxValues[0]+", "+maxValues[1]+", "+maxValues[2]+")");
    }

    public void UpdateText(float currentValue,float maxValue, TextView currentReadingOutput,TextView maxReadingOutput, String sensorName){
        //Updates text for the light sensor.
        currentReadingOutput.setText("Current "+sensorName+" Reading:\n"+currentValue);
        maxReadingOutput.setText("Max "+sensorName+" Reading:\n"+maxValue);
    }
}

final class LightSensorEventListener extends GeneralSensor {
    TextView output;
    TextView outputMax;
    String sensorName;
    float lightMax;

    public LightSensorEventListener(TextView outputView, TextView outputViewMax){
        output = outputView;
        outputMax = outputViewMax;

        lightMax = 0;
        sensorName = "Light Sensor";
        UpdateText(0f,lightMax,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightMax = CheckMax(se.values[0],lightMax);
            UpdateText(se.values[0],lightMax,output,outputMax,sensorName);
        }
    }
}

class AccSensorEventListener extends GeneralSensor {
    TextView output;
    TextView outputMax;
    public String sensorName;
    float accMax[];
    float accValues[][];
    LineGraphView g;

    public AccSensorEventListener(TextView outputView, TextView outputViewMax, LineGraphView graph, float accReadings[][]){
        output = outputView;
        outputMax = outputViewMax;
        g = graph;
        accValues = accReadings;

        accMax = new float[3];
        sensorName = "Accelerometer";
        UpdateText(new float[]{0,0,0},accMax,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            //Acc saves up to 100 values of its most recent history using the following code:
            for (int i = 99; i > 0; i--){       //Moves all items 1 place to the right
                accValues[i][0] = accValues[i-1][0];
                accValues[i][1] = accValues[i-1][1];
                accValues[i][2] = accValues[i-1][2];
            }
            accValues[0][0] = se.values[0];    //Sets the first value as the new acc value in the list
            accValues[0][1] = se.values[1];
            accValues[0][2] = se.values[2];

            g.addPoint(se.values);  //Graphs the new acc value.
            accMax = CheckMax(se.values,accMax);
            UpdateText(se.values,accMax,output,outputMax,sensorName);
        }
    }
}

class MfSensorEventListener extends GeneralSensor {
    TextView output;
    TextView outputMax;
    public String sensorName;
    float mfMax[];

    public MfSensorEventListener(TextView outputView, TextView outputViewMax){
        output = outputView;
        outputMax = outputViewMax;

        mfMax = new float[3];
        sensorName = "Magnetic Field";
        UpdateText(new float[]{0,0,0},mfMax,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mfMax = CheckMax(se.values,mfMax);
            UpdateText(se.values,mfMax,output,outputMax,sensorName);
        }
    }
}

class RvSensorEventListener extends GeneralSensor {
    TextView output;
    TextView outputMax;
    public String sensorName;
    float rvMax[];

    public RvSensorEventListener(TextView outputView, TextView outputViewMax){
        output = outputView;
        outputMax = outputViewMax;

        rvMax = new float[3];
        sensorName = "Rotation Vector";
        UpdateText(new float[]{0,0,0},rvMax,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            rvMax = CheckMax(se.values,rvMax);
            UpdateText(se.values,rvMax,output,outputMax,sensorName);
        }
    }
}
