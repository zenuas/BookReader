package org.zenu.bookreader;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public abstract class Book
{
	public String Title;
	public String Path;
	public Drawable Cover = null;
	public String Page = "";
	private Direction direct_ = Direction.RightToLeft;
	
	public Book(String path)
	{
		Title = org.zenu.bookreader.Path.getFileNameWithoutExtension(path);
		Path = path;
	}
	
	public CharSequence getTitle()
	{
		return(this.Title);
	}
	
	public byte[] serializeCover()
	{
		Drawable d = getCover();
		close();
		if(d == null || !(d instanceof BitmapDrawable)) {return(null);}
		
		Bitmap bmp = ((BitmapDrawable)d).getBitmap();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, out);
		return(out.toByteArray());
	}
	
	@SuppressWarnings("deprecation")
	public void deserializeCover(byte[] data)
	{
		if(data == null)
		{
			Cover = null;
			return;
		}
		Cover = new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
	}
	
	public void deserializeCover(Context context, byte[] data)
	{
		if(data == null)
		{
			Cover = null;
			return;
		}
		Cover = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(data, 0, data.length));
	}
	
	public Direction getDirection()
	{
		return(direct_);
	}
	
	public void setDirection(Direction direct)
	{
		direct_ = direct;
	}
	
	public abstract Drawable getCover();
	public abstract Drawable currentPage() throws Exception;
	public abstract void movePage(String page) throws Exception;
	public abstract boolean moveNextPage() throws Exception;
	public abstract boolean movePrevPage() throws Exception;
	public abstract void moveFirstPage() throws Exception;
	public abstract void moveLastPage() throws Exception;
	public abstract int getPageIndex();
	public abstract void setPageIndex(int page);
	public abstract int getMaxPage();
	public abstract void setMaxPage(int page);
	public abstract long getLastModified();
	public abstract void setLastModified(long d);
	
	public void close()
	{
	}
}
