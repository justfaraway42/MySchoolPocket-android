package de.justproductions.myschoolpocket.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import de.justproductions.myschoolpocket.R;
import de.justproductions.myschoolpocket.school.SchoolsActivity;

public class StartActivity extends Activity {
	protected boolean _active = true;
	protected int _splashTime = 1500;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.start);

		// thread for displaying the SplashScreen
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					ExceptionHandler.makeExceptionAlert(getBaseContext(), e);
				} finally {
					SharedPreferences preferences = PreferenceManager
							.getDefaultSharedPreferences(getBaseContext());
					
					if (preferences.getBoolean("reloadOnStartup", true)) {
						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean("reloadNews", true);
						editor.commit();
					}
					
					boolean chooseSchool = preferences.getBoolean("chooseSchool", true);
					
					Intent intent;
					
					if (chooseSchool || (preferences.getString("school_feed", null) == null)) {
						// App wird gestartet - Schule muss gew�hlt werden
						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean("chooseSchool", false);
						if (preferences.getString("school_feed", null) == null) {
							// App wird zum ersten mal gestartet
							// Maximalwert f�r gespeicherte Artikel wird auf 15 gesetzt
							editor.putBoolean("reloadOnStartup", true);
							editor.putString("maximumArticles", "15");
						}
						editor.putInt("tabNumber", 0);
						editor.commit();
						
						intent = new Intent(getBaseContext(), SchoolsActivity.class);
						startActivity(intent);

					} else {
						intent = new Intent(getBaseContext(),TabLayoutController.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// Intent loescht alle anderen Activitys
					}
					
					startActivity(intent);
					finish();
					// Activity beendet sich selbst
				}
			}
		};
		splashTread.start();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			_active = false;
		}
		return true;
	}
}
