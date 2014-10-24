package org.zenu.bookreader;

import java.util.List;


public class Strings
{
	public static String join(String[] xs, String separator)
	{
		StringBuilder x = new StringBuilder();
		for(int i = 0; i < xs.length; i++)
		{
			if(i > 0) {x.append(separator);}
			x.append(xs[i]);
		}
		return(x.toString());
	}
	
	public static String join(List<String> xs, String separator)
	{
		StringBuilder x = new StringBuilder();
		for(int i = 0; i < xs.size(); i++)
		{
			if(i > 0) {x.append(separator);}
			x.append(xs.get(i));
		}
		return(x.toString());
	}
}
