package org.zenu.bookreader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class BookCacheDB
	extends SQLiteOpenHelper
{
	public BookCacheDB(Context context)
	{
		super(context, "book_cache.db", null, 1);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("create table BookCache (path text, pageindex int, page text, maxpage int, cover text, direction int, spread int, lastmodified int, primary key(path));");
		db.execSQL("create table Bookmarks (path text, pageindex int, page text, createtime int, primary key(path, pageindex));");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("drop table BookCache;");
		db.execSQL("drop table Bookmarks;");
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("drop table BookCache;");
		db.execSQL("drop table Bookmarks;");
		onCreate(db);
	}
	
	public Book getBook(String path) throws Exception
	{
		return(getBook(new File(path)));
	}
	
	public Book getBook(File f) throws Exception
	{
		String path = f.getCanonicalPath();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("BookCache", null, "path = ?", new String[] {path}, null, null, null);
		
		Book book = BookFactory.open(path);
		if(book == null) {return(null);}
		try
		{
			if(c.moveToFirst())
			{
				long d = c.getLong(c.getColumnIndex("lastmodified"));
				if(d == book.getLastModified())
				{
					book.setPageIndex(c.getInt(c.getColumnIndex("pageindex")));
					book.Page = c.getString(c.getColumnIndex("page"));
					book.setMaxPage(c.getInt(c.getColumnIndex("maxpage")));
					book.CoverPage = c.getString(c.getColumnIndex("cover"));
					book.setDirection(Direction.valueOf(c.getInt(c.getColumnIndex("direction"))));
					book.setLastModified(d);
				}
				else
				{
					db.execSQL("delete from BookCache where text = ?", new Object[] {path});
				}
			}
		}
		catch(Exception e)
		{
			// DBから取得失敗してもなるべくそのまま続行する
			e.printStackTrace();
			ApplicationContext.getContext().addBugReport(e);
		}
		return(book);
	}
	
	public void saveBook(Book book)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from BookCache where path = ?", new Object[] {book.Path});
		
		ContentValues values = new ContentValues();
		values.put("path", book.Path);
		values.put("pageindex", book.getPageIndex());
		values.put("page", book.Page);
		values.put("maxpage", book.getMaxPage());
		values.put("cover", book.CoverPage);
		values.put("direction", book.getDirection().getId());
		values.put("lastmodified", book.getLastModified());
		db.insert("BookCache", null, values);
	}
	
	public List<Bookmark> getBookmarks()
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("Bookmarks", null, null, null, null, null, "createtime desc");
		
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		try
		{
			if(c.moveToFirst())
			{
				do
				{
					Bookmark x = new Bookmark();
					x.Path = c.getString(c.getColumnIndex("path"));
					x.PageIndex = c.getInt(c.getColumnIndex("pageindex"));
					x.Page = c.getString(c.getColumnIndex("page"));
					bookmarks.add(x);
					
				} while(c.moveToNext());
			}
		}
		catch(Exception e)
		{
			// DBから取得失敗してもなるべくそのまま続行する
			e.printStackTrace();
			ApplicationContext.getContext().addBugReport(e);
		}
		return(bookmarks);
	}
	
	public List<Bookmark> getBookmarks(Book book)
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("Bookmarks", null, "path = ?", new String[] {book.Path}, null, null, "createtime desc");
		
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		try
		{
			if(c.moveToFirst())
			{
				do
				{
					Bookmark x = new Bookmark();
					x.Path = c.getString(c.getColumnIndex("path"));
					x.PageIndex = c.getInt(c.getColumnIndex("pageindex"));
					x.Page = c.getString(c.getColumnIndex("page"));
					bookmarks.add(x);
					
				} while(c.moveToNext());
			}
		}
		catch(Exception e)
		{
			// DBから取得失敗してもなるべくそのまま続行する
			e.printStackTrace();
			ApplicationContext.getContext().addBugReport(e);
		}
		return(bookmarks);
	}
	
	public void addBookmark(Book book)
	{
		removeBookmark(book);
		
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("path", book.Path);
		values.put("pageindex", book.getPageIndex());
		values.put("page", book.Page);
		values.put("createtime", new Date().getTime());
		db.insert("Bookmarks", null, values);
	}
	
	public void removeBookmark(Book book)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from Bookmarks where path = ? and pageindex = ?", new Object[] {book.Path, book.getPageIndex()});
	}
}