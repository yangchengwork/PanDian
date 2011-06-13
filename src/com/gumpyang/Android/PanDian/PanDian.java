package com.gumpyang.Android.PanDian;

import android.app.Activity;

import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.content.ContentValues;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.util.Log;

import java.util.Calendar;
import 	java.lang.Integer;
import java.io.FileWriter;

public class PanDian extends Activity {
    private final static String TAG = "PanDian";
    // 数据库对象
    private SQLiteDatabase mSQLiteData = null;
    // 数据库名
    private final static String DATABASE_NAME = "pandian.db";
    // 表名
    private final static String PRODUCT_TABLE_NAME = "product";

    // 表中的字段
    private final static String TABLE_ID = "_id";
    private final static String TABLE_NUM = "num";
    private final static String TABLE_NAME = "name";
    private final static String TABLE_AMOUNT = "amount";

    // 创建表的sql语句
    private final static String CREATE_PRODUCT_TABLE = "CREATE TABLE "
            + PRODUCT_TABLE_NAME
            + " (" + TABLE_ID
            + " INTEGER PRIMARY KEY, "
            + TABLE_NUM + " TEXT, "
            + TABLE_NAME + " TEXT)";
//		+ TABLE_AMOUNT + " INTEGER)";

    private final static String PDATE = "pandian";
    private Cursor proCursor;
    private Cursor panCursor;

    private static Button btn_scan;
    private static Button btn_ok;
    private static Button btn_new;
    private static Button btn_export;
    private static EditText mBarCode;
    private static EditText mName;
    private static EditText mAmount;

    private int sqlcontrol = 0; // 0 需要加入产品表和盘点表 1 产品表中有需要加入盘点表 2 盘点表需要修改数量

    private final static boolean PC_TEST = false; // true; //   

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn_scan = (Button)findViewById(R.id.scan_bt);
        btn_ok = (Button)findViewById(R.id.button_ok);
        btn_new = (Button)findViewById(R.id.new_bt);
        btn_export = (Button)findViewById(R.id.export_bt);
        mBarCode = (EditText)findViewById(R.id.barcode);
        mName = (EditText)findViewById(R.id.name);
        mAmount = (EditText)findViewById(R.id.amount);
        
        mBarCode.setFocusable(true);
        
        // 创建或打开数据库
        mSQLiteData = this.openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_READABLE, null);

        // 获取数据库的cursor
        try {
            mSQLiteData.execSQL(CREATE_PRODUCT_TABLE);
        } catch (Exception e) {

        }

        try {
            String CREATE_DATE_TABLE = "CREATE TABLE "
                                       + PDATE
                                       + " (" + TABLE_ID
                                       + " INTEGER PRIMARY KEY, "
                                       + TABLE_NUM + " TEXT, "
                                       + TABLE_NAME + " TEXT, "
                                       + TABLE_AMOUNT + " INTEGER)";
            mSQLiteData.execSQL(CREATE_DATE_TABLE);
        } catch (Exception e) {

        }

        btn_scan.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (PC_TEST) {
                    UpdateBarcode("6901028075763");
                } else {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    // intent.setPackage("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "ONE_D_MODE"); // "PRODUCT_MODE"); // "QR_CODE_MODE");
                    // intent.putExtra("SCAN_WIDTH", 800);
                    // intent.putExtra("SCAN_HEIGHT", 200);
                    startActivityForResult(intent, 0);
                }
            }
        });

        btn_ok.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SaveData();
                mName.setText(null);
                mAmount.setText(null);
                mBarCode.setText(null);
                sqlcontrol = 0;
            }
        });

        btn_new.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mSQLiteData.execSQL("DROP TABLE " + PDATE);
                String CREATE_DATE_TABLE = "CREATE TABLE "
                                           + PDATE
                                           + " (" + TABLE_ID
                                           + " INTEGER PRIMARY KEY, "
                                           + TABLE_NUM + " TEXT, "
                                           + TABLE_NAME + " TEXT, "
                                           + TABLE_AMOUNT + " INTEGER)";
                mSQLiteData.execSQL(CREATE_DATE_TABLE);
            }
        });
        
        btn_export.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String sdcard =  android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
                Calendar calendar = Calendar.getInstance();
                String name = sdcard + String.format("/Pandian_%d%02d%02d.csv",  
                	calendar.get(Calendar.YEAR),
                				(calendar.get(Calendar.MONTH) + 1),
                                calendar.get(Calendar.DATE));
                // Log.i(TAG, "sdcard file name=" + name);
                
                Cursor mCursor = mSQLiteData.query(PDATE, new String[] {TABLE_NUM, TABLE_NAME, TABLE_AMOUNT},
                		null, null, null, null, null);
                if (mCursor == null || mCursor.getCount() == 0) {
                	return;
                }
                try {
                FileWriter fw = new FileWriter(name);
 
                mCursor.moveToFirst();
                do {
                	String buf = String.format("%s, %s, %d\r\n",
                			mCursor.getString(mCursor.getColumnIndex(TABLE_NUM)),
                			mCursor.getString(mCursor.getColumnIndex(TABLE_NAME)),
                			mCursor.getInt(mCursor.getColumnIndex(TABLE_AMOUNT))
                			);
                	fw.write(buf);
/**
                	Log.i(TAG, String.format("%s, %s, %d",
                			mCursor.getString(mCursor.getColumnIndex(TABLE_NUM)),
                			mCursor.getString(mCursor.getColumnIndex(TABLE_NAME)),
                			mCursor.getInt(mCursor.getColumnIndex(TABLE_AMOUNT))
                			)
                			);
*/
                } while (mCursor.moveToNext());
                fw.close();
                } catch (Exception e) {

                }
 
/**
                mSQLiteData.execSQL(".separator \", \"\n");
                mSQLiteData.execSQL(".output " + sdcard + "/" + name + "\n");
                mSQLiteData.execSQL("select * from " + PDATE);
*/
            }
        });
        
        mBarCode.setOnFocusChangeListener(new OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
            	if(!hasFocus) {
            		UpdateBarcode(mBarCode.getText().toString());
            	}
            }
        });
        
        mName.setOnFocusChangeListener(new OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
            	if(!hasFocus) {
            		if (sqlcontrol > 0) {	// 产品表中已经存在，只是改名
	                    ContentValues cv = new ContentValues();
	                    cv.put(TABLE_NUM, mBarCode.getText().toString());
	                    cv.put(TABLE_NAME, mName.getText().toString());
	                    mSQLiteData.update(PRODUCT_TABLE_NAME, cv, TABLE_ID + "="
	                                       + Integer.toString(proCursor.getInt(proCursor.getColumnIndex(TABLE_ID))),
	                                       null);
            		}
            	}
            }
        });
    }

    @Override
    public void onDestroy() {
        mSQLiteData.close();
        super.onDestroy();
    }

    private void SaveData() {
        ContentValues cv = new ContentValues();
        switch (sqlcontrol) {
        case 0:
            cv.put(TABLE_NUM, mBarCode.getText().toString());
            cv.put(TABLE_NAME, mName.getText().toString());
            mSQLiteData.insert(PRODUCT_TABLE_NAME, null, cv);
        case 1:
            cv.put(TABLE_NUM, mBarCode.getText().toString());
            cv.put(TABLE_NAME, mName.getText().toString());
            cv.put(TABLE_AMOUNT, Integer.parseInt(mAmount.getText().toString()));
            mSQLiteData.insert(PDATE, null, cv);
            break;
        case 2:
            cv.put(TABLE_NUM, mBarCode.getText().toString());
            cv.put(TABLE_NAME, mName.getText().toString());
            cv.put(TABLE_AMOUNT, Integer.parseInt(mAmount.getText().toString()));
            mSQLiteData.update(PDATE, cv, TABLE_ID + "="
                               + Integer.toString(panCursor.getInt(panCursor.getColumnIndex(TABLE_ID))),
                               null);
            break;
        default:
            break;
        };
        sqlcontrol = 0;
    }

    private void UpdateBarcode(String num) {
        if (num == null) {
            mName.setText(null);
            mAmount.setText(null);
            mBarCode.setText(null);
            mBarCode.setFocusable(true);
            sqlcontrol = 0;
            return;
        }
        mBarCode.setText(num);

        proCursor = QueryTable(PRODUCT_TABLE_NAME, num);
        if (proCursor == null) {
            mAmount.setText(null);
            mName.setText(null);
            mName.setFocusable(true);
            sqlcontrol = 0;
            return;
        }
        ReadProduct(proCursor);
        panCursor = QueryTable(PDATE, num);
        if (panCursor == null) {
            mAmount.setText("1");
            mAmount.setFocusable(true);
            sqlcontrol = 1;
            return;
        }
        ReadPandian(panCursor);
        sqlcontrol = 2;
    }

    private void ReadProduct(Cursor cur) {
        mName.setText(cur.getString(cur.getColumnIndex(TABLE_NAME)));
    }
    private void ReadPandian(Cursor cur) {
        mAmount.setText(Integer.toString(cur.getInt(cur.getColumnIndex(TABLE_AMOUNT))));
    }

    private Cursor QueryTable(String table, String num) {
        Cursor mCursor = mSQLiteData.query(table, null,
                                           "num='" + num + "'", null, null, null, null);

        //mCursor不等于null,将标识指向第一条记录
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
        } else {
            mCursor = null;
        }
        return mCursor;
    }

    /** */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                // String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                UpdateBarcode(contents);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                UpdateBarcode(null);
            }
        }
    }
}
