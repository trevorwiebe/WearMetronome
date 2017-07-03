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

                        whatToSetBeatPerMin = whatToSetBeatPerMin + num - 1;

                        mBeatsPerMinute.setText(Integer.toString(whatToSetBeatPerMin));

                    }
                }
                return false;
            }
        });

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void plus(View view){
        int num = Integer.parseInt(mBeatsPerMinute.getText().toString());
        mBeatsPerMinute.setText(Integer.toString(num++));
    }

    public void minus(View view){
        int num = Integer.parseInt(mBeatsPerMinute.getText().toString());
        mBeatsPerMinute.setText(Integer.toString(num--));
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
