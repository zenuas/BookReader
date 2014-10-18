package org.zenu.bookreader;

import android.graphics.drawable.Drawable;


public class BookDirectory
	extends Book
{
	public BookDirectory(String path)
	{
		super(path);
		
		Title = org.zenu.bookreader.Path.getFileName(path);
		Path = path;
	}
	
	@Override
	public Drawable getCover()
	{
		return(null);
	}
	
	@Override
	public Drawable currentPage() throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void movePage(String page) throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean moveNextPage() throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean movePrevPage() throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void moveFirstPage() throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void moveLastPage() throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void close()
	{
	}
	
	@Override
	public int getPageIndex()
	{
		return(-1);
	}
	
	@Override
	public int getMaxPage()
	{
		return(-1);
	}
}
