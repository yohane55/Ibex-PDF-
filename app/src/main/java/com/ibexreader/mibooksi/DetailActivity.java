package com.ibexreader.mibooksi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		Intent i = getIntent();
		final Uri ur =  i.getData();

		String path = Uri.decode(ur.getEncodedPath());
		if (path == null) {
			path = ur.toString();
		}
		final String finalPath = path;
		if(ur!=null){
			final ImageView co = (ImageView) findViewById(R.id.book_cover_iv);
			TextView ti	= (TextView) findViewById(R.id.title);
			ti.setText(ur.getLastPathSegment());

			bitmap = BitmapManager.getInstance().getCreateThumbnail(finalPath, DetailActivity.this, finalPath,600,900);
			co.setImageBitmap(bitmap);

			findViewById(R.id.open_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(DetailActivity.this,MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(ur);
					startActivity(intent);
				}
			});


			findViewById(R.id.share_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i  = new Intent(Intent.ACTION_SEND);
					i.setData(ur);
					i.setType("*/*");


					startActivity(i);
				}
			});


			findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					AlertDialog.Builder b = new AlertDialog.Builder(DetailActivity.this);

					b.setTitle("Delete ???");

					b.setMessage("Delete this document ??");

					b.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							File f = new File(finalPath);
							if(f.exists()) f.delete();

							finish();
						}
					});



					b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					});

					b.create().show();


				}
			});

		}
	}

	@Override
	protected void onDestroy() {

		if(bitmap!=null) {
			bitmap.recycle();
			bitmap = null;

		}
		super.onDestroy();
	}
}
