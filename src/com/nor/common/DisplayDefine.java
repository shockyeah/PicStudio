package com.nor.common;

/**
 * 11/08/04 時点未使用 出力画像のｻｲｽﾞ変更用に準備
 * @author shukuya
 *
 */
public class DisplayDefine {
	public static final int TYPE_CGA 	= 0;	// 320x200
	public static final int TYPE_QVGA	= 1;	// 320x240
	public static final int TYPE_VGA	= 2;	// 640x480
	public static final int TYPE_WVGA	= 3;	// 800x480
	public static final int TYPE_WVGAA	= 4;	// 854x480
	public static final int TYPE_SVGA	= 5;	// 800x600
	public static final int TYPE_WSVGA	= 6;	// 1024x600
	public static final int TYPE_XGA	= 7;	// 1024x768
	public static final int TYPE_HD720	= 8;	// 1280x720
	public static final int TYPE_WXGA	= 9;	// 1280x768
	public static final int TYPE_WXGAA	= 10;	// 1280x800
	public static final int TYPE_SXGA	= 11;	// 1280x1024
	public static final int TYPE_WSXGA	= 12;	// 1680x1050
	public static final int TYPE_SXGAA  = 13;	// 1400x1050

	private static int[][] sizes = {
		{TYPE_CGA	,320	,200},
		{TYPE_QVGA	,320	,240},
		{TYPE_VGA	,640	,480},
		{TYPE_WVGA	,800	,480},
		{TYPE_WVGAA	,854	,480},
		{TYPE_SVGA	,800	,600},
		{TYPE_WSVGA	,1024	,600},
		{TYPE_XGA	,1024	,600},
		{TYPE_HD720	,1280	,768},
		{TYPE_WXGA	,1280	,720},
		{TYPE_WXGAA	,1280	,768},
		{TYPE_SXGA	,1280	,800},
		{TYPE_WSXGA	,1680	,1050},
		{TYPE_SXGAA	,1400	,1050}
	};

	private static String[] names = {
		"TYPE_CGA",
		"TYPE_QVGA",
		"TYPE_VGA",
		"TYPE_WVGA",
		"TYPE_WVGAA",
		"TYPE_SVGA",
		"TYPE_WSVGA",
		"TYPE_XGA",
		"TYPE_HD720",
		"TYPE_WXGA",
		"TYPE_WXGAA",
		"TYPE_SXGA",
		"TYPE_WSXGA",
		"TYPE_SXGAA"
	};

	public static int[] getSize(int type) {
		try {
			int[] retsize = new int[2];
			retsize[0] = sizes[type][1];
			retsize[1] = sizes[type][2];
			return retsize;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return null;
	}

	public static String getName(int type) {
		try {
			return names[type];
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return null;
	}
}