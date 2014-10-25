package org.zenu.bookreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class BookZipArchive
	extends Book
{
	public static final String[] AcceptExtension = {".zip"};
	
	public BookZipArchive(String path)
	{
		super(path);
		setLastModified(new File(path).lastModified());
	}
	
	private ZipFile zip_ = null;
	private String[] files_ = null;
	public ZipFile getArchive() throws IOException
	{
		if(zip_ == null) {zip_ = new ZipFile(Path);}
		return(zip_);
	}
	
	public boolean fileLoaded()
	{
		return(files_ != null);
	}
	
	public String[] getArchiveFiles() throws IOException
	{
		if(files_ == null)
		{
			List<String> xs = new ArrayList<String>();
			ZipFile zip = getArchive();
			for(Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();)
			{
				ZipEntry entry = e.nextElement();
				if(!entry.isDirectory())
				{
					xs.add(entry.getName());
				}
			}
			org.zenu.bookreader.Path.sortWindowsFileName(xs);
			files_ = xs.toArray(new String[0]);
		}
		return(files_);
	}
	
	@Override
	public void close()
	{
		if(zip_ != null)
		{
			try
			{
				zip_.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			zip_ = null;
			files_ = null;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Drawable getCover(int width, int height)
	{
		if(this.Cover == null) 
		{
			try
			{
				ZipFile zip = getArchive();
				String cover = getArchiveFiles()[0];
				
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(zip.getInputStream(zip.getEntry(cover)), null, opt);
				
				opt.inJustDecodeBounds = false;
				opt.inSampleSize = Math.max(1, Math.min(opt.outWidth / width, opt.outHeight / height));
				opt.inPurgeable = true;
				Bitmap bmp = BitmapFactory.decodeStream(zip.getInputStream(zip.getEntry(cover)), null, opt);
				
				Cover = new BitmapDrawable(bmp);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				close();
			}
		}
		return(Cover);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Drawable currentPage(int width, int height) throws Exception
	{
		if(Page.equals(""))
		{
			if(page_index_ >= 0)
			{
				Page = getArchiveFiles()[page_index_];
			}
			else
			{
				moveFirstPage();
			}
		}
		
		ZipFile zip = getArchive();

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(zip.getInputStream(zip.getEntry(Page)), null, opt);
		
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = Math.max(1, Math.min(opt.outWidth / width, opt.outHeight / height));
		opt.inPurgeable = true;
		Bitmap bmp = BitmapFactory.decodeStream(zip.getInputStream(zip.getEntry(Page)), null, opt);
		
		return(new BitmapDrawable(bmp));
	}

	@Override
	public void movePage(String page) throws Exception
	{
		Page = page;
	}
	
	@Override
	public boolean moveNextPage() throws Exception
	{
		if(!Page.equals(""))
		{
			String[] files = getArchiveFiles();
			int current = getPageIndex();
			if(current + 1 >= files.length)
			{
				//　Pageが最終ページなら移動せずfalseを返す
				return(false);
			}
			else
			{
				//　該当のPageの次のページへ移動しtrueを返す
				//　Pageの該当が無ければ最初のページへ移動しtrueを返す
				Page = files[current + 1];
				return(true);
			}
		}
		
		// Pageが未指定なら最初のページへ移動しtrueを返す
		moveFirstPage();
		return(true);
	}
	
	@Override
	public boolean movePrevPage() throws Exception
	{
		if(!Page.equals(""))
		{
			String[] files = getArchiveFiles();
			int current = getPageIndex();
			if(current == 0)
			{
				//　Pageが1ページ目なら移動せずfalseを返す
				return(false);
			}
			else if(current > 0)
			{
				// Pageの前のページに移動しtrueを返す
				Page = files[current - 1];
				return(true);
			}
		}
		
		// Pageが未指定なら最初のページへ移動しtrueを返す
		//　Pageの該当が無ければ最初のページへ移動しtrueを返す
		moveFirstPage();
		return(true);
	}
	
	@Override
	public void moveFirstPage() throws Exception
	{
		String[] files = getArchiveFiles();
		Page = files[0];
	}
	
	@Override
	public void moveLastPage() throws Exception
	{
		String[] files = getArchiveFiles();
		Page = files[files.length - 1];
	}

	private int page_index_ = -1;
	private int max_page_ = -1;
	private long last_modified_ = 0;
	
	@Override
	public int getPageIndex()
	{
		if(!fileLoaded()) {return(page_index_);}
		try
		{
			String[] files = getArchiveFiles();
			for(int i = 0; i < files.length; i++)
			{
				if(files[i].equals(Page))
				{
					page_index_ = i;
					return(i);
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return(-1);
	}
	
	@Override
	public void setPageIndex(int page)
	{
		page_index_ = page;
		if(!fileLoaded()) {Page = ""; return;}
		try
		{
			movePage(getArchiveFiles()[page]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public int getMaxPage()
	{
		if(!fileLoaded()) {return(max_page_);}
		try
		{
			max_page_ = getArchiveFiles().length;
			return(max_page_);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return(-1);
	}
	
	@Override
	public void setMaxPage(int page)
	{
		max_page_ = page;
	}
	
	@Override
	public long getLastModified()
	{
		return(last_modified_);
	}
	
	@Override
	public void setLastModified(long d)
	{
		last_modified_ = d;
	}
}
