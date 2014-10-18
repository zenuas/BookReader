package org.zenu.bookreader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.graphics.drawable.Drawable;


public class BookZipArchive
	extends Book
{
	public static final String[] AcceptExtension = {".zip"};
	
	public BookZipArchive(String path)
	{
		super(path);
	}

	private ZipFile zip_ = null;
	private String[] files_ = null;
	public ZipFile getArchive() throws IOException
	{
		if(zip_ == null) {zip_ = new ZipFile(Path);}
		return(zip_);
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
			
			// WindowsXP以降のファイル名ソーと順を模倣
			Collections.sort(xs, new Comparator<String>()
				{
					@Override
					public int compare(String left, String right)
					{
						int left_index = 0;
						int right_index = 0;
						
						while(left_index < left.length() && right_index < right.length())
						{
							char left_char = left.charAt(left_index);
							char right_char = right.charAt(right_index);
							
							if(left_char == right_char)
							{
								left_index++;
								right_index++;
							}
							else if(
								left_char >= '0' && left_char <= '9' &&
								right_char >= '0' && right_char <= '9')
							{
								int left_num = left_char - '0';
								int right_num = right_char - '0';
								
								left_index++;
								right_index++;
								while(true)
								{
									left_char = left.charAt(left_index);
									
									if(left_char >= '0' && left_char <= '9')
									{
										left_num = left_num * 10 + left_char - '0';
										left_index++;
									}
									else
									{
										break;
									}
								}
								while(true)
								{
									right_char = right.charAt(right_index);
									
									if(right_char >= '0' && right_char <= '9')
									{
										right_num = right_num * 10 + right_char - '0';
										right_index++;
									}
									else
									{
										break;
									}
								}
								if(left_num != right_num) {return(left_num - right_num);}
							}
							else
							{
								return(left_char - right_char);
							}
						}
						
						if(left_index < left.length()) {return(-1);}
						if(right_index < right.length()) {return(1);}
						return(0);
					}
				});
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

	@Override
	public Drawable getCover()
	{
		if(this.Cover == null) 
		{
			try
			{
				ZipFile zip = getArchive();
				String[] files = getArchiveFiles();
				return(Drawable.createFromStream(zip.getInputStream(zip.getEntry(files[0])), files[0]));
				/*
				ZipFile zip = getArchive();
				for(Enumeration<? extends ZipEntry> xs = zip.entries(); xs.hasMoreElements();)
				{
					ZipEntry entry = xs.nextElement();
					if(!entry.isDirectory())
					{
						return(Drawable.createFromStream(zip.getInputStream(entry), entry.getName()));
					}
				}
				*/
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return(this.Cover);
	}
	
	@Override
	public Drawable currentPage() throws Exception
	{
		if(Page.equals("")) {moveFirstPage();}
		
		ZipFile zip = getArchive();
		return(Drawable.createFromStream(zip.getInputStream(zip.getEntry(Page)), Page));
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
	
	@Override
	public int getPageIndex()
	{
		try
		{
			String[] files = getArchiveFiles();
			for(int i = 0; i < files.length; i++)
			{
				if(files[i].equals(Page)) {return(i);}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return(-1);
	}
	
	@Override
	public int getMaxPage()
	{
		try
		{
			return(getArchiveFiles().length);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return(-1);
	}
}
