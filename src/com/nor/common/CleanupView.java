package com.nor.common;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

public class CleanupView {
	public static final void clean(View view) {
		if (view instanceof ImageButton) {
			ImageButton ib = (ImageButton) view;
			ib.setImageDrawable(null);
		} else if (view instanceof ImageView) {
			ImageView iv = (ImageView) view;
			iv.setImageDrawable(null);
		} else if (view instanceof SeekBar) {
			SeekBar sb = (SeekBar) view;
			sb.setProgressDrawable(null);
			sb.setThumb(null);
		// } else if(view instanceof( xxxx )) { --
		// 他にもDrawableを使用するUIコンポーネントがあれば追加
		}
		view.setBackgroundDrawable(null);
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			int size = vg.getChildCount();
			for (int i = 0; i < size; i++) {
				clean(vg.getChildAt(i));
			}
		}
	}

}
