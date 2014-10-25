package org.zenu.bookreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BookShelvesManager
{
	public static List<String> getShelves(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(BookShelvesManager.class.getName(), Context.MODE_PRIVATE);
		String shelves = pref.getString("Shelves", "");
		return(shelves.length() == 0 ? new ArrayList<String>() : Lists.toList(shelves.split("\n")));
	}
	
	public static void addShelves(Context context, String path)
	{
		List<String> shelves = getShelves(context);
		shelves.add(path);
		
		SharedPreferences pref = context.getSharedPreferences(BookShelvesManager.class.getName(), Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putString("Shelves", Strings.join(shelves, "\n"));
		edit.commit();
	}
	
	public static void removeShelves(Context context, String path)
	{
		List<String> shelves = getShelves(context);
		shelves.remove(path);
		
		SharedPreferences pref = context.getSharedPreferences(BookShelvesManager.class.getName(), Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putString("Shelves", Strings.join(shelves, "\n"));
		edit.commit();
	}
}
