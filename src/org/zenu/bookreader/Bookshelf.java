package org.zenu.bookreader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		final GridView grid = (GridView) findViewById(R.id.shelf);
		
		final LayoutInflater inflater_ = getLayoutInflater();
		
		grid.setAdapter(new ArrayAdapter<Book>(this, 0, getBooks(shelf_name))
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
						item.icon.setImageDrawable(book.getCover());
						item.text.setText(book.getTitle());
					}
					
					return(convertView);
				}
			});
	}
	
	public List<Book> getBooks(String shelf_name)
	{
		List<Book> xs = new ArrayList<Book>();
		
		File dir = new File(shelf_name);
		for(File f : dir.listFiles())
		{
			if(f.isDirectory())
			{
				xs.add(new BookDirectory(f));
			}
			else
			{
				xs.add(new Book(f));
			}
		}
		
		return(xs);
	}
}
