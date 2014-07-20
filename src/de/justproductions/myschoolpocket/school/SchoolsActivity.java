package de.justproductions.myschoolpocket.school;

import java.io.File;
import java.net.MalformedURLException;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.justproductions.myschoolpocket.R;
import de.justproductions.myschoolpocket.main.ExceptionHandler;
import de.justproductions.myschoolpocket.main.TabLayoutController;

public class SchoolsActivity extends ListActivity {
	
	ArrayAdapter<String> fileList;
	Listener adapter;
	SchoolDatabase db;
	Cursor c;
	String level;
	School currentSchool;
	Long selectedStateID;
	Long selectedCommunityID;
	
    /** Called when the activity is first created. */
    @SuppressLint("UseValueOf")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.schools_chooseSchool);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
		
        db = new SchoolDatabase();
		new File(SchoolDatabase.DB_PATH).delete();
    	
    	getResources().openRawResource(R.raw.data);
    	SchoolParser parser = new SchoolParser();
    	parser.get(this, getResources().openRawResource(R.raw.data));
		
    	// comment for choice of schools in other communities and/or states
		c = db.getSchoolsFromCommunity(new Long(1));
    	String[] from = new String[] { "name" };
    	int[] to = new int[] { R.id.list_item_title };
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_school, c, from, to);
        setListAdapter(mAdapter);
        level = "school";
        selectedStateID = (long) 1;
        selectedCommunityID = (long) 1;

        // uncomment for choice of schools in other communities and/or states
/*
    	c = db.getCounties();

    	String[] from = new String[] { "name" };
    	int[] to = new int[] { R.id.list_item_title };

        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_school, c, from, to);
        setListAdapter(mAdapter);
        level = "state"; // level = "communitiy", if only other communities
*/
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (level == "state") {
			c.moveToPosition(position);
			selectedStateID = c.getLong(0);
			
			c = db.getCommunitiesFromState(selectedStateID);
	    	String[] from = new String[] { "name" }; 
	    	int[] to = new int[] { R.id.list_item_title };
	        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_school, c, from, to);
	        setListAdapter(mAdapter);
	        level = "community";
		} else if (level == "community") {
			c.moveToPosition(position);
			selectedCommunityID = c.getLong(0);
			
			c = db.getSchoolsFromCommunity(selectedCommunityID);
	    	String[] from = new String[] { "name" };
	    	int[] to = new int[] { R.id.list_item_title };
	        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_school, c, from, to);
	        setListAdapter(mAdapter);
	        level = "school";
		} else if (level == "school") {
			
			c.moveToPosition(position);
			Long school = c.getLong(0);
			currentSchool = null;
			try {
				currentSchool = db.getSchool(school);
			} catch (MalformedURLException e) {
				ExceptionHandler.makeExceptionAlert(getBaseContext(), e);
			}
			
			setTitle(R.string.schools_chooseClass);
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.classes_names));
	        setListAdapter(adapter);
	        level = "class";
		} else if (level == "class") {
			
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("school_name", currentSchool.name);
			editor.putString("school_feed", currentSchool.feed.toString());
			editor.putString("timetables_names", currentSchool.timetables_names);
			editor.putString("timetables_urls", currentSchool.timetables_urls);
			editor.putString("timetables_classes", currentSchool.timetables_classes);
			editor.putString("representation_urls", currentSchool.representation_urls);
			editor.putString("phone_number", currentSchool.phone_number);
			editor.putString("website", currentSchool.website.toString());
			editor.putString("email_address", currentSchool.email_address);
			editor.putString("address", currentSchool.address);
			
			editor.putBoolean("chooseSchool", false);
			editor.putBoolean("schoolChosen", true);
			editor.commit();
			
			db.close();
			
			editor.putString("classLevel", getResources().getStringArray(R.array.classes_values)[position]);
			editor.commit();
			
			Intent intent = new Intent(getBaseContext(), TabLayoutController.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			// um nach einem Schulwechsel nicht zurueck in die Info-Activity zu springen
			// android:noHistory="true" bei TabLayout wuerde die App bei jedem oeffnen (auch, wenn sie nur aus dem Cache geladen wird) neustarten (StartActivity)
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// uncomment for choice of schools in other communities and/or states
/*
			if (level == "community") { // leave this if-clause commented, if only other states
				c = db.getCounties();

				String[] from = new String[] { "name" };
				int[] to = new int[] { R.id.list_item_title };

				SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
						R.layout.list_item_school, c, from, to);
				setListAdapter(mAdapter);
				level = "state";
			} else if (level == "school") {
				c.moveToFirst();
				Long school = c.getLong(1);

				c = db.getCommunitiesFromSchool(school);
				String[] from = new String[] { "name" };
				int[] to = new int[] { R.id.list_item_title };
				SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
						R.layout.list_item_school, c, from, to);
				setListAdapter(mAdapter);
				level = "community";
			}
*/
			if (level == "class") {
				c = db.getCounties();
				c = db.getCommunitiesFromState(selectedStateID);
				c = db.getSchoolsFromCommunity(selectedCommunityID);

		        setTitle(R.string.schools_chooseSchool);
		        
				String[] from = new String[] { "name" };
				int[] to = new int[] { R.id.list_item_title };
				SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
						R.layout.list_item_school, c, from, to);
				setListAdapter(mAdapter);
				level = "school";
			}
		}
		return true;
	}
	
}