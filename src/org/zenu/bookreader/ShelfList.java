package org.zenu.bookreader;

import java.io.File;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;


public class ShelfList
	extends ListActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelflist);
		setTitle(ShelfList.class.getSimpleName());
		
		setupShelves();
		getListView().setOnItemLongClickListener(new OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
				{
					BookShelvesManager.removeShelves(ShelfList.this, (String) parent.getItemAtPosition(position));
					setupShelves();
					return(true);
				}
			});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.shelflist, menu);
		return(super.onCreateOptionsMenu(menu));
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.add_shelf:
			new DirectorySelectDialog(this)
				{
					@Override
					public void onSelected(File d)
					{
						BookShelvesManager.addShelves(ShelfList.this, d.getAbsolutePath());
						setupShelves();
					}
				}.show();
			break;
		}
		return(super.onMenuItemSelected(featureId, item));
	}
	
	public void setupShelves()
	{
		final LayoutInflater inflater_ = getLayoutInflater();
		
		setListAdapter(new ArrayAdapter<String>(this, 0, BookShelvesManager.getShelves(this))
			{
				@Override
				public View getView(int position, View convertView, ViewGroup parent)
				{
					class ViewHolder
					{
						//public ImageView icon;
						public TextView text;
					}
					ViewHolder item;
					
					if(convertView == null)
					{
						convertView = inflater_.inflate(R.layout.shelf_item, parent, false);
						
						item = new ViewHolder();
						//item.icon = (ImageView) convertView.findViewById(R.id.icon);
						item.text = (TextView) convertView.findViewById(R.id.edit);
						
						convertView.setTag(item);
					}
					else
					{
						item = (ViewHolder) convertView.getTag();
					}
					
					if(position < getCount())
					{
						String path = getItem(position);
						//item.icon.setImageDrawable(null);
						item.text.setText(path);
					}
					
					return(convertView);
				}
			});
	}
}
