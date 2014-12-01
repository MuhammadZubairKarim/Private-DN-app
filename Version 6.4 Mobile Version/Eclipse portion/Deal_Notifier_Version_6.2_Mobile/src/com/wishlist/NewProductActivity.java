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

import com.zkarim.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewProductActivity extends Activity implements OnItemSelectedListener {

	// Progress Dialog
	private ProgressDialog pDialog;
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser(); // For the categories
	
	List<String> categoriesList = new ArrayList<String>();
	
	// Initializing array for list of categories.. JSONArray
	 JSONArray categories = null;

	// Cancel button
	Button btnbtnBackToAllProductsView;
	
	// List of declarations 
	JSONParser jsonParser = new JSONParser();
	Spinner  spinner;
	EditText inputOfferPrice;
	EditText inputBrand;
	EditText inputModelNumber;
	EditText inputUPC;
	EditText inputKeyword1;
	EditText inputKeyword2;
	EditText inputKeyword3;
	EditText inputKeyword4;
	EditText inputKeyword5;
	String   CategoryId;
	

	// Url to create new product
	private static String url_create_product = "http://192.168.1.100/trial/create_product.php";
	
	// Url to get all categories list
	private static String url_all_categories = "http://192.168.1.100/trial/get_all_categories.php";	

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SUCCESSS = "success"; // Need to figure this out!
	private static final String TAG_CATEGORIES = "categories";
	private static final String TAG_ID = "Id";
	private static final String TAG_NAME = "Name";
	
	// For Categories spinner, a context needs to be initialized for passing in as one of the arguments
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_product);
		context = this;
		
		// Loading all categories in background Thread whenever this Activity is loaded
		new LoadAllCategories().execute();
	
		spinner = (Spinner) findViewById(R.id.categories_spinner);
		
		// Spinner item select listener
		 spinner.setOnItemSelectedListener(this);
		
		// Edit Text
		inputOfferPrice = (EditText) findViewById(R.id.inputOfferPrice);
		inputBrand = (EditText) findViewById(R.id.inputBrand);
		inputModelNumber = (EditText) findViewById(R.id.inputModelNumber);
		inputUPC = (EditText) findViewById(R.id.inputUPC);
		inputKeyword1 = (EditText) findViewById(R.id.inputKeyword1);
		inputKeyword2 = (EditText) findViewById(R.id.inputKeyword2);
		inputKeyword3 = (EditText) findViewById(R.id.inputKeyword3);
		inputKeyword4 = (EditText) findViewById(R.id.inputKeyword4);
		inputKeyword5 = (EditText) findViewById(R.id.inputKeyword5);

		// Create button
		Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);

		// Button click event
		btnCreateProduct.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Creating new product in background thread
				new CreateNewProduct().execute();
				
			}
		});
		
		
		// Cancel button
		Button btnbtnBackToAllProductsView = (Button) findViewById(R.id.btnBackToAllProductsView);
		
		//  Button click event
		btnbtnBackToAllProductsView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Takes you back to all products activity screen
				Intent i = new Intent(getApplicationContext(),
						AllProductsActivity.class);
				startActivity(i);
			}
		});
		
	}
	
	// Categories section! 
	
			/**
			 * Background Async Task to Load all categories by making HTTP Request
			 * */
			class LoadAllCategories extends AsyncTask <String, String, String> {

				/**
				 * Before starting background thread Show Progress Dialog
				 * */
				@Override
				protected void onPreExecute() {
					super.onPreExecute();

				}

				/**
				 * getting All categories from url
				 * */
				protected String doInBackground(String... args) {
				
					// Building Parameters
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("",""));

					// getting JSON string from URL
					JSONObject json = jParser.makeHttpRequest(url_all_categories, "GET",
							params);

					try {
						// Checking for SUCCESS TAG
						int success = json.getInt(TAG_SUCCESSS);

						if (success == 1) {
							// Getting Array of Categories
							categories = json.getJSONArray(TAG_CATEGORIES);

							// Looping through All Categories
							for (int i = 0; i < categories.length(); i++) {
								JSONObject c = categories.getJSONObject(i);
								
								// Storing each JSON item in variable
								String Name = c.getString(TAG_NAME);
								String Id	= c.getString(TAG_ID);

								// Creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// Adding each child node to HashMap key => value
								map.put(Name, Id);
								// Adding HashList to ArrayList
								categoriesList.add(Name);

							}
						} else {
							// This else portion should not be needed because there should always be a list of categories
							
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
					runOnUiThread(new Runnable() {
						public void run() {
							/**
							 * Updating parsed JSON data into ListView
							 * */
							// Create an ArrayAdapter using the string array and a default spinner layout
							ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
							         android.R.layout.simple_spinner_item, categoriesList);
							// Specify the layout to use when the list of choices appears
							adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							 // Apply the adapter to the spinner
							spinner.setAdapter(adapter);
						}
					});

				}

			}
	
			// Custom method to help initiate validations
			int boolToInt(Boolean b) {
			    return b.compareTo(false);
			}    

	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Brand and Offer-price are not required for validation check
			// CategoryId is already a String ...
			String ModelNumber = inputModelNumber.getText().toString();
			String UPC = inputUPC.getText().toString();
			String Keyword1 = inputKeyword1.getText().toString();
			String Keyword2 = inputKeyword2.getText().toString();
			String Keyword3 = inputKeyword3.getText().toString();
			String Keyword4 = inputKeyword4.getText().toString();
			String Keyword5 = inputKeyword5.getText().toString();
			
			// Do Error Checking Here!
			
			Log.d("IsCancelled", "Checking1");		
			if (UPC.isEmpty() && ModelNumber.isEmpty() && (boolToInt(Keyword1.isEmpty()) 
				+ boolToInt(Keyword2.isEmpty()) + boolToInt(Keyword3.isEmpty())
				+ boolToInt(Keyword4.isEmpty()) + boolToInt(Keyword5.isEmpty()) > 3)) {
			Log.d("IsCancelled", "Checking2");
				cancel(true);
			}
						
			else {
				pDialog = new ProgressDialog(NewProductActivity.this);
				pDialog.setMessage("Creating Product..");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
		}
	}
			

		/**
		 * Adding an item to the wish-list
		 * */
		protected String doInBackground(String... args) {
			for (int i = 0; i < 10000; i++){
				if (isCancelled()) {
					Log.d("IsCancelled", "Checking3");
					return null;
				}
			}
			DatabaseHandler db = new DatabaseHandler(getBaseContext());
			HashMap<String, String> user = new HashMap<String, String>();
			user = db.getUserDetails();
			
			// This uid is coming from the db handler
			String uid = user.get("uid");
			String OfferPrice = inputOfferPrice.getText().toString();
			String Brand = inputBrand.getText().toString();
			String ModelNumber = inputModelNumber.getText().toString();
			String UPC = inputUPC.getText().toString();
			String Keyword1 = inputKeyword1.getText().toString();
			String Keyword2 = inputKeyword2.getText().toString();
			String Keyword3 = inputKeyword3.getText().toString();
			String Keyword4 = inputKeyword4.getText().toString();
			String Keyword5 = inputKeyword5.getText().toString();

			Log.d("OH MY Please WORK!", uid);
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("UserId", uid)); 
			params.add(new BasicNameValuePair("CategoryId", CategoryId)); // Selected Id of categories list
			params.add(new BasicNameValuePair("OfferPrice", OfferPrice));
			params.add(new BasicNameValuePair("Brand", Brand));
			params.add(new BasicNameValuePair("ModelNumber", ModelNumber));
			params.add(new BasicNameValuePair("Upc", UPC));
			params.add(new BasicNameValuePair("Keyword1", Keyword1));
			params.add(new BasicNameValuePair("Keyword2", Keyword2));
			params.add(new BasicNameValuePair("Keyword3", Keyword3));
			params.add(new BasicNameValuePair("Keyword4", Keyword4));
			params.add(new BasicNameValuePair("Keyword5", Keyword5));

			// Getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"POST", params);
			Log.d("Checking for response", "Checking for JSON posted");
			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				Log.d("Checking for response", "Checking for success");
				if (success == 1) {
					// Successfully created product
					Intent i = new Intent(getApplicationContext(),
							AllProductsActivity.class);
					startActivity(i);
					Log.d("Checking for response", "Checking for in All Products Activity");

					// closing this screen
					finish();
				} else {
					// failed to create product
					Log.d("Checking for response", "Checking for failure");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}
		
		protected void onCancelled(String file_url){
			Log.d("IsCancelled", "Checking4");
			int duration = Toast.LENGTH_LONG;
			Context context = getApplicationContext();
			Toast toast = Toast.makeText(context, "Please enter the UPC or Model Number or two Keywords!", duration);
			toast.show();
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// Dismiss the dialog once done
			pDialog.dismiss();
		}

	}
	// To detect which item has been selected on the Spinner
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// Printing selected item id in Log.d Adding + 1 to compensate for count starting from 0 and not from 1!
		parent.getItemIdAtPosition(position + 1);
		CategoryId = Integer.toString(position + 1);
		
		//Toast.makeText(
          //      getApplicationContext(),
            //            parent.getItemIdAtPosition(position + 1) + " Id Selected",
              //  Toast.LENGTH_LONG).show();
		
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
		
	}
}
