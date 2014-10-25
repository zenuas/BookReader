package org.zenu.bookreader;

import java.io.File;

import android.content.Context;


public class ImageCacheManager
{
	public String CacheDir = "";
	
	public ImageCacheManager(Context context)
	{
		File d = context.getExternalCacheDir();
		CacheDir = (d != null ? d : context.getCacheDir()).getAbsolutePath();
	}
}
