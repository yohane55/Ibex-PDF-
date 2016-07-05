package com.ibexreader.mibooksi.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

public class BooksDb {
	private static final String DB_NAME = "mibooks.db";
	private SQLiteDatabase db;
	private final Context context;
	private final BooksDbHelper dbhelper;
	public static String MAIN_TABLE_NAME = "books";

	public BooksDb(Context c) {
		context = c;
		dbhelper = new BooksDbHelper(context, DB_NAME, null,1);
	}

	public void close() {
		db.close();
	}

	/**
	 * returns readable SQLite database
	 * 
	 * @throws SQLiteException
	 */
	public void open() throws SQLiteException {
		if (db == null || !db.isOpen())
			db = dbhelper.getWritableDatabase();
	}

	public SQLiteDatabase getDatabase() {
		if (db!=null && db.isOpen()) {
			return db;
		} else {
			open();
			return db;
		}
	}

	public ArrayList<Book> getAllRows() {

		ArrayList<Book> b  = new ArrayList<>();
		Cursor c = db.query(MAIN_TABLE_NAME, null, null, null,
				null, null, null);
		if (c != null && c.moveToFirst()) {



			do {

				if(!TextUtils.isEmpty(c.getString(c.getColumnIndex(BooksDbHelper.URI)))){
					final File f = new File(Uri.parse(c.getString(c.getColumnIndex(BooksDbHelper.URI))).getPath());
					if(f.exists()) {


						final Book book = new Book(c.getInt(0), c.getString(c.getColumnIndex(BooksDbHelper.NAME)),
								Uri.parse(c.getString(c.getColumnIndex(BooksDbHelper.URI))),
								c.getInt(c.getColumnIndex(BooksDbHelper.FAVORITE)) == 1,
								c.getString(c.getColumnIndex(BooksDbHelper.EXTRA1)));

						b.add(book);
					}
//					else{
//						delete(c.getInt(0));
//					}

				}

			} while (c.moveToNext());
		}

		return b;
	}



	public Cursor getAllIDs() {
		String[] ID_Col = new String[] { "_id" };
		Cursor c = db.query(MAIN_TABLE_NAME, ID_Col, null, null,
				null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;

	}

	public Cursor getRows(int start, int end) {
		Cursor cur = db.rawQuery("SELECT * from " + MAIN_TABLE_NAME
				+ " LIMIT " + start + "," + end, null); // db.query(TABLE_NAME,
														// null, null, null,
														// null,
		// null, null);
		if (cur != null) {
			cur.moveToFirst();
		}
		return cur;
	}




	public long delete(int id){

		try {
			 return db.delete(MAIN_TABLE_NAME,"_id = "+id,null);
		}catch (SQLiteException ex){
			ex.printStackTrace();
		}




		return -1;
	}


	public long add(String name, String url, int fav, String data) {
		Cursor c =null;
		try{
			 c = db.query(MAIN_TABLE_NAME, null, BooksDbHelper.URI+" = ?",new String[]{url} , null, null, null);
		}catch (SQLiteException e){
			e.printStackTrace();
		}

		if(c!=null && c.getCount() > 0){
			return -100;
		}

		ContentValues cv = new ContentValues();

		cv.put(BooksDbHelper.NAME,name);
		cv.put(BooksDbHelper.URI,url);
		cv.put(BooksDbHelper.FAVORITE,fav);
		cv.put(BooksDbHelper.EXTRA1,data);

		try{
			return db.insert(MAIN_TABLE_NAME,null,cv);
		}catch (SQLiteException e){
			e.printStackTrace();
		}

		return -1;

	}

	public ArrayList<Book> getFAVs() {
		Cursor c =null;
		final ArrayList<Book> Books = new ArrayList<>();

		try{
			c = db.query(MAIN_TABLE_NAME, null, BooksDbHelper.FAVORITE+"=?", new String[]{""+1},null,null,null);
		}catch (SQLiteException e){
			e.printStackTrace();
		}


		if(c!=null && c.moveToFirst()){
			do{
				 final Book s = new Book(c.getInt(0),c.getString(c.getColumnIndex(BooksDbHelper.NAME)),
						Uri.parse(c.getString(c.getColumnIndex(BooksDbHelper.URI))),
						c.getInt(c.getColumnIndex(BooksDbHelper.FAVORITE)) ==1,
						c.getString(c.getColumnIndex(BooksDbHelper.EXTRA1)));
				Books.add(s);
			}while (c.moveToNext());
		}

		return Books;

	}

	public Book getBook(int id) {
		Cursor c =null;


		try{
			c = db.query(MAIN_TABLE_NAME, null, "_id = "+id, null,null,null,null);
		}catch (SQLiteException e){
			e.printStackTrace();
		}


		if(c!=null && c.moveToFirst()){

				final Book s = new Book(c.getInt(0),c.getString(c.getColumnIndex(BooksDbHelper.NAME)),
						Uri.parse(c.getString(c.getColumnIndex(BooksDbHelper.URI))),
						c.getInt(c.getColumnIndex(BooksDbHelper.FAVORITE))==1,
						c.getString(c.getColumnIndex(BooksDbHelper.EXTRA1)));
				return s;

		}

		return null;
	}

	public long update(int id, String name, String url, int fav, String data) {
		ContentValues cv = new ContentValues();

		cv.put(BooksDbHelper.NAME,name);
		cv.put(BooksDbHelper.URI,url);
		cv.put(BooksDbHelper.FAVORITE,fav);
		cv.put(BooksDbHelper.EXTRA1,data);

		try{
			return db.update(MAIN_TABLE_NAME,cv,"_id = "+id, null);
		}catch (SQLiteException e){
			e.printStackTrace();
		}

		return -1;

	}
}