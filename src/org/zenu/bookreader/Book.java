package org.zenu.bookreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.graphics.drawable.Drawable;

public class Book
{
	public String Title;
	public String Path;
	public Drawable Cover = null;
	
	public Book(File f)
	{
		this.Title = f.getName();
		this.Path = f.getAbsolutePath();
		
		int ext = this.Title.lastIndexOf('.');
		if(ext >= 0)
		{
			this.Title = this.Title.substring(0, ext);
		}
	}
	
	public CharSequence getTitle()
	{
		return(this.Title);
	}
	
	public Drawable getCover()
	{
		if(this.Cover == null) 
		{
			try
			{
				ZipInputStream zip = new ZipInputStream(new FileInputStream(this.Path));
				try
				{
					while(true)
					{
						ZipEntry entry = zip.getNextEntry();
						if(entry == null) {break;}
						if(!entry.isDirectory())
						{
							return(Drawable.createFromStream(zip, entry.getName()));
						}
					}
				}
				finally
				{
					zip.close();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				//this.Cover = new Icon();
			}
		}
		return(this.Cover);
		
	}
}
