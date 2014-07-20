package de.justproductions.myschoolpocket.news;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import de.justproductions.myschoolpocket.R;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class NewsDetailsActivity extends Activity {
	
	WebView webView;
	Bundle extras;
	Context context;
	
    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        
        context = this;
        
        webView = (WebView) findViewById(R.id.webview);
        
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        
		extras = getIntent().getExtras();
		
        setTitle(extras.getString("title"));
		
		String summary = "<html><head>";
	    summary = summary + "<style>body { background-color:white; font-size:18x; font-family:HelveticaNeue; margin-left:15px; margin-right:15px; } .title { font-size:22px; font-family:Helvetica text-align:justify; } .date { font-size:12px; font-family:Helvetica; color:gray; text-align:right; }</style>";
	    summary = summary + "</head><body>";
	    
	    summary = summary + "<p>";
	    summary = summary + "<b class=\"title\">";
	    summary = summary + extras.getString("title");
	    summary = summary + "</b>";
	    
	    summary = summary + "<div class=\"date\">";
	    summary = summary + extras.getString("date");
	    summary = summary + "</div>";
	    summary = summary + "</p>";
	    
	    String description = extras.getString("description");
	    
	    description = description.replace("<p>", "");
	    description = description.replace("</p>", "");
        description = description.replace(" ", "");

	    description = description.replace("<img", "<p> <img width=\"100%\" height=\"auto\"");
	    // durch das aendern der Breite (with) auf 100% - ist die Breite des Bildes immer an die maximale Breite des Bildschirmes angepasst
	    // -> Im Querformat also gr��er, als im Hochformat
	    
	    description = description.replace("/>", "/> </p>");
	    description = description.replace("float: right;", "");
	    description = description.replace("float: left;", "");
	    
	    summary = summary + description;
	    summary = summary + "</body></html>";
		
		webView.loadDataWithBaseURL(null, summary, "text/html", "utf8", null);
	}
    
	  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	        
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.newsdetails, menu);
	        
	    return true;
	  }
	  
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		Intent intent;
		
		switch (item.getItemId()) {
		case R.id.openBrowser:
			
			Uri uri = Uri.parse(extras.getString("link"));
			intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			
			return true;
			
		case R.id.share:
			
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, extras.getString("link"));
			context.startActivity(Intent.createChooser(shareIntent, getString(R.string.news_share)));
			
			return true;
			
		default:
			return super.onContextItemSelected(item);
		}
	}
    
}
