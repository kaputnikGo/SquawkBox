package UI;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import MAIN.Utilities;


public class SquawkBrowser {
	private static final int SHELL_HEIGHT = 950;
	private static final int SHELL_WIDTH = 860;
	private static final int SHELL_MIN_WIDTH = 320;
	private static final int SHELL_MIN_HEIGHT = 640;
	private SquawkView squawkView;
	private Browser browser;
	private Shell shell;
	
	private static String[] urls;

	private static final int TYPE_UNKNOWN = 0;
	private static final int TYPE_LOCAL = 1;
	private static final int TYPE_WWW_UP = 2;
	private static final int TYPE_WWW_DOWN = 3;
	
	private static final int GET_FORM = 0;
	private static final int SET_FORM = 1;
	
	protected String currentBrowserHtml;
	protected String exportHtml;
	protected String gridOnString;
	protected String gridOffString;
	
	protected boolean initPhase = false;
	protected boolean gridTog = true;
	
	public SquawkBrowser(final SquawkView squawkView) {
		// constructor
		this.squawkView = squawkView;		
	    shell = new Shell(this.squawkView.getDisplay());
	    shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
	    shell.setMinimumSize(SHELL_MIN_WIDTH, SHELL_MIN_HEIGHT);
	    
	    Monitor primary = this.squawkView.getDisplay().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    int posx = bounds.width - rect.width;
	    int posy = bounds.y + (bounds.height - rect.height) / 2; 
	    shell.setLocation(posx, posy);
	    
	    shell.setText("Squawk Box Browser");
	    shell.setLayout(new GridLayout());
	    Image icon = new Image(this.squawkView.getDisplay(), getClass().getClassLoader().getResourceAsStream("resources/plug_icon.png"));
	    shell.setImage(icon);
	    
		shell.addShellListener(new ShellListener() {
			public void shellClosed(ShellEvent e) {
				debug("Browser shell closed.");
				squawkView.browserClosed();
			}

			@Override
			public void shellActivated(ShellEvent arg0) {
				//
			}

			@Override
			public void shellDeactivated(ShellEvent arg0) {
				//
			}

			@Override
			public void shellDeiconified(ShellEvent arg0) {
				//
			}

			@Override
			public void shellIconified(ShellEvent arg0) {
				//
			}
		});
	}

/************************************************************
* 
* 		// public methods 
* 
************************************************************/	
	public boolean initBrowser() {
		try {			
			Composite comp = new Composite(shell, SWT.NONE);
			GridData data = new GridData(GridData.FILL_BOTH);
			comp.setLayoutData(data);
			comp.setLayout(new FillLayout());
			final SashForm form = new SashForm(comp, SWT.HORIZONTAL);
			form.setLayout(new FillLayout());

			// SWT.MOZILLA is slow (old version) and no source view
			debug("SWT version: " + SWT.getVersion());
			browser = new Browser(form, SWT.NONE);	
			initPhase = true;			
			debug("Squawk Box Browser opened with type: " + browser.getBrowserType() +  " and js: " + browser.getJavascriptEnabled() + " - ");
		    // browser title grab
		    browser.addTitleListener(new TitleListener() {
		    	public void changed(TitleEvent event) {
		    		shell.setText("Squawk Box Browser - " + event.title);
		    	}
		    });
			
		    browser.addProgressListener(new ProgressListener() {
		    	public void changed(ProgressEvent event) {
		    		//
		    	}
		    	public void completed(ProgressEvent event) {
		    		debug("browser processing complete reached.");
		    		searchBrowserContent();
		    		
		    		if (!initPhase) {				
		    			iterateFormFields(GET_FORM);
		    			squawkView.updateFormFieldsDisplay();
		    		}
		    	}
		    });		   
		    
			shell.open();	
			// grid blue thing
			gridOnString = Utilities.DESIGN_GRID_ON;
			gridOffString = Utilities.DESIGN_GRID_OFF;
		}
		catch (SWTError e) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Browser cannot be initialised.");
			messageBox.setText("Exit");
			messageBox.open();
			return false;
		}		
		return true;
	}
	public void openInitPage(final String userString) {
		// this opens the generic template for a given webcode	
		debug("resource url: " + userString);
		browser.setUrl(userString);
		
	}
	
	public void openTemplatePage(final String userString) {
		//TODO
		// this opens the generic template for a given webcode
		// change to load the url to a var for editing in squawk.		
		initPhase = false;
		debug("resource url: " + userString);
		browser.setUrl(userString);
	}
	
	public void startComponents() {
		//TODO
		initPhase = false;
		// clear formfields in case templates were used
		squawkView.clearFormFields();
		browser.setText(Utilities.returnComponentFileHtml("head.html"));
		searchBrowserContent();
		debug("browser window cleared with head.html");
	}
	
	public void addComponentToPage(final String userComponentName) {
		// append a component to current existing browser page.
		// getText append to text
		URL componentFile = getClass().getClassLoader().getResource("templates/EMAIL/components/" + userComponentName);		
		if (componentFile == null) {
			debug("Component file not found: " + userComponentName);
			return;
		}		
		//
		String currentPage = browser.getText();
		currentPage += Utilities.returnComponentFileHtml(userComponentName);
		browser.setText(currentPage);
	}
	
	public void openWebpage(final String userString) {
		int type = TYPE_UNKNOWN;
		debug("Checking url string: " + userString);
		if (Utilities.checkString(userString)) {
			type = checkWebpageType(userString);
		}
		else {
			debug("Not a valid url string: " + userString);
			type =  TYPE_UNKNOWN;
		}
		debug("url type: " + type);
		switch (type) {
			case 0: // unknown
					unknownPagetype(userString);
					break;
			case 1: // local html page
					loadLocalWebpage(userString); 
					break;
			case 2: // www page up
					loadWWWpage(userString);
					break;
			case 3: // www page down
					websiteNoConnection(userString);
					break;
			default: // ummmm.
					break;					
		}
	}
	
	public void openLocalDirFile() {
		DirectoryDialog dialog = new DirectoryDialog(shell);
		String folder = dialog.open();
		if (folder == null) return;
		File file = new File(folder);
		File[] files = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".html") || name.endsWith(".htm");
			}
		});
		if (files.length == 0) return;
		urls = new String[files.length];
		//int index = 0;
		for (int i = 0; i < files.length; i++) {
			try {
				String url = files[i].toURI().toURL().toString();
				urls[i] = url;
			}
			catch (MalformedURLException ex) {
				debug ("bad url.");
			}
		}
		if (urls.length > 0) {
			browser.setUrl(urls[0]);
			debug("local file opened: " + urls[0]);
		}		
	}
	
	public void processBrowser() {
		debug("processBrowser called.");
		iterateFormFields(SET_FORM);
	}
	
	public void openEmailTemplate() {
		debug("opening email responsive template...");
		// get deviceHtml
		URL emailFile = getClass().getClassLoader().getResource("templates/EMAIL/Component_template-v2-responsive.html");
		if (emailFile == null) 
			debug("Email template file not found.");
		else
			openTemplatePage(emailFile.toString());
	}
	
	public String getBrowserHtml() {
		return browser.getText();
	}
	
	public void setBrowserText(String message) {
		browser.setText("<html><body><br />" + message + "  <br /></body></html>");		
	}
	
	public void dumpToBrowser(final String htmlDump) {
		debug("html dump to browser called.");
		browser.setText(htmlDump);
	}
	
	public void exportBrowser(final String savename) {
		// yar
		// load the var with the browser html first
		exportHtml = browser.getText();
		
        if (!saveTemplateFile(saveDialog(savename))) { 
            debug("exportHtml save fail with name: " + savename);
        	return; 
        } 
        else {
        	debug ("exportHtml saved with name: " + savename);
        }
	}
	
	public void toggleGrid() {
		// dem blue lines all over the place...
	    debug("toggle called...");
	    boolean result;
	    //TODO
	    // not working for the component view
	    try {
	    
		    if (!gridTog) {
		    	result = browser.execute(gridOnString);
		    }
		    else {
		    	result = browser.execute(gridOffString);
		    }
		    if (!result) {
		    	debug("browser failed to execute toggle grid append script.");		    	
		    }
		    //flip it
		    gridTog = !gridTog;
		    
		    //regardless of state, refresh browser, one way or another
		    currentBrowserHtml = browser.getText();
		    browser.setText(currentBrowserHtml);		    
		    //browser.refresh();		    
		} catch (SWTException e) {
			// caused by js returning an error.
			debug("toggle error: " + e);
		}
	}
	
	public void executeJavascript(String javascript) {		
		boolean result = executeJavascriptAndWait(javascript);
		if (result) debug("Browser execute js success.");
		else
			debug("Browser execute js fail.");
	}
	
	public void refreshBrowser() {
		searchBrowserContent();
		if (!initPhase) {				
			iterateFormFields(GET_FORM);
			squawkView.updateFormFieldsDisplay();
		}
	}

/************************************************************
* 
* 		// internal methods 
* 
************************************************************/
	private void debug(final String message) {
		squawkView.updateConsole(message);
	}
	
	private String saveDialog(final String savename) {
		// this will save with correct extension...even though can't see it in dialog.
		FileDialog dialog = new FileDialog(shell, SWT.SAVE); 
		dialog.setFilterNames(new String[] {"HTML Files"});
		dialog.setFilterExtensions(new String[] {"*.html"});
		dialog.setFilterPath("c:\\"); // windows...
		dialog.setFileName(savename + ".html");
		dialog.setOverwrite(true);
		debug("Save dialog open...");
		
		String filename = dialog.open();
		return filename;
	}
	
	private boolean saveTemplateFile(final String fileName) {
		if (fileName == null || fileName == "") {
			debug("fileName is null or empty.");
			return false;
		}
        final File outputFile = new File(fileName);	
        try {
			FileWriter fw = new FileWriter(outputFile);
			fw.write(exportHtml);
			fw.close();
			debug("device saved.");
			return true;
			
		} catch (IOException e) {
			debug("Save device to file failed.");
			e.printStackTrace();
		}		
		return false;
	}
	
	
	private int checkWebpageType(final String urlCandidate) {
		// need to check if a local page or a www
		if (Utilities.isValidUrl(urlCandidate)) {
			// is a website
			if (Utilities.checkWebsiteUp(urlCandidate)) {
				return TYPE_WWW_UP;
			}
			else {
				return TYPE_WWW_DOWN;
			}
		}
		else {
			//possibly a local html page
			return TYPE_LOCAL;
		}		
	}
	
	// set some sort of html here, then open browser - Unicode only for string
	private void unknownPagetype(final String unknown) {
		// case of string not resolving as www or local html
		browser.setText("<html><body><br />The requested url is of unknown type: " + unknown + "  <br /></body></html>");
	}
	
	private void loadLocalWebpage(final String localpage) {
		// is on local machine, hopefully an html file....
		browser.setText("<html><body><br />Loading local webpage... <br /></body></html>");
		// then...
		browser.setUrl(localpage);
	}
	
	private void loadWWWpage(final String wwwpage) {
		// load it up kiddo...
		browser.setText("<html><body><br />Fetching webpage... <br /></body></html>");
		//then...
		browser.setUrl(wwwpage);
	}
	
	private void websiteNoConnection(final String userString) {
		// Utilities said: HttpURLConnection.HTTP_OK == false;
		// reasons list:
		browser.setText("<html><body><br />Error fetching webpage: " + userString + " <br /></body></html>");
		// 1. your connection to wan
		// 2. isp thru
		// 3. website down... get error code.
	}
	

/************************************************************
* 
* 		// browser parsing 
* 
************************************************************/	
	
	private void searchBrowserContent() {
		// after user loads component,
		// parse the browser html and get all div id="#NAME#"
		debug("searchBrowserContent called...");		
		List<String> formFieldNames = new ArrayList<String>();
		List<String> formFieldContent = new ArrayList<String>();
		currentBrowserHtml = "";
		currentBrowserHtml = browser.getText();
		// look for str "# and #" with a word in between...
		String startToken = "id=\"#";
		String endToken = "#\"";
		String closingTag = "</span>";
		String content;
		
		// add title, is between <head> and </head>
		formFieldNames.add("title");
		formFieldContent.add(patternMatchTitle());
		
		// look for image urls
		Pattern pattern = Pattern.compile(startToken + "(.*?)" + endToken);
		Matcher matcher = pattern.matcher(currentBrowserHtml);
		String tag;
		while (matcher.find()) {
			// here remove the id= part
			tag = matcher.group().toString();
			tag = tag.substring(4, tag.length()-1);
			formFieldNames.add(tag);
			
			if (matcher.group().contains("URL")) {
				formFieldContent.add(patternMatchImageTag(matcher.group()));
			}
			else {
				// trawl for chars until closingTag
				// account for closing angle bracket
				content = currentBrowserHtml.substring(matcher.end() + 1, 
						currentBrowserHtml.indexOf(closingTag, matcher.end() + 1));
				// maybe needs a strip tabs
				content = stripFormatting(content);
				formFieldContent.add(content);
			}
		}
		squawkView.updateFormFieldMap(formFieldNames, formFieldContent);
	}
	
	private String patternMatchTitle() {
		String title = "default title";
		String startToken = "<title>";
		String endToken = "</title>";
		
		Pattern pattern = Pattern.compile(startToken + "(.*?)" + endToken);
		Matcher matcher = pattern.matcher(currentBrowserHtml);
		
		if (matcher.find()) {
			title = matcher.group();
			// strip the tokens from it
			title = title.substring(title.indexOf(startToken) + startToken.length(), title.indexOf(endToken));
		}
		debug("patternMatch title: " + title);
		return title;
	}
	
	private String patternMatchImageTag(final String imageTag) {
		// imageTagContent starts with any imageTag (#NAMEURL#) and ends with either ">" or "/>" 
		// can include styling elements too.
		if (imageTag == null || imageTag == "") return "no image tag";
		
		String imageTagContent = "";
		String startToken = imageTag;
		String endToken = ">";
		
		Pattern pattern = Pattern.compile(startToken + "(.*?)" + endToken);
		Matcher matcher = pattern.matcher(currentBrowserHtml);
		if (matcher.find()) {
			imageTagContent = matcher.group();
			// strip the tokens from it
			imageTagContent = imageTagContent.substring(
					imageTagContent.indexOf(startToken) + startToken.length(), 
					imageTagContent.indexOf(endToken));
		}
		
		return imageTagContent;
	}
	
	private void iterateFormFields(int type) {
		// CHECK THE SPELLING & CASE OF THE ELEMENT ID!!
		
		//TODO
		// html entities and special chars are gobbleygook
		
		Iterator<Entry<String, String>> entries = squawkView.FORM_FIELDS.entrySet().iterator();
		String key;
		String value;
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>)entries.next();			
			key = entry.getKey();
			value = entry.getValue();
			if (type == GET_FORM) {
				// get the div id data and add to the Map
				if (key.equals("title")) {
					value = (String)browser.evaluate("return document.title");
				}
				else {
					value = (String)browser.evaluate("return document.getElementById('" + key + "').innerHTML;");
				}				
			}
			else if (type == SET_FORM) {
				// set the div id data from the Map				
				value = stripFormatting(value);
				value = replaceWithHtmlEntity(value);
				boolean done = false;
				String changer = "";
				
				if (key.equals("title")) {
					changer = "document.title = \"" + value + "\";";
					done = false;
					try {
						done = browser.execute(changer);
					}
					catch (SWTError e) {
						debug("browser execute error: " + e.getMessage());
					}
					if (!done) {
						debug("execute fail for key title: " + value + ", possibly due to special chars - use html entities only.");
					}					
				}
				else {									
					changer = "document.getElementById('" + key + "').innerHTML = '" + value + "';";					
					done = false;
					try {
						done = browser.execute(changer);
					}
					catch (SWTError e) {
						debug("browser execute error: " + e.getMessage());
					}					
					if (!done) {
						debug("execute innerHTML fail for " + key + ", possibly due to special chars - use html entities only.");
					}
				}
			}
		}
	}
	
	/*
	  
	//not yet...
	  
	private void changeSingleFormField(String key, String newValue) {
		// alt method of key based search
		if (squawkView.FORM_FIELDS.containsKey(key)) {
			squawkView.FORM_FIELDS.put(key, newValue);
		}
	}
	*/
	
	private String stripFormatting(String original) {
		String changed = original;
		changed = changed.replaceAll("\n", "");
		changed = changed.replaceAll("\t", "");
		changed = changed.replaceAll("\r", ""); 
		
		return changed;
	}
	
	private String replaceWithHtmlEntity(String original) {
		// may need to only look for ' as it is also the closing element for js
		String changed = original;
		changed = changed.replaceAll("'", "&rsquo;");
		changed = changed.replaceAll("’", "&rsquo;");		
		return changed;
	}
	
	/*
	private void addTemplateHeadCSS(String cssLine) {		
		// eg. additions/rewrites to <head><style> - may not work...		
		//browser.execute("document.write(\"<style>body { background-color:#000 }</style>\");");
	}
	*/
	
	private static Pattern JAVASCRIPT_LINE_COMMENT_PATTERN = Pattern.compile("^^\\s*//.*$", Pattern.MULTILINE);
	private boolean executeJavascriptAndWait(String javascript) {
		javascript = JAVASCRIPT_LINE_COMMENT_PATTERN.matcher(javascript).replaceAll("");
		return browser.execute(javascript);
	}
}
