package com.wishlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wishlist.JSONParser;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditProductActivity extends Activity implements OnItemSelectedListener {
	
	int 	 CID;
	int		 origCategoryId;
	Spinner  spinner;
	Spinner  inputCategoryId; // New addition
	EditText inputBrand;
	EditText inputModelNumber;
	EditText inputUPC;
	EditText inputOfferPrice;
	EditText inputKeyword1;
	EditText inputKeyword2;
	EditText inputKeyword3;
	EditText inputKeyword4;
	EditText inputKeyword5;
	EditText txtCreatedAt;
	Button btnSave;
	Button btnDelete;
	Button btnMatches;
	String CategoryIds;
	// The unique product Id for items within the wish-list table
	String pid; 
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	// Creating JSON Parser object for categories spinner
	JSONParser jParser = new JSONParser(); // For the categories
		
	List<String> categoriesList = new ArrayList<String>();
		
	// products JSONArray
	JSONArray categories = null;
	
	
	// Cancel button needs to be deleted!!!
	//Button btnBackToAllProductsView;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	// Url to get all categories list
	private static String url_all_categories = "http://192.168.1.100/trial/get_all_categories.php";

	// Url for getting the details of a single product 
	private static final String url_product_details = "http://192.168.1.100/trial/get_product_details.php";

	// Url to update product
	private static final String url_update_product = "http://192.168.1.100/trial/update_product.php";

	// Url to delete product
	private static final String url_delete_product = "http://192.168.1.100/trial/delete_product.php";
	
	// JSON Node names for categories NEED TO IDENTIFY WHETHER THIS IS NEEDED OR NOT
	private static final String TAG_SUCCESSS = "success"; // Need to figure this out!
	private static final String TAG_CATEGORIES = "categories";
	private static final String TAG_ID = "Id";
	private static final String TAG_NAME = "Name";
			
	// For Categories spinner, a context needs to be initialized for passing in as one of the arguments
	private Context context;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_PID = "pid"; //The unofficial unique product id
	private static final String TAG_CATEGORY_ID = "CategoryId";
	//private static final String TAG_CATEGORYID = "origCategoryId";
	private static final String TAG_CAT_ID = "CategoryIds"; // This works fine!!
	private static final String TAG_OFFER_PRICE = "OfferPrice";
	private static final String TAG_BRAND = "Brand";
	private static final String TAG_MODEL_NUMBER = "ModelNumber";
	private static final String TAG_UPC = "UPC";
	private static final String TAG_KEYWORD1 = "Keyword1";
	private static final String TAG_KEYWORD2 = "Keyword2";
	private static final String TAG_KEYWORD3 = "Keyword3";
	private static final String TAG_KEYWORD4 = "Keyword4";
	private static final String TAG_KEYWORD5 = "Keyword5";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_product);
		context = this;
		
		// Save button
		btnSave = (Button) findViewById(R.id.btnSave);
		// Delete button
		btnDelete = (Button) findViewById(R.id.btnDelete);
		// Matches button
		btnMatches = (Button) findViewById(R.id.btnShowAllMatches);
		
		// getting product details from intent
		Intent i = getIntent();

		// getting product id (pid) from intent
		pid = i.getStringExtra(TAG_PID);
		
		Log.d("Edit-Product Getting intent", pid);
		
		// Getting complete product details in background thread
		new GetProductDetails().execute();
		
		//Loading all categories in background Thread whenever this Activity is loaded
		new LoadAllCategories().execute();

		spinner = (Spinner) findViewById(R.id.edit_categories_spinner);
						
		// spinner item select listener
		spinner.setOnItemSelectedListener(this);

		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				new SaveProductDetails().execute();
			}
		});

		// Delete button click event
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting product in background thread
				new DeleteProduct().execute();
			}
		});
		
		// Matches button
		// What happens when you click the button
		btnMatches.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// takes you back to all matches activity screen
				Intent i = new Intent(getApplicationContext(),
						AllMatches.class);
				i.putExtra(TAG_PID, pid);
				startActivity(i);
			}
		});
		
		
		
		// Cancel button
				Button btnbtnBackToAllProductsView = (Button) findViewById(R.id.btnBackToAllProductsView);
				
				// What happens when you click the button
				btnbtnBackToAllProductsView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						// takes you back to all products activity screen
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
							int CID = (origCategoryId - 1); // This hacky works for now!
							//int tmp = ((origCategoryId-1) < 0) ? 0 : origCategoryId; // Not required anymore.
							spinner.setSelection(CID); 
						}
					});
				}
			}
	
	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetProductDetails extends AsyncTask<String, String, JSONObject> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditProductActivity.this);
			pDialog.setMessage("Loading your wish-list details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected JSONObject doInBackground(String... params) {

					// Check for success tag
					int success;
					JSONObject product = null;
					try {
						// Building Parameters
						List<NameValuePair> params0 = new ArrayList<NameValuePair>();
						params0.add(new BasicNameValuePair("Id",pid)); // Effort to match Kyle's specs. The "Id" gets passed to the server-side scripting language, hence this tag should match Exactly with the Specs!!

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_product_details, "GET", params0);

						// check your log for json response
						Log.d("Single Product Details", json.toString());

						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray(TAG_PRODUCT); // JSON Array
							
							// get first product object from JSON Array
							product = productObj.getJSONObject(0);
						} else {
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
			
			return product;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(JSONObject my_prod) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
			
			// product with this pid found
			inputOfferPrice = (EditText) findViewById(R.id.inputOfferPrice);
			inputBrand = (EditText) findViewById(R.id.inputBrand);
			inputModelNumber = (EditText) findViewById(R.id.inputModelNumber);
			inputUPC = (EditText) findViewById(R.id.inputUPC);
			inputKeyword1 = (EditText) findViewById(R.id.inputKeyword1);
			inputKeyword2 = (EditText) findViewById(R.id.inputKeyword2);
			inputKeyword3 = (EditText) findViewById(R.id.inputKeyword3);
			inputKeyword4 = (EditText) findViewById(R.id.inputKeyword4);
			inputKeyword5 = (EditText) findViewById(R.id.inputKeyword5);

			try
			{
				// display product data in EditText
				inputOfferPrice.setText(my_prod.getString(TAG_OFFER_PRICE));
				origCategoryId = Integer.parseInt(my_prod.getString(TAG_CATEGORY_ID));
				inputBrand.setText(my_prod.getString(TAG_BRAND));
				inputModelNumber.setText(my_prod.getString(TAG_MODEL_NUMBER));
				inputUPC.setText(my_prod.getString(TAG_UPC));
				inputKeyword1.setText(my_prod.getString(TAG_KEYWORD1));
				inputKeyword2.setText(my_prod.getString(TAG_KEYWORD2));
				inputKeyword3.setText(my_prod.getString(TAG_KEYWORD3));
				inputKeyword4.setText(my_prod.getString(TAG_KEYWORD4));
				inputKeyword5.setText(my_prod.getString(TAG_KEYWORD5));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Custom method to help initiate validations
		int boolToInt(Boolean b) {
		    return b.compareTo(false);
		}
		
	/**
	 * Background Async Task to Save product Details
	 * */
	class SaveProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Adding validations while updating products
			// Bad practice of copy and pasting code - temporary solution
			// Strings Offer-price and Brand are not required for validation checks 
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
				Log.d("IsCancelled", "Checking5");
				cancel(true);
			}
			else {
				
				pDialog = new ProgressDialog(EditProductActivity.this);
				pDialog.setMessage("Saving your updated wish-list ...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
			}
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {
			for (int i = 0; i < 10000; i++){
				if (isCancelled()) {
					Log.d("IsCancelled", "Checking6");
					return null;
				}
			}

			// getting updated data from EditTexts
			String OfferPrice = inputOfferPrice.getText().toString();
			String Brand = inputBrand.getText().toString();
			String ModelNumber = inputModelNumber.getText().toString();
			String UPC = inputUPC.getText().toString();
			String Keyword1 = inputKeyword1.getText().toString();
			String Keyword2 = inputKeyword2.getText().toString();
			String Keyword3 = inputKeyword3.getText().toString();
			String Keyword4 = inputKeyword4.getText().toString();
			String Keyword5 = inputKeyword5.getText().toString();

			// Building Parameters
			// Updating products!!
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_PID, pid));
			params.add(new BasicNameValuePair(TAG_CAT_ID, CategoryIds));
			params.add(new BasicNameValuePair(TAG_OFFER_PRICE, OfferPrice));
			params.add(new BasicNameValuePair(TAG_BRAND, Brand));
			params.add(new BasicNameValuePair(TAG_MODEL_NUMBER, ModelNumber));
			params.add(new BasicNameValuePair(TAG_UPC, UPC));
			params.add(new BasicNameValuePair(TAG_KEYWORD1, Keyword1));
			params.add(new BasicNameValuePair(TAG_KEYWORD2, Keyword2));
			params.add(new BasicNameValuePair(TAG_KEYWORD3, Keyword3));
			params.add(new BasicNameValuePair(TAG_KEYWORD4, Keyword4));
			params.add(new BasicNameValuePair(TAG_KEYWORD5, Keyword5));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_product,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
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
			// dismiss the dialog once product updated
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete Product
	 * */
	class DeleteProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditProductActivity.this);
			pDialog.setMessage("Deleting...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("Id", pid));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_product, "POST", params);

				// check your log for json response
				Log.d("Delete Product", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about product deletion
					setResult(100, i);
					finish();
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
			// dismiss the dialog once product deleted
			pDialog.dismiss();

		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// Printing selected item id in Log.d Adding + 1 to compensate for count starting from 0 and not from 1!
			parent.getItemIdAtPosition(position);
			CategoryIds = Integer.toString(position + 1);
			Toast.makeText( getApplicationContext(), parent.getItemIdAtPosition(position + 1) + " Id Selected",
				                Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
		
	}

}
