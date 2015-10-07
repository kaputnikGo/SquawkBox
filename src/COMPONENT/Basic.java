package COMPONENT;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MAIN.Utilities;

public class Basic {
	
	private boolean isEditable;
	private String displayName;
	private String htmlContent;
	private Map<String, String> span_map;
	
	public Basic() {
		// default constructor
		// need to account for a component with NO editable regions, maybe just an html filler
		isEditable = false;
		displayName = "empty component";
		htmlContent = "empty html content";
		span_map = new LinkedHashMap<String, String>();
		span_map.put(displayName, htmlContent);
	}
	
	public Basic(int id, boolean isEditable, String displayName, String htmlContent, Map<String, String> span_map) {
		// overloaded constructor
		this.isEditable = isEditable;
		this.displayName = displayName;
		this.htmlContent = htmlContent;	
		this.span_map = new LinkedHashMap<String, String>(span_map);
	}

// getters	
	public boolean getIsEditable() {
		return isEditable;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
// setters	
	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}
	public void setDisplayName(String displayName) {
		if (Utilities.checkString(displayName)) this.displayName = displayName;
	}
	public void setHtmlContent(String htmlContent) {
		if (Utilities.checkString(htmlContent)) this.htmlContent = htmlContent;
	}
	public void setSpanMap(Map<String, String>span_map) {
		if(!isEditable) 
			return;
		if (Utilities.checkMapEmpty(span_map)) 
			return;
		else {
			this.span_map.clear();
			this.span_map = new LinkedHashMap<String, String>(span_map);
		}
	}	
// others
	public boolean addNewEditableSpan(String newSpanName, String newSpanContent) {
		if (!isEditable) 
			return false;
		if (Utilities.checkString(newSpanName)) {
			if (Utilities.checkString(newSpanContent)) {
				span_map.put(newSpanName, newSpanContent);
				return true;
			}
		}		
		return false;
	}
	public String getContentForSpan(String spanName) {
		if (!isEditable) 
			return null;
		if (Utilities.checkString(spanName)) {
			for (String key : span_map.keySet()) {
				if (key.equals(spanName)) {
					return span_map.get(key);
				}
			}
		}		
		return null;
	}
	public boolean checkForNewSpans() {
		// user has edited htmlContent and added a new span id (hopefully in the correct format)
		// assume that at present only user can manually add them into html directly, not through the forms
		// poll htmlContent for ALL spans
		if (!isEditable) 
			return false;
		List<String> spanNames = new ArrayList<String>();
		List<String> spanContent = new ArrayList<String>();
		
		String startToken = "id=\"#";
		String endToken = "#\"";
		String closingTag = "</span>";
		int spanCount = 0;

		Pattern pattern = Pattern.compile(startToken + "(.*?)" + endToken);
		Matcher matcher = pattern.matcher(htmlContent);
		String tag;
		while (matcher.find()) {
			// here remove the id= part
			tag = matcher.group().toString();
			tag = tag.substring(4, tag.length()-1);
			spanNames.add(tag);
			// trawl for chars until closingTag
			// account for closing angle bracket
			spanContent.add(htmlContent.substring(matcher.end() + 1, 
					htmlContent.indexOf(closingTag, matcher.end() + 1)));
			spanCount++;
		}
		if (spanCount == span_map.size()) 
			return false;
		else {
			// it would make sense to add them huh...
			int i = 0;
			for (String key : spanNames) {
				span_map.putIfAbsent(key, spanContent.get(i));
				i++;
			}
		}		
		return true;
	}
	
	public boolean checkForHtmlEntities() {
		boolean check = false;
		htmlContent = htmlContent.replaceAll("&#x02018;", "&lsquo;");		
		htmlContent = htmlContent.replaceAll("&#x02019;", "&rsquo;");		
		htmlContent = htmlContent.replaceAll("&#x0201C;", "&ldquo;");		
		htmlContent = htmlContent.replaceAll("&#x0201D;", "&rdquo;");		
		htmlContent = htmlContent.replaceAll("&#x02026;", "&hellip;");
		htmlContent = htmlContent.replaceAll("&#x02010;", "&ndash;");				
		htmlContent = htmlContent.replaceAll("&#x02014;", "&mdash;");		
		htmlContent = htmlContent.replaceAll("&#x000A9;", "&copy;");		
		htmlContent = htmlContent.replaceAll("&#x02022;", "&bull;");
		return check;
	}
}