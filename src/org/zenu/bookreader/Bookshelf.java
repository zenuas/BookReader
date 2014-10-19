package org.zenu.bookreader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
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
		
		createShelf(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Books");
	}

	public void createShelf(String shelf_name)
	{
		setTitle(shelf_name);
		List<Book> books = getBooks(shelf_name);
		if(books == null) {return;}
		
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
							item.icon.setImageDrawable(book.getCover());
							item.text.setText(book.getTitle());
						}
						finally
						{
							book.close();
						}
					}
					
					return(convertView);
				}
			});
		
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
	
	public List<Book> getBooks(String shelf_name)
	{
		File[] files = new File(shelf_name).listFiles();
		if(files == null) {return(null);}
		
		List<Book> xs = new ArrayList<Book>();
		for(File f : Path.sortWindowsFolder(Arrays.asList(files)))
		{
			if(f.isDirectory())
			{
				xs.add(new BookDirectory(f.getAbsolutePath()));
			}
			else
			{
				try
				{
					xs.add(((ApplicationContext) getApplicationContext()).getDB().getBook(f.getAbsolutePath()));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return(xs);
	}
}
