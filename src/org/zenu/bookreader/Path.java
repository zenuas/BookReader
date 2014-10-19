package org.zenu.bookreader;

import java.io.File;
import java.util.Collections;
import java.util.List;


public class Path
{
	public static final char PathSeparator = '/';
	
	public static String getDirectoryName(String path)
	{
		int dir = path.lastIndexOf(PathSeparator);
		return(dir >= 0 ? path.substring(0, dir + 1) : "");
	}
	
	public static String getFileName(String path)
	{
		int dir = path.lastIndexOf(PathSeparator);
		return(dir >= 0 ? path.substring(dir + 1) : path);
	}
	
	public static String getFileNameWithoutExtension(String path)
	{
		String name = getFileName(path);
		int ext = name.lastIndexOf('.');
		return(ext >= 0 ? name.substring(0, ext) : name);
	}
	
	public static String getExtension(String path)
	{
		String name = getFileName(path);
		int ext = name.lastIndexOf('.');
		return(ext >= 0 ? name.substring(ext) : "");
	}
	
	public static List<String> sortWindowsFileName(List<String> xs)
	{
		sortWindowsFileName(xs, new Func1<String, String>()
			{
				@Override
				public String invoke(String a1)
				{
					return(a1);
				}
			});
		return(xs);
	}
	
	public static <T> List<T> sortWindowsFileName(List<T> xs, final Func1<T, String> f)
	{
		// WindowsXP以降のファイル名ソート順を模倣
		Collections.sort(xs, new ComparatorWindowsFileName<T>(f));
		return(xs);
	}
	
	public static List<File> sortWindowsFolder(List<File> xs)
	{
		// xsに複数ディレクトリのFileをまとめている場合は未対応
		ComparatorWindowsFileName<File> compare = new ComparatorWindowsFileName<File>(new Func1<File, String>()
			{
				@Override
				public String invoke(File a1)
				{
					return(a1.getName());
				}
			})
			{
				@Override
				public int compare(File left_, File right_)
				{
					if(left_.isDirectory() == right_.isDirectory()) {return(super.compare(left_, right_));}
					return(left_.isDirectory() ? -1 : 1);
				}
			};
		Collections.sort(xs, compare);
		return(xs);
	}
}
