package lab2_202_14.lab2_202_14;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Alex Karner on 2017-05-28.
 */
class LightSensorEventListener extends GeneralSensor {
    float Max;
    public LightSensorEventListener(TextView outputView, TextView outputViewMax){
        super(outputView,outputViewMax);
        Max = 0;
        sensorName = "Light Sensor";
        UpdateText(0f,Max,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LIGHT) {
            Max = CheckMax(se.values[0],Max);
            UpdateText(se.values[0],Max,output,outputMax,sensorName);
        }
    }
}
