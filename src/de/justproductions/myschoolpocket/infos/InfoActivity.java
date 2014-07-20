package de.justproductions.myschoolpocket.infos;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.justproductions.myschoolpocket.R;

public class InfoActivity extends ListActivity {
	
	public static ListAdapter listAdapter;
	private static ArrayList<Info> items = new ArrayList<Info>(); // String gen�gt
	static SharedPreferences prefs;
	static String[] actions;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		listAdapter = new ListAdapter();
		setListAdapter(listAdapter);

		actions = new String[] {
				"header",
				getString(R.string.info_call),
				getString(R.string.info_web),
				getString(R.string.info_mail),
				getString(R.string.info_navigation)
		};
		
		loadSources();
	}
	
	public class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Info currentItem = items.get(position);
			LayoutInflater inflater = LayoutInflater.from(getBaseContext());
			
			if (currentItem.title == "header") {
				convertView = inflater.inflate(R.layout.list_item_header, parent,
						false);
				
				TextView sectionView = (TextView) convertView
						.findViewById(R.id.list_item_section_text);
				sectionView.setText(currentItem.description);

				convertView.setOnClickListener(null);
				convertView.setOnLongClickListener(null);
				convertView.setLongClickable(false);
			} else {
				final Info currentInfo = currentItem;
				convertView = inflater.inflate(R.layout.list_item_info, parent, false);
				
				final ImageView image = (ImageView)convertView.findViewById(R.id.list_item_entry_icon);
				image.setImageResource(getResources().getIdentifier(currentInfo.icon, "drawable", getPackageName()));

				final TextView title = (TextView)convertView.findViewById(R.id.list_item_entry_title);
				title.setText(currentInfo.title);

				final TextView subtitle = (TextView)convertView.findViewById(R.id.list_item_entry_summary);
				subtitle.setText(currentInfo.description);
			}
			
			return convertView;
		}
    }
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		final TextView title = (TextView)v.findViewById(R.id.list_item_entry_title);
		
		if (title.getText() == getString(R.string.info_call)) {
			v.getContext().startActivity(new Intent(android.content.Intent.ACTION_DIAL,Uri.parse("tel:" + prefs.getString("phone_number", null))));
			// ACTION_DIAL -> Nummer in der Telefon App anzeigen
			// ACTION_CALL -> direkt anrufen (benoetigt "Uses Permission" CALL_PHONE in Manifest)
		} else if (title.getText() == getString(R.string.info_web)) {
			Uri uri = Uri.parse(prefs.getString("website", null));
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			v.getContext().startActivity(intent);
		} else if (title.getText() == getString(R.string.info_mail)) {
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			
		 	String[] recipients = new String[]{prefs.getString("email_address", null)};
		 	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
		 	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		 	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, R.string.info_mailSignature);
		 	emailIntent.setType("vnd.android-dir/");
		 
		 	startActivity(Intent.createChooser(emailIntent, "Choose App to send Mail:"));
		} else if (title.getText() == getString(R.string.info_navigation)) {
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + prefs.getString("address", null)));
			startActivity(i);
		}
    }
	
	public static void loadSources() {
		items.clear();
		
		String[] descriptions = new String[] {
				prefs.getString("school_name", null),
				prefs.getString("phone_number", null),
				prefs.getString("website", null),
				prefs.getString("email_address", null),
				prefs.getString("address", null)
		};
		
		String[] icons = new String[] {
				"",
				"ic_info_call",
				"ic_info_website",
				"ic_info_mail",
				"ic_info_navigate"
		};
		
		int position = 0;
		
		for (String action : actions) {
			items.add(new Info(icons[position], action, descriptions[position]));
			position++;
		}
	}
}
