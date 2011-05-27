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
import android.util.Log;

import java.util.Calendar;
import 	java.lang.Integer;

public class PanDian extends Activity {
	private final static String TAG = "gumptest";
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
	
	private Button btn_scan;
	private Button btn_ok;
	private EditText mBarCode;
	private EditText mName;
	private EditText mAmount;
	
	private int sqlcontrol = 0; // 0 需要加入产品表和盘点表 1 产品表中有需要加入盘点表 2 盘点表需要修改数量
	
	private final static boolean PC_TEST = false; // true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);
        
        btn_scan = (Button)findViewById(R.id.scan_bt);
        mBarCode = (EditText)findViewById(R.id.barcode);
        btn_ok = (Button)findViewById(R.id.button_ok);
        mName = (EditText)findViewById(R.id.name);
        mAmount = (EditText)findViewById(R.id.amount);
/**        
        Calendar calendar = Calendar.getInstance();
        PDATE = "P" + calendar.get(Calendar.YEAR)
        	+ calendar.get(Calendar.MONTH)
        	+ calendar.get(Calendar.DAY_OF_MONTH)
        	+ calendar.get(Calendar.HOUR_OF_DAY)
        	+ calendar.get(Calendar.MINUTE);
*/        
        // 创建或打开数据库
//        String sdcard =  "" + android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
//        mName.setText(sdcard);
        mSQLiteData = this.openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_READABLE, null);
        
        // 获取数据库的cursor
        try {
        	mSQLiteData.execSQL(CREATE_PRODUCT_TABLE);
        } catch (Exception e) {
//        	UpdataAdapter();
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
//        	UpdataAdapter();
        }

        btn_scan.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		if (PC_TEST) {
        			UpdateBarcode("0123456789");
        		} else {
        			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	                intent.setPackage("com.google.zxing.client.android");
	                intent.putExtra("SCAN_MODE", "ONE_D_MODE"); // "PRODUCT_MODE"); // "QR_CODE_MODE");
	                intent.putExtra("SCAN_WIDTH", 800);
	                intent.putExtra("SCAN_HEIGHT", 200);
	                startActivityForResult(intent, 0);
        		}
        	}
        });

        btn_ok.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		SaveData();
        		mBarCode.setText(null);
        		mName.setText(null);
        		mAmount.setText(null);
        		sqlcontrol = 0;
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
		Log.d(TAG, "sqlcontrol=" + sqlcontrol);
		switch (sqlcontrol) {
		case 0:
			cv.put(TABLE_NUM, mBarCode.getText().toString());
			cv.put(TABLE_NAME, mName.getText().toString());
			mSQLiteData.insert(PRODUCT_TABLE_NAME, null, cv);
			cv.put(TABLE_AMOUNT, Integer.parseInt(mAmount.getText().toString()));
			mSQLiteData.insert(PDATE, null, cv);
			break;
		case 1:
			cv.put(TABLE_NUM, mBarCode.getText().toString());
			cv.put(TABLE_NAME, mName.getText().toString());
			cv.put(TABLE_AMOUNT, Integer.parseInt(mAmount.getText().toString()));
			mSQLiteData.insert(PDATE, null, cv);
			break;
		case 2:
			String amount = mAmount.getText().toString();
			cv.put(TABLE_NUM, mBarCode.getText().toString());
			cv.put(TABLE_NAME, mName.getText().toString());
			cv.put(TABLE_AMOUNT, Integer.parseInt(amount));
			int ret = mSQLiteData.update(PDATE, cv, TABLE_ID + "=" 
					+ Integer.toString(panCursor.getInt(panCursor.getColumnIndex(TABLE_ID))), 
					null);
			Log.d(TAG, "ret=" + ret);
			break;
		default:
			break;
		};
		sqlcontrol = 0;
	}
    
    private void UpdateBarcode(String num) {
    	if (num == null) {
    		mBarCode.setText(null);
    		mName.setText(null);
    		mAmount.setText(null);
    		sqlcontrol = 0;
    		return;
    	}
        mBarCode.setText(num);
        
        proCursor = QueryTable(PRODUCT_TABLE_NAME, num);
        if (proCursor == null)
        {
        	mName.setText(null);
        	mAmount.setText(null);
        	sqlcontrol = 0;
        	return;
        }
        ReadProduct(proCursor);
        panCursor = QueryTable(PDATE, num);
        if (panCursor == null) {
        	mAmount.setText("1");
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
    	Log.d(TAG, "test id=" + Integer.toString(cur.getInt(cur.getColumnIndex(TABLE_ID))));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	Log.d(TAG, "onActivityResult");
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                UpdateBarcode(contents);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	UpdateBarcode(null);
            }
        }
   	}
}