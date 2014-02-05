package com.example.routeoptimization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private Button addAdr;
	
	private TextView tvWayPoints;
	private Button routeButton;
	private String googleHttpResponse = null;
	private JSONArray googleWayPoints;

	private ArrayList<Integer> addrIDs;
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initWidgets();
		addrIDs = new ArrayList<Integer>();
		addrIDs.add(R.id.etAddress1);
	}

	private void initWidgets() {
		addAdr = (Button) findViewById(R.id.bAddAddr);
		routeButton = (Button) findViewById(R.id.bGetRoute);	
		addAdr.setOnClickListener(this);
		routeButton.setOnClickListener(this);
		
		
		tvWayPoints = (TextView) findViewById(R.id.tvWayPoints);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bAddAddr:
			addAddress();
			break;
		case R.id.bGetRoute:
			startHttpRequest();		
			googleWayPoints = generateJSONWayPoints(googleHttpResponse);			
			tvWayPoints.setText(googleWayPoints.toString());
			addAddressButtons(googleWayPoints);
			break;
		}
		
	}
	
	private void startHttpRequest(){

		Thread thread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		    	String urlString;					
				URI myURL;
				try {
					urlString = getGoogleAddressURL();
					myURL = new URI(urlString);
					getHttpResponse(myURL);	
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      
		    }
		});
		thread.start();	
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getHttpResponse(URI url){
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(new HttpGet(url));
			 StatusLine statusLine = response.getStatusLine();
			    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			        ByteArrayOutputStream out = new ByteArrayOutputStream();
			        response.getEntity().writeTo(out);
			        out.close();
			        googleHttpResponse = out.toString();			        			        
			    } else{
			        //Closes the connection.
			        response.getEntity().getContent().close();
			        throw new IOException(statusLine.getReasonPhrase());
			    }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void addAddress() {
		LinearLayout l = (LinearLayout) findViewById(R.id.myLinearLayout);
		EditText et = new EditText(getApplicationContext());
		et.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		et.setHint("Address");
		int gen_id = generateViewId();
		et.setId(gen_id);
		et.setTextColor(Color.BLACK);
		addrIDs.add(gen_id);
		l.addView(et);
	}

	private String getGoogleAddressURL() {
		int numAddress = addrIDs.size();
		String [] addresses = getAddressArray();

		String origin = addresses[0];
		String destination = addresses[numAddress - 1];
		String wayPoints = "";
		for (int i = 1; i <= numAddress - 2; i++) {
			if (i == numAddress - 2) {
				wayPoints += addresses[i];
			} else {
				wayPoints += addresses[i] + "|";
			}
		}
		String urlString = null;
		try {
			urlString = "http://maps.googleapis.com/maps/api/directions/json?origin="
					+ origin
					+ "&destination="
					+ destination
					+ "&waypoints="
					+ URLEncoder.encode("optimize:true|" + wayPoints, "UTF-8")
					+ "&sensor=false";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlString;
	}
	
	private String[] getAddressArray(){
		int numAddress = addrIDs.size();
		String[] addresses = new String[numAddress];// addresses

		for (int i = 0; i < numAddress; i++) {
			addresses[i] = ((EditText) findViewById(addrIDs.get(i))).getText()
					.toString();
		}
		return addresses;
	}

	private int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1;
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	public JSONArray generateJSONWayPoints(String httpResponse) {
		JSONArray wayPointOrder = null;
		if (httpResponse != null) {
			try {
				JSONObject obj = new JSONObject(httpResponse);
				JSONArray arr = obj.getJSONArray("routes");

				for (int i = 0; i < arr.length(); i++) {
					if (arr.getJSONObject(i).has("waypoint_order")) {
						wayPointOrder = arr.getJSONObject(i).getJSONArray(
								"waypoint_order");
						return wayPointOrder;
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return wayPointOrder;
	}
	
	private void addAddressButtons(JSONArray wayPoints){
		String [] addresses = getAddressArray();	
		
		int numWayPoints = wayPoints.length();
		int numAddr = addresses.length;
		
		
		
		String [] rightAddressOrder = new String[numAddr];
		rightAddressOrder[0] = addresses[0];
		rightAddressOrder[numAddr-1] = addresses[numAddr-1];
		
	
		
		for(int i = 0; i < numWayPoints;i++ ){
			int addrIndex;
			try {
				addrIndex = wayPoints.getInt(i);
				rightAddressOrder[i+1] = addresses[addrIndex+1];				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		String previous = rightAddressOrder[0];
		for(int i = 1; i < numAddr; i++){
			String current = rightAddressOrder[i];
			addNavigationButton(previous,current,i);
			previous = current;
		}
	}
	
	private void addNavigationButton(final String startAddr, final String endAddr,int index){
		LinearLayout l = (LinearLayout) findViewById(R.id.myLinearLayout2);
		Button button = new Button(getApplicationContext());
		button.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		button.setText("Trip " + index);
		int gen_id = generateViewId();
		button.setId(gen_id);		
		l.addView(button);		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String mapURL = "http://maps.google.com/maps?source=s_d&saddr=" +startAddr + "&daddr=" 
						 + endAddr;
						
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(mapURL));
				startActivity(intent);	
	        }			
	    });
		
		button.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				LinearLayout l = (LinearLayout) findViewById(R.id.myLinearLayout2);
				l.removeView(v);
				return true;	           
	        }
	    });
	}

	
	

}