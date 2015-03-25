package com.nor.image;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.util.Log;

public class UndoManager {
	/** Undo許容範囲 */
	private final static int UNDO_MAX = 10;
	/** Undoを保持するクラスの配列 */
	private static ArrayList<UndoModel> undoModelList = new ArrayList<UndoModel>();

	/**
	 * Undo初期化
	 */
	public static void clear() {
		undoModelList.clear();
	}

	/**
	 * Undo追加
	 */
	public static void addUndo(Bitmap bitmap, ColorFilter filter) {

		// 許容範囲を超えた場合は一番古いデータを削除
		while (undoModelList.size() >= UNDO_MAX) {

			// 使用済みbitmapを解放
			UndoModel undoModel = undoModelList.get(undoModelList.size() - 1);
			Bitmap recycleBitmap = undoModel.getBitmap();
			recycleBitmap.recycle();
			undoModel = null;
			
			undoModelList.remove(undoModelList.size() - 1);
			undoModelList.trimToSize();
		}

		try {
			// Undoを登録
			UndoModel undoModel = new UndoModel();
			if (!bitmap.isRecycled()) {
				bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			}
			undoModel.setBitmap(bitmap);
			undoModel.setFilter(filter);
			undoModelList.add(0, undoModel);
			undoModelList.trimToSize();
			System.gc();
		} catch (OutOfMemoryError ome) {
			Log.e("addUndo", "", ome);
		}
	}

	/**
	 * Undoを入れ替え
	 * @param undoModel
	 */
	public static void replaceUndo(UndoModel undoModel) {
		for (int i = 0; i < undoModelList.size(); i++) {
			UndoModel model = undoModelList.get(i);
			if (model.equals(undoModel)) {
				undoModelList.set(i, undoModel);
				break;
			}
		}
	}

	/**
	 * 指定されたUndoModelを取得（UndoModelは削除しない）
	 * @param index
	 * @return
	 */
	public static UndoModel getUndo(int index) {
		if (index < 0) { return null; }
		if (undoModelList == null) { return null; }
		if (undoModelList.isEmpty()) { return null; }
		if (undoModelList.size() <= index) { return null; }
		return undoModelList.get(index);
	}

	/**
	 * Undo削除
	 * @param index
	 */
	public static void removeUndo(int index) {
		undoModelList.remove(index);
		undoModelList.trimToSize();
	}

	/**
	 * 保持しているUndoのサイズを取得
	 * @return 保持しているUndoサイズを返却
	 */
	public static int getUndoSize() {
		if (undoModelList != null) {
			return undoModelList.size();
		}
		return 0;
	}
}
