package com.ibexreader.mibooksi.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BooksDbHelper extends SQLiteOpenHelper {

	public static final String NAME = "book_name";
	public static final String URI = "book_url";
	public static final String FAVORITE = "book_fav";
	public static final String EXTRA1 = "book_data";
	private static final String TABLE_CREATE = "CREATE TABLE " + BooksDb.MAIN_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " +
			NAME + " TEXT, " +
			URI + " TEXT, "+
			FAVORITE + " INTEGER, "+
			EXTRA1 + " TEXT"
			+");";
	private static String DB_NAME;
	private String DB_PATH;
	private final Context mCon;

	public BooksDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		mCon = context;

	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ BooksDb.MAIN_TABLE_NAME);
		onCreate(db);


	}

//	public SQLiteDatabase openDB() {
//		SQLiteDatabase db = null;
//		try {
//			SQLiteDatabase localSQLiteDatabase = SQLiteDatabase.openDatabase(
//					this.DB_PATH + DB_NAME, null, 0);
//			db = localSQLiteDatabase;
//			return db;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}