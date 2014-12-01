package library;

import java.util.HashMap;
import org.apache.http.NameValuePair;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables:
	
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "trial";

	// Login table name
	private static final String TABLE_LOGIN = "login";

	// Login table columns names
	private static final String KEY_ID = "id";
	private static final String KEY_UID = "unique_id";// The crazy large number which acts as out new and temporary User Id and works for now
	private static final String KEY_USERNAME = "username";
	public static final String KEY_EMAIL = "email";
	private static final String KEY_JOIN_DATE = "join_date";
	private static final String KEY_FIRST_NAME = "first_name";
	private static final String KEY_LAST_NAME = "last_name";
	private static final String KEY_AGE = "age";
	private static final String KEY_COUNTRY = "country";
	private static final String KEY_POSTAL_CODE = "postal_code";
	

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_UID + " TEXT," + KEY_USERNAME + " TEXT," 
				+ KEY_EMAIL + " TEXT UNIQUE," + KEY_JOIN_DATE + " TEXT," 
				+ KEY_FIRST_NAME + " TEXT," + KEY_LAST_NAME + " TEXT," 
				+ KEY_AGE + " INTEGER," + KEY_COUNTRY + " TEXT,"
				+ KEY_POSTAL_CODE + " INTEGER" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String uid, String username, String email, String join_date, String first_name, String last_name, String age, String country, String postal_code) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UID, uid);
		values.put(KEY_USERNAME, username); 
		values.put(KEY_EMAIL, email); 
		values.put(KEY_JOIN_DATE, join_date); 
		values.put(KEY_FIRST_NAME, first_name); 
		values.put(KEY_LAST_NAME, last_name); 
		values.put(KEY_AGE, age); 
		values.put(KEY_COUNTRY, country); 
		values.put(KEY_POSTAL_CODE, postal_code); 

		// Inserting Row
		db.insert(TABLE_LOGIN, null, values);
		// Closing database connection
		db.close(); 
	}

	public void addUser(String email) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_EMAIL, email); 

		// Inserting Row
		db.insert(TABLE_LOGIN, null, values);
		db.close(); 
	}

	public NameValuePair getUser() {
		return null;
	}

	/**
	 * Getting user-data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("uid", cursor.getString(1));
			user.put("username", cursor.getString(2));
			user.put("email", cursor.getString(3));
			user.put("join_date", cursor.getString(4));
			user.put("first_name", cursor.getString(5));
			user.put("last_name", cursor.getString(6));
			user.put("age", cursor.getString(7));
			user.put("country", cursor.getString(8));
			user.put("postal_code", cursor.getString(9));
		}
		cursor.close();
		db.close();
		return user;
	}

	/**
	 * Getting user login status return true if rows are there in table
	 * */
	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}

	public String returnRows() {
		String response = "";
		String countQuery = "SELECT * FROM " + TABLE_LOGIN;
		SQLiteDatabase db = this.getReadableDatabase();
		for (int j = 0; j < getRowCount(); j++) {
			Cursor cursor = db.rawQuery(countQuery, null);
			response += cursor.getColumnName(j);
		}
		Log.v("LT", response);
		return response;
	}

	/**
	 * Re-create database Delete all tables and create them again
	 * */
	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_LOGIN, null, null);
		db.close();
	}

}