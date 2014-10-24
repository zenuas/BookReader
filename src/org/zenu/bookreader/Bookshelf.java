package org.zenu.bookreader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
		
		ApplicationContext app = (ApplicationContext) getApplicationContext();
		app.sendBugReport();

		createShelf();
		List<String> shelves = BookShelvesManager.getShelves(this);
		if(shelves.size() > 0)
		{
			setupShelf(shelves.get(0));
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
		menu.removeGroup(R.id.shelves);
		
		List<String> shelves = BookShelvesManager.getShelves(this);
		for(int i = 0; i < shelves.size(); i++)
		{
			String s = shelves.get(i);
			MenuItem x = menu.add(R.id.shelves, i, 0, s);
			x.setChecked((i == 0 && current_shelf_.length() == 0) || (current_shelf_.equals(s)));
		}
		menu.setGroupCheckable(R.id.shelves, true, true);
		return(super.onPrepareOptionsMenu(menu));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.add_shelf:
			startActivity(new Intent(this, ShelfList.class));
			break;
		case R.id.list_bookmark:
			break;
		case R.id.list_history:
			break;
			
		default:
			setupShelf((String) item.getTitle());
			break;
		}
		return(super.onOptionsItemSelected(item));
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
					
					Intent intent = new Intent();
					intent.setClass(Bookshelf.this, BookViewer.class);
					intent.putExtra(BookViewer.class.getName() + ".path", book.Path);
					
					Bookshelf.this.startActivity(intent);
				}
			});
	}
	
	private String current_shelf_ = ""; 
	public void setupShelf(String shelf_name)
	{
		setTitle(shelf_name);
		List<Book> books = getBooks(shelf_name, true);
		if(books == null) {return;}
		current_shelf_ = shelf_name;
		
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
					ViewHolder item;
					
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
						try
						{
							boolean loaded = (book.Cover != null);
							item.icon.setImageDrawable(book.getCover());
							item.text.setText(book.getTitle());
							if(!loaded) {((ApplicationContext) getApplicationContext()).getDB().saveBook(book);}
						}
						finally
						{
							book.close();
						}
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
					xs.add(context.getDB().getBook(f));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return(xs);
	}
}
