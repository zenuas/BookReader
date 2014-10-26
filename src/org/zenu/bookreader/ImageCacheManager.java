package org.zenu.bookreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class ImageCacheManager
{
	public String CacheDir = "";
	
	final static Object temp_create_ = new Object();
	final static Object cache_access_ = new Object();
	
	public ImageCacheManager(Context context)
	{
		File d = context.getExternalCacheDir();
		CacheDir = (d != null ? d : context.getCacheDir()).getAbsolutePath();
		
		// 5分間隔で有効期限の切れたキャッシュを消去する、終了条件なし
		new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					while(true)
					{
						try
						{
							Thread.sleep(5 * 60 * 1000);
							clearCache(new Date().getTime());
						}
						catch(InterruptedException e)
						{
							e.printStackTrace();
							ApplicationContext.getContext().addBugReport(e);
						}
					}
				}
			}).start();
	}
	
	public Bitmap getBitmap(Book book, String page, int width, int height) throws Exception
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(book.getStream(page), null, opt);
		
		opt.inJustDecodeBounds = false;
		if(width > 0 && height > 0)
		{
			opt.inSampleSize = Math.max(1, Math.min(opt.outWidth / width, opt.outHeight / height));
		}
		else
		{
			opt.inSampleSize = Math.max(1, Math.min(opt.outWidth / 1000, opt.outHeight / 1000));
		}
		opt.inPurgeable = true;
		return(BitmapFactory.decodeStream(book.getStream(page), null, opt));
	}
	
	@SuppressWarnings("deprecation")
	public Drawable getCacheImage(Book book, String page, int width, int height, long expire) throws Exception
	{
		// cache = CacheDir/getHashcode(Path, page)_width_height_expire
		String file = getHashcode(Path.combine(book.Path, page)) + "_" + String.valueOf(width) + "_" + String.valueOf(height) + "_" + String.valueOf(expire);
		final File cache = new File(Path.combine(CacheDir, file));
		
		// 幅、高さの指定されていない場合はキャッシュ作成など行わない
		if(width > 0 && height > 0)
		{
			synchronized(cache_access_)
			{
				if(cache.isFile() && book.getLastModified() <= cache.lastModified())
				{
					cache.setLastModified(new Date().getTime());
					return(new BitmapDrawable(cache.getCanonicalPath()));
				}
			}
		}
		
		Bitmap bmp = getBitmap(book, page, width, height);
		if(width > 0 && height > 0 && bmp != null)
		{
			makeCache(cache, bmp, false);
		}
		
		return(new BitmapDrawable(bmp));
	}
	
	public void makeCache(Book book, String page, int width, int height, long expire) throws Exception
	{
		if(width <= 0 || height <= 0) {return;}
		
		// cache = CacheDir/getHashcode(Path, page)_width_height_expire
		String file = getHashcode(Path.combine(book.Path, page)) + "_" + String.valueOf(width) + "_" + String.valueOf(height) + "_" + String.valueOf(expire);
		File cache = new File(Path.combine(CacheDir, file));
		
		synchronized(cache_access_)
		{
			if(cache.isFile() && book.getLastModified() <= cache.lastModified())
			{
				cache.setLastModified(new Date().getTime());
				return;
			}
		}
		
		Bitmap bmp = getBitmap(book, page, width, height);
		if(bmp != null) {makeCache(cache, bmp, true);}
	}
	
	public void makeCache(final File cache, final Bitmap bmp, final boolean needRecycle)
	{
		new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// .tempにファイル出力し、最後に目的のファイルにリネームする
					synchronized(temp_create_)
					{
						try
						{
							File temp = new File(Path.combine(CacheDir, ".temp"));
							cache.getParentFile().mkdirs();
							FileOutputStream out = new FileOutputStream(temp);
							try
							{
								bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
							}
							finally
							{
								if(needRecycle) {bmp.recycle();}
								out.close();
							}
							synchronized(cache_access_)
							{
								cache.delete();
								temp.renameTo(cache);
							}
						}
						catch(IOException e)
						{
							e.printStackTrace();
							ApplicationContext.getContext().addBugReport(e);
						}
					}
				}
			}).start();
	}
	
	public void clearCache(long now)
	{
		synchronized(temp_create_)
		{
			synchronized(cache_access_)
			{
				File[] caches = new File(CacheDir).listFiles();
				if(caches == null) {return;}
				
				for(File f : caches)
				{
					long expire = 0;
					
					// cache = CacheDir/getHashcode(Path, page)_width_height_expire
					String[] xs = f.getName().split("_");
					if(xs.length != 4) {continue;}
					try
					{
						expire = Long.valueOf(xs[3]);
					}
					catch(NumberFormatException e)
					{
						//e.printStackTrace();
						//ApplicationContext.getContext().addBugReport(e);
						continue;
					}
					
					if(expire > 0 && f.lastModified() + expire <= now)
					{
						f.delete();
					}
				}
			}
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
