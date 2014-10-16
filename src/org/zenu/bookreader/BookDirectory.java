package org.zenu.bookreader;

import java.io.File;

import android.graphics.drawable.Drawable;


public class BookDirectory
	extends Book
{
	public BookDirectory(File f)
	{
		super(f);
	}

	@Override
	public Drawable getCover()
	{
		return(null);
	}
}
