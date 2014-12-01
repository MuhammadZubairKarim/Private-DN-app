package library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {

	private JSONParser jsonParser;

	private static String loginURL = "http://192.168.1.100/trial/";
	private static String registerURL = "http://192.168.1.100/trial/";

	private static String login_tag = "login";
	private static String register_tag = "register";
	
	// Constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	// Login with user-name and password provided by client
	public JSONObject loginUser(String username, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("Username", username)); 
		params.add(new BasicNameValuePair("Password", password)); 

		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		return json;
	}

	// Register a new user with the following variables
	public JSONObject registerUser(String username, String email, String password, String first_name, String last_name, String age, String country, String postal_code) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("Username", username)); 
		params.add(new BasicNameValuePair("Email", email));
		params.add(new BasicNameValuePair("Password", password));
		params.add(new BasicNameValuePair("FirstName", first_name));
		params.add(new BasicNameValuePair("LastName", last_name));
		params.add(new BasicNameValuePair("Age", age));
		params.add(new BasicNameValuePair("Country", country));
		params.add(new BasicNameValuePair("ZipCode", postal_code));

		// Getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		
		return json;
	}

	// Determine whether the user is logged in or not
	public boolean isUserLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if (count > 0) {
			// When user is logged in
			return true;
		}
		return false;
	}

	// Logout
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
}