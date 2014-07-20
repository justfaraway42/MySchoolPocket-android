package de.justproductions.myschoolpocket.plans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import de.justproductions.myschoolpocket.main.ExceptionHandler;
import de.justproductions.myschoolpocket.main.TabLayoutController;

@SuppressLint("NewApi")
public class PlansDownload extends AsyncTask<String, String, String> {

	String savepath;
	Context context;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		PlansActivity.progressDialog.setProgress(0);

		new File(PlansActivity.filepath).mkdirs();
	}

	@Override
	protected String doInBackground(String... downloadURL) {
		int count;
		
		InputStream input = null;
		OutputStream output = null;
		
		try {

			URL url = new URL(downloadURL[0]);
			URLConnection conexion = url.openConnection();
			conexion.connect();

			int lenghtOfFile = conexion.getContentLength();

			PlansActivity.progressDialog.setMax(lenghtOfFile);
			PlansActivity.progressDialog.incrementProgressBy(1024);

			input = new BufferedInputStream(url.openStream());

			String fileName = downloadURL[0].substring(downloadURL[0]
					.lastIndexOf('/') + 1);
			savepath = PlansActivity.filepath + fileName;

			output = new FileOutputStream(savepath);

			byte data[] = new byte[1024];

			long total = 0;

			while ((count = input.read(data)) != -1 && !this.isCancelled()) {
				total += count;
				publishProgress("" + (int) ((total * 100) / lenghtOfFile)
						* 1024);
				output.write(data, 0, count);
			}
			
		} catch (Exception e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		} finally {
			try {
				output.flush();
			} catch (IOException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
			try {
				output.close();
			} catch (Exception e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
			try {
				input.close();
			} catch (Exception e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		PlansActivity.progressDialog.setProgress(Integer.parseInt(progress[0]));
	}

	@Override
	protected void onPostExecute(String unused) {

		updateAdapters();
		
		if (TabLayoutController.appInForeground()) {
			PlansActivity.progressDialog.dismiss();
			PlansActivity.openPDF(new File(savepath), PlansActivity.progressDialog.getContext());
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		new File(savepath).delete();
		updateAdapters();
		PlansActivity.progressDialog.dismiss();
	}

	private void updateAdapters() {
		if (TabLayoutController.tabHost.getCurrentTab() == 1) {
			PlansActivity.listAdapter.notifyDataSetChanged();
		} else if (TabLayoutController.tabHost.getCurrentTab() == 2) {
			PlansActivity.listAdapter2.notifyDataSetChanged();
		}
	}
}
