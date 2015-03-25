package com.nor.image;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;

public class UndoModel {
	private Bitmap bitmap;
	private ColorFilter filter;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public ColorFilter getFilter() {
		return filter;
	}

	public void setFilter(ColorFilter filter) {
		this.filter = filter;
	}

	public UndoModel() {
	}

	public UndoModel(Bitmap bitmap, ColorFilter filter) {
		this.bitmap = bitmap;
		this.filter = filter;
	}
}
