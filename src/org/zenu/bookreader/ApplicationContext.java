package org.zenu.bookreader;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;


public class ApplicationContext
	extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		registerBugReport();
		
		db_ = new BookCacheDB(this);
	}
	
	private BookCacheDB db_;
	public BookCacheDB getDB()
	{
		return(db_);
	}
	
	public void registerBugReport()
	{
		final SharedPreferences pref = getSharedPreferences(getClass().getName(), MODE_PRIVATE);
		
		final UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
			{
				@Override
				public void uncaughtException(Thread thread, Throwable ex)
				{
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					Editor edit = pref.edit();
					String pkg = getPackageName();
					
					PrintWriter p = new PrintWriter(out);
					try
					{
						p.println("PackageInfo.PackageName : " + pkg);
						try
						{
							PackageInfo info = getPackageManager().getPackageInfo(pkg, 0);
							p.println("PackageInfo.VersionName : " + info.versionName);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						p.println("Date : " + (new Date()).toString());
						p.println("Build.DEVICE : " + Build.DEVICE);
						p.println("Build.MODEL : " + Build.MODEL);
						p.println("Build.VERSION.SDK_INT : " + Build.VERSION.SDK_INT);
						ex.printStackTrace(p);
						p.println();
					}
					finally
					{
						p.close();
					}
					
					edit.putString("BugReport", out.toString());
					edit.commit();
					
					defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
				}
			});
	}
	
	public boolean sendBugReport()
	{
		final String BUG_REPORT_SENDTO = "mailto:zenuas@gmail.com";
		
		final SharedPreferences pref = getSharedPreferences(getClass().getName(), MODE_PRIVATE);
		String report = pref.getString("BugReport", "");
		if(report.length() <= 0) {return(false);}
		
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(BUG_REPORT_SENDTO));
		intent.putExtra(Intent.EXTRA_SUBJECT, "BugReport");
		intent.putExtra(Intent.EXTRA_TEXT, report);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(intent);
		
		Editor edit = pref.edit();
		edit.putString("BugReport", "");
		edit.commit();
		return(true);
	}
}
