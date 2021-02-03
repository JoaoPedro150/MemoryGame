package com.tsi.android.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Button> _gameButtons;
    private ProgressBar _progressBar;
    private View _rootLayout;
    private View _gameOverScreen;
    private int _clicksCounter;
    private boolean _isShowingSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _gameButtons = new ArrayList<>();
        _progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        _gameOverScreen = findViewById(R.id.game_over_screen);
        _rootLayout = findViewById(R.id.root);

        for (int i = 1; i <= 6; i++)
            _gameButtons.add((Button)findViewById(getResources().getIdentifier("btn_" + i, "id", getPackageName())));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return _isShowingSequence || super.dispatchTouchEvent(ev);
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
        _isShowingSequence = true;
        _gameOverScreen.setVisibility(View.GONE);
        resetButtons(v.getContext());
        Collections.shuffle(_gameButtons);

        new Thread() {
            @Override
            public void run() {
                AlphaAnimation animation = new AlphaAnimation(1F, 0.1F);
                animation.setDuration(100);

                for (Button btn: _gameButtons) {
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    btn.startAnimation(animation);
                }

                _isShowingSequence = false;
            }
        }.start();
    }

    private void gameOver(int clickedButtonColor) {
        _gameOverScreen.setBackgroundColor(clickedButtonColor);
        _gameOverScreen.setVisibility(View.VISIBLE);
    }

    private void resetButtons(Context context) {
        _clicksCounter = 0;
        _progressBar.setProgress(0);
        _rootLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        for (Button btn: _gameButtons)
            btn.setVisibility(View.VISIBLE);
    }
}