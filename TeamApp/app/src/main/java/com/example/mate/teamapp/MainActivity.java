package com.example.mate.teamapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    MediaPlayer player;
    private static final long START_TIME__IN_MILLISECONDS=30000;

    private TextView mTextViewCountdown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMilliseconds = START_TIME__IN_MILLISECONDS;
    private long mEndTime;
    private MediaPlayer music;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //összekötöm a vezérlőt
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextViewCountdown = findViewById(R.id.text_view_countdown);
        mButtonStartPause =  findViewById(R.id.button_start_pause);
        mButtonReset =  findViewById(R.id.button_reset);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        music = MediaPlayer.create(MainActivity.this,R.raw.finish);

        /*
        SwitchCompat onOffSwitch = (SwitchCompat) findViewById(R.xml.preferences);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
            }
        });
        */
        // PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        android.support.v7.preference.PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Made By: Mate Gregor and Viktor Halasz", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", i, i1);
                long setTimeFromTimePicker = i*60000 + i1*1000;
                mTimeLeftInMilliseconds = setTimeFromTimePicker;
                mTextViewCountdown.setText(timeLeftFormatted);
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
                // hang lejátszása, ha a beállítás be van kapcsolva
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean playMusic = prefs.getBoolean("sound_alert_switch", true);
                if (playMusic) {
                    music.start();
                }

                //music.start();
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
        int i = timePicker.getHour();
        int i1 = timePicker.getMinute();
        //mTimeLeftInMilliseconds = START_TIME__IN_MILLISECONDS;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", i, i1);
        long setTimeFromTimePicker = i*60000 + i1*1000;
        mTimeLeftInMilliseconds = setTimeFromTimePicker;
        mTextViewCountdown.setText(timeLeftFormatted);

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
        }else{
            mButtonReset.setVisibility(View.VISIBLE);
            mButtonStartPause.setText("Start");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
