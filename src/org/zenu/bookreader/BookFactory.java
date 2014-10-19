package org.zenu.bookreader;

import android.annotation.SuppressLint;


public class BookFactory
{
	@SuppressLint("DefaultLocale")
	public static Book open(String path) throws Exception
	{
		String ext = Path.getExtension(path).toLowerCase();
		
		if(contains(BookZipArchive.AcceptExtension, ext)) {return(new BookZipArchive(path));}
		
		throw new Exception();
	}
	
	public static boolean contains(String[] xs, String s)
	{
		for(String x : xs)
		{
			if(x.equals(s)) {return(true);}
		}
		return(false);
	}
}
