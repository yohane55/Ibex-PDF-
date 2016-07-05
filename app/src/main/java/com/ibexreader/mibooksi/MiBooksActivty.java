package com.ibexreader.mibooksi;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAd;
import com.ibexreader.mibooksi.adapters.Book;
import com.ibexreader.mibooksi.adapters.BookAdapter;
import com.ibexreader.mibooksi.adapters.BooksDb;

import java.io.File;
import java.util.ArrayList;

public class MiBooksActivty extends AppCompatActivity {

	private static final String PREF_COLUMN_COUNT = "column_count";
	protected BookAdapter adpter;
//    protected BookAdapter rec_adpter;
    private String TAG="MiBook";
    private ProgressBar spinner_loading;
    BooksDb booksDb;
	private static Book lastOpendBook=null;
	private RecyclerView rv;

	private int column_count =2;
	private GridLayoutManager gridLM;
	private int LOADED =0;
	private int NORMAL=0;
	private int RECENT=1;
	private InterstitialAd interstitial;
	private Toolbar toolbar;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_books_activty);
        toolbar  = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        booksDb = new BooksDb(this);
        booksDb.open();

		AdSettings.addTestDevice("e52317a3a67f5363cdca0f0e939040f8");


		interstitial = new InterstitialAd(this,"1742346906022582_1742349886022284");
		interstitial.loadAd();

		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);


		column_count =sp.getInt(PREF_COLUMN_COUNT,column_count);

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && column_count>3) {
			column_count =3;
		}

			rv = (RecyclerView) findViewById(R.id.books_recycler);
		gridLM =/* new LinearLayoutManager(this); //*/ new GridLayoutManager(this,column_count);
        rv.setLayoutManager(gridLM);
		rv.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.items_offset));
//        RecyclerView recentrv = (RecyclerView) findViewById(R.id.recent_recycler);
//        recentrv.setLayoutManager(new GridLayoutManager(this,3));


        adpter = new BookAdapter(this, new ArrayList<Book>(),false);
		adpter.setColumnCount(column_count);

        spinner_loading = (ProgressBar) findViewById(R.id.loading_spinner);

        adpter.setOnItemSelectedListner(new BookAdapter.OnItemSelectedListner() {
            @Override
            public void onItemSelected(int pos) {



                Book bk = adpter.getItem(pos);


				lastOpendBook = bk;
                booksDb.add(bk.Title,bk.uri.toString(),0,"");

                Intent intent = new Intent(MiBooksActivty.this,MuPDFActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(bk.uri);
                startActivity(intent);

				showIntertitial();

            }
        });

        rv.setAdapter(adpter);

//        if (fname.endsWith(".jfif"))
//            return true;
//        if (fname.endsWith(".jfif-tbnl"))
//            return true;
//        if (fname.endsWith(".tif"))
//            return true;
//        if (fname.endsWith(".tiff"))

		loaNormal();

    }

	private void showIntertitial() {
		if(interstitial!=null && interstitial.isAdLoaded()){
			interstitial.show();
		}
	}

	@Override
    protected void onPause() {
        booksDb.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        booksDb.open();
		if(adpter!=null){
			adpter.refreshItem(lastOpendBook);
		}
//        spinner_loading.setVisibility(View.GONE);
    }



	private class Scanner extends AsyncTask<String,Void,ArrayList<Book>>{

		private boolean mExternalStorageAvailable;
		private boolean mExternalStorageWriteable;

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            showScanningProgress(true);
        }

        @Override
        protected ArrayList<Book> doInBackground(String... params) {
            return searchFile(params);


        }


        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            super.onPostExecute(books);


            showScanningProgress(false);

            Log.e(TAG, "onPostExecute: Books found "+ books.size()  );
            adpter.setNewData(books);


        }

		void updateExternalStorageState() {
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mExternalStorageAvailable = mExternalStorageWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				mExternalStorageAvailable = true;
				mExternalStorageWriteable = false;
			} else {
				mExternalStorageAvailable = mExternalStorageWriteable = false;
			}
			Log.d(TAG, "updateExternalStorageState: ExtrnalStorage avialable:"+mExternalStorageAvailable + " " +
					"writable:"+mExternalStorageWriteable);
		}

        private ArrayList<Book> searchFile(String... params) {
            ArrayList<Book> result = new ArrayList<Book>();
			updateExternalStorageState();
            if(mExternalStorageAvailable) {
				searchRecursive(result, Environment.getExternalStorageDirectory(), params);

			}
//			if(Environment.isExternalStorageEmulated()) {
//				searchRecursive(result, Environment.getDataDirectory(), params);
//			}



				return result;
        }

        private void searchRecursive(ArrayList<Book> result,File dir, String... params){
            Book book;

			if(null == dir || dir.listFiles()== null) return;

            for (File f : dir.listFiles()) {

//                Log.d(TAG, "searchRecursive: "+f.getName());
                if(f!=null && f.isFile() && containsKeyWord(f,params) && notHiiden(f)){


                    book = new Book(-1,f.getName(),Uri.fromFile(f),false,null);
//                    book.uri = Uri.fromFile(f);
//                    book.Title = f.getName();
//                    book.setType(f.getName());

                    result.add(book);

                }else if(f!=null && f.isDirectory() && notHiiden(f)){
                    searchRecursive(result,f,params);
                }
            }
        }

		private boolean notHiiden(File f) {
			return f!=null && !f.isHidden();
		}

		private boolean containsKeyWord(File f, String[] params) {
            for(String p: params){
//                Log.d(TAG, "searchRecursive: "+p);
                if(f.getName().toLowerCase().endsWith(p.toLowerCase())){
                    return true;
                }
            }


            return false;
        }

    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.recent_books){

			if(LOADED == NORMAL) {
				loadRecents();
				item.setIcon(R.drawable.ic_device_black);
				item.setTitle("LOAD ALL");
			}else{
				item.setIcon(R.drawable.ic_recent_black);
				item.setTitle("LOAD RECENT");
				loaNormal();

			}
			return true;
		}else if(item.getItemId() == R.id.colomn){
			changeColumn();
			return true;
		}else if(item.getItemId() == R.id.about){
			Dialog  dialog=  new Dialog(this);
			dialog.setTitle("About Ibex PDF");
			dialog.setContentView(R.layout.about_lyaout);
			dialog.show();
			return true;
		}else {


			return super.onOptionsItemSelected(item);
		}
	}

	private void loaNormal() {
		new Scanner().execute(".pdf",".epub",".cbz",".xps", ".jfif", ".jfif-tbnl", ".tif",".tiff");

		LOADED = NORMAL;

		toolbar.setSubtitle("All documents");



	}

	private void changeColumn() {
		int max = 3;
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			max =4;
		}
		if(column_count<max){
			column_count++;
		}else{
			column_count =1;
		}

		adpter.setColumnCount(column_count);

		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor e = sp.edit();
		e.putInt(PREF_COLUMN_COUNT, column_count);
		e.apply();

		rv.setLayoutManager(new GridLayoutManager(this, column_count));
	}

	private void loadRecents() {

		adpter.setNewData(booksDb.getAllRows());
		LOADED = RECENT;
		toolbar.setSubtitle("Recents");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main,menu);


		return super.onCreateOptionsMenu(menu);
	}

	private void showScanningProgress(boolean b) {
            spinner_loading.setVisibility(b? View.VISIBLE:View.GONE);
    }


	public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

		private int mItemOffset;

		public ItemOffsetDecoration(int itemOffset) {
			mItemOffset = itemOffset;
		}

		public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
			this(context.getResources().getDimensionPixelSize(itemOffsetId));
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
								   RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);
			outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
		}
	}


	@Override
	protected void onDestroy() {
		if(adpter!=null){
			adpter.onDestroy();
		}
		super.onDestroy();
	}
}
