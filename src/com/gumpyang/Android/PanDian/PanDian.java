package com.gumpyang.Android.PanDian;

import android.app.Activity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
// import com.google.zxing.Reader;
import com.google.zxing.MultiFormatReader;

public class PanDian extends Activity {
	// 数据库对象
	private SQLiteDatabase mSQLiteData = null;
	// 数据库名
	private final static String DATABASE_NAME = "test.db";
	// 表名
	private final static String TABLE_NAME = "table1";
	
	// 表中的字段
	private final static String TABLE_ID = "_id";
	private final static String TABLE_NUM = "num";
	private final static String TABLE_NAM = "name";
	private final static String TABLE_AMOUNT = "amount";
	
	// 条码读取
	private MultiFormatReader mMultiFormatReader;
	
	// 创建表的sql语句
	private final static String CREATE_TABLE = "CREATE TABLE "
		+ TABLE_NAME
		+ " (" + TABLE_ID
		+ " INTEGER PRIMARY KEY, "
		+ TABLE_NUM + " INTEGER, "
		+ TABLE_NAM + " TEXT, "
		+ TABLE_AMOUNT + " INTEGER)";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // 创建或打开数据库
        mSQLiteData = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        
        // 获取数据库的cursor
        try {
        	mSQLiteData.execSQL(CREATE_TABLE);
        } catch (Exception e) {
        	UpdataAdapter();
        }
        
        mMultiFormatReader = new MultiFormatReader();
    }
    
    public void UpdataAdapter() {
    }
}