package com.tsi.android.activity;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tsi.android.database.Database;
import com.tsi.android.model.Record;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    @Test
    public void createDataBase() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        new Database(appContext);
    }

    @Test
    public void insertRecord() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Database db = new Database(appContext);
        db.clear();
        Record record = new Record(null,"ana",1000,10);
        db.insertOrUpdate(record);

        Assert.assertFalse(db.getAll().isEmpty());
        Assert.assertNotNull(db.getRecord(record.getName()));
    }

    @Test
    public void insertDifferentRecords() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Database db = new Database(appContext);
        db.clear();
        Record bobRecord = new Record(null,"bob",100,10);
        db.insertOrUpdate(bobRecord);

        Record anaRecord = new Record(null,"ana",980,99);
        db.insertOrUpdate(anaRecord);

        Record anotherRecordOfBob = new Record(null,"bob",500,100);
        db.insertOrUpdate(anotherRecordOfBob);

        Record anotherRecordOfAna = new Record(null,"ana",740,120);
        db.insertOrUpdate(anotherRecordOfAna);

        anotherRecordOfAna = new Record(null,"ana",800,60);
        db.insertOrUpdate(anotherRecordOfAna);

        anotherRecordOfBob = new Record(null,"bob",300,9);
        db.insertOrUpdate(anotherRecordOfBob);

        anotherRecordOfBob = new Record(null,"bob",300,200);
        db.insertOrUpdate(anotherRecordOfBob);

        anotherRecordOfAna = new Record(null,"ana",830,20);
        db.insertOrUpdate(anotherRecordOfAna);

        Collection<Record> records = db.getAll();
        Assert.assertEquals(records.size(), 2);

        Record anaRealRecord = db.getRecord(anaRecord.getName());
        Assert.assertEquals((Integer)740, anaRealRecord.getErrors());
        Assert.assertEquals((Integer)20, anaRealRecord.getTime());

        Record bobRealRecord = db.getRecord(bobRecord.getName());
        Assert.assertEquals((Integer)100, bobRealRecord.getErrors());
        Assert.assertEquals((Integer)9, bobRealRecord.getTime());
    }

    @Test
    public void updateRecord() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Database db = new Database(appContext);
        db.clear();

        Record firstRecord = new Record(null,"ana",1000,10);
        db.insertOrUpdate(firstRecord);

        Record anotherRecord = new Record(null,"ana",900,100);
        db.insertOrUpdate(anotherRecord);

        anotherRecord = new Record(null,"ana",920,1000);
        db.insertOrUpdate(anotherRecord);

        anotherRecord = new Record(null,"ana",940,1);
        db.insertOrUpdate(anotherRecord);

        Collection<Record> records = db.getAll();

        Assert.assertEquals(records.size(), 1);

        Record realRecord = records.iterator().next();

        Assert.assertEquals((Integer)900, realRecord.getErrors());
        Assert.assertEquals((Integer)1, realRecord.getTime());
    }
}