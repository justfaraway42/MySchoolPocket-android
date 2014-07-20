package de.justproductions.myschoolpocket.school;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SchoolDatabase {
	
    //The Android's default system path of your application database.
    public static String DB_PATH = "/data/data/de.justproductions.myschoolpocket/schools.sqlite"; //"/data/data/YOUR_PACKAGE/databases/"
    
	private static final String TABLE_STATES = "states";
	private static final String TABLE_COMMUNITIES = "communities";
	private static final String TABLE_SCHOOLS = "schools";
	
	private static SQLiteDatabase db;

	public SchoolDatabase() {
		
		if (new File(DB_PATH).exists()) {
			db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
		} else {
			db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
			db.execSQL("create table states (_id integer primary key autoincrement, name text not null);");
			db.execSQL("create table communities (_id integer primary key autoincrement, communitytostate integer, name text not null);");
			db.execSQL("create table schools (_id integer primary key autoincrement, schooltocommunity integer, name text not null, feed text, timetables_names text, timetables_urls text, timetables_classes text, representation_urls URL, phone_number text, website text, email_address text, address texts);");
		}
	}
	
	public void insertState(String name) throws ParseException {
		
		ContentValues values = new ContentValues(); 
		values.put("name", name);
		db.insert(TABLE_STATES, null, values);
		
		//Log.e("neues Land eingef�gt:", name);
	}
	
	public void insertCommunity(String name) throws ParseException {
		
		Cursor c = db.query(TABLE_STATES, null, null, null, null, null, "_id" + " DESC");
		// Absteigend sortieren, da so zuletzt eingef�gte Stadt als erste ausgew�hlt werden kann und ihr damit die einfach Bundesl�nder zugeordnet werden k�nnen 
		
		c.moveToFirst();
		
		ContentValues values = new ContentValues();
		values.put("communitytostate", c.getLong(0));
		values.put("name", name);
		db.insert(TABLE_COMMUNITIES, null, values);

		//Log.e("neues Bundesland eingef�gt:", name);
	}
	
	public void insertSchool(School currentSchool) throws ParseException {
		
		Cursor c = db.query(TABLE_COMMUNITIES, null, null, null, null, null, "_id" + " DESC");
		
		c.moveToFirst();
		
		ContentValues values = new ContentValues();
		values.put("schooltocommunity", c.getLong(0));
		values.put("name", currentSchool.name);
		values.put("feed", currentSchool.feed.toString());
		values.put("timetables_names", currentSchool.timetables_names);
		values.put("timetables_urls", currentSchool.timetables_urls);
		values.put("timetables_classes", currentSchool.timetables_classes);
		values.put("representation_urls", currentSchool.representation_urls);
		values.put("phone_number", currentSchool.phone_number);
		values.put("website", currentSchool.website.toString());
		values.put("email_address", currentSchool.email_address);
		values.put("address", currentSchool.address);
		
		db.insert(TABLE_SCHOOLS, null, values);
		
		//Log.e("neue Schule eingef�gt:", currentSchool.name);
	}
	
	public Cursor getCounties() {
		Cursor c = db.query(TABLE_STATES, null, null, null, null, null, "name ASC");
		return c;
	}
	
	public Cursor getCommunitiesFromState(Long state) {
		Cursor c = db.query(TABLE_COMMUNITIES, null, "communitytostate = ?", new String[] { String.valueOf(state) }, null, null, "name ASC");
		return c;
	}
	
	public Cursor getSchoolsFromCommunity(Long community) {
		Cursor c = db.query(TABLE_SCHOOLS, null, "schooltocommunity = ?", new String[] { String.valueOf(community) }, null, null, "name ASC");
		return c;
	}
	
	public School getSchool(Long school) throws MalformedURLException {
		School currentSchool = new School();
		Cursor c = db.query(TABLE_SCHOOLS, null, "_id = ?", new String[] { String.valueOf(school) }, null, null, "name ASC");
		
		c.moveToFirst();
		
		currentSchool.name = c.getString(2);
		currentSchool.feed = new URL(c.getString(3));
		currentSchool.timetables_names = c.getString(4);
		currentSchool.timetables_urls = c.getString(5);
		currentSchool.timetables_classes = c.getString(6);
		currentSchool.representation_urls = c.getString(7);
		currentSchool.phone_number = c.getString(8);
		currentSchool.website = new URL(c.getString(9));
		currentSchool.email_address = c.getString(10);
		currentSchool.address = c.getString(11);
		
		return currentSchool;
	}
	
	public Cursor getCommunitiesFromSchool(Long school) {
		Cursor c = db.query(TABLE_COMMUNITIES, null, "_id = ?", new String[] { String.valueOf(school) }, null, null, "name ASC");
		c.moveToFirst(); //  es gibt nur ein Bundesland
		Long state = c.getLong(1); // communitytostate
		c = getCommunitiesFromState(state);
		return c;
	}
	
	public void close() {
		//db.close();
	}

}
