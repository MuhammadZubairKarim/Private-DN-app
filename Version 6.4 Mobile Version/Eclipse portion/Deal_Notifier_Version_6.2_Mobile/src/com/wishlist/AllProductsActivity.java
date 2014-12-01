package com.wishlist;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import library.DatabaseHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loginandregistration.DashboardActivity;
import com.zkarim.R;

// This Activity has 'connections' with all the other activities in the app. So handle with care!

public class AllProductsActivity extends ListActivity {


	// Add New Item to Your Wish-List
	Button btnNewProduct;
	
	// Back to Dash-board button
	Button btnBackToDashboard;
	

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
	ArrayList<HashMap<String, String>> productsList;
	
	// Url to get all products list
	private static String url_all_products = "http://192.168.1.100/trial/get_all_products.php";
			
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PID = "pid"; // Is the unique product id. This needs to be changed eventually to match Kyle's specs
	private static final String TAG_BRAND = "Brand";

	// Products JSONArray
	JSONArray products = null;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_products);

		// Button for taking to adding item on wish-list
		Button btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);

		// View products click event
		btnNewProduct.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching create new product activity
				Intent i = new Intent(getApplicationContext(),
						NewProductActivity.class);
				startActivity(i);
			}
		});
		
		// Buttons
				btnBackToDashboard = (Button) findViewById(R.id.btnBackToDashboard);

				// view products click event
				btnBackToDashboard.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						// Launching The AllProduct Activity
						Intent i = new Intent(getApplicationContext(),
								DashboardActivity.class);
						startActivity(i);

					}
				});
		
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllProducts().execute();
		
		// Get listview
		ListView lv = getListView();

		// On selecting single product
		// Launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Getting values from selected ListItem
				String pid = ((TextView) view.findViewById(R.id.pid)).getText()
						.toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						EditProductActivity.class);

				// Sending pid to next activity
				in.putExtra(TAG_PID, pid);

				// Starting new activity and expecting some response back
				startActivityForResult(in, 100);

			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//Toast msg = Toast.makeText(this, "List pressed", Toast.LENGTH_LONG);
		//msg.show();

		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();

			finish();

			startActivity(intent);

		}

	}
	

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AllProductsActivity.this);
			pDialog.setMessage("Loading products. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			DatabaseHandler db = new DatabaseHandler(getBaseContext());
			HashMap<String, String> user = new HashMap<String, String>();
			user = db.getUserDetails();
			
			String uid = user.get("uid");

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("UserId", uid)); 

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_products, "GET",
					params);

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// Products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);

					// Looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String pid = c.getString(TAG_PID);
						String Brand = c.getString(TAG_BRAND);

						// Creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// Adding each child node to HashMap key => value
						map.put(TAG_PID, pid);
						map.put(TAG_BRAND, Brand);

						// Adding HashList to ArrayList
						productsList.add(map);

					}
				} else {
					// No products found
					// Launch Add New product Activity
					Intent i = new Intent(getApplicationContext(),
							NewProductActivity.class);

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
			// Dismiss the dialog after getting all products
			pDialog.dismiss();
			// Updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							AllProductsActivity.this, productsList,
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