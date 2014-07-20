package de.justproductions.myschoolpocket.beta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.justproductions.myschoolpocket.R;

public class BetaActivity extends Activity {

	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beta);

		context = this;
		
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		final boolean surveyCompleted = preferences.getBoolean("surveyCompleted", false);
		
		Button startSurveyButton = (Button) findViewById(R.id.startSurveyButton);
		TextView betaText = (TextView) findViewById(R.id.betaTextView);
		
		if (surveyCompleted) {
			betaText.setText("Danke f�r Dein Feedback!\n\nVergiss nicht die App zu laden, sobald sie verf�gbar ist. :)");
			startSurveyButton.setVisibility(View.INVISIBLE);
		} else {
			betaText.setText("Du kannst die Umfrage zur App nur einmal ausf�llen.\n\nNutze die App deshalb intensiv �ber einen l�ngeren Zeitraum und nehme Dir vorher etwas Zeit bevor Du die Umfrage startest.");
			
		startSurveyButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("Willst Du die Umfrage wirklich jetzt starten?")
						.setCancelable(false)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								
								startActivity(new Intent(getBaseContext(), BetaFormActivity.class));
								
								SharedPreferences.Editor editor = preferences.edit();
								editor.putBoolean("surveyCompleted", true);
								editor.commit();
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
				}
			});
		}
	}
}
