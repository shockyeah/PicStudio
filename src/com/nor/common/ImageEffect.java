package com.nor.common;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.util.Log;

public class ImageEffect {
	/** ｸﾞﾗﾌｨｯｸ「明度」を表す定数 */
	public static final int GRAPHIC_BRIGHTNESS	= 0;
	/** ｸﾞﾗﾌｨｯｸ「彩度」を表す定数 */
	public static final int GRAPHIC_CHROMA		= 1;
	/** ｸﾞﾗﾌｨｯｸ「ｶﾗｰﾌｨﾙﾀｰ」を表す定数 */
	public static final int GRAPHIC_FILTER		= 2;
	/** ｸﾞﾗﾌｨｯｸ「ﾄｲｶﾒﾗ」を表す定数 */
	public static final int GRAPHIC_TOY			= 3;
	/** ｸﾞﾗﾌｨｯｸ「ｾﾋﾟｱ」を表す定数 */
	public static final int GRAPHIC_SEPIA		= 4;
    /** ｸﾞﾗﾌｨｯｸ「ﾓﾉｸﾛ」を表す定数 */
	public static final int GRAPHIC_MONOCHROME 	= 5;
	/** ｸﾞﾗﾌｨｯｸ「ﾈｶﾞ」を表す定数 */
	//public static final int GRAPHIC_NEGA		= 5;
	/** ｸﾞﾗﾌｨｯｸ「白黒」を表す定数 */
	public static final int GRAPHIC_WHITEBLACK	= 6;
	/** ｸﾞﾗﾌｨｯｸ「ﾓｻﾞｲｸ」を表す定数 */
	public static final int GRAPHIC_MOSAIC 		= 7;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし左右（弱）」を表す定数 */
	public static final int GRAPHIC_BLUR_SIDE_L	= 8;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし左右（中）」を表す定数 */
	public static final int GRAPHIC_BLUR_SIDE_N	= 9;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし左右（強）」を表す定数 */
	public static final int GRAPHIC_BLUR_SIDE_H	= 10;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし上下（弱）」を表す定数 */
	public static final int GRAPHIC_BLUR_VERT_L	= 11;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし上下（中）」を表す定数 */
	public static final int GRAPHIC_BLUR_VERT_N	= 12;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし上下（強）」を表す定数 */
	public static final int GRAPHIC_BLUR_VERT_H	= 13;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし上下左右（弱）」を表す定数 */
	public static final int GRAPHIC_BLUR_CUBE_L	= 14;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし上下左右（中）」を表す定数 */
	public static final int GRAPHIC_BLUR_CUBE_N	= 15;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし上下左右（強）」を表す定数 */
	public static final int GRAPHIC_BLUR_CUBE_H	= 16;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし円（弱）」を表す定数 */
	public static final int GRAPHIC_BLUR_CIRC_L	= 17;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし円（中）」を表す定数 */
	public static final int GRAPHIC_BLUR_CIRC_N	= 18;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし円（強）」を表す定数 */
	public static final int GRAPHIC_BLUR_CIRC_H	= 19;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし全体（弱）」を表す定数 */
	public static final int GRAPHIC_BLUR_ALL_L	= 20;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし全体（中）」を表す定数 */
	public static final int GRAPHIC_BLUR_ALL_N	= 21;
	/** ｸﾞﾗﾌｨｯｸ「ぼかし全体（強）」を表す定数 */
	public static final int GRAPHIC_BLUR_ALL_H	= 22;

    /** ﾓｻﾞｲｸのﾄﾞｯﾄ数 */
    private static final int MOSAIC_DOT = 8;

    /**
     * 指定した座標が全体に対する1/scopeに含まれる場合true
     * @param wh 全体の幅
     * @param xy ﾁｪｯｸする座標
     * @param scope 幅に対するﾁｪｯｸ範囲を分母で指定(3の場合は画面幅の1/3以内の時true)
     * @return 指定範囲に指定座標が含まれる場合はtrueを返却
     */
    public static boolean isScope(int wh, int xy, int scope) {
    	// 画面左側から1/scopeに含まれる場合
    	if (0 <= xy && xy <= (wh / scope)) { return true; }
    	// 画面右側から1/scopeに含まれる場合
    	if ((wh / scope) * (scope - 1) <= xy && xy <= wh) { return true; }
    	return false;
    }

    /**
     *
     * @param bmp
     * @param type
     * @return
     */
    public static Bitmap effect(Bitmap bmp, int type) {
    	int width = bmp.getWidth();
    	int height = bmp.getHeight();
    	// 画像の中心座標
    	Point center = new Point(width / 2, height / 2);
    	// ぼかしに用いる円の半径
    	double radius = (width + height) / 6;
    	// ｶﾗｰﾌｨﾙﾀｰ一括で操作を行うｴﾌｪｸﾄはstatic関数を呼び出して行う
    	// ﾋﾟｸｾﾙ単位で操作を行うｴﾌｪｸﾄ
    	int pixels[] = new int[width * height];
    	// TODO debug ｴﾌｪｸﾄ実行時間観測
    	//long start = System.currentTimeMillis();
    	bmp.getPixels(pixels, 0, width, 0, 0, width, height);
    	for (int x = 0; x < width; x++) {
    		for (int y = 0; y < height; y++) {
    			int pixel = pixels[x + y * width];
    			// ぼかし（左右）
    			if ((GRAPHIC_BLUR_SIDE_L == type) || (GRAPHIC_BLUR_SIDE_N == type) || (GRAPHIC_BLUR_SIDE_H == type)) {
    				// 画面の左右1/4にぼかしを入れる
    				//if (0 <= x && x <= (width / 4) || (width / 4) * 3 <= x && x <= width) {
    				if (isScope(width, x, 4)) {
    					int color = -1;
    					if (GRAPHIC_BLUR_SIDE_L == type) { color = getBlurPixelLight(pixels, x, y, width, height); }
    					if (GRAPHIC_BLUR_SIDE_N == type) { color = getBlurPixel(pixels, x, y, width, height); }
    					if (GRAPHIC_BLUR_SIDE_H == type) { color = getBlurPixelHeavy(pixels, x, y, width, height); }
        				if (-1 != color) {
    						pixels[x + y * width] = color;
    					}
    				}
    			}
    			// ぼかし（上下）
    			if ((GRAPHIC_BLUR_VERT_L == type) || (GRAPHIC_BLUR_VERT_N == type) || (GRAPHIC_BLUR_VERT_H == type)) {
    				// 画面の上下1/4にぼかしを入れる
    				//if (0 <= y && y <= (height / 4) || (height / 4) * 3 <= y && y <= height) {
    				if (isScope(height, y, 4)) {
    					int color = -1;
    					if (GRAPHIC_BLUR_VERT_L == type) { color = getBlurPixelLight(pixels, x, y, width, height); }
    					if (GRAPHIC_BLUR_VERT_N == type) { color = getBlurPixel(pixels, x, y, width, height); }
    					if (GRAPHIC_BLUR_VERT_H == type) { color = getBlurPixelHeavy(pixels, x, y, width, height); }
        				if (-1 != color) {
    						pixels[x + y * width] = color;
    					}
    				}
    			}
    			// ぼかし（上下左右）
    			if ((GRAPHIC_BLUR_CUBE_L == type) || (GRAPHIC_BLUR_CUBE_N == type) || (GRAPHIC_BLUR_CUBE_H == type)) {
    				// 画面の上下左右1/4にぼかしを入れる
    				if (isScope(width, x, 4) || isScope(height, y, 4)) {
    					int color = -1;
    					if (GRAPHIC_BLUR_CUBE_L == type) { color = getBlurPixelLight(pixels, x, y, width, height); }
    					if (GRAPHIC_BLUR_CUBE_N == type) { color = getBlurPixel(pixels, x, y, width, height); }
    					if (GRAPHIC_BLUR_CUBE_H == type) { color = getBlurPixelHeavy(pixels, x, y, width, height); }
        				if (-1 != color) {
    						pixels[x + y * width] = color;
    					}
    				}
    			}
    			// ぼかし（円）
    			if ((GRAPHIC_BLUR_CIRC_L == type) || (GRAPHIC_BLUR_CIRC_N == type) || (GRAPHIC_BLUR_CIRC_H == type)) {
    				// 中心と対象座標間の距離が指定した半径以上の場合はぼかし効果適用
    				double dx = Math.pow(center.x - x, 2);
    				double dy = Math.pow(center.y - y, 2);
    				double distance = Math.sqrt(dx + dy);
    				if (distance>radius) {
						int color = -1;
						if (GRAPHIC_BLUR_CIRC_L == type) { color = getBlurPixelLight(pixels, x, y, width, height); }
						if (GRAPHIC_BLUR_CIRC_N == type) { color = getBlurPixel(pixels, x, y, width, height); }
						if (GRAPHIC_BLUR_CIRC_H == type) { color = getBlurPixelHeavy(pixels, x, y, width, height); }
	    				if (-1 != color) {
							pixels[x + y * width] = color;
						}
    				}
    			}
    			// ぼかし（全体）
    			if ((GRAPHIC_BLUR_ALL_L == type) || (GRAPHIC_BLUR_ALL_N == type) || (GRAPHIC_BLUR_ALL_H == type)) {
					int color = -1;
					if (GRAPHIC_BLUR_ALL_L == type) { color = getBlurPixelLight(pixels, x, y, width, height); }
					if (GRAPHIC_BLUR_ALL_N == type) { color = getBlurPixel(pixels, x, y, width, height); }
					if (GRAPHIC_BLUR_ALL_H == type) { color = getBlurPixelHeavy(pixels, x, y, width, height); }
    				if (-1 != color) {
						pixels[x + y * width] = color;
					}
    			}
				// ﾓｻﾞｲｸ
				if (GRAPHIC_MOSAIC == type) {
					if ((0 == x % MOSAIC_DOT) && (0 == y % MOSAIC_DOT)) {
						int r = 0;
						int g = 0;
						int b = 0;
						for (int i = 0; i < MOSAIC_DOT; i++) {
							if (x + i >= width) { break; }
							for (int j = 0 ; j < MOSAIC_DOT; j++) {
								if (y + j >= height) { break; }
								int dotColor = Color.BLACK;
								try {
									dotColor = pixels[(x + i) + (width * (y + j))];
								} catch (ArrayIndexOutOfBoundsException e) {
									Log.v("setGraphic", "x=" + x + "/y=" + y + "/width=" + width + "/height=" + height);
								}
								r += Color.red(dotColor);
								g += Color.green(dotColor);
								b += Color.blue(dotColor);
							}
						}
						r = r / (MOSAIC_DOT * MOSAIC_DOT);
						g = g / (MOSAIC_DOT * MOSAIC_DOT);
						b = b / (MOSAIC_DOT * MOSAIC_DOT);
						for (int k = 0; k < MOSAIC_DOT; k++) {
							for (int l = 0 ; l < MOSAIC_DOT; l++){
								try {
									pixels[(x + k) + (width * (y + l))] = Color.rgb(r, g, b);
								} catch (ArrayIndexOutOfBoundsException e) {
								}
							}
						}
					}
				}
				// ｾﾋﾟｱ調実行時にﾋﾟｸｾﾙ単位でﾓﾉｸﾛにする必要があるため残す
				// ﾓﾉｸﾛ
				if (GRAPHIC_MONOCHROME == type) {
					int g = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
					pixels[x + y * width] = Color.argb(Color.alpha(pixel), g, g, g);
				}
				// 白黒
				if (GRAPHIC_WHITEBLACK == type) {
					int g = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
					// 若干白を多めに設定(半分は0x80)
					if (g < 0x60) {
						g = Color.BLACK;
					} else {
						g = Color.WHITE;
					}
					pixels[x + y * width] = Color.argb(Color.alpha(pixel), g, g, g);
				}
    		}
    	}
    	bmp.setPixels(pixels, 0, width, 0, 0, width, height);
    	// TODO debug
    	//Log.v("effect", "ｴﾌｪｸﾄ実行時間=" + (System.currentTimeMillis() - start) + "ms");
    	return bmp;
    }

    /**
     * 指定したﾋﾟｸｾﾙにぼかし効果を付けて返却 3x3
     * @param pixels ぼかし効果を適用する範囲のﾋﾟｸｾﾙ値配列
     * @param x ぼかし効果の中心点X軸
     * @param y ぼかし効果の中心点Y軸
     * @param width ぼかし効果適用範囲の横
     * @param height ぼかし効果適用範囲の縦
     * @return
     */
    public static int getBlurPixelLight(int[] pixels, int x, int y, int width, int height) {
    	//double a = 0.0;
		double r = 0.0;
		double g = 0.0;
		double b = 0.0;
		double coe = 1.0/9.0;	// ぼかし幅の二乗
		// 畳込み演算
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				int dotColor = Color.BLACK;
				try {
					int index = (x + i) + (width * (y + j));
					// 画面外がぼかし範囲になった場合は中心点の色を割り当て
					if ((index < 0) || (pixels.length - 1 < index)) {
						index = x + (width * y);
					}
					dotColor = pixels[index];
					//a += coe * Color.alpha(dotColor);
					r += coe * Color.red(dotColor);
					g += coe * Color.green(dotColor);
					b += coe * Color.blue(dotColor);
				} catch (ArrayIndexOutOfBoundsException e) {
					Log.v("getBlurPixelLight", "x=" + x + "/y=" + y + "/width=" + width + "/height=" + height);
				}
			}
		}
		return Color.rgb((int)r, (int)g, (int)b);
		//return Color.argb((int)a, (int)r, (int)g, (int)b);
    }

    /**
     * 指定したﾋﾟｸｾﾙにぼかし効果を付けて返却 5x5
     * @param pixels ぼかし効果を適用する範囲のﾋﾟｸｾﾙ値配列
     * @param x ぼかし効果の中心点X軸
     * @param y ぼかし効果の中心点Y軸
     * @param width ぼかし効果適用範囲の横
     * @param height ぼかし効果適用範囲の縦
     * @return
     */
    public static int getBlurPixel(int[] pixels, int x, int y, int width, int height) {
    	//double a = 0.0;
		double r = 0.0;
		double g = 0.0;
		double b = 0.0;
		double coe = 1.0/25.0;	// ぼかし幅の二乗
		// 畳込み演算
		for(int i = -2; i <= 2; i++) {
			for(int j = -2; j <= 2; j++) {
				int dotColor = Color.BLACK;
				try {
					int index = (x + i) + (width * (y + j));
					// 画面外がぼかし範囲になった場合は中心点の色を割り当て
					if ((index < 0) || (pixels.length - 1 < index)) {
						index = x + (width * y);
					}
					dotColor = pixels[index];
					//a += coe * Color.alpha(dotColor);
					r += coe * Color.red(dotColor);
					g += coe * Color.green(dotColor);
					b += coe * Color.blue(dotColor);
				} catch (ArrayIndexOutOfBoundsException e) {
					Log.v("getBlurPixel", "x=" + x + "/y=" + y + "/width=" + width + "/height=" + height);
				}
			}
		}
		return Color.rgb((int)r, (int)g, (int)b);
		//return Color.argb((int)a, (int)r, (int)g, (int)b);
    }

    /**
     * 指定したﾋﾟｸｾﾙにぼかし効果を付けて返却 7x7
     * @param pixels ぼかし効果を適用する範囲のﾋﾟｸｾﾙ値配列
     * @param x ぼかし効果の中心点X軸
     * @param y ぼかし効果の中心点Y軸
     * @param width ぼかし効果適用範囲の横
     * @param height ぼかし効果適用範囲の縦
     * @return
     */
    public static int getBlurPixelHeavy(int[] pixels, int x, int y, int width, int height) {
    	//double a = 0.0;
		double r = 0.0;
		double g = 0.0;
		double b = 0.0;
		double coe = 1.0/49.0;	// ぼかし幅の二乗
		// 畳込み演算
		for(int i = -3; i <= 3; i++) {
			for(int j = -3; j <= 3; j++) {
				int dotColor = Color.BLACK;
				try {
					int index = (x + i) + (width * (y + j));
					// 画面外がぼかし範囲になった場合は中心点の色を割り当て
					if ((index < 0) || (pixels.length - 1 < index)) {
						index = x + (width * y);
					}
					dotColor = pixels[index];
					//a += coe * Color.alpha(dotColor);
					r += coe * Color.red(dotColor);
					g += coe * Color.green(dotColor);
					b += coe * Color.blue(dotColor);

				} catch (ArrayIndexOutOfBoundsException e) {
					Log.v("getBlurPixel", "x=" + x + "/y=" + y + "/width=" + width + "/height=" + height);
				}
			}
		}
		return Color.rgb((int)r, (int)g, (int)b);
		//return Color.argb((int)a, (int)r, (int)g, (int)b);
    }

    /**
     *
     * @param value
     * @return
     */
    public static ColorMatrixColorFilter getBrightnessFilter(int value) {
    	ColorMatrix cont = new ColorMatrix();
    	ColorMatrix sat = new ColorMatrix();
    	ColorMatrix concat = new ColorMatrix();
        float scale = (float)(value)/50.0f;
        float translate = (-.5f * scale + .5f) * 255.f;
        cont.set(new float[] {
    		scale	,0		,0		,0	,translate,
    		0		,scale	,0		,0	,translate,
    		0		,0		,scale	,0	,translate,
    		0		,0		,0		,1	,0}
        );
        concat.reset();
        concat.postConcat(sat);
        concat.postConcat(cont);
        return new ColorMatrixColorFilter(concat);
    }

    /**
     *
     * @param value
     * @return
     */
    public static ColorMatrixColorFilter getChromaFilter(int value) {
    	ColorMatrix cont = new ColorMatrix();
    	ColorMatrix sat = new ColorMatrix();
    	ColorMatrix concat = new ColorMatrix();
        float rl = 0.212671f;
        float gl = 0.715160f;
        float bl = 0.072169f;
        float sf = (float)(value)/50.0f;
        float nf = 1-sf;
        float nr = rl * nf;
        float ng = gl * nf;
        float nb = bl * nf;
        sat.set(new float[] {
    		nr+sf	,ng		,nb		,0	,0,
    		nr		,ng+sf	,nb		,0	,0,
    		nr		,ng		,nb+sf	,0	,0,
    		0		,0		,0		,1	,0 }
        );
        concat.reset();
        concat.postConcat(sat);
        concat.postConcat(cont);
        return new ColorMatrixColorFilter(concat);
    }

    /**
    * ｾﾋﾟｱ調のｶﾗｰﾌｨﾙﾀｰを取得
    * @return
    */
   public static ColorMatrixColorFilter getSepiaFilter() {
		ColorMatrix cont = new ColorMatrix();
		ColorMatrix sat = new ColorMatrix();
		ColorMatrix concat = new ColorMatrix();
		cont.set(new float[] {
	   		1.2f	,0		,0		,0	,0,
	   		0		,1.0f	,0  	,0	,0,
	   		0		,0		,0.7f	,0	,0,
	   		0		,0		,0  	,1	,0}
	  		/*0.9f	,0		,0		,0	,0,
	   		0		,0.7f	,0  	,0	,0,
	   		0		,0		,0.4f	,0	,0,
	   		0		,0		,0  	,1	,0}*/
		);
		concat.reset();
		concat.postConcat(sat);
		concat.postConcat(cont);
		return new ColorMatrixColorFilter(concat);
   }

   /**
    * ﾈｶﾞ調のｶﾗｰﾌｨﾙﾀｰを取得
    * @return
    */
   public static ColorMatrixColorFilter getNegaFilter() {
	   	ColorMatrix cont = new ColorMatrix();
	   	ColorMatrix sat = new ColorMatrix();
	   	ColorMatrix concat = new ColorMatrix();
	   	cont.set(new float[] {
       		-1	,0	,0  ,0	,0xFF,
       		0	,-1	,0  ,0	,0xFF,
       		0	,0	,-1	,0	,0xFF,
       		0	,0	,0  ,1	,0}
	   	);
	   	concat.reset();
	   	concat.postConcat(sat);
	   	concat.postConcat(cont);
	   	return new ColorMatrixColorFilter(concat);
   }

   /**
    * ﾓﾉｸﾛ調のｶﾗｰﾌｨﾙﾀｰを取得
    * @return
    */
   public static ColorMatrixColorFilter getMonospaceFilter() {
	   	ColorMatrix cont = new ColorMatrix();
	   	ColorMatrix sat = new ColorMatrix();
	   	ColorMatrix concat = new ColorMatrix();
	   	float r = 0.298912f;
	   	float g = 0.586611f;
	   	float b = 0.114478f;
	   	cont.set(new float[] {
       		r	,g	,b  ,0 	,0,
       		r	,g	,b  ,0 	,0,
       		r	,g	,b	,0 	,0,
       		0	,0  ,0  ,1	,0}
	   	);
	   	concat.reset();
	   	concat.postConcat(sat);
	   	concat.postConcat(cont);
	   	return new ColorMatrixColorFilter(concat);
   }

   /**
    * 指定した色のｶﾗｰﾌｨﾙﾀｰを取得
    * @param r
    * @param g
    * @param b
    * @return
    */
   public static ColorMatrixColorFilter getColorFilter(float r, float g, float b) {
		ColorMatrix cont = new ColorMatrix();
		ColorMatrix sat = new ColorMatrix();
		ColorMatrix concat = new ColorMatrix();
		cont.set(new float[] {
	   		r		,0		,0		,0	,0,
	   		0		,g		,0  	,0	,0,
	   		0		,0		,b		,0	,0,
	   		0		,0		,0  	,1	,0}
		);
		concat.reset();
		concat.postConcat(sat);
		concat.postConcat(cont);
		return new ColorMatrixColorFilter(concat);
   }
}
