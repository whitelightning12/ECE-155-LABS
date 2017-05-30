package lab2_202_14.lab2_202_14;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Alex Karner on 2017-05-28.
 */
class MfSensorEventListener extends GeneralSensor {

    public MfSensorEventListener(TextView outputView, TextView outputViewMax){
        super(outputView,outputViewMax);
        sensorName = "Magnetic Field";
        UpdateText(new float[]{0,0,0},Max,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Max = CheckMax(se.values,Max);
            UpdateText(se.values,Max,output,outputMax,sensorName);
        }
    }
}
