package com.moon.todo_lists;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDBHelper extends SQLiteOpenHelper {
    public static final String DATABASENAME = "task.db";
    private static final int DATABASEVER = 1;
    public static final String TABLENAME = "tasktable";
    public static final String TASK_TITLE = "title";
    public static final String TASK_ID = "id";

    public TaskDBHelper(Context context) {
        super(context,DATABASENAME,null,DATABASEVER);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLENAME + " ( " +
                TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                TASK_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropDB = " DROP TABLE IF EXISTS " + TABLENAME;
        db.execSQL(dropDB);
        this.onCreate(db);
    }
}
