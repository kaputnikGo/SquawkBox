import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class SquawkBrowser {
	private static final int SHELL_HEIGHT = 950;
	private static final int SHELL_WIDTH = 860;
	private boolean DEBUG = true;
	private SquawkView squawkView;
	private Browser browser;
	private Shell shell;
	
	private ToolBar toolbar;
	private ToolItem openButton;
	private static String[] urls;
	
	private static final int TYPE_UNKNOWN = 0;
	private static final int TYPE_LOCAL = 1;
	private static final int TYPE_WWW_UP = 2;
	private static final int TYPE_WWW_DOWN = 3;
	
	protected String userByline = "default byline";
	protected String userHeading = "default heading";
	protected String userPara1 = "default user paragraph 1";
	protected String userPara2 = "default user paragraph 2";
	protected String userPara3 = "default user paragraph 3";
	protected int userCoverWidth = 180;
	protected int userCoverHeight = 227;
	protected String userReportUrl = "http://www.moneymorning.com.au/wp-content/uploads/2015/04/Top10MiningStocks-cover-sml.png";
	protected String exportedDeviceHtml;	
	
	SquawkBrowser(SquawkView squawkView) {
		// constructor
		this.squawkView = squawkView;
	    shell = new Shell(this.squawkView.getDisplay());
	    shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
	    
	    Monitor primary = this.squawkView.getDisplay().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    int posx = bounds.width - rect.width;
	    int posy = bounds.y + (bounds.height - rect.height) / 2; 
	    shell.setLocation(posx, posy);
	    
	    shell.setText("Squawk Browser");
	    shell.setLayout(new GridLayout());
	    Image icon = new Image(this.squawkView.getDisplay(), getClass().getClassLoader().getResourceAsStream("resources/plug_icon.png"));
	    shell.setImage(icon);
	    
	    Composite compTools = new Composite(shell, SWT.NONE);
	    GridData data = new GridData(GridData.FILL_HORIZONTAL);
	    compTools.setLayoutData(data);
	    compTools.setLayout(new GridLayout(2, false));
	    
	    toolbar = new ToolBar(shell, SWT.BORDER | SWT.FLAT);
	    openButton = new ToolItem(toolbar, SWT.PUSH);
	    openButton.setText("open");	
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
			
			browser = new Browser(form, SWT.NONE);			    
		    // browser title grab
		    browser.addTitleListener(new TitleListener() {
		    	public void changed(TitleEvent event) {
		    		shell.setText("Squawk Browser : " + event.title);
		    	}
		    });
			
		    browser.addProgressListener(new ProgressListener() {
		    	public void changed(ProgressEvent event) {
		    		//
		    	}
		    	public void completed(ProgressEvent event) {
		    		parseDevice();
		    	}
		    });
		    
			// open file button
		    openButton.addListener(SWT.Selection, new Listener() {
		    	public void handleEvent(Event e) {
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
		    		}
		    	}
		    });
		    
			shell.open();
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
	
	public void openResourcePage(String userString) {
		// ONLY called when opening a template dir/file from the template folders
		debug("resource url: " + userString);
		browser.setUrl(userString);
		userString = null;
	}
	
	public void openWebpage(String userString) {
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
	
	public void processBrowser() {		
		browser.refresh();
	}
	
	public String getDeviceHtml() {
		return browser.getText();
	}
	
	public void exportDevice(String savename) {
		// yar
		exportedDeviceHtml = browser.getText();
		// save it somewhere for ultimate zip and send
		//System.out.println(exportedDeviceHtml);

		boolean saveDevice = (saveDeviceFile(saveDialog(savename)));
		
        if ( saveDevice == false) { 
            debug("device save fail with name: " + savename);
        	return; 
        } 
        else {
        	debug ("device saved with name: " + savename);
        }
	}


/************************************************************
* 
* 		// internal methods 
* 
************************************************************/
	protected void debug(String message) {
		if (DEBUG) squawkView.updateConsole(message);
	}
	
	private String saveDialog(String savename) {
		// this will save with correct extension...even though can't see it in dialog.
		FileDialog dialog = new FileDialog(shell, SWT.SAVE); 
		dialog.setFilterNames(new String[] {"Text Files", "HTML Files"});
		dialog.setFilterExtensions(new String[] { "*.txt", ".html"});
		dialog.setFilterPath("c:\\"); // windows...
		dialog.setFileName(savename + ".txt");
		dialog.setOverwrite(true);
		debug("Save dialog open...");
		
		String filename = dialog.open();
		return filename;
	}
	
	private boolean saveDeviceFile(String fileName) {
		if (fileName == null || fileName == "") {
			debug("fileName is null or empty.");
			return false;
		}
        final File outputFile = new File(fileName);	
        try {
			FileWriter fw = new FileWriter(outputFile);
			fw.write(exportedDeviceHtml);
			fw.close();
			debug("device saved.");
			return true;
			
		} catch (IOException e) {
			debug("Save device to file failed.");
			e.printStackTrace();
		}		
		return false;
	}
	
	private int checkWebpageType(String urlCandidate) {
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
	private void unknownPagetype(String unknown) {
		// case of string not resolving as www or local html
		browser.setText("<html><body><br />The requested url is of unknown type: " + unknown + "  <br /></body></html>");
	}
	
	private void loadLocalWebpage(String localpage) {
		// is on local machine, hopefully an html file....
		browser.setText("<html><body><br />Loading local webpage... <br /></body></html>");
		// then...
		browser.setUrl(localpage);
	}
	
	private void loadWWWpage(String wwwpage) {
		// load it up kiddo...
		browser.setText("<html><body><br />Fetching webpage... <br /></body></html>");
		//then...
		browser.setUrl(wwwpage);
	}
	
	private void websiteNoConnection(String userString) {
		// Utilities said: HttpURLConnection.HTTP_OK == false;
		// reasons list:
		browser.setText("<html><body><br />Error fetching webpage: " + userString + " <br /></body></html>");
		// 1. your connection to wan
		// 2. isp thru
		// 3. website down... get error code.
	}
	
	private void parseDevice() {
		// <div id="idname"> requires the quotes to be escaped below.
		debug("parse device called.");
		// better luck with separating these into single calls. must be better way...
		// browser.execute(String script) is to run javascript
		browser.execute("document.getElementById(\"templateByline\").innerHTML = '" + userByline + "';");
		browser.execute("document.getElementById(\"templateHeading\").innerHTML = '" + userHeading + "';");
		browser.execute("document.getElementById(\"templatePara1\").innerHTML = '" + userPara1 + "';");
		browser.execute("document.getElementById(\"templatePara2\").innerHTML = '" + userPara2 + "';");
		browser.execute("document.getElementById(\"templatePara3\").innerHTML = '" + userPara3 + "';");
		browser.execute("document.getElementById(\"templateCover\").style.width = '" + userCoverWidth + "';");
		browser.execute("document.getElementById(\"templateCover\").style.height = '" + userCoverHeight + "';");
	}
}
