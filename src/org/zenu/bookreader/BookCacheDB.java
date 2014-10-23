package org.zenu.bookreader;

import java.io.File;

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
		db.execSQL("create table BookCache (path text primary key, cover blob, page text, pageindex int, maxpage int, direction int, spread int, lastmodified int);");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("drop table BookCache;");
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("drop table BookCache;");
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
		try
		{
			if(c.moveToFirst())
			{
				long d = c.getLong(c.getColumnIndex("lastmodified"));
				if(d == book.getLastModified())
				{
					book.Page = c.getString(c.getColumnIndex("page"));
					//book.setPageIndex(c.getInt(c.getColumnIndex("pageindex")));
					book.setMaxPage(c.getInt(c.getColumnIndex("maxpage")));
					book.setDirection(Direction.valueOf(c.getInt(c.getColumnIndex("direction"))));
					book.setLastModified(d);
					book.deserializeCover(c.getBlob(c.getColumnIndex("cover")));
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
		}
		return(book);
	}
	
	public void saveBook(Book book)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from BookCache where path = ?", new Object[] {book.Path});
		
		ContentValues values = new ContentValues();
		values.put("path", book.Path);
		//values.put("cover", book.serializeCover());
		values.put("page", book.Page);
		values.put("pageindex", book.getPageIndex());
		values.put("maxpage", book.getMaxPage());
		values.put("direction", book.getDirection().getId());
		values.put("lastmodified", book.getLastModified());
		db.insert("BookCache", null, values);
	}
}