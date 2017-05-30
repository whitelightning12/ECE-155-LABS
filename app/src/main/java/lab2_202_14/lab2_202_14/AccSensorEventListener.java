package lab2_202_14.lab2_202_14;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.uwaterloo.sensortoy.LineGraphView;

/**
 * Created by Alex Karner on 2017-05-28.
 */
class AccSensorEventListener extends GeneralSensor {
    float accValues[][];
    LineGraphView g;

    public AccSensorEventListener(TextView outputView, TextView outputViewMax, LineGraphView graph, float accReadings[][]){
        super(outputView,outputViewMax);
        g = graph;
        accValues = accReadings;
        sensorName = "Accelerometer";
        UpdateText(new float[]{0,0,0},Max,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
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
            Max = CheckMax(se.values,Max);
            UpdateText(se.values,Max,output,outputMax,sensorName);
        }
    }
}
