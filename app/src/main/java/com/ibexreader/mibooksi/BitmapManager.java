/**
 *
 * Copyright (C) 2014 Yohannes Ejigu Ademe, yohaaan55@yahoo.com, +251918029694
 * This code and the whole project(fynGeez Amharic Keyboard)is copyrighted to 
 * Yohannes Ejigu. All or partial use of this code with out written permition 
 * or with out having a legal ownership is strictly forbidden and is punishable 
 * by law!
 *
 */
package com.ibexreader.mibooksi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.ibexreader.mibooksi.adapters.MuPDFThumb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class BitmapManager {

	HashMap<String, Bitmap> mImagesMap;
	private Bitmap lastFreedBm;
	private String lastFreedId=null;

	static BitmapManager manager;
	// private constructor to create the objects
	private BitmapManager() {
		mImagesMap = new HashMap<String, Bitmap>();
		manager =this;
	}


	public static BitmapManager getInstance(){
		if(manager==null){
			manager = new BitmapManager();
		}

		return manager;
	}

	public void put(String id, Bitmap bmp) {
		mImagesMap.put(id, bmp);
	}

	public void freeBitmap(String id) {
		lastFreedBm = mImagesMap.get(id);
		lastFreedId = id;
		mImagesMap.remove(id);

	}

	public Bitmap getBitmap(int id) {
		return mImagesMap.get(id);
	}

	// call in order to free memory of Bitmap objects
	public void freeBitmaps() {

		for (Bitmap image : mImagesMap.values()) {
			// also, it's better to check whether it is recycled or not
			if (image != null && !image.isRecycled()) {
				image.recycle();
				image = null; // null reference
			}
		}
		mImagesMap.clear();
	}



	public void recycleLastFreed(String id){
		if(lastFreedId!=null && lastFreedId.equals(id)){
			if(lastFreedBm!=null) lastFreedBm.recycle();
		}

	}


	public Bitmap getOrCreateThumbnail(String id, Context context, String path, int w, int h) {
		if(mImagesMap.get(id)== null || mImagesMap.get(id).isRecycled() ) {

			final File f = new File(path);

			File thumb = null;
			String dir = f.getParent();

			if (f != null && f.isFile() && f.getName().toLowerCase().endsWith(".epub")) {

				if (dir != null) {
					File thumbdir = new File(dir, ".thumbs");
					if (!thumbdir.exists()) {
						thumbdir.mkdir();
					}
					thumb = new File(thumbdir.getPath(), f.getName() + ".thumb");

					if (thumb.exists()) {
						mImagesMap.put(id, BitmapFactory.decodeFile(thumb.getPath()));
					} else {
						try {
							DataUnzipper.searchForSimilar(path, "cover", thumb.getPath() );
							mImagesMap.put(id, BitmapFactory.decodeFile(thumb.getPath()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				return mImagesMap.get(id);


			} else {



				if (f.isFile()) {
					if (dir != null) {
						File thumbdir = new File(dir, ".thumbs");
						if (!thumbdir.exists()) {
							thumbdir.mkdir();
						}
						thumb = new File(thumbdir.getPath(), f.getName() + ".thumb");

						if (thumb.exists()) {
							mImagesMap.put(id, BitmapFactory.decodeFile(thumb.getPath()));
						} else {
							MuPDFThumb muPDFThumb = null;
							try {

								muPDFThumb = new MuPDFThumb(context, path);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (muPDFThumb != null) {
								final Bitmap bitmap = muPDFThumb.thumbOfFirstPage(w, h);

								try {
									FileOutputStream fos = new FileOutputStream(thumb);
									bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);

									fos.flush();
									fos.close();
									mImagesMap.put(id, bitmap);
									muPDFThumb.onDestroy();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}

							}

							muPDFThumb = null;
						}


					}
				}

				return mImagesMap.get(id);
			}

		}else{
			return mImagesMap.get(id);
		}

	}

	@Override
	protected void finalize() {

		if (mImagesMap.size() > 0) {
			freeBitmaps();
		}
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public Bitmap getCreateThumbnail(String id, Context context, String path, int w, int h) {
		final File f = new File(path);

		String prev = Environment.getDownloadCacheDirectory()+"/"+".largThumb/large.thumb";
		File thumb = null;
		String dir = f.getParent();

		if (f != null && f.isFile() && f.getName().toLowerCase().endsWith(".epub")) {

			if (dir != null) {
				File thumbdir = new File(dir, ".thumbs");
				if (!thumbdir.exists()) {
					thumbdir.mkdir();
				}
				thumb = new File(thumbdir.getPath(), f.getName() + ".thumb");

				if (thumb.exists()) {
					mImagesMap.put(id, BitmapFactory.decodeFile(thumb.getPath()));
				} else {
					try {
						DataUnzipper.searchForSimilar(path, "cover", thumb.getPath());
						mImagesMap.put(id, BitmapFactory.decodeFile(thumb.getPath()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return mImagesMap.get(id);
		}
		else

		{

			MuPDFThumb muPDFThumb = null;
			try {

				muPDFThumb = new MuPDFThumb(context, path);
			} catch (Exception e) {
				e.printStackTrace();
			}


			if (muPDFThumb != null) {
				final Bitmap bitmap = muPDFThumb.thumbOfFirstPage(w, h);


				muPDFThumb.onDestroy();
				muPDFThumb = null;

				return bitmap;



			}


		}

		return null;
	}
}
