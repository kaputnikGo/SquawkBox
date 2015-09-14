package UI;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import MAIN.SqlTalk;
import MAIN.SquawkBrowser;
import MAIN.Utilities;


// view and controller...

public class SquawkView {
	private static final int SHELL_WIDTH = 1100;
	private static final int SHELL_HEIGHT = 1000;
	private static final boolean DEBUG = true;
	private static Image icon;
	private SqlTalk squawk;
	private SquawkBrowser squawkBrowser;
	
	private static final String errorString = "n/a";
	
	//layout structure elements
	protected Display display;
	protected Shell shell;
	protected ViewInflate viewInflate;
	
	// var strings
	private String appName = "SQUAWK BOX.";
	
	protected String userWebcode = "";
	protected String userTemplateName = "";
	
	// changeable UI elements
	protected Text text1;
	protected Text text2;
	protected Text text3a;
	protected Text text3b;
	protected Text text3c;
	protected Text text4;
	protected Text text12;
	protected Text text13;
	protected Text text14;
	protected Text text15;

	protected Button[] webcodeButtons;
	protected Combo comboTemplates;
	protected Composite rowComp2;
	
	
	protected Button b5;
	protected Button b6;
	protected Button b7;
	protected Button b8;
	protected Button b9;
	protected Button b10;
	protected Button b11;
	protected Text debugText;

	
	public SquawkView() {
		// constructor
		// init the display window
		display = new Display();		
		shell = new Shell(display);
		shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
		shell.setMinimumSize(SHELL_WIDTH, SHELL_HEIGHT);
		shell.setText(appName);
		shell.setLayout(new FormLayout());

		icon = new Image(display, getClass().getClassLoader().getResourceAsStream("resources/plug_icon.png"));
	    shell.setImage(icon); 

	    viewInflate = new ViewInflate();
		viewInflate.initView(this);		
		squawk = new SqlTalk(this);
		squawkBrowser = new SquawkBrowser(this);
		
		updateConsole("Browser created.");
		initBrowser();
		
	    shell.addListener(SWT.Close, new Listener() {
	        public void handleEvent(Event event) {
	          int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
	          MessageBox messageBox = new MessageBox(shell, style);
	          messageBox.setText("Information");
	          messageBox.setMessage("Close the SquawkBox?");
	          event.doit = messageBox.open() == SWT.YES;
	        }
	    });
	}
	
/************************************************************
* 
* 		// public methods 
* 
************************************************************/		
	public void run() {				
		shell.open();
		updateConsole("Squawk says install...");
		squawk.installDB();		
		updateConsole("Squawk says finished.");
		
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// gc
		display.dispose();
	}
	
	public Display getDisplay() {
		return display;
	}
	
	public void browserClosed() {
		squawkBrowser = null;
	}
	
	
	
	public Shell getShell() {
		if (shell != null) 
			return shell;
		else 
			return null;		
	}
	
	public void updateFormFields() {
		// hopefully called by browser, after it has
		// loaded a template file	
		text1.setText(squawkBrowser.userTitle);
		text1.setEnabled(true);
		text2.setText(squawkBrowser.userPreheader);
		text2.setEnabled(true);
		text3a.setText(squawkBrowser.userPara1);
		text3a.setEnabled(true);
		text3b.setText(squawkBrowser.userSignoff);
		text3b.setEnabled(true);
		text3c.setText(squawkBrowser.userFooter);
		text3c.setEnabled(true);
		text4.setText(squawkBrowser.userBannerLogo);
		text4.setEnabled(true);
		text12.setText(squawkBrowser.userDate);
		text12.setEnabled(true);
		text13.setText(squawkBrowser.userHeading);
		text13.setEnabled(true);
		text14.setText(squawkBrowser.userAuthor);
		text14.setEnabled(true);
		text15.setText(squawkBrowser.userIntro);
		text15.setEnabled(true);
		
		updateConsole("update form fields called.");
	}
	
	public void disableFormFields() {
		// called due to template parse error
		// no editable span divs found
		text1.setText(errorString);
		text1.setEnabled(false);		
		text2.setText(errorString);
		text2.setEnabled(false);		
		text3a.setText(errorString);
		text3a.setEnabled(false);		
		text3b.setText(errorString);
		text3b.setEnabled(false);		
		text3c.setText(errorString);
		text3c.setEnabled(false);		
		text4.setText(errorString);
		text4.setEnabled(false);
		text12.setText(errorString);
		text12.setEnabled(false);
		text13.setText(errorString);
		text13.setEnabled(false);
		text14.setText(errorString);
		text14.setEnabled(false);
		text15.setText(errorString);
		text15.setEnabled(false);
		
		updateConsole("Not an editable template.");
	}
	
	public void updateConsole(final String message) {		
		if (DEBUG) {		
			if (Utilities.checkString(message)) {
				debugText.append(message + "\n");
			}
		}
	}
		
/************************************************************
* 
* 		// control methods 
* 
************************************************************/	
	private void initBrowser() {
		// first get the resource html file		
		if (squawkBrowser.initBrowser()) {
			URL initFile = getClass().getClassLoader().getResource("resources/index.html");
			if (initFile == null) {
				updateConsole("Init browser file not found. ");
				return;
			}
			squawkBrowser.openInitPage(initFile.toString());
			updateConsole("openResourcePage called.");
		}
	}
	
	private void openBrowser() {
		// mainly to allow user to re-open if closed
		if (squawkBrowser == null) {
			squawkBrowser = new SquawkBrowser(this);
			updateConsole("Browser created.");
			initBrowser();
		}
		else {
			updateConsole("browser not null.");
		}
	}
	
	void userButtonPressed(Event event) {
		// find out which button was pressed
		Button button = ((Button) event.widget);
		userWebcode = button.getText();
		comboTemplates.setItems(squawk.getTemplateList(userWebcode));
		updateConsole("Button press: " + userWebcode);
	}
	
/************************************************************
* 
* 		// interface methods 
* 
************************************************************/		
	void addButtonListeners() {
		//TODO	    
	    b5.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// re-process the current device with new vars
	    		squawkBrowser.processBrowser();
	    	}
	    });
	    b6.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// export the finished device as a text file
	    		// send default save name string to browser
	    		squawkBrowser.exportBrowser("test_save");
	    	}
	    });
	    b7.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// 
	    		openBrowser();
	    	}
	    });
		// open file button
	    b8.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.openLocalDirFile();
	    	}
	    });
		// dump to squawkBrowser button
	    b9.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.dumpToBrowser(Utilities.debugDumpTableDB(squawk.getDefaultDB(), squawk.getComponentTable()));
	    	}
	    });
		// add site specific wrapper to device in browser
	    b10.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.openEmailTemplate();
	    	}
	    });
		// toggle grid lines of blue
	    b11.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.toggleGrid();
	    	}
	    });
	}
	
	void addWebcodeButtons() {
		int numButtons = Utilities.WEBCODE_LIST.length;
		if (numButtons <= 0) numButtons = 1;		
		webcodeButtons = new Button[numButtons];		
		for (int i = 0; i < numButtons; i++) {
			webcodeButtons[i] = new Button(rowComp2, SWT.PUSH);
			webcodeButtons[i].setText(Utilities.WEBCODE_LIST[i]);
			webcodeButtons[i].addListener(SWT.Selection,  new Listener() {
				@Override
				public void handleEvent(Event event) {
					userButtonPressed(event);
				}
				
			});
		}		
	}
		
	void addTextListeners() {
		// these listen to the text boxes and update the browser vars
		text1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userTitle = text1.getText();
			}
		});
		text2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userPreheader = text2.getText();
			}
		});
		
		// need to break this down into the 3 paras on the Viewer.
		text3a.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userPara1 = text3a.getText();
			}
		});
		text3b.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userSignoff = text3b.getText();
			}
		});
		text3c.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userFooter = text3c.getText();
			}
		});
		// report url
		text4.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userBannerLogo = text4.getText();
			}
		});
		//
		text12.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userDate = text12.getText();
			}
		});
		//
		text13.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userHeading = text13.getText();
			}
		});
		//
		text14.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userAuthor = text14.getText();
			}
		});
		//
		text15.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userIntro = text15.getText();
			}
		});
	}
	
	void addComboElements() {	
		comboTemplates = new Combo(rowComp2, SWT.DROP_DOWN | SWT.BORDER);
	    SelectionListener selectionListener = new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		Combo combo = ((Combo) event.widget);
	    		
	    		// this will load the template for a given webcode
	    		userTemplateName = combo.getText();
	    		URL templateFile = getClass().getClassLoader().getResource("templates/EMAIL/" + userWebcode + "/" + userTemplateName + ".html");
	    		
	    		if (templateFile == null) {
	    			updateConsole("Template file not found for userWebcode " + userWebcode);
	    			//TODO
	    			// reset the form and the browser window
	    			disableFormFields();
	    			squawkBrowser.setBrowserText("Template file " + userTemplateName + " not found for userWebcode " + userWebcode);
	    			return;
	    		}
	    		else {
	    			squawkBrowser.openTemplatePage(templateFile.toString());
	    			updateFormFields();
	    		}
	    	}
	    };
	    comboTemplates.addSelectionListener(selectionListener);
	    comboTemplates.setItems(Utilities.TEMPLATE_LIST);
	    comboTemplates.select(0);
	}
}
