package com.ibexreader.mibooksi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;

import com.ibexreader.mibooksi.MuPDFCore;

/**
 * Created by john on 5/27/16.
 */
public class MuPDFThumb extends MuPDFCore {
    public MuPDFThumb(Context context, String path) throws Exception {
        super(context, path);
    }

	public Bitmap thumbOfFirstPage(int w, int h){
		countPages();
		PointF pageSize = getPageSize(0);
		float sourceScale = Math.max(w/pageSize.x, h/pageSize.y);

		Point size = new Point((int)(pageSize.x*sourceScale),(int) (pageSize.y*sourceScale));

		final Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
		drawPage(bitmap,0,size.x,size.y,0,0,size.x,size.y, new Cookie());

		return bitmap;
	}
}
