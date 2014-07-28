package ui;

import java.util.Timer;
import java.util.TimerTask;

import network.ChainRequest;
import network.TicketAgent;

import com.example.ticketsrobber.R;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

;

public class ShowResultActivity extends ActionBarActivity {
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_result);
		//
		// Intent intent = getIntent();
		// String s = intent.getStringExtra("departureField");
		// ((TextView)findViewById(R.id.departureResult)).setText(s);
		//
		// String ss = intent.getStringExtra("departureDateField");
		// System.out.println("!!!!" + ss + "~~~~~~~~");
		// ((TextView)findViewById(R.id.date)).setText(intent.getStringExtra("departureDateField"));
		doBookRefresh(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(ShowResultActivity.this, SearchActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void doBookRefresh(View view) {
		ImageView imageView = (ImageView) findViewById(R.id.bookCaptcha);
		LoginActivity.mAgent.loadBookCaptcha(imageView);
	}

	public void doSubmit(View view) {
		/*
		 * Timer timer = new Timer(); final Handler handler = new Handler(){
		 * 
		 * public void handleMessage(Message msg) { switch (msg.what) { case 1:
		 * progressDialog.setMessage("成功"); finish(); break; }
		 * super.handleMessage(msg); }
		 * 
		 * }; TimerTask task = new TimerTask(){
		 * 
		 * public void run() { Message message = new Message(); message.what =
		 * 1; handler.sendMessage(message); }
		 * 
		 * };
		 * 
		 * timer.schedule(task, 2000);
		 */

		EditText captchaInput = (EditText) findViewById(R.id.bookCaptchaInput);

		LoginActivity.mAgent.submitBooking(captchaInput.getText().toString(),
				new ChainRequest.Listener() {

					@Override
					public void onFinished(boolean result) {
						if (result) {
							progressDialog.cancel();
							AlertDialog.Builder builder1 = new AlertDialog.Builder(
									ShowResultActivity.this);
							builder1.setMessage("Succeeded");
							builder1.setCancelable(true);
							builder1.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
											ShowResultActivity.this.finish();
										}
									});

							AlertDialog alert11 = builder1.create();
							alert11.show();
						} else {
							progressDialog.setMessage("失败。。。");
						}
					}
				});

		// 创建ProgressDialog对象
		progressDialog = new ProgressDialog(ShowResultActivity.this);
		// 设置进度条风格，风格为圆形，旋转的
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 标题
		progressDialog.setTitle("提示");
		// 设置ProgressDialog 提示信息
		progressDialog.setMessage("提交中..........");
		// 设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		progressDialog.setCancelable(true);
		// 设置ProgressDialog 的一个Button
		// 让ProgressDialog显示
		progressDialog.show();
	}
}
