package de.justproductions.myschoolpocket.school;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

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


public class SchoolParser extends DefaultHandler {

	StringBuffer chars = new StringBuffer();
	
	School currentSchool;
	SchoolDatabase db = new SchoolDatabase();
	
	private Context context;
	
	public void get(Context ctx, InputStream inputStream) {
		context = ctx;
		
		try {

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			
			xr.setContentHandler(this);
			xr.parse(new InputSource(inputStream));


		} catch (IOException e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		} catch (SAXException e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		} catch (ParserConfigurationException e) {
			ExceptionHandler.makeExceptionAlert(context, e);
		}
	}
	
	@Override
	public void startDocument() {
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) {
		chars = new StringBuffer();
		
		if (localName == "state") { // <localName QName(0)="value(0)" [...]/>
			try {
				db.insertState(atts.getValue(0));
			} catch (ParseException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
		}
		
		if (localName == "community") {
			try {
				db.insertCommunity(atts.getValue(0));
			} catch (ParseException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
		}
		
		if (localName == "school") {
			currentSchool = new School();
			currentSchool.name = atts.getValue(0);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {
		chars.append(new String(ch, start, length));
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (localName.equalsIgnoreCase("feed")) {
			try {
				currentSchool.feed = new URL (chars.toString());
			} catch (MalformedURLException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
		}
		if (localName.equalsIgnoreCase("timetables_urls")) {
			currentSchool.timetables_urls = chars.toString();
		}
		if (localName.equalsIgnoreCase("timetables_names")) {
			currentSchool.timetables_names = chars.toString();
		}
		if (localName.equalsIgnoreCase("representation_urls")) {
			currentSchool.representation_urls = chars.toString();
		}
		if (localName.equalsIgnoreCase("timetables_classes")) {
			currentSchool.timetables_classes = chars.toString();
		}
		if (localName.equalsIgnoreCase("phone_number")) {
			currentSchool.phone_number = chars.toString();
		}
		if (localName.equalsIgnoreCase("website")) {
			try {
				currentSchool.website = new URL(chars.toString());
			} catch (MalformedURLException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
		}
		if (localName.equalsIgnoreCase("email_address")) {
			currentSchool.email_address = chars.toString();
		}
		if (localName.equalsIgnoreCase("address")) {
			currentSchool.address = chars.toString();
		}
		
		if (localName.equalsIgnoreCase("school")) { // </school>!
			try {
				db.insertSchool(currentSchool);
			} catch (ParseException e) {
				ExceptionHandler.makeExceptionAlert(context, e);
			}
		}
	}
	
	@Override
	public void endDocument() {
		//db.close();
	}

}
