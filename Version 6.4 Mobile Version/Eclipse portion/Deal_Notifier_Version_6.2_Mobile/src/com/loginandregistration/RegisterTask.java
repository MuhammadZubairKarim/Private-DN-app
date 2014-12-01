package com.loginandregistration;

import library.DatabaseHandler;
import library.UserFunctions;
import org.json.JSONException;
import org.json.JSONObject;

import com.zkarim.R;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.EditText;

public class RegisterTask extends AsyncTask<String, Void, Integer> {

	private ProgressDialog progressDialog;
	private RegisterActivity activity;
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid"; //The crazy large number which acts as our new and temporary User Id and works for now
	private static String KEY_USERNAME = "username";
	private static String KEY_EMAIL = "email";
	private static String KEY_JOIN_DATE = "join_date";
	private static String KEY_FIRST_NAME = "first_name";
	private static String KEY_LAST_NAME = "last_name";
	private static String KEY_AGE = "age";
	private static String KEY_COUNTRY = "country";
	private static String KEY_POSTAL_CODE = "postal_code";
	private int responseCode = 0;

	//Custom method to help initiate validations
	int boolToInt(Boolean b) {
	    return b.compareTo(false);
	}   
	
	/*
	 * Constructor that takes parameters passed by LoginFragment and stores them
	 * as class- wide fields so that all methods can access necessary variables.
	 */
	public RegisterTask(RegisterActivity activity, ProgressDialog progressDialog) {
		this.activity = activity;
		this.progressDialog = progressDialog;
	}

	/*
	 * A necessary but very simple method that launches a ProgressDialog to show
	 * the user that a background task is operating (registration).
	 */
	@Override
	protected void onPreExecute() {
		progressDialog.show();
	}
	
	
	/*
	 * This method does almost all of the work for the class. It builds a
	 * connection to the local server, collects the details from the UI of the user's
	 * information, and then tries to register the user with the SQL database.
	 * All of the actual HTTP connection work is done in a background library
	 * class for security - including the hashing of a password into a 64bit
	 * encryption.
	 */
	@Override
	protected Integer doInBackground(String... arg0) {
		EditText Email = (EditText) activity
				.findViewById(R.id.registerEmail);
		EditText passwordEdit = (EditText) activity
				.findViewById(R.id.registerPassword);
		EditText UsernameEdit = (EditText) activity.findViewById(R.id.registerUser_Name);
		EditText FirstnameEdit = (EditText) activity.findViewById(R.id.registerFirst_Name);
		EditText LastnameEdit = (EditText) activity.findViewById(R.id.registerLast_Name);
		EditText AgeEdit = (EditText) activity.findViewById(R.id.registerAge);
		EditText CountryEdit = (EditText) activity.findViewById(R.id.registerCountry);
		EditText PostalCodeEdit = (EditText) activity.findViewById(R.id.registerPostal_Code);
		// Moves on the next step
		String username = UsernameEdit.getText().toString();
		String email = Email.getText().toString();
		String password = passwordEdit.getText().toString();
		String first_name = FirstnameEdit.getText().toString();
		String last_name = LastnameEdit.getText().toString();
		String age = AgeEdit.getText().toString();
		String country = CountryEdit.getText().toString();
		String postal_code = PostalCodeEdit.getText().toString();
		Log.v(email, password);
		UserFunctions userFunction = new UserFunctions();
		JSONObject json = userFunction.registerUser(username, email, password, first_name, last_name, age, country, postal_code);

		// Check for login response
		try {
			if (json.getString(KEY_SUCCESS) != null) {

				String res = json.getString(KEY_SUCCESS);
				if (Integer.parseInt(res) == 1) {
					
					// New user has been successfully registered
					// Store user details in SQLite Database
					DatabaseHandler db = new DatabaseHandler(
							activity.getApplicationContext());
					JSONObject json_user = json.getJSONObject("user");
					
					// Clear all previous data in database
					userFunction.logoutUser(activity.getApplicationContext());
					db.addUser(json.getString(KEY_UID),
							json_user.getString(KEY_USERNAME),
							json_user.getString(KEY_EMAIL),
							json_user.getString(KEY_JOIN_DATE),
							json_user.getString(KEY_FIRST_NAME),
							json_user.getString(KEY_LAST_NAME),
							json_user.getString(KEY_AGE),
							json_user.getString(KEY_COUNTRY),
							json_user.getString(KEY_POSTAL_CODE));
					
					// successful registration
					responseCode = 1;
				} else {
					// Error in registration
					responseCode = 0;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseCode;
	}

	/*
	 * This final method concludes the background task. Its responseCode
	 * variable is sent from doInBackground, and this method acts based on the
	 * code it is sent. If the code is 1, registration was successful and the
	 * main activity notifies the user of success - the inverse occurs if there
	 * is a failure and 0 was sent.
	 */
	@Override
	protected void onPostExecute(Integer responseCode) {
		EditText Email = (EditText) activity
				.findViewById(R.id.registerEmail);
		EditText passwordEdit = (EditText) activity
				.findViewById(R.id.registerPassword);
		Email.getText().toString();

		if (responseCode == 1) {
			progressDialog.dismiss();
			activity.registerReport(responseCode);
			Email.setText("");
			passwordEdit.setText("");
		}
		if (responseCode == 0) {
			progressDialog.dismiss();
			activity.registerReport(responseCode);
		}
	}
}