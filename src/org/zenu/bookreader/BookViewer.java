package org.zenu.bookreader;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.bookviewer);
		ActionBar x = getActionBar();
		if(x != null)
		{
			x.setDisplayHomeAsUpEnabled(true);
			x.hide();
		}
		
		final String path = getIntent().getStringExtra(BookViewer.class.getName() + ".path");
		createViewer();
		
		if(!setupViewer(path))
		{
			Toast.makeText(BookViewer.this, R.string.bookviewer_fileopenerror, Toast.LENGTH_LONG).show();
			BookViewer.this.finish();
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
	
	private Toast toast_ = null;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.bookviewer, menu);
		
		SeekBar seek = (SeekBar) menu.findItem(R.id.page_seek).getActionView().findViewById(R.id.seek);
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
			{
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					if(!fromUser) {return;}
					try
					{
						if(toast_ != null) {toast_.cancel();}
						toast_ = Toast.makeText(BookViewer.this, String.valueOf(progress + 1) + " / " + String.valueOf(seekBar.getMax()), Toast.LENGTH_SHORT);
						toast_.show();
						
						book_.setPageIndex(progress);
						updateCanvas(book_);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar)
				{
				}
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar)
				{
				}
			});
		
		return(super.onCreateOptionsMenu(menu));
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem ltor = (MenuItem) menu.findItem(R.id.left_to_right);
		MenuItem rtol = (MenuItem) menu.findItem(R.id.right_to_left);
		if(book_ != null && book_.getDirection() == Direction.LeftToRight)
		{
			ltor.setVisible(false);
			rtol.setVisible(true);
		}
		else
		{
			ltor.setVisible(true);
			rtol.setVisible(false);
		}
		return(super.onPrepareOptionsMenu(menu));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case android.R.id.home:
			finish();
			break;
			
		case R.id.back_to_shelf:
			Intent intent = new Intent(this, Bookshelf.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
			
		case R.id.bookmarks:
			break;
			
		case R.id.add_bookmark:
			break;
			
		case R.id.remove_bookmark:
			break;

		case R.id.left_to_right:
			book_.setDirection(Direction.LeftToRight);
			saveBook();
			setActionBarVisible(true);
			break;
			
		case R.id.right_to_left:
			book_.setDirection(Direction.RightToLeft);
			saveBook();
			setActionBarVisible(true);
			break;
		}
		return(super.onOptionsItemSelected(item));
	}
	
	private ScaleGestureDetector scale_dector_;
	private GestureDetector fling_dector_;
	
	public void createViewer()
	{
		image_ = (ImageView) findViewById(R.id.viewer);
		image_.setScaleType(ScaleType.MATRIX);
		
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
				public boolean onSingleTapConfirmed(MotionEvent e)
				{
					if(book_ != null)
					{
						if(book_.getDirection() == Direction.LeftToRight)
						{
							// 左綴の場合、ビューの左1/3をタッチでページ戻り、右2/3をタッチでページ送り
							int center = image_.getWidth() * 1 / 3;
							if(e.getX() - image_.getLeft() < center)
							{
								movePrevPage();
							}
							else
							{
								moveNextPage();
							}
						}
						else
						{
							// 右綴の場合、ビューの左2/3をタッチでページ送り、右1/3をタッチでページ戻り
							int center = image_.getWidth() * 2 / 3;
							if(e.getX() - image_.getLeft() < center)
							{
								moveNextPage();
							}
							else
							{
								movePrevPage();
							}
						}
					}
					return(super.onSingleTapConfirmed(e));
				}
				
				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
				{
					if(book_ != null)
					{
						Matrix x = new Matrix(image_.getImageMatrix());
						x.postTranslate(-distanceX, -distanceY);
						image_.setImageMatrix(x);
						image_.invalidate();
					}
					return(super.onScroll(e1, e2, distanceX, distanceY));
				}
				
				@Override
				public boolean onDoubleTap(MotionEvent e)
				{
					if(book_ != null)
					{
						setActionBarVisibleToggle();
					}
					return(super.onDoubleTap(e));
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
		if(book_ != null) {book_.close();}
		book_ = book;
		try
		{
			updateCanvas(book_);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return(false);
		}
		setTitle(book_.getTitle());
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
		if(book_ != null)
		{
			moveNextPage();
		}
		else
		{
			super.onBackPressed();
		}
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
		setActionBarVisible(false);
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
				next.setPageIndex(0);
				
				setupViewer(next);
				Toast.makeText(this, book_.getTitle(), Toast.LENGTH_SHORT).show();
			}
			updateCanvas(book_);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void movePrevPage()
	{
		setActionBarVisible(false);
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
			updateCanvas(book_);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateCanvas(Book book) throws Exception
	{
		new AsyncTask<Book, Integer, Drawable>()
			{
				private int size_;
				private ProgressDialog wait_;
				
				@Override
				protected void onPreExecute()
				{
					size_ = getCanvasSize();
					
					wait_ = new ProgressDialog(BookViewer.this);
					wait_.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					wait_.show();
				}
				
				@Override
				protected Drawable doInBackground(Book... params)
				{
					try
					{
						return(params[0].currentPage(size_, size_));
					}
					catch(Exception e)
					{
						cancel(false);
						e.printStackTrace();
					}
					return(null);
				}
				
				@Override
				protected void onCancelled()
				{
					wait_.dismiss();
				}
				
				@Override
				protected void onPostExecute(Drawable result)
				{
					image_.setImageDrawable(result);
					wait_.dismiss();
					setAutoRotateAndFitScaleAndCenter();
					saveBook();
				}
			}.execute(book);
	}
	
	@SuppressWarnings("deprecation")
	public int getCanvasSize()
	{
		// ビューの高さ・幅の大きいほうの1.5倍をキャンパスサイズとする
		// 画像ロード後に回転、拡大することを考慮し少し大きめに設定している
		// 取得失敗時(初期表示中)はディスプレイサイズを代わりに使う
		int size = Math.max(image_.getWidth(), image_.getHeight());
		if(size <= 0)
		{
			Display display = getWindowManager().getDefaultDisplay();
			size = Math.max(display.getWidth(), display.getHeight());
		}
		return(size * 3 / 2);
	}
	
	public ActionBar setActionBarVisible(boolean visible)
	{
		ActionBar x = getActionBar();
		if(x != null)
		{
			if(visible)
			{
				x.show();
				SeekBar seek = (SeekBar) findViewById(R.id.seek);
				if(book_.getDirection() == Direction.LeftToRight)
				{
					seek.setRotation(0);
				}
				else
				{
					seek.setRotation(180);
				}
				seek.setMax(book_.getMaxPage());
				seek.setProgress(book_.getPageIndex());
			}
			else
			{
				x.hide();
			}
		}
		return(x);
	}
	
	public ActionBar setActionBarVisibleToggle()
	{
		ActionBar x = getActionBar();
		if(x != null)
		{
			setActionBarVisible(!x.isShowing());
		}
		return(x);
	}
	
	public void saveBook()
	{
		((ApplicationContext) getApplicationContext()).getDB().saveBook(book_);
	}
}
