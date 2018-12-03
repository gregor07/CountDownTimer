package com.example.mate.teamapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {
    MediaPlayer player;
    private static final long START_TIME__IN_MILLISECONDS=30000;

    private TextView mTextViewCountdown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonIncrease;
    private Button mButtonDecrease;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMilliseconds = START_TIME__IN_MILLISECONDS;
    private long mEndTime;
    private MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //összekötöm a vezérlőt
        mTextViewCountdown = findViewById(R.id.text_view_countdown);
        mButtonStartPause =  findViewById(R.id.button_start_pause);
        mButtonReset =  findViewById(R.id.button_reset);
        mButtonIncrease = findViewById(R.id.button_increase);
        mButtonDecrease = findViewById(R.id.button_decrease);
        music = MediaPlayer.create(MainActivity.this,R.raw.finish);

        mButtonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeLeftInMilliseconds=mTimeLeftInMilliseconds+60000;
                updateCountDownText();
            }
        });
        mButtonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimeLeftInMilliseconds-60000>0){
                    mTimeLeftInMilliseconds=mTimeLeftInMilliseconds-60000;
                    updateCountDownText();
                }
            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimerRunning){
                    pauseTimer();
                }else{
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });


    }

    //
    private  void startTimer(){
        // megadjuk, hogy mikor kéne lejárnia a számlálónak.
        // Így kiküszöbölhető az elforgatásokból adódó idő különbözet
        mEndTime = System.currentTimeMillis()+mTimeLeftInMilliseconds;
        //a függvény paraméterében az eltelt idő hosszát adjuk át ms-ben és az intervallumot
        mCountDownTimer = new CountDownTimer(mTimeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisecondsUntilFinished) {
                mTimeLeftInMilliseconds = millisecondsUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                // false-ra állítjuk, ha már nem fut a timer
                music.start();
                mTimerRunning = false;
                updateButtons();
                mTextViewCountdown.setText("00:00");
            }
        // amint rákattintunk a start gombra, a gomb meghívja majd ezt a startTimer függvényt és elkezdődik a másodpercek számlálása.
        }.start();
        //Ha már fut a számlálónk, akkor a felirata ezentúl a Pause felirat legyen
        mTimerRunning = true;
        updateButtons();
    }

    //ez a függvény lestoppolja a számlálót
    private void pauseTimer(){
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateButtons();
    }
    //nullázza az eltelt időt
    private void resetTimer(){
        mTimeLeftInMilliseconds = START_TIME__IN_MILLISECONDS;
        updateCountDownText();
        updateButtons();
    }
    private void updateCountDownText(){
        //hány perc telt el
        int minutes = (int) (mTimeLeftInMilliseconds / 1000) / 60;
        //hny másodperc telt el (a moddal visszakapjuk, hogy az osztás után mennyi a maradék....
        int seconds = (int) (mTimeLeftInMilliseconds / 1000) % 60;

        //formázzuk egy stingben a kapott értéket, hogy idő formátumként jelenjen meg
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountdown.setText(timeLeftFormatted);
    }
    private void updateButtons(){
        if (mTimerRunning){
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
            mButtonIncrease.setText("+");
            mButtonDecrease.setText("-");
        }else{
            mButtonReset.setVisibility(View.VISIBLE);
            mButtonStartPause.setText("Start");
            mButtonIncrease.setText("+");
            mButtonDecrease.setText("-");
            //1 másodperc alatt
            if (mTimeLeftInMilliseconds < 1000){
                mButtonStartPause.setVisibility(View.INVISIBLE);
            }else{
                mButtonStartPause.setVisibility(View.VISIBLE);
            }
        }
    }

    //az elforgatásból adódó hiba kiküszöbölésére

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millisLeft", mTimeLeftInMilliseconds);
        outState.putBoolean("timerRunning", mTimerRunning);
        outState.putLong("endTime", mEndTime);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mTimerRunning = savedInstanceState.getBoolean("timerRunning");
        mTimeLeftInMilliseconds = savedInstanceState.getLong("millisLeft");

        updateCountDownText();
        updateButtons();

        if (mTimerRunning){
            mEndTime = savedInstanceState.getLong("endTime");
            mTimeLeftInMilliseconds = mEndTime - System.currentTimeMillis();
            startTimer();
        }
    }
    //ez a metódus azért felel, hogy ha letesszük tálcára az appot, akkor a háttérben tovább fusson
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMilliseconds);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        //ha nincs ez a feltétel és kilépünk az appból a visszaszámláló elindítása előtt, akkor összeomlik
        //ezzel kiköszöböljük ezt a lehetséges hibát
        if (mCountDownTimer!= null){
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mTimeLeftInMilliseconds = prefs.getLong("millisLeft", START_TIME__IN_MILLISECONDS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateButtons();

        if (mTimerRunning){
            mEndTime = prefs.getLong("endTime",0);
            mTimeLeftInMilliseconds = mEndTime-System.currentTimeMillis();

            if (mTimeLeftInMilliseconds < 0){
                mTimeLeftInMilliseconds = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            }else{
                startTimer();
            }
        }
    }
}
