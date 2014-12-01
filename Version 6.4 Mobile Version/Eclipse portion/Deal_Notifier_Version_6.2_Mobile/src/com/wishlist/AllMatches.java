package com.wishlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zkarim.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AllMatches extends ListActivity {
	

	// Back to wish-list button
	Button btnBackToAllProductsView;
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
	ArrayList<HashMap<String, String>> matchesList;
	
	// Url for getting all matches on this product
	private static final String url_product_matches = "http://192.168.1.100/trial/get_product_matches.php";
	
	// Products JSONArray
	JSONArray products = null;
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PID = "pid"; // Is the unique product id. This needs to be changed eventually to match Kyle's specs
	private static final String TAG_BRAND = "Price";

	// Declaration of strings
	String pid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_matches);
 
		Log.d("Matches", "Oncreate 0.5");
		
		// getting product details from intent
		Intent i = getIntent();

		// getting product id (pid) from intent
		pid = i.getStringExtra(TAG_PID);
		
		Log.d("Matches", "Oncreate 0.75");
		
		Log.d("Getting intent from Matches", pid); // Causing Null Pointer exception...
		
		Log.d("Matches", "Oncreate 1");
		// Buttons
				btnBackToAllProductsView = (Button) findViewById(R.id.btnBackToAllProductsView);

				// view products click event
				btnBackToAllProductsView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						// Launching The AllProduct Activity
						Intent i = new Intent(getApplicationContext(),
								AllProductsActivity.class);
						startActivity(i);

					}
				});	
				
				
			Log.d("Matches", "Oncreate 2");
			
			// Hashmap for ListView
			matchesList = new ArrayList<HashMap<String, String>>();

			// Loading products in Background Thread
			new LoadAllMatches().execute();
				
	
}
	
	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllMatches extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d("Matches", "Oncreate 3");
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			
			Log.d("Matches", "Oncreate 4");
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("WishId", pid)); // Need to get the wish Id here somewhow

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_product_matches, "GET",
					params);
			Log.d("Matches", "Oncreate 5");
			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				Log.d("Matches", "Oncreate 6");
				if (success == 1) {
					// Products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);
					Log.d("Matches", "Oncreate 7");
					// Looping through All Products
					String test = Integer.toString(products.length());
					Log.d("bleh", test);
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						Log.d("Matches", "Oncreate 7.5");
						String pid_internal = c.getString(TAG_PID);
						String Brand = c.getString(TAG_BRAND);
						Log.d("Matches", "Oncreate 8");
						// Creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						Log.d("Matches", "Oncreate 9");
						// Adding each child node to HashMap key => value
						Log.d("Matches", "Oncreate 10");
						map.put(TAG_PID, pid_internal);
						map.put(TAG_BRAND, Brand);
						Log.d("Matches", "Oncreate 11");
						// Adding HashList to ArrayList
						matchesList.add(map);
						Log.d("Matches", "Oncreate 12");
					}
				} else {
					// No products found
					// Launch Add New product Activity
					Log.d("Matches", "Oncreate 13");
					Intent i = new Intent(getApplicationContext(),
							AllProductsActivity.class);

					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			
			// Updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							AllMatches.this, matchesList,
							R.layout.list_item, new String[] { TAG_PID,
									TAG_BRAND },
							new int[] { R.id.pid, R.id.brand });
					// Updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
	
}