package org.zenu.bookreader;

import java.util.ArrayList;
import java.util.List;

public class Strings
{
	public static String join(String[] xs, String separator)
	{
		StringBuilder x = new StringBuilder();
		for(int i = 0; i < xs.length; i++)
		{
			x.append(xs[i]);
			if(i > 0) {x.append(separator);}
		}
		return(x.toString());
	}
	public static String join(List<String> xs, String separator)
	{
		StringBuilder x = new StringBuilder();
		for(int i = 0; i < xs.size(); i++)
		{
			x.append(xs.get(i));
			if(i > 0) {x.append(separator);}
		}
		return(x.toString());
	}
	
	public static List<String> filter(String[] xs, Func1<String, Boolean> f)
	{
		List<String> xxs = new ArrayList<String>();
		for(int i = 0; i < xs.length; i++)
		{
			if(f.invoke(xs[i])) {xxs.add(xs[i]);}
		}
		return(xxs);
	}
	
	public static List<String> filter(List<String> xs, Func1<String, Boolean> f)
	{
		List<String> xxs = new ArrayList<String>();
		for(int i = 0; i < xs.size(); i++)
		{
			String s = xs.get(i);
			if(f.invoke(s)) {xxs.add(s);}
		}
		return(xxs);
	}
	
	public static List<String> map(String[] xs, Func1<String, String> f)
	{
		List<String> xxs = new ArrayList<String>();
		for(int i = 0; i < xs.length; i++)
		{
			xxs.add(f.invoke(xs[i]));
		}
		return(xxs);
	}
	
	public static List<String> map(List<String> xs, Func1<String, String> f)
	{
		List<String> xxs = new ArrayList<String>();
		for(int i = 0; i < xs.size(); i++)
		{
			xxs.add(f.invoke(xs.get(i)));
		}
		return(xxs);
	}
	
	public static <T> T foldLeft(String[] xs, Func2<T, String, T> f, T r)
	{
		for(int i = 0; i < xs.length; i++)
		{
			r = f.invoke(r, xs[i]);
		}
		return(r);
	}
	
	public static <T> T foldLeft(List<String> xs, Func2<T, String, T> f, T r)
	{
		for(int i = 0; i < xs.size(); i++)
		{
			r = f.invoke(r, xs.get(i));
		}
		return(r);
	}
	
	public static <T> T foldRight(String[] xs, Func2<T, String, T> f, T r)
	{
		for(int i = xs.length - 1; xs.length > 0; i--)
		{
			r = f.invoke(r, xs[i]);
		}
		return(r);
	}
	
	public static <T> T foldRight(List<String> xs, Func2<T, String, T> f, T r)
	{
		for(int i = xs.size() - 1; i > 0; i--)
		{
			r = f.invoke(r, xs.get(i));
		}
		return(r);
	}
}
