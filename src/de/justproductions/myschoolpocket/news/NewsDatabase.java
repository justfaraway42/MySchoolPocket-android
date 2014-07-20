package de.justproductions.myschoolpocket.news;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import de.justproductions.myschoolpocket.main.ExceptionHandler;

public class NewsDatabase {

    public static String DB_PATH = "/data/data/de.justproductions.myschoolpocket/news.sqlite";
    // Ort zum speichern der Datenbank innerhalb der App"/data/data/YOUR_PACKAGE/databases/"
	
	private static final String ARTICLES_TABLE = "articles";
	
	public static SQLiteDatabase db;
	private Context context;

	public NewsDatabase(Context ctx) {
		context = ctx;
		
		if (new File(DB_PATH).exists()) {
			db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
		} else {
			db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
			db.execSQL("create table articles (_id integer primary key autoincrement, title text not null, description text not null, link text not null, date text not null, read integer);");
		}
	}

	public void insertArticle(String title, String description, URL link, long date) throws ParseException {
		
		Cursor c = db.query(ARTICLES_TABLE, new String [] {"_id", "title", "description", "link", "date", "read"}, "title=? AND date=?",
		         new String[] { String.valueOf(title), String.valueOf(date) }, null, null, null); // prueft, ob Artikel mit gleichem Titel und gleicher Zeit schon existiert
		
		if (c.getCount() == 0) {
			ContentValues values = new ContentValues(); 
			values.put("title", title);
			values.put("description", description);
			values.put("link", link.toString());
			values.put("date", String.valueOf(date));
			
			values.put("read", 0);
			
			db.insert(ARTICLES_TABLE, null, values);
			
			//Log.e("neuen Artikel eingef�gt:", title);
		}
	}
	
	public static void setRead(long article_id) {
		ContentValues values = new ContentValues();
		values.put("read", true);
		
		db.update(ARTICLES_TABLE, values, "_id = "+article_id, null);
	}
	
	public void setAllRead() {
		ContentValues values = new ContentValues();
		values.put("read", true);
		db.update(ARTICLES_TABLE, values, null, null);
	}
	
	public void setAllUnread() {
		ContentValues values = new ContentValues();
		values.put("read", false);
		db.update(ARTICLES_TABLE, values, null, null);
	}
	
	public Cursor getCursor() {
		Cursor c = db.query(ARTICLES_TABLE, new String[] { "_id",
				"title", "description", "link", "date", "read" }, null, null, null,
				null, "date" + " DESC");
		return c;
	}
	
	public Article getArticle(Cursor cursor) {

		Article article = new Article();
		
		try {
			article._id = cursor.getLong(0);
			article.title = cursor.getString(1);
			article.description = cursor.getString(2);
			
			article.link = (new URL(cursor.getString(3)));
			article.date = cursor.getLong(4);
			
			if (cursor.getInt(5) == 0) {
				article.read = false;
			} else if (cursor.getInt(5) == 1) {
				article.read = true;
			}
		} catch (SQLException e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		} catch (MalformedURLException e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		}
		return article;
	}
	
	public void checkNumberOfArticles(Context context) {
		Cursor c = getCursor();
		
		int numberOfArticles = c.getCount();
		
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	String maximumOfArticles = prefs.getString("maximumArticles", null);
		
		if (numberOfArticles > Integer.valueOf(maximumOfArticles)) {
			
			for (int i = Integer.valueOf(maximumOfArticles); i < numberOfArticles; ++i) {
				
				c.moveToPosition(i);
				long currentArticleID = c.getLong(0);
				db.delete(ARTICLES_TABLE, "_id=?", new String[] { String.valueOf(currentArticleID) });
			}
		}
	}
	
	public static void delete() {
		db.delete(ARTICLES_TABLE, null, null);
		db.close();
	}

}
