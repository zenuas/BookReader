package org.zenu.bookreader;

import java.util.List;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;


public class BookViewer
	extends Activity
{
	private Book book_ = null;
	private ImageView image_ = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookviewer);
		
		String path = getIntent().getStringExtra(BookViewer.class.getName() + ".path");
		createViewer();
		if(!setupViewer(path))
		{
			Toast.makeText(this, R.string.bookviewer_fileopenerror, Toast.LENGTH_LONG).show();
			this.finish();
		}
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

	private ScaleGestureDetector scale_dector_;
	private GestureDetector fling_dector_;
	
	public void createViewer()
	{
		image_ = (ImageView) findViewById(R.id.viewer);
		image_.setScaleType(ScaleType.MATRIX);
		
		image_.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
			{
				@Override
				public void onGlobalLayout()
				{
					setAutoRotateAndFitScaleAndCenter();
				}
			});
		
		scale_dector_ = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener()
			{
				@Override
				public boolean onScale(ScaleGestureDetector detector)
				{
					// getScaleFactorの1/8倍でスケールを変更、なんとなく操作しやすかった倍率で根拠は無い
					float scale = detector.getScaleFactor();
					scale = (scale - 1.0f) / 8.0f + 1.0f;
					
					Matrix x = new Matrix(image_.getImageMatrix());
					x.postTranslate(-detector.getFocusX(), -detector.getFocusY());
					x.postScale(scale, scale);
					x.postTranslate(detector.getFocusX(), detector.getFocusY());
					image_.setImageMatrix(x);
					image_.invalidate();
					return(super.onScale(detector));
				}
			});
		
		fling_dector_ = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
			{
				@Override
				public boolean onSingleTapUp(MotionEvent event)
				{
					// とりあえず右綴じ想定でビューの左2/3をタッチでページ送り、右1/3をタッチでページ戻り
					if(event.getX() - image_.getLeft() < image_.getWidth() * 2 / 3)
					{
						moveNextPage();
					}
					else
					{
						movePrevPage();
					}
					return(super.onSingleTapUp(event));
				}
				
				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
				{
					Matrix x = new Matrix(image_.getImageMatrix());
					x.postTranslate(-distanceX, -distanceY);
					image_.setImageMatrix(x);
					image_.invalidate();
					return(super.onScroll(e1, e2, distanceX, distanceY));
				}
			});
	}
	
	public boolean setupViewer(String path)
	{
		Book book;
		try
		{
			book = ((ApplicationContext) getApplicationContext()).getDB().getBook(path);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return(false);
		}
		return(setupViewer(book));
	}
	
	public boolean setupViewer(Book book)
	{
		Drawable p;
		try
		{
			p = book.currentPage();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return(false);
		}
		image_.setImageDrawable(p);
		if(book_ != null) {book_.close();}
		book_ = book;
		return(true);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean scaled = scale_dector_.isInProgress();
		scale_dector_.onTouchEvent(event);
		if(scaled || scale_dector_.isInProgress())
		{
			return(true);
		}
		return(fling_dector_.onTouchEvent(event));
	}
	
	@Override
	public void onBackPressed()
	{
		//super.onBackPressed();
		moveNextPage();
	}
	
	public void setAutoRotateAndFitScaleAndCenter()
	{
		Rect rc = image_.getDrawable().getBounds();
		
		float view_height = image_.getHeight();
		float view_width = image_.getWidth();
		float image_height = rc.height();
		float image_width = rc.width();
		
		Matrix x = new Matrix();
		
		// ビューが縦表示で、画像の横幅が縦幅の1.5倍を超えていたら90度回転
		float scale = 1.0f;
		x.postTranslate(-image_width / 2, -image_height / 2);
		if(view_height > view_width && image_width > image_height * 1.5)
		{
			x.postRotate(90);
			scale = view_height / image_width;
		}
		else
		{
			float height_scale = view_height / image_height;
			float width_scale = view_width / image_width;
			
			scale = Math.min(height_scale, width_scale);
		}
		
		x.postScale(scale, scale);
		x.postTranslate(view_width / 2, view_height / 2);
		image_.setImageMatrix(x);
		image_.invalidate();
	}
	
	public int indexOfBook(List<Book> xs, Book x)
	{
		for(int i = 0; i < xs.size(); i++)
		{
			if(xs.get(i).Path.equals(x.Path)) {return(i);}
		}
		return(-1);
	}
	
	public void moveNextPage()
	{
		try
		{
			if(!book_.moveNextPage())
			{
				// 最後のページであれば次の本の1ページ目に進む
				List<Book> books = Bookshelf.getBooks((ApplicationContext) getApplicationContext(), Path.getDirectoryName(book_.Path), false);
				int current = indexOfBook(books, book_);
				if(current + 1 >= books.size())
				{
					Toast.makeText(this, R.string.nextbook_not_found, Toast.LENGTH_SHORT).show();
					return;
				}
				
				Book next = books.get(current + 1);
				next.Page = "";
				
				setupViewer(next);
				Toast.makeText(this, book_.getTitle(), Toast.LENGTH_SHORT).show();
			}
			image_.setImageDrawable(book_.currentPage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void movePrevPage()
	{
		try
		{
			if(!book_.movePrevPage())
			{
				// 最初のページであれば前の本に戻る
				List<Book> books = Bookshelf.getBooks((ApplicationContext) getApplicationContext(), Path.getDirectoryName(book_.Path), false);
				int current = indexOfBook(books, book_);
				if(current <= 0)
				{
					Toast.makeText(this, R.string.prevbook_not_found, Toast.LENGTH_SHORT).show();
					return;
				}
				
				setupViewer(books.get(current - 1));
				Toast.makeText(this, book_.getTitle(), Toast.LENGTH_SHORT).show();
			}
			image_.setImageDrawable(book_.currentPage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
