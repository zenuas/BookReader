package org.zenu.bookreader;

import java.io.InputStream;

import android.graphics.drawable.Drawable;

public abstract class Book
{
	public String Title;
	public String Path;
	public Drawable Cover = null;
	public String Page = "";
	public String CoverPage = "";
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
	
	public Direction getDirection()
	{
		return(direct_);
	}
	
	public void setDirection(Direction direct)
	{
		direct_ = direct;
	}
	
	public abstract Drawable getCover(int width, int height);
	public abstract Drawable currentPage(int width, int height) throws Exception;
	public abstract InputStream getStream(String page) throws Exception;
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
