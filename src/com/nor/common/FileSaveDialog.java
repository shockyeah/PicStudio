package com.nor.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nor.picstudio.R;

public class FileSaveDialog extends Activity
	implements DialogInterface.OnKeyListener, DialogInterface.OnClickListener {

	/** 本ｸﾗｽを呼び出したｸﾗｽのContext */
	private Context context = null;
	/** ｶﾚﾝﾄﾃﾞｨﾚｸﾄﾘ */
	private File current = null;
	/** ﾌｧｲﾙ一覧表示用ﾀﾞｲｱﾛｸﾞ */
	private Dialog dialog = null;
	/** ﾌｧｲﾙ選択時の拡張子ﾌｨﾙﾀﾘﾝｸﾞ */
	private String[] filter_ext = null;
	/** ﾌｧｲﾙ名入力用EditText */
	private EditText edit_filename = null;

	/** 保存するBitmap */
	private Bitmap bitmap = null;

	public FileSaveDialog(Context context) {
		this.context = context;
	}

	public void onClick(DialogInterface dialog, int whitch) {
		try {
			if (current.listFiles().length <= whitch) { return; }
			File file = current.listFiles()[whitch];
			if (file.isDirectory()) {
				dialog.dismiss();
				show(file.getAbsolutePath());
			}
		} catch (Exception e) {
			Log.v("FileSelectDialog", "onClick", e);
		}
	}

	public boolean onKey(DialogInterface dialog, int keycode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keycode == KeyEvent.KEYCODE_BACK) {
				File parent = current.getParentFile();
				if (parent == null) { return false; }
				show(parent.getAbsolutePath());
				dialog.dismiss();
			}
		}
		return false;
	}


	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	/**
	 * ﾀﾞｲｱﾛｸﾞを閉じる
	 */
	public void dismiss() {
		dialog.dismiss();
		dialog = null;
	}

	/**
	 * ﾌｧｲﾙ選択ﾀﾞｲｱﾛｸﾞ表示
	 * @param path ﾌｧｲﾙ選択ﾀﾞｲｱﾛｸﾞ自に開くﾃﾞﾌｫﾙﾄのﾊﾟｽ
	 * @return ﾀﾞｲｱﾛｸﾞ正常表示時true、異常時falseを返却。
	 */
	public boolean show(String path) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setCancelable(true);
			builder.setOnKeyListener(this);
			builder.setView(getFilenameInputEdit(path));	// ここでcurrentを生成しているの
			builder.setItems(getFileList(path), this);
			builder.setTitle(current.getPath());
			builder.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int witch) {
					// 保存実行処理
					File file = new File(current.getPath()
							+ "/" + edit_filename.getText().toString() + ".jpg");
					((OnFileSaveDialogListener)context).OnFileSaved(save(file), file);		// ﾌｧｲﾙ保存ｲﾍﾞﾝﾄ発生
				}
			});
			builder.setNeutralButton(R.string.dialog_parent, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int witch) {
					File parent = current.getParentFile();
					if (parent == null) { return; }
					show(parent.getAbsolutePath());
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int witch) {
					dialog.dismiss();
					dialog = null;
				}
			});
			dialog = builder.create();
			dialog.show();
			return true;
		} catch (Exception e) {
			Log.v("FileSelectDialog", "show()" , e);
		}
		return false;
	}

	/**
	 * 指定されたﾊﾟｽのﾌｧｲﾙ一覧を取得
	 * @param path ﾌｧｲﾙ一覧を取得するﾃﾞｨﾚｸﾄﾘのﾊﾟｽ
	 * @return 指定されたﾊﾟｽのﾌｧｲﾙ一覧。存在しない場合はnullを返却。
	 */
	private String[] getFileList(String path) {
		if (!isFolderPath(path)) { return null; }								// 指定されたﾊﾟｽの妥当性をﾁｪｯｸ
		if (current != null) { current = null; }								// ｶﾚﾝﾄﾃﾞｨﾚｸﾄﾘの更新
		current = new File(path);

		File[] files = null;
		if (filter_ext == null) {
			files = current.listFiles();
		} else {
			files = current.listFiles(getFileExtensionFilter(filter_ext));		// 指定された拡張子でﾌｨﾙﾀﾘﾝｸﾞ
		}
		if (files == null) { return new String[0]; }							// ﾌｧｲﾙ、ﾌｫﾙﾀﾞが存在しない
		String[] retStr = new String[(int)files.length];
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			retStr[i] = f.getName();
			if (f.isDirectory()) {
				retStr[i] += "/";
			}
		}
		return retStr;
	}

	/**
	 * 指定されたﾊﾟｽのﾌｫﾙﾀﾞ一覧を取得
	 * @param path ﾌｫﾙﾀﾞ一覧を取得するﾃﾞｨﾚｸﾄﾘのﾊﾟｽ
	 * @return 指定されたﾊﾟｽのﾌｫﾙﾀﾞ一覧。存在しない場合はnullを返却。
	 */
	@SuppressWarnings({ "unused" })
	private String[] getFolderList(String path) {
		if (!isFolderPath(path)) { return null; }								// 指定されたﾊﾟｽの妥当性をﾁｪｯｸ
		if (current != null) { current = null; }								// ｶﾚﾝﾄﾃﾞｨﾚｸﾄﾘの更新
		current = new File(path);

		File[] files = current.listFiles();
		if (files == null) { return new String[0]; }							// ﾌｧｲﾙ、ﾌｫﾙﾀﾞが存在しない

		ArrayList<String> file_array = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				file_array.add(f.getName());
			}
		}

		String[] str_files = new String[file_array.size()];
		for (int i = 0; i < str_files.length; i++) {
			str_files[i] = file_array.get(i);
		}
		return str_files;
	}

	/**
	 * 指定されたﾊﾟｽが有効か否か判定
	 * @param path 検査するﾊﾟｽ
	 * @return ﾌｧｲﾙもしくはﾌｫﾙﾀﾞが存在する場合はtrueを返却
	 */
	private boolean isFolderPath(String path) {
		if ((path == null) || (path == "")) { return false; }
		File file = new File(path);
		if (!file.isDirectory()) { return false; }
		return true;
	}

	/**
	 * 拡張子ﾌｨﾙﾀｰ
	 * @param _ext ﾌｨﾙﾀﾘﾝｸﾞに用いる拡張子を格納したString配列
	 * @return 指定された拡張子を含むFileFilter
	 */
    public static FilenameFilter getFileExtensionFilter(String[] _ext) {
        final String[] ext = _ext;
        return new FilenameFilter() {
            public boolean accept(File file, String name) {
            	if (file.isDirectory()) { return true; }						// ﾌｫﾙﾀﾞの場合は無条件に許可
            	for (String s : ext) {
            		if (name.endsWith(s)) { return true; }
            	}
                return false;
            }
        };
    }

	/**
	 * ﾀﾞｲｱﾛｸﾞ表示時の拡張子ﾌｨﾙﾀﾘﾝｸﾞ
	 * @return 設定されているﾌｨﾙﾀﾘﾝｸﾞ用拡張子群
	 */
	public String[] getFilter_ext() {
		return filter_ext;
	}

	/**
	 * ﾀﾞｲｱﾛｸﾞ表示時の拡張子ﾌｨﾙﾀﾘﾝｸﾞ
	 * @param filter_ext 設定するﾌｨﾙﾀﾘﾝｸﾞ用拡張子
	 */
	public void setFilter_ext(String[] filter_ext) {
		this.filter_ext = filter_ext;
	}

	/**
	 * ﾌｧｲﾙ保存ﾀﾞｲｱﾛｸﾞ用View生成
	 * @param path ﾌｧｲﾙ保存ﾀﾞｲｱﾛｸﾞを開くPath
	 * @return ﾌｧｲﾙ保存ﾀﾞｲｱﾛｸﾞ用View
	 */
	private View getFilenameInputEdit(String path) {
		TableLayout l = new TableLayout(context);
		l.setPadding(2, 2, 2, 2);
		TextView tv = new TextView(context);
		tv.setText(context.getString(R.string.dialog_filename));
		l.addView(tv);
		if (edit_filename != null) { edit_filename = null; }
		edit_filename = new EditText(context);
		Calendar cl = Calendar.getInstance();
		Date dt = cl.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		edit_filename.setText(sdf.format(dt));												// 拡張子はgifのみなので実行時に付与する
		edit_filename.setFilters(new InputFilter[] {new InputFilter.LengthFilter(128)});	// 入力文字数制限
		edit_filename.setInputType(InputType.TYPE_CLASS_TEXT);
		edit_filename.setTransformationMethod(SingleLineTransformationMethod.getInstance());// 改行不可
		TableRow name_row = new TableRow(context);
		name_row.addView(edit_filename);
		l.addView(name_row);
		return (View)l;
	}

	/**
	 * ﾌｧｲﾙ保存実行
	 * @param file 保存するﾌｧｲﾙ(画像のみ)
	 * @return 保存成功時trueを返却
	 */
	private boolean save(File file) {
		try {
			// 不正なﾌｧｲﾙ名をﾁｪｯｸ
			/*
			if (edit_filename.getText().toString().matches(".*[@＠｢「｣」\\d:：.*")) {
				return false;
			}
			*/
			if (file.exists()) {	// 既にﾌｧｲﾙが存在する
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(true);
				builder.setMessage(getString(R.string.dialog_alreadyexist));
				builder.setPositiveButton(getString(R.string.dialog_ok),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return false;
			}
			// ﾌｧｲﾙ書き出し
            // ﾊﾞｲﾄﾃﾞｰﾀに変換
            byte[] imgData = chngBmpToData(bitmap, Bitmap.CompressFormat.JPEG, 100);
            // ﾌｫﾙﾀﾞへｺﾋﾟｰ
            saveDataToStorage(imgData, file.getPath());
            // 選択された画像PATHを共有領域にｾｯﾄする
            SharedPreferences pref = context.getSharedPreferences("PREF_PICT", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("select_pict", file.getPath());
            editor.commit();
    	} catch (Exception e) {
    		Log.v("save", "file.getPath()=" + file.getPath(), e);
            return false;
    	}
		return true;
	}

	/**
	 * ビットマップ画像をバイトデータに変換する
	 * @param src      Bitmap
	 * @param format   Bitmap.CompressFormat
	 * @param quality   int
	 * @return         byte[]
	 */
	private static byte[] chngBmpToData(Bitmap src, Bitmap.CompressFormat format, int quality) {
	   ByteArrayOutputStream output = new ByteArrayOutputStream();
	   src.compress(format, quality, output);
	   return output.toByteArray();

	}
	/**
	 * 画像を保存する
	 * @param data   byte[]   画像データ
	 * @param dataName   String   保存パス
	 * @return   boolean
	 * @throws Exception
	 */
	private void saveDataToStorage(byte[] data, String dataName) throws Exception {
	   FileOutputStream fileOutputStream = null;
	   try {
	      // 指定保存先に保存する
	      fileOutputStream = new FileOutputStream(dataName);
	      fileOutputStream.write(data);
	   } catch (Exception e) {
	   } finally {
	      if (fileOutputStream != null) {
	         fileOutputStream.close();
	         fileOutputStream = null;
	      }
	   }
	}

	/**
	 * ﾌｧｲﾙ保存実行時のｲﾍﾞﾝﾄ
	 */
	public interface OnFileSaveDialogListener {
		/**
		 * ﾌｧｲﾙ選択完了時のｲﾍﾞﾝﾄ
		 * @param result ﾌｧｲﾙ保存成功時true
		 * @param file 保存を実行したﾌｧｲﾙ
		 */
		public void OnFileSaved(boolean result, File file);
	}
}
