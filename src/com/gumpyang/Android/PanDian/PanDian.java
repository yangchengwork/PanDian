package com.gumpyang.Android.PanDian;

import android.app.Activity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;

public class PanDian extends Activity {
	private SQLiteDatabase mSQLiteData = null;
	private final static String DATABASE_NAME = "test.db";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSQLiteData = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
    }
}