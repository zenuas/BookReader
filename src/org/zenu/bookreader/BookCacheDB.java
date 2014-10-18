package org.zenu.bookreader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class BookCacheDB
	extends SQLiteOpenHelper
{
	public final static String TABLE_NAME = "BookCache";
	
	public BookCacheDB(Context context)
	{
		super(context, "book_cache.db", null, 1);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("create table " + TABLE_NAME + " (path text primary key, cover blob, page text);");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("drop table BookCache;");
		onCreate(db);
	}
	
	public Book getBook(String path) throws Exception
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_NAME, null, "path = ?", new String[] {path}, null, null, null);
		
		Book book = BookFactory.open(path);
		if(c.moveToFirst())
		{
			book.deserializeCover(c.getBlob(c.getColumnIndex("cover")));
		}
		return(book);
	}
}