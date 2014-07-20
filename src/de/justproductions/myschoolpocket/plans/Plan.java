package de.justproductions.myschoolpocket.plans;

public class Plan {

	public String title;
	public String url;
	public boolean isHeader;
	
	public Plan(String title, String url, boolean isHeader) {
		this.title = title;
		this.url = url;
		this.isHeader = isHeader;
	}
}
