package com.example.qr_codescan;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private TextView mTextView;
	private ListView mListView;
	private ArrayList<String> list1 = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private SharedPreferences.Editor editor;
	private static final String fileName = "sharedfile";// 定义保存的文件的名称

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) findViewById(R.id.result);
		mListView = (ListView) findViewById(R.id.list);
		Button mButton = (Button) findViewById(R.id.button1);
		Button mButton2 = (Button) findViewById(R.id.button2);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);

		SharedPreferences predata = super.getSharedPreferences(fileName,
				MODE_PRIVATE);
		list1.clear();
		int sizes = predata.getInt("data_size", 0);
		for (int i = 0; i < sizes; i++) {
			String temp = predata.getString("data" + i, null);
			list1.add(temp);
			adapter.add(temp);
		}
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});

		mButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog2();
				
			}
		});
	}

	protected void dialog2() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
    	builder.setMessage("确认清空吗？");
    	builder.setTitle("提示");
    	builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.dismiss();
    			//放入确认键的代码
    			if (adapter != null) {
					adapter.clear();
					adapter.notifyDataSetChanged();
				}
				// mListView.setAdapter(null);
				if (list1.size() > 0) {
					SharedPreferences.Editor mSP=getSharedPreferences(fileName, 0).edit();
					mSP.clear();
					mSP.commit();
					list1.clear();
				}
    			
    		}
    	});
    	builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.dismiss();
    		}
    	});
    	builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();

				mTextView.setText(bundle.getString("result"));
				if (list1.size() > 0) {
					for (String tmp : list1) {
						if (tmp.indexOf(bundle.getString("result")) != -1) {
							// AlertDialog.Builder dialog=new Builder(this);
							// dialog.setTitle("?");
							Toast.makeText(this,
									tmp.substring(0, tmp.indexOf("：")),
									Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}

				Time t = new Time();
				t.setToNow();
				String format = t.year + "-" + (t.month + 1) + "-" + t.monthDay
						+ "-" + t.hour + ":" + t.minute + "："
						+ bundle.getString("result");
				adapter.add(format);
				adapter.notifyDataSetChanged();
				list1.add(format);
				// mImageView.setImageBitmap((Bitmap)
				// data.getParcelableExtra("bitmap"));
				// mListView.add(1,bundle.getString("result")+"");
				// arr=bundle.getString("result")+"\n";

				editor = super.getSharedPreferences(fileName, MODE_PRIVATE)
						.edit();
				editor.putInt("data_size", list1.size());
				for (int i = 0; i < list1.size(); i++) {
					editor.putString("data" + i, list1.get(i));
				}
				editor.commit();
			}
			break;
		}
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("确定退出吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
		}
		return false;
	}
}
