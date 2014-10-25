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
	public Drawable getCover(int width, int height)
	{
		return(ApplicationContext.getContext().getResources().getDrawable(android.R.drawable.ic_menu_gallery));
	}
	
	@Override
	public Drawable currentPage(int width, int height) throws Exception
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
	public void setPageIndex(int page)
	{
	}
	
	@Override
	public int getMaxPage()
	{
		return(-1);
	}
	
	@Override
	public void setMaxPage(int page)
	{
	}
	
	@Override
	public long getLastModified()
	{
		return(0);
	}
	
	@Override
	public void setLastModified(long d)
	{
	}
}
