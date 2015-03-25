package com.nor.picstudio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nor.common.FileSaveDialog.OnFileSaveDialogListener;
import com.nor.common.ImageEffect;
import com.nor.image.UndoManager;
import com.nor.image.UndoModel;
/**
 * PhotoStuio
 * @author shukuya
 *
 */
public class PicStudio extends GraphicsActivity
	implements OnFileSaveDialogListener, OnClickListener {

	/** ｷﾞｬﾗﾘｰから読み込んだBitmapｵﾌﾞｼﾞｪｸﾄ */
	//public SoftReference<Bitmap> src_bitmap = null;
	/** ﾍﾟﾝの大きさ */
	private float pensize = 14;
	/** ﾍﾟﾝ色 */
    public int pencolor = Color.WHITE;
    /** ﾍﾟﾝｴﾌｪｸﾄ */
    public MaskFilter peneffect = null;
    /** ﾍﾟﾝﾓｰﾄﾞ */
    public int penmode = MODE_NORMAL;
    /** ﾓｰﾄﾞ */
    public static final int MODE_NORMAL 	= 0;
    public static final int MODE_TEXT 		= 1;
    public static final int MODE_CIRCLE 	= 2;
    public static final int MODE_STAR 		= 3;
    public static final int MODE_HEART 		= 4;
    public static final int MODE_HEART_FAT 	= 5;
    public static final int MODE_GLITTER	= 6;
    public static final int MODE_TEAR		= 7;
    public static final int MODE_FLOWER		= 8;
    public static final int MODE_SNOW		= 9;
	/** 描画View */
	private MyView myview = null;
    /** 色選択ﾀﾞｲｱﾛｸﾞ */
    private AlertDialog color_dialog 	= null;
    /** ｻｲｽﾞ選択ﾀﾞｲｱﾛｸﾞ */
    private AlertDialog size_dialog 	= null;
    /** ｴﾌｪｸﾄ選択ﾀﾞｲｱﾛｸﾞ */
    private AlertDialog effect_dialog 	= null;
    /** ﾓｰﾄﾞ選択ﾀﾞｲｱﾛｸﾞ */
    private AlertDialog mode_dialog 	= null;
    /** 明度選択ﾀﾞｲｱﾛｸﾞ */
    private AlertDialog bright_dialog 	= null;
    /** 彩度選択ﾀﾞｲｱﾛｸﾞ */
    private AlertDialog chroma_dialog 	= null;
    /** ｶﾗｰﾌｨﾙﾀｰ選択ﾀﾞｲｱﾛｸﾞ */
    private AlertDialog filter_dialog	= null;
    /** ｸﾞﾗﾌｨｯｸ選択ﾀﾞｲｱﾛｸﾞ */
    private AlertDialog graphic_dialog 	= null;
    /** ぼかし選択ﾀﾞｲｱﾛｸﾞ */ //TODO ぼかし別ﾀﾞｲｱﾛｸﾞ対応
    private AlertDialog blur_dialog 	= null;
    /** 色 */
    public int[] colors_id = new int[] {
    	R.color.light_red,		R.color.light_blue,		R.color.light_yellow,
    	R.color.light_green,	R.color.light_aqua,		R.color.light_orange,
    	R.color.light_purple,	R.color.light_pink,		R.color.light_brown,
    	R.color.light_gray,		R.color.red,			R.color.blue,
    	R.color.yellow,			R.color.green,			R.color.aqua,
    	R.color.orange,			R.color.purple,			R.color.pink,
    	R.color.brown,			R.color.gray,
    	R.color.dark_red,		R.color.dark_blue,		R.color.dark_yellow,
    	R.color.dark_green,		R.color.dark_aqua,		R.color.dark_orange,
    	R.color.dark_purple,	R.color.dark_pink,		R.color.dark_brown,
    	R.color.dark_gray,		R.color.black,			R.color.white
    };
    /** 明度調節用SeekBar */
    private SeekBar bright_bar = null;
    /** 彩度調節用SeekBar */
    private SeekBar chroma_bar = null;
    /** ｶﾗｰﾌｨﾙﾀｰ調節用SeekBar */
    private SeekBar[] filter_bar = null;
    /** ｶﾗｰﾌｨﾙﾀｰ現在値TextView */
    private TextView[] filter_view = null;

	private Paint       mPaint;
	private MaskFilter  mEmboss;
	private MaskFilter  mBlur_normal;
	private MaskFilter  mBlur_inner;
	private MaskFilter  mBlur_outer;
	private MaskFilter  mBlur_solid;

	/** ﾌﾟﾛｸﾞﾚｽﾀﾞｲｱﾛｸﾞ */
	private ProgressDialog prgDialog = null;

	/** 画像保存時の横ｻｲｽﾞ設定EditText */
	//private EditText saveWidth = null;
	/** 画像保存時の縦ｻｲｽﾞ設定EditText */
	//private EditText saveHeight = null;

	/** ﾊﾞｯｸｱｯﾌﾟﾃﾞｰﾀ最大保持数 */
	private static final int BACKUP_MAX=1;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);
	    mPaint = new Paint();
	    mPaint.setAntiAlias(true);
	    mPaint.setDither(true);
	    mPaint.setColor(getResources().getColor(R.color.white));
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setStrokeJoin(Paint.Join.ROUND);
	    mPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPaint.setStrokeWidth(pensize);
	    mPaint.setTextSize(pensize);
	    mEmboss = new EmbossMaskFilter(
	    		new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
	    mBlur_normal = new BlurMaskFilter(4f, BlurMaskFilter.Blur.NORMAL);
	    mBlur_inner = new BlurMaskFilter(8f, BlurMaskFilter.Blur.INNER);
	    mBlur_outer = new BlurMaskFilter(8f, BlurMaskFilter.Blur.OUTER);
	    mBlur_solid = new BlurMaskFilter(6f, BlurMaskFilter.Blur.SOLID);
    	// ﾒﾆｭｰﾎﾞﾀﾝにﾘｽﾅｰ登録
    	((ImageButton)findViewById(R.id.imageButton_color)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_size)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_effect)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_text)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_revert)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_clear)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_graphic)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_camera)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_load)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_save)).setOnClickListener(this);
    	//((ImageButton)findViewById(R.id.imageButton_share)).setOnClickListener(this);
    	((ImageButton)findViewById(R.id.imageButton_close)).setOnClickListener(this);
    	/* ﾀﾞｲｱﾛｸﾞの表示が更新されなかったため、ﾀﾞｲｱﾛｸﾞ選択時に再度生成する
    	// 色選択ﾀﾞｲｱﾛｸﾞ生成
	    color_dialog = new AlertDialog.Builder(this)
	    	.setTitle(getString(R.string.dialog_color_select))
	    	.setPositiveButton(getString(R.string.dialog_cancel), null)
		    .setView(getColorView())
	    	.create();
	    // 太さ選択ﾀﾞｲｱﾛｸﾞ生成
		size_dialog = new AlertDialog.Builder(this)
	    	.setTitle(getString(R.string.dialog_size_select))
	    	.setPositiveButton(getString(R.string.dialog_cancel), null)
		    .setView(getPenSizeView())
	    	.create();
		// ｴﾌｪｸﾄﾀﾞｲｱﾛｸﾞ生成
		effect_dialog = new AlertDialog.Builder(this)
	    	.setTitle(getString(R.string.dialog_effect_select))
	    	.setPositiveButton(getString(R.string.dialog_cancel), null)
		    .setView(getEffectView())
	    	.create();
    	// ﾓｰﾄﾞ選択ﾀﾞｲｱﾛｸﾞ生成
    	mode_dialog = new AlertDialog.Builder(this)
	    	.setTitle(getString(R.string.dialog_mode_select))
	    	.setPositiveButton(getString(R.string.dialog_cancel), null)
		    .setView(getModeView())
	    	.create();
	    */
    	// 明度調節用SeekBar生成
    	bright_bar = new SeekBar(this);
    	//bright_bar.setProgress(128);
    	bright_bar.setMax(255);
    	bright_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast toast = Toast.makeText(getApplicationContext(),
						Integer.toString(seekBar.getProgress()), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				myview.setPreviewFilter(null);
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				//myview.setmBitmapColorFilter(ImageEffect.getBrightnessFilter(bright_bar.getProgress()));
				//myview.drawBitmap();
				myview.setPreviewFilter(ImageEffect.getBrightnessFilter(bright_bar.getProgress()));
				myview.invalidate();
			}
		});
    	// 明度ﾀﾞｲｱﾛｸﾞを生成
		bright_dialog = new AlertDialog.Builder(this)
			.setTitle(getString(R.string.dialog_brightness_select))
			.setView(bright_bar)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.dialog_apply), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					myview.setmBitmapColorFilter(ImageEffect.getBrightnessFilter(bright_bar.getProgress()));
					// 実ﾌｧｲﾙにﾌｨﾙﾀｰ設定を書き込み、画面を更新、ﾌｨﾙﾀｰを初期化
					myview.drawBitmap();
					myview.invalidate();
					myview.setPreviewFilter(null);
				}
			})
			.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					myview.undo();
					myview.setPreviewFilter(null);
				}
			})
			.create();
		// ﾀﾞｲｱﾛｸﾞの背景を暗くしない
		bright_dialog.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// ﾀﾞｲｱﾛｸﾞの表示位置を下に変更
		WindowManager.LayoutParams params = bright_dialog.getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM;
		bright_dialog.getWindow().setAttributes(params);
		// 彩度調節用SeekBar生成
    	chroma_bar = new SeekBar(this);
    	//chroma_bar.setProgress(128);
    	chroma_bar.setMax(255);
    	chroma_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast toast = Toast.makeText(getApplicationContext(),
						Integer.toString(seekBar.getProgress()), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				myview.setPreviewFilter(null);
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				//myview.setmBitmapColorFilter(ImageEffect.getChromaFilter(chroma_bar.getProgress()));
				//myview.drawBitmap();
				myview.setPreviewFilter(ImageEffect.getChromaFilter(chroma_bar.getProgress()));
				myview.invalidate();
			}
		});
    	// 彩度調節用ﾀﾞｲｱﾛｸﾞ生成
		chroma_dialog = new AlertDialog.Builder(this)
			.setTitle(getString(R.string.dialog_chroma_select))
			.setView(chroma_bar)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.dialog_apply), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					myview.setmBitmapColorFilter(ImageEffect.getChromaFilter(chroma_bar.getProgress()));
					myview.drawBitmap();
					myview.invalidate();
					myview.setPreviewFilter(null);
				}
			})
			.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					myview.undo();
					myview.setPreviewFilter(null);
				}
			})
			.create();
		// ﾀﾞｲｱﾛｸﾞの背景を暗くしない
		chroma_dialog.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// ﾀﾞｲｱﾛｸﾞの表示位置を下に変更
		params = chroma_dialog.getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM;
		chroma_dialog.getWindow().setAttributes(params);
		// ｶﾗｰﾌｨﾙﾀｰ調節用SeekBar生成
		LayoutInflater inflater = LayoutInflater.from(this);
		View color_layout = inflater.inflate(R.layout.color_filter, null);
		filter_bar = new SeekBar[] {
			(SeekBar)color_layout.findViewById(R.id.seekBar_red),
			(SeekBar)color_layout.findViewById(R.id.seekBar_green),
			(SeekBar)color_layout.findViewById(R.id.seekBar_blue)
		};
		filter_view = new TextView[] {
			(TextView)color_layout.findViewById(R.id.textView_red),
			(TextView)color_layout.findViewById(R.id.textView_green),
			(TextView)color_layout.findViewById(R.id.textView_blue)
		};
		for (SeekBar bar : filter_bar) {
			bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					myview.setPreviewFilter(null);
				}
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					float values[] = new float[] {
						((float)(filter_bar[0].getProgress()) / 10f),
						((float)(filter_bar[1].getProgress()) / 10f),
						((float)(filter_bar[2].getProgress()) / 10f)
					};
					for (int i=0; i<values.length; i++) {
						filter_view[i].setText(Float.toString(values[i]));
					}
					//myview.setmBitmapColorFilter(ImageEffect.getColorFilter(values[0], values[1], values[2]));
					//myview.drawBitmap();
					myview.setPreviewFilter(ImageEffect.getColorFilter(values[0], values[1], values[2]));
					myview.invalidate();
				}
			});
		}
		// ｶﾗｰﾌｨﾙﾀｰ用ﾀﾞｲｱﾛｸﾞ生成
		filter_dialog = new AlertDialog.Builder(this)
			.setTitle(getString(R.string.dialog_color_filter_select))
			.setView(color_layout)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.dialog_apply), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					float values[] = new float[] {
						((float)(filter_bar[0].getProgress()) / 10f),
						((float)(filter_bar[1].getProgress()) / 10f),
						((float)(filter_bar[2].getProgress()) / 10f)
					};
					// ColorFilter起動時は初期状態とする
					/*
					for (int i=0; i<values.length; i++) {
						filter_view[i].setText(Float.toString(values[i]));
					}*/
					myview.setmBitmapColorFilter(ImageEffect.getColorFilter(values[0], values[1], values[2]));
					myview.drawBitmap();
					myview.invalidate();
					myview.setPreviewFilter(null);
				}
			})
			.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					myview.undo();
					myview.setPreviewFilter(null);
				}
			})
			.create();
		// ﾀﾞｲｱﾛｸﾞの背景を暗くしない
		filter_dialog.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// ﾀﾞｲｱﾛｸﾞの表示位置を下に変更
		params = filter_dialog.getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM;
		filter_dialog.getWindow().setAttributes(params);
		// ぼかし効果用ﾀﾞｲｱﾛｸﾞ
		/*
		View blur_layout = inflater.inflate(R.layout.blur_filter, null);
		blur_dialog = new AlertDialog.Builder(this)
		.setTitle(getString(R.string.dialog_blur_select))
		.setView(blur_layout)
		.setCancelable(false)
		.setPositiveButton(getString(R.string.dialog_apply), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whitch) {
				// ぼかし設定をRadioGroupより取得
				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.blur_filter, null);
				RadioGroup style=(RadioGroup)layout.findViewById(R.id.radioGroup_style);
				RadioGroup level=(RadioGroup)layout.findViewById(R.id.radioGroup_level);
				RadioGroup width=(RadioGroup)layout.findViewById(R.id.radioGroup_width);
				int effect=ImageEffect.GRAPHIC_BLUR_SIDE_L;
				// ｽﾀｲﾙ
				for (int i=0;i<style.getChildCount();i++) {
					if (style.getChildAt(i).equals(style.findViewById(
					style.getCheckedRadioButtonId()))) {
						break;
					}
					// ImageEffectの定数でｽﾀｲﾙは3区切り
					effect+=3;
				}
				// ﾚﾍﾞﾙ
				for (int i=0;i<level.getChildCount();i++) {
					if (level.getChildAt(i).equals(style.findViewById(
					level.getCheckedRadioButtonId()))) {
						break;
					}
					// ImageEffectの定数でﾚﾍﾞﾙは1区切り
					effect++;
				}
				// 幅
				for (int i=0;i<width.getChildCount();i++) {
					// TODO
				}
				graphic_type = effect;
				// 画像効果適用中ﾌﾟﾛｸﾞﾚｽﾊﾞｰ表示
				String[] graphicarray = getResources().getStringArray(R.array.graphic_array);
				prgDialog.setTitle(graphicarray[effect]);
				prgDialog.setMessage(getString(R.string.dialog_grphic_executing));
				prgDialog.setCancelable(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 画像全体に効果を適用する
						myview.setGraphic(graphic_type);
						graphicHandler.sendEmptyMessage(0);
					}
				}).start();
				prgDialog.show();
			}
		})
		.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whitch) {
			}
		})
		.create();
		*/
		// 画像効果用ﾀﾞｲｱﾛｸﾞ生成
		graphic_dialog = new AlertDialog.Builder(this)
			.setTitle(getString(R.string.dialog_graphic_select))
			.setPositiveButton(getString(R.string.dialog_cancel), null)
			.setView(getGraphicView())
			.create();
		// 共用ﾌﾟﾛｸﾞﾚｽﾀﾞｲｱﾛｸﾞ生成
		prgDialog = new ProgressDialog(this);
		prgDialog.setTitle(R.string.dialog_save);
		prgDialog.setMessage(getString(R.string.dialog_saving));
		prgDialog.setCancelable(false);
        // ﾊﾟﾚｯﾄを初期化
    	myview = new MyView(this);
        ((FrameLayout)findViewById(R.id.frameLayout_palette)).addView(myview, 0);
		WindowManager wm = ((WindowManager)getSystemService(Context.WINDOW_SERVICE));
		Display disp = wm.getDefaultDisplay();
		int w = disp.getWidth();
		int h = disp.getHeight();
		// 共有により他のｱﾌﾟﾘから画像を渡された場合(暗黙intentから起動)
		Uri photoUri = null;
		Bitmap bitmap = null;
		try{
			photoUri = Uri.parse(getIntent().getExtras().get("android.intent.extra.STREAM").toString());
		} catch (Exception e) {
		}
		if (photoUri!=null) {
			try {
				Bitmap image = uri2bmp(photoUri);
				if (image!=null) {
					bitmap = image.copy(Bitmap.Config.ARGB_8888, true);
					image = null;
					System.gc();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    // ﾃﾞﾌｫﾙﾄは黒一色のﾋﾞｯﾄﾏｯﾌﾟを設定
		if (bitmap==null) {
			bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawColor(getResources().getColor(R.color.black));
		}
		// 画面の向きを設定
		orientation = disp.getRotation();
	    myview.setmBitmap(bitmap);
    	myview.initBitmap();
	    // 初回起動時はｷﾞｬﾗﾘｰを表示
	    //Intent intent = new Intent(Intent.ACTION_PICK);
    	//intent.setType("image/*");
    	//startActivityForResult(intent, REQUEST_PICK_CONTACT);
    	Toast.makeText(this, getString(R.string.toast_start), Toast.LENGTH_LONG).show();
		// ﾊﾞｯｸｱｯﾌﾟﾃﾞｰﾀの復元確認
		if (fileList().length>0) {
			String filename=fileList()[0];
			new AlertDialog.Builder(this)
			.setTitle(filename)
			.setMessage(getString(R.string.dialog_restore_select))
			.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					if (myview.restore()) {
						//src_bitmap=myview.getmBitmap();
						WindowManager wm = ((WindowManager)getSystemService(Context.WINDOW_SERVICE));
						Display disp = wm.getDefaultDisplay();
						orientation = disp.getRotation();
					    //myview.setmBitmap(src_bitmap);
				    	myview.initBitmap();
				    	Toast.makeText(getApplicationContext(), getString(R.string.toast_restore_success), Toast.LENGTH_SHORT).show();
					} else {
						// ﾘｽﾄｱ失敗
						Toast.makeText(getApplicationContext(), getString(R.string.toast_restore_fail), Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
				}
			})
			.show();
		}
    }

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 終了処理
		Log.v("onDestroy", "終了処理");
		//src_bitmap = null;
		mPaint = null;
		mEmboss = null;
		mBlur_normal= null;
		mBlur_inner = null;
		mBlur_outer = null;
		mBlur_solid = null;
		bright_bar = null;
		chroma_bar = null;
		if (color_dialog != null) {
			color_dialog.dismiss();
		    color_dialog = null;
		}
		if (size_dialog != null) {
		    size_dialog.dismiss();
		    size_dialog = null;
		}
		if (effect_dialog != null) {
		    effect_dialog.dismiss();
		    effect_dialog = null;
		}
		if (mode_dialog != null) {
		    mode_dialog.dismiss();
		    mode_dialog = null;
		}
		if (bright_dialog != null) {
		    bright_dialog.dismiss();
		    bright_dialog = null;
		}
		if (chroma_dialog != null) {
		    chroma_dialog.dismiss();
		    chroma_dialog = null;
		}
		if (filter_dialog != null) {
			filter_dialog.dismiss();
			filter_dialog = null;
		}
		if (blur_dialog != null) {
			blur_dialog.dismiss();
			blur_dialog = null;
		}
		if (graphic_dialog != null) {
		    graphic_dialog.dismiss();
		    graphic_dialog = null;
		}
		if (prgDialog != null) {
		    prgDialog.dismiss();
			prgDialog = null;
		}
		myview.finish();
		myview = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ﾒﾆｭｰﾎﾞﾀﾝを押した場合、ﾒﾆｭｰの表示、非表示を切り替える
		if (keyCode == KeyEvent.KEYCODE_MENU) {
		    switch (((HorizontalScrollView)findViewById(R.id.horizontalScrollView_menu)).getVisibility()) {
		    case View.VISIBLE:
		    case View.INVISIBLE:
		    	((HorizontalScrollView)findViewById(R.id.horizontalScrollView_menu)).setVisibility(View.GONE);
		    	break;
		    case View.GONE:
		    	((HorizontalScrollView)findViewById(R.id.horizontalScrollView_menu)).setVisibility(View.VISIBLE);
		    	break;
		    }
		}
		// 戻るﾎﾞﾀﾝを押した場合、終了確認ﾀﾞｲｱﾛｸﾞを表示
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
			.setMessage(getString(R.string.dialog_close_confirm))
			.setPositiveButton(getString(R.string.dialog_save_close), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					myview.backup();
					Log.v("onKeyDown", "ﾊﾞｯｸｱｯﾌﾟ");
					finish();
				}
			})
			.setNeutralButton(getString(R.string.dialog_discard_close), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					// ﾊﾞｯｸｱｯﾌﾟﾃﾞｰﾀ全消去
			    	for (String filepath:fileList()) {
			    		deleteFile(filepath);
					}
			    	finish();
				}
			})
			.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					// ｷｬﾝｾﾙ
				}
			})
			.show();
		}
		return false;//super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// 画面回転後にｱﾌﾟﾘのﾃﾞｰﾀを復元
		Log.v("onRestoreInstanceState", "画面回転後のﾘｽﾄｱ");	// 未使用
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 画面回転検出時にｱﾌﾟﾘのﾃﾞｰﾀを保存
		Log.v("onSaveInstanceState", "画面回転前のｾｰﾌﾞ");		// 未使用
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// 画面回転検出時にｱﾌﾟﾘのﾃﾞｰﾀを保存
		Log.v("onRetainNonConfigurationInstance", "画面回転前のｾｰﾌﾞ");
		return super.onRetainNonConfigurationInstance();
	}

	@Override
	public void onClick(View v) {
		// ﾒﾆｭｰﾎﾞﾀﾝの重複を防ぐ
		((HorizontalScrollView)findViewById(R.id.horizontalScrollView_menu)).setEnabled(false);
		/*
		// ﾒﾆｭｰ押下時にﾊﾞｲﾌﾞﾚｰｼｮﾝ
		Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(200);
		*/
		//long[] pattern = {0,1000,500,300,100,50};
		//vibrator.vibrate(pattern, -1);
		// 色選択ﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_color))) {
			if (color_dialog!=null) { color_dialog=null; }
		    color_dialog = new AlertDialog.Builder(this)
		    	.setTitle(getString(R.string.dialog_color_select))
		    	.setPositiveButton(getString(R.string.dialog_cancel), null)
			    .setView(getColorView())
		    	.create();
		    color_dialog.show();
		}
		// ｻｲｽﾞﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_size))) {
			if (size_dialog!=null) { size_dialog=null; }
			size_dialog = new AlertDialog.Builder(this)
		    	.setTitle(getString(R.string.dialog_size_select))
		    	.setPositiveButton(getString(R.string.dialog_cancel), null)
			    .setView(getPenSizeView())
		    	.create();
		    size_dialog.show();
		}
		// ｴﾌｪｸﾄﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_effect))) {
			if (effect_dialog!=null) { effect_dialog=null; }
			effect_dialog = new AlertDialog.Builder(this)
		    	.setTitle(getString(R.string.dialog_effect_select))
		    	.setPositiveButton(getString(R.string.dialog_cancel), null)
			    .setView(getEffectView())
		    	.create();
			effect_dialog.show();
		}
		// ﾃｷｽﾄ入力ﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_text))) {
			if (mode_dialog!=null) { mode_dialog=null; }
	    	mode_dialog = new AlertDialog.Builder(this)
		    	.setTitle(getString(R.string.dialog_mode_select))
		    	.setPositiveButton(getString(R.string.dialog_cancel), null)
			    .setView(getModeView())
		    	.create();
			// ﾃｷｽﾄﾓｰﾄﾞ単一版
        	//myview.text_dialog.show();
			// ﾓｰﾄﾞﾘｽﾄ表示版
        	mode_dialog.show();
		}
		// 元に戻すﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_revert))) {
			// 元に戻す
        	myview.undo();
		}
		// 全消去ﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_clear))) {
			new AlertDialog.Builder(this)
			.setMessage(getString(R.string.dialog_clear_confirm))
			.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					// 黒一色に初期化
					WindowManager wm = ((WindowManager)getSystemService(Context.WINDOW_SERVICE));
					Display disp = wm.getDefaultDisplay();
					int w = disp.getWidth();
					int h = disp.getHeight();
					// 使用していたbitmapオブジェクトを一旦取得し、リサイクルされていない場合は、新規作成
					Bitmap bitmap = myview.getmBitmap();
				    // ﾃﾞﾌｫﾙﾄは黒一色のﾋﾞｯﾄﾏｯﾌﾟを設定
					if (!bitmap.isRecycled()) {
						bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
					}
					Canvas canvas = new Canvas(bitmap);
					canvas.drawColor(getResources().getColor(R.color.black));
					// 画面の向きを設定
					orientation = disp.getRotation();
					myview.setmBitmap(bitmap);
		        	myview.initBitmap();
					// ﾊﾞｯｸｱｯﾌﾟﾃﾞｰﾀ全消去
		        	for (String filepath:fileList()) {
		        		deleteFile(filepath);
					}
				}
			})
			.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
				}
			})
			.show();
		}
		// ｸﾞﾗﾌｨｯｸ（画像一括ｴﾌｪｸﾄ）
		if (v.equals(findViewById(R.id.imageButton_graphic))) {
			graphic_dialog.show();
		}
		// ｶﾒﾗ起動
		if (v.equals(findViewById(R.id.imageButton_camera))) {
			Calendar cl = Calendar.getInstance();
			Date dt = cl.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String filename = "+" + sdf.format(dt) + ".jpg";
			// 画像保存用のﾌｫﾙﾀﾞが存在しない場合は作成
			String folderPath = Environment.getExternalStorageDirectory().toString()
				+ "/" + getResources().getString(R.string.app_folder);
			capture_file = null;
			try {
				File dir = new File(folderPath);
				if (!dir.exists()) {
					dir.mkdir();
				}
				capture_file = new File(folderPath, filename);
			} catch (Exception e) {
			}
			// ｶﾒﾗ起動
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capture_file));
			startActivityForResult(intent, REQUEST_CAMERA);
		}
		// 読出ﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_load))) {
        	// ｲﾝﾃﾝﾄ設定
        	Intent intent = new Intent(Intent.ACTION_PICK);
        	// 全ｲﾒｰｼﾞ対象
        	intent.setType("image/*");
        	// ｷﾞｬﾗﾘｰ表示
        	startActivityForResult(intent, REQUEST_PICK_CONTACT);
		}
		// 保存ﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_save))) {
			// 保存確認ﾀﾞｲｱﾛｸﾞ
			new AlertDialog.Builder(this)
			.setMessage(getString(R.string.dialog_save_confirm))
			//.setView((View)findViewById(R.layout.save))
			.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					save();
				}
			})
			.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
				}
			})
			.show();
		}
		// 終了ﾎﾞﾀﾝ
		if (v.equals(findViewById(R.id.imageButton_close))) {
			new AlertDialog.Builder(this)
			.setMessage(getString(R.string.dialog_close_confirm))
			.setPositiveButton(getString(R.string.dialog_save_close), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					myview.backup();
					Log.v("onClick", "ﾊﾞｯｸｱｯﾌﾟ");
					finish();
				}
			})
			.setNeutralButton(getString(R.string.dialog_discard_close), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					// ﾊﾞｯｸｱｯﾌﾟﾃﾞｰﾀ全消去
			    	for (String filepath:fileList()) {
			    		deleteFile(filepath);
					}
			    	finish();
				}
			})
			.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					// ｷｬﾝｾﾙ
				}
			})
			.show();
			return;
		}
		// ﾒﾆｭｰﾎﾞﾀﾝの重複を防ぐ
		((HorizontalScrollView)findViewById(R.id.horizontalScrollView_menu)).setEnabled(true);
	}

	@Override
	public void OnFileSaved(boolean result, File file) {
		if (result) {
	        // 正常終了
	        new AlertDialog.Builder(this)
	        	.setTitle(getString(R.string.dialog_save_completed))
	            .setMessage(file.getPath())
	            .setPositiveButton("OK", null)
	            .show();
		} else {
	        new AlertDialog.Builder(this)
	        	.setTitle(getString(R.string.dialog_save_failed))
	        	.setMessage(file.getPath())
	            .setPositiveButton("OK", null)
	            .show();
		}
	}

	/** Activity「ｷﾞｬﾗﾘｰ」を表す定数 */
	private static final int REQUEST_PICK_CONTACT 	= 0;
	/** Activity「ｶﾒﾗ」を表す定数 */
	private static final int REQUEST_CAMERA			= 1;
	/** ｶﾒﾗｱﾌﾟﾘにて撮影したﾌｧｲﾙi */
	private File capture_file	= null;
	/**
	 * 標準（ｷﾞｬﾗﾘｰやｶﾒﾗ等）から戻り時に呼ばれるｲﾍﾞﾝﾄ
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("PhotoStudio", "onActivityResult/resultCode="+resultCode+"/requestCode="+requestCode);
		// ｷｬﾝｾﾙ
		if (resultCode == RESULT_CANCELED) { return; }
		if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_CONTACT) {
			// スレッドへ引渡し
			final Intent intent = data;
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 画像選択ｷｬﾝｾﾙ時
					if (intent == null) { return;}
					// 画像URIを取得
					Uri photoUri = intent.getData();
					// 画像を取得
					//ContentResolver conReslv = getContentResolver();
					if (photoUri != null) {
						try {
							// ﾋﾞｯﾄﾏｯﾌﾟ画像を取得
							//Bitmap image = MediaStore.Images.Media.getBitmap(conReslv, photoUri);
							Bitmap image = uri2bmp(photoUri);
							Bitmap bitmap = image.copy(Bitmap.Config.ARGB_8888, true);
							myview.setmBitmap(bitmap);
							image = null;
							System.gc();
						} catch (OutOfMemoryError ome) {
							System.gc();
							myview.setErr(MyView.ERR_OUTOFMEMORY);
						} catch (Exception e) {
							myview.setErr(MyView.ERR_EXCEPTION);
						}
					}
					// ﾊﾝﾄﾞﾗへ完了の合図を送出
					imgIandler.sendEmptyMessage(0);
				}
			}).start();
		}
		if (resultCode == RESULT_OK && requestCode == REQUEST_CAMERA) {
			// ｽﾚｯﾄﾞへ引渡し
			final Intent intent = data;
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 画像選択ｷｬﾝｾﾙ時
					if (intent == null) {
						Log.v("debug", "Intentがぬるぽ");
						//return;
					}
					// 画像URIを取得(Xperiaの場合はここで正規のﾊﾟｽが入ってくる)
					Uri photoUri = null;
					try {
						// 画像を登録
						Uri uri = Uri.fromFile(capture_file);
						ContentValues values = new ContentValues();
						values.put(MediaStore.Images.Media.TITLE,uri.getLastPathSegment());
						values.put(MediaStore.Images.Media.DISPLAY_NAME,uri.getLastPathSegment());
						values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
						values.put(MediaStore.Images.Media.DATA,uri.getPath());
						values.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());
						//Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
						getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
						// ﾋﾞｯﾄﾏｯﾌﾟ画像を取得
						//Bitmap image = MediaStore.Images.Media.getBitmap(conReslv, photoUri);
						if (photoUri == null) {
							photoUri = Uri.fromFile(capture_file);
						}
						Bitmap image = uri2bmp(photoUri);
						Bitmap bitmap = image.copy(Bitmap.Config.ARGB_8888, true);
						myview.setmBitmap(bitmap);
						image = null;
						System.gc();
					} catch (OutOfMemoryError ome) {
						System.gc();
						myview.setErr(MyView.ERR_OUTOFMEMORY);
					} catch (Exception e) {
						myview.setErr(MyView.ERR_EXCEPTION);
					}
					// ﾊﾝﾄﾞﾗへ完了の合図を送出
					imgIandler.sendEmptyMessage(0);
				}
			}).start();
		}
	}

	/**
	 * 画像選択完了時のｲﾍﾞﾝﾄﾊﾝﾄﾞﾗｰ
	 */
	private Handler imgIandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//myview.setmBitmap(src_bitmap);
			myview.initBitmap();
		}
	};

	/**
	 * 画像保存処理
	 * @return 保存成功時trueを返却
	 */
	private boolean save() {
		// 保存中ﾌﾟﾛｸﾞﾚｽﾀﾞｲｱﾛｸﾞ表示
		prgDialog.setTitle(R.string.dialog_save);
		prgDialog.setMessage(getString(R.string.dialog_saving));
		prgDialog.show();
		// ﾌﾟﾛｸﾞﾚｽﾀﾞｲｱﾛｸﾞ表示中の処理
		new Thread(new Runnable() {
			@Override
			public void run() {
				Calendar cl = Calendar.getInstance();
				Date dt = cl.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String filename = sdf.format(dt) + ".jpg";
		    	// 保存実行
		    	//String result = MediaStore.Images.Media.insertImage(
		    	//		getContentResolver(), myview.getmBitmap(), filename, null);
				// 画像保存用のﾌｫﾙﾀﾞが存在しない場合は作成
				String folderPath = Environment.getExternalStorageDirectory().toString()
					+ "/" + getResources().getString(R.string.app_folder);
				// 応答用ﾒｯｾｰｼﾞ
				Message msg = new Message();
				msg.obj = folderPath + "/" + filename;
				msg.arg1 = -1;
				FileOutputStream out = null;
				boolean isSuccess = false;
				try {
					File dir = new File(folderPath);
					if (!dir.exists()) {
						dir.mkdir();
					}
					File file = new File(folderPath, filename);
					out = new FileOutputStream(file);
					//if (file.createNewFile()) {
					Bitmap bmp = myview.getmBitmap();
					isSuccess = bmp.compress(CompressFormat.JPEG, 100, out);
					bmp = null;
					if (!isSuccess) {
						file.delete();
					}
					//}
					out.flush();
					dir = null;
					file = null;
				} catch (SecurityException se) {
					Log.v("save", "SecurityException", se);
					isSuccess = false;
				} catch (IOException ioe) {
					Log.v("save", "IOException", ioe);
					isSuccess = false;
				} finally {
					if (out != null) {
						try {
							out.close();
							out = null;
						} catch (Throwable t) {
						}
					}
				}
				// 保存失敗
				if (!isSuccess) {
					saveHandler.sendMessage(msg);
					return;
				}
				// 画像を登録
				Uri uri = Uri.fromFile(new File(folderPath, filename));
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.TITLE,uri.getLastPathSegment());
				values.put(MediaStore.Images.Media.DISPLAY_NAME,uri.getLastPathSegment());
				values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
				values.put(MediaStore.Images.Media.DATA,uri.getPath());
				values.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());
				//Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
				getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
				// 保存成功
				msg.arg1 = 0;
		    	saveHandler.sendMessage(msg);
			}
		})
		.start();
		return true;
	}

	/** 画像保存中ﾀﾞｲｱﾛｸﾞの消去と保存完了後の結果ﾀﾞｲｱﾛｸﾞ表示  */
	private Handler saveHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			prgDialog.hide();
			//prgDialog.dismiss();
			Bundle b = new Bundle();
			b.putString("path", (String)msg.obj);	// 保存先のﾊﾟｽをﾀﾞｲｱﾛｸﾞに表示するため引数に渡す
			if (msg.arg1 == 0) {	// 保存成功
				showDialog(DIALOG_SAVE_OK, b);
			} else {				// 保存失敗
				showDialog(DIALOG_SAVE_NG, b);
			}
			b = null;
			return;
		}
	};

	/** 保存完了ﾀﾞｲｱﾛｸﾞを表す定数 */
	private static final int DIALOG_SAVE_OK = 1;
	/** 保存失敗ﾀﾞｲｱﾛｸﾞを表す定数 */
	private static final int DIALOG_SAVE_NG = 2;

	@Override
	protected Dialog onCreateDialog(int id, Bundle b) {
		Dialog dialog = null;
		final String savePath=b.getString("path");
		switch (id) {
		case DIALOG_SAVE_OK:
			dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.dialog_save_completed)
			.setMessage(getString(R.string.dialog_save_completed_message) + savePath)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.dialog_share), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
					// 共有
					Log.v("onCreateDialog", "onClick():画像共有");

					/* エラー未発生、データ渡せず
					Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(bmp);
					canvas.drawColor(Color.RED);

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("image/jpeg");
					Bundle b = new Bundle();
					b.putParcelable("data", bmp);
					intent.putExtras(b);
					startActivity(Intent.createChooser(intent, null));
					*/

					// 画像共有Intentを呼び出し
					Intent intent=new Intent();
					intent.setAction(Intent.ACTION_SEND);
					// bitmapをストレージのパスから渡す

					intent.setType("image/jpeg");
					Uri uri=Uri.parse("file://"+savePath);
					intent.putExtra(Intent.EXTRA_STREAM, uri);
					// debug
					Log.v("onCreateDialog", "onClick():uri="+uri.toString());

					// bitmapをデータとして渡す
					/*
					try {
						intent.setType("data");
						Bundle b=new Bundle();
						b.putParcelable("data", myview.getmBitmap());
						intent.putExtras(b);
					} catch (Exception e) {
						Log.v("onCreateDialog", "onClick():data to intent Exception", e);
					}*/

					// TODO ｱﾒﾌﾞﾛに写真投稿出来ない
					// ParcelableをimplementしたBitmapであれば渡せる（小さいサイズのみ）

					//intent.putExtra(Intent.EXTRA_TITLE, "Photo");
					// ﾌｧｲﾙの読み込み権限を与える
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// EXTRA_TEXTを起動したIntentが受け取る場合
					//intent.putExtra(Intent.EXTRA_TEXT, "Photo");

					// intent実行（画像を受け取るアプリを選択するウィンドウ表示）
					startActivity(Intent.createChooser(intent, null));

				}
			})
			.setNegativeButton(getString(R.string.dialog_close),  new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
				}
			})
			.create();
			break;
		case DIALOG_SAVE_NG:
			dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.dialog_save_failed)
			.setMessage(getString(R.string.dialog_save_failed_message) + savePath)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.dialog_apply), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
				}
			})
			.create();
			break;
		}
		return dialog;//super.onCreateDialog(id, args);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle b) {
		super.onPrepareDialog(id, dialog, b);
		switch (id) {
		case DIALOG_SAVE_OK:
			dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.dialog_save_completed)
			.setMessage(getString(R.string.dialog_save_completed_message) + b.getString("path"))
			.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
				}
			})
			.create();
			break;
		case DIALOG_SAVE_NG:
			dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.dialog_save_failed)
			.setMessage(getString(R.string.dialog_save_failed_message) + b.getString("path"))
			.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whitch) {
				}
			})
			.create();
			break;
		}
	}

	/** 画面の向き変更前ﾊﾞｯﾌｧ */
	private int orientation = -1;
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// MyViewｸﾗｽ画面回転時の処理
		myview.resetBitmap();
	}

	/**
	 * 画面の向きに応じたBitmapｸﾗｽを返却
	 * @param bitmap 画面の向きに応じるBitmap
	 * @return 画面向きに応じたBitmap
	 */
	private synchronized Bitmap getRotationBmp(Bitmap bitmap, int now_orientation) {
		Matrix matrix = new Matrix();
		float rotate = 0f;
		// 画面の向きに応じて回転
		//float rotate = (orientation - disp.getRotation()) * 90f;
		// 縦か横かだけの回転
		if ((orientation == Surface.ROTATION_0) || (orientation == Surface.ROTATION_180)) {
			if ((now_orientation == Surface.ROTATION_90) || (now_orientation == Surface.ROTATION_270)) {
				rotate = -90f;
			}
		}
		if ((orientation == Surface.ROTATION_90) || (orientation == Surface.ROTATION_270)) {
			if ((now_orientation == Surface.ROTATION_0) || (now_orientation == Surface.ROTATION_180)) {
				rotate = 90f;
			}
		}
		matrix.postRotate(rotate);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		matrix = null;
		System.gc();
		return bmp;
	}

	/**
	 * Uri→bitmapを画像ｻｲｽﾞで読み込み(OutOfMemoryError対策)
	 * @param uri Uri Bitmapを取得するUri
	 * @return 指定したUriから取得したBitmapを返却
	 */
    public Bitmap uri2bmp(Uri uri) {
        BitmapFactory.Options options = null;
        InputStream in = null;
        try {
            // 画像ｻｲｽﾞの取得
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            in = getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(in, null, options);
            in.close();
			// 端末の画面ｻｲｽﾞを取得
			WindowManager wm = ((WindowManager)getSystemService(Context.WINDOW_SERVICE));
			Display disp = wm.getDefaultDisplay();
			int w = disp.getWidth();
			int h = disp.getHeight();
			// 画面の向きを取得
			orientation = disp.getRotation();
            int scaleW = 0;
            int scaleH = 0;
            // 横長の画像の場合は回転後のｻｲｽﾞを元に計算
            boolean isrotate = false;
            // 画面が縦の時
            if ((orientation == Surface.ROTATION_0) || (orientation == Surface.ROTATION_180)) {
	            if (options.outHeight < options.outWidth) {
	            	isrotate = true;
	            	scaleW = options.outWidth / h;
	            	scaleH = options.outHeight / w;
	            } else {
	            	scaleW = options.outWidth / w;
	            	scaleH = options.outHeight / h;
	            }
            } else {
            	if (options.outHeight > options.outWidth) {
	            	isrotate = true;
	            	scaleW = options.outWidth / h;
	            	scaleH = options.outHeight / w;
	            } else {
	            	scaleW = options.outWidth / w;
	            	scaleH = options.outHeight / h;
	            }
            }
            int scale = Math.max(scaleW,scaleH);
            //画像の読み込み
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            options.inPurgeable = true;
            // この時点は読み込み用に小さくしているため縮小が丁度ではない
            in = getContentResolver().openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
            in.close();
            // 以降で画面に丁度あうように微調整(縮小)
            // 画面が縦の時
            if ((orientation == Surface.ROTATION_0) || (orientation == Surface.ROTATION_180)) {
	            if (isrotate != false) {
	            	Matrix matrix = new Matrix();
					matrix.postRotate(90.0f);
					bmp = Bitmap.createBitmap(bmp, 0, 0,
							bmp.getWidth(), bmp.getHeight(), matrix, true);
	            }
	            // 縦長の画像(横幅を画面ｻｲｽﾞに合わせて、縦ｻｲｽﾞを横ｻｲｽﾞとの比率で縮小)
				if (h < bmp.getHeight()) {
					float reduce = h / (float)bmp.getHeight();
					float resize_w = (float)bmp.getWidth() * reduce;
					float resize_h = (float)bmp.getHeight() * reduce;
					// 小数点以下を破棄
					bmp = resize(bmp, (float)((int)resize_w), (float)((int)resize_h));
				}
				// 横長の画像(縦幅を画面ｻｲｽﾞに合わせて、横ｻｲｽﾞを縦ｻｲｽﾞとの比率で縮小)
				if (w < bmp.getWidth()) {
					float reduce = w / (float)bmp.getWidth();
					float resize_w = (float)bmp.getWidth() * reduce;
					float resize_h = (float)bmp.getHeight() * reduce;
					// 小数点以下を破棄
					bmp = resize(bmp, (float)((int)resize_w), (float)((int)resize_h));
				}
            } else {
            	if (isrotate != false) {
	            	Matrix matrix = new Matrix();
					matrix.postRotate(-90.0f);
					bmp = Bitmap.createBitmap(bmp, 0, 0,
							bmp.getWidth(), bmp.getHeight(), matrix, true);
	            }
	            // 縦長の画像(横幅を画面ｻｲｽﾞに合わせて、縦ｻｲｽﾞを横ｻｲｽﾞとの比率で縮小)
				if (h < bmp.getHeight()) {
					float reduce = h / (float)bmp.getHeight();
					float resize_w = (float)bmp.getWidth() * reduce;
					float resize_h = (float)bmp.getHeight() * reduce;
					// 小数点以下を破棄
					bmp = resize(bmp, (float)((int)resize_w), (float)((int)resize_h));
				}
				// 横長の画像(縦幅を画面ｻｲｽﾞに合わせて、横ｻｲｽﾞを縦ｻｲｽﾞとの比率で縮小)
				if (w < bmp.getWidth()) {
					float reduce = w / (float)bmp.getWidth();
					float resize_w = (float)bmp.getWidth() * reduce;
					float resize_h = (float)bmp.getHeight() * reduce;
					// 小数点以下を破棄
					bmp = resize(bmp, (float)((int)resize_w), (float)((int)resize_h));
				}
            }
            return bmp;
        } catch (Exception e) {
        	Log.v("uri2bmp", "", e);
            if (in != null)
				try {
					in.close();
				} catch (IOException ioe) {
				}
            return null;
        }
    }

	/**
	 * Bitmapｻｲｽﾞ変換
	 * @param bitmap ﾘｻｲｽﾞするBitmap
	 * @param resizeWidth ﾘｻｲｽﾞ後の幅
	 * @param resizeHeight ﾘｻｲｽﾞ後の高さ
	 * @return ﾘｻｲｽﾞ後のBitmap
	 */
	private Bitmap resize(Bitmap bitmap, float resizeWidth, float resizeHeight){
		if (bitmap == null) { return null; }
		if ((resizeWidth <= 0) || (resizeHeight <= 0)) { return null;}
		float resizeScaleWidth;
		float resizeScaleHeight;
		Matrix matrix = new Matrix();
		resizeScaleWidth = resizeWidth / (float)bitmap.getWidth();
		resizeScaleHeight = resizeHeight / (float)bitmap.getHeight();
		matrix.postScale(resizeScaleWidth, resizeScaleHeight);
		//Bitmap resizeBitmap = null;
		try {
			bitmap = Bitmap.createBitmap(
					bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
			Log.v("resize", "bitmap.getRowBytes=" + bitmap.getRowBytes(), e);
		}
		return bitmap;
	}

	/**
	 * 色選択ﾀﾞｲｱﾛｸﾞ
	 * @return ListViewに色ﾘｽﾄを追加して返却
	 */
	private View getColorView() {
		int[] colorarray = new int[colors_id.length];
		String[] items = new String[colorarray.length];
		for (int i = 0; i < items.length; i ++) {
			items[i] = "";	// ﾘｽﾄとしての文字は不要なのでとりあえず空
		}
		ListView listview = new ListView(this);
		listview.setAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_single_choice, items
			) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					// ｻｲｽﾞ指定用のｸﾗｽを使用してViewに実装する
					SelectDrawable d = new SelectDrawable();
					d.setSize(pensize);
					d.setColor(getResources().getColor(colors_id[position]));
					d.setFilter(peneffect);
					d.setMode(penmode);
					d.invalidateSelf();
					view.setBackgroundDrawable(d);
					d = null;
					return view;
				}
		});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				pencolor = getResources().getColor(colors_id[position]);
				mPaint.setColor(pencolor);
				//color_dialog.dismiss();
				color_dialog.hide();
			}
		});
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				pencolor = getResources().getColor(colors_id[position]);
				mPaint.setColor(pencolor);
				//color_dialog.dismiss();
				color_dialog.hide();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		return listview;
	}

	/**
	 * ﾍﾟﾝｻｲｽﾞ選択ﾀﾞｲｱﾛｸﾞ
	 * @return ListViewにﾍﾟﾝｻｲｽﾞﾘｽﾄを追加して返却
	 */
	private View getPenSizeView() {
		int[] sizearray = getResources().getIntArray(R.array.pensize_array);
		String[] items = new String[sizearray.length];
		for (int i = 0; i < items.length; i ++) {
			items[i] = "";	// ﾘｽﾄとしての文字は不要なのでとりあえず空
		}
		ListView listview = new ListView(this);
		listview.setAdapter(new ArrayAdapter<String> (this, android.R.layout.select_dialog_singlechoice, items
			) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					// ｻｲｽﾞ指定用のｸﾗｽを使用してViewに実装する
					SelectDrawable d = new SelectDrawable();
					d.setSize((float)getResources().getIntArray(R.array.pensize_array)[position]);
					d.setColor(pencolor);
					d.setFilter(peneffect);
					d.setMode(penmode);
					d.invalidateSelf();
					view.setBackgroundDrawable(d);
					d = null;
					return view;
				}
		});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				pensize = getResources().getIntArray(R.array.pensize_array)[position];
				mPaint.setStrokeWidth(pensize);
				mPaint.setTextSize(pensize);
				//size_dialog.dismiss();
				size_dialog.hide();
			}
		});
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				pensize = (float)getResources().getColor(colors_id[position]);
				mPaint.setStrokeWidth(pensize);
				mPaint.setTextSize(pensize);
				//size_dialog.dismiss();
				size_dialog.hide();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		return listview;
	}

	/**
	 * 特殊効果選択ﾀﾞｲｱﾛｸﾞ
	 * @return ListViewに特殊効果選択ﾘｽﾄを追加して返却
	 */
	public View getEffectView() {
		String[] effectarray = getResources().getStringArray(R.array.effect_array);
		String[] items = new String[effectarray.length];
		for (int i = 0; i < items.length; i ++) {
			items[i] = "";	// ﾘｽﾄとしての文字は不要なのでとりあえず空
		}
		ListView listview = new ListView(this);
		listview.setAdapter(new ArrayAdapter<String> (this, android.R.layout.select_dialog_singlechoice, items
			) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					// ｻｲｽﾞ指定用のｸﾗｽを使用してViewに実装する
					SelectDrawable d = new SelectDrawable();
					d.setSize(pensize);
					d.setColor(pencolor);
					if (position == 0) { d.setFilter(null); }
					if (position == 1) { d.setFilter(mEmboss); }
					if (position == 2) { d.setFilter(mBlur_normal); }
					if (position == 3) { d.setFilter(mBlur_inner); }
					if (position == 4) { d.setFilter(mBlur_outer); }
					if (position == 5) { d.setFilter(mBlur_solid); }
					d.setMode(penmode);
					d.invalidateSelf();
					view.setBackgroundDrawable(d);
					d = null;
					return view;
				}
		});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) { peneffect = null; }
				if (position == 1) { peneffect = mEmboss; }
				if (position == 2) { peneffect = mBlur_normal; }
				if (position == 3) { peneffect = mBlur_inner; }
				if (position == 4) { peneffect = mBlur_outer; }
				if (position == 5) { peneffect = mBlur_solid; }
				mPaint.setMaskFilter(peneffect);
				//effect_dialog.dismiss();
				effect_dialog.hide();
			}
		});
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) { peneffect = null; }
				if (position == 1) { peneffect = mEmboss; }
				if (position == 2) { peneffect = mBlur_normal; }
				if (position == 3) { peneffect = mBlur_inner; }
				if (position == 4) { peneffect = mBlur_outer; }
				if (position == 5) { peneffect = mBlur_solid; }
				mPaint.setMaskFilter(peneffect);
				//effect_dialog.dismiss();
				effect_dialog.hide();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		return listview;
	}

	/**
	 * ﾓｰﾄﾞ選択ﾀﾞｲｱﾛｸﾞ
	 * @return ListViewにﾓｰﾄﾞ選択ﾘｽﾄを追加して返却
	 */
	public View getModeView() {
		int[] modearray = new int[10];	// ﾓｰﾄﾞは10種類
		String[] items = new String[modearray.length];
		for (int i = 0; i < items.length; i ++) {
			items[i] = "";	// ﾘｽﾄとしての文字は不要なのでとりあえず空
		}
		ListView listview = new ListView(this);
		listview.setAdapter(new ArrayAdapter<String> (this, android.R.layout.select_dialog_singlechoice, items
			) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					// ｻｲｽﾞ指定用のｸﾗｽを使用してViewに実装する
					SelectDrawable d = new SelectDrawable();
					d.setSize(pensize);
					d.setColor(pencolor);
					d.setFilter(peneffect);
					d.setMode(position);
					d.invalidateSelf();
					view.setBackgroundDrawable(d);
					d = null;
					return view;
				}
		});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				penmode = position;
				//mode_dialog.dismiss();
				mode_dialog.hide();
				if (penmode == MODE_TEXT) {
		        	myview.text_dialog.show();
				}
			}
		});
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				penmode = position;
				//mode_dialog.dismiss();
				mode_dialog.hide();
				if (penmode == MODE_TEXT) {
		        	myview.text_dialog.show();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		return listview;
	}

	/** 画像効果適用中ﾀﾞｲｱﾛｸﾞを表示するため、選択した画像効果を保持する変数をここで定義 */
	private int graphic_type = 0;
	/**
	 * ｸﾞﾗﾌｨｯｸ選択ﾀﾞｲｱﾛｸﾞ
	 * @return ListViewにﾓｰﾄﾞ選択ﾘｽﾄを追加して返却
	 */
	public View getGraphicView() {
		//String[] graphicarray = getResources().getStringArray(R.array.graphic_array_name);
		String[] graphicarray = getResources().getStringArray(R.array.graphic_array);
		ListView listview = new ListView(this);
		listview.setAdapter(new ArrayAdapter<String> (this, android.R.layout.select_dialog_singlechoice, graphicarray
			) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					/*
					// ｻｲｽﾞ指定用のｸﾗｽを使用してViewに実装する
					SelectDrawable d = new SelectDrawable();
					d.setSize(pensize);
					d.setColor(pencolor);
					d.setFilter(peneffect);
					d.setMode(position);
					view.setBackgroundDrawable(d);
					d = null;*/
					view.setBackgroundColor(Color.GRAY);
					return view;
				}
		});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				graphic_type = position;
				//graphic_dialog.dismiss();
				graphic_dialog.hide();
				// 明度と彩度、ｶﾗｰﾌｨﾙﾀｰの場合は設定ﾀﾞｲｱﾛｸﾞがあるため処理中ﾀﾞｲｱﾛｸﾞを表示しない
				if ((ImageEffect.GRAPHIC_BRIGHTNESS == graphic_type)
				|| (ImageEffect.GRAPHIC_CHROMA == graphic_type)
				|| (ImageEffect.GRAPHIC_FILTER == graphic_type)) {
					myview.setGraphic(graphic_type);
				// ぼかしﾀﾞｲｱﾛｸﾞ
				/*
				} else if ((ImageEffect.GRAPHIC_BLUR_SIDE_L <= graphic_type)
				&& (graphic_type <= ImageEffect.GRAPHIC_BLUR_ALL_H)) {
					blur_dialog.show();
				*/
				} else {
					// 画像効果適用中ﾌﾟﾛｸﾞﾚｽﾊﾞｰ表示
					String[] graphicarray = getResources().getStringArray(R.array.graphic_array);
					prgDialog.setTitle(graphicarray[position]);
					prgDialog.setMessage(getString(R.string.dialog_grphic_executing));
					prgDialog.setCancelable(false);
					new Thread(new Runnable() {
						@Override
						public void run() {
							// 画像全体に効果を適用する
							myview.setGraphic(graphic_type);
							graphicHandler.sendEmptyMessage(0);
						}
					}).start();
					prgDialog.show();
				}
			}
		});
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				graphic_type = position;
				//graphic_dialog.dismiss();
				graphic_dialog.hide();
				// 明度と彩度の場合は設定ﾀﾞｲｱﾛｸﾞがあるため処理中ﾀﾞｲｱﾛｸﾞを表示しない
				if ((ImageEffect.GRAPHIC_BRIGHTNESS == graphic_type)
				|| (ImageEffect.GRAPHIC_CHROMA == graphic_type)
				|| (ImageEffect.GRAPHIC_FILTER == graphic_type)) {
					myview.setGraphic(graphic_type);
				// ぼかしﾀﾞｲｱﾛｸﾞ
				/*
				} else if ((ImageEffect.GRAPHIC_BLUR_SIDE_L <= graphic_type)
				&& (graphic_type <= ImageEffect.GRAPHIC_BLUR_ALL_H)) {
					blur_dialog.show();
				*/
				} else {
					// 画像効果適用中ﾌﾟﾛｸﾞﾚｽﾊﾞｰ表示
					String[] graphicarray = getResources().getStringArray(R.array.graphic_array);
					prgDialog.setTitle(graphicarray[position]);
					prgDialog.setMessage(getString(R.string.dialog_grphic_executing));
					prgDialog.setCancelable(false);
					new Thread(new Runnable() {
						@Override
						public void run() {
							// 画像全体に効果を適用する
							myview.setGraphic(graphic_type);
							graphicHandler.sendEmptyMessage(0);
						}
					}).start();
					prgDialog.show();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		return listview;
	}

	/** ｸﾞﾗﾌｨｯｸ適用中のﾌﾟﾛｸﾞﾚｽﾀﾞｲｱﾛｸﾞ消去処理 */
	private Handler graphicHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			myview.invalidate();
			prgDialog.hide();
			//prgDialog.dismiss();
			return;
		}
	};

	/**
	 * 指定された位置と半径を元に星を描画するPathｸﾗｽを返却
	 * @param center 星の中心
	 * @param r 星の半径
	 * @return 星を描画するためのPath
	 */
	public Path getStarPath(Point center, float r) {
		double angle = Math.PI / 2.0; // 傾きを調整するための角度
        // 星を描くために正五角形の各頂点の座標を計算
        float[] x = new float[5];
        float[] y = new float[5];
        for(int i=0; i<5; i++) {
            // 各頂点の座標を計算
            x[i] = (float) (center.x + (r * (Math.cos(2.0 * i * Math.PI / 5.0 + angle))));
            y[i] = (float) (center.y - (r * (Math.sin(2.0 * i * Math.PI / 5.0 + angle))));
        }
        // 正五角形の各頂点を星形になるように線で結んでいく
        Path path = new Path();
        path.moveTo(x[0], y[0]);
        path.lineTo(x[2], y[2]);
        path.lineTo(x[4], y[4]);
        path.lineTo(x[1], y[1]);
        path.lineTo(x[3], y[3]);
        path.lineTo(x[0], y[0]);
        // ﾀﾞﾌﾞﾙﾀｯﾁ時は角度を加える
        Path retPath = new Path();
        Matrix matrix = new Matrix();
        matrix.postRotate(myview.getAngle(), center.x, center.y);
        retPath.addPath(path, matrix);
        return retPath;
	}

	/**
	 * 指定された位置と半径を元にﾊｰﾄを描画するPathｸﾗｽを返却
	 * @param center ﾊｰﾄの中心
	 * @param r ﾊｰﾄの半径
	 * @return ﾊｰﾄを描画するためのPath
	 */
	public Path getHeartPath(Point center, float r) {
		Path path = new Path();
		// ﾊｰﾄを描画するために必要な座標を算出
		float half = r / 2;
		float[][] rp = new float[][] {
			{ center.x			, center.y - half },	// 中腹凹み
			{ center.x - half	, center.y + half},		// 左側膨らみ終点
			{ center.x 			, center.y + r },		// 下部末端
			{ center.x + half	, center.y + half },	// 右側膨らみ終点
		};
		// 左側膨らみ
		path.moveTo(rp[0][0], rp[0][1]);
		path.rCubicTo(-half, -half, -r, half, -half, r);
		// 下
		path.lineTo(rp[2][0], rp[2][1]);
		path.lineTo(rp[3][0], rp[3][1]);
		// 右側膨らみ
		path.rCubicTo(half, -half, 0, -(r + half), -half, -r);
		path.close();
		// ﾀﾞﾌﾞﾙﾀｯﾁ時は角度を加える
        Path retPath = new Path();
        Matrix matrix = new Matrix();
        matrix.postRotate(myview.getAngle(), center.x, center.y);
        retPath.addPath(path, matrix);
        return retPath;
	}

	/**
	 * 指定された位置と半径を元に太いﾊｰﾄを描画するPathｸﾗｽを返却
	 * @param center ﾊｰﾄの中心
	 * @param r ﾊｰﾄの半径
	 * @return ﾊｰﾄを描画するためのPath
	 */
	public Path getHeartFatPath(Point center, float r) {
		Path path = new Path();
		// ﾊｰﾄを描画するために必要な座標を算出
		float half = r / 2;
		float[][] rp = new float[][] {
			{ center.x			, center.y - half },			// 中腹凹み
			{ center.x - half	, center.y + half},				// 左側膨らみ終点
			{ center.x 			, center.y + half + r / 3 },	// 下部末端
			{ center.x + half	, center.y + half },			// 右側膨らみ終点
		};
		// 左側膨らみ
		path.moveTo(rp[0][0], rp[0][1]);
		path.rCubicTo(-half, -half, -(r + half), half, -half, r);
		// 下
		path.lineTo(rp[2][0], rp[2][1]);
		path.lineTo(rp[3][0], rp[3][1]);
		// 右側膨らみ
		path.rCubicTo(r, -half, 0, -(r + half), -half, -r);
		path.close();
		// ﾀﾞﾌﾞﾙﾀｯﾁ時は角度を加える
        Path retPath = new Path();
        Matrix matrix = new Matrix();
        matrix.postRotate(myview.getAngle(), center.x, center.y);
        retPath.addPath(path, matrix);
        return retPath;
	}

	/**
	 * 指定された位置と半径を元にｷﾗｷﾗを描画するPathｸﾗｽを返却
	 * @param center ｷﾗｷﾗの中心
	 * @param r ｷﾗｷﾗの半径
	 * @return ｷﾗｷﾗを描画するためのPath
	 */
	public Path getGlitterPath(Point center, float r) {
		Path path = new Path();
		float half = r / 2;
		float quart = r / 4;
		float oct = r / 8;
		path.moveTo(center.x, center.y - r);
		path.rCubicTo(-oct, quart * 2, -(oct * 2), quart * 3, -half, r);
		path.rCubicTo(oct * 2, quart, oct * 3, quart * 3, half, r);
		path.rCubicTo(oct, -(quart * 2), oct * 2, -(quart * 3), half, -r);
		path.rCubicTo(-(oct * 2), -quart, -(oct * 3), -(quart * 3), -half, -r);
		// ﾀﾞﾌﾞﾙﾀｯﾁ時は角度を加える
        Path retPath = new Path();
        Matrix matrix = new Matrix();
        matrix.postRotate(myview.getAngle(), center.x, center.y);
        retPath.addPath(path, matrix);
		return retPath;
	}

	/**
	 * 指定された位置と半径を元に涙を描画するPathｸﾗｽを返却
	 * @param center 涙の中心
	 * @param r 涙の半径
	 * @return 涙を描画するためのPath
	 */
	public Path getTearPath(Point center, float r) {
		Path path = new Path();
		path.moveTo(center.x, center.y - r);


		path.rCubicTo(-r, r * 2, r, r * 2, 0, 0);
		// ﾀﾞﾌﾞﾙﾀｯﾁ時は角度を加える
        Path retPath = new Path();
        Matrix matrix = new Matrix();
        matrix.postRotate(myview.getAngle(), center.x, center.y);
        retPath.addPath(path, matrix);
		return retPath;
	}

	/**
	 * 指定された位置と半径を元に花を描画するPathｸﾗｽを返却
	 * @param center 花の中心
	 * @param r 花の半径
	 * @return 花を描画するためのPath
	 */
	public Path getFlowerPath(Point center, float r) {
        // 中心の丸
        Path path = new Path();
		double angle = Math.PI / 2.0; // 傾きを調整するための角度
        // 花を描くために正十二角形の各頂点の座標を計算
        float[] x = new float[12];
        float[] y = new float[12];
        for(int i=0; i<12; i++) {
            // 各頂点の座標を計算（絶対値）
            //x[i] = (float) (center.x + (r * (Math.cos(2.0 * i * Math.PI / 12.0 + angle))));
            //y[i] = (float) (center.y - (r * (Math.sin(2.0 * i * Math.PI / 12.0 + angle))));
        	// 中心点からの距離で算出
        	x[i] = (float) ((r * (Math.cos(2.0 * i * Math.PI / 12.0 + angle))));
            y[i] = (float) ((r * (Math.sin(2.0 * i * Math.PI / 12.0 + angle))));
        }
        // 花の付け根部分の座標を算出
        float[] peak_x = new float[6];
        float[] peak_y = new float[6];
        for(int i=0; i<6; i++) {
            // 各頂点の座標を計算（絶対値）
        	peak_x[i] = (float) (center.x + ((r / 16) * (Math.cos(2.0 * i * Math.PI / 6.0 + angle))));
        	peak_y[i] = (float) (center.y - ((r / 16) * (Math.sin(2.0 * i * Math.PI / 6.0 + angle))));
        }
        // 花びらを描画
        path.moveTo(peak_x[3], peak_y[3]);
        path.rCubicTo(x[1], y[1], x[11], y[11], 0, 0);
        path.moveTo(peak_x[2], peak_y[2]);
        path.rCubicTo(x[1], y[1], x[3], y[3], 0, 0);
        path.moveTo(peak_x[1], peak_y[1]);
        path.rCubicTo(x[3], y[3], x[5], y[5], 0, 0);
        path.moveTo(peak_x[0], peak_y[0]);
        path.rCubicTo(x[5], y[5], x[7], y[7], 0, 0);
        path.moveTo(peak_x[5], peak_y[5]);
        path.rCubicTo(x[7], y[7], x[9], y[9], 0, 0);
        path.moveTo(peak_x[4], peak_y[4]);
        path.rCubicTo(x[9], y[9], x[11], y[11], 0, 0);
		// ﾀﾞﾌﾞﾙﾀｯﾁ時は角度を加える
        Path retPath = new Path();
        Matrix matrix = new Matrix();
        matrix.postRotate(myview.getAngle(), center.x, center.y);
        retPath.addPath(path, matrix);
        return retPath;
	}

	/**
	 * 指定された位置と半径を元に雪の結晶を描画するPathｸﾗｽを返却
	 * @param center 結晶の中心
	 * @param r 結晶の半径
	 * @return 結晶を描画するためのPath
	 */
	public Path getSnowPath(Point center, float r) {
        // 中心の丸
        Path path = new Path();
		double angle = Math.PI / 2.0; // 傾きを調整するための角度
		// 結晶の描画に必要な正六角形の座標を算出
        float[][] x = new float[6][5];
        float[][] y = new float[6][5];
        float r5 = (r / 5);
        for(int i=0; i<6; i++) {
        	for (int j=0; j<5; j++) {
	        	// 各頂点の座標を計算（絶対値）
	        	x[i][j] = (float) (center.x + ((r5 * (j + 1)) * (Math.cos(2.0 * i * Math.PI / 6.0 + angle))));
	        	y[i][j] = (float) (center.y - ((r5 * (j + 1)) * (Math.sin(2.0 * i * Math.PI / 6.0 + angle))));
        	}
        }
		// 結晶の描画に必要な正十二角形の座標を算出
        float[][] x24 = new float[24][5];
        float[][] y24 = new float[24][5];
        for(int i=0; i<24; i++) {
        	for (int j=0; j<5; j++) {
	        	// 各頂点の座標を計算（絶対値）
	        	x24[i][j] = (float) (center.x + ((r5 * (j + 1)) * (Math.cos(2.0 * i * Math.PI / 24.0 + angle))));
	        	y24[i][j] = (float) (center.y - ((r5 * (j + 1)) * (Math.sin(2.0 * i * Math.PI / 24.0 + angle))));
        	}
        }
		// 中心の六角形の線＊
        for (int i=0; i<6; i++) {
	        //path.moveTo(center.x, center.y);
        	path.moveTo(x[i][0], y[i][0]);
			path.lineTo(x[i][4], y[i][4]);
			path.close();
        }
		// 小さい六角形
        path.moveTo(x[0][0], y[0][0]);
        for (int i=1; i<6; i++) {
        	path.lineTo(x[i][0], y[i][0]);
        }
        path.lineTo(x[0][0], y[0][0]);
        path.close();
		// 1段階大きい六角形
        path.moveTo(x[0][1], y[0][1]);
        for (int i=1; i<6; i++) {
        	path.lineTo(x24[(i*4)-2][3], y24[(i*4)-2][3]);
        	path.lineTo(x[i][1], y[i][1]);
        }
        path.lineTo(x24[(6*4)-2][3], y24[(6*4)-2][3]);
        path.lineTo(x[0][1], y[0][1]);
        path.close();
        // 結晶の羽の部分
        path.moveTo(x[0][2], y[0][2]);
        path.lineTo(x24[1][3], y24[1][3]);
        path.close();
        path.moveTo(x[0][3], y[0][3]);
        path.lineTo(x24[1][4], y24[1][4]);
        path.close();
        // 結晶の上以外の部分（6角形の内5角形分の結晶の羽描画）
        for (int i=1; i<6; i++) {
        	path.moveTo(x[i][2],y[i][2]);
        	path.lineTo(x24[(i*4)-1][3], y24[(i*4)-1][3]);
        	path.close();
        	path.moveTo(x[i][3],y[i][3]);
        	path.lineTo(x24[(i*4)-1][4], y24[(i*4)-1][4]);
        	path.close();
        	path.moveTo(x[i][2],y[i][2]);
        	path.lineTo(x24[(i*4)+1][3], y24[(i*4)+1][3]);
        	path.close();
        	path.moveTo(x[i][3],y[i][3]);
        	path.lineTo(x24[(i*4)+1][4], y24[(i*4)+1][4]);
        	path.close();
        }
        // 結晶の上の部分のみ配列のｲﾝﾃﾞｯｸｽが合わないため個別に描画
        path.moveTo(x[0][2], y[0][2]);
        path.lineTo(x24[23][3], y24[23][3]);
        path.close();
        path.moveTo(x[0][3], y[0][3]);
        path.lineTo(x24[23][4], y24[23][4]);
        path.close();

		// ﾀﾞﾌﾞﾙﾀｯﾁ時は角度を加える
        Path retPath = new Path();
        Matrix matrix = new Matrix();
        matrix.postRotate(myview.getAngle(), center.x, center.y);
        retPath.addPath(path, matrix);
        return retPath;
	}

	/**
	 * 渡された引数間の角度-180～+180を算出
	 * @param x1 中心となるX軸
	 * @param y1 中心となるY軸
	 * @param x2 中心から角度を算出する位置のX軸
	 * @param y2 中心から角度を算出する位置のY軸
	 * @return
	 */
	private float get2PointAngle(float x1, float y1, float x2, float y2) {
		if ((x1 < 0) || (y1 < 0) || (x2 < 0) || (y2 < 0)) { return 0f; }
		double radian = (float)Math.atan2(x2 - x1, y2 - y1);	// ﾗｼﾞｱﾝ値(-3～+3)
		float angle = (float) (radian * 180f / Math.PI);
		return -angle;	// ﾀﾞﾌﾞﾙﾀｯﾁ時の2つ目のﾀｯﾁ位置に向かって角度を付けるためﾏｲﾅｽ
	}

	/**
	 * ﾍﾟﾝ指定用ﾀﾞｲｱﾛｸﾞのﾘｽﾄ用ｸﾗｽ
	 */
	public class SelectDrawable extends Drawable {
		private int color = Color.TRANSPARENT;
		private float size = 0;
		private MaskFilter filter = null;
		private int mode = MODE_NORMAL;
		@Override
		public void draw(Canvas canvas) {
			canvas.drawColor(Color.GRAY);
			Paint paint = new Paint();
			paint.setColor(color);
			// 線
			if (mode == MODE_NORMAL) {
				paint.setStrokeWidth(size);
				paint.setMaskFilter(filter);
				canvas.drawLine(10, 35, (float)getBounds().width() - 50, 35, paint);
			}
			// ﾃｷｽﾄ
			if (mode == MODE_TEXT) {
				//paint.setStrokeWidth(0.5f);
				paint.setStrokeWidth(pensize/30);
				paint.setTextSize(size);
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
				paint.setMaskFilter(filter);
				canvas.drawText(getString(R.string.menu_text), 10, 45, paint);
			}
			// 丸 ﾍﾟﾝｻｲｽﾞを半径として使用
			if (mode == MODE_CIRCLE) {
				paint.setStrokeWidth(size);
				paint.setMaskFilter(filter);
				paint.setStyle(Paint.Style.FILL);
				// 円の中心を引数に取るため左を他のﾓｰﾄﾞと合わせるためｻｲｽﾞ分X軸ずらす
				canvas.drawCircle(10 + size, 35, size, paint);
			}
			// 星
			if (mode == MODE_STAR) {
				paint.setStrokeWidth(size);
				paint.setMaskFilter(filter);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawPath(getStarPath(new Point((int)(10 + size), 35), size), paint);
			}
			// ﾊｰﾄ
			if (mode == MODE_HEART) {
				paint.setStrokeWidth(size);
				paint.setMaskFilter(filter);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawPath(getHeartPath(new Point((int)(10 + size), 35), size), paint);
			}
			// ﾊｰﾄ太
			if (mode == MODE_HEART_FAT) {
				paint.setStrokeWidth(size);
				paint.setMaskFilter(filter);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawPath(getHeartFatPath(new Point((int)(10 + size), 35), size), paint);
			}
			// ｷﾗｷﾗ
			if (mode == MODE_GLITTER) {
				paint.setStrokeWidth(size);
				paint.setMaskFilter(filter);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawPath(getGlitterPath(new Point((int)(10 + size), 35), size), paint);
			}
			// 涙
			if (mode == MODE_TEAR) {
				paint.setStrokeWidth(size);
				paint.setMaskFilter(filter);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawPath(getTearPath(new Point((int)(10 + size), 35), size), paint);
			}
			// 花
			if (mode == MODE_FLOWER) {
				paint.setStrokeWidth(size);
				paint.setMaskFilter(filter);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawPath(getFlowerPath(new Point((int)(10 + size), 35), size), paint);
			}
			// 雪の結晶
			if (mode == MODE_SNOW) {
				paint.setStrokeWidth(size/12);
				paint.setMaskFilter(filter);
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawPath(getSnowPath(new Point((int)(10 + size), 35), size), paint);
			}
			paint = null;
		}
		@Override
		public int getOpacity() {
			return 0;
		}
		@Override
		public void setAlpha(int alpha) {
		}
		@Override
		public void setColorFilter(ColorFilter cf) {
		}
		public void setColor(int color) {
			this.color = color;
		}
		public int getColor() {
			return this.color;
		}
		public void setSize(float size) {
			this.size = size;
		}
		public float getSize() {
			return this.size;
		}
		public MaskFilter getFilter() {
			return filter;
		}
		public void setFilter(MaskFilter filter) {
			this.filter = filter;
		}
		public int getMode() {
			return mode;
		}
		public void setMode(int mode) {
			this.mode = mode;
		}
	}
	public class MyView extends View implements GestureDetector.OnGestureListener {
		//private Context context = null;
	    @SuppressWarnings("unused")
		private static final float MINP = 0.25f;
	    @SuppressWarnings("unused")
		private static final float MAXP = 0.75f;

	    /** 定期ﾊﾞｯｸｱｯﾌﾟ間隔ms */
	    private static final long BACKUP_INTERVAL=60000;

	    private int err = ERR_NO_ERR;
	    public static final int ERR_NO_ERR		= 0;
	    public static final int ERR_EXCEPTION 	= 1;
		public static final int ERR_OUTOFMEMORY = 2;
		public static final int ERR_IOEXCEPTION = 3;

		//private SoftReference<Bitmap> mBitmap;
	    private Bitmap  mBitmap;
		private Canvas  mCanvas;
	    private Path    mPath;
	    private Paint   mBitmapPaint;

	    /** ｴﾌｪｸﾄ選択中ﾌﾟﾚﾋﾞｭｰﾌﾗｸﾞ */
	    private ColorFilter previewFilter = null;
		/** ﾃｷｽﾄ入力用のﾀﾞｲｱﾛｸﾞ */
	    public AlertDialog text_dialog = null;
	    /** ﾃｷｽﾄ入力用のｴﾃﾞｨｯﾄﾎﾞｯｸｽ */
	    private EditText edittext = null;
	    /** ﾀﾞﾌﾞﾙﾀｯﾁ時の角度 */
	    private float angle = 0;

	    /** 定期ﾊﾞｯｸｱｯﾌﾟｽﾚｯﾄﾞ */
	    private Thread backup_thread=null;

	    /** ロングタップ検出用 */
	    private GestureDetector gestureDetector = null;

		public MyView(Context c) {
	        super(c);
	        // ロングタップイベント用
	        gestureDetector = new GestureDetector(c, this);

	        //context = c;
	        setBackgroundColor(getResources().getColor(R.color.black));
	        edittext = new EditText(c);
	        edittext.setInputType(InputType.TYPE_CLASS_TEXT);
	        if (text_dialog != null) { text_dialog = null; }
        	text_dialog = new AlertDialog.Builder(getContext())
	    		.setTitle(getString(R.string.dialog_input_text))
	    		.setView(edittext)
	    		.setCancelable(false)
	    		.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						penmode = MODE_TEXT;
					}
				})
				.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
			        	penmode = MODE_NORMAL;
					}
				})
				.create();
	        initBitmap();
	        // 定期ﾊﾞｯｸｱｯﾌﾟｽﾚｯﾄﾞ
	        backup_thread=new Thread(new Runnable() {
				@Override
				public void run() {
					while (backup_thread!=null) {
						try {
							// 1分間隔でﾊﾞｯｸｱｯﾌﾟ
							Thread.sleep(BACKUP_INTERVAL);
						} catch (InterruptedException ie) {
						}
						if (backup_thread!=null) {
							backup();
							Log.v("MyView", "1分定期ﾊﾞｯｸｱｯﾌﾟ");
						}
					}
				}
			});
	        backup_thread.start();
	    }

		/**
		 * MyViewｸﾗｽの終了処理
		 */
		public void finish() {
			mBitmap.recycle();
			mBitmap = null;
			backup_thread=null;
		}

	    @Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	    }

	    public Canvas getmCanvas() {
			return mCanvas;
		}

		public void setmCanvas(Canvas mCanvas) {
			this.mCanvas = mCanvas;
		}

	    public Paint getmBitmapPaint() {
			return mBitmapPaint;
		}

		public void setmBitmapPaint(Paint mBitmapPaint) {
			this.mBitmapPaint = mBitmapPaint;
		}

		public ColorFilter getmBitmapColorFilter() {
			return mBitmapPaint.getColorFilter();
		}

		public void setmBitmapColorFilter(ColorFilter filter) {
			this.mBitmapPaint.setColorFilter(filter);
		}

		public int getErr() {
			return err;
		}

		public void setErr(int err) {
			this.err = err;
		}

	    public Bitmap getmBitmap() {
			return mBitmap;
		}

		public void setmBitmap(Bitmap mBitmap) {
			try {
				if (!mBitmap.isRecycled()) {
					this.mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
				}
				mBitmap.recycle();
				mBitmap = null;
			} catch (OutOfMemoryError ome) {
				showErrDialog(ERR_OUTOFMEMORY);
			}
		}

		public float getAngle() {
			return angle;
		}

		public void setAngle(float angle) {
			this.angle = angle;
		}

		public ColorFilter getPreviewFilter() {
			return previewFilter;
		}

		public void setPreviewFilter(ColorFilter previewFilter) {
			this.previewFilter = previewFilter;
		}

		public void showErrDialog(int error) {
			if (ERR_EXCEPTION == err) {
				new AlertDialog.Builder(getContext())
				.setMessage(R.string.error_exception)
				.setPositiveButton(getString(R.string.dialog_ok), null)
				.show();
				err = ERR_NO_ERR;
			}
			if (ERR_OUTOFMEMORY == err) {
				System.gc();
				new AlertDialog.Builder(getContext())
				.setMessage(R.string.error_bitmap_size)
				.setPositiveButton(getString(R.string.dialog_ok), null)
				.show();
				err = ERR_NO_ERR;
			}
		}

		/**
		 * ｱﾝﾄﾞｩを行うためのﾊﾞｯｸｱｯﾌﾟ
		 */
		public synchronized void addUndo() {
			if (mBitmap == null) { return; }
			UndoManager.addUndo(mBitmap, getmBitmapColorFilter());
		}

		/**
		 * ｱﾝﾄﾞｩ実行
		 */
		public synchronized void undo() {
	        mX = -1;
	        mY = -1;

	        // ﾋﾞｯﾄﾏｯﾌﾟのﾘｽﾄｱ
	        UndoModel undoModel = UndoManager.getUndo(0);
	        if (undoModel == null) { return; }
	        mBitmap = undoModel.getBitmap();
	        setmBitmapColorFilter(undoModel.getFilter());

	        // CanvasにUndoを適用
			if (mCanvas != null) { mCanvas = null; }
			if (mBitmap != null) {
				mCanvas = new Canvas(mBitmap);
				invalidate();
			} else {
				Log.e("undo", "mBitmapがnull");
			}

			// Undo完了済みのUndoModelを削除
			UndoManager.removeUndo(0);
			return;
		}

		/**
		 * ﾃﾞｰﾀ消失防止用画像ﾊﾞｯｸｱｯﾌﾟ処理
		 * 内部記憶領域へﾋﾞｯﾄﾏｯﾌﾟﾌｧｲﾙを保存
		 */
		public void backup() {
			if (mBitmap == null) { return; }
			// ﾊﾞｯｸｱｯﾌﾟ容量を超えた場合は削除
			while (fileList().length>=BACKUP_MAX){
				deleteFile(fileList()[0]);
			}
			// ﾊﾞｯｸｱｯﾌﾟﾌｧｲﾙ生成(後ろの要素が最新)
			FileOutputStream out=null;
			try {
				// 現在時刻ﾐﾘ秒までをﾌｧｲﾙ名として付与
				Calendar cl = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
				String filename=sdf.format(cl.getTime());
				out=openFileOutput(filename, Context.MODE_PRIVATE);
				mBitmap.compress(CompressFormat.JPEG, 100, out);
				Log.v("backup", "filename="+filename);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out=null;
			}
			return;
		}

		/**
		 * ﾃﾞｰﾀ消失防止用ﾘｽﾄｱ処理
		 * 内部記憶領域からﾋﾞｯﾄﾏｯﾌﾟ画像を読み出し
		 */
		public boolean restore() {
			if (fileList().length<=0) { return false; }
			// ここで座標を一旦ﾘｾｯﾄしないと全消去時に最後の絵が残る
	        mX = -1;
	        mY = -1;
	        // ﾋﾞｯﾄﾏｯﾌﾟのﾘｽﾄｱ
	        if (mBitmap != null) { mBitmap = null; }
	        String filepath=null;
	        try {
		        // ﾊﾞｯｸｱｯﾌﾟに用いるﾌｧｲﾙ名を生成(後ろの要素が最新)
		        filepath=fileList()[fileList().length-1];
		        // 内部記憶領域のﾊﾟｽからUriで変換出来るﾌｧｲﾙを生成
		        File file=getFileStreamPath(filepath);
		        // uri2bmpﾒｿｯﾄﾞを使うため、FileｸﾗｽからUriｸﾗｽを生成
		        Uri uri=Uri.fromFile(file);
		        // uri2bmpﾒｿｯﾄﾞを使いﾋﾞｯﾄﾏｯﾌﾟを生成（画像の向き、ｻｲｽﾞも併せて変換する）
		        Bitmap bitmap=uri2bmp(uri);
		        // 生成したﾋﾞｯﾄﾏｯﾌﾟからmutable（可変な）ﾋﾞｯﾄﾏｯﾌﾟを生成
		        mBitmap=bitmap.copy(Bitmap.Config.ARGB_8888, true);
	        } catch (Exception e) {
	        	return false;
	        }
	        if (mBitmap==null) { return false; }

			/*
	        FileInputStream in=null;
			try {
				in=openFileInput(filepath);
				// 1MBまで
				byte[] byteBmp=new byte[1024000];
				in.read(byteBmp);
				Bitmap bmp=BitmapFactory.decodeByteArray(byteBmp, 0, byteBmp.length);
				mBitmap=bmp.copy(Bitmap.Config.ARGB_8888, true);
				deleteFile(filepath);
			} catch (FileNotFoundException ffe) {
				Log.v("restore", "failed/filepath="+filepath, ffe);
				return false;
			} catch (IOException ioe) {
				Log.v("restore", "failed/filepath="+filepath, ioe);
				return false;
			} finally {
				if (in!=null) {
					try {
						in.close();
					} catch (IOException e) {
					}
					in=null;
				}
			}*/
			if (mCanvas != null) { mCanvas = null; }
			try {
				mCanvas = new Canvas(mBitmap);
			} catch (IllegalStateException ise) {
				// Immutable bitmap（不変なﾋﾞｯﾄﾏｯﾌﾟだった場合は可変で再度生成）
				return false;

			}
			invalidate();
	        // ﾃﾞﾊﾞｯｸﾞﾛｸﾞ
			Log.v("restore", "success/filepath="+filepath);
			return true;
		}

	    /**
	     * Bitmapをｸﾘｱする
	     */
	    public void initBitmap() {
	    	if (mBitmap != null) {

	    		// 画像が変わったらﾊﾟﾚｯﾄ用ﾚｲｱｳﾄのｻｲｽﾞを再定義
	            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mBitmap.getWidth(), mBitmap.getHeight());
	            params.gravity = Gravity.CENTER;
	            myview.setLayoutParams(params);

	        	// 各種描画用ｸﾗｽ再定義
		    	if (mCanvas != null) { mCanvas = null; }
		        mCanvas = new Canvas(mBitmap);
		        if (mPath != null) { mPath = null; }
		        mPath = new Path();
		        if (mBitmapPaint != null) { mBitmapPaint = null; }
		        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		        // ﾊﾞｯｸｱｯﾌﾟﾃﾞｰﾀを初期化
		        UndoManager.clear();

		        // ここで座標を一旦ﾘｾｯﾄしないと全消去時に最後の絵が残る
		        mX = -1;
		        mY = -1;

		        // 再描画実行
		        invalidate();
	    	}
	    }

	    /**
	     * 画面回転時に行うMyViewｸﾗｽのﾘｾｯﾄ
	     */
	    public synchronized void resetBitmap() {
	    	WindowManager wm = ((WindowManager)getSystemService(Context.WINDOW_SERVICE));
			Display disp = wm.getDefaultDisplay();
			int now_orientation = disp.getRotation();
			// 初回起動時
			if (orientation == -1) {
				orientation = now_orientation;
				return;
			}

			/** 2011.10.25 */
			// 現在の描画Viewとﾊﾞｯｸｱｯﾌﾟ分を画面の向きに合わせて回転
	    	//src_bitmap = getRotationBmp(src_bitmap, now_orientation);
			setmBitmap(getRotationBmp(mBitmap, now_orientation));

			// Undoデータも回転
			for (int i = 0; i < UndoManager.getUndoSize(); i++) {
				UndoModel undoModel = UndoManager.getUndo(i);
				Bitmap bitmap = undoModel.getBitmap();
				undoModel.setBitmap(getRotationBmp(bitmap, now_orientation));
				UndoManager.replaceUndo(undoModel);
			}

			wm = null;
			disp = null;
    		// 画像が変わったらﾊﾟﾚｯﾄ用ﾚｲｱｳﾄのｻｲｽﾞを再定義
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            		mBitmap.getWidth(), mBitmap.getHeight());
            params.gravity = Gravity.CENTER;
            setLayoutParams(params);
            // ｷｬﾝﾊﾞｽを再作成しないと、手書きが上手く行かないため再作成
			setmCanvas(new Canvas(mBitmap));
	    	// 描画状態を初期化(ここでﾘｾｯﾄしないとonDrawで描画される)
	    	mX = -1;
	    	mY = -1;
			invalidate();
			orientation = now_orientation;
	    }

	    @Override
	    protected void onDraw(Canvas canvas) {
	    	try {
		        canvas.drawColor(getResources().getColor(R.color.black));
		    	if (mBitmap != null) {
		    		// ｴﾌｪｸﾄﾌﾟﾚﾋﾞｭｰ中の場合
		    		if (previewFilter != null) {
		    			Paint p = new Paint(mBitmapPaint);
		    			p.setColorFilter(previewFilter);
		    			canvas.drawBitmap(mBitmap, 0, 0, p);
		    			p = null;
		    			return;
		    		} else {
		    			canvas.drawBitmap(mBitmap, 0, 0, new Paint(Paint.DITHER_FLAG));
		    		}
			        //canvas.drawBitmap(src_bitmap, 0, 0, mBitmapPaint);	// ｿｰｽの画像にのみｴﾌｪｸﾄ
			        //canvas.drawBitmap(mBitmap, 0, 0, null);				// 描画中のﾋﾞｯﾄﾏｯﾌﾟを重ねる
			        if ((mX < 0) || (mY < 0)) { return; }
			        // 線描写
			        if (penmode == MODE_NORMAL) {
				        canvas.drawPath(mPath, mPaint);
			        }
			        // ﾃｷｽﾄ描写
			        if (penmode == MODE_TEXT) {
				        // ﾃｷｽﾄ時のみｽﾄﾛｰｸを0.5f固定
				        Paint p = new Paint(mPaint);
				        //p.setStrokeWidth(0.5f);
				        p.setStrokeWidth(pensize/30);
				        p.setStyle(Paint.Style.FILL_AND_STROKE);
				        // Empty状態で文字をPathによって描画すると一瞬表示がおかしくなる
				        if (mPath.isEmpty() == false) {
				        	canvas.drawTextOnPath(edittext.getText().toString(), mPath, 0, 0, p);
				        }
				        p = null;
			        }
					// 丸
					if (penmode == MODE_CIRCLE) {
						Paint p = new Paint(mPaint);
						p.setStyle(Paint.Style.FILL);
						p.setAlpha(64);
						canvas.drawCircle(mX, mY, pensize, p);
						p = null;
					}
					// 星
					if (penmode == MODE_STAR) {
						Paint p = new Paint(mPaint);
						p.setStyle(Paint.Style.FILL);
						p.setAlpha(64);
						// ﾀｯﾁ位置が図形の中心に来るよう微調整
						float x = mX - pensize;
						float y = mY - pensize;
						canvas.drawPath(getStarPath(new Point((int)(x + pensize), (int)y), pensize), p);
						p = null;
					}
					// ﾊｰﾄ
					if (penmode == MODE_HEART) {
						Paint p = new Paint(mPaint);
						p.setStyle(Paint.Style.FILL);
						p.setAlpha(64);
						// ﾀｯﾁ位置が図形の中心に来るよう微調整
						float x = mX - pensize;
						float y = mY - pensize;
						canvas.drawPath(getHeartPath(new Point((int)(x + pensize), (int)y), pensize), p);
						p = null;
					}
					// ﾊｰﾄ太
					if (penmode == MODE_HEART_FAT) {
						Paint p = new Paint(mPaint);
						p.setStyle(Paint.Style.FILL);
						p.setAlpha(64);
						// ﾀｯﾁ位置が図形の中心に来るよう微調整
						float x = mX - pensize;
						float y = mY - pensize;
						canvas.drawPath(getHeartFatPath(new Point((int)(x + pensize), (int)y), pensize), p);
						p = null;
					}
					// ｷﾗｷﾗ
					if (penmode == MODE_GLITTER) {
						Paint p = new Paint(mPaint);
						p.setStyle(Paint.Style.FILL);
						p.setAlpha(64);
						// ﾀｯﾁ位置が図形の中心に来るよう微調整
						float x = mX - pensize;
						float y = mY - pensize;
						canvas.drawPath(getGlitterPath(new Point((int)(x + pensize), (int)y), pensize), p);
						p = null;
					}
					// 涙
					if (penmode == MODE_TEAR) {
						Paint p = new Paint(mPaint);
						p.setStyle(Paint.Style.FILL);
						p.setAlpha(64);
						// ﾀｯﾁ位置が図形の中心に来るよう微調整
						float x = mX - pensize;
						float y = mY - pensize;
						canvas.drawPath(getTearPath(new Point((int)(x + pensize), (int)y), pensize), p);
						p = null;
					}
					// 花
					if (penmode == MODE_FLOWER) {
						Paint p = new Paint(mPaint);
						p.setStyle(Paint.Style.FILL);
						p.setAlpha(64);
						// ﾀｯﾁ位置が図形の中心に来るよう微調整
						float x = mX - pensize;
						float y = mY - pensize;
						canvas.drawPath(getFlowerPath(new Point((int)(x + pensize), (int)y), pensize), p);
						p = null;
					}
					// 雪の結晶
					if (penmode == MODE_SNOW) {
						Paint p = new Paint(mPaint);
						p.setStyle(Paint.Style.STROKE);
						p.setStrokeWidth(pensize/12);
						p.setAlpha(64);
						// ﾀｯﾁ位置が図形の中心に来るよう微調整
						float x = mX - pensize;
						float y = mY - pensize;
						canvas.drawPath(getSnowPath(new Point((int)(x + pensize), (int)y), pensize), p);
						p = null;
					}
		    	}
	    	} catch (Exception e) {
	    		Log.v("PicStudio", "onDraw", e);
	    		// Draw中に例外が発生したら元に戻す
	    		//restore();
	    	}
	    }

	    private float mX, mY;
	    private static final float TOUCH_TOLERANCE = 4;

	    private void touch_start(float x, float y) {
	        // ﾊﾞｯｸｱｯﾌﾟ
	        addUndo();
	        mPath.reset();
	        mPath.moveTo(x, y);
	        mX = x;
	        mY = y;

	    }
	    private void touch_move(float x, float y) {
	        float dx = Math.abs(x - mX);
	        float dy = Math.abs(y - mY);
	        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
	            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
	            mX = x;
	            mY = y;
	        }
	    }
	    private void touch_up() {
	        // commit the path to our offscreen
	        // 線
	        if (penmode == MODE_NORMAL) {
	        	mPath.lineTo(mX, mY);
	        	mCanvas.drawPath(mPath, mPaint);
	        }
	        // ﾃｷｽﾄ
	        if (penmode == MODE_TEXT) {
		        // ﾃｷｽﾄ時のみｽﾄﾛｰｸを0.5f固定(書き終わったらﾓｰﾄﾞを戻す)
		        Paint p = new Paint(mPaint);
		        //p.setStrokeWidth(0.5f);
		        p.setStrokeWidth(pensize/30);
		        p.setStyle(Paint.Style.FILL_AND_STROKE);
		        // Empty状態で文字をPathによって描画すると一瞬表示がおかしくなる
		        if (mPath.isEmpty() == false) {
		        	mCanvas.drawTextOnPath(edittext.getText().toString(), mPath, 0, 0, p);
		        }
		        p = null;
	        }
	        // 丸
			if (penmode == MODE_CIRCLE) {
				Paint p = new Paint(mPaint);
				p.setStyle(Paint.Style.FILL);
				p.setAlpha(194);
				mCanvas.drawCircle(mX, mY, pensize, p);
				p = null;
			}
			// 星
			if (penmode == MODE_STAR) {
				Paint p = new Paint(mPaint);
				p.setStyle(Paint.Style.FILL);
				p.setAlpha(194);
				// ﾀｯﾁ位置が図形の中心に来るよう微調整
				float x = mX - pensize;
				float y = mY - pensize;
				mCanvas.drawPath(getStarPath(new Point((int)(x + pensize), (int)y), pensize), p);
			}
			// ﾊｰﾄ
			if (penmode == MODE_HEART) {
				Paint p = new Paint(mPaint);
				p.setStyle(Paint.Style.FILL);
				p.setAlpha(194);
				// ﾀｯﾁ位置が図形の中心に来るよう微調整
				float x = mX - pensize;
				float y = mY - pensize;
				mCanvas.drawPath(getHeartPath(new Point((int)(x + pensize), (int)y), pensize), p);
			}
			// ﾊｰﾄ太
			if (penmode == MODE_HEART_FAT) {
				Paint p = new Paint(mPaint);
				p.setStyle(Paint.Style.FILL);
				p.setAlpha(194);
				// ﾀｯﾁ位置が図形の中心に来るよう微調整
				float x = mX - pensize;
				float y = mY - pensize;
				mCanvas.drawPath(getHeartFatPath(new Point((int)(x + pensize), (int)y), pensize), p);
			}
			// ｷﾗｷﾗ
			if (penmode == MODE_GLITTER) {
				Paint p = new Paint(mPaint);
				p.setStyle(Paint.Style.FILL);
				p.setAlpha(194);
				// ﾀｯﾁ位置が図形の中心に来るよう微調整
				float x = mX - pensize;
				float y = mY - pensize;
				mCanvas.drawPath(getGlitterPath(new Point((int)(x + pensize), (int)y), pensize), p);
			}
			// 涙
			if (penmode == MODE_TEAR) {
				Paint p = new Paint(mPaint);
				p.setStyle(Paint.Style.FILL);
				p.setAlpha(194);
				// ﾀｯﾁ位置が図形の中心に来るよう微調整
				float x = mX - pensize;
				float y = mY - pensize;
				mCanvas.drawPath(getTearPath(new Point((int)(x + pensize), (int)y), pensize), p);
			}
			// 花
			if (penmode == MODE_FLOWER) {
				Paint p = new Paint(mPaint);
				p.setStyle(Paint.Style.FILL);
				p.setAlpha(194);
				// ﾀｯﾁ位置が図形の中心に来るよう微調整
				float x = mX - pensize;
				float y = mY - pensize;
				mCanvas.drawPath(getFlowerPath(new Point((int)(x + pensize), (int)y), pensize), p);
			}
			// 雪の結晶
			if (penmode == MODE_SNOW) {
				Paint p = new Paint(mPaint);
				p.setStyle(Paint.Style.STROKE);
				p.setStrokeWidth(pensize/12);
				p.setAlpha(194);
				// ﾀｯﾁ位置が図形の中心に来るよう微調整
				float x = mX - pensize;
				float y = mY - pensize;
				mCanvas.drawPath(getSnowPath(new Point((int)(x + pensize), (int)y), pensize), p);
			}
	        // kill this so we don't double draw
	        mPath.reset();
	    }

	    @Override
	    public boolean onTouchEvent(MotionEvent event) {

	    	// ロングタップ検出用
	    	gestureDetector.onTouchEvent(event);

	    	if (mBitmap == null) { return true; }
	        float x = event.getX();
	        float y = event.getY();
	        switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
    	        	touch_start(x, y);
	                invalidate();
	                break;
	            case MotionEvent.ACTION_MOVE:
	            	// ﾀﾞﾌﾞﾙﾀｯﾁ時は画像を回転させる
	    	        if (event.getPointerCount() == 2) {
	    	        	setAngle(get2PointAngle(event.getX(0), event.getY(0), event.getX(1), event.getY(1)));
	    	        }
	    	        touch_move(x, y);
	                invalidate();
	                break;
	            case MotionEvent.ACTION_UP:
	                touch_up();
	                invalidate();
	                break;
	        }
	        return true;
	    }

	    /**
	     * @see GestureDetector.OnGestureListener
	     */
	    @Override
	    public boolean onDown(MotionEvent e) {
	    	Log.v("OnGestureListener", "onDown");
	    	return false;
	    }

	    /**
	     * @see GestureDetector.OnGestureListener
	     */
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	    	Log.v("OnGestureListener", "onFling");
	    	return false;
	    }

	    /**
	     * @see GestureDetector.OnGestureListener
	     */
	    @Override
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	    	Log.v("OnGestureListener", "onScroll");
	    	return false;
	    }

	    /**
	     * @see GestureDetector.OnGestureListener
	     */
	    @Override
	    public void onShowPress(MotionEvent e) {
	    	Log.v("OnGestureListener", "onShowPress");
	    }

	    /**
	     * @see GestureDetector.OnGestureListener
	     */
	    @Override
	    public boolean onSingleTapUp(MotionEvent e) {
	    	Log.v("OnGestureListener", "onSingleTapUp");
	    	return false;
	    }

	    /**
	     * @see GestureDetector.OnGestureListener
	     */
	    @Override
	    public void onLongPress(MotionEvent e) {
	    	Log.v("OnGestureListener", "onLongPress");
	    	// ロングタップ時に設定中の色で背景色を塗りつぶし
	    	addUndo();
	    	Canvas canvas = new Canvas(this.mBitmap);
			canvas.drawColor(pencolor);
	    	invalidate();
	    }

	    /**
	     * 描画された箇所にｴﾌｪｸﾄを設定
	     */
	    public void setGraphic(int type) {
	    	switch (type) {
	    	case ImageEffect.GRAPHIC_BRIGHTNESS:
	    		addUndo();
	    		// 明度調整ﾀﾞｲｱﾛｸﾞ表示前にSeekBarを初期化
	    		//bright_bar.setProgress(0);
	    		bright_dialog.show();	// 指定値によってｴﾌｪｸﾄ
	    		break;
	    	case ImageEffect.GRAPHIC_CHROMA:
	    		addUndo();
	    		// 彩度調整ﾀﾞｲｱﾛｸﾞ表示前にSeekBarを初期化
	    		//chroma_bar.setProgress(0);
	    		chroma_dialog.show();	// 指定値によってｴﾌｪｸﾄ
	    		break;
	    	case ImageEffect.GRAPHIC_FILTER:
	    		addUndo();
				// ColorFilter表示前にSeekBarとTextViewを初期化
				for (int i=0; i<filter_bar.length; i++) {
					filter_bar[i].setProgress(10);
					filter_view[i].setText("1.0");
				}
	    		filter_dialog.show();	// 指定値によってｴﾌｪｸﾄ
	    	case ImageEffect.GRAPHIC_TOY:
				addUndo();
				bright_bar.setProgress(90);
				setmBitmapColorFilter(ImageEffect.getBrightnessFilter(90));
				chroma_bar.setProgress(90);
				setmBitmapColorFilter(ImageEffect.getChromaFilter(90));
				break;
	    	case ImageEffect.GRAPHIC_SEPIA:
	    		addUndo();
	    		// 一旦ｸﾞﾚｰｽｹｰﾙにしてからｾﾋﾟｱをﾌｨﾙﾀｰ
	    		mCanvas.drawBitmap(ImageEffect.effect(mBitmap, ImageEffect.GRAPHIC_MONOCHROME), 0, 0, null);
				setmBitmapColorFilter(ImageEffect.getColorFilter(1.2f, 1.0f, 0.8f)); //getSepiaFilter());
				break;
	    	case ImageEffect.GRAPHIC_MONOCHROME:
	    		addUndo();
				setmBitmapColorFilter(ImageEffect.getMonospaceFilter());
				break;
	    	/* なんか上手く行かないのでｺﾒﾝﾄ
	    	case ImageEffect.GRAPHIC_NEGA:
	    		addUndo();
				setmBitmapColorFilter(ImageEffect.getNegaFilter());
				break;
			*/
	    	default:	// ﾋﾟｸｾﾙ単位でｴﾌｪｸﾄを実行する場合
	    		addUndo();
	        	mCanvas.drawBitmap(ImageEffect.effect(mBitmap, type), 0, 0, null);
	    	}
	    	// ｶﾗｰﾌｨﾙﾀｰの状態をﾋﾞｯﾄﾏｯﾌﾟに反映
	    	drawBitmap();
	    	//setmBitmapColorFilter(null);
	    }

	    public void drawBitmap() {
	    	mCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
	    	// ｶﾗｰﾌｨﾙﾀｰ実行後は、変数を元に戻す
	        if (mBitmapPaint != null) { mBitmapPaint = null; }
	        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	    }
	}
}
