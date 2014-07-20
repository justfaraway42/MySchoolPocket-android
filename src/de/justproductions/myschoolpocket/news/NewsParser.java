package de.justproductions.myschoolpocket.news;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import de.justproductions.myschoolpocket.main.ExceptionHandler;


public class NewsParser extends DefaultHandler {
	
	private Article currentArticle = new Article();
	// zum tempor�ren speichern eines Artikels
	
	private NewsDatabase db = null;
	
	private Context context;

	StringBuffer chars = new StringBuffer();

	
	/**
	 * This is the entry point to the parser and creates the feed to be parsed
	 * 
	 * @param feedUrl
	 * @return 
	 * @return
	 */
	public void getLatestArticles(Context ctx, String feedUrl) {
		context = ctx;
		URL url = null;
		
		try {
			db = new NewsDatabase(context);

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			url = new URL(feedUrl);
			
			xr.setContentHandler(this);
			xr.parse(new InputSource(url.openStream()));


		} catch (IOException e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		} catch (SAXException e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		} catch (ParserConfigurationException e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) {
		chars = new StringBuffer();
	}

	@Override
	public void characters(char ch[], int start, int length) {
		// wird jedes mal aufgerufen, wenn eine Zeichenkette geparst wurde
		// blockweise -> deswegen: append
		chars.append(new String(ch, start, length));
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (localName.equalsIgnoreCase("title")) {
			currentArticle.title = (chars.toString());
		}
		
		else if (localName.equalsIgnoreCase("description")) {
			currentArticle.description = (chars.toString());
		}
		
		else if (localName.equalsIgnoreCase("link")) {
			try {
				currentArticle.link = (new URL(chars.toString()));
			} catch (MalformedURLException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
		}
		
		else if (localName.equalsIgnoreCase("pubDate")) {
			
			try {

				SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
				Date dateObject = dateFormatter.parse(chars.toString());
				
				long time = dateObject.getTime();
				
				currentArticle.date = time;
			
			} catch (ParseException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
		}

		if (localName.equalsIgnoreCase("item")) {

			try {
				db.insertArticle(currentArticle.title, currentArticle.description, currentArticle.link, currentArticle.date);
			} catch (ParseException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}

			currentArticle = new Article();
		}
	}
	
	@Override
	public void endDocument() {
		//db.close();
	}

}
