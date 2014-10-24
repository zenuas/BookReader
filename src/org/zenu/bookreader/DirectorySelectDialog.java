package org.zenu.bookreader;

import java.io.File;
import java.io.FileFilter;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;


public abstract class DirectorySelectDialog
	extends Builder
{
	public abstract void onSelected(File d);
	
	public DirectorySelectDialog(Context context)
	{
		this(context, "/");
	}
	
	public DirectorySelectDialog(Context context, String path)
	{
		super(context);
		setDirectory(path);
		
		setNegativeButton("cancel", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
		setPositiveButton("select", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					onSelected(current_);
				}
			});
		setNeutralButton("..", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					String x = current_.getParent();
					if(x != null) {setDirectory(x);}
					dialog.dismiss();
					DirectorySelectDialog.this.show();
				}
			});
	}
	
	private File current_ = null;
	public boolean setDirectory(String path)
	{
		File f = new File(path);
		
		// setItems $ map (x -> x.getName()) $ current_.listFiles(x -> x.isDirectory())
		File[] xs = f.listFiles(new FileFilter()
			{
				@Override
				public boolean accept(File pathname)
				{
					return(pathname.isDirectory());
				}
			});
		if(xs == null)
		{
			Toast.makeText(this.getContext(), R.string.directoy_access_denied, Toast.LENGTH_SHORT).show();
			return(false);
		}
		
		final String[] files =
			Lists.map(
				xs,
				new Func1<File, String>()
					{
						@Override
						public String invoke(File a1)
						{
							return(a1.getName());
						}
					}
				).toArray(new String[0]);
		setItems(files, new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					setDirectory(Path.combine(current_.getAbsolutePath(), files[which]));
					dialog.dismiss();
					DirectorySelectDialog.this.show();
				}
			});
		
		current_ = f;
		setTitle(path);
		return(true);
	}
}
