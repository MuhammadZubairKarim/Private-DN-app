package com.loginandregistration;

import java.util.HashMap;

import com.wishlist.AllProductsActivity;
import com.zkarim.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import library.DatabaseHandler;
import library.UserFunctions;

public class DashboardActivity extends Activity {
	
	UserFunctions userFunctions;
	Button btnLogout;
	Button btnViewProducts;
	DatabaseHandler dbHandler;
	private HashMap<String, String> user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userFunctions = new UserFunctions();
		if (userFunctions.isUserLoggedIn(getApplicationContext())) {
			// If user successfully logs in, move on the dash-board
			setContentView(R.layout.dashboard);
			btnLogout = (Button) findViewById(R.id.btnLogout);

			dbHandler = new DatabaseHandler(getApplicationContext());
			user = dbHandler.getUserDetails();
			TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
			usernameTextView.setText(user.get("username"));

			btnLogout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					userFunctions.logoutUser(getApplicationContext());
					Intent login = new Intent(getApplicationContext(),
							LoginActivity.class);
					login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(login);
					// Closing dash-board screen
					finish();
				}
			});

		} else {
			// user is not logged in show login screen
			Intent login = new Intent(getApplicationContext(),
					LoginActivity.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			// Closing dash-board screen
			finish();
		}

		// Buttons
		btnViewProducts = (Button) findViewById(R.id.btnViewProducts);

		// view products click event
		btnViewProducts.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching The AllProduct Activity
				Intent i = new Intent(getApplicationContext(),
						AllProductsActivity.class);
				startActivity(i);

			}
		});

	}

}