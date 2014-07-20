package de.justproductions.myschoolpocket.main;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import de.justproductions.myschoolpocket.R;
import de.justproductions.myschoolpocket.infos.InfoActivity;
import de.justproductions.myschoolpocket.news.NewsActivity;
import de.justproductions.myschoolpocket.plans.PlansActivity;

public class TabLayoutController extends TabActivity {
	
	public static TabHost tabHost;
	int myInt;
	private static boolean appInForegroundMode;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.main);

	    Resources res = getResources();
	    tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    Intent intent;
	    
	    intent = new Intent().setClass(this, NewsActivity.class);
	    // Intent fuer das starten der Activitys
	    
	    spec = tabHost.newTabSpec("news").setIndicator(getString(R.string.news),
	                      res.getDrawable(R.drawable.icon_tab_news))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, PlansActivity.class);
	    spec = tabHost.newTabSpec("timetables").setIndicator(getString(R.string.timetables),
	                      res.getDrawable(R.drawable.icon_tab_timetables))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, PlansActivity.class);
	    spec = tabHost.newTabSpec("representation").setIndicator(getString(R.string.representation),
	                      res.getDrawable(R.drawable.icon_tab_representation))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, InfoActivity.class);
	    spec = tabHost.newTabSpec("info").setIndicator(getString(R.string.info),
	                      res.getDrawable(R.drawable.icon_tab_info))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, SettingsActivity.class);
	    spec = tabHost.newTabSpec("settings").setIndicator(getString(R.string.settings),
	                      res.getDrawable(R.drawable.icon_tab_settings))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    //BETA-TAB
//	    intent = new Intent().setClass(this, BetaActivity.class);
//	    spec = tabHost.newTabSpec("beta").setIndicator(getString(R.string.beta),
//	                      res.getDrawable(R.drawable.icon_tab_beta))
//	                  .setContent(intent);
//	    tabHost.addTab(spec);
		
	    tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {
				if (tabId == "news") {
			    	NewsActivity.deleteOldArticles();
			    } else if (tabId == "timetables" || tabId == "representation") {
			    	PlansActivity.loadSources();
			    } else if (tabId == "info") {
			    	InfoActivity.loadSources();
			    }
			}});
	    
	    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    tabHost.setCurrentTab(preferences.getInt("tabNumber", 0));
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	    
		  final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		  SharedPreferences.Editor editor = preferences.edit();
		  
		  if (preferences.getBoolean("reloadOnStartup", false)) {
			  editor.putInt("tabNumber", 0);
		  } else {
			  editor.putInt("tabNumber", tabHost.getCurrentTab());
		  }
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    appInForegroundMode = false;
	    
		  final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		  SharedPreferences.Editor editor = preferences.edit();
		  
		  if (preferences.getBoolean("reloadOnStartup", false)) {
			  editor.putInt("tabNumber", 0);
		  } else {
			  editor.putInt("tabNumber", tabHost.getCurrentTab());
		  }
		  
		  editor.commit();
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    appInForegroundMode = true;
	}
	
	public static boolean appInForeground() {
	    return appInForegroundMode;
	}
}