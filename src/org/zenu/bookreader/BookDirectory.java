package org.zenu.bookreader;

import java.io.File;

import android.graphics.drawable.Drawable;


public class BookDirectory
	extends Book
{
	public BookDirectory(File f)
	{
		super(f);
		
		this.Title = f.getName();
	}

	@Override
	public Drawable getCover()
	{
		return(null);
	}
}
