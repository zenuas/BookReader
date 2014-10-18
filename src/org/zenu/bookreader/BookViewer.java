package org.zenu.bookreader;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Toast;


public class BookViewer
	extends Activity
{
	private Book book_ = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookviewer);
		
		String path = getIntent().getStringExtra(BookViewer.class.getName() + ".path");
		try
		{
			book_ = ((ApplicationContext) getApplicationContext()).getDB().getBook(path);
		}
		catch(Exception e)
		{
			Toast.makeText(this, R.string.bookviewer_fileopenerror, Toast.LENGTH_LONG).show();
			this.finish();
		}
		createViewer();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if(book_ != null)
		{
			book_.close();
			book_ = null;
		}
	}
	
	public void createViewer()
	{
		final MatrixImageView image = (MatrixImageView) findViewById(R.id.viewer);
		
		Drawable p = null;
		try
		{
			p = book_.currentPage();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		image.setImageDrawable(p);
		image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
			{
				@Override
				public void onGlobalLayout()
				{
					image.setAutoRotateAndFitScaleAndCenter();
				}
			});
		
		image.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if(event.getAction() == MotionEvent.ACTION_UP)
					{
						try
						{
							book_.moveNextPage();
							image.setImageDrawable(book_.currentPage());
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						image.setAutoRotateAndFitScaleAndCenter();
					}
					return(true);
				}
			});
	}
}
