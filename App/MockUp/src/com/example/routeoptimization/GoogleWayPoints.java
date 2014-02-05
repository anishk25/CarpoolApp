package com.example.routeoptimization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GoogleWayPoints {

	private String urlString;
	private JSONArray wayPointOrder;
	private String httpResponse;

	public GoogleWayPoints(String urlString) {
		this.urlString = urlString;
		this.wayPointOrder = null;
		httpResponse=null;
	}

	public void generateJSONWayPoints() {
		getWayPointsString();
		if (httpResponse != null) {
			try {
				JSONObject obj = new JSONObject(httpResponse);
				JSONArray arr = obj.getJSONArray("routes");

				for (int i = 0; i < arr.length(); i++) {
					if (arr.getJSONObject(i).has("waypoint_order")) {
						wayPointOrder = arr.getJSONObject(i).getJSONArray(
								"waypoint_order");
						break;
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public JSONArray getWayPointOrder(){
		return this.wayPointOrder;
	}

	private void getWayPointsString() {	
		Thread httpThread = new Thread(new Runnable() {
		
			@Override
			public void run() {
				URI uri;
				try {
					uri = new URI(urlString);
					httpResponse = getResponse(uri);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		httpThread.start();
		
		try {
			httpThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	private String getResponse(URI uri) {
		String responseString = null;
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(new HttpGet(uri));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();			
			} else {
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
			return responseString;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
