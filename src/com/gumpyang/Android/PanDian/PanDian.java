package com.gumpyang.Android.PanDian;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import com.google.zxing.Reader;
import com.google.zxing.MultiFormatReader;
import android.widget.Button;
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
	// 数据库对豄1�7
	private SQLiteDatabase mSQLiteData = null;
	// 数据库名
	private final static String DATABASE_NAME = "test.db";
	// 表名
	private final static String TABLE_NAME = "table1";
	
	// 表中的字殄1�7
	private final static String TABLE_ID = "_id";
	private final static String TABLE_NUM = "num";
	private final static String TABLE_NAM = "name";
	private final static String TABLE_AMOUNT = "amount";
	
	// 条码读取
	private Reader mReader;
	
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
	private static final int RESULT_CODE = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // 创建或打弄1�7数据庄1�7
        mSQLiteData = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        
        // 获取数据库的cursor
        try {
        	mSQLiteData.execSQL(CREATE_TABLE);
        } catch (Exception e) {
        	UpdataAdapter();
        }
        
        mReader = new MultiFormatReader();
        
        btn_scan = (Button)findViewById(R.id.scan_bt);
        
        btn_scan.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v)
        	{
        		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        		ContentValues values = new ContentValues(3);
        		values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
        		values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
        		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        		imageFilePath = PanDian.this.getContentResolver().insert(
        				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
        		startActivityForResult(intent, RESULT_CODE);
        	}
        });
    }
    
    public void UpdataAdapter() {
    };
    
    /** */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_CODE){
    		try {
				Bundle extra = data.getExtras();
				/**
				 * Ȼ��Ϊ�˽�Լ�ڴ�����ģ����ﷵ�ص�ͼƬ��һ��121*162������ͼ��
				 * ��ô��η���������Ҫ�Ĵ�ͼ�أ�������
				 * Ȼ���洢��ͼƬ������ͼƬ�Ĵ洢λ�ã��ܲ���ֱ�ӽ�ͼƬ��ʾ�����ء�
				 * ����������Ƶ�����ͼƬ�Ĵ������ʾ���Ƿǳ������ڴ�ģ�����PC��˵���ܲ���ʲô�����Ƕ����ֻ���˵
				 * �ܿ���ʹ���Ӧ����Ϊ�ڴ�ľ����������������ã�AndroidΪ���ǿ��ǵ�����һ��
				 * Android�п���ʹ��BitmapFactory�������һ���ڲ���BitmapFactory.Options��ʵ��ͼƬ�Ĵ������ʾ
				 * BitmapFactory��һ�������࣬��������˺ܶ��ֻ�ȡBitmap�ķ�����BitmapFactory.Options������һ��inSampleSize�������趨����ֵΪ8������ص��ڴ��е�ͼƬ�Ĵ�С��
				 * ��ԭͼƬ��1/8��С��������ԶԶ�������ڴ�����ġ�
				 * BitmapFactory.Options op = new BitmapFactory.Options();
				 * op.inSampleSize = 8;
				 * Bitmap pic = BitmapFactory.decodeFile(imageFilePath, op);
				 * ����һ�ֿ�ݵķ�ʽ������һ�Ŵ�ͼ����Ϊ�����ÿ���������ʾ��Ļ�Ĵ�С��ͼƬ��ԭʼ��С
				 * Ȼ����ʱ������Ҫ�������ǵ���Ļ������Ӧ�����ţ���β����أ�
				 * 
				 */
				//����ȡ����Ļ����
				Display display = this.getWindowManager().getDefaultDisplay();
				//��ȡ��Ļ�Ŀ�͸�
				int dw = display.getWidth();
				int dh = display.getHeight();
				/**
				 * Ϊ�˼������ŵı�����������Ҫ��ȡ����ͼƬ�ĳߴ磬������ͼƬ
				 * BitmapFactory.Options������һ�������ͱ���inJustDecodeBounds����������Ϊtrue
				 * ���������ǻ�ȡ���ľ���ͼƬ�ĳߴ磬�����ü���ͼƬ�ˡ�
				 * �������������ֵ��ʱ�����ǽ��žͿ��Դ�BitmapFactory.Options��outWidth��outHeight�л�ȡ��ֵ
				 */
				BitmapFactory.Options op = new BitmapFactory.Options();
				//op.inSampleSize = 8;
				op.inJustDecodeBounds = true;
				//Bitmap pic = BitmapFactory.decodeFile(imageFilePath, op);//������������Ժ�op�е�outWidth��outHeight����ֵ��
				//����ʹ����MediaStore�洢���������URI��ȡ����������ʽ
				Bitmap pic = BitmapFactory.decodeStream(this
						.getContentResolver().openInputStream(imageFilePath),
						null, op);
				int wRatio = (int) Math.ceil(op.outWidth / (float) dw); //�����ȱ���
				int hRatio = (int) Math.ceil(op.outHeight / (float) dh); //����߶ȱ���
				Log.v("Width Ratio:", wRatio + "");
				Log.v("Height Ratio:", hRatio + "");
				/**
				 * �����������Ǿ���Ҫ�ж��Ƿ���Ҫ�����Լ����׶Կ��Ǹ߽������š�
				 * ����ߺͿ���ȫ����������Ļ����ô�������š�
				 * ����ߺͿ���������Ļ��С�������ѡ�������ء�
				 * ����Ҫ�ж�wRatio��hRatio�Ĵ�С
				 * ���һ���������ţ���Ϊ���Ŵ��ʱ��С��Ӧ���Զ�����ͬ�������š�
				 * ����ʹ�õĻ���inSampleSize����
				 */
				if (wRatio > 1 && hRatio > 1) {
					if (wRatio > hRatio) {
						op.inSampleSize = wRatio;
					} else {
						op.inSampleSize = hRatio;
					}
				}
				op.inJustDecodeBounds = false; //ע�����һ��Ҫ����Ϊfalse����Ϊ�������ǽ�������Ϊtrue����ȡͼƬ�ߴ���
				pic = BitmapFactory.decodeStream(this.getContentResolver()
						.openInputStream(imageFilePath), null, op);
				imageView.setImageBitmap(pic);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
   	}
}
