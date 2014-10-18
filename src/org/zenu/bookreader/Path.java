package org.zenu.bookreader;


public class Path
{
	public static final char PathSeparator = '/';

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
}
