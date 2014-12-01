package com.loginandregistration;

import com.zkarim.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	Button btnRegister;
	Button btnLinkToLogin;
	EditText inputUserName;
	EditText inputEmail;
	EditText inputPassword;
	EditText inputFirstName;
	EditText inputLastName;
	EditText inputAge;
	EditText inputCountry;
	EditText inputPostalCode;
	TextView registerErrorMsg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// Importing all assets like buttons and text fields
		inputUserName = (EditText) findViewById(R.id.registerUser_Name);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		inputFirstName = (EditText) findViewById(R.id.registerFirst_Name);
		inputLastName = (EditText) findViewById(R.id.registerLast_Name);
		inputAge = (EditText) findViewById(R.id.registerAge);
		inputCountry = (EditText) findViewById(R.id.registerCountry);
		inputPostalCode = (EditText) findViewById(R.id.registerPostal_Code);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ProgressDialog progressDialog = new ProgressDialog(
						RegisterActivity.this);
				
				progressDialog.setMessage("Registering...");
				
				RegisterTask registerTask = new RegisterTask(
						RegisterActivity.this, progressDialog);
				
				registerTask.execute();
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				
				startActivity(i);
				
				// Close Registration View
				finish();
			}
		});
	}

	public void registerReport(Integer responseCode) {
		int duration = Toast.LENGTH_LONG;
		Context context = getApplicationContext();

		if (responseCode == 0) {
			Toast toast = Toast.makeText(context, "Register Error", duration);
			toast.show();
		} else {
			Toast toast = Toast.makeText(context, "Register Success", duration);
			toast.show();
			Intent i = new Intent(getApplicationContext(),
					DashboardActivity.class);
			startActivity(i);
			finish();
		}

	}
}