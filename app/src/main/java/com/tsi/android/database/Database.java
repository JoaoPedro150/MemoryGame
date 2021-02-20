package com.tsi.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tsi.android.model.Record;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Database implements Closeable {
    private static final String DATABASE_NAME = "bd1";
    private static final int DATABASE_ACCESS = 0;
    private static final String TABLE = "records";

    private static final String SQL_STRUCT = "CREATE TABLE IF NOT EXISTS records (id INTEGER PRIMARY KEY,time INTEGER NOT NULL,name TEXT NOT NULL,errors INTEGER NOT NULL)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM records";
    private static final String SQL_SELECT_BY_NAME = "SELECT * FROM records where name = ?";
    private static final String SQL_CLEAR = "DROP TABLE IF EXISTS records";

    private SQLiteDatabase database;

    public Database(Context context) {
        database = context.openOrCreateDatabase(DATABASE_NAME, DATABASE_ACCESS, null);
        database.execSQL(SQL_STRUCT);
    }

    public void insertOrUpdate(Record entry) {
        Record record = getRecord(entry.getName());

        if (record == null) {
            database.insert(TABLE, null, parseToContentValues(entry));
            return;
        }

        if (entry.getTime() < record.getTime()) {
            record.setTime(entry.getTime());
        }
        if (entry.getErrors() < record.getErrors()) {
            record.setErrors(entry.getErrors());
        }

        database.update(TABLE, parseToContentValues(record), "id = ?", new String[] { String.valueOf(record.getId()) });
    }

    public List<Record> getAll() {
        return get(SQL_SELECT_ALL);
    }

    public Record getRecord(String name) {
        Collection<Record> records = get(SQL_SELECT_BY_NAME, name);

        if (records.isEmpty())
            return  null;

        return records.iterator().next();
    }

    public void clear() {
        database.execSQL(SQL_CLEAR);
        database.execSQL(SQL_STRUCT);
    }

    @Override
    public void close()  {
        database.close();
    }

    private ContentValues parseToContentValues(Record record) {
        ContentValues values = new ContentValues();
        values.put("id", record.getId());
        values.put("name", record.getName());
        values.put("time", record.getTime());
        values.put("errors", record.getErrors());
        return values;
    }

    private List<Record> get(String sqlCommand, String... selectionArgs) {
        List<Record> records = new ArrayList<>();
        Record record;

        Cursor cursor = database.rawQuery(sqlCommand, selectionArgs);
        if (cursor.moveToFirst()) {
            int indexId = cursor.getColumnIndex("id");
            int indexName = cursor.getColumnIndex("name");
            int indexErrors = cursor.getColumnIndex("errors");
            int indexTime = cursor.getColumnIndex("time");

            do {
                record = new Record(
                        cursor.getInt(indexId),
                        cursor.getString(indexName),
                        cursor.getInt(indexErrors),
                        cursor.getInt(indexTime));

                records.add(record);
            }while (cursor.moveToNext());
        }

        cursor.close();

        return records;
    }
}
