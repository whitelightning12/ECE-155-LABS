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
    float currentFilterReading[];
    float sensorHistory[][];
    float sensorHistoryFiltered[][];
    LineGraphView g;
    LineGraphView g2;


    public AccSensorEventListener(TextView outputView, TextView outputViewMax, LineGraphView graph, LineGraphView graph2, float accSensorHistory[][], float accSensorHistoryFiltered[][]){
        super(outputView,outputViewMax);
        currentFilterReading = new float[3];
        sensorHistory = accSensorHistory;
        sensorHistoryFiltered = accSensorHistoryFiltered;
        g = graph;
        g2 = graph2;
        sensorName = "Accelerometer";
        //UpdateText(new float[]{0,0,0},Max,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            //Acc saves up to 100 values of its most recent history using the following code:
            for (int i = 99; i > 0; i--){       //Moves all items 1 place to the right
                sensorHistory[i][0] = sensorHistory[i-1][0];
                sensorHistory[i][1] = sensorHistory[i-1][1];
                sensorHistory[i][2] = sensorHistory[i-1][2];
            }
            sensorHistory[0][0] = se.values[0];    //Sets the first value as the new acc value in the list
            sensorHistory[0][1] = se.values[1];
            sensorHistory[0][2] = se.values[2];

            currentFilterReading = filterReading(se.values,currentFilterReading);

            //Acc saves up to 100 values of its most recent filtered history using the following code:
            for (int i = 99; i > 0; i--){       //Moves all items 1 place to the right
                sensorHistoryFiltered[i][0] = sensorHistoryFiltered[i-1][0];
                sensorHistoryFiltered[i][1] = sensorHistoryFiltered[i-1][1];
                sensorHistoryFiltered[i][2] = sensorHistoryFiltered[i-1][2];
            }
            sensorHistoryFiltered[0][0] = currentFilterReading[0];    //Sets the first value as the new acc value in the list
            sensorHistoryFiltered[0][1] = currentFilterReading[1];
            sensorHistoryFiltered[0][2] = currentFilterReading[2];

            g.addPoint(se.values);  //Graphs the new acc value.
            g2.addPoint(currentFilterReading);  //Graphs the new acc value.
        }
    }
}
