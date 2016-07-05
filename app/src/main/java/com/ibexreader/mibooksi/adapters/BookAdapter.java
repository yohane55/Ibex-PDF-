package com.ibexreader.mibooksi.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.ibexreader.mibooksi.AsyncTask;
import com.ibexreader.mibooksi.BitmapManager;
import com.ibexreader.mibooksi.DetailActivity;
import com.ibexreader.mibooksi.MiBooksApplication;
import com.ibexreader.mibooksi.R;

import java.util.ArrayList;

/**
 * Created by john on 5/27/16.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.Vholder> {


	//emebetgashaw7@gmail.com
    private final boolean isList;

	static int LIST_VIEW_TYPE = 0;
	static int GRID_VIEW_TYPE = 1;
	static int AD_VIEW_TYPE = 2;

	public static final boolean CUSTOMIZE_TEMPLATE = true;
	private static final int MSG_RECYCLE_LAST = 847;
	private static BitmapManager bitmapManager;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MSG_RECYCLE_LAST:
					if(bitmapManager!=null){
						bitmapManager.recycleLastFreed((String) msg.obj);
					}
			}
		}
	};
	private int columnCount=3;

	public void refreshItem(Book lastOpendBook) {

		notifyDataSetChanged();
		if(lastOpendBook!=null){

			String path = Uri.decode(lastOpendBook.uri.getEncodedPath());
			if (path == null) {
				path = lastOpendBook.uri.toString();
			}

			bitmapManager.freeBitmap(path);
			notifyItemChanged(mItems.indexOf(path));
		}
	}

	public void onDestroy() {
		if(bitmapManager!=null){
			bitmapManager.freeBitmaps();
		}
	}

	@Override
	public int getItemViewType(int position) {

//		if((position==0 || position ==7) ){
//			return AD_VIEW_TYPE;
//		}
		return columnCount ==1? LIST_VIEW_TYPE: GRID_VIEW_TYPE;
	}

	public Object getCustomizedAttributes() {
		NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
		.setBackgroundColor(Color.parseColor("#F6F8FB"))
				.setTitleTextColor(Color.parseColor("#1A253C"))
				.setButtonTextColor(Color.parseColor("#FFFFFF"))
				.setButtonColor(Color.parseColor("#435F9C"));

		return viewAttributes;
	}

	public View getNativeAdView() {

		NativeAd ad =MiBooksApplication.getInstance().getNextAd();
		View adv;
		if(ad!=null) {
			adv =
			NativeAdView.render(mCon, ad, NativeAdView.Type.HEIGHT_300, (NativeAdViewAttributes) getCustomizedAttributes());

		}else{
			adv =  new View(mCon);
		}
		return adv;
	}

	public interface OnItemSelectedListner{
        void onItemSelected(int pos);
    }


    OnItemSelectedListner onItemSelectedListner;





    private final Context mCon;
    private ArrayList<Book> mItems;

    public BookAdapter(Context context, ArrayList<Book> items, boolean list){
        mCon = context;
		AdSettings.addTestDevice("e52317a3a67f5363cdca0f0e939040f8");
		bitmapManager = BitmapManager.getInstance();
        mItems = items;
        isList = list;
    }

    public void setNewData(ArrayList<Book> items){
        mItems = items;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mItems!=null?mItems.size(): 0;
    }


    public Book getItem(int i) {
        return mItems!=null && mItems.size()>i?mItems.get(i):null;
    }

    @Override
    public Vholder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(mCon);
        View view = viewType == AD_VIEW_TYPE ? getNativeAdView() :


				inflater.inflate(viewType==LIST_VIEW_TYPE? R.layout.custom_grid_item :R.layout.books_itme_view, null);


        return new Vholder(view);
    }

    @Override
    public void onBindViewHolder(Vholder holder, int position) {

			holder.onBind(getItem(position));


    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setOnItemSelectedListner(OnItemSelectedListner osl){
        onItemSelectedListner =osl;
    }


//
//    @Override
//    public View getView(int pos, View view, ViewGroup viewGroup) {
//
//        if(view==null){
//            final LayoutInflater inflater = LayoutInflater.from(mCon);
//            view = inflater.inflate(R.layout.books_itme_view, null);
//
//        }
//        final TextView ti    = (TextView) view.findViewById(R.id.textView);
//        ti.setText(getItem(pos).Title);
//
//
//        return view;
//    }


	public void setColumnCount(int columnCount){
		this.columnCount = columnCount;
//		notifyDataSetChanged();
	}

    public class Vholder extends RecyclerView.ViewHolder implements View.OnClickListener {

		View item;

        public Vholder(View itemView) {
            super(itemView);
            item = itemView;
            item.setOnClickListener(this);
			ImageButton ib = (ImageButton) item.findViewById(R.id.detail_btn);
			ib.setOnClickListener(this);

        }


        public void onBind(final Book book){


			if(columnCount==1){
				item.setBackgroundResource(R.drawable.shelf_row);

			}else if(columnCount == 2){
				if((getAdapterPosition()+1) % columnCount == 0){
					item.setBackgroundResource(R.drawable.two_shelf_row_right);

				}else{
					item.setBackgroundResource(R.drawable.two_shelf_row_left);

				}
			}else {
				if (columnCount != 1 && getAdapterPosition() > 0 && (getAdapterPosition() + 1) % columnCount == 0) {
					item.setBackgroundResource(R.drawable.three_shelf_row_right);
				} else if (getAdapterPosition() == 0 || (getAdapterPosition() + 1) % columnCount == 1) {
					item.setBackgroundResource(R.drawable.three_shelf_row_left);
				} else {


						item.setBackgroundResource(R.drawable.three_shelf_row_middle);

				}

			}

            final TextView ti    = (TextView) item.findViewById(R.id.textView);
             ti.setText(book.Title);

            final ImageView cover = (ImageView) item.findViewById(R.id.book_cover_iv);

			try {



//				File f = new File(book.uri.toString());
				String path = Uri.decode(book.uri.getEncodedPath());
				if (path == null) {
					path = book.uri.toString();
				}
				final String finalPath = path;
				new Thread() {
					@Override
					public void run() {
						cover.setImageBitmap(bitmapManager.getOrCreateThumbnail(finalPath,mCon, finalPath,320,480));
						 Message msg = mHandler.obtainMessage(MSG_RECYCLE_LAST);
						msg.obj = finalPath;
						mHandler.sendMessageDelayed(msg, 5*1000);
					}
				}.run();



			} catch (Exception e) {
				e.printStackTrace();

			}

//

        }

        @Override
        public void onClick(View view) {

			switch (view.getId()){
				case R.id.detail_btn:
					Intent i  = new Intent(mCon, DetailActivity.class);
					i.setData(getItem(getAdapterPosition()).uri);
					mCon.startActivity(i);

					break;
				default:
					if(onItemSelectedListner!=null){
						onItemSelectedListner.onItemSelected(getAdapterPosition());
					}
			}

        }
    }



	protected class ThumbnailGetterAsync extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	}
}
