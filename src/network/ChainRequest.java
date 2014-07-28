package network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.StringRequest;

public class ChainRequest {
	static public interface Action {
		String getUrl();

		Map<String, String> getParams();

		boolean onSuccess(String response);

		boolean onFailed(VolleyError error);
	}

	static public interface Listener {
		void onFinished(boolean result);
	}

	Queue<Action> mActionQue;
	Listener mListener;

	public ChainRequest(Action actions[], Listener listener) {
		mActionQue = new LinkedList<Action>(Arrays.asList(actions));
		mListener = listener;
	}

	private class PostRequest extends StringRequest {
		private Map<String, String> params;
		private Map<String, String> headers;

		public PostRequest(String url, Map<String, String> params,
				Response.Listener<String> listener, ErrorListener errorListener) {
			super(Request.Method.POST, url, listener, errorListener);
			this.params = params;
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			headers = new HashMap<String, String>(super.getHeaders());
			return headers;
		}

		@Override
		protected Map<String, String> getParams() throws AuthFailureError {
			return params;
		}
	}

	void execute(final RequestQueue queue) {
		if (mActionQue.isEmpty()) {
			mListener.onFinished(true);
			return;
		}
		final Action action = mActionQue.poll();
		String url = action.getUrl();
		Map<String, String> params = action.getParams();

		PostRequest request = new PostRequest(url, params,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (!action.onSuccess(response)) {
							mListener.onFinished(false);
							return;
						}
						execute(queue);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if (!action.onFailed(error)) {
							mListener.onFinished(false);
							return;
						}
						execute(queue);
					}
				});
		queue.add(request);
	}
}