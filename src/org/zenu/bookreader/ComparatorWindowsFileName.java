package org.zenu.bookreader;

import android.annotation.SuppressLint;
import java.util.Comparator;


@SuppressLint("DefaultLocale")
public class ComparatorWindowsFileName<T>
	implements Comparator<T>
{
	private Func1<T, String> f_;
	public ComparatorWindowsFileName(Func1<T, String> f)
	{
		f_ = f;
	}
	
	@Override
	public int compare(T left_, T right_)
	{
		String left = f_.invoke(left_).toUpperCase();
		String right = f_.invoke(right_).toUpperCase();
		
		int left_index = 0;
		int right_index = 0;
		
		while(left_index < left.length() && right_index < right.length())
		{
			char left_char = left.charAt(left_index);
			char right_char = right.charAt(right_index);
			
			if(left_char == right_char)
			{
				left_index++;
				right_index++;
			}
			else if(
				left_char >= '0' && left_char <= '9' &&
				right_char >= '0' && right_char <= '9')
			{
				int left_num = left_char - '0';
				int right_num = right_char - '0';
				
				left_index++;
				right_index++;
				while(left_index < left.length())
				{
					left_char = left.charAt(left_index);
					
					if(left_char >= '0' && left_char <= '9')
					{
						left_num = left_num * 10 + left_char - '0';
						left_index++;
					}
					else
					{
						break;
					}
				}
				while(right_index < right.length())
				{
					right_char = right.charAt(right_index);
					
					if(right_char >= '0' && right_char <= '9')
					{
						right_num = right_num * 10 + right_char - '0';
						right_index++;
					}
					else
					{
						break;
					}
				}
				if(left_num != right_num) {return(left_num - right_num);}
			}
			else
			{
				return(left_char - right_char);
			}
		}
		
		if(left_index < left.length()) {return(-1);}
		if(right_index < right.length()) {return(1);}
		return(0);
	}
	
}
