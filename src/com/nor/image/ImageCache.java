package com.nor.image;

import java.lang.ref.*;
import java.util.HashMap;

import android.graphics.Bitmap;

/**
 * Bitmapイメージキャッシュクラス（未使用）
 *
 * @author n.shukuya
 *
 */
public class ImageCache {
	/** イメージ管理クラス */
	private static HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

	public static Bitmap getImage(String key) {
		if (cache.containsKey(key)) {
			SoftReference<Bitmap> ref = cache.get(key);
			if (ref != null) {
				return ref.get();
			}
		}
		return null;
	}

	public static void setImage(String key, Bitmap image) {
		cache.put(key, new SoftReference<Bitmap>(image));
	}

	public static boolean hasImage(String key) {
		return cache.containsKey(key);
	}

	public static void removeImage(String key) {
		cache.remove(key);
	}

	public static void clear() {
		cache.clear();
	}
}
