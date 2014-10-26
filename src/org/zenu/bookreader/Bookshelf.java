package org.zenu.bookreader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class Bookshelf
	extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookshelf);
		
		createShelf();
		List<String> shelves = BookShelvesManager.getShelves(this);
		if(shelves.size() > 0)
		{
			String shelf = shelves.get(0);
			setupShelf(shelf, shelf);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.bookshelf, menu);
		return(super.onCreateOptionsMenu(menu));
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.findItem(R.id.send_bugreport).setEnabled(((ApplicationContext) getApplicationContext()).existsBugReport());
		
		menu.removeGroup(R.id.shelves);
		
		List<String> shelves = BookShelvesManager.getShelves(this);
		for(int i = 0; i < shelves.size(); i++)
		{
			String s = shelves.get(i);
			MenuItem x = menu.add(R.id.shelves, i, 0, s);
			x.setChecked(current_shelf_.equals(s));
		}
		menu.setGroupCheckable(R.id.shelves, true, true);
		return(super.onPrepareOptionsMenu(menu));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.manage_shelf:
			startActivity(new Intent(this, ShelfList.class));
			break;
			
		case R.id.list_bookmark:
			chooseBookmark();
			break;
			
		case R.id.list_history:
			chooseHistory();
			break;
			
		case R.id.send_bugreport:
			((ApplicationContext) getApplicationContext()).sendBugReport();
			break;
			
		default:
			String shelf = (String) item.getTitle();
			setupShelf(shelf, shelf);
			break;
		}
		return(super.onOptionsItemSelected(item));
	}
	
	@Override
	public void onBackPressed()
	{
		String parent = Path.getDirectoryName(current_shelf_indir_);
		parent = (parent.length() > 0 && parent.charAt(parent.length() - 1) == File.separatorChar ? parent.substring(0, parent.length() - 1) : parent);
		if(parent.length() > 0 && parent.startsWith(current_shelf_))
		{
			setupShelf(current_shelf_, parent);
		}
		else
		{
			super.onBackPressed();
		}
	}
	
	public void createShelf()
	{
		final GridView grid = (GridView) findViewById(R.id.shelf);
		
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					Book book = (Book) parent.getItemAtPosition(position);
					
					if(book instanceof BookDirectory)
					{
						setupShelf(current_shelf_, Path.combine(current_shelf_indir_, book.Title));
					}
					else
					{
						openBook(book.Path);
					}
				}
			});
	}
	
	public void openBook(String path)
	{
		Intent intent = new Intent();
		intent.setClass(Bookshelf.this, BookViewer.class);
		intent.setData(Uri.parse(path));
		
		Bookshelf.this.startActivity(intent);
	}
	
	public void openBook(String path, int page_index)
	{
		Intent intent = new Intent();
		intent.setClass(Bookshelf.this, BookViewer.class);
		intent.setData(Uri.parse(path));
		intent.putExtra(BookViewer.class.getName() + ".page", page_index);
		
		Bookshelf.this.startActivity(intent);
	}
	
	private String current_shelf_ = ""; 
	private String current_shelf_indir_ = ""; 
	public void setupShelf(String shelf_name, String indir)
	{
		if(!indir.startsWith(shelf_name)) {return;}
		
		setTitle(indir);
		List<Book> books = getBooks(indir, true);
		if(books == null) {return;}
		current_shelf_ = shelf_name;
		current_shelf_indir_ = indir;
		
		final GridView grid = (GridView) findViewById(R.id.shelf);
		final LayoutInflater inflater_ = getLayoutInflater();
		
		grid.setAdapter(new ArrayAdapter<Book>(this, 0, books)
			{
				@Override
				public View getView(int position, View convertView, ViewGroup parent)
				{
					class ViewHolder
					{
						public ImageView icon;
						public TextView text;
					}
					final ViewHolder item;
					
					if(convertView == null)
					{
						convertView = inflater_.inflate(R.layout.item, parent, false);
						
						item = new ViewHolder();
						item.icon = (ImageView) convertView.findViewById(R.id.icon);
						item.text = (TextView) convertView.findViewById(R.id.edit);
						
						convertView.setTag(item);
					}
					else
					{
						item = (ViewHolder) convertView.getTag();
					}
					
					if(position < getCount())
					{
						Book book = getItem(position);
						if(book instanceof BookDirectory)
						{
							item.icon.setImageResource(android.R.drawable.ic_menu_gallery);
						}
						else
						{
							if(book.Cover != null)
							{
								item.icon.setImageDrawable(book.Cover);
							}
							else
							{
								item.icon.setImageResource(android.R.drawable.ic_menu_recent_history);
								new AsyncTask<Book, Integer, Drawable>()
								{
									@Override
									protected Drawable doInBackground(Book... params)
									{
										try
										{
											return(params[0].getCover(item.icon.getWidth(), item.icon.getHeight()));
										}
										finally
										{
											params[0].close();
										}
									}
									
									@Override
									protected void onPostExecute(Drawable result)
									{
										item.icon.setImageDrawable(result);
									}
								}.execute(book);
							}
						}
						item.text.setText(book.getTitle());
					}
					
					return(convertView);
				}
			});
	}
	
	public List<Book> getBooks(String shelf_name, boolean include_dir)
	{
		return(getBooks((ApplicationContext) getApplicationContext(), shelf_name, include_dir));
	}
	
	public static List<Book> getBooks(ApplicationContext context, String shelf_name, boolean include_dir)
	{
		File[] files = new File(shelf_name).listFiles();
		if(files == null) {return(null);}
		
		List<Book> xs = new ArrayList<Book>();
		for(File f : Path.sortWindowsFolder(Arrays.asList(files)))
		{
			try
			{
				if(f.isDirectory())
				{
					if(include_dir) {xs.add(new BookDirectory(f.getCanonicalPath()));}
				}
				else
				{
					Book book = context.getDB().getBook(f);
					if(book != null) {xs.add(book);}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ApplicationContext.getContext().addBugReport(e);
			}
		}
		
		return(xs);
	}
	
	public void chooseHistory()
	{
		Builder list = new Builder(this);
		list.setTitle(R.string.open_from_history);
		
		// setItems $ map (x -> getFileName x) $ getHistory
		final BookCacheDB db = ((ApplicationContext) getApplicationContext()).getDB();
		final List<String> files = HistoryManager.getHistory(this);
		list.setItems(Lists.map(files, new Func1<String, String>()
			{
				@Override
				public String invoke(String a1)
				{
					try
					{
						Book book = db.getBook(a1);
						return(Path.getFileName(a1) + " [ " + String.valueOf(book.getPageIndex() + 1) + " / " + String.valueOf(book.getMaxPage() + " ]"));
					}
					catch(Exception e)
					{
						e.printStackTrace();
						ApplicationContext.getContext().addBugReport(e);
					}
					return(Path.getFileName(a1));
				}
			}).toArray(new String[0]), new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					openBook(files.get(which));
				}
			});
		list.show();
	}
	
	public void chooseBookmark()
	{
		Builder list = new Builder(this);
		list.setTitle(R.string.open_from_bookmark);
		
		// setItems $ map (x -> getFileName x.Path) $ getBookmarks
		final BookCacheDB db = ((ApplicationContext) getApplicationContext()).getDB();
		final List<Bookmark> bookmarks = db.getBookmarks();
		list.setItems(Lists.map(bookmarks, new Func1<Bookmark, String>()
			{
				@Override
				public String invoke(Bookmark a1)
				{
					try
					{
						Book book = db.getBook(a1.Path);
						return(Path.getFileName(a1.Path) + " [ " + String.valueOf(a1.PageIndex + 1) + " / " + String.valueOf(book.getMaxPage() + " ]"));
					}
					catch(Exception e)
					{
						e.printStackTrace();
						ApplicationContext.getContext().addBugReport(e);
					}
					return(Path.getFileName(a1.Path) + " [ " + String.valueOf(a1.PageIndex + 1) + " ]");
				}
			}).toArray(new String[0]), new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Bookmark bookmark = bookmarks.get(which);
					openBook(bookmark.Path, bookmark.PageIndex);
				}
			});
		list.show();
	}
}
