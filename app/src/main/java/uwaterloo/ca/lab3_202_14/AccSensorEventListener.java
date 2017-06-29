package uwaterloo.ca.lab3_202_14;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.widget.TextView;

import java.util.TimerTask;


/**
 * Created by Alex Karner on 2017-05-28.
 */
class AccSensorEventListener extends GeneralSensor {



    enum sensorState {WAIT, FALL_FIRST, RISE_SECOND, RISE_FIRST, FALL_SECOND, DETERMINED}
    enum foundState {NONE,UNKNOWN,RIGHT,LEFT,FORWARD,BACKWARD}

    float currentFilterReading[];
    float previousFilterReading[];
    float deltaAcc[];
    TextView direction;

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
    private GameLoopTask myGL;

    public AccSensorEventListener(TextView outputViewDirection,GameLoopTask myGameLoopTask){
        direction = outputViewDirection;
        currentFilterReading = new float[3];
        previousFilterReading = new float[3];
        deltaAcc = new float [3];
        sensorName = "Accelerometer";
        sensorStateLRFSM = sensorState.WAIT;
        sensorStateFBFSM = sensorState.WAIT;
        typeLR = foundState.NONE;
        typeFB = foundState.NONE;
        direction.setText(typeLR.toString());
        myGL = myGameLoopTask;

        maxLR = 0;
        minLR = 0;
        maxFB = 0;
        minFB = 0;

        thresRightRise = 0.1f;
        thresRightFall = -0.1f;
        thresRightMax = 1;
        thresRightMin = -3;
        thresLeftRise = 0.1f;
        thresLeftFall = -0.1f;
        thresLeftMax = 3;
        thresLeftMin = -1;

        thresForwardRise = 0.1f;
        thresForwardFall = -0.1f;
        thresForwardMax = 3;
        thresForwardMin = -1;
        thresBackwardRise = 0.1f;
        thresBackwardFall = -0.1f;
        thresBackwardMax = 1;
        thresBackwardMin = -3;
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
                if (deltaAcc[1] < thresBackwardFall){ //If the change in acc y is less then a certain threshold then the state switches to Fall first.
                    sensorStateFBFSM = sensorState.FALL_FIRST;  //A fall first in the value of deltaAcc indicates a Right hand Gesture.
                }
                else if (deltaAcc[1] > thresForwardRise){ //If the change in acc is greater then a certain threshold then the state switches to Rise First.
                    sensorStateFBFSM = sensorState.RISE_FIRST;  //A rise first in the value of deltaAcc indicates a left hand Gesture.
                }
                break;
            case FALL_FIRST:
                if (deltaAcc[1] > 0f & minFB < thresBackwardMin){ //If the acc value is increasing and the proper minimum was reached then switch to Rise_1 (RiseRight.
                    sensorStateFBFSM = sensorState.RISE_SECOND; //A rise second in the value of deltaAcc indicates a Right hand Gesture.
                }
                else if (deltaAcc[1] > 0f & minFB > thresBackwardMin ){ //If the acc value is increasing and the proper minimum was never reached then unknown signal detected.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.UNKNOWN; //If the fall does not complete then it is a unknown hand movement
                }
                if (minFB > currentFilterReading[1]){ //if the current reading is less then the min make the current reading the new minimum.
                    minFB = currentFilterReading[1];
                }
                break;
            case RISE_SECOND:
                if (deltaAcc[1] < 0f & maxFB > thresBackwardMax){ //If the acc value is decreasing and the proper maximum was reached then switch to determined right state.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.BACKWARD;
                }
                else if (deltaAcc[1] < 0f & maxFB < thresBackwardMax ){ //If the acc value is decreasing and the proper maximum was never reached then unknown signal detected.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.UNKNOWN;
                }
                if (maxFB < currentFilterReading[1]){ //if the current reading is greater then the max make the current reading the new maximum.
                    maxFB = currentFilterReading[1];
                }
                break;
            case RISE_FIRST:
                if (deltaAcc[1] < 0f & maxFB > thresForwardMax){ //If the acc value is decreasing and the proper maximum was reached then switch to Fall Left.
                    sensorStateFBFSM = sensorState.FALL_SECOND;
                }
                else if (deltaAcc[1] < 0f & maxFB < thresForwardMax ){ //If the acc value is decreasing and the proper maximum was never reached then unknown signal detected.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.UNKNOWN;
                }
                if (maxFB < currentFilterReading[1]){ //if the current reading is greater then the max make the current reading the new maximum.
                    maxFB = currentFilterReading[1];
                }
                break;
            case FALL_SECOND:
                if (deltaAcc[1] > 0f & minFB < thresForwardMin){ //If the acc value is increasing and the proper minimum was reached then switch to determined type left.
                    sensorStateFBFSM = sensorState.DETERMINED;
                    typeFB = foundState.FORWARD;
                }
                else if (deltaAcc[1] > 0f & minFB > thresForwardMin ){ //If the acc value is increasing and the proper minimum was never reached then unknown signal detected.
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
            //uses the previous acc and current acc values to get the change in acc
            //Once the deltaAcc is saved the previous filter Readings become the current
            //filter readings
            currentFilterReading = filterReading(se.values,currentFilterReading);
            deltaAcc[0] = previousFilterReading[0] - currentFilterReading[0];
            deltaAcc[1] = previousFilterReading[1] - currentFilterReading[1];
            deltaAcc[2] = previousFilterReading[2] - currentFilterReading[2];
            previousFilterReading[0] = currentFilterReading[0];
            previousFilterReading[1] = currentFilterReading[1];
            previousFilterReading[2] = currentFilterReading[2];
            sensorStateLRFSM = FSMRightLeft(sensorStateLRFSM);
            sensorStateFBFSM = FSMForwardBackward(sensorStateFBFSM);

            //If either state machine reaches determined state move the block
            if (sensorStateFBFSM == sensorState.DETERMINED){                //If the forward backward state machine gets a determined state output proper direction to gameblock
                direction.setText(typeFB.toString());
                if (typeFB == foundState.BACKWARD){
                    myGL.setDirection(GameLoopTask.gameDirection.DOWN);     //If the movement of the phone is backwards set the direction of the block to down
                }
                else if(typeFB == foundState.FORWARD){                      //If the movement of the phone is forwards set the direction of the block to up
                    myGL.setDirection(GameLoopTask.gameDirection.UP);
                }
            }
            if (sensorStateLRFSM == sensorState.DETERMINED){                //If the Left right state machine gets a determined state output proper direction to gameblock
                direction.setText(typeLR.toString());
                if (typeLR == foundState.RIGHT){
                    myGL.setDirection(GameLoopTask.gameDirection.RIGHT);    //If the movement of the phone is right set the direction of the block to right
                }
                else if(typeLR == foundState.LEFT){
                    myGL.setDirection(GameLoopTask.gameDirection.LEFT);     //If the movement of the phone is left set the direction of the block to left
                }
            }
        }
    }
}
