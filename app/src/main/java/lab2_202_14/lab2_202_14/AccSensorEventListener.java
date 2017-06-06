package lab2_202_14.lab2_202_14;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.widget.TextView;

import ca.uwaterloo.sensortoy.LineGraphView;

/**
 * Created by Alex Karner on 2017-05-28.
 */
class AccSensorEventListener extends GeneralSensor {

    enum sensorState {WAIT, FALL_FIRST, RISE_SECOND, RISE_FIRST, FALL_SECOND, DETERMINED}
    enum foundState {NONE,UNKNOWN,RIGHT,LEFT,FORWARD,BACKWARD}

    float currentReading[];
    float currentFilterReading[];
    float previousFilterReading[];
    float deltaAcc[];
    TextView direction;

    float sensorHistory[][];
    float sensorHistoryFiltered[][];
    LineGraphView g;
    LineGraphView g2;

    float maxLR;
    float minLR;
    float maxFB;
    float minFB;

    float thresRightRise;
    float thresRightFall;
    float thresRightMax;
    float thresRightMin;
    float thresLeftRise;
    float thresLeftFall;
    float thresLeftMax;
    float thresLeftMin;


    float thresForwardRise;
    float thresForwardFall;
    float thresForwardMax;
    float thresForwardMin;
    float thresBackwardRise;
    float thresBackwardFall;
    float thresBackwardMax;
    float thresBackwardMin;

    sensorState sensorStateLRFSM;
    sensorState sensorStateFBFSM;
    foundState typeLR;
    foundState typeFB;
    foundState typeLRFB;



    public AccSensorEventListener(TextView outputView, TextView outputViewMax, TextView outputViewDirection, LineGraphView graph, LineGraphView graph2, float accSensorHistory[][], float accSensorHistoryFiltered[][]){
        super(outputView,outputViewMax);
        direction = outputViewDirection;
        currentFilterReading = new float[3];
        previousFilterReading = new float[3];
        currentReading = new float[3];
        deltaAcc = new float [3];
        sensorHistory = accSensorHistory;
        sensorHistoryFiltered = accSensorHistoryFiltered;
        g = graph;
        g2 = graph2;
        sensorName = "Accelerometer";
        sensorStateLRFSM = sensorState.WAIT;
        sensorStateFBFSM = sensorState.WAIT;
        typeLR = foundState.NONE;
        typeFB = foundState.NONE;
        typeLRFB = foundState.NONE;

        maxLR = 0;
        minLR = 0;
        maxFB = 0;
        minFB = 0;

        thresRightRise = 0.1f;
        thresRightFall = -0.1f;
        thresRightMax = 2;
        thresRightMin = -4;
        thresLeftRise = 0.1f;
        thresLeftFall = -0.1f;
        thresLeftMax = 4;
        thresLeftMin = -2;

        thresForwardRise = 2;
        thresForwardFall = -2;
        thresForwardMax = 4;
        thresForwardMin = -3;
        thresBackwardRise = 2;
        thresBackwardFall = -2;
        thresBackwardMax = 1;
        thresBackwardMin = -5;
        //UpdateText(new float[]{0,0,0},Max,output,outputMax,sensorName); //Initial text update to set sensor reading to 0.
    }

    public sensorState FSMRightLeft(sensorState sensorStateLRFSM){
        switch(sensorStateLRFSM){
            case WAIT:
                if (deltaAcc[0] < thresLeftFall){ //If the change in acc is less then a certain threshold then the state switches to Fall first.
                    sensorStateLRFSM = sensorState.FALL_FIRST;  //A fall first in the value of deltaAcc indicates a Right hand Gesture.
                }
                else if (deltaAcc[0] > thresRightRise){ //If the change in acc is greater then a certain threshold then the state switches to Rise First.
                    sensorStateLRFSM = sensorState.RISE_FIRST;  //A rise first in the value of deltaAcc indicates a left hand Gesture.
                }
                break;
            case FALL_FIRST:
                if (deltaAcc[0] > 0f & minLR < thresLeftMin){ //If the acc value is increasing and the proper minimum was reached then switch to Rise_1 (RiseRight.
                    sensorStateLRFSM = sensorState.RISE_SECOND; //A rise second in the value of deltaAcc indicates a Right hand Gesture.
                }
                else if (deltaAcc[0] > 0f & minLR > thresLeftMin ){ //If the acc value is increasing and the proper minimum was never reached then unknown signal detected.
                    sensorStateLRFSM = sensorState.DETERMINED;
                    typeLR = foundState.UNKNOWN; //If the fall does not complete then it is a unknown hand movement
                }
                if (minLR > currentFilterReading[0]){ //if the current reading is less then the min make the current reading the new minimum.
                    minLR = currentFilterReading[0];
                }
                break;
            case RISE_SECOND:
                if (deltaAcc[0] < 0f & maxLR > thresLeftMax){ //If the acc value is decreasing and the proper maximum was reached then switch to determined right state.
                    sensorStateLRFSM = sensorState.DETERMINED;
                    typeLR = foundState.LEFT;
                }
                else if (deltaAcc[0] < 0f & maxLR < thresLeftMax ){ //If the acc value is decreasing and the proper maximum was never reached then unknown signal detected.
                    sensorStateLRFSM = sensorState.DETERMINED;
                    typeLR = foundState.UNKNOWN;
                }
                if (maxLR < currentFilterReading[0]){ //if the current reading is greater then the max make the current reading the new maximum.
                    maxLR = currentFilterReading[0];
                }
                break;
            case RISE_FIRST:
                if (deltaAcc[0] < 0f & maxLR > thresRightMax){ //If the acc value is decreasing and the proper maximum was reached then switch to Fall Left.
                    sensorStateLRFSM = sensorState.FALL_SECOND;
                }
                else if (deltaAcc[0] < 0f & maxLR < thresRightMax ){ //If the acc value is decreasing and the proper maximum was never reached then unknown signal detected.
                    sensorStateLRFSM = sensorState.DETERMINED;
                    typeLR = foundState.UNKNOWN;
                }
                if (maxLR < currentFilterReading[0]){ //if the current reading is greater then the max make the current reading the new maximum.
                    maxLR = currentFilterReading[0];
                }
                break;
            case FALL_SECOND:
                if (deltaAcc[0] > 0f & minLR < thresRightMin){ //If the acc value is increasing and the proper minimum was reached then switch to determined type left.
                    sensorStateLRFSM = sensorState.DETERMINED;
                    typeLR = foundState.RIGHT;
                }
                else if (deltaAcc[0] > 0f & minLR > thresRightMin ){ //If the acc value is increasing and the proper minimum was never reached then unknown signal detected.
                    sensorStateLRFSM = sensorState.DETERMINED;
                    typeLR = foundState.UNKNOWN;
                }
                if (minLR > currentFilterReading[0]){ //if the current reading is less then the min make the current reading the new minimum.
                    minLR = currentFilterReading[0];
                }
                break;
            case DETERMINED:
                maxLR = 0;
                minLR = 0;
                sensorStateLRFSM = sensorState.WAIT;
                typeLR = foundState.NONE;
                break;
        }
        return sensorStateLRFSM;
    }

    public sensorState FSMForwardBackward(sensorState sensorStateFBFSM){
        switch(sensorStateFBFSM){
            case WAIT:
                if (deltaAcc[1] < thresForwardFall){ //If the change in acc y is less then a certain threshold then the state switches to Fall first.
                    sensorStateFBFSM = sensorState.FALL_FIRST;  //A fall first in the value of deltaAcc indicates a Right hand Gesture.
                }
                else if (deltaAcc[1] > thresLeftRise){ //If the change in acc is greater then a certain threshold then the state switches to Rise First.
                    sensorStateFBFSM = sensorState.RISE_FIRST;  //A rise first in the value of deltaAcc indicates a left hand Gesture.
                }
                break;
            case FALL_FIRST:
                if (deltaAcc[1] > 0f & minFB < thresForwardMin){ //If the acc value is increasing and the proper minimum was reached then switch to Rise_1 (RiseRight.
                    sensorStateFBFSM = sensorState.RISE_SECOND; //A rise second in the value of deltaAcc indicates a Right hand Gesture.
                }
                else if (deltaAcc[1] > 0f & minFB > thresForwardMin ){ //If the acc value is increasing and the proper minimum was never reached then unknown signal detected.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.UNKNOWN; //If the fall does not complete then it is a unknown hand movement
                }
                if (minFB > currentFilterReading[1]){ //if the current reading is less then the min make the current reading the new minimum.
                    minFB = currentFilterReading[1];
                }
                break;
            case RISE_SECOND:
                if (deltaAcc[1] < 0f & maxFB > thresForwardMax){ //If the acc value is decreasing and the proper maximum was reached then switch to determined right state.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.FORWARD;
                }
                else if (deltaAcc[1] < 0f & maxFB < thresForwardMax ){ //If the acc value is decreasing and the proper maximum was never reached then unknown signal detected.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.UNKNOWN;
                }
                if (maxFB < currentFilterReading[1]){ //if the current reading is greater then the max make the current reading the new maximum.
                    maxFB = currentFilterReading[1];
                }
                break;
            case RISE_FIRST:
                if (deltaAcc[1] < 0f & maxFB > thresBackwardMax){ //If the acc value is decreasing and the proper maximum was reached then switch to Fall Left.
                    sensorStateFBFSM = sensorState.FALL_SECOND;
                }
                else if (deltaAcc[1] < 0f & maxFB < thresBackwardMax ){ //If the acc value is decreasing and the proper maximum was never reached then unknown signal detected.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.UNKNOWN;
                }
                if (maxFB < currentFilterReading[1]){ //if the current reading is greater then the max make the current reading the new maximum.
                    maxFB = currentFilterReading[1];
                }
                break;
            case FALL_SECOND:
                if (deltaAcc[1] > 0f & minFB < thresBackwardMin){ //If the acc value is increasing and the proper minimum was reached then switch to determined type left.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.BACKWARD;
                }
                else if (deltaAcc[1] > 0f & minFB > thresBackwardMin ){ //If the acc value is increasing and the proper minimum was never reached then unknown signal detected.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.UNKNOWN;
                }
                if (minFB > currentFilterReading[1]){ //if the current reading is less then the min make the current reading the new minimum.
                    minFB = currentFilterReading[1];
                }
                break;
            case DETERMINED:
                maxFB = 0;
                minFB = 0;
                sensorStateFBFSM = sensorState.WAIT;
                typeFB = foundState.NONE;
                break;
        }
        return sensorStateFBFSM;
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            currentFilterReading = filterReading(se.values,currentFilterReading);
            deltaAcc[0] = previousFilterReading[0] - currentFilterReading[0];
            deltaAcc[1] = previousFilterReading[1] - currentFilterReading[1];
            deltaAcc[2] = previousFilterReading[2] - currentFilterReading[2];
            previousFilterReading[0] = currentFilterReading[0];
            previousFilterReading[1] = currentFilterReading[1];
            previousFilterReading[2] = currentFilterReading[2];
            sensorStateLRFSM = FSMRightLeft(sensorStateLRFSM);
            sensorStateFBFSM = FSMForwardBackward(sensorStateFBFSM);
            //if (sensorStateLRFSM == sensorState.DETERMINED & sensorStateFBFSM == sensorState.WAIT | sensorStateLRFSM == sensorState.DETERMINED & sensorStateFBFSM == sensorState.DETERMINED ){
            //    typeLRFB = typeLR;
            //}
            //else if (sensorStateLRFSM == sensorState.WAIT & sensorStateFBFSM == sensorState.DETERMINED){
            //    typeLRFB = typeFB;
           // }
            //else if ( sensorStateLRFSM == sensorState.WAIT & sensorStateFBFSM == sensorState.WAIT
            //        | sensorStateLRFSM == sensorState.WAIT & sensorStateFBFSM != sensorState.WAIT & typeFB != foundState.UNKNOWN
            //        | sensorStateLRFSM != sensorState.WAIT & sensorStateFBFSM == sensorState.WAIT & typeLR != foundState.UNKNOWN){
            //    typeLRFB = foundState.NONE;
            //}
            //else{
            //    typeLRFB = foundState.UNKNOWN;
            //}
            if (sensorStateLRFSM == sensorState.DETERMINED){
                direction.setText(typeLR.toString());
            }

            //Acc saves up to 100 values of its most recent history using the following code:
            for (int i = 99; i > 0; i--){       //Moves all items 1 place to the right
                sensorHistory[i][0] = sensorHistory[i-1][0];
                sensorHistory[i][1] = sensorHistory[i-1][1];
                sensorHistory[i][2] = sensorHistory[i-1][2];
            }
            sensorHistory[0][0] = se.values[0];    //Sets the first value as the new acc value in the list
            sensorHistory[0][1] = se.values[1];
            sensorHistory[0][2] = se.values[2];

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
