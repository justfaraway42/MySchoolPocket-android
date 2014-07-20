package de.justproductions.myschoolpocket.news;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.justproductions.myschoolpocket.R;
import de.justproductions.myschoolpocket.main.TabLayoutController;

@SuppressLint("NewApi")
public class NewsActivity extends ListActivity {

	ProgressDialog loadingDialog;
	static ListAdapter adapter;
	static NewsDatabase newsdb;
	String feedPath;
	static Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		context = this;
		loadingDialog = new ProgressDialog(context);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		feedPath = preferences.getString("school_feed", null);

		boolean schoolChosen = preferences.getBoolean("schoolChosen", false);
		boolean reloadNews = preferences.getBoolean("reloadNews", false);

		if (schoolChosen) {
			// Schule wurde ausgew�hlt, News werden (zum ersten Mal) geladen

			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("schoolChosen", false);
			editor.commit();

			startDownload();
		} else if (!new NewsDatabase(this).getCursor().moveToFirst() || reloadNews) {
			// pr�ft, ob die News DB leer ist, ODER bei jedem Neustart
			// neugeladen werden soll (Einstellung)
			// (ist "reloadOnStartup = true" wird "reloadNews" auf "true" in der
			// StartAktivity gesetzt)
			
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("reloadNews", false);
			editor.commit();

			startDownload();
		}

		newsdb = new NewsDatabase(this);

		Cursor c = newsdb.getCursor();
		adapter = new ListAdapter(this, c);
		setListAdapter(adapter);
	}

	private void startDownload() {
	
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			loadingDialog.setMessage(getString(R.string.news_load));
			loadingDialog.setCancelable(false);
			loadingDialog.show();
	
			new startParsing().execute("");
		} else {

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

	public class ListAdapter extends CursorAdapter {

		public ListAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.list_item_article, parent, false);

			bindView(v, context, cursor);

			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			Article currentArticle = newsdb.getArticle(cursor);

			ImageView image = (ImageView) view.findViewById(R.id.read_icon);
			if (!currentArticle.read) {
				image.setImageResource(R.drawable.news_unread);
			} else if (currentArticle.read) {
				image.setImageResource(R.drawable.news_read);
			}

			TextView title = (TextView) view.findViewById(R.id.title_cell);
			title.setText(currentArticle.title);

			TextView date = (TextView) view.findViewById(R.id.date_cell);

			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"d. MMMM y HH:mm"); // ist systemlokalisiert
			Date dateObject = new Date(currentArticle.date);
			String dateString = dateFormatter.format(dateObject);

			date.setText(dateString);
		}
	}

	class startParsing extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... strings) {

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			feedPath = prefs.getString("school_feed", null);

			NewsParser parser = new NewsParser();
			parser.getLatestArticles(context, feedPath);

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... item) {
		}

		@Override
		protected void onPostExecute(Integer result) {
			deleteOldArticles();

			if (loadingDialog.isShowing()
					&& TabLayoutController.appInForeground()) {
				loadingDialog.dismiss();
			}
		}
	}
	
	public static void deleteOldArticles() {
		newsdb.checkNumberOfArticles(context);
		adapter.getCursor().requery();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Article currentArticle = newsdb.getArticle(adapter.getCursor());

		NewsDatabase.setRead(currentArticle._id);
		// Lesestatus in Datenbank auf gelesen setzen

		adapter.getCursor().requery();
		// evt. vorhandenes Ungelesen-Icon entfernen

		// start intent
		Intent itemintent = new Intent(v.getContext(),
				NewsDetailsActivity.class);
		itemintent.putExtra("title", currentArticle.title);
		itemintent.putExtra("description", currentArticle.description);
		itemintent.putExtra("link", currentArticle.link.toString());
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("d. MMMM y HH:mm");
		// ist automatisch lokalisiert
		
		Date dateObject = new Date(currentArticle.date);
		String dateString = dateFormatter.format(dateObject);

		itemintent.putExtra("date", dateString);
		v.getContext().startActivity(itemintent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.news, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.reload:
			startDownload();

			return true;

		case R.id.allRead:
			newsdb.setAllRead();
			adapter.getCursor().requery();
			return true;

		case R.id.allUnread:
			newsdb.setAllUnread();
			adapter.getCursor().requery();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}
}
