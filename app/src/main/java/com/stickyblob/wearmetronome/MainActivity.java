package com.stickyblob.wearmetronome;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.input.RotaryEncoder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    Handler mHandler = new Handler();
    Runnable mRunnable;
    Vibrator mVibrator;
    EditText mBeatsPerMinute;
    Button mStartBtn;
    Button mStopBtn;


    private int delay = 0;
    private long oldTime;
    private int whatToSetBeatPerMin = 0;
    private long reCheckSensors = 45;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mBeatsPerMinute = (EditText) findViewById(R.id.beats_per_minute);
        mStartBtn = (Button) findViewById(R.id.start_btn);
        mStopBtn = (Button) findViewById(R.id.stop_btn);

        mBeatsPerMinute.setText(Integer.toString(whatToSetBeatPerMin));

        setAmbientEnabled();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                delay = getMillisFromEditText();
                mVibrator.vibrate(delay / 5);
                mHandler.postDelayed(this, delay);
            }
        };

        mBeatsPerMinute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    return;
                }
                if (Integer.parseInt(s.toString()) > 400) {
                    mBeatsPerMinute.setText("400");
                    whatToSetBeatPerMin = 400;
                }

                if (Integer.parseInt(s.toString()) < 0) {
                    mBeatsPerMinute.setText("0");
                    whatToSetBeatPerMin = 0;
                }
            }
        });

        mBeatsPerMinute.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && RotaryEncoder.isFromRotaryEncoder(event)) {

                    long timeNow = System.currentTimeMillis();

                    if (timeNow - oldTime > reCheckSensors) {

                        float delta = -RotaryEncoder.getRotaryAxisValue(event) * RotaryEncoder.getScaledScrollFactor(MainActivity.this);


                        num = Math.round(delta);


                        oldTime = timeNow;
//
//                        if (num > 10) {
//                            Log.d(TAG, "onGenericMotion: here 100");
//                            num = num + 100;
//                        }else if(num > 11){
//                            Log.d(TAG, "onGenericMotion: here 90");
//                            num = num + 90;
//                        }else if(num > 10){
//                            Log.d(TAG, "onGenericMotion: here 80");
//                            num = num + 80;
//                        }else if(num > 9){
//                            Log.d(TAG, "onGenericMotion: here 70");
//                            num = num + 70;
//                        }else if(num > 8){
//                            Log.d(TAG, "onGenericMotion: here 60");
//                            num = num + 60;
//                        }else if(num > 7){
//                            Log.d(TAG, "onGenericMotion: here 50");
//                            num = num + 50;
//                        }else if(num > 6){
//                            Log.d(TAG, "onGenericMotion: here 40");
//                            num = num + 40;
//                        }else if(num > 5){
//                            Log.d(TAG, "onGenericMotion: here 30");
//                            num = num + 30;
//                        }else if(num > 4){
//                            Log.d(TAG, "onGenericMotion: here 20");
//                            num = num + 20;
//                        }else{
//                            Log.d(TAG, "onGenericMotion: here -1");
//                            num = num - 1;
//                        }
//                        if(num > 2){
//                            reCheckSensors = 200;
//                            num = num * 3;
//                        }else if(num > 5){
//                            reCheckSensors = 75;
//                            num = num * 5;
//                        }else if(num > 10){
//                            reCheckSensors = 0;
//                            num = num * 7;
//                        }

                        whatToSetBeatPerMin = whatToSetBeatPerMin + num - 1;

                        mBeatsPerMinute.setText(Integer.toString(whatToSetBeatPerMin));

                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void start(View view) {

        showStopBtn();

        if (mBeatsPerMinute.getText().toString().equals("")) {
            return;
        }
        delay = getMillisFromEditText();
        mHandler.postDelayed(mRunnable, delay);
    }

    public void stop(View view) {

        showStartBtn();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void showStartBtn() {
        mStartBtn.setVisibility(View.VISIBLE);
        mStopBtn.setVisibility(View.INVISIBLE);
    }

    private void showStopBtn() {
        mStartBtn.setVisibility(View.INVISIBLE);
        mStopBtn.setVisibility(View.VISIBLE);
    }

    private int getMillisFromEditText() {
        int bpm = Integer.parseInt(mBeatsPerMinute.getText().toString());
        double some_num = 60.000 / bpm;
        double millis = some_num * 1000;
        return (int) Math.round(millis);
    }
}
