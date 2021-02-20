package com.tsi.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.tsi.android.database.Database;
import com.tsi.android.model.Record;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecordsActivity extends AppCompatActivity {
    private TableLayout _tableLayout;
    private int currentTab = 0;
    private List<Record> _records;
    private Database _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        Intent intent = getIntent();
        String userName = intent.getStringExtra(MainActivity.USER_NAME);
        int errorCount = intent.getIntExtra(MainActivity.USER_ERROR_COUNT, 0);
        int elapsedTime = intent.getIntExtra(MainActivity.USER_TIME, 0);

        _db = new Database(this);
        _db.insertOrUpdate(new Record(null, userName, errorCount, elapsedTime));

        _tableLayout = findViewById(R.id.rankingTable);
        _records = _db.getAll();

        Collections.sort(_records,(r1, r2) -> r1.getTime().compareTo(r2.getTime()));

        populateTable(currentTab);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabChange(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    public void onClearBtnClick(View v) {
        _db.clear();
        _tableLayout.removeViews(2, _records.size());
        _records.clear();
    }

    public void onTabChange(int pos) {
        _tableLayout.removeViews(2, _records.size());

        if (pos == currentTab) {
            return;
        }

        currentTab = pos;

        if (currentTab == 0) {
            ((TextView)findViewById(R.id.txtRankingMode)).setText(R.string.best_time);
            Collections.sort(_records,(r1, r2) -> r1.getTime().compareTo(r2.getTime()));
        }
        else {
            ((TextView)findViewById(R.id.txtRankingMode)).setText(R.string.less_errors);
            Collections.sort(_records,(r1, r2) -> r1.getErrors().compareTo(r2.getErrors()));
        }

        populateTable(pos);
    }

    public void populateTable(int type) {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                1.0f
        );
        int i = 1;

        for (Record record:
                _records) {
            TableRow row = new TableRow(this);

            TextView txtPos = new TextView(this);
            txtPos.setText(String.format(Locale.getDefault(), "%dº", i++));
            txtPos.setTypeface(null, Typeface.BOLD);
            txtPos.setTextSize(18);
            txtPos.setGravity(Gravity.CENTER);
            txtPos.setLayoutParams(param);
            row.addView(txtPos);

            TextView txtName = new TextView(this);
            txtName.setText(record.getName());
            txtName.setLayoutParams(param);
            txtName.setGravity(Gravity.CENTER);
            txtName.setTextSize(18);
            row.addView(txtName);

            TextView txtTime = new TextView(this);

            if (type == 0) {
                txtTime.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(record.getTime()),
                        TimeUnit.MILLISECONDS.toSeconds(record.getTime()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(record.getTime()))
                ));
            }
            else {
                txtTime.setText(String.valueOf(record.getErrors()));
            }
            txtTime.setTextSize(18);
            txtTime.setGravity(Gravity.CENTER);
            txtTime.setLayoutParams(param);
            row.addView(txtTime);

            _tableLayout.addView(row);
        }
    }
}