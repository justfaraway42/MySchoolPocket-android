package de.justproductions.myschoolpocket.beta;

import de.justproductions.myschoolpocket.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

public class BetaFormActivity extends Activity {
	
	WebView webView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle("Feedbackbogen");
		setContentView(R.layout.webview);
		
        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("http://www.eurogymnasium-waldenburg.de/beta.php");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

}
