package com.gumpyang.Android.PanDian;

import android.app.Activity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.widget.Button;
import android.widget.TextView;
import android.net.Uri;
import android.content.Intent;
import android.content.ContentValues;
import android.view.View;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.view.Display;

import java.util.Calendar;

public class PanDian extends Activity {
	// 数据库对象
	private SQLiteDatabase mSQLiteData = null;
	// 数据库名
	private final static String DATABASE_NAME = "test.db";
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
	
	private String PDATE;
	
	private Button btn_scan;
	private Button btn_ok;
	private TextView mBarCode;
	private TextView mName;
	private TextView mAmount;
	
	private final static boolean PC_TEST = true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btn_scan = (Button)findViewById(R.id.scan_bt);
        mBarCode = (TextView)findViewById(R.id.barcode);
        btn_ok = (Button)findViewById(R.id.button_ok);
        mName = (TextView)findViewById(R.id.name);
        mAmount = (TextView)findViewById(R.id.amount);
        
        Calendar calendar = Calendar.getInstance();
        PDATE = "P" + calendar.get(Calendar.YEAR)
        	+ calendar.get(Calendar.MONTH)
        	+ calendar.get(Calendar.DAY_OF_MONTH)
        	+ calendar.get(Calendar.HOUR_OF_DAY)
        	+ calendar.get(Calendar.MINUTE);
/*        
        String sdcard =  "" + android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        mName.setText(sdcard);
*/      
        // 创建或打开数据库
        mSQLiteData = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        
        // 获取数据库的cursor
        try {
        	mSQLiteData.execSQL(CREATE_PRODUCT_TABLE);
        } catch (Exception e) {
        	UpdataAdapter();
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
        	UpdataAdapter();
        }

        btn_scan.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		if (PC_TEST) {
        			UpdateBarcode(null); // "0123456789");
        		} else {
        			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	                intent.setPackage("com.google.zxing.client.android");
	                intent.putExtra("SCAN_MODE", "ONE_D_MODE"); // "PRODUCT_MODE"); // "QR_CODE_MODE");
	                startActivityForResult(intent, 0);
        		}
        	}
        });

        btn_ok.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		mSQLiteData.close();
        	}
        });
    }
    
    public void UpdataAdapter() {
/**    	
    	Cursor cur = mSQLiteData.query(PRODUCT_TABLE_NAME, new String[] {TABLE_ID,
    			TABLE_NUM, TABLE_NAME, TABLE_AMOUNT}, null, null, null, null,
    			null);

    	miCount = cur.getCount();
    	if (cur != null && cur.getCount() >= 0)
    	{
    		
    	}
*/
    }
    
    private void UpdateBarcode(String num) {
    	if (num == null) {
    		mBarCode.setText(null);
    		mName.setText(null);
    		mAmount.setText(null);
    		return;
    	}
        mBarCode.setText(num);
    }

    /** */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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