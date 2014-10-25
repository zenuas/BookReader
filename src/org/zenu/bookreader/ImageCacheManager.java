package org.zenu.bookreader;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class ImageCacheManager
{
	public String CacheDir = "";
	
	public ImageCacheManager(Context context)
	{
		File d = context.getExternalCacheDir();
		CacheDir = (d != null ? d : context.getCacheDir()).getAbsolutePath();
	}
	
	@SuppressWarnings("deprecation")
	public Drawable getCacheImage(Book book, String page, int width, int height) throws Exception
	{
		String file = getHashcode(Path.combine(book.Path, page)) + "_" + String.valueOf(width) + "_" + String.valueOf(height);
		File cache = new File(Path.combine(CacheDir, file));
		if(cache.isFile() && book.getLastModified() <= cache.lastModified())
		{
			return(new BitmapDrawable(cache.getCanonicalPath()));
		}
		else
		{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(book.getStream(page), null, opt);
			
			opt.inJustDecodeBounds = false;
			opt.inSampleSize = Math.max(1, Math.min(opt.outWidth / width, opt.outHeight / height));
			opt.inPurgeable = true;
			Bitmap bmp = BitmapFactory.decodeStream(book.getStream(page), null, opt);
			
			cache.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(cache);
			try
			{
				bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			}
			finally
			{
				out.close();
			}
			
			return(new BitmapDrawable(bmp));
		}
	}
	
	public String getHashcode(String s) throws NoSuchAlgorithmException
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(s.getBytes());
		
		// join "" $ map (x -> toHexString(x)) $ md5.digest
		byte[] digest = md5.digest();
		StringBuilder hex = new StringBuilder();
		for(int i = 0; i < digest.length; i++)
		{
			int x = digest[i] & 0xff;
			hex.append((x < 0x10 ? "0" : "") + Integer.toHexString(x));
		}
		return(hex.toString());
	}
}
