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
	
	// 创建表的sql语句
	private final static String CREATE_TABLE = "CREATE TABLE "
		+ TABLE_NAME
		+ " (" + TABLE_ID
		+ " INTEGER PRIMARY KEY, "
		+ TABLE_NUM + " INTEGER, "
		+ TABLE_NAM + " TEXT, "
		+ TABLE_AMOUNT + " INTEGER)";
	
	private Button btn_scan;
	private Uri imageFilePath;
	private ImageView imageView;
	private TextView mBarCode;
	
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
        
        btn_scan = (Button)findViewById(R.id.scan_bt);
        mBarCode = (TextView)findViewById(R.id.barcode);
        
        btn_scan.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v)
        	{
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.setPackage("com.google.zxing.client.android");
                intent.putExtra("SCAN_MODE", "ONE_D_MODE"); // "PRODUCT_MODE"); // "QR_CODE_MODE");
                startActivityForResult(intent, 0);
        	}
        });

    }
    
    public void UpdataAdapter() {
    }

    /** */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	// super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                mBarCode.setText(contents);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
   	}
}