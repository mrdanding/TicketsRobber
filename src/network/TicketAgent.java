package network;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import khandroid.ext.apache.http.HttpHost;
import khandroid.ext.apache.http.conn.params.ConnRoutePNames;
import khandroid.ext.apache.http.cookie.Cookie;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;
import khandroid.ext.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import khandroid.ext.apache.http.params.CoreConnectionPNames;

import org.json.JSONException;
import org.json.JSONObject;

import util.ExtHttpClientStack;
import util.SslHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticketsrobber.R;

public class TicketAgent {
	//private static final String BOOK_IMG_URL = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=passenger&rand=randp";
	private static final String BOOK_IMG_URL = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand";
	private static final String LOGIN_IMG_URL = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand";
	private static final String CHK_CAPTCHA_URL = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
	private static final String CHK_USER_URL = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";
	private static final String LOGIN_URL = "https://kyfw.12306.cn/otn/login/userLogin";
	private static final String BOOK_URL = "http://qiangpiaowang.cloudapp.net/Home/submitOrderRequest";
	private static final String SUBMIT_URL = "http://qiangpiaowang.cloudapp.net/Home/ConfirmRequest";
	private RequestQueue mSslQueue;
	private DefaultHttpClient mHttpClient;
	private String bookString;

	public TicketAgent(Activity activity) {
		// Replace R.raw.test with your keystore
		InputStream keyStore = activity.getResources().openRawResource(
				R.raw.keystore);

		// Usually getting the request queue shall be in singleton like in {@see
		// Act_SimpleRequest}
		// Current approach is used just for brevity
		mHttpClient = new SslHttpClient(keyStore, "123123");
		//mHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		//		new HttpHost("10.86.196.131", 8080, "http"));
		mHttpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 50000);// 连接时间
		mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				50000);// 数据传输时间
		mHttpClient
				.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
						0, false));
		mSslQueue = Volley.newRequestQueue(activity, new ExtHttpClientStack(
				mHttpClient));
	}

	public void clearCookies() {
		mHttpClient.getCookieStore().clear();
	}

	public String getCookiesString() {
		StringBuffer cookieSB = new StringBuffer();
		for (Cookie cookie : mHttpClient.getCookieStore().getCookies()) {
			cookieSB.append(cookie.getName() + "=" + cookie.getValue() + "; ");
		}
		return cookieSB.toString().substring(0, cookieSB.length() - 2);
	}

	public void loadLoginCaptcha(final ImageView imageView) {
		loadCaptcha(LOGIN_IMG_URL, new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				imageView.setImageBitmap(response);
				imageView.setScaleType(ScaleType.FIT_XY);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO: imageView.setImageResource(R.drawable.default_image);
			}
		});
	}

	public void loadBookCaptcha(final ImageView imageView) {
		loadCaptcha(BOOK_IMG_URL, new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				imageView.setImageBitmap(response);
				imageView.setScaleType(ScaleType.FIT_XY);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO: imageView.setImageResource(R.drawable.default_image);
			}
		});
	}

	public void requestLogin(final String username, final String password,
			final String captcha, ChainRequest.Listener listener) {
		new ChainRequest(new ChainRequest.Action[] { new ChainRequest.Action() {

			@Override
			public String getUrl() {
				return CHK_CAPTCHA_URL;
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("randCode", captcha);
				params.put("rand", "sjrand");
				return params;
			}

			@Override
			public boolean onSuccess(String response) {
				if (response == null) {
					return false;
				}
				try {
					JSONObject jResult = new JSONObject(response);
					if (!"Y".equals(jResult.getString("data"))) {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			public boolean onFailed(VolleyError error) {
				return false;
			}

		}, new ChainRequest.Action() {

			@Override
			public String getUrl() {
				return CHK_USER_URL;
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("loginUserDTO.user_name", username);
				params.put("userDTO.password", password);
				params.put("randCode", captcha);
				return params;
			}

			@Override
			public boolean onSuccess(String response) {
				if (response == null) {
					return false;
				}
				try {
					JSONObject jResult = new JSONObject(response);
					if (!"Y".equals(jResult.getJSONObject("data").getString(
							"loginCheck"))) {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			public boolean onFailed(VolleyError error) {
				return false;
			}

		}, new ChainRequest.Action() {

			@Override
			public String getUrl() {
				return LOGIN_URL;
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("_json_att", "");
				return params;
			}

			@Override
			public boolean onSuccess(String response) {
				return false;
			}

			@Override
			public boolean onFailed(VolleyError error) {
				return error.networkResponse.statusCode == 302;
			}
		} }, listener).execute(mSslQueue);
	}

	public void requestBooking(final Date date, final String from,
			final String to, final String type, ChainRequest.Listener listener) {
		bookString = null;
		new ChainRequest(new ChainRequest.Action[] { new ChainRequest.Action() {

			@Override
			public String getUrl() {
				return BOOK_URL;
			}

			@SuppressLint("SimpleDateFormat")
			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("cookie", getCookiesString());
				params.put("trainDate",
						new SimpleDateFormat("yyyy-MM-dd").format(date));
				params.put("fromSation", from); // typo on azure
				params.put("toStation", to);
				params.put("purposeCode", type);
				return params;
			}

			@Override
			public boolean onSuccess(String response) {
				if (response == null) {
					return false;
				}
				if (response.split("\\|").length == 6) {
					bookString = response;
					return true;
				}
				return false;
			}

			@Override
			public boolean onFailed(VolleyError error) {
				return false;
			}

		} }, listener).execute(mSslQueue);
	}

	public void submitBooking(final String captcha,
			ChainRequest.Listener listener) {
		assert bookString != null;
		new ChainRequest(new ChainRequest.Action[] { new ChainRequest.Action() {

			@Override
			public String getUrl() {
				return SUBMIT_URL;
			}

			@SuppressLint("SimpleDateFormat")
			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				try {
					bookString = URLDecoder.decode(bookString, "UTF-8");
					String[] parts = bookString.split("\\|");
					params.put("cookie", getCookiesString());
					params.put("passengerTicketStr", parts[0]);
					params.put("oldPassengerStr", parts[1]);
					params.put("purposeCode", parts[2]);
					params.put("keyCheckIsChange", parts[3]);
					params.put("leftTicketStr", parts[4]);
					params.put("trainLocation", parts[5]);
					params.put("randCode", captcha);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return params;
			}

			@Override
			public boolean onSuccess(String response) {
				if (response == null) {
					return false;
				}
				Log.i("submitBooking", "result: " + response);
				return true;
			}

			@Override
			public boolean onFailed(VolleyError error) {
				return false;
			}

		} }, listener).execute(mSslQueue);
	}

	private void loadCaptcha(String url, Listener<Bitmap> listener,
			ErrorListener errorListener) {
		ImageRequest request = new ImageRequest(url, listener, 0, 0,
				Config.RGB_565, errorListener);
		mSslQueue.add(request);
	}
}
