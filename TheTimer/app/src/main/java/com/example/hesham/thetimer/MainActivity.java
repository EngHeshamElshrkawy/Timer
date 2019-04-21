package com.example.hesham.thetimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private long timeCountInMilliSeconds = 60000;
    private boolean work = true;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private EditText editTextMinuteWork;
    private EditText editTextMinuteBreak;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;
    private Button buttonStartStop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();


    }

    private void initViews() {
        progressBarCircle = findViewById(R.id.progressBarCircle);
        editTextMinuteWork = findViewById(R.id.editTextMinuteWork);
        editTextMinuteBreak = findViewById(R.id.editTextMinuteBreak);
        textViewTime = findViewById(R.id.textViewTime);
        imageViewReset = findViewById(R.id.imageViewReset);
        imageViewStartStop = findViewById(R.id.imageViewStartStop);
        buttonStartStop = findViewById(R.id.startStopButton);
    }


    private void initListeners() {
        imageViewReset.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
        buttonStartStop.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewReset:
                reset();
                break;
           // case R.id.imageViewStartStop:
           //     startStop();
           //     break;
            case R.id.startStopButton:
                startStop();
                break;
        }
    }


    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }



    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
            //imageViewReset.setVisibility(View.VISIBLE);
            // changing play icon to stop icon
            //imageViewStartStop.setImageResource(R.drawable.icon_stop);
            buttonStartStop.setBackground(this.getResources().getDrawable(R.drawable.stop_selector));
            // making edit text not editable
            editTextMinuteWork.setEnabled(false);
            editTextMinuteBreak.setEnabled(false);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {

            // hiding the reset icon
            imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            //imageViewStartStop.setImageResource(R.drawable.icon_start);
            buttonStartStop.setBackground(this.getResources().getDrawable(R.drawable.start_selector));
            // making edit text editable
            editTextMinuteWork.setEnabled(true);
            editTextMinuteBreak.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    private void setTimerValues() {
        int time = 0;
        if (!editTextMinuteWork.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(editTextMinuteWork.getText().toString().trim());
        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                editTextMinuteBreak.setEnabled(false);

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
                editTextMinuteWork.setEnabled(true);
                editTextMinuteBreak.setEnabled(true);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;

                popUpForBreak();
            }

        };
        countDownTimer.start();
    }


    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }



    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    private void popUpForBreak() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
        if (work == true) {
            String title = "Need a break?";
            String msg = "Would you like to take your break?";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int time = 0;
                            if (!editTextMinuteBreak.getText().toString().isEmpty()) {
                                // fetching value from edit text and type cast to integer
                                time = Integer.parseInt(editTextMinuteBreak.getText().toString().trim());
                            } else {
                                // toast message to fill edit text
                                Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
                            }
                            // assigning values after converting to milliseconds
                            timeCountInMilliSeconds = time * 60 * 1000;
                            setProgressBarValues();
                            startCountDownTimer();

                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setProgressBarValues();
                    startCountDownTimer();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            work = false;
        }else{
            String title = "Go back to work?";
            String msg = "Would you like to go back to working?";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setTimerValues();
                            setProgressBarValues();
                            startCountDownTimer();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setProgressBarValues();
                    startCountDownTimer();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            work = true;
        }

    }


}