package com.lekston.timeup;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import static android.os.SystemClock.elapsedRealtime;

public class MainActivity extends AppCompatActivity implements Chronometer.OnChronometerTickListener {

    ArrayList<Long> _sequences = new ArrayList<>();
    MediaPlayer _duck;
    MediaPlayer _duck2;
    int _index = 0;
    int _iteration = 0;
    int _max_iteration = 0;
    Chronometer _chronometer;
    PowerManager.WakeLock _wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        _chronometer = (Chronometer)findViewById(R.id.chronometer);
        _chronometer.setOnChronometerTickListener(this);
        _chronometer.setCountDown(true);
        _duck = MediaPlayer.create(MainActivity.this, R.raw.duck);
        _duck.setLooping(false);
        _duck2 = MediaPlayer.create(MainActivity.this, R.raw.duck2);
        _duck2.setLooping(false);
    }

    public void handleStartClick(View sender) {
        EditText sequenceEdit = (EditText)findViewById(R.id.sequenceEdit);
        String sequence_text = sequenceEdit.getText().toString();
        String durations[] = sequence_text.split(",");
        _sequences.clear();
        for (int index = 0; index < durations.length; index++) {
            _sequences.add(Long.decode(durations[index]) * 1000);
        }
        if (_sequences.size() <= 0) {
            return;
        }
        EditText iterationEdit = (EditText)findViewById(R.id.iterationEdit);
        _max_iteration = Integer.decode(iterationEdit.getText().toString());

        _index = -1;
        _iteration = 1;
        TextView finish_view = (TextView)findViewById(R.id.finishLabel);
        finish_view.setVisibility(View.INVISIBLE);
        nextDuration();
    }

    public void handleStopClick(View sender) {
        _chronometer.stop();
        TextView finish_view = (TextView)findViewById(R.id.finishLabel);
        finish_view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        if (chronometer.getBase() - elapsedRealtime() <= 0) {
            nextDuration();
        }
    }

    protected void nextDuration() {
        _index++;
        if (_index < 0 || _index >= _sequences.size()) { _index = 0; _iteration++;}
        if (_iteration > _max_iteration) {
            _chronometer.stop();
            TextView finish_view = (TextView)findViewById(R.id.finishLabel);
            finish_view.setVisibility(View.VISIBLE);
            return;
        }
        _chronometer.setBase(elapsedRealtime() + _sequences.get(_index));
        _chronometer.start();
        if (_index % 2 == 0) {
            _duck.start();
        } else {
            _duck2.start();
        }

    }
}
