/*
 * Copyright 2011-2014 Rafael Iñigo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vikingbrain.dmt.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.util.DmtLogger;

/**
 * Implementation of generic DAO.
 * 
 * @author Rafael Iñigo
 */
public class GenericDAO extends SQLiteOpenHelper {

	private static final String TAG = GenericDAO.class.getSimpleName();
	
	private static SQLiteDatabase db;

	public static final int DATABASE_VERSION = 9;
	private static String DATABASE_NAME = "databaseMMT";
	private static final String ASSET_DB_PATH = "databases";

	public enum TypeOrder {
		ASC, DESC
	};

	private static GenericDAO instance;

	private final Context mContext;
	private String mUpgradePathFormat;

	/**
	 * Constructor.
	 * @param context the Android context
	 */
	private GenericDAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		mContext = context;

		mUpgradePathFormat = ASSET_DB_PATH + "/" + DATABASE_NAME + "_upgrade_%s-%s.sql";
	}

	/**
	 * Get instrance of GenericDAO.
	 * @param ctx the Android context
	 * @return the generic DAO
	 */
	public static GenericDAO getInstance(Context ctx) {
		if (instance == null) {
			instance = new GenericDAO(ctx);
			try {
				DmtLogger.w(TAG, "Creating or opening the database [ "	+ DATABASE_NAME + " ].");
				db = instance.getWritableDatabase();
			} catch (SQLiteException se) {
				DmtLogger.e(TAG, "Cound not create and/or open the database [ " + DATABASE_NAME + " ] that will be used for reading and writing.", se);
			}
		}
		return instance;
	}

	/**
	 * Close database.
	 */
	public void close() {
		if (instance != null) {
			DmtLogger.w(TAG, "Closing the database [ " + DATABASE_NAME + " ].");
			db.close();
			instance = null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DmtLogger.w(TAG, "Trying to create database.");
		try {
			onUpgrade(db, Constants.NUMBER_ZERO, DATABASE_VERSION);
		} catch (SQLException se) {
			DmtLogger.e(TAG, "Cound not create the database table according to the SQL statement.", se);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		DmtLogger.w(TAG, "Upgrading database " + DATABASE_NAME + " from version " + oldVersion + " to " + newVersion + "...");

		ArrayList<String> paths = new ArrayList<String>();
		getUpgradeFilePaths(oldVersion, newVersion - 1, newVersion, paths);

		if (paths.isEmpty()) {
			DmtLogger.e(TAG, "no upgrade script path from " + oldVersion + " to " + newVersion);
			throw new SQLiteAssetException("no upgrade script path from " + oldVersion + " to " + newVersion);
		}

		Collections.sort(paths);
		for (String path : paths) {
			try {
				DmtLogger.w(TAG, "processing upgrade: " + path);
				InputStream is = mContext.getAssets().open(path);
				String sql = convertStreamToString(is);
				if (sql != null) {
					String[] cmds = sql.split(";");
					for (String cmd : cmds) {
						// Log.d(TAG, "cmd=" + cmd);
						if (cmd.trim().length() > 0) {
							db.execSQL(cmd);
						}
					}
				}
			} catch (IOException e) {
				DmtLogger.e(TAG, "Eror with files.", e);
			} catch (SQLException se) {
				DmtLogger.e(TAG, "Cound not execute onUpgrade.", se);
			}

		}

		DmtLogger.i(TAG, "Successfully upgraded database " + DATABASE_NAME	+ " from version " + oldVersion + " to " + newVersion);

	}

	/**
	 * Insert data values into a table.
	 * @param table the table name
	 * @param values the values
	 * @return the row ID
	 */
	public long insert(String table, ContentValues values) {
		return db.insert(table, null, values);
	}

	/**
	 * Get database cursor.
	 * @param table the table name
	 * @param columns the column names
	 * @param orderBy the order by
	 * @param orderType the order type
	 * @return the cursor
	 */
	public Cursor get(String table, String[] columns, String orderBy, TypeOrder orderType) {
		Cursor cursor = db.query(table, columns, null, null, null, null, "UPPER(" + orderBy + ") " + orderType);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * Get database cursor.
	 * @param table the table name
	 * @param columns the column names
	 * @param id the id
	 * @return the cursor
	 */
	public Cursor get(String table, String[] columns, long id) {
		Cursor cursor = db.query(true, table, columns, Constants.PRIMARY_KEY_ID + "=" + id, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * Get database cursor.
	 * @param table the table name
	 * @param columns the column names
	 * @param where where condition
	 * @return the cursor
	 */
	public Cursor get(String table, String[] columns, String where) {
		Cursor cursor = db.query(true, table, columns, where, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * Delete table.
	 * @param table table name
	 * @return the number of rows affected
	 */
	public int delete(String table) {
		return db.delete(table, "1", null);
	}

	/**
	 * Delete object with id from a table
	 * @param table table name
	 * @param id object id
	 * @return the number of rows affected
	 */
	public int delete(String table, long id) {
		return db.delete(table, Constants.PRIMARY_KEY_ID + "=" + id, null);
	}

	/**
	 * Update the content for an object by id for a table
	 * @param table the table name
	 * @param id the id
	 * @param values the new values
	 * @return the number of rows affected
	 */
	public int update(String table, long id, ContentValues values) {
		return db.update(table, values, Constants.PRIMARY_KEY_ID + "=" + id, null);
	}

	/**
	 * Get the SQL script file from assets corresponding to the database upgrade.
	 * @param oldVersion the previous database version
	 * @param newVersion the new database version
	 * @return the SQL script with the upgrade
	 */
	private InputStream getUpgradeSQLStream(int oldVersion, int newVersion) {
		String path = String.format(mUpgradePathFormat, oldVersion, newVersion);
		try {
			InputStream is = mContext.getAssets().open(path);
			return is;
		} catch (IOException e) {
			DmtLogger.e(TAG, "missing database upgrade script: " + path);
			return null;
		}
	}

	/**
	 * Load in paths variable all the SQL script required to upgrade the
	 * database schema from some database version to another higher. 
	 * @param baseVersion the base version
	 * @param start the version start
	 * @param end the version end
	 * @param paths the file paths
	 */
	private void getUpgradeFilePaths(int baseVersion, int start, int end,
			ArrayList<String> paths) {

		int a;
		int b;

		InputStream is = getUpgradeSQLStream(start, end);
		if (is != null) {
			String path = String.format(mUpgradePathFormat, start, end);
			paths.add(path);
			// CustomLogger.d(TAG, "found script: " + path);
			a = start - 1;
			b = start;
			is = null;
		} else {
			a = start - 1;
			b = end;
		}

		if (a < baseVersion) {
			return;
		} else {
			getUpgradeFilePaths(baseVersion, a, b, paths); // recursive call
		}

	}

	/**
	 * Convert input stream to string
	 * @param is the input stream
	 * @return the string
	 */
	private String convertStreamToString(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}

	/**
	 * An exception that indicates there was an error with SQLite asset retrieval or parsing. 
	 * @author Rafael Iñigo
	 *
	 */
	public class SQLiteAssetException extends SQLiteException {

		/** serialVersionUID. **/
		private static final long serialVersionUID = -2728453103557943876L;

		/**
		 * Constructor.
		 */
		public SQLiteAssetException() {}

		/**
		 * Constructor.
		 * @param error string with the error
		 */
	    public SQLiteAssetException(String error) {
	        super(error);
	    }
	}
}