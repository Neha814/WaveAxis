package com.example.functions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;

@SuppressWarnings("deprecation")
public class JSONParser {
	static InputStream is = null;
	static String jObj = "";
	static String json = "";
	private int error_code;

	/* Error */

	/**
	 * 
	 * @param url
	 * @param method
	 *            (post or get) method will decide which method is to be used.
	 * @param paramList
	 *            contains name value pair which is to be send in http request.
	 * @return String
	 * 
	 * 
	 */

	public String makeHttpRequest(String url, String method,
			java.util.List<org.apache.http.NameValuePair> paramList) {

		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url);

		String result = null;

		try {
			Log.e("URL===>", "" + url + paramList);
			httppost.setEntity(new UrlEncodedFormEntity(paramList));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				Log.i("STATUS OK", "STATUS OK");

				result = out.toString();

			} else {
				// close connection
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (Exception e) {

			Log.i("error encountered", "" + e);
		}
		Log.i("web service result=======", "==" + result);
		return result;

	}
	
	
}
