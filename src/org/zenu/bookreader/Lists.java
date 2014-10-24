package org.zenu.bookreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lists
{
	public static <T> List<T> filter(T[] xs, Func1<T, Boolean> f)
	{
		List<T> xxs = new ArrayList<T>();
		for(int i = 0; i < xs.length; i++)
		{
			if(f.invoke(xs[i])) {xxs.add(xs[i]);}
		}
		return(xxs);
	}
	
	public static <T> List<T> filter(List<T> xs, Func1<T, Boolean> f)
	{
		List<T> xxs = new ArrayList<T>();
		for(int i = 0; i < xs.size(); i++)
		{
			T s = xs.get(i);
			if(f.invoke(s)) {xxs.add(s);}
		}
		return(xxs);
	}
	
	public static <T, TR> List<TR> map(T[] xs, Func1<T, TR> f)
	{
		List<TR> xxs = new ArrayList<TR>();
		for(int i = 0; i < xs.length; i++)
		{
			xxs.add(f.invoke(xs[i]));
		}
		return(xxs);
	}
	
	public static <T, TR> List<TR> map(List<T> xs, Func1<T, TR> f)
	{
		List<TR> xxs = new ArrayList<TR>();
		for(int i = 0; i < xs.size(); i++)
		{
			xxs.add(f.invoke(xs.get(i)));
		}
		return(xxs);
	}
	
	public static <T, TR> TR foldLeft(T[] xs, Func2<TR, T, TR> f, TR r)
	{
		for(int i = 0; i < xs.length; i++)
		{
			r = f.invoke(r, xs[i]);
		}
		return(r);
	}
	
	public static <T, TR> TR foldLeft(List<T> xs, Func2<TR, T, TR> f, TR r)
	{
		for(int i = 0; i < xs.size(); i++)
		{
			r = f.invoke(r, xs.get(i));
		}
		return(r);
	}
	
	public static <T, TR> TR foldRight(T[] xs, Func2<TR, T, TR> f, TR r)
	{
		for(int i = xs.length - 1; xs.length > 0; i--)
		{
			r = f.invoke(r, xs[i]);
		}
		return(r);
	}
	
	public static <T, TR> TR foldRight(List<T> xs, Func2<TR, T, TR> f, TR r)
	{
		for(int i = xs.size() - 1; i > 0; i--)
		{
			r = f.invoke(r, xs.get(i));
		}
		return(r);
	}
	
	public static <T> List<T> toList(T[] xs)
	{
		return(new ArrayList<T>(Arrays.asList(xs)));
	}
	
	/*
	public static <T> T[] toList(List<T> xs)
	{
		return((T[]) xs.toArray());
		return(xs.toArray((T[]) xs.toArray()));
	}
	*/
}
