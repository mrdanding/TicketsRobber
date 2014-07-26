package ui;

import ui.SearchActivity.RobTicketButtonListener;

import com.example.ticketsrobber.R;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {
	ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		Button loginButton = (Button)findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new LoginButtonListener());
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
		// automatically handle clicksafs on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class LoginButtonListener implements OnClickListener{
		public void onClick(View view){
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, SearchActivity.class);
			startActivity(intent);
			//创建ProgressDialog对象  
            progressDialog = new ProgressDialog(LoginActivity.this);  
            // 设置进度条风格，风格为圆形，旋转的  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            // 设置ProgressDialog 标题  
            progressDialog.setTitle("提示");  
            // 设置ProgressDialog 提示信息  
            progressDialog.setMessage("登陆中..........");  
            // 设置ProgressDialog 的进度条是否不明确  
            progressDialog.setIndeterminate(false);           
            // 设置ProgressDialog 是否可以按退回按键取消  
            progressDialog.setCancelable(true);           
            //设置ProgressDialog 的一个Button  
            // 让ProgressDialog显示  
            progressDialog.show();  
		}
	}
}
