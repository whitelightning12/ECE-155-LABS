package uwaterloo.ca.lab4_202_14;

import android.hardware.SensorEventListener;
import android.widget.TextView;

/**
 * Created by Alex Karner on 2017-05-28.
 */
abstract class GeneralSensor implements SensorEventListener {
    //Contains functions that each sensor needs.
    public String sensorName;
    final int C = 15;


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

    public float[] filterReading(float newReading[], float previousReading[]){
        //Filters the reading by adding (newReading-previousReading)/C to previous reading
        //where C is a constant determined to work best for our phone
        float filteredReading[] = new float[3];
        previousReading[0] += (newReading[0] - previousReading[0])/C;
        previousReading[1] += (newReading[1] - previousReading[1])/C;
        previousReading[2] += (newReading[2] - previousReading[2])/C;
        return previousReading;
    }
}
