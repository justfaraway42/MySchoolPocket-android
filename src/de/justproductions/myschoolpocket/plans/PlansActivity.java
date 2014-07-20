package de.justproductions.myschoolpocket.plans;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import de.justproductions.myschoolpocket.R;
import de.justproductions.myschoolpocket.main.ExceptionHandler;
import de.justproductions.myschoolpocket.main.TabLayoutController;

public class PlansActivity extends ListActivity {

	static String[] names;
	static String[] classes;
	static String[] urls;
	private static ArrayList<Plan> plans;
	static String todayString;
	PlansDownload currentDownload;
	static SharedPreferences preferences;

	static Context context;

	static AccessoriesAdapter listAdapter;
	static AccessoriesAdapter listAdapter2;
	static ProgressDialog progressDialog;
	static String filepath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		names = new String[] {};
		plans = new ArrayList<Plan>();
		todayString = context.getString(R.string.today);

		if (TabLayoutController.tabHost.getCurrentTab() == 1) {
			listAdapter = new AccessoriesAdapter();
			setListAdapter(listAdapter);
		} else if (TabLayoutController.tabHost.getCurrentTab() == 2) {
			listAdapter2 = new AccessoriesAdapter();
			setListAdapter(listAdapter2);
		}
		
		this.getListView().setLongClickable(true);
		   this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
		        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
		        	
		        	Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, plans.get(position).url.toString());
					context.startActivity(Intent.createChooser(shareIntent, getString(R.string.news_share)));
		        	
		            return true;
		        }
		    });
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		final Plan currentPlan = plans.get(position);
		File file = new File(PlansActivity.filepath
				+ getFileStringFromURL(currentPlan.url));

		if ((preferences.getBoolean("showReloadButtonInTimetables", true) && TabLayoutController.tabHost
				.getCurrentTab() == 1)
				|| (preferences.getBoolean("showReloadButtonInRepresentation",
						false) && TabLayoutController.tabHost.getCurrentTab() == 2)) {
			// Reload-Button
			if (file.exists()) {
				openPDF(file, v.getContext());

			} else {

				startDownload(currentPlan.url, v.getContext());

				// open File in onPostExecute from currentDownlaod
			}
		} else {
			// without Reload-Button: always download File

			startDownload(currentPlan.url, v.getContext());

			// open File in onPostExecute from currentDownlaod
		}
	}

	static String getFileStringFromURL(String url) {

		return url.substring(url.lastIndexOf('/') + 1);
	}

	class AccessoriesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return plans.size();
		}

		@Override
		public Plan getItem(int position) {
			return plans.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			preferences = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());

			final Plan currentPlan = plans.get(position);
			LayoutInflater inflater = LayoutInflater.from(getBaseContext());

			if (currentPlan.isHeader) {
				convertView = inflater.inflate(R.layout.list_item_header,
						parent, false);

				TextView sectionView = (TextView) convertView
						.findViewById(R.id.list_item_section_text);
				sectionView.setText(currentPlan.title);

				convertView.setOnClickListener(null);
				convertView.setOnLongClickListener(null);
				convertView.setLongClickable(false);
			} else {
				convertView = inflater.inflate(R.layout.list_item_plan, parent,
						false);

				ImageButton imageButton = (ImageButton) convertView
						.findViewById(R.id.button1);

				if (new File(PlansActivity.filepath
						+ getFileStringFromURL(currentPlan.url)).exists()) {
					imageButton.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.ic_plans_reload));
				} else {
					imageButton.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.ic_plans_download));
				}

				if ((preferences.getBoolean("showReloadButtonInTimetables",
						true) && TabLayoutController.tabHost.getCurrentTab() == 1)
						|| (preferences.getBoolean(
								"showReloadButtonInRepresentation", false) && TabLayoutController.tabHost
								.getCurrentTab() == 2)) {
					imageButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startDownload(currentPlan.url, v.getContext());
						}
					});
				} else {
					imageButton.setVisibility(View.GONE);
				}

				TextView title = (TextView) convertView
						.findViewById(R.id.list_item_entry_title);
				title.setText(currentPlan.title);

				TextView subtitle = (TextView) convertView
						.findViewById(R.id.list_item_entry_summary);
				subtitle.setText(getDate(currentPlan.url));
			}

			return convertView;
		}
	}

	private void startDownload(String url, Context context) {
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			// Verbindung zum Internet besteht
			progressDialog = new ProgressDialog(context);

			progressDialog.setMessage(context
					.getString(R.string.plans_download));
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					currentDownload.cancel(true);
				}
			});
			progressDialog.show();

			currentDownload = new PlansDownload();
			currentDownload.execute(url);
		} else {
			// keine Verbindung zum Internet
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_noconnection);
			dialog.setTitle(context
					.getString(R.string.dialog_noconnection_title));
			dialog.setCancelable(true);

			Button button = (Button) dialog
					.findViewById(R.id.noconnectiondialog_button);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	public static void openPDF(File file, Context context) {
		Uri path = Uri.fromFile(file);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(path, "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	private static String getDate(String url) {
		File file = new File(PlansActivity.filepath + getFileStringFromURL(url));

		if (file.exists()
				&& (preferences
						.getBoolean("showReloadButtonInTimetables", true) && TabLayoutController.tabHost
						.getCurrentTab() == 1)
				|| (preferences.getBoolean("showReloadButtonInRepresentation",
						false) && TabLayoutController.tabHost.getCurrentTab() == 2)) {

			long lastModified = file.lastModified();
			Date lastModifiedDate = new Date(lastModified);

			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"dd.MM.yy HH:mm", Locale.US);

			return (context.getString(R.string.plans_lastUpdated) + " " + dateFormatter
					.format(lastModifiedDate));
		} else {

			if (TabLayoutController.tabHost.getCurrentTab() == 1) {
				return (context.getString(R.string.plans_downloadTimetable));
			} else if (TabLayoutController.tabHost.getCurrentTab() == 2) {
				return (context
						.getString(R.string.plans_downloadRepresentation));
			}
			return null;
		}
	}

	@SuppressLint("SdCardPath")
	public static void loadSources() {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		plans.clear();

		switch (TabLayoutController.tabHost.getCurrentTab()) {
		case 1:

			filepath = "/sdcard/myschoolpocket/timetables/";

			names = prefs.getString("timetables_names", null).split(";");
			urls = prefs.getString("timetables_urls", null).split(";");
			classes = prefs.getString("timetables_classes", null).split(";");

			// add Plans

			String classLevel = prefs.getString("classLevel", "no");

			if (classLevel.equals("no")) {
				plans.add(new Plan(
						context.getString(R.string.plans_allClasses), "", true));
			} else {
				plans.add(new Plan(context.getString(R.string.plans_class)
						+ " " + classLevel, "", true));
			}

			for (int i = 0; i < names.length; i++) {
				if (classLevel.equals("no")) {
					plans.add(new Plan(names[i], urls[i], false));
				} else if (classes[i].equals(classLevel)) {
					plans.add(new Plan(names[i], urls[i], false));
				}
			}

			listAdapter.notifyDataSetChanged();
			break;

		case 2:

			filepath = "/sdcard/myschoolpocket/representation/";
			
			urls = prefs.getString("representation_urls", null).split(";");

			if (urls.length == 5) {

				String[] weekdays = new String[] { "Monday", "Tuesday",
						"Wednesday", "Thuesday", "Friday" };

				String[] sortedWeekdays = new String[5];
				int today = 0;
				int i = 0;

				for (String weekday : weekdays) {
					try {
						SimpleDateFormat dateFormatter = new SimpleDateFormat(
								"E", Locale.US);

						Object dateObject = dateFormatter.parse(weekday);

						dateFormatter = new SimpleDateFormat("EEEE"); // Systemlokalisiert
						sortedWeekdays[i] = dateFormatter.format(dateObject);

						if (sortedWeekdays[i].equals(dateFormatter
								.format(new Date()))) {
							// import android.UTIL.Date!!! (nicht
							// android.SQL.Date)
							today = i + 1; // ansonsten: Montag = 0
						}
					} catch (ParseException e) {
						ExceptionHandler.makeExceptionAlert(context, e);
					}

					i++;
				}

				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				boolean sort = preferences.getBoolean("showTodayFirst", true);

				if (sort) {
					for (i = 1; i < today; i++) {
						// sorting Array...
						String tempFirstName = sortedWeekdays[0];
						String tempFirstURL = urls[0];
						
						for (int u = 0; u < sortedWeekdays.length - 1; u++) {
							sortedWeekdays[u] = String
									.valueOf(sortedWeekdays[u + 1]);
							urls[u] = String.valueOf(urls[u + 1]);
						}
						sortedWeekdays[sortedWeekdays.length - 1] = tempFirstName;
						urls[urls.length - 1] = tempFirstURL;
					}
				}

				// add Plans
				if (today != 0 && sort) {
					plans.add(new Plan(context.getString(R.string.today), "",
							true));
					plans.add(new Plan(sortedWeekdays[0], urls[0], false));

					plans.add(new Plan(context
							.getString(R.string.plans_restWeek), "", true));
					for (i = 1; i < sortedWeekdays.length; i++) {
						plans.add(new Plan(sortedWeekdays[i], urls[i], false));
					}
				} else {
					plans.add(new Plan(context
							.getString(R.string.plans_weekOverview), "", true));
					for (i = 0; i < sortedWeekdays.length; i++) {
						plans.add(new Plan(sortedWeekdays[i], urls[i], false));
					}
				}
			} else if (urls.length == 1) {
				plans.add(new Plan(todayString, "", true));
				plans.add(new Plan(todayString, urls[0], false));
			}

			listAdapter2.notifyDataSetChanged();
			break;

		default:
			break;
		}

	}
}
