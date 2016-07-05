package com.ibexreader.mibooksi.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.ibexreader.mibooksi.R;

/**
 * Created by john on 6/13/16.
 */
public class ColorPicker extends PopupWindow {


	private ColorAdapter adapter;
	private SeekBar seekBar;

	public float getSeekBarValue() {
		return seekBar.getProgress();

	}


	public interface OnColorChanged{
		void onColorChanged(int color, float width);
	}

	private OnColorChanged onColorChanged;

	public void setOnColorChanged(OnColorChanged oncc){
		onColorChanged =  oncc;
	}

	public ColorPicker(Context context) {
		super(context);
		init(context);
		setFocusable(true);
	}


	public void setSeekBarValue(float value){
		seekBar.setProgress((int) value);
	}

	private void init(Context context){
		LayoutInflater inflater =  LayoutInflater.from(context);

		View root = inflater.inflate(R.layout.popup_layout,null);



		RecyclerView rc = (RecyclerView) root.findViewById(R.id.color_recycler);
		rc.setLayoutManager(new GridLayoutManager(context,6));
		adapter =  new ColorAdapter(context);
		rc.setAdapter(adapter);


		setContentView(root);

		seekBar =(SeekBar) root.findViewById(R.id.sizeSeekBar);

		Button cx =  (Button)root.findViewById(R.id.cancelButton);
		cx.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		Button ok =  (Button)root.findViewById(R.id.okButton);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				deliverResults();
			}
		});





	}

	public void deliverResults() {
		if(onColorChanged!=null){
			onColorChanged.onColorChanged(adapter.getSelctedColor(),getSeekBarValue());
		}

		dismiss();
	}


	public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.CVholder>{

		private int selectedItem = -1;

		private final Context mCon;

		private int[] colors = {R.color.md_green_A400,
				R.color.md_light_green_A400,
				R.color.md_lime_A400,
				R.color.md_yellow_A400,
				R.color.md_amber_A400,
				R.color.md_orange_A400,
				R.color.md_deep_orange_A400,
				R.color.md_red_A400,
				R.color.md_pink_A400,
				R.color.md_purple_A400,
				R.color.md_indigo_A400,
				R.color.md_blue_A400,
				R.color.md_light_blue_A400,
				R.color.md_black_1000,

		};
		private int last=-1;

		public ColorAdapter(Context context){
			mCon = context;
		}

		@Override
		public ColorAdapter.CVholder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			View v = inflater.inflate(R.layout.color_view, null);


			return new CVholder(v);
		}


		public int getSelctedColor(){
			if(selectedItem!= -1){
				return mCon.getResources().getColor(colors[selectedItem]);
			}else {
				return 0;
			}
		}

		@Override
		public void onBindViewHolder(ColorAdapter.CVholder holder, int position) {
				holder.onBind(colors[position]);
		}

		@Override
		public int getItemCount() {
			return colors.length;
		}

		public class CVholder extends RecyclerView.ViewHolder implements View.OnClickListener {
			View colorV;
			View root;
			public CVholder(View itemView) {
				super(itemView);

				root =itemView;
				colorV = itemView.findViewById(R.id.color);
				itemView.setOnClickListener(this);

			}

			public void onBind(int color){

				colorV.setBackgroundColor(mCon.getResources().getColor(color));


				if(getAdapterPosition()==selectedItem){
					root.setBackgroundResource(R.drawable.select);
				}else{
					root.setBackgroundResource(0);
				}
			}

			@Override
			public void onClick(View view) {
				last = selectedItem;

				selectedItem = getAdapterPosition();

				notifyItemChanged(last);
				notifyItemChanged(selectedItem);
				root.setBackgroundResource(R.drawable.select);

			}
		}
	}


}
