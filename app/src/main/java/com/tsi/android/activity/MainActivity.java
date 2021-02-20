package com.tsi.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String USER_NAME = "userName";
    public static String USER_ERROR_COUNT = "userErrorCount";
    public static String USER_TIME = "userElapsedTime";

    private List<Button> _gameButtons;
    private ProgressBar _progressBar;
    private View _rootLayout;
    private View _gameOverScreen;
    private Chronometer _chronometer;
    private TextView _txtErrorCounter;
    private TextInputEditText _txtInputName;
    private int _clicksCounter;
    private int _errorCounter;
    private int time;
    private boolean _isShowingSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _gameButtons = new ArrayList<>();
        _progressBar = findViewById(R.id.progress_bar);
        _chronometer = findViewById(R.id.chronometer);
        _txtErrorCounter = findViewById(R.id.txtErrors);
        _txtInputName = findViewById(R.id.txtInputName);
        _gameOverScreen = findViewById(R.id.game_over_screen);
        _rootLayout = findViewById(R.id.root);

        for (int i = 1; i <= 6; i++)
            _gameButtons.add(findViewById(getResources().getIdentifier("btn_" + i, "id", getPackageName())));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return _isShowingSequence || super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        _chronometer.stop();
        _chronometer.setBase(SystemClock.elapsedRealtime());
        _gameOverScreen.setVisibility(View.GONE);
        _txtErrorCounter.setText("0");
        _progressBar.setProgress(0);
        _rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        for (Button btn: _gameButtons)
            btn.setVisibility(View.VISIBLE);
    }

    public void onButtonClick(View v) {
        Button clickedButton = (Button)v;

        if (_gameButtons.indexOf(clickedButton) != _clicksCounter++)
            resetButtons(v.getContext());
        else {
            clickedButton.setVisibility(View.INVISIBLE);
            _progressBar.setProgress(_clicksCounter);

            int clickedButtonColor = ContextCompat.getColor(
                    clickedButton.getContext(),
                    getResources().getIdentifier(
                            "color_btn_" + clickedButton.getText(),
                            "color", getPackageName()));

            _rootLayout.setBackgroundColor(clickedButtonColor);

            if (_clicksCounter == 6)
                gameOver(clickedButtonColor);
        }
    }

    public void onResetButtonClick(View v) {
        _chronometer.stop();
        _chronometer.setBase(SystemClock.elapsedRealtime());

        _isShowingSequence = true;
        _gameOverScreen.setVisibility(View.GONE);

        resetButtons(getBaseContext());

        _errorCounter = 0;
        _txtErrorCounter.setText("0");

        Collections.shuffle(_gameButtons);

        showSequence();
    }

    public void onSaveRecordClick(View v) {
        Intent intent = new Intent(this, RecordsActivity.class);
        Editable editable = _txtInputName.getText();
        if (editable != null) {
            intent.putExtra(USER_NAME, editable.toString());
        }
        intent.putExtra(USER_ERROR_COUNT, _errorCounter);
        intent.putExtra(USER_TIME,time);
        startActivity(intent);
    }

    private void showSequence() {
        new Thread() {
            @Override
            public void run() {

                AlphaAnimation animation = new AlphaAnimation(1F, 0.1F);
                animation.setDuration(80);

                for (Button btn: _gameButtons) {
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    btn.startAnimation(animation);
                }

                _isShowingSequence = false;
                _chronometer.setBase(SystemClock.elapsedRealtime());
                _chronometer.start();
            }
        }.start();
    }

    private void gameOver(int clickedButtonColor) {
        _chronometer.stop();
        time = (int)(SystemClock.elapsedRealtime() - _chronometer.getBase());
        _gameOverScreen.setBackgroundColor(clickedButtonColor);
        _gameOverScreen.setVisibility(View.VISIBLE);
    }

    private void resetButtons(Context context) {
        _errorCounter++;
        _txtErrorCounter.setText(String.valueOf( _errorCounter));
        _clicksCounter = 0;
        _progressBar.setProgress(0);
        _rootLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        for (Button btn: _gameButtons)
            btn.setVisibility(View.VISIBLE);
    }
}