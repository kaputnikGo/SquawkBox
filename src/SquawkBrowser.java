import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
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


public class SquawkBrowser {
	private static final int SHELL_HEIGHT = 950;
	private static final int SHELL_WIDTH = 860;
	private static final int SHELL_MIN_WIDTH = 320;
	private static final int SHELL_MIN_HEIGHT = 640;
	private boolean DEBUG = true;
	private SquawkView squawkView;
	private Browser browser;
	private Shell shell;
	
	private static String[] urls;
	
	private static final int TYPE_UNKNOWN = 0;
	private static final int TYPE_LOCAL = 1;
	private static final int TYPE_WWW_UP = 2;
	private static final int TYPE_WWW_DOWN = 3;
	
	protected String userTitle = "default title";
	protected String userPreheader = "default preheader";
	protected String userPara1 = "default user paragraph 1";
	protected String userSignoff = "default signoff";
	protected String userFooter = "default footer";
	protected String userBannerLogo = "";
	
	protected String exportedDeviceHtml;
	protected boolean shellOpen = false;
	protected boolean initPhase = false;
	
	SquawkBrowser(final SquawkView squawkView) {
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
				shellOpen = false;
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
			
			browser = new Browser(form, SWT.NONE);	
			initPhase = true;
		    // browser title grab
		    browser.addTitleListener(new TitleListener() {
		    	public void changed(TitleEvent event) {
		    		shell.setText("Squawk Box Browser : " + event.title);
		    	}
		    });
			
		    browser.addProgressListener(new ProgressListener() {
		    	public void changed(ProgressEvent event) {
		    		//
		    	}
		    	public void completed(ProgressEvent event) {
		    		//addUserContent();
		    		debug("browser completed reached.");
		    		getTemplateContent();
		    	}
		    });		   
		    
			shell.open();
			shellOpen = true;
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
		initPhase = false;
		debug("resource url: " + userString);
		browser.setUrl(userString);
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
		// parse here as well
		browser.refresh();
		addUserContent();
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
	
	public void dumpToBrowser(final String htmlDump) {
		debug("html dump to browser called.");
		browser.setText(htmlDump);
	}
	
	public void exportBrowser(final String savename) {
		// yar
		exportedDeviceHtml = browser.getText();
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
	protected void debug(final String message) {
		if (DEBUG) squawkView.updateConsole(message);
	}
	
	private String saveDialog(final String savename) {
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
	
	private boolean saveDeviceFile(final String fileName) {
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
	
	private void getTemplateContent() {
		//TODO
		// is stopping at newlines...!!
		
		// only do this if parsing a template file
		if (initPhase) 
			return;
		else {
			debug("getTemplateContent called.");
			boolean has = browser.execute("document.title");
			debug("has: " + has);
			
			userTitle = (String)browser.evaluate("return document.title");
			userBannerLogo = (String)browser.evaluate("return document.getElementById('bannerLogo').src;");
			userPreheader = (String)browser.evaluate("return document.getElementById('templatePreheader').innerHTML;");			
			// again needs to be an array
			userPara1 = (String)browser.evaluate("return document.getElementById('templatePara1').innerHTML;");
			userSignoff = (String)browser.evaluate("return document.getElementById('templateSignoff').innerHTML;");
			userFooter = (String)browser.evaluate("return document.getElementById('templateFooter').innerHTML;");
			
			// update the view
			squawkView.updateFormFields();
		}
	}
	
	private void addUserContent() {
		// browser.execute(String script) is to run javascript
		browser.execute("document.title = '" + userTitle + "';");
		browser.execute("document.getElementById(\"bannerLogo\").src = '" + userBannerLogo + "';");
		browser.execute("document.getElementById(\"templatePreheader\").innerHTML = '" + userPreheader + "';");
		
		// this needs to be an array or userPara[n]
		browser.execute("document.getElementById(\"templatePara1\").innerHTML = '" + userPara1 + "';");
		
		browser.execute("document.getElementById(\"templateSignoff\").innerHTML = '" + userSignoff + "';");
		browser.execute("document.getElementById(\"templateFooter\").innerHTML = '" + userFooter + "';");
		
		// eg. additions/rewrites to <head><style> - may not work...
		//browser.execute("document.write(\"<style>body { background-color:#000 }</style>\");");

	}
}
