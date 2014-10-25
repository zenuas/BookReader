package org.zenu.bookreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class HistoryManager
{
	public final static int HistorySize = 30;
	
	public static List<String> getHistory(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(HistoryManager.class.getName(), Context.MODE_PRIVATE);
		String history = pref.getString("History", "");
		return(history.length() == 0 ? new ArrayList<String>() : Lists.toList(history.split("\n")));
	}
	
	public static void addHistory(Context context, String path)
	{
		List<String> history = getHistory(context);
		history.remove(path);
		history.add(0, path);
		
		SharedPreferences pref = context.getSharedPreferences(HistoryManager.class.getName(), Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putString("History", Strings.join(Lists.top(history, HistorySize), "\n"));
		edit.commit();
	}
}
