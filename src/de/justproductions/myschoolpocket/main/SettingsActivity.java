package de.justproductions.myschoolpocket.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.justproductions.myschoolpocket.R;

@SuppressLint("SdCardPath")
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	// Auskommentieren fuer erweiterte Einstellungen, wie besipielsweise das kopieren der Datenbanken
	/*private String tt_files = "/sdcard/egw/timetables/";
	private String rp_files = "/sdcard/egw/representation/";*/
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setTitle(getString(R.string.settings));
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		ListPreference maximumArticles = (ListPreference) getPreferenceScreen().findPreference("maximumArticles");
		maximumArticles.setSummary(getPreferenceScreen().getSharedPreferences().getString("maximumArticles", null));
		
		ListPreference changeClass = (ListPreference) getPreferenceScreen().findPreference("classLevel");
		changeClass.setSummary(changeClass.getEntry());
		
		Preference changeSchool = findPreference("changeSchool");
		changeSchool.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(getString(R.string.preferences_changeSchool_warning_title));
				builder.setMessage(getString(R.string.preferences_changeSchool_warning_summary))
					.setCancelable(false)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							
							// delete old Stuff
							deleteRecursive(new File("/sdcard/egw/"));
							new File("/data/data/de.justproductions.egw/News.sqlite").delete();
								// need absolut path, because the "createDB" is in the constructor of the (NewsDatabase.DB_PATH)
							
							final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
							SharedPreferences.Editor editor = preferences.edit();
							
							editor.putInt("tabNumber", Integer.valueOf(0));
							editor.commit();
							
							editor.putBoolean("chooseSchool", true);
							editor.commit();
							

							Intent mainIntent = new Intent(getBaseContext(),
									StartActivity.class);
							mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(mainIntent);
							finish();
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
			           }
			       });
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
		});
		
		Preference aboutApp = findPreference("aboutApp");
		aboutApp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {

                copyDatabaseToSdCard();
				
				PackageInfo pinfo = null;
				try {
					pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				} catch (NameNotFoundException e) {
					ExceptionHandler.makeExceptionAlert(getBaseContext(), e);
				}

		        final Dialog dialog = new Dialog(context);
		        dialog.setContentView(R.layout.dialog_about);
		        dialog.setTitle(getString(R.string.app_name) + " " + getString(R.string.app_version) + ": " + pinfo.versionName);
		        dialog.setCancelable(true);

		        Button button = (Button) dialog.findViewById(R.id.aboutdialog_button);
		        button.setOnClickListener(new OnClickListener() {
		        @Override
		            public void onClick(View v) {
		                dialog.dismiss();
		            }
		        });
  
		        dialog.show();
				
				return true;
			}
		});}

    public void copyDatabaseToSdCard() {
        Log.e("Databasehealper", "********************************");
        try {
            File f1 = new File("/data/data/de.justproductions.myschoolpocket/school.sqlite");
            if (f1.exists()) {

                File f2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/My_App_Database.db");
                f2.createNewFile();
                InputStream in = new FileInputStream(f1);
                OutputStream out = new FileOutputStream(f2);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        Log.e("Databasehealper", "********************************");
    }

		// Auskommentieren fuer erweiterte Einstellungen, wie besipielsweise das kopieren der Datenbanken
		/*Preference copySchoolsDatabase = findPreference("copySchoolsDatabase");
		copySchoolsDatabase.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				new File(SchoolDatabase.DB_PATH).mkdirs();
				
				try {
					OutputStream out = new FileOutputStream(
							"/sdcard/egw/schools.sqlite");
					InputStream in = new FileInputStream(SchoolDatabase.DB_PATH);

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;

					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					in.close();
					out.close();

				} catch (Exception e) {
					ExceptionHandler.makeExceptionAlert(getBaseContext(), e);
				}
				Toast.makeText(getBaseContext(), "Schools-Database copied",
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		
		Preference deleteNewsDatabase = findPreference("deleteNewsDatabase");
		deleteNewsDatabase.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				File db = new File(NewsDatabase.DB_PATH);

				if (db.exists()) {
					new File(NewsDatabase.DB_PATH).delete();
					Toast.makeText(getBaseContext(), "News-Database removed",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), "already removed...",
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});

		Preference copyNewsDatabase = findPreference("copyNewsDatabase");
		copyNewsDatabase.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				new File(SchoolDatabase.DB_PATH).mkdirs();
				
				try {
					OutputStream out = new FileOutputStream(
							"/sdcard/egw/news.sqlite");
					InputStream in = new FileInputStream(NewsDatabase.DB_PATH);

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;

					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					in.close();
					out.close();

				} catch (Exception e) {
					ExceptionHandler.makeExceptionAlert(getBaseContext(), e);
				}

				Toast.makeText(getBaseContext(), "database copied",
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});

		Preference tt_delete = findPreference("tt_delete");
		tt_delete.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				File dir = new File(tt_files);

				if (dir.exists()) {
					deleteRecursive(new File(tt_files));
					Toast.makeText(getBaseContext(), "Timetable Plans removed",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), "already removed...",
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});

		Preference rp_delete = findPreference("rp_delete");
		rp_delete.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				File dir = new File(rp_files);

				if (dir.exists()) {
					deleteRecursive(new File(rp_files));
					Toast.makeText(getBaseContext(),
							"Representation Plans removed", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(getBaseContext(), "already removed...",
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
	}*/
	
	void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory()) {
	        for (File child : fileOrDirectory.listFiles())
	            deleteRecursive(child);
	    }

	    fileOrDirectory.delete();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		ListPreference maximumArticles = (ListPreference) getPreferenceScreen().findPreference("maximumArticles");
		maximumArticles.setSummary(getPreferenceScreen().getSharedPreferences().getString("maximumArticles", null));
		
		ListPreference changeClass = (ListPreference) getPreferenceScreen().findPreference("classLevel");
		changeClass.setSummary(changeClass.getEntry());
		
	}
}
