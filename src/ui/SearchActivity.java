package ui;

import java.util.Date;

import model.MainActivity;
import network.ChainRequest;

import com.example.ticketsrobber.R;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

public class SearchActivity extends ActionBarActivity {
	private Button robTicketButton = null;
	private ProgressDialog progressDialog = null;
	private CalendarView calendar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		robTicketButton = (Button) findViewById(R.id.robTicketButton);
		robTicketButton.setOnClickListener(new RobTicketButtonListener());

		calendar = (CalendarView) findViewById(R.id.departureDateField);
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
			intent.setClass(SearchActivity.this, LoginActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	class RobTicketButtonListener implements OnClickListener {
		public void onClick(View view) {
			final Intent intent = new Intent();
			intent.setClass(SearchActivity.this, ShowResultActivity.class);

			final Date date = new Date(2014 - 1900, 7 - 1, 31);
			final String from = "SHH";
			final String to = "SZH";
			final String type = "ADULT";

			intent.putExtra("departureField",
					((EditText) findViewById(R.id.departureField)).getText()
							.toString());
			intent.putExtra("departureDateField",
					((CalendarView) findViewById(R.id.departureDateField))
							.getDate());

			LoginActivity.mAgent.requestBooking(date, from, to, type,
					new ChainRequest.Listener() {

						@Override
						public void onFinished(boolean result) {
							if (result) {
								SearchActivity.this.startActivity(intent);
								progressDialog.cancel();
							} else {
								progressDialog.setMessage("失败。。。");
							}
						}
					});

			// 创建ProgressDialog对象
			progressDialog = new ProgressDialog(SearchActivity.this);
			// 设置进度条风格，风格为圆形，旋转的
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// 设置ProgressDialog 标题
			progressDialog.setTitle("提示");
			// 设置ProgressDialog 提示信息
			progressDialog.setMessage("抢票中..........");
			// 设置ProgressDialog 的进度条是否不明确
			progressDialog.setIndeterminate(false);
			// 设置ProgressDialog 是否可以按退回按键取消
			progressDialog.setCancelable(true);
			// 设置ProgressDialog 的一个Button
			// 让ProgressDialog显示
			progressDialog.show();
		}
	}
}
