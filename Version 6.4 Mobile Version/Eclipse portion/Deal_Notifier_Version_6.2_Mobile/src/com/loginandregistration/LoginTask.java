package com.loginandregistration;

import library.DatabaseHandler;
import library.UserFunctions;
import org.json.JSONException;
import org.json.JSONObject;

import com.zkarim.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.EditText;

public class LoginTask extends AsyncTask<String, Void, Integer> {

	private ProgressDialog progressDialog;
	private LoginActivity activity;
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

	public LoginTask(LoginActivity activity, ProgressDialog progressDialog) {
		this.activity = activity;
		this.progressDialog = progressDialog;
	}

	@Override
	protected void onPreExecute() {
		progressDialog.show();
	}

	protected Integer doInBackground(String... arg0) {
		EditText Usernameedit = (EditText) activity.findViewById(R.id.loginUsername);
		EditText passwordEdit = (EditText) activity
				.findViewById(R.id.loginPassword);
		String Username = Usernameedit.getText().toString();
		String password = passwordEdit.getText().toString();
		UserFunctions userFunction = new UserFunctions();
		JSONObject json = userFunction.loginUser(Username, password);

		// Check for login response
		try {
			if (json.getString(KEY_SUCCESS) != null) {
				String res = json.getString(KEY_SUCCESS);

				if (Integer.parseInt(res) == 1) {	
					// User successfully logged in
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
					
					responseCode = 1;
					// Close Login Screen

				} else {
					responseCode = 0;
					// Error in login
				}
			}

		} catch (NullPointerException e) {
			e.printStackTrace();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return responseCode;
	}

	@Override
	protected void onPostExecute(Integer responseCode) {
		activity.findViewById(R.id.loginUsername);
		activity
				.findViewById(R.id.loginPassword);

		if (responseCode == 1) {
			progressDialog.dismiss();
			Intent i = new Intent();
			i.setClass(activity.getApplicationContext(),
					DashboardActivity.class);
			activity.startActivity(i);
			// activity.loginReport(responseCode);
		} else {
			progressDialog.dismiss();
			activity.showLoginError(responseCode);

		}

	}
}